package com.mak001.ircbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mak001.ircbot.gui.SetUp;

/**
 * The settings manager.
 * 
 * @author Mak001
 * 
 */
public class SettingsManager {

	public static String lineSeparator, fileSeparator, userHome, settingsFolder;

	private static String USER_NAME = "USER NAME";
	private static String USER_PASS = "USER PASS";
	private static String COMMAND_PREFIX = "COMMAND PREFIX";
	private static String NETWORK = "NETWORK";
	private static String CHANNELS = "CHANNELS";
	private static String CHANNEL_NAME = "NAME";
	private static String CHANNEL_PASS = "PASS";

	private String u_name;
	private String u_pass;
	private String prefix;
	private String network;
	private HashMap<String, String> channels = new HashMap<String, String>();

	private static String SETTINGS_FILE_STRING;
	private static File SETTINGS_FILE;

	public SettingsManager() {
		lineSeparator = System.getProperty("line.separator");
		fileSeparator = System.getProperty("file.separator");
		userHome = System.getProperty("user.home") + fileSeparator + "IRCBot";
		settingsFolder = userHome + fileSeparator + "Settings" + fileSeparator;
		SETTINGS_FILE_STRING = settingsFolder + "settings.json";
		SETTINGS_FILE = new File(SETTINGS_FILE_STRING);

		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the settings
	 * 
	 * @throws IOException
	 */
	private void load() throws IOException {
		if (SETTINGS_FILE.exists()) {
			JSONObject obj = new JSONObject(getFileText(SETTINGS_FILE));
			u_name = obj.getString(USER_NAME);
			u_pass = obj.getString(USER_PASS);
			prefix = obj.getString(COMMAND_PREFIX);

			network = obj.getString(NETWORK);

			JSONArray chans = obj.getJSONArray(CHANNELS);
			for (int i = 0; i < chans.length(); i++) {
				JSONObject chan = chans.getJSONObject(i);
				channels.put(chan.getString(CHANNEL_NAME), chan.has(CHANNEL_PASS) ? chan.getString(CHANNEL_PASS) : null);
			}
		} else {
			SETTINGS_FILE.createNewFile();
			genDefault();
		}
	}

	private String getFileText(File file) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String strLine = null;
		while ((strLine = input.readLine()) != null) {
			response.append(strLine);
		}
		input.close();
		return response.toString();
	}

	/**
	 * Generates the default permissions and saves.
	 * 
	 * @throws IOException
	 */
	public void genDefault() throws IOException {
		// TODO

		new Thread(new Runnable() {

			@Override
			public void run() {
				SetUp setUp = new SetUp();
				setUp.setVisible(true);
				while (setUp.isVisible()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				network = setUp.getJTextFields()[0].getText();
				channels.put(setUp.getJTextFields()[1].getText(), "");
				u_name = setUp.getJTextFields()[2].getText();
				u_pass = setUp.getJTextFields()[3].getText();
				prefix = setUp.getJTextFields()[4].getText();
				setUp.dispose();
			}
		}).run();
		save();
	}


	/**
	 * Saves all the users to a file
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		Bot bot = Boot.getBot();

		JSONObject obj = new JSONObject();
		obj.put(COMMAND_PREFIX, prefix);
		obj.put(USER_NAME, u_name);
		obj.put(USER_PASS, u_pass);
		obj.put(NETWORK, network);

		JSONArray chans = new JSONArray();
		if (bot == null || bot.getChannels() == null) {
			for (Entry<String, String> entry : channels.entrySet()) {
				JSONObject chan = new JSONObject();
				chan.put(CHANNEL_NAME, entry.getKey());
				chan.put(CHANNEL_PASS, entry.getValue());
				chans.put(chan);
			}
		} else {
			for (String c : bot.getChannels()) {
				// TODO - redo after bot stores channel objects
				JSONObject chan = new JSONObject();
				chan.put(CHANNEL_NAME, c);
				chan.put(CHANNEL_PASS, "");
				chans.put(chan);
			}
		}
		obj.put(CHANNELS, chans);

		FileWriter writer = new FileWriter(SETTINGS_FILE);
		writer.write(obj.toString(5));
		writer.close();
	}

	public String getServer() {
		return network;
	}

	public String getCommandPrefix() {
		return prefix;
	}

	public String getNick() {
		return u_name;
	}

	public String getNickPass() {
		return u_pass;
	}

	public HashMap<String, String> getChannels() {
		return channels;
	}

	public void addChannel(String chan) {
		channels.put(chan, "");
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeChannel(String chan) {
		channels.remove(chan);
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeNick(String nick) {
		u_name = nick;
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeCommandPrefix(String prefix) {
		this.prefix = prefix;
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
