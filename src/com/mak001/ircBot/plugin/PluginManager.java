package com.mak001.ircBot.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.mak001.ircBot.plugin.permissions.Permission;


public class PluginManager {

	private final Map<String, Plugin> plugins = new HashMap<String, Plugin>();
	private final Map<ListenerTypes, List<Listener>> listeners = new HashMap<ListenerTypes, List<Listener>>();
	private final Map<Plugin, List<Command>> commands = new HashMap<Plugin, List<Command>>();
	private final SimplePluginLoader pluginLoader;
	private final Map<String, Permission> permissions = new HashMap<String, Permission>();

	public static final int CHANNEL_MODE_EVENT = 0, USER_MODE_EVENT = 1;
	public static final int FINGER_EVENT = 0, PING_EVENT = 1, VERSION_EVENT = 1;

	/**
	 * A list of listener classes
	 * 
	 * @author Mak001
	 */
	private enum ListenerTypes
	{
		ACTION_LISTENER(ActionListener.class, "Action listener"), CTCP_LISTENER(CTCPListener.class, "CTCP listener"), JOIN_LISTENER(
				JoinListener.class, "Join listener"), MESSAGE_LISTENER(MessageListener.class, "Message listener"), MODE_LISTENER(
				ModeListener.class, "Mode listener"), NICK_CHANGE_LISTENER(NickChangeListener.class,
				"Nick change listener"), NOTICE_LISTENER(NoticeListener.class, "Notice listener"), PART_LISTENER(
				PartListener.class, "Part listener"), PRIVATE_MESSAGE_LISTENER(PrivateMessageListener.class,
				"Private message listener"), QUIT_LISTENER(QuitListener.class, "Quit listener");

		private final Class<? extends Listener> clazz;
		private final String name;

		private ListenerTypes(Class<? extends Listener> clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Class<? extends Listener> getBaseClass() {
			return clazz;
		}
	}


	public PluginManager(Bot bot) {
		pluginLoader = new SimplePluginLoader(bot);
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
			plugins.put(getPluginName(result), result);
			registerListeners(result);
		}
		return result;
	}

	public void addPlugin(Plugin plugin) {
		if (plugins.get(getPluginName(plugin)) == null) {
			plugins.put(getPluginName(plugin), plugin);
			registerListeners(plugin);
		} else {

		}
	}

	private synchronized void registerListeners(Plugin plugin) {
		for (ListenerTypes type : ListenerTypes.values()) {
			if (plugin.getClass().isInstance(type.getBaseClass())) {
				listeners.get(type).add(type.getBaseClass().cast(plugin));
			}
		}
	}

	public synchronized void removePlugin(String name) {
		removePlugin(plugins.get(name));
	}

	public synchronized void removePlugin(Plugin plugin) {
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
		commands.remove(plugin);
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
		return commands.get(command.getParentPlugin()).remove(command);
	}

	/**
	 * @return A map of Commands with the linked plugins.
	 */
	public Map<Plugin, List<Command>> getCommands() {
		return commands;
	}

	/**
	 * @param plugin
	 *            - The plugin to get the registered commands from.
	 * @return A list of Commands that are related to the plugin.
	 */
	public List<Command> getCommands(Plugin plugin) {
		return commands.get(plugin);
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

}
