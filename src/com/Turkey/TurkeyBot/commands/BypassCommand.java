package com.Turkey.TurkeyBot.commands;

import com.Turkey.TurkeyBot.TurkeyBot;

public class BypassCommand extends Command
{

	public BypassCommand(String n)
	{
		super(n, "");
	}

	public void oncommand(TurkeyBot bot, String sender, String message)
	{
		String[] args = message.split(" ");
		if(args.length == 2)
		{
			bot.giveImmunityTo(args[1].toLowerCase());
			bot.sendMessage("The user " + args[1] + " now bypasses the filter next message.");
		}
		else
		{
			bot.sendMessage("Invalid Arguments! Try !Bypass <UserName>");
		}
	}

	public boolean canEdit()
	{
		return false;
	}

	public String getPermissionLevel()
	{
		return "Mod";
	}
}
