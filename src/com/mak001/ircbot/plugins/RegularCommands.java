package com.mak001.ircbot.plugins;

import com.mak001.api.plugins.Command;
import com.mak001.api.plugins.Command.CommandAction;
import com.mak001.api.plugins.Manifest;
import com.mak001.api.plugins.Plugin;
import com.mak001.ircbot.Bot;
import com.mak001.ircbot.SettingsManager;
import com.mak001.ircbot.plugin.InvalidPluginException;

@Manifest(authors = { "mak001" }, name = "Default commands", version = 1.0, description = "The default commands that came with the bot (not including the permission commands)")
public class RegularCommands extends Plugin {

	private final String admin = "main.admin";
	private final String plugins = "main.plugins";
	private final String shutdown_node = "main.shutdown";
	private String prefix = SettingsManager.getCommandPrefix();

	public RegularCommands(Bot bot) {
		super(bot, "CMD");

		bot.getPluginManager().registerCommand(say);
		bot.getPluginManager().registerCommand(join);
		bot.getPluginManager().registerCommand(part);
		bot.getPluginManager().registerCommand(nick);
		bot.getPluginManager().registerCommand(set);
		bot.getPluginManager().registerCommand(broadcast);
		bot.getPluginManager().registerCommand(load_plugin);
		bot.getPluginManager().registerCommand(reload_plugin);
		bot.getPluginManager().registerCommand(unload_plugin);
		bot.getPluginManager().registerCommand(shutdown);
		bot.getPluginManager().registerCommand(about);
	}

	private boolean isChannel(String string) {
		return string.substring(0, 1).equals("#");
	}

	private Command say = new Command(this, "SAY", admin, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String s[] = additional.split(" ");
			if (isChannel(s[0])) {
				bot.sendMessage(s[0], additional.replace(s[0] + " ", ""));
			} else {
				bot.sendMessage(channel == null ? sender : channel, additional);
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Send a message to the current channel : Syntax: " + prefix + "SAY <stuff to say>");
			bot.sendMessage(sender, "Send a message to a channel : Syntax: " + prefix + "SAY #CHANNEL <stuff to say>");
		}
	});

	private Command join = new Command(this, "JOIN", admin, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			if (!additional.contains(" ")) {
				if (isChannel(additional)) {
					bot.addChannel(additional);
				} else {
					bot.addChannel("#" + additional);
				}
			} else {
				bot.sendMessage(sender, additional + " is not a valid channel");
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Joins a channel : Syntax: " + prefix + "JOIN <CHANNEL_NAME>");
		}
	});

	private Command part = new Command(this, new String[] { "LEAVE", "PART" }, admin, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			if (additional != null && additional.equals("") && channel != null && !channel.equals("")) {
				// bot.removeChannel(channel);
			}// TODO?
			if (!additional.contains(" ")) {
				if (isChannel(additional)) {
					bot.removeChannel(additional);
				} else {
					bot.removeChannel("#" + additional);
				}
			} else {
				bot.sendMessage(sender, additional + " is not a valid channel");
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Leaves a channel : Syntax: " + prefix + "[LEAVE | PART] <CHANNEL_NAME>");
		}
	});


	private Command nick = new Command(this, "NICK", admin, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			if (additional != null && !additional.equals("") && !additional.contains(" ")) {
				bot.changeNick(additional);
			} else {
				bot.sendMessage(sender, additional + " is not a valid nick");
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Changes the bot's current nick : Syntax: " + prefix + "NICK <NEW_NICK>");
		}
	});

	private Command set = new Command(this, "SET", admin, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] command_array = additional.split(" ");
			if (command_array[0].equalsIgnoreCase("NICK")) {
				SettingsManager.changeNick(command_array[1]);
				bot.changeNick(command_array[1]);

			} else if (command_array[0].equalsIgnoreCase("COMMAND_PREFIX")) {
				SettingsManager.changeCommandPrefix(command_array[1]);

			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Changes the bot's default nick to use : Syntax: " + prefix + "SET NICK <NEW_NICK>");
			bot.sendMessage(sender, "Changes the bot's default command <PREFIX> (what commands start with)  : Syntax: "
					+ prefix + "SET COMMAND_PREFIX <PREFIX>");
		}
	});

	private Command broadcast = new Command(this, "BROADCAST", admin, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			for (String chan : bot.getChannels()) {
				bot.sendMessage(chan, additional);
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Broadcasts a specified sting in all joined channels");
		}
	});

	private Command reload_plugin = new Command(this, "RELOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			Plugin plugin = bot.getPluginManager().getPlugin(additional);
			if (plugin != null) {
				try {
					bot.getPluginManager().reloadPlugin(plugin);
				} catch (InvalidPluginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				bot.sendMessage(sender, "No plugin named " + additional + ".");
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Reloads a plugin : Syntax: " + prefix + "RELOAD PLUGIN <PLUGIN'S_GENERAL_COMMAND>");
		}
	});

	private Command unload_plugin = new Command(this, "UNLOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			Plugin plugin = bot.getPluginManager().getPlugin(additional);
			if (plugin != null) {
				bot.getPluginManager().removePlugin(plugin);
			} else {
				bot.sendMessage(sender, "No plugin named " + additional + ".");
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Unloads a plugin : Syntax: " + prefix + "UNLOAD PLUGIN <PLUGIN'S_GENERAL_COMMAND>");
		}
	});

	private Command load_plugin = new Command(this, "LOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			// bot.loadPlugin(additional, sender, 0);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Loads a plugin : Syntax: " + prefix + "LOAD PLUGIN <PLUGIN_CLASS_OR_JAR_NAME>");
		}
	});


	private Command shutdown = new Command(this, "SHUTDOWN", shutdown_node, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			bot.shutDown(sender);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Shuts down the bot : Syntax: " + prefix + "SHUTDOWN");
		}
	});

	private Command about = new Command(this, "ABOUT", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = channel == null ? sender : channel;
			if (additional != null && !additional.equals("")) {
				for (Plugin p : bot.getPluginManager().getPlugins()) {
					if (p.GENERAL_COMMAND.equalsIgnoreCase(additional)) {
						bot.sendMessage(sender, getPluginInfo(p));
						return;
					}
				}
			} else {
				bot.sendMessage(target, "MAK001's bot built on PIRC " + Bot.VERSION);
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets info on the bot or plugin : Syntax: " + prefix
					+ "ABOUT <GENERAL PLUGIN COMMAND>");
		}
	});

	private String getPluginInfo(Plugin p) {
		Manifest pluginManifest = p.getManifest();
		String authors = "";
		String site = pluginManifest.website().equals("") ? "" : "  ,  site: " + pluginManifest.website();
		String description = pluginManifest.description().equals("") ? "   no description" : "  description: "
				+ pluginManifest.description();
		for (String author : pluginManifest.authors()) {
			authors = authors + authors == "" ? "" : ", " + author;
		}
		return pluginManifest.name() + " by: " + authors + "   version: " + pluginManifest.version() + " - " + site
				+ " - " + description;
	}
}