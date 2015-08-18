package com.mak001.ircbot;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;


public class RootCommands extends Thread implements Runnable {

	private final String[] EXIT = new String[] { "EXIT", "STOP", "SHUTDOWN" };

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
			processCommandLineCommand(line);
		}
	}

	public void kill() {
		running = false;
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processCommandLineCommand(String command) {
		// TODO Auto-generated method stub
		if (equalsIgnoreCase(command, EXIT)) {
			Boot.getBot().shutDown("Bot Console");
		} else {
			System.out.println(command + " is not a valid console commmand.");
		}
	}

	private boolean equalsIgnoreCase(String base, String[] compare) {
		for (String s : compare) {
			if (base.equalsIgnoreCase(s)) return true;
		}
		return false;
	}
}
