package com.mak001.ircBot.plugins.defaults.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

import com.mak001.ircBot.Bot;
import com.mak001.ircBot.methods.StringMethods;
import com.mak001.ircBot.settings.Settings;

public class ReadFile {

	public static void addStrictFP(Bot bot, String sender, String url) {
		String s = "";
		String name = "";
		try {
			URL item = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					item.openStream()));

			String ins;
			boolean isReading = false;
			while ((ins = in.readLine()) != null) {
				if (!isReading) {
					if (ins.contains("<pre class=\"brush:")) {
						if (ins.contains("import")) {
							s = StringMethods.cutBeginning(ins, "\">");
						}
						isReading = true;
					}
				} else {
					if (ins.contains("</pre>")) {
						if (ins.contains("}")) {
							s = s + StringMethods.truncate(ins, "</");
						}
						isReading = false;
					} else {
						if (ins.contains("extends Handle")) {
							name = ins.replace("public class ", "")
									.replace(" extends Handle", "")
									.replace("{", "").replace(" ", "");
						}
						s = s + ins + Settings.lineSeparator;
					}
				}
			}
			s = s.replace("&quot;", "\"").replace("&lt;", "<")
					.replace("&gt;", ">");
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					Settings.userHome + Settings.fileSeparator + "Plugins"
							+ Settings.fileSeparator + name + ".java")));
			out.write(s);

			out.flush();
			out.close();
			in.close();
			MyCompiler.compile(bot, sender, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addPasteBin(Bot bot, String sender, String string) {
		String s = "";
		String name = "";
		try {
			URL item = new URL(string);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					item.openStream()));

			String ins;
			while ((ins = in.readLine()) != null) {
				if (ins.contains("extends Handle")) {
					name = ins.replace("public class ", "")
							.replace(" extends Handle", "").replace("{", "")
							.replace(" ", "");
				}
				s = s + ins + Settings.lineSeparator;
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(
					Settings.userHome + Settings.fileSeparator + "Plugins"
							+ Settings.fileSeparator + name + ".java")));
			out.write(s);

			out.flush();
			out.close();
			in.close();
			MyCompiler.compile(bot, sender, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
