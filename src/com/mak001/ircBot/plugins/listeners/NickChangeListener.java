package com.mak001.ircBot.plugins.listeners;

public interface NickChangeListener {
	/**
	 * This method is called whenever someone (possibly us) changes nick on any
	 * of the channels that we are on.
	 * <p>
	 * The implementation of this method in the PircBot abstract class performs
	 * no actions and may be overridden as required.
	 * 
	 * @param oldNick
	 *            The old nick.
	 * @param login
	 *            The login of the user.
	 * @param hostname
	 *            The hostname of the user.
	 * @param newNick
	 *            The new nick.
	 */
	public void onNickChange(String oldNick, String login, String hostname,
			String newNick);

}
