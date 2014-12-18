package com.mak001.ircBot;

import java.util.ArrayList;


public class Channel {

	private final String name;
	private ArrayList<Character> modes = new ArrayList<Character>();

	public Channel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Character> getModes() {
		return modes;
	}

	public boolean hasMode(char mode) {
		return modes.contains(mode);
	}

	public void addModes(String mode) {
		System.out.println(mode);
		char[] ms = mode.toCharArray();
		for (char m : ms) {
			if (m != '+') {
				modes.add(m);
			}
		}
		printmodes();
	}

	public void removeModes(String mode) {
		System.out.println(mode);
		char[] ms = mode.toCharArray();
		for (char m : ms) {
			if (m != '-') {
				Character c = new Character(m);
				if (modes.contains(c)) {
					modes.remove(c);
				}
			}
		}
		printmodes();
	}

	private void printmodes() {
		String s = "Channel modes: +";
		for (Character c : modes) {
			s = s + c;
		}
		System.out.println(s);
		System.out.println("has colorcode: " + hasMode('c'));
	}
}
