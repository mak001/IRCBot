package com.mak001.api.plugins.listeners;

/**
 * A listener for mode changes
 *
 * @author MAK001
 */
public interface ModeListener {

    void onChannelMode(String channel, String sourceNick, String sourceLogin,
                       String sourceHostname, String mode);

    void onUserMode(String channel, String sourceNick, String sourceLogin,
                    String sourceHostname, String mode);

}
