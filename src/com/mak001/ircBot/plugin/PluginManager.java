package com.mak001.ircBot.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mak001.api.plugins.Command;
import com.mak001.api.plugins.Plugin;
import com.mak001.api.plugins.listeners.ActionListener;
import com.mak001.api.plugins.listeners.CTCPListener;
import com.mak001.api.plugins.listeners.JoinListener;
import com.mak001.api.plugins.listeners.Listener;
import com.mak001.api.plugins.listeners.MessageListener;
import com.mak001.api.plugins.listeners.ModeListener;
import com.mak001.api.plugins.listeners.NickChangeListener;
import com.mak001.api.plugins.listeners.NoticeListener;
import com.mak001.api.plugins.listeners.PartListener;
import com.mak001.api.plugins.listeners.PrivateMessageListener;
import com.mak001.api.plugins.listeners.QuitListener;
import com.mak001.ircBot.Bot;
import com.mak001.ircBot.permissions.PermissionHandler;
import com.mak001.ircBot.plugins.Permissions;
import com.mak001.ircBot.plugins.RegularCommands;

public class PluginManager {

	private final Map<String, Plugin> plugins = new HashMap<String, Plugin>();
	private final Map<String, Plugin> plugin_commands = new HashMap<String, Plugin>();
	private final Map<Plugin, List<Command>> commands = new HashMap<Plugin, List<Command>>();
	private final List<Command> full_command_list = new ArrayList<Command>();
	private final PluginLoader pluginLoader;
	private final PermissionHandler permissionHandler;


	private final List<List<? extends Listener>> listeners = new ArrayList<List<? extends Listener>>();
	private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private final List<CTCPListener> ctcpListeners = new ArrayList<CTCPListener>();
	private final List<JoinListener> joinListeners = new ArrayList<JoinListener>();
	private final List<MessageListener> messageListeners = new ArrayList<MessageListener>();
	private final List<ModeListener> modeListeners = new ArrayList<ModeListener>();
	private final List<NickChangeListener> nickChangeListeners = new ArrayList<NickChangeListener>();
	private final List<NoticeListener> noticeListeners = new ArrayList<NoticeListener>();
	private final List<PartListener> partListeners = new ArrayList<PartListener>();
	private final List<PrivateMessageListener> privateMessageListeners = new ArrayList<PrivateMessageListener>();
	private final List<QuitListener> quitListeners = new ArrayList<QuitListener>();

	public static final int CHANNEL_MODE_EVENT = 0, USER_MODE_EVENT = 1;
	public static final int FINGER_EVENT = 0, PING_EVENT = 1, VERSION_EVENT = 1;

	public PluginManager(Bot bot) {
		listeners.add(actionListeners);
		listeners.add(ctcpListeners);
		listeners.add(joinListeners);
		listeners.add(messageListeners);
		listeners.add(modeListeners);
		listeners.add(nickChangeListeners);
		listeners.add(noticeListeners);
		listeners.add(partListeners);
		listeners.add(privateMessageListeners);
		listeners.add(quitListeners);

		pluginLoader = new PluginLoader(bot, this);
		permissionHandler = new PermissionHandler(); // TODO - move to bot?
	}

	/**
	 * Loads the plugin in the specified file
	 * <p>
	 * File must be valid according to the current enabled Plugin interfaces
	 * 
	 * @param file
	 *            File containing the plugin to load
	 * @return The Plugin loaded, or null if it was invalid
	 * @throws InvalidPluginException
	 *             Thrown when the specified file is not a
	 *             valid plugin
	 * @throws UnknownDependencyException
	 *             If a required dependency could not
	 *             be found
	 */
	public synchronized Plugin loadPlugin(File file) throws InvalidPluginException {
		if (file == null) return null;
		Plugin result = pluginLoader.loadPlugin(file);
		if (result != null) {
			addPlugin(result);
		}
		return result;
	}

	public void reloadPlugin(String name) throws InvalidPluginException {
		pluginLoader.reloadPlugin(name);
	}

	public void reloadPlugin(Plugin plugin) throws InvalidPluginException {
		pluginLoader.reloadPlugin(plugin);
	}

	public void addPlugin(Plugin plugin) {
		if (plugins.get(plugin.getName()) == null && plugin_commands.get(plugin) == null) {
			plugins.put(plugin.getName(), plugin);
			plugin_commands.put(plugin.getCommand(), plugin);
			// commands.put(plugin, new ArrayList<Command>());
			registerListeners(plugin);
		} else {

		}
	}

	private synchronized void registerListeners(Plugin plugin) {
		if (plugin instanceof ActionListener) {
			actionListeners.add((ActionListener) plugin);
		}
		if (plugin instanceof CTCPListener) {
			ctcpListeners.add((CTCPListener) plugin);
		}
		if (plugin instanceof JoinListener) {
			joinListeners.add((JoinListener) plugin);
		}
		if (plugin instanceof MessageListener) {
			messageListeners.add((MessageListener) plugin);
		}
		if (plugin instanceof ModeListener) {
			modeListeners.add((ModeListener) plugin);
		}
		if (plugin instanceof NickChangeListener) {
			nickChangeListeners.add((NickChangeListener) plugin);
		}
		if (plugin instanceof NoticeListener) {
			noticeListeners.add((NoticeListener) plugin);
		}
		if (plugin instanceof PartListener) {
			partListeners.add((PartListener) plugin);
		}
		if (plugin instanceof PrivateMessageListener) {
			privateMessageListeners.add((PrivateMessageListener) plugin);
		}
		if (plugin instanceof QuitListener) {
			quitListeners.add((QuitListener) plugin);
		}
	}

	public synchronized void removePlugin(String name) {
		removePlugin(plugins.get(name));
	}

	public synchronized void removePlugin(Plugin plugin) { // TODO - test
		if (!isPermissions(plugin) && !isDefault(plugin)) {
			for (List<? extends Listener> _listeners : listeners) {
				for (Listener l : _listeners) {
					if (l.equals(plugin)) _listeners.remove(plugin);
				}
			}
			plugins.remove(plugin.getName());
			plugin_commands.remove(plugin.getCommand());
			full_command_list.removeAll(commands.remove(plugin));
			pluginLoader.unloadPlugin(plugin.getManifest().name());
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
		if (commands.get(command.getParentPlugin()) == null) {
			commands.put(command.getParentPlugin(), new ArrayList<Command>());
		}
		full_command_list.add(command);
		return commands.get(command.getParentPlugin()).add(command);
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
		if (!isPermissions(command.getParentPlugin())) {
			full_command_list.remove(command);
			return commands.get(command.getParentPlugin()).remove(command);
		}
		return false;
	}

	/**
	 * @return A map of Commands with the linked plugins.
	 */
	public Map<Plugin, List<Command>> getCommands() {
		return commands;
	}

	/**
	 * @return A full list of commands.
	 */
	public List<Command> getAllCommands() {
		List<Command> _commands = new ArrayList<Command>();
		for (Plugin plugin : commands.keySet()) {
			_commands.addAll(commands.get(plugin));
		}
		return _commands;
	}

	/**
	 * @param plugin
	 *            - The plugin to get the registered commands from.
	 * @return A list of Commands that are related to the plugin.
	 */
	public List<Command> getCommands(Plugin plugin) {
		return commands.get(plugin);
	}

	public boolean onCommand(String channel, String sender, String login, String hostname, String message) {
		// TODO - clean up so it doesn't call User.hasPermision() multiple times
		for (Command command : full_command_list) {
			for (String c : command.getCommand()) {
				if (permissionHandler.getUser(sender).hasPermission(command.getPermission())) {
					if (message.toLowerCase().startsWith(c.toLowerCase())) {
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
		}
		return false;
	}

	public void triggerActionListeners(String sender, String login, String hostname, String target, String action) {
		for (ActionListener listener : actionListeners) {
			listener.onAction(sender, login, hostname, target, action);
		}
	}

	public void triggerJoinListeners(String channel, String sender, String login, String hostname) {
		for (JoinListener listener : joinListeners) {
			listener.onJoin(channel, sender, login, hostname);
		}
	}

	public void triggerMessageListeners(String channel, String sender, String login, String hostname, String message) {
		for (MessageListener listener : messageListeners) {
			listener.onMessage(channel, sender, login, hostname, message);
		}
	}

	public void triggerNickChangeListeners(String oldNick, String login, String hostname, String newNick) {
		for (NickChangeListener listener : nickChangeListeners) {
			listener.onNickChange(oldNick, login, hostname, newNick);
		}
	}

	public void triggerNoticeListeners(String sourceNick, String sourceLogin, String sourceHostname, String target,
			String notice) {
		for (NoticeListener listener : noticeListeners) {
			listener.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
		}
	}

	public void triggerPartListeners(String channel, String sender, String login, String hostname) {
		for (PartListener listener : partListeners) {
			listener.onPart(channel, sender, login, hostname);
		}
	}

	public void triggerPrivateMessageListeners(String sender, String login, String hostname, String message) {
		for (PrivateMessageListener listener : privateMessageListeners) {
			listener.onPrivateMessage(sender, login, hostname, message);
		}
	}

	public void triggerQuitListeners(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
		for (QuitListener listener : quitListeners) {
			listener.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
		}
	}

	public void triggerFingerListeners(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		for (CTCPListener listener : ctcpListeners) {
			listener.onFinger(sourceNick, sourceLogin, sourceHostname, target);
		}
	}

	public void triggerPingListeners(String user, String sourceLogin, String sourceHostname, String target,
			String pingValue) {
		for (CTCPListener listener : ctcpListeners) {
			listener.onPing(user, sourceLogin, sourceHostname, target, pingValue);
		}
	}

	public void triggerVersionListeners(String sourceNick, String sourceLogin, String sourceHostname, String target) {
		for (CTCPListener listener : ctcpListeners) {
			listener.onVersion(sourceNick, sourceLogin, sourceHostname, target);
		}
	}

	public void triggerChannelModeListeners(String channel, String sourceNick, String sourceLogin,
			String sourceHostname, String mode) {
		for (ModeListener listener : modeListeners) {
			listener.onChannelMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
		}
	}

	public void triggerUserModeListeners(String channel, String sourceNick, String sourceLogin, String sourceHostname,
			String mode) {
		for (ModeListener listener : modeListeners) {
			listener.onUserMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
		}
	}

	public Plugin getPlugin(String name) {
		return plugins.get(name);
	}

	public Plugin getPluginByCommand(String command) {
		return plugin_commands.get(command);
	}

	public Collection<Plugin> getPlugins() {
		return plugins.values();
	}

	private boolean isPermissions(Plugin p) {
		return p.getName().equals("Permissions") && p instanceof Permissions;
	}

	private boolean isDefault(Plugin p) {
		return p.getName().equals("Default commands") && p instanceof RegularCommands;
	}

}
