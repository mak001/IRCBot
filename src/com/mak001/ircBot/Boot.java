package com.mak001.ircBot;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mak001.ircBot.gui.GUI;
import com.mak001.ircBot.gui.simple.SimpleGUI;
import com.mak001.ircBot.plugins.Plugin;
import com.mak001.ircBot.plugins.defaults.permissions.IRCPermissions;
import com.mak001.ircBot.settings.Settings;
import com.mak001.ircBot.settings.SettingsWriter;

public class Boot {

	public static String SERVER;
	public static String CHANNEL;

	private static Class<?> c;
	private static ClassLoader loader;
	private static Bot bot;
	private static GUI gui;

	private static SimpleGUI simpleGUI;

	public static void main(String[] argsList) throws Exception {
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

		SERVER = Settings.get(Settings.SERVER);

		// Now start our bot up.
		bot = new Bot();

		loadPlugins();

		// Enable debugging output.
		bot.setVerbose(true);

		// Connect to the IRC server.
		System.out.println("Connecting to " + SERVER);
		bot.connect(SERVER);
	}

	private static void loadPlugins() {
		File dir = new File(Settings.userHome + Settings.fileSeparator + "Plugins" + Settings.fileSeparator + "bin");
		for (File f : dir.listFiles()) {
			String trueName = f.getName().substring(0, f.getName().length() - 6);
			if (f != null && !f.isDirectory() && !f.getName().contains("$") && !bot.getPlugins().contains(trueName)) {
				if (f.getName().endsWith("class")) {
					loadPluginClass(f.getName());
				} else if (f.getName().endsWith(".jar")) {
					loadPluginJar(f.getName());
				}
			}
		}
	}

	public static boolean loadPluginJar(String fileName) {
		String file_path = Settings.userHome + Settings.fileSeparator + "Plugins" + Settings.fileSeparator + "bin"
				+ Settings.fileSeparator + fileName;
		File f = new File(file_path);

		String trueName = fileName.substring(0, fileName.length() - 4);
		System.out.println("Adding jar: " + trueName);
		try {
			if (f != null && f.isFile() && f.toString().toLowerCase().endsWith(".jar")
					&& f.getName().contains(fileName)) {

				URL url = new URL("jar:file:///" + file_path + "!/");
				URLConnection connection = url.openConnection();
				JarFile file = ((JarURLConnection) connection).getJarFile();
				Enumeration<JarEntry> eje = file.entries();

				ClassLoader STAFLoader = Boot.class.getClassLoader();
				URLClassLoader URLLoader = new URLClassLoader(new URL[] { url }, STAFLoader);

				while (eje.hasMoreElements()) {
					JarEntry je = eje.nextElement();

					if (je.getName().endsWith(".class") && !je.getName().contains("$")) {

						String className = je.getName().replaceAll("/", "\\.");
						className = className.substring(0, className.length() - 6);
						Class<?> clazz = Class.forName(className, true, URLLoader);
						if (clazz.getSuperclass() == Plugin.class) {
							Constructor<?>[] cs = clazz.getConstructors();
							Object invoke = cs[0].newInstance(bot);
							Plugin h = (Plugin) (invoke);
							bot.add(h);
							System.out.println("Added: " + trueName);
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean loadPluginClass(String fileName) {
		File dir = new File(Settings.userHome + Settings.fileSeparator + "Plugins" + Settings.fileSeparator + "bin");
		File f = new File(Settings.userHome + Settings.fileSeparator + "Plugins" + Settings.fileSeparator + "bin"
				+ Settings.fileSeparator + fileName);

		String trueName = fileName.substring(0, fileName.length() - 6);
		System.out.println("Adding class: " + trueName);
		try {

			if (f != null && f.isFile() && f.toString().toLowerCase().endsWith(".class")
					&& f.getName().contains(fileName) && !f.getName().contains("$")) {
				loader = new URLClassLoader(new URL[] { dir.toURI().toURL() });
				c = loader.loadClass(trueName);

				Constructor<?>[] cs = c.getConstructors();
				Object invoke = cs[0].newInstance(bot);
				Plugin h = (Plugin) (invoke);
				bot.add(h);

				System.out.println("Added: " + trueName);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void setUp() {
		Settings.lineSeparator = System.getProperty("line.separator");
		Settings.fileSeparator = System.getProperty("file.separator");
		Settings.userHome = System.getProperty("user.home") + Settings.fileSeparator + "IRCBot";
		Settings.init();
		IRCPermissions.load();

		/*
		 * Makes sure all settings are loaded/generated before saving data
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				SettingsWriter.update();
				IRCPermissions.save();
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

	protected static void dispose() {
		if (gui != null) {
			gui.dispose();
		}
		if (simpleGUI != null) {
			simpleGUI.dispose();
		}
	}

}