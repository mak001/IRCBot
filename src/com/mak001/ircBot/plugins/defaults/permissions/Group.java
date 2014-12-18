package com.mak001.ircBot.plugins.defaults.permissions;

import java.util.ArrayList;

public class Group {

	private final String name;
	private final ArrayList<String> permissions;
	private final ArrayList<String> exceptions;
	private final Group inheritance;
	private ArrayList<String> allPermissions;

	public Group(String name, String inheritance,
			ArrayList<String> permissions, ArrayList<String> exceptions) {
		this.name = name;
		this.permissions = permissions;
		this.exceptions = exceptions;
		this.inheritance = IRCPermissions.getGroup(inheritance);
		reDoNodes();
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getPermissions() {
		return allPermissions;
	}

	public ArrayList<String> getOriginalPermissions() {
		return permissions;
	}

	public ArrayList<String> getExceptions() {
		return exceptions;
	}

	public Group getInheritance() {
		return inheritance;
	}

	public boolean isDefault() {
		return getInheritance() == null;
	}

	@Override
	public String toString() {
		if (inheritance != null)
			return name + " --- inheritance: " + inheritance.getName() + "---"
					+ name + " permissions: " + permissions.toString() + "---"
					+ name + " exceptions: " + exceptions.toString();
		return name + " --- inheritance: default---" + name + " permissions: "
				+ permissions.toString() + "---" + name + " exceptions: "
				+ exceptions.toString();
	}

	public void addPermission(String permission) {
		if (!permissions.contains(permission)) {
			permissions.add(permission);
			reDoNodes();
			for (User u : IRCPermissions.getUsers().values()) {
				if (u.getGroup().equals(name))
					u.reDoNodes();
			}
		}
	}

	public void removePermission(String permission) {
		if (getInheritancePermissions().contains(permission))
			exceptions.add(permission);
		if (permissions.contains(permission))
			permissions.remove(permission);
		reDoNodes();
		for (User u : IRCPermissions.getUsers().values()) {
			if (u.getGroup().equals(name))
				u.reDoNodes();
		}
	}

	private void reDoNodes() {
		ArrayList<String> temp = new ArrayList<String>();
		for (String per : permissions) {
			if (!exceptions.contains(per))
				if (!temp.contains(per))
					temp.add(per);
		}

		if (inheritance != null) {
			for (String perm : inheritance.getPermissions()) {
				if (!exceptions.contains(perm))
					if (!temp.contains(perm))
						temp.add(perm);
			}
		}
		allPermissions = temp;
	}

	public ArrayList<String> getInheritancePermissions() {
		if (inheritance != null) {
			return inheritance.getPermissions();
		}
		return new ArrayList<String>();
	}

}
