package com.mak001.ircbot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import com.mak001.ircbot.gui.full.GUI;
import com.mak001.ircbot.gui.simple.SimpleGUI;

public class Boot {

	public static String SERVER;
	public static String CHANNEL;

	private static Bot bot;
	private static GUI gui;
	private static SimpleGUI simpleGUI;
	private static RootCommands rc;

	private final static String NO_GUI = "nogui";
	private final static String SIMPLE_GUI = "simpleGUI";
	private final static String FULL_GUI = "fullGUI";

	public static void main(String[] argsList) throws Exception {
		SettingsManager.load();
		List<String> args = Arrays.asList(argsList);

		rc = new RootCommands();
		if (args.contains(NO_GUI)) {
			rc.start();
		} else if (args.contains(SIMPLE_GUI)) {
			System.out.println("loading simple GUI");
			simpleGUI = new SimpleGUI();
			simpleGUI.setVisible(true);
			redirectSystemStreams(SIMPLE_GUI);
		} else {
			System.out.println("loading GUI");
			gui = new GUI();
			gui.setVisible(true);
			redirectSystemStreams(FULL_GUI);
		}
		setUp();

		SERVER = SettingsManager.getServer();

		// Now start our bot up.
		bot = new Bot();

		// Enable debugging output.
		// TODO - bot.setVerbose(true);

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
		if (arg.equals(SIMPLE_GUI)) {
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

		} else if (arg.equals(FULL_GUI)) { // TODO

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
		rc.kill();
	}

	public static RootCommands getConsoleCommands() {
		return rc;
	}
}