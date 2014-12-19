package com.mak001.api.plugins.listeners;

public interface MessageListener {

	/**
	 * This method is called whenever a message is sent to a channel. Do NOT use
	 * for command listening.
	 * <p>
	 * The implementation of this method in the PircBot abstract class performs
	 * no actions and may be overridden as required.
	 * 
	 * @param channel
	 *            The channel to which the message was sent.
	 * @param sender
	 *            The nick of the person who sent the message.
	 * @param login
	 *            The login of the person who sent the message.
	 * @param hostname
	 *            The hostname of the person who sent the message.
	 * @param message
	 *            The actual message sent to the channel.
	 */
	public void onMessage(String channel, String sender, String login,
			String hostname, String message);

}
