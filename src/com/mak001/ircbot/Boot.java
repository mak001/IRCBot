package com.mak001.ircbot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import com.mak001.ircbot.gui.GUI;
import com.mak001.ircbot.gui.simple.SimpleGUI;

public class Boot {

	public static String SERVER;
	public static String CHANNEL;

	private static Bot bot;
	private static GUI gui;

	private static SimpleGUI simpleGUI;

	public static void main(String[] argsList) throws Exception {
		SettingsManager.load();
		List<String> args = Arrays.asList(argsList);

		if (!args.contains("-gui") && !args.contains("simpleGUI")) {
			System.out.println("loading GUI");
			gui = new GUI();
			gui.setVisible(true);
			redirectSystemStreams("-gui");
		} else if (args.contains("simpleGUI")) {
			System.out.println("loading simple GUI");
			simpleGUI = new SimpleGUI();
			simpleGUI.setVisible(true);
			redirectSystemStreams("simpleGUI");
		}
		setUp();

		SERVER = SettingsManager.getServer();

		// Now start our bot up.
		bot = new Bot();

		// Enable debugging output.
		bot.setVerbose(true);

		// Connect to the IRC server.
		System.out.println("Connecting to " + SERVER);
		bot.connect(SERVER);
	}


	private static void setUp() {
		/*
		 * Makes sure all settings are loaded/generated before saving data
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					bot.getPermissionHandler().save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	public static Bot getBot() {
		return bot;
	}

	private static void redirectSystemStreams(String arg) {
		if (arg.equals("simpleGUI")) {
			OutputStream out = new OutputStream() {

				@Override
				public void write(final int b) throws IOException {
					simpleGUI.updateEditorPane(String.valueOf((char) b));
				}

				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					simpleGUI.updateEditorPane(new String(b, off, len));
				}

				@Override
				public void write(byte[] b) throws IOException {
					write(b, 0, b.length);
				}
			};

			System.setOut(new PrintStream(out, true));
			System.setErr(new PrintStream(out, true));

		} else if (arg.equals("-gui")) { // TODO

			OutputStream out = new OutputStream() {

				@Override
				public void write(final int b) throws IOException {
					gui.updateEditorPane(String.valueOf((char) b));
				}

				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					gui.updateEditorPane(new String(b, off, len));
				}

				@Override
				public void write(byte[] b) throws IOException {
					write(b, 0, b.length);
				}
			};

			System.setOut(new PrintStream(out, true));
			System.setErr(new PrintStream(out, true));
		}
	}

	public static void dispose() {
		if (gui != null) {
			gui.dispose();
		}
		if (simpleGUI != null) {
			simpleGUI.dispose();
		}
	}
}