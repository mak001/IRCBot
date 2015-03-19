package org.jibble.pircbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class Channel {


	private final String name;
	private HashMap<String, User> _users = new HashMap<String, User>();
	private ArrayList<Modes> modes = new ArrayList<Modes>();

	public Channel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public User getUser(String name) {
		return _users.get(name);
	}

	public boolean changeUserName(String oldNick, String newNick) {
		if (_users.containsKey(oldNick)) {
			return _users.put(newNick, _users.remove(oldNick)) != null;
		}
		return false;
	}

	public boolean removeUserByName(String nick) {
		return _users.remove(nick) != null;
	}

	public boolean removeUser(User user) {
		return removeUserByName(user.getNick());
	}


	public boolean addUserByName(String prefix, String nick) {
		return _users.put(nick, new User(prefix, nick)) != null;
	}

	public boolean addUser(User user) {
		return _users.put(user.getNick(), user) != null;
	}

	public Collection<User> getUsers() {
		return _users.values();
	}

	@Override
	public boolean equals(Object chan) {
		if (chan instanceof Channel) {
			if (this == chan) return true;
			if (this.name.equals(((Channel) chan).name)) return true;
		}
		return false;
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
