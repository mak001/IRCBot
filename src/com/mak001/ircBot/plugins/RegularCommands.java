package com.mak001.ircBot.plugins;

import org.jibble.pircbot.PircBot;

import com.mak001.ircBot.Bot;
import com.mak001.api.plugins.Command;
import com.mak001.api.plugins.Command.CommandAction;
import com.mak001.api.plugins.Manifest;
import com.mak001.api.plugins.Plugin;
import com.mak001.ircBot.plugins.compiler.MyCompiler;
import com.mak001.ircBot.plugins.compiler.ReadFile;
import com.mak001.ircBot.plugins.permissions.IRCPermissions;
import com.mak001.ircBot.settings.Settings;

@Manifest(authors = { "mak001" }, name = "Default commands", version = 1.0, description = "The default commands that came with the bot (not including the permission commands)")
public class RegularCommands extends Plugin {

	private final String admin = "main.admin";
	private final String plugins = "main.plugins";
	private final String shutdown_node = "main.shutdown";
	private String prefix = Settings.get(Settings.COMMAND_PREFIX);

	public RegularCommands(Bot bot) {
		super(bot, "CMD");

		bot.registerCommand(say);
		bot.registerCommand(join);
		bot.registerCommand(part);
		bot.registerCommand(nick);
		bot.registerCommand(set);
		bot.registerCommand(add_plugin);
		bot.registerCommand(load_plugin);
		bot.registerCommand(reload_plugin);
		bot.registerCommand(unload_plugin);
		bot.registerCommand(shutdown);
		bot.registerCommand(about);
		bot.registerCommand(help);
	}

	public void sendHelp(String sender, String message) {

		String[] s = message.split(" ");
		String prefix = Settings.get(Settings.COMMAND_PREFIX);

		if (IRCPermissions.hasPermission(sender, "main.admin")) {
			if (message != null && !message.isEmpty()) {
				if (s[0].equalsIgnoreCase("SAY")) {
					bot.sendMessage(sender, "Send a message to the default channel : Syntax: " + prefix
							+ "CMD SAY <stuff to say>");
					bot.sendMessage(sender, "Send a message to a channel : Syntax: " + prefix
							+ "CMD SAY #CHANNEL <stuff to say>");
				} else if (s[0].equalsIgnoreCase("JOIN")) {
					bot.sendMessage(sender, "Joins a channel : Syntax: " + prefix + "CMD JOIN <CHANNEL_NAME>");
				} else if (s[0].equalsIgnoreCase("LEAVE")) {
					bot.sendMessage(sender, "Leaves a channel : Syntax: " + prefix + "CMD LEAVE <CHANNEL_NAME>");
				} else if (s[0].equalsIgnoreCase("PART")) {
					bot.sendMessage(sender, "Leaves a channel : Syntax: " + prefix + "CMD PART <CHANNEL_NAME>");
				} else if (s[0].equalsIgnoreCase("SET")) {
					if (s[1].equalsIgnoreCase("NICK")) {
						bot.sendMessage(sender, "Changes the bot's default nick to use : Syntax: " + prefix
								+ "CMD SET NICK <NEW_NICK>");
					} else if (s[1].equalsIgnoreCase("<PREFIX>")) {
						bot.sendMessage(sender,
								"Changes the bot's default command <PREFIX> (what commands start with)  : Syntax: "
										+ prefix + "CMD SET COMMAND_PREFIX <PREFIX>");
					}
				}
			} else if (s[0].equalsIgnoreCase("NICK")) {
				bot.sendMessage(sender, "Changes the bot's current nick : Syntax: " + prefix + "CMD NICK <NEW_NICK>");
			} else {
				bot.sendMessage(sender, "Send a message to the default channel : Syntax: " + prefix
						+ "CMD SAY <stuff to say>");
				bot.sendMessage(sender, "Send a message to a channel : Syntax: " + prefix
						+ "CMD SAY #CHANNEL <stuff to say>");
				bot.sendMessage(sender, "Joins a channel : Syntax: " + prefix + "CMD JOIN <CHANNEL_NAME>");
				bot.sendMessage(sender, "Leaves a channel : Syntax: " + prefix + "CMD LEAVE <CHANNEL_NAME>");
				bot.sendMessage(sender, "Leaves a channel : Syntax: " + prefix + "CMD PART <CHANNEL_NAME>");
				bot.sendMessage(sender,
						"Changes the bot's default command <PREFIX> (what commands start with)  : Syntax: " + prefix
								+ "CMD SET COMMAND_PREFIX <PREFIX>");
				bot.sendMessage(sender, "Changes the bot's default nick to use : Syntax: " + prefix
						+ "CMD SET NICK <NEW_NICK>");
				bot.sendMessage(sender, "Changes the bot's current nick : Syntax: " + prefix + "CMD NICK <NEW_NICK>");
			}
		} else {
			bot.sendMessage(sender, "You do not have access to admin commands.");
		}
		if (IRCPermissions.hasPermission(sender, "main.add")) {
			bot.sendMessage(sender, "Adds new plugins : Syntax : " + prefix + "CMD ADD <SCRIPT_URL>");
			bot.sendMessage(sender, "Currently supported sites: paste.strictfp.com | pastebin.com");
			bot.sendMessage(sender, "If the script is local use : " + prefix + "CMD ADD LOCAL <FILE_NAME>");
		} else {
			bot.sendMessage(sender, "You do not have access to add plugins.");
		}
	}

	private void addLocal(String sender, String fileName) {
		MyCompiler.compile(bot, sender, fileName);
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
				bot.removeChannel(channel);
			}
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
				Settings.put(Settings.NICK, command_array[1], true);
				bot.changeNick(command_array[1]);

			} else if (command_array[0].equalsIgnoreCase("COMMAND_PREFIX")) {
				Settings.put(Settings.COMMAND_PREFIX, command_array[1], true);

			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Changes the bot's default nick to use : Syntax: " + prefix + "SET NICK <NEW_NICK>");
			bot.sendMessage(sender, "Changes the bot's default command <PREFIX> (what commands start with)  : Syntax: "
					+ prefix + "SET COMMAND_PREFIX <PREFIX>");
		}
	});

	private Command add_plugin = new Command(this, "ADD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			if (additional.contains("strictfp")) {
				if (additional.contains("/raw/")) {
					ReadFile.addStrictFP(bot, sender, additional);
				} else {
					ReadFile.addStrictFP(bot, sender,
							additional.replace("http://paste.strictfp.com/", "http://paste.strictfp.com/raw/"));
				}
			} else if (additional.contains("local")) {
				addLocal(sender, additional.replace("local ", ""));
			} else if (additional.contains("pastebin")) {
				if (additional.contains("raw.php?i=")) {
					ReadFile.addPasteBin(bot, sender, additional);
				} else {
					ReadFile.addPasteBin(bot, sender,
							additional.replace("http://pastebin.com/", "http://pastebin.com/raw.php?i="));
				}
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			// TODO Auto-generated method stub
		}
	});

	private Command reload_plugin = new Command(this, "RELOAD PLUGIN", plugins, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			Plugin plugin = bot.getPluginByName(additional);
			if (plugin != null) {
				bot.reloadPlugin(plugin, sender);
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
			Plugin plugin = bot.getPluginByName(additional);
			if (plugin != null) {
				bot.unloadPlugin(plugin, sender);
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
			bot.loadPlugin(additional, sender, 0);
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
				for (Plugin p : bot.getPlugins()) {
					if (p.GENERAL_COMMAND.equalsIgnoreCase(additional)) {
						bot.sendMessage(sender, getPluginInfo(p));
						return;
					}
				}
			} else {
				bot.sendMessage(target, "MAK001's bot built on PIRC " + PircBot.VERSION);
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets info on the bot or plugin : Syntax: " + prefix
					+ "ABOUT <GENERAL PLUGIN COMMAND>");
		}
	});

	private Command help = new Command(this, "HELP", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			if (additional == null || additional.equals("")) {
				bot.sendMessage(sender, "This will list every plugin command, use " + Settings.COMMAND_PREFIX
						+ "HELP <PLUGIN COMMAND>   for more help with an individual plugin.");
				for (Plugin p : bot.getPlugins()) {
					String name = p.getManifest().name();
					bot.sendMessage(sender, name + " - " + p.GENERAL_COMMAND);
				}
			} else {
				bot.sendMessage(sender, "This will list every commands from a plugin");
				for (Plugin p : bot.getPlugins()) {
					if (additional.equalsIgnoreCase(p.GENERAL_COMMAND)) {
						for (Command c : bot.getCommands()) {
							if (c.getParentPlugin().equals(p)) {
								c.onHelp(channel, sender, login, hostname);
							}
						}
					}
				}
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
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
		return pluginManifest.name() + " by: " + authors + "   version: " + pluginManifest.version() + site
				+ description;
	}
}