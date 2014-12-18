package com.mak001.ircBot.plugins.listeners;

public interface PartListener {

	/**
	 * This method is called whenever someone (possibly us) parts a channel
	 * which we are on.
	 * <p>
	 * The implementation of this method in the PircBot abstract class performs
	 * no actions and may be overridden as required.
	 * 
	 * @param channel
	 *            The channel which somebody parted from.
	 * @param sender
	 *            The nick of the user who parted from the channel.
	 * @param login
	 *            The login of the user who parted from the channel.
	 * @param hostname
	 *            The hostname of the user who parted from the channel.
	 */
	public void onPart(String channel, String sender, String login,
			String hostname);

}
