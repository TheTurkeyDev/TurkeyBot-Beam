package com.Turkey.TurkeyBot.commands;

import com.Turkey.TurkeyBot.TurkeyBot;

public class NightBotCommand extends Command
{
	public NightBotCommand(String n)
	{
		super(n, "");
	}

	public void oncommand(TurkeyBot bot, String sender, String message)
	{
		bot.sendMessage("Nightbot? pfft wannabe");
	}
	
	public boolean canEdit()
	{
		return false;
	}
}