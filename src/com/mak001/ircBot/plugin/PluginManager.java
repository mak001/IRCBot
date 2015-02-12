package com.mak001.ircBot.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jibble.pircbot.PircBot;

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
import com.mak001.ircBot.plugin.permissions.PermissionHandler;
import com.mak001.ircBot.plugins.Permissions;
import com.mak001.ircBot.plugins.RegularCommands;


public class PluginManager {

	private final Map<String, Plugin> plugins = new HashMap<String, Plugin>();
	private final Map<String, Plugin> plugin_commands = new HashMap<String, Plugin>();
	private final Map<ListenerTypes, List<Listener>> listeners = new HashMap<ListenerTypes, List<Listener>>();
	private final Map<Plugin, List<Command>> commands = new HashMap<Plugin, List<Command>>();
	private final List<Command> full_command_list = new ArrayList<Command>();
	private final PluginLoader pluginLoader;
	private final PermissionHandler permissionHandler;

	public static final int CHANNEL_MODE_EVENT = 0, USER_MODE_EVENT = 1;
	public static final int FINGER_EVENT = 0, PING_EVENT = 1, VERSION_EVENT = 1;

	/**
	 * A list of listener classes
	 * 
	 * @author Mak001
	 */
	public enum ListenerTypes
	{
		ACTION_LISTENER("Action listener"), CTCP_LISTENER("CTCP listener"), JOIN_LISTENER("Join listener"), MESSAGE_LISTENER(
				"Message listener"), MODE_LISTENER("Mode listener"), NICK_CHANGE_LISTENER("Nick change listener"), NOTICE_LISTENER(
				"Notice listener"), PART_LISTENER("Part listener"), PRIVATE_MESSAGE_LISTENER("Private message listener"), QUIT_LISTENER(
				"Quit listener");

		private final String name;

		private ListenerTypes(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public PluginManager(Bot bot) {
		pluginLoader = new PluginLoader(bot, this);
		permissionHandler = new PermissionHandler();

		addPlugin(new RegularCommands(bot));
		addPlugin(new Permissions(bot));

		for (ListenerTypes type : ListenerTypes.values()) {
			listeners.put(type, new ArrayList<Listener>());
		}
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
		if (plugins.get(getPluginName(plugin)) == null && plugin_commands.get(plugin) == null) {
			plugins.put(getPluginName(plugin), plugin);
			plugin_commands.put(getPluginCommand(plugin), plugin);
			registerListeners(plugin);
		} else {

		}
	}

	private synchronized void registerListeners(Plugin plugin) {
		if (plugin instanceof ActionListener) {
			listeners.get(ListenerTypes.ACTION_LISTENER).add((ActionListener) plugin);
		}
		if (plugin instanceof CTCPListener) {
			listeners.get(ListenerTypes.CTCP_LISTENER).add((CTCPListener) plugin);
		}
		if (plugin instanceof JoinListener) {
			listeners.get(ListenerTypes.JOIN_LISTENER).add((JoinListener) plugin);
		}
		if (plugin instanceof MessageListener) {
			listeners.get(ListenerTypes.MESSAGE_LISTENER).add((MessageListener) plugin);
		}
		if (plugin instanceof ModeListener) {
			listeners.get(ListenerTypes.MODE_LISTENER).add((ModeListener) plugin);
		}
		if (plugin instanceof NickChangeListener) {
			listeners.get(ListenerTypes.NICK_CHANGE_LISTENER).add((NickChangeListener) plugin);
		}
		if (plugin instanceof NoticeListener) {
			listeners.get(ListenerTypes.NOTICE_LISTENER).add((NoticeListener) plugin);
		}
		if (plugin instanceof PartListener) {
			listeners.get(ListenerTypes.PART_LISTENER).add((PartListener) plugin);
		}
		if (plugin instanceof PrivateMessageListener) {
			listeners.get(ListenerTypes.PRIVATE_MESSAGE_LISTENER).add((PrivateMessageListener) plugin);
		}
		if (plugin instanceof QuitListener) {
			listeners.get(ListenerTypes.QUIT_LISTENER).add((QuitListener) plugin);
		}
	}

	public synchronized void removePlugin(String name) {
		removePlugin(plugins.get(name));
	}

	public synchronized void removePlugin(Plugin plugin) {
		if (!isPermissions(plugin)) {
			Iterator<Entry<ListenerTypes, List<Listener>>> it = listeners.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<ListenerTypes, List<Listener>> entry = it.next();
				List<Listener> listener_list = entry.getValue();
				if (listener_list != null && !listener_list.isEmpty()) {
					for (Listener listener : listener_list) {
						if (listener.equals(plugin)) listener_list.remove(plugin);
					}
				}
			}
			plugins.remove(getPluginName(plugin));
			plugin_commands.remove(getPluginCommand(plugin));
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
		for (Command command : full_command_list) {
			// TODO - check if user has permission
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

	/**
	 * Triggers all the listeners with the given type. The
	 * listeners are supplied with the info they need with vars[].
	 * 
	 * @param type
	 *            - The type of listener to trigger.
	 * @param vars
	 *            - the data to pass onto the listener
	 */
	public void triggerListener(ListenerTypes type, String... vars) {
		triggerListener(type, 0, vars);
	}

	/**
	 * Triggers all the listeners with the given type and sub_type. The
	 * listeners are supplied with the info they need with vars[].
	 * 
	 * @param type
	 *            - The type of listener to trigger.
	 * @param sub_type
	 *            - If the listener has multiple methods, this will distinguish
	 *            when to use each
	 * @param vars
	 *            - the data to pass onto the listener
	 */
	public void triggerListener(ListenerTypes type, int sub_type, String... vars) {
		List<Listener> _listeners = listeners.get(type);
		for (Listener listener : _listeners) {
			switch (type) {
				case ACTION_LISTENER:
					((ActionListener) listener).onAction(vars[0], vars[1], vars[2], vars[3], vars[4]);
					break;
				case CTCP_LISTENER:
					if (sub_type == FINGER_EVENT) {
						((CTCPListener) listener).onFinger(vars[0], vars[1], vars[2], vars[3]);
					} else if (sub_type == PING_EVENT) {
						((CTCPListener) listener).onPing(vars[0], vars[1], vars[2], vars[3], vars[4]);
					} else if (sub_type == VERSION_EVENT) {
						((CTCPListener) listener).onVersion(vars[0], vars[1], vars[2], vars[3]);
					}
					break;
				case JOIN_LISTENER:
					((JoinListener) listener).onJoin(vars[0], vars[1], vars[2], vars[3]);
					break;
				case MESSAGE_LISTENER:
					((MessageListener) listener).onMessage(vars[0], vars[1], vars[2], vars[3], vars[4]);
					break;
				case MODE_LISTENER:
					if (sub_type == CHANNEL_MODE_EVENT) {
						((ModeListener) listener).onChannelMode(vars[0], vars[1], vars[2], vars[3], vars[4]);
					} else if (sub_type == USER_MODE_EVENT) {
						((ModeListener) listener).onChannelMode(vars[0], vars[1], vars[2], vars[3], vars[4]);
					}
					break;
				case NICK_CHANGE_LISTENER:
					((NickChangeListener) listener).onNickChange(vars[0], vars[1], vars[2], vars[3]);
					break;
				case NOTICE_LISTENER:
					((NoticeListener) listener).onNotice(vars[0], vars[1], vars[2], vars[3], vars[4]);
					break;
				case PART_LISTENER:
					((PartListener) listener).onPart(vars[0], vars[1], vars[2], vars[3]);
					break;
				case PRIVATE_MESSAGE_LISTENER:
					((PrivateMessageListener) listener).onPrivateMessage(vars[0], vars[1], vars[2], vars[3]);
					break;
				case QUIT_LISTENER:
					((QuitListener) listener).onQuit(vars[0], vars[1], vars[2], vars[3]);
					break;
				default:
					break;
			}
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

	/**
	 * Gets a plugin's description. May return an empty String.
	 * 
	 * @param plugin
	 *            - The plugin to get the description of.
	 * @return The description of the given plugin.
	 */
	public String getPluginDescription(Plugin plugin) {
		return plugin.getManifest().description();
	}

	/**
	 * Gets a plugin's name
	 * 
	 * @param plugin
	 *            - The plugin to get the name of.
	 * @return The name of the given plugin.
	 */
	public String getPluginName(Plugin plugin) {
		return plugin.getManifest().name();
	}

	/**
	 * Gets a plugin's website. May return an empty String.
	 * 
	 * @param plugin
	 *            - The plugin to get the website of.
	 * @return The website of the given plugin.
	 */
	public String getPluginSite(Plugin plugin) {
		return plugin.getManifest().website();
	}

	/**
	 * Gets a plugin's version
	 * 
	 * @param plugin
	 *            - The plugin to get the version of.
	 * @return The version of the given plugin.
	 */
	public double getPluginVersion(Plugin plugin) {
		return plugin.getManifest().version();
	}

	/**
	 * Gets a plugin's authors. It might only return a one long String array.
	 * 
	 * @param plugin
	 *            - The plugin to get the authors of.
	 * @return The authors of the given plugin.
	 */
	public String[] getPluginAuthors(Plugin plugin) {
		return plugin.getManifest().authors();
	}

	public String getPluginCommand(Plugin plugin) {
		return plugin.GENERAL_COMMAND;
	}

	private boolean isPermissions(Plugin p) {
		return getPluginName(p).equals("Permissions") && p instanceof Permissions;
	}

}
