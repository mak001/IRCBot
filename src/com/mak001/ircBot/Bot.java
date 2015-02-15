package com.mak001.ircBot;

import java.io.File;
import java.util.ArrayList;

import org.jibble.pircbot.PircBot;

import com.mak001.ircBot.plugin.PluginManager;
import com.mak001.ircBot.plugins.Permissions;
import com.mak001.ircBot.plugins.RegularCommands;
import com.mak001.ircBot.plugins.permissions.IRCPermissions;
import com.mak001.ircBot.settings.Settings;
import com.mak001.ircBot.settings.SettingsWriter;

public class Bot extends PircBot {

	private ArrayList<Channel> channels = new ArrayList<Channel>();
	private boolean shouldDie = false;
	private final PluginManager manager;

	public Bot() {
		manager = new PluginManager(this);
		manager.addPlugin(new RegularCommands(this));
		manager.addPlugin(new Permissions(this));
		File folder = new File(Settings.userHome + Settings.fileSeparator + "Plugins" + Settings.fileSeparator + "bin");
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
		setName(Settings.get(Settings.NICK));
		setVersion("mak001's bot (Based off of Pirc)");
	}

	@Override
	protected void onConnect() {
		ident();
		for (String chan : Settings.getChannels()) {
			this.joinChannel(chan);
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
			String s = message.replace(Settings.get(Settings.COMMAND_PREFIX), "");
			manager.onCommand(channel, sender, login, hostname, s);
		} else {
			manager.triggerMessageListeners(channel, sender, login, hostname, message);
		}
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replace(Settings.get(Settings.COMMAND_PREFIX), "");
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
		return message.substring(0, 1).equals(Settings.get("COMMAND_PREFIX"));
	}


	public void shutDown(String sender) {
		shouldDie = true;
		this.quitServer("Requested by: " + sender);
		SettingsWriter.update();
		Boot.dispose();
		this.dispose();
	}

	public PluginManager getPluginManager() {
		return manager;
	}

	private void ident() {
		String pass = Settings.get(Settings.NICK_PASS);
		String nick = Settings.get(Settings.NICK);
		if (!getNick().equals(nick) && !getNick().equals("PircBot")) {
			sendMessage("Nickserv", "ghost " + nick + " " + pass);
			changeNick(nick);
		}
		if (pass != null && !pass.equals("")) {
			sendMessage("Nickserv", "identify " + pass);
		}
	}


	public void addChannel(String chan) {

		if (!Settings.getChannels().contains(chan)) {
			Settings.getChannels().add(chan);
			SettingsWriter.update();
		}
	}

	public void removeChannel(String chan) {
		removeChannel(chan, "");
	}

	public void removeChannel(String chan, String reason) {
		Settings.getChannels().remove(getChannelByName(chan));
		SettingsWriter.update();
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


	public void createPermissionsUser(String user) {
		if (IRCPermissions.getUser(user) == null) {
			IRCPermissions.createUser(user);
			IRCPermissions.save();
		}
	}
}