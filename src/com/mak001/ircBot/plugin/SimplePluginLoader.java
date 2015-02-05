package com.mak001.ircBot.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mak001.api.plugins.Plugin;
import com.mak001.ircBot.Boot;
import com.mak001.ircBot.Bot;


public class SimplePluginLoader {

	private final Bot bot;

	public SimplePluginLoader(Bot bot) {
		this.bot = bot;
	}

	public Plugin loadPlugin(String file_name) throws InvalidPluginException {
		return loadPlugin(new File(file_name));// TODO - ?
	}

	public Plugin loadPlugin(File file) throws InvalidPluginException {
		if (file != null && file.isFile() && file.toString().toLowerCase().endsWith(".jar")) {
			try {
				return addPluginClass(getPluginMainClass(file));
			} catch (Exception e) {
				throw new InvalidPluginException(e);
			}
		}
		throw new InvalidPluginException();
	}

	private boolean isPluginClass(Class<?> clazz) {
		return clazz.getSuperclass() == Plugin.class;
	}

	private Plugin addPluginClass(Class<?> clazz) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (isPluginClass(clazz)) {
			Constructor<?>[] cs = clazz.getConstructors();
			Object invoke = cs[0].newInstance(bot);
			Plugin h = (Plugin) (invoke);
			bot.add(h);
		}
		return null;
	}

	private Class<?> getPluginMainClass(File file) throws ClassNotFoundException, IOException {
		return getPluginMainClass(file.getCanonicalPath());
	}

	private Class<?> getPluginMainClass(String file_path) throws ClassNotFoundException, IOException {
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
				if (isPluginClass(clazz)) return clazz;
			}
		}
		return null;
	}
}
