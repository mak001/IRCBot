package com.mak001.ircbot;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;


public class RootCommands extends Thread implements Runnable {

	private final String[] EXIT = new String[] { "EXIT", "STOP" };

	private boolean running = true;
	private final Console console;
	private final BufferedReader br;

	public RootCommands() {
		System.out.println("Starting console input reader.");
		console = System.console();
		if (console == null) {
			System.out.println("console not available. Opting for BufferedReader");
			br = new BufferedReader(new InputStreamReader(System.in));
		} else {
			br = null;
		}
	}

	@Override
	public void run() {
		while (running) {
			String line;
			if (console == null) {
				try {
					line = br.readLine();
				} catch (IOException e) {
					line = "";
				}
			} else {
				line = console.readLine();
			}

			// TODO Auto-generated method stub
			if (equalsIgnoreCase(line, EXIT)) {
				Boot.getBot().shutDown("Bot Console");
			}
		}
	}

	public void kill() {
		running = false;
	}

	private boolean equalsIgnoreCase(String base, String[] compare) {
		for (String s : compare) {
			if (base.equalsIgnoreCase(s)) return true;
		}
		return false;
	}
}
