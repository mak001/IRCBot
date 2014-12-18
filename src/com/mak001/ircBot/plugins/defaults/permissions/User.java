package com.mak001.ircBot.plugins.defaults.permissions;

import java.util.ArrayList;

public class User {

	private final String name;
	private ArrayList<String> permissions;
	private ArrayList<String> exceptions;
	private ArrayList<String> allPermissions;
	private String group;

	public User(String name, String group, ArrayList<String> permissions,
			ArrayList<String> exceptions) {
		if (permissions == null)
			permissions = new ArrayList<String>();
		if (exceptions == null)
			exceptions = new ArrayList<String>();
		if (group == null || group.isEmpty()) {
			group = IRCPermissions.getDefaultGroup();
		}
		this.name = name;
		this.permissions = permissions;
		this.group = group;
		this.exceptions = exceptions;

		reDoNodes();
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getPermissions() {
		return allPermissions;
	}

	public ArrayList<String> getPermissionsWithoutExceptions() {
		return permissions;
	}

	public ArrayList<String> getExceptions() {
		return exceptions;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void addPermission(String permission) {
		permissions.add(permission);
	}

	public void removePermission(String permission) {
		if (IRCPermissions.getGroup(group).getPermissions()
				.contains(permission))
			exceptions.add(permission);
		permissions.remove(permission);
		reDoNodes();
	}

	public void reDoNodes() {
		if (allPermissions != null)
			allPermissions.clear();
		ArrayList<String> als = new ArrayList<String>();
		for (String per : permissions) {
			if (!exceptions.contains(per)) {
				als.add(per);
			}
		}
		for (String per : IRCPermissions.getGroup(group).getPermissions()) {
			if (!exceptions.contains(per)) {
				als.add(per);
			}
		}
		allPermissions = als;
	}

	public String toString() {
		return "User: " + name + "---Permissions: " + permissions.toString()
				+ "---Group: " + group + "---Exceptions: "
				+ exceptions.toString();
	}
}
