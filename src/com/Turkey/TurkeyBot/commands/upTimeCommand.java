package com.Turkey.TurkeyBot.commands;

import java.util.Date;

import com.Turkey.TurkeyBot.TurkeyBot;

public class upTimeCommand extends Command
{
	public upTimeCommand(String n)
	{
		super(n, "");
	}

	public void oncommand(TurkeyBot bot, String sender, String message)
	{
		Date updated = bot.connectedChannel.updated_at;

		long diff = System.currentTimeMillis() - updated.getTime();

		int hours = (int) (diff / 3600000);
		int remainder = (int) (diff - hours * 3600000);
		int mins = (remainder / 60000);
		remainder = (remainder - mins * 60000);
		int secs = remainder / 1000;

		bot.sendMessage(bot.capitalizeName(bot.getChannel()) + " has been streaming for " + hours + " hours " + mins + " minutes and " + secs + " seconds!");
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
