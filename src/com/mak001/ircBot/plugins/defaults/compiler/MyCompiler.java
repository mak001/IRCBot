package com.mak001.ircBot.plugins.defaults.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.mak001.ircBot.Boot;
import com.mak001.ircBot.Bot;
import com.mak001.ircBot.settings.Settings;

public class MyCompiler {

	public static void compile(Bot bot, String sender, String fileName) {
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

			List<String> optionList = new ArrayList<String>();

			optionList.addAll(Arrays.asList("-classpath",
					System.getProperty("java.class.path")));
			optionList.addAll(Arrays.asList("-d", Settings.userHome
					+ Settings.fileSeparator + "Plugins"
					+ Settings.fileSeparator + "bin"));

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

			Iterable<? extends JavaFileObject> compilationUnits = Arrays
					.asList(getJavaFileObject(fileName));

			bot.sendMessage(Boot.CHANNEL, "Attempting to compile and add: "
					+ fileName);

			if (compiler.getTask(null, null, diagnostics, optionList, null,
					compilationUnits).call()) {

				bot.sendMessage(Boot.CHANNEL, "Compiled and added: " + fileName);
			} else {
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
						.getDiagnostics()) {
					String message = String.format("Error on line %d in %s%n",
							diagnostic.getLineNumber(), diagnostic.getSource());
					bot.sendMessage(sender, message);
				}
				bot.sendMessage(Boot.CHANNEL, "Failed to compile and added: "
						+ fileName);
			}
		} catch (Exception e) {
			bot.sendMessage(Boot.CHANNEL, "Failed to compile and add: "
					+ fileName);
			bot.sendMessage(sender, e.getMessage());
			for (int i = 0; i < 6; i++) {
				bot.sendMessage(sender, "" + e.getStackTrace()[i]);
			}
		}
		Boot.loadPluginClass(fileName);
	}

	private static String readFile(String fileName) {
		String c = "";
		try {
			URL item = new File(Settings.userHome + Settings.fileSeparator
					+ "Plugins" + Settings.fileSeparator + fileName + ".java")
					.toURI().toURL();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					item.openStream()));

			String ins;
			while ((ins = in.readLine()) != null) {
				c = c + ins + Settings.lineSeparator;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	private static JavaFileObject getJavaFileObject(String fileName) {
		JavaFileObject so = null;
		try {
			so = new JavaSourceFromString(fileName, readFile(fileName));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return so;
	}

	private static class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}
}