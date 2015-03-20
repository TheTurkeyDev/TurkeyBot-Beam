package com.Turkey.TurkeyBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pro.beam.api.BeamAPI;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.chat.BeamChat;
import pro.beam.api.resource.chat.BeamChatConnectable;
import pro.beam.api.resource.chat.events.EventHandler;
import pro.beam.api.resource.chat.events.IncomingMessageEvent;
import pro.beam.api.resource.chat.events.data.IncomingMessageData.MessagePart;
import pro.beam.api.resource.chat.events.data.IncomingMessageData.MessagePart.Type;
import pro.beam.api.resource.chat.methods.AuthenticateMessage;
import pro.beam.api.resource.chat.methods.ChatSendMethod;
import pro.beam.api.resource.chat.replies.AuthenticationReply;
import pro.beam.api.resource.chat.replies.ReplyHandler;
import pro.beam.api.response.users.UserSearchResponse;
import pro.beam.api.services.impl.ChatService;
import pro.beam.api.services.impl.UsersService;

import com.Turkey.TurkeyBot.chat.AutoAnnouncement;
import com.Turkey.TurkeyBot.chat.ModerateChat;
import com.Turkey.TurkeyBot.commands.AddCommand;
import com.Turkey.TurkeyBot.commands.AddResponse;
import com.Turkey.TurkeyBot.commands.AutoTurtleCommand;
import com.Turkey.TurkeyBot.commands.BypassCommand;
import com.Turkey.TurkeyBot.commands.Command;
import com.Turkey.TurkeyBot.commands.CurrencyCommand;
import com.Turkey.TurkeyBot.commands.DeleteCommand;
import com.Turkey.TurkeyBot.commands.EditCommand;
import com.Turkey.TurkeyBot.commands.EditPermission;
import com.Turkey.TurkeyBot.commands.FunWayBotCommand;
import com.Turkey.TurkeyBot.commands.MooBotCommand;
import com.Turkey.TurkeyBot.commands.NightBotCommand;
import com.Turkey.TurkeyBot.commands.SlotsCommand;
import com.Turkey.TurkeyBot.commands.StatusCommand;
import com.Turkey.TurkeyBot.commands.WinnerCommand;
import com.Turkey.TurkeyBot.commands.upTimeCommand;
import com.Turkey.TurkeyBot.files.AccountSettings;
import com.Turkey.TurkeyBot.files.AnnouncementFile;
import com.Turkey.TurkeyBot.files.ChatSettings;
import com.Turkey.TurkeyBot.files.CurrencyFile;
import com.Turkey.TurkeyBot.files.ResponseSettings;
import com.Turkey.TurkeyBot.files.SettingsFile;
import com.Turkey.TurkeyBot.gui.ConsoleTab;
import com.Turkey.TurkeyBot.gui.ConsoleTab.Level;
import com.Turkey.TurkeyBot.gui.KeyWordRaffleTab;
import com.Turkey.TurkeyBot.gui.QuestionRaffleTab;
import com.Turkey.TurkeyBot.util.CurrencyThread;
import com.Turkey.TurkeyBot.util.HTTPConnect;
import com.Turkey.TurkeyBot.util.KeyWordRaffle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TurkeyBot
{
	public static final String VERSION = "Beta 1.0.0";

	private static HashMap<String, Command> commands = new HashMap<String, Command>();

	private static String botName = "";
	private String stream = "";
	private String currencyName;

	private String lastCommand = "";
	private long lastCommandTime = 0;

	public CurrencyFile currency;
	public SettingsFile settings;
	public ChatSettings chatSettings;
	public ResponseSettings spamResponseFile;
	public AccountSettings accountSettingsFile;
	public CurrencyThread currencyTrack;
	public AnnouncementFile announceFile;

	private ModerateChat chatmoderation;
	private AutoAnnouncement announcer;

	private ArrayList<String> viewers;

	private List<String> bypass = new ArrayList<String>();

	public static JsonParser json;
	public static BeamAPI beam;
	private BeamUser user;
	public BeamUser connectedChannel;
	private BeamChat chat;
	private BeamChatConnectable connectable;


	private boolean connected = false;

	/**
	 * Initializes the Chat side of the bot
	 * @throws Exception
	 */
	public TurkeyBot() throws Exception
	{
		beam = new BeamAPI();
		json = new JsonParser();
		loadFiles();
		currencyName = settings.getSetting("CurrencyName");
		loadCommands();
		chatmoderation = new ModerateChat(this);
	}

	/**
	 * Loads the files needed for the bot
	 */
	private void loadFiles()
	{
		try
		{
			currency = new CurrencyFile(this);
			settings = new SettingsFile(this);
			chatSettings = new ChatSettings(this);
			spamResponseFile = new ResponseSettings(this);
			accountSettingsFile = new AccountSettings(this);
			announceFile = new AnnouncementFile();
		} catch (IOException e){e.printStackTrace();}
	}

	/**
	 * Loads the commands for TurkeyBot
	 */
	private void loadCommands()
	{
		commands.put("!slots".toLowerCase(), new SlotsCommand("Slots"));
		commands.put(("!"+currencyName.replaceAll(" ", "")).toLowerCase(), new CurrencyCommand("Currency"));
		commands.put("!upTime".toLowerCase(), new upTimeCommand("Uptime"));
		//commands.put("!Math".toLowerCase(), new MathCommand("Math"));
		commands.put("!Winner".toLowerCase(), new WinnerCommand("Winner"));
		commands.put("!bypass".toLowerCase(), new BypassCommand("Bypass"));
		commands.put("!addCommand".toLowerCase(), new AddCommand("AddCommand"));
		commands.put("!editCommand".toLowerCase(), new EditCommand("EditCommand"));
		commands.put("!addResponse".toLowerCase(), new AddResponse("AddResponse"));
		commands.put("!editPermission".toLowerCase(), new EditPermission("EditPermission"));
		commands.put("!deleteCommand".toLowerCase(), new DeleteCommand("DeleteCommand"));
		commands.put("!commandstatus".toLowerCase(), new StatusCommand("commandStatus"));
		commands.put("!nightbot".toLowerCase(), new NightBotCommand("NightBot"));
		commands.put("!moobot".toLowerCase(), new MooBotCommand("MooBot"));
		commands.put("!funwaybot".toLowerCase(), new FunWayBotCommand("Funwaybot"));
		commands.put("!autoTurtle".toLowerCase(), new AutoTurtleCommand("autoTurtle"));

		File filesfolder = new File("C:" + File.separator + "TurkeyBot" + File.separator + "commands");
		for(String s: filesfolder.list())
		{
			try{
				File f = new File(filesfolder.getAbsolutePath() + File.separator + s);
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String result = "";
				String line = "";
				while((line = reader.readLine()) != null)
				{
					result += line;
				}
				reader.close();

				String name = f.getName().substring(0, f.getName().indexOf("."));
				JsonObject obj = json.parse(result).getAsJsonObject();

				if(obj.get("LoadFile").getAsBoolean())
				{
					Command c = new Command(name, obj.get("Responses").getAsJsonObject().get("0").getAsString());
					JsonObject responses = obj.get("Responses").getAsJsonObject();
					for(int i = 1; i < obj.get("Number_Of_Responses").getAsInt(); i++)
					{
						c.addResponse(responses.get("" + i).getAsString());
					}
					this.addCommand(c);
				}
				else
				{
					Command c = getCommandFromName("!" + name);
					if(c== null)
						c = commands.get(("!"+currencyName.replaceAll(" ", "")).toLowerCase());
					c.getFile().updateCommand();
				}

			}catch(IOException e){}
		}
	}

	/**
	 * Called when a message is sent in a chat that the bot is in.
	 */
	public void onMessage(String sender, String message, String perm) 
	{
		ConsoleTab.output(Level.Chat, "[" + sender + "] " + message);
		if(!chatmoderation.isValidChat(message, sender, perm))
			return;
		int index = message.indexOf(" ");
		if(index < 1)
			index = message.length();
		if(commands.containsKey(message.substring(0, index).toLowerCase()))
		{
			Command command = commands.get(message.substring(0, index).toLowerCase());
			if(command.isEnabled() && command.getPermissionLevel().equalsIgnoreCase(perm) && (!lastCommand.equalsIgnoreCase(command.getName()) || (lastCommandTime == 0 || System.currentTimeMillis() - lastCommandTime > 3000)))
			{
				lastCommand = command.getName();
				lastCommandTime = System.currentTimeMillis();
				command.oncommand(this, sender, message);
			}
		}
		else if(message.equalsIgnoreCase("!Disconnect") && (sender.equalsIgnoreCase(stream.substring(1)) || sender.equalsIgnoreCase("turkey2349")))
		{
			disconnectFromChannel();
		}
		else if(message.equalsIgnoreCase("!reconnect") && (sender.equalsIgnoreCase(stream.substring(1)) || sender.equalsIgnoreCase("turkey2349")))
		{
			String lastchannel = stream.substring(1);
			disconnectFromChannel();
			try
			{
				connectToChannel(lastchannel);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			} catch (ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		/*else if(message.equalsIgnoreCase("!Commands") || message.equalsIgnoreCase("!Help"))
		{
			String toSend = "These are the available commands to use ";
			for(String command: commands.keySet())
			{
				if(commands.get(command).canUseByDefault())
				{
					toSend += command + " ";
				}
			}
			sendMessage(toSend);
		}
		else if((message.substring(0, index).equalsIgnoreCase("!Add"+currencyName) && sender.equalsIgnoreCase(stream.substring(1)))&& this.hasPermission(sender, "Streamer"))
		{
			String[] args = message.split(" ");
			if(args.length != 2)
			{
				sendMessage("Invalid arguments");
			}
			else
			{
				int ammount;
				try{
					ammount = Integer.parseInt(args[1]);
				}catch(NumberFormatException e){sendMessage("Invalid Integer"); return;}
				for(User user: getUsers(channel))
				{
					currency.addCurrencyFor(user.getNick(), ammount);
				}
				sendMessage("Gave " + ammount + " " + currencyName + " to everyone!");
			}
		}*/

		if(KeyWordRaffleTab.getCurrentRaffle() != null && KeyWordRaffleTab.getCurrentRaffle().isRunning())
		{
			KeyWordRaffle raffle = KeyWordRaffleTab.getCurrentRaffle();
			if(raffle.getKeyWord().equalsIgnoreCase(message))
				raffle.addEntry(sender.toLowerCase());
		}

		if(QuestionRaffleTab.isRunning())
		{
			if(QuestionRaffleTab.getAnswer().equalsIgnoreCase(message))
			{
				this.sendMessage(this.capitalizeName(sender) + " Has guessed the correct answer! The answer was: " + QuestionRaffleTab.getAnswer());
				QuestionRaffleTab.end();
			}
		}
	}

	/**
	 * Sends a message from the bot to the chat it is currently in.
	 * Also auto outputs the message in the console.
	 * @param msg The message to be sent to the chat.
	 */
	public void sendMessage(String msg)
	{
		ConsoleTab.output(Level.Chat, "["+ botName + "] " + msg);
		if(stream != "" || !this.settings.getSettingAsBoolean("isSilent"))
			connectable.send(ChatSendMethod.of(msg));
	}


	/**
	 * Connects the bot to the specified channel.
	 * Auto handles if the Bot is already in a channel or is not connected to the twitch server.
	 * @param channel The channel for the bot to connect to.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void connectToChannel(String channel) throws InterruptedException, ExecutionException
	{
		ConsoleTab.clearConsole();
		try{
			user = beam.use(UsersService.class).login(this.accountSettingsFile.getSetting("Username"), SecretStuff.password).get();
		}catch(ExecutionException e){ConsoleTab.output(Level.Alert, "Failed To login to beam! check your login credentials!");return;}
		UserSearchResponse search = beam.use(UsersService.class).search(channel).get();
		if(search.size() > 0)
		{
			connectedChannel = beam.use(UsersService.class).findOne(search.get(0).id).get();
		}
		else
		{
			System.out.println("Size 0");
			return;
		}
		chat = beam.use(ChatService.class).findOne(connectedChannel.channel.id).get();
		connectable = chat.makeConnectable(beam);

		boolean connected = connectable.connectBlocking();

		if (connected)
		{
			connectable.send(AuthenticateMessage.from(connectedChannel.channel, user, chat.authkey), new ReplyHandler<AuthenticationReply>() 
					{
				@Override
				public void onSuccess(AuthenticationReply reply) 
				{
					ConsoleTab.output(Level.Info, "Authenticated on Beam!");
				}
					});
		}
		connectable.on(IncomingMessageEvent.class, new EventHandler<IncomingMessageEvent>() 
				{
			@Override
			public void onEvent(IncomingMessageEvent event) 
			{
				String msg = "";
				for(MessagePart msgp: event.data.message)
				{
					if(msgp.type.equals(Type.LINK))
					{
						sendMessage(spamResponseFile.getSetting("LinkMessage"));
						return;
					}
					msg+=msgp.data;
				}
				onMessage(event.data.user_name, msg, event.data.user_role);
			}
				});
		stream = channel;
		if(!settings.getSetting("AnnounceDelay").equals("-1"))
		{
			announcer = new AutoAnnouncement(this);
		}

		ConsoleTab.output(Level.Info, "Connected to " + stream + "'s channel!");
		if(!settings.getSettingAsBoolean("SilentJoinLeave"))
			this.sendMessage("Hello I am TurkeyBot");
	}

	/**
	 * Disconnects the bot from the current channel it is in.
	 */
	public void disconnectFromChannel()
	{
		connectable.close();
		if(!settings.getSettingAsBoolean("SilentJoinLeave"))
			this.sendMessage("GoodBye!");
		else
			ConsoleTab.output(Level.Alert, "Disconnected to the channel silently!");
		ConsoleTab.output(Level.Alert, "Disconnected from " + stream.substring(1) + "'schannel!");
		if(currencyTrack != null)
			currencyTrack.stopThread();
		announcer.stop();
		stream = "";
	}

	/**
	 * Capitalizes the first letter of the given name.
	 * Used for an aesthetic look.
	 * @param name The String to be capitalized.
	 * @return Capitalized name.
	 */
	public String capitalizeName(String name)
	{
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}

	/**
	 * Gets the current channel the bot is in.
	 * @return The name on the current channel the bot is in.
	 */
	public String getChannel()
	{
		return stream;
	}

	/**
	 * Gets the list of users currently in the chat.
	 * @return List of current Viewers.
	 */
	public ArrayList<String> getViewers()
	{
		return viewers;
	}

	/**
	 * Loads all of the viewers for the the current channel that the bot is in.
	 */
	public void loadViewers()
	{
		JsonObject obj = json.parse(HTTPConnect.GetResponsefrom("https://tmi.twitch.tv/group/user/" + this.getChannel() + "/chatters")).getAsJsonObject();
		viewers = new ArrayList<String>();
		obj = obj.get("chatters").getAsJsonObject();
		JsonArray mods = obj.get("moderators").getAsJsonArray();
		JsonArray staff = obj.get("staff").getAsJsonArray();
		JsonArray admins = obj.get("admins").getAsJsonArray();
		JsonArray globalMod = obj.get("global_mods").getAsJsonArray();
		JsonArray watchers = obj.get("viewers").getAsJsonArray();
		for(int i = 0; i < mods.size(); i++)
			viewers.add(mods.get(i).getAsString());
		for(int i = 0; i < staff.size(); i++)
			viewers.add(staff.get(i).getAsString());
		for(int i = 0; i < admins.size(); i++)
			viewers.add(admins.get(i).getAsString());
		for(int i = 0; i < globalMod.size(); i++)
			viewers.add(globalMod.get(i).getAsString());
		for(int i = 0; i < watchers.size(); i++)
			viewers.add(watchers.get(i).getAsString());
	}

	/**
	 * Adds the specified user name to a list for people who will bypass the chat modertion check.
	 * @param name The username to add to the list.
	 */
	public void giveImmunityTo(String name)
	{
		bypass.add(name);
	}

	/**
	 * Checks to see if the given user name is able to bypass the chat filter.
	 * Auto removes the username from the list if the name is on the list.
	 * @param name The username to check to see if they bypass the filter.
	 * @return If the given username can bypass the chat filter.
	 */
	public boolean checkForImmunity(String name)
	{
		if(bypass.contains(name))	
			bypass.remove(name);
		else
			return false;
		return true;
	}

	/**
	 * Gets the current list of all of the commands.
	 * @return
	 */
	public Object[] getCommands()
	{
		return commands.keySet().toArray();
	}

	/**
	 * Returns whether or not the bot has connected to Twitch servers.
	 * @return If the the bot has connected to Twitch servers.
	 */
	public boolean didConnect()
	{
		return connected;
	}

	/**
	 * Gets the the command class for the given command name.
	 * @param name The name of a command to be returned.
	 * @return The command for the given name. Null if not found.
	 */
	public static Command getCommandFromName(String name)
	{
		return commands.get(name.toLowerCase());
	}

	/**
	 * Adds a command to the command list and auto generates its properties file.
	 * @param command The command to be added to the bot.
	 */
	public void addCommand(Command command)
	{
		commands.put("!" + command.getName().toLowerCase(), command);
		command.getFile().updateCommand();
	}

	/**
	 * Removes a command and its file from the bot.
	 * @param command The command to be removed from the bot.
	 */
	public void removeCommand(Command command)
	{
		commands.remove(("!" + command.getName()).toLowerCase());
		command.getFile().removeCommand();
		if(settings.getSettingAsBoolean("outputchanges"))
		{
			sendMessage("Removed command " + command.getName());
		}
	}

	/**
	 * Returns the name of the currency that is currently entered for the bot.
	 * @return The name of the currency.
	 */
	public String getCurrencyName()
	{
		return currencyName;
	}

	/**
	 * Gets the list of permissions currently in TurkeyBot.
	 * @return List of current permissions.
	 */
	public static String[] getPermissions()
	{
		return new String[]{"User", "Mod", "Streamer"};
	}
}