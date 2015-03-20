package com.Turkey.TurkeyBot.commands;


import com.Turkey.TurkeyBot.TurkeyBot;

public class CurrencyCommand extends Command
{
	public CurrencyCommand(String n)
	{
		super(n, "");
	}

	public void oncommand(TurkeyBot bot, String sender, String message)
	{
		int currency = bot.currency.getCurrencyFor(sender);
		bot.sendMessage("" + bot.capitalizeName(sender) + " You have " + currency + " " + bot.getCurrencyName());
	}
	
	public boolean canEdit()
	{
		return false;
	}
}
