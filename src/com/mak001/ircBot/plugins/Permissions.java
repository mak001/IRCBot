package com.mak001.ircBot.plugins;

import java.util.List;

import com.mak001.api.plugins.Command;
import com.mak001.api.plugins.Command.CommandAction;
import com.mak001.api.plugins.Manifest;
import com.mak001.api.plugins.Plugin;
import com.mak001.ircBot.Bot;
import com.mak001.ircBot.settings.Settings;

@Manifest(authors = { "mak001" }, name = "Permissions", version = 1.0, description = "Manage what people can and cannot use, you can also have exceptions.")
public class Permissions extends Plugin {

	private final String add = "perms.add";
	private final String remove = "perms.remove";
	private String prefix = Settings.get(Settings.COMMAND_PREFIX);

	public Permissions(Bot bot) {
		super(bot, "PEX");

		bot.getPluginManager().registerCommand(get_user_permissions);
		bot.getPluginManager().registerCommand(add_user_permission);
		bot.getPluginManager().registerCommand(remove_user_permission);
	}

	private Command get_user_permissions = new Command(this, "GET PERMISSIONS", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : sender;
			List<String> per = bot.getPermissionHandler().getUser(target).getPermissions();
			if (per == null) {
				bot.sendMessage(sender, target + " has no permissions");
				return;
			}
			for (int i = 0; i < per.size(); i++) {
				bot.sendMessage(sender, per.get(i));
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets the user's permissions  : Syntax: " + prefix + "GET PERMISSIONS <USER>");
		}
	});

	private Command add_user_permission = new Command(this, "ADD PERMISSION", add, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			bot.getPermissionHandler().addPermission(additions[0], additions[1]);
			bot.sendMessage(sender, "Added premission node " + additions[1] + " for user " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Adds a permission to a user  : Syntax: " + prefix
					+ "ADD PERMISSION <USER> <PERMISSION_NODE>");
		}
	});

	private Command remove_user_permission = new Command(this, "REMOVE PERMISSION", remove, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			if (bot.getPermissionHandler().getUser(additions[0]) == null) {
				bot.sendMessage(sender, "No user with the name " + additions[0]);
				return;
			}
			if (!bot.getPermissionHandler().getUser(additions[0]).getPermissions().contains(additions[1])) {
				bot.sendMessage(sender, "The user does not have that permission.");
				return;
			}

			bot.getPermissionHandler().removePermission(additions[0], additions[1]);
			bot.sendMessage(sender, "removed premission node " + additions[1] + " for user " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Removes a permission to a user  : Syntax: " + prefix
					+ "REMOVE USER PERMISSION <USER> <PERMISSION_NODE>");
		}
	});
}