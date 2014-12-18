package com.mak001.ircBot.plugins.listeners;

public interface NoticeListener {

	/**
	 * This method is called whenever we receive a notice.
	 * <p>
	 * The implementation of this method in the PircBot abstract class performs
	 * no actions and may be overridden as required.
	 * 
	 * @param sourceNick
	 *            The nick of the user that sent the notice.
	 * @param sourceLogin
	 *            The login of the user that sent the notice.
	 * @param sourceHostname
	 *            The hostname of the user that sent the notice.
	 * @param target
	 *            The target of the notice, be it our nick or a channel name.
	 * @param notice
	 *            The notice message.
	 */
	public void onNotice(String sourceNick, String sourceLogin,
			String sourceHostname, String target, String notice);

}
