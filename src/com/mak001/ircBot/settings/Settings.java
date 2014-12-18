package com.mak001.ircBot.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {


	public static String lineSeparator, fileSeparator, userHome;

	public static final String SERVER = "SERVER", NICK = "NICK",
			NICK_PASS = "NICK_PASS", COMMAND_PREFIX = "COMMAND_PREFIX";

	protected static final String CHANNELS = "CHANNELS";

	private static ArrayList<String> channels = new ArrayList<String>();
	private static HashMap<String, String> settings = new HashMap<String, String>();

	public static void init() {
		SettingsWriter.init();
		load();
	}

	private static void load() {
		try {
			File file = new File(Settings.userHome + Settings.fileSeparator
					+ "Settings" + Settings.fileSeparator + "Settings.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;

				String[] info = line.split("=");
				if (!info[0].equals(CHANNELS)) {
					settings.put(info[0], info[1]);
				} else {
					String[] chans = info[1].split(", ");
					for (String chan : chans) {
						channels.add(chan);
					}
				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(final String KEY) {
		return settings.get(KEY);
	}

	public static void put(final String KEY, final String VALUE) {
		put(KEY, VALUE, false);
	}

	public static void put(final String KEY, final String VALUE, boolean update) {
		settings.put(KEY, VALUE);
		if (update) SettingsWriter.update();
	}

	public static HashMap<String, String> getSettings() {
		return settings;
	}

	public static ArrayList<String> getChannels() {
		return channels;
	}
}
