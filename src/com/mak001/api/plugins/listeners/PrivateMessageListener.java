package com.mak001.api.plugins.listeners;

public interface PrivateMessageListener {

	/**
	 * This method is called whenever a private message is sent to the PircBot.
	 * Do NOT use for command listening.
	 * <p>
	 * The implementation of this method in the PircBot abstract class performs
	 * no actions and may be overridden as required.
	 * 
	 * @param sender
	 *            The nick of the person who sent the private message.
	 * @param login
	 *            The login of the person who sent the private message.
	 * @param hostname
	 *            The hostname of the person who sent the private message.
	 * @param message
	 *            The actual message.
	 */
	public void onPrivateMessage(String sender, String login, String hostname,
			String message);

}
