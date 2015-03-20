package com.Turkey.TurkeyBot.util;

import java.util.ArrayList;
import java.util.List;

import com.Turkey.TurkeyBot.gui.Gui;

public class KeyWordRaffle
{
	private boolean isRunning = false;
	private boolean followersOnly = true;
	private String keyWord;
	private List<String> entries;

	public KeyWordRaffle(String key)
	{
		keyWord = key;
		entries = new ArrayList<String>();
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public String getKeyWord()
	{
		return keyWord;
	}

	public void addEntry(String name)
	{
		if(isFollowersOnly())
				entries.add(name);
		else if(!isFollowersOnly())
			entries.add(name);
		Gui.reloadTab();
	}

	public String getRandomEntry()
	{
		return entries.get((int)(Math.random() * entries.size()));
	}

	public List<String> getEntries()
	{
		return entries;
	}

	public boolean isFollowersOnly()
	{
		return followersOnly;
	}

	public void setFollowersOnly(boolean followersOnly)
	{
		this.followersOnly = followersOnly;
	}
}