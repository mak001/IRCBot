package com.mak001.ircBot;

import java.util.ArrayList;

import org.jibble.pircbot.PircBot;

import com.mak001.api.plugins.Command;
import com.mak001.ircBot.plugins.Permissions;
import com.mak001.api.plugins.Plugin;
import com.mak001.ircBot.plugins.RegularCommands;
import com.mak001.ircBot.plugins.permissions.IRCPermissions;
import com.mak001.api.plugins.listeners.ActionListener;
import com.mak001.api.plugins.listeners.CTCPListener;
import com.mak001.api.plugins.listeners.JoinListener;
import com.mak001.api.plugins.listeners.MessageListener;
import com.mak001.api.plugins.listeners.ModeListener;
import com.mak001.api.plugins.listeners.NickChangeListener;
import com.mak001.api.plugins.listeners.NoticeListener;
import com.mak001.api.plugins.listeners.PartListener;
import com.mak001.api.plugins.listeners.PrivateMessageListener;
import com.mak001.api.plugins.listeners.QuitListener;
import com.mak001.ircBot.settings.Settings;
import com.mak001.ircBot.settings.SettingsWriter;

public class Bot extends PircBot {

	private ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	private ArrayList<Channel> channels = new ArrayList<Channel>();
	private ArrayList<Command> commands = new ArrayList<Command>();
	private boolean shouldDie = false;

	public Bot() {
		this.setName(Settings.get(Settings.NICK));
		this.setVersion("mak001's bot (Based off of Pirc)");

		plugins.add(new RegularCommands(this));
		plugins.add(new Permissions(this));


	}

	@Override
	public void onDisconnect() {
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
	public void onConnect() {
		ident();
		for (String chan : Settings.getChannels()) {
			addChannel(chan);
		}
	}

	public void addChannel(String chan) {
		channels.add(new Channel(chan));
		joinChannel(chan);
		setMode(chan, "");
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


	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replace(Settings.get(Settings.COMMAND_PREFIX), "");
			onCommand(channel, sender, login, hostname, s);
		} else {
			for (Plugin h : plugins) {
				if (h instanceof MessageListener) {
					((MessageListener) h).onMessage(channel, sender, login, hostname, message);
				}
			}
		}
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (isCommand(message)) {
			String s = message.replace(Settings.get(Settings.COMMAND_PREFIX), "");
			onCommand(sender, sender, login, hostname, s);
		} else {
			for (Plugin h : plugins) {
				if (h instanceof PrivateMessageListener) {
					((PrivateMessageListener) h).onPrivateMessage(sender, login, hostname, message);
				}
			}
		}
	}

	@Override
	public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		for (Plugin h : plugins) {
			if (h instanceof QuitListener) {
				((QuitListener) h).onQuit(sourceNick, sourceLogin, sourceHostname, reason);
			}
		}
	}

	@Override
	public void onPart(String channel, String sender, String login, String hostname) {
		for (Plugin h : plugins) {
			if (h instanceof PartListener) {
				((PartListener) h).onPart(channel, sender, login, hostname);
			}
		}
	}

	@Override
	public void onJoin(String channel, String sender, String login, String hostname) {
		for (Plugin h : plugins) {
			if (h instanceof JoinListener) {
				((JoinListener) h).onJoin(channel, sender, login, hostname);
			}
		}
	}

	@Override
	public void onNickChange(String oldNick, String login, String hostname, String newNick) {
		for (Plugin h : plugins) {
			if (h instanceof NickChangeListener) {
				((NickChangeListener) h).onNickChange(oldNick, login, hostname, newNick);
			}
		}
	}

	@Override
	public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		for (Plugin h : plugins) {
			if (h instanceof NoticeListener) {
				((NoticeListener) h).onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
			}
		}
	}

	@Override
	public void onAction(String sender, String login, String hostname, String target, String action) {
		for (Plugin h : plugins) {
			if (h instanceof ActionListener) {
				((ActionListener) h).onAction(sender, login, hostname, target, action);
			}
		}
	}

	@Override
	public void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
		super.onPing(sourceNick, sourceLogin, sourceHostname, target, pingValue);
		for (Plugin h : plugins) {
			if (h instanceof CTCPListener) {
				((CTCPListener) h).onPing(sourceNick, sourceLogin, sourceHostname, target, pingValue);
			}
		}
	}

	@Override
	public void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		super.onFinger(sourceNick, sourceLogin, sourceHostname, target);
		for (Plugin h : plugins) {
			if (h instanceof CTCPListener) {
				((CTCPListener) h).onFinger(sourceNick, sourceLogin, sourceHostname, target);
			}
		}
	}

	@Override
	public void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		super.onVersion(sourceNick, sourceLogin, sourceHostname, target);
		for (Plugin h : plugins) {
			if (h instanceof CTCPListener) {
				((CTCPListener) h).onVersion(sourceNick, sourceLogin, sourceHostname, target);
			}
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
		for (Plugin h : plugins) {
			if (h instanceof ModeListener) {
				((ModeListener) h).onChannelMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
			}
		}
	}

	@Override
	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
			String recipientNick, String reason) {
		if (this.getNick().equalsIgnoreCase(recipientNick)) {
			joinChannel(channel);
			setMode(channel, "");
		}
	}

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
			Channel chann = new Channel(parts[1]);
			channels.add(chann);
			if (modes.contains("+")) chann.addModes(modes);
		}
	}

	@Override
	public void onUserMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
		for (Plugin h : plugins) {
			if (h instanceof ModeListener) {
				((ModeListener) h).onUserMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
			}
		}
	}

	private boolean isCommand(String message) {
		return message.substring(0, 1).equals(Settings.get("COMMAND_PREFIX"));
	}

	private boolean onCommand(String channel, String sender, String login, String hostname, String message) {
		for (Command command : commands) {
			for (String c : command.getCommand()) {
				if (message.toLowerCase().startsWith(c.toLowerCase())) {
					System.out.println(c);
					String newMessage = "";
					if (c.length() + 1 <= message.length()) {
						newMessage = message.replaceFirst(message.substring(0, c.length() + 1), "");
					}
					command.onCommand(channel, sender, login, hostname, newMessage);
					return true;
				} else if (message.toUpperCase().startsWith("HELP " + c.toUpperCase())) {
					command.onHelp(channel, sender, login, hostname);
					return true;
				}
			}
		}
		return false;
	}

	public void createPermissionsUser(String user) {
		if (IRCPermissions.getUser(user) == null) {
			IRCPermissions.createUser(user);
			IRCPermissions.save();
		}
	}

	public void add(final Plugin p) {
		plugins.add(p);
	}

	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	public void shutDown(String sender) {
		shouldDie = true;
		this.quitServer("Requested by: " + sender);
		SettingsWriter.update();
		Boot.dispose();
		this.dispose();
	}

	public void unloadPlugin(Plugin p) {
		unloadPlugin(p, null);
	}

	public void unloadPlugin(Plugin p, String target) {
		String name = p.getManifest().name();

		if (!p.getManifest().name().equals("Default commands") && !p.getManifest().name().equals("Permissions")) {
			boolean b = plugins.remove(p);
			if (target != null) {
				if (b) {
					unloadCommands(p);
					sendMessage(target, "Successfully unloaded " + name);
				} else {
					sendMessage(target, "Failed to unloaded " + name);
				}
			}
		} else {
			if (target != null) {
				sendMessage(target, "Can not unloaded " + name);
			}
			System.out.println("Can not unload " + name);
		}
	}

	public boolean loadPlugin(String name, String target, int x) {
		boolean b = false;
		if (name.endsWith(".jar")) {
			b = Boot.loadPluginJar(name);
		} else if (name.endsWith(".class")) {
			b = Boot.loadPluginClass(name);
		}
		if (b) {
			sendMessage(target, "Successfully loaded " + name);
		} else {
			sendMessage(target, "Failed to load " + name);
		}
		return b;
	}

	public boolean loadPlugin(String name, String domain) {
		if (name.contains(".")) {
			return Boot.loadPluginJar(domain.replace("!/", "").replace(
					domain.substring(0, domain.lastIndexOf("\\") + 1), ""));
		} else {
			return Boot.loadPluginClass(name + ".class");
		}
	}

	public void reloadPlugin(Plugin p) {
		reloadPlugin(p, null);
	}

	public void reloadPlugin(Plugin p, String target) {
		if (!p.getManifest().name().equals("Default commands") && !p.getManifest().name().equals("Permissions")) {
			String name = p.getClass().getName();
			String domain = p.getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
			unloadPlugin(p);
			boolean b = loadPlugin(name, domain);
			if (target != null) {
				if (b) {
					sendMessage(target, "Successfully reloaded " + name);
				} else {
					this.sendMessage(target, "Failed to reload " + name);
				}
			}
		}
	}

	public Plugin getPluginByName(String string) {
		for (Plugin p : plugins) {
			if (p.getManifest().name().equalsIgnoreCase(string)
					|| p.getClass().getSimpleName().equalsIgnoreCase(string)) {
				return p;
			}
		}
		return null;
	}

	private void unloadCommands(Plugin plugin) {
		for (Command command : commands) {
			if (command.getParentPlugin().equals(plugin)) {
				unregisterCommand(command);
			}
		}
	}

	/**
	 * registers a command
	 * 
	 * @param command
	 *            - The command to register
	 * @return - If the command could be registered
	 * 
	 * @see ArrayList#add(Object)
	 */
	public boolean registerCommand(Command command) {
		return commands.add(command);
	}

	/**
	 * unregisters a command
	 * 
	 * @param command
	 *            - The command to unregister
	 * @return - If the command could be unregistered
	 * 
	 * @see ArrayList#remove(Object)
	 */
	public boolean unregisterCommand(Command command) {
		return commands.remove(command);
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}
}