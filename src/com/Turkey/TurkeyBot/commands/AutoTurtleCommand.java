package com.Turkey.TurkeyBot.commands;

import com.Turkey.TurkeyBot.TurkeyBot;

public class AutoTurtleCommand extends Command
{
	public AutoTurtleCommand(String n)
	{
		super(n, "");
	}

	public void oncommand(TurkeyBot bot, String sender, String message)
	{
		bot.sendMessage("First MooBot, Then TurkeyBot, now autoTurtle? Man its becoming a zoo in here.");
	}
	
	@Override
	public boolean canEdit()
	{
		return false;
	}
}