package com.mak001.api.plugins;

import org.jibble.pircbot.PircBot;

/**
 * All plugins must extend this class and have a {@link Manifest} A plugin will
 * NOT load without a {@link Manifest}, however, the {@link Manifest} can have
 * default values.
 * 
 * @author mak001
 */
public abstract class Plugin {

	/**
	 * Used for setting up the help and about commands
	 */
	public final String GENERAL_COMMAND;
	protected final PircBot bot;

	/**
	 * @param bot
	 *            - Just make your constructor Plugin(MyBot myBot)
	 * @param GENERAL_COMMAND
	 *            - The command to respond to
	 */
	public Plugin(PircBot bot, String GENERAL_COMMAND) {
		this.GENERAL_COMMAND = GENERAL_COMMAND;
		this.bot = bot;
	}

	/**
	 * @return The manifest of the plugin
	 */
	public Manifest getManifest() {
		return getClass().getAnnotation(Manifest.class);
	}

}
