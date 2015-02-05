package org.jibble.pircbot;

import java.util.ArrayList;


public class Channel {


	private final String name;
	private ArrayList<Modes> modes = new ArrayList<Modes>();
	private ArrayList<User> users = new ArrayList<User>();

	public Channel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean removeUser(User user) {
		return users.remove(user);
	}


	public boolean addUser(User user) {
		return users.add(user);
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public ArrayList<Modes> getModes() {
		return modes;
	}

	public boolean hasMode(char mode) {
		return modes.contains(getMode(mode));
	}

	public boolean hasMode(Modes mode) {
		return modes.contains(mode);
	}

	public void addModes(String mode) {
		System.out.println(mode);
		char[] ms = mode.toCharArray();
		for (char m : ms) {
			if (m != '+') {
				modes.add(getMode(m));
			}
		}
	}

	public void removeModes(String mode) {
		System.out.println(mode);
		char[] ms = mode.toCharArray();
		for (char m : ms) {
			if (m != '-') {
				if (modes.contains(getMode(m))) {
					modes.remove(getMode(m));
				}
			}
		}
	}

	public Modes getMode(char c) {
		for (Modes m : Modes.values()) {
			if (c == m.getChar()) return m;
		}
		return null;
	}

	public enum Modes
	{
		NO_CONTROL_CODES('c'), NO_EXTERNAL_MESSAGES('n'), OPS_TOPIC('t'), SECRET('s'), PARANOIA('p'), MODERATED('m'), INVITE_ONLY(
				'i'), BANDWIDTH_SAVER('B'), NO_CTCPS('C'), MODREG('M'), NO_NOTICES('N'), REGISTERED_ONLY('R'), SSL_ONLY(
				'S'), PERSIST_ONLY('z'), OPER_ONLY('O');

		private final char _mode;

		private Modes(char c) {
			_mode = c;
		}

		public char getChar() {
			return _mode;
		}
	}
}
