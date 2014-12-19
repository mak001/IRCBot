package com.mak001.ircBot.plugins.permissions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.mak001.api.OrganizedMap;
import com.mak001.ircBot.settings.Settings;

public class IRCPermissions {

	private static OrganizedMap<String, User> users = new OrganizedMap<String, User>();
	private static OrganizedMap<String, Group> groups = new OrganizedMap<String, Group>();

	private static BufferedWriter writer;
	private static BufferedReader reader;
	public static final String tab = "\t";
	private static String defaultGroup = null;

	public static boolean hasPermission(String sender, String node) {
		if (node == null || node.equals("")) return true;
		return getUser(sender) != null && getUser(sender).getPermissions().contains(node);
	}

	public static Group getGroup(String name) {
		return groups.get(name);
	}

	public static OrganizedMap<String, Group> getGroups() {
		return groups;
	}

	public static User getUser(String name) {
		return users.get(name);
	}

	public static OrganizedMap<String, User> getUsers() {
		return users;
	}

	public static void setUpGroups() {
		try {
			writer = new BufferedWriter(new FileWriter(new File(Settings.userHome + Settings.fileSeparator + "Settings"
					+ Settings.fileSeparator + "groups.mak")));

			writer.write("#use the pound symbol (#) for comments" + Settings.lineSeparator);

			writer.write(":Normal" + Settings.lineSeparator);
			writer.write(tab + "-" + "main.add" + Settings.lineSeparator);

			writer.write("#use :end: to end the file" + Settings.lineSeparator);
			writer.write("#use a colon (:) to start a new group" + Settings.lineSeparator);
			writer.write(":Admin" + Settings.lineSeparator);
			writer.write("#use a single dash (-) to add a permission" + Settings.lineSeparator);
			writer.write(tab + "-" + "main.admin" + Settings.lineSeparator);
			writer.write("#use a double dash (--) to add a an exception" + Settings.lineSeparator);
			writer.write(tab + "--" + "main.add" + Settings.lineSeparator);
			writer.write("#use inheritance:GROUP_NAME to add the the commands from another group"
					+ Settings.lineSeparator);
			writer.write(tab + "inheritance:" + "Normal" + Settings.lineSeparator);

			writer.write(":end:" + Settings.lineSeparator);

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setUpUsers() {
		try {
			writer = new BufferedWriter(new FileWriter(new File(Settings.userHome + Settings.fileSeparator + "Settings"
					+ Settings.fileSeparator + "users.mak")));

			writer.write("#use the pound symbol (#) for comments" + Settings.lineSeparator);
			writer.write("#use a colon (:) to start a new user" + Settings.lineSeparator);
			writer.write(":mak001" + Settings.lineSeparator);
			writer.write("#use a single dash (-) to add a permission" + Settings.lineSeparator);
			writer.write(tab + "-" + "main.admin" + Settings.lineSeparator);
			writer.write("#use a double dash (--) to add a an exception" + Settings.lineSeparator);
			writer.write(tab + "--" + "main.add" + Settings.lineSeparator);
			writer.write("#use group:GROUP_NAME to add the user to a group (can only be in one)"
					+ Settings.lineSeparator);
			writer.write(tab + "group:" + "Admin" + Settings.lineSeparator);

			writer.write("#use :end: to end the file" + Settings.lineSeparator);
			writer.write(":end:" + Settings.lineSeparator);

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		System.out.println("Loading....");
		File userData = new File(Settings.userHome + Settings.fileSeparator + "Settings" + Settings.fileSeparator
				+ "users.mak");
		File groupData = new File(Settings.userHome + Settings.fileSeparator + "Settings" + Settings.fileSeparator
				+ "groups.mak");
		loadGroups(groupData);
		loadUsers(userData);
	}

	public static void save() {
		File userData = new File(Settings.userHome + Settings.fileSeparator + "Settings" + Settings.fileSeparator
				+ "users.mak");
		File groupData = new File(Settings.userHome + Settings.fileSeparator + "Settings" + Settings.fileSeparator
				+ "groups.mak");
		String n = Settings.lineSeparator;

		try {
			writer = new BufferedWriter(new FileWriter(userData));
			for (User u : users.values()) {
				writer.write(":" + u.getName() + n);
				for (String ex : u.getExceptions()) {
					writer.write(tab + "--" + ex + n);
				}
				for (String perm : u.getPermissionsWithoutExceptions()) {
					writer.write(tab + "-" + perm + n);
				}
				writer.write(tab + "group:" + u.getGroup() + n);
			}
			writer.write(":end:");

			writer.flush();
			writer.close();
			writer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			writer = new BufferedWriter(new FileWriter(groupData));
			for (int i = 0; i < groups.values().size(); i++) {
				Group g = groups.getAt(i);
				writer.write(":" + g.getName() + n);
				for (String ex : g.getExceptions()) {
					writer.write(tab + "--" + ex + n);
				}
				for (String perm : g.getOriginalPermissions()) {
					writer.write(tab + "-" + perm + n);
				}
				if (g.isDefault()) {
					writer.write(tab + "inheritance:default" + n);
				} else {
					writer.write(tab + "inheritance:" + g.getInheritance().getName() + n);
				}
			}
			writer.write(":end:");

			writer.flush();
			writer.close();
			writer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadGroups(File file) {
		System.out.println("Reading group file.");
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line; // line of reader

			String name = null;
			String inheritance = null;
			ArrayList<String> permissions = new ArrayList<String>();
			ArrayList<String> exceptions = new ArrayList<String>();

			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				if (!line.replace(tab, "").startsWith("#")) { // comment line

					if (line.startsWith(":")) { // new user/end
						if (name != null) {
							groups.put(name, new Group(name, inheritance, permissions, exceptions));
						}

						if (!line.contains(":end:")) {
							name = null;
							inheritance = null;
							permissions = new ArrayList<String>();
							exceptions = new ArrayList<String>();
							name = line.replace(":", "");
						}

					} else if (line.replace(tab, "").startsWith("--")) { // exception
						exceptions.add(line.replace(tab, "").replace("--", "").replace(" ", ""));
					} else if (line.replace(tab, "").startsWith("-")) { // permission
						permissions.add(line.replace(tab, "").replace("-", "").replace(" ", ""));
					} else if (line.replace(tab, "").startsWith("inheritance:")) { // inheritance
						String inher = line.replace(tab, "").replace("inheritance:", "").replace(" ", "");
						if (!inher.equalsIgnoreCase("default")) {
							inheritance = inher;
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadUsers(File file) {
		System.out.println("Reading user file.");
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line; // line of reader

			String name = null;
			String group = null;
			ArrayList<String> permissions = new ArrayList<String>();
			ArrayList<String> exceptions = new ArrayList<String>();

			while ((line = reader.readLine()) != null) {

				if (line.isEmpty()) continue;
				if (!line.replace(tab, "").startsWith("#")) { // comment line

					if (line.startsWith(":")) { // new user
						if (name != null) {
							users.put(name, new User(name, group, permissions, exceptions));
						}
						if (!line.contains(":end:")) {
							name = "a";
							group = null;
							permissions = new ArrayList<String>();
							exceptions = new ArrayList<String>();
							name = line.replace(":", "");
						}
					} else if (line.replace(tab, "").startsWith("--")) { // exception
						exceptions.add(line.replace(tab, "").replace("--", "").replace(" ", ""));
					} else if (line.replace(tab, "").startsWith("-")) { // permission
						permissions.add(line.replace(tab, "").replace("-", "").replace(" ", ""));
					} else if (line.replace(tab, "").startsWith("group:")) { // group
						group = line.replace(tab, "").replace("group:", "").replace(" ", "");
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setUserGroup(String name, String group) {
		if (users.get(name) != null) {
			users.get(name).setGroup(group);
		} else {
			createUser(name).setGroup(group);
		}
		save();
	}

	public static void addUserPermission(String name, String node) {
		if (users.get(name) != null) {
			users.get(name).addPermission(node);
		} else {
			createUser(name).addPermission(node);
		}
		save();
	}

	public static void addGroupPermission(String name, String node) {
		groups.get(name).addPermission(node);
		save();
	}

	public static void removeUserPermission(String name, String node) {
		users.get(name).removePermission(node);
		save();
	}

	public static void removeGroupPermission(String name, String node) {
		groups.get(name).removePermission(node);
		save();
	}

	public static User createUser(String name) {
		User u = null;
		if (users.get(name) == null) {
			u = new User(name, defaultGroup, null, null);
			users.put(name, u);
			save();
		}
		return u;
	}

	public static String getDefaultGroup() {
		if (defaultGroup == null) defaultGroup = lookupDefaultGroup();
		return defaultGroup;
	}

	private static String lookupDefaultGroup() {
		String s = "";
		for (Group g : groups.values())
			if (g.isDefault()) s = g.getName();
		return s;
	}
}
