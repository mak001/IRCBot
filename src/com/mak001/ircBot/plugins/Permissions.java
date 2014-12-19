package com.mak001.ircBot.plugins;

import java.util.ArrayList;

import com.mak001.ircBot.Bot;
import com.mak001.api.plugins.Command;
import com.mak001.api.plugins.Command.CommandAction;
import com.mak001.api.plugins.Manifest;
import com.mak001.api.plugins.Plugin;
import com.mak001.ircBot.plugins.permissions.IRCPermissions;
import com.mak001.ircBot.plugins.permissions.User;
import com.mak001.ircBot.settings.Settings;

@Manifest(authors = { "mak001" }, name = "Permissions", version = 1.0, description = "Manage what people can and cannot use, you can also have exceptions.")
public class Permissions extends Plugin {

	private final String add = "perms.add";
	private final String remove = "perms.remove";
	private String prefix = Settings.get(Settings.COMMAND_PREFIX);

	public Permissions(Bot bot) {
		super(bot, "PEX");

		bot.registerCommand(get_user_group);
		bot.registerCommand(get_user_exceptions);
		bot.registerCommand(get_user_permissions);

		bot.registerCommand(get_group_exceptions);
		bot.registerCommand(get_group_permissions);
		bot.registerCommand(get_group_users);

		bot.registerCommand(add_user_permission);
		bot.registerCommand(add_group_permission);
		bot.registerCommand(add_group_user);

		bot.registerCommand(remove_user_permission);
		bot.registerCommand(remove_group_permission);
		bot.registerCommand(remove_group_user);
	}

	private Command get_user_group = new Command(this, "GET USER GROUP", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : sender;
			String group = IRCPermissions.getUser(target) == null ? IRCPermissions.getDefaultGroup() : IRCPermissions
					.getUser(target).getGroup();
			bot.sendMessage(sender, group);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets the user's permission group  : Syntax: " + prefix + "GET USER GROUP <USER>");
		}
	});

	private Command get_user_exceptions = new Command(this, "GET USER EXCEPTIONS", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : sender;
			if (IRCPermissions.getUser(target) == null) {
				bot.sendMessage(sender, "User has no exceptions.");
				return;
			}
			ArrayList<String> per = IRCPermissions.getUser(target).getExceptions();
			if (per.size() == 0) {
				bot.sendMessage(sender, "User has no exceptions");
				return;
			}
			for (int i = 0; i < per.size(); i++) {
				bot.sendMessage(sender, per.get(i));
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets the user's permission exceptions  : Syntax: " + prefix
					+ "GET USER EXCEPTIONS <USER>");
		}
	});

	private Command get_user_permissions = new Command(this, "GET USER PERMISSIONS", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : sender;
			ArrayList<String> per = IRCPermissions.getUser(target) == null ? IRCPermissions.getGroup(
					IRCPermissions.getDefaultGroup()).getPermissions() : IRCPermissions.getUser(target)
					.getPermissions();
			for (int i = 0; i < per.size(); i++) {
				bot.sendMessage(sender, per.get(i));
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets the user's permissions  : Syntax: " + prefix + "GET USER PERMISSIONS <USER>");
		}
	});

	private Command get_group_exceptions = new Command(this, "GET GROUP EXCEPTIONS", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional
					: IRCPermissions.getUser(sender) == null ? IRCPermissions.getDefaultGroup() : IRCPermissions
							.getUser(sender).getGroup();
			if (IRCPermissions.getGroup(target) == null) {
				bot.sendMessage(sender, "No group named " + target);
				return;
			}
			ArrayList<String> per = IRCPermissions.getGroup(target).getExceptions();
			for (int i = 0; i < per.size(); i++) {
				bot.sendMessage(sender, per.get(i));
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets the group's permission exceptions  : Syntax: " + prefix
					+ "GET GROUP EXCEPTIONS <GROUP>");
		}
	});

	private Command get_group_permissions = new Command(this, "GET GROUP PERMISSIONS", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : IRCPermissions
					.getUser(sender).getGroup();
			if (IRCPermissions.getGroup(target) == null) {
				bot.sendMessage(sender, "No group named " + target);
				return;
			}
			ArrayList<String> per = IRCPermissions.getGroup(target).getPermissions();
			for (int i = 0; i < per.size(); i++) {
				bot.sendMessage(sender, per.get(i));
			}
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets the group's permissions  : Syntax: " + prefix
					+ "GET GROUP PERMISSIONS <GROUP>");
		}
	});

	private Command get_group_users = new Command(this, "GET GROUP USERS", new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String target = (additional != null && !additional.equals("")) ? additional : IRCPermissions
					.getUser(sender).getGroup();

			if (IRCPermissions.getGroup(target) == null) {
				bot.sendMessage(sender, "No group named " + target);
				return;
			}

			String user_list = "";
			for (User u : IRCPermissions.getUsers().values()) {
				if (u.getGroup().equalsIgnoreCase(target)) {
					user_list = user_list.equals("") ? u.getName() : user_list + ", " + u.getName();
				}
			}
			bot.sendMessage(sender, user_list);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Gets all the users in a group : Syntax: " + prefix + "GET GROUP USERS <GROUP>");
		}
	});


	private Command add_user_permission = new Command(this, "ADD USER PERMISSION", add, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			IRCPermissions.addUserPermission(additions[0], additions[1]);
			bot.sendMessage(sender, "Added premission node " + additions[1] + " for user " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Adds a permission to a user  : Syntax: " + prefix
					+ "ADD USER PERMISSION <USER> <PERMISSION_NODE>");
		}
	});

	private Command add_group_permission = new Command(this, "ADD GROUP PERMISSION", add, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			IRCPermissions.addGroupPermission(additions[0], additions[1]);
			bot.sendMessage(sender, "Added premission node " + additions[1] + " for group " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Adds a permission to a group  : Syntax: " + prefix
					+ "ADD GROUP PERMISSION <GROUP> <PERMISSION_NODE>");
		}
	});

	private Command add_group_user = new Command(this, "ADD GROUP USER", add, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			IRCPermissions.setUserGroup(additions[1], additions[0]);
			bot.sendMessage(sender, "Added user " + additions[1] + " to group " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Adds a user to a group  : Syntax: " + prefix + "ADD GROUP USER <GROUP> <USER>");
		}
	});

	private Command remove_user_permission = new Command(this, "REMOVE USER PERMISSION", remove, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			if (IRCPermissions.getUser(additions[0]) == null) {
				bot.sendMessage(sender, "No user with the name " + additions[0]);
				return;
			}
			if (!IRCPermissions.getUser(additions[0]).getPermissions().contains(additions[1])) {
				bot.sendMessage(sender, "The user does not have that permission.");
				return;
			}

			IRCPermissions.removeUserPermission(additions[0], additions[1]);
			bot.sendMessage(sender, "removed premission node " + additions[1] + " for user " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Removes a permission to a user  : Syntax: " + prefix
					+ "REMOVE USER PERMISSION <USER> <PERMISSION_NODE>");
		}
	});

	private Command remove_group_permission = new Command(this, "REMOVE GROUP PERMISSION", remove, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			if (IRCPermissions.getGroup(additions[0]) == null) {
				bot.sendMessage(sender, "No group named " + additions[0]);
				return;
			}
			if (!IRCPermissions.getGroup(additions[0]).getPermissions().contains(additions[1])) {
				bot.sendMessage(sender, "The group does not have that permission.");
				return;
			}

			IRCPermissions.removeGroupPermission(additions[0], additions[1]);
			bot.sendMessage(sender, "Removed premission node " + additions[1] + " for group " + additions[0]);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Removes a permission to a group  : Syntax: " + prefix
					+ "REMOVE GROUP PERMISSION <GROUP> <PERMISSION_NODE>");
		}
	});

	private Command remove_group_user = new Command(this, "REMOVE GROUP USER", remove, new CommandAction() {

		@Override
		public void onCommand(String channel, String sender, String login, String hostname, String additional) {
			String[] additions = additional.split(" ");
			if (IRCPermissions.getGroup(additions[0]) == null) {
				bot.sendMessage(sender, "No group named " + additions[0]);
				return;
			}
			if (IRCPermissions.getUser(additions[1]) != null
					&& (IRCPermissions.getUser(additions[1]).getGroup() == null || IRCPermissions.getUser(additions[1])
							.getGroup().equals(IRCPermissions.getDefaultGroup()))) {
				bot.sendMessage(sender, "User is not in a group, or is in the default group already");
				return;
			}
			String group = IRCPermissions.getGroup(additions[0]).getName();
			IRCPermissions.setUserGroup(additions[1], IRCPermissions.getDefaultGroup());
			bot.sendMessage(sender, "Removed user " + additions[1] + " from group " + group);
		}

		@Override
		public void onHelp(String channel, String sender, String login, String hostname) {
			bot.sendMessage(sender, "Removes a user to a group  : Syntax: " + prefix
					+ "REMOVE GROUP USER <GROUP> <USER>");
		}
	});
}