package com.mak001.ircbot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.jibble.pircbot.PircBot;

import com.mak001.ircbot.permissions.PermissionHandler;
import com.mak001.ircbot.plugin.PluginManager;
import com.mak001.ircbot.plugins.Permissions;
import com.mak001.ircbot.plugins.RegularCommands;

public class Bot extends PircBot {

	private ArrayList<Channel> channels = new ArrayList<Channel>();
	private boolean shouldDie = false;
	private final PluginManager manager;
	private final PermissionHandler permissionHandler;

	public Bot() {
		permissionHandler = new PermissionHandler();
		manager = new PluginManager(this);
		manager.addPlugin(new RegularCommands(this));
		manager.addPlugin(new Permissions(this));
		File folder = new File(SettingsManager.userHome + SettingsManager.fileSeparator + "Plugins");
		for (File file : folder.listFiles()) {
			try {
				String path = file.getCanonicalPath();
				if (path != null && path.endsWith(".jar")) {
					manager.loadPlugin(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setName(Boot.getSettingsManager().getNick());
		setVersion("mak001's bot (Based off of Pirc)");
	}

	@Override
	protected void onConnect() {
		ident();
		for (Entry<String, String> chan : Boot.getSettingsManager().getChannels().entrySet()) {
			if (chan.getValue() == null || chan.getValue().isEmpty()) this.joinChannel(chan.getKey());
			this.joinChannel(chan.getKey(), chan.getValue());
		}
	}

	@Override
	protected void onDisconnect() {
		channels.clear(); // clears the channels
		try {
			while (!isConnected() && !shouldDie) {
				try {
					reconnect();
				} catch (Exception e) {
					System.out.println("Failed to reconnect. Trying agian in 10 seconds.");
					Thread.sleep(10000);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replace(Boot.getSettingsManager().getCommandPrefix(), "");
			manager.onCommand(channel, sender, login, hostname, s);
		} else {
			manager.triggerMessageListeners(channel, sender, login, hostname, message);
		}
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replace(Boot.getSettingsManager().getCommandPrefix(), "");
			manager.onCommand(sender, sender, login, hostname, s);
		} else {
			manager.triggerPrivateMessageListeners(sender, login, hostname, message);
		}
	}

	@Override
	public void onAction(String sender, String login, String hostname, String target, String action) {
		manager.triggerActionListeners(sender, login, hostname, target, action);
	}

	@Override
	public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		manager.triggerNoticeListeners(sourceNick, sourceLogin, sourceHostname, target, notice);
	}

	@Override
	public void onJoin(String channel, String sender, String login, String hostname) {
		manager.triggerJoinListeners(channel, sender, login, hostname);
	}

	@Override
	public void onPart(String channel, String sender, String login, String hostname) {
		manager.triggerPartListeners(channel, sender, login, hostname);
	}

	@Override
	public void onNickChange(String oldNick, String login, String hostname, String newNick) {
		manager.triggerNickChangeListeners(oldNick, login, hostname, newNick);
	}

	@Override
	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
			String recipientNick, String reason) {
		// TODO - add in ban-handling
		if (this.getNick().equalsIgnoreCase(recipientNick)) {
			channels.remove(getChannelByName(channel));
			joinChannel(channel);
		}
	}

	@Override
	public void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		Channel chan = getChannelByName(channel);
		if (chan != null) {
			if (mode.contains("-")) {
				chan.removeModes(mode);
			} else if (mode.contains("+")) {
				chan.addModes(mode);
			}
		}
		manager.triggerChannelModeListeners(channel, sourceNick, sourceLogin, sourceHostname, mode);
	}

	@Override
	public void onUserMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		manager.triggerUserModeListeners(channel, sourceNick, sourceLogin, sourceHostname, mode);
	}

	@Override
	public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		manager.triggerQuitListeners(sourceNick, sourceLogin, sourceHostname, reason);
	}

	@Override
	protected void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		super.onVersion(sourceNick, sourceLogin, sourceHostname, target);
		manager.triggerVersionListeners(sourceNick, sourceLogin, sourceHostname, target);
	}

	@Override
	protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
		super.onPing(sourceNick, sourceLogin, sourceHostname, target, pingValue);
		manager.triggerPingListeners(sourceNick, sourceLogin, sourceHostname, target, pingValue);
	}

	@Override
	protected void onServerPing(String response) { // TODO - ???
		this.sendRawLine("PONG " + response);
	}

	@Override
	protected void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		super.onTime(sourceNick, sourceLogin, sourceHostname, target);
	}

	@Override
	protected void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		super.onFinger(sourceNick, sourceLogin, sourceHostname, target);
		manager.triggerFingerListeners(sourceNick, sourceLogin, sourceHostname, target);
	}

	@Override
	protected void onServerResponse(int code, String response) {
		if (code == RPL_CHANNELMODEIS) {
			String[] parts = response.split(" ");
			Channel chan = getChannelByName(parts[1]);
			String modes = parts[2];
			if (channels.contains(chan)) {
				if (modes.startsWith("+")) {
					chan.addModes(modes);
				} else {
					chan.removeModes(modes);
				}
			}
			Channel chann = new Channel(parts[1], null);
			channels.add(chann);
			if (modes.contains("+")) chann.addModes(modes);
		}
	}

	private boolean isCommand(String message) {
		return message.substring(0, 1).equals(Boot.getSettingsManager().getCommandPrefix());
	}


	public void shutDown(String sender) {
		shouldDie = true;
		this.quitServer("Requested by: " + sender);
		try {
			Boot.getSettingsManager().save();
			permissionHandler.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boot.dispose();
		this.dispose();
	}

	public PluginManager getPluginManager() {
		return manager;
	}

	private void ident() {
		String pass = Boot.getSettingsManager().getNickPass();
		String nick = Boot.getSettingsManager().getNick();
		if (!getNick().equals(nick) && !getNick().equals("PircBot")) {
			sendMessage("Nickserv", "ghost " + nick + " " + pass);
			changeNick(nick);
		}
		if (pass != null && !pass.equals("")) {
			sendMessage("Nickserv", "identify " + pass);
		}
	}


	public void addChannel(String chan) {
		Boot.getSettingsManager().addChannel(chan);
	}

	public void removeChannel(String chan) {
		removeChannel(chan, "");
	}

	public void removeChannel(String chan, String reason) {
		Boot.getSettingsManager().removeChannel(chan);
		partChannel(chan, reason);
		channels.remove(chan);
	}

	public Channel getChannelByName(String chan) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(chan)) {
				return channel;
			}
		}
		return null;
	}

	public PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}
}