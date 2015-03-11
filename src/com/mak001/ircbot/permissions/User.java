package com.mak001.ircbot.permissions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;


public class User {

	private final List<String> permissions;
	private final String name;

	public static String NAME = "NAME";
	public static String PERMISSIONS = "PERMISSIONS";

	protected User(String name) {
		this(name, new ArrayList<String>());
	}

	protected User(String name, List<String> permissions) {
		this.permissions = permissions;
		this.name = name;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public boolean addPermission(String permission) {
		if (permissions.contains(permission)) return false;
		return permissions.add(permission);
	}
	
	public boolean removePermission(String permission) {
		if (permissions.contains(permission)) return false;
		return permissions.remove(permission);
	}

	public boolean hasPermission(String permission) {
		if (permission == null || permission.equals("")) return true;
		return getPermissions().contains(permission);
	}

	public String getName() {
		return name;
	}

	public JSONObject getSaveObject() {
		JSONObject obj = new JSONObject();
		obj.put(NAME, name);
		obj.put(PERMISSIONS, permissions);
		return obj;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof User) if (((User) o).getName().equalsIgnoreCase(name)) return true;
		return false;
	}
}
