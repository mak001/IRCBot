package com.mak001.ircBot.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mak001.ircBot.gui.SetUp;


public class SettingsWriter {

	private static File file;
	private static BufferedWriter writer;

	public static boolean init() {
		if (!(file = new File(Settings.userHome)).exists()) {
			file.mkdirs();
		}

		if (!(file = new File(Settings.userHome + Settings.fileSeparator + "Settings")).exists()) {
			file.mkdirs();
		}

		if (!(file = new File(Settings.userHome + Settings.fileSeparator + "Settings" + Settings.fileSeparator
				+ "Settings.txt")).exists()) {

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
					Settings.put(Settings.SERVER, setUp.getJTextFields()[0].getText());

					Settings.getChannels().add(setUp.getJTextFields()[1].getText());
					Settings.put(Settings.NICK, setUp.getJTextFields()[2].getText());
					Settings.put(Settings.NICK_PASS, setUp.getJTextFields()[3].getText());
					Settings.put(Settings.COMMAND_PREFIX, setUp.getJTextFields()[4].getText());
					setUp.dispose();
				}
			}).run();

			update();
		}
		if (!(file = new File(Settings.userHome + Settings.fileSeparator + "Plugins")).exists()) {
			file.mkdirs();
		}

		if (!(file = new File(Settings.userHome + Settings.fileSeparator + "Resources")).exists()) {
			file.mkdirs();
		}

		if (!(file = new File(Settings.userHome + Settings.fileSeparator + "run.bat")).exists()) {
			writeRun();
		}

		if (!(file = new File(Settings.userHome + Settings.fileSeparator + "Resources" + Settings.fileSeparator
				+ "FindJDK.bat")).exists()) {
			writeJDK();
		}

		return true;
	}

	private static void writeRun() {
		try {
			String n = Settings.lineSeparator;
			writer = new BufferedWriter(
					new FileWriter(new File(Settings.userHome + Settings.fileSeparator + "run.bat")));

			writer.write("@echo off" + n);
			writer.write("title IRCBot" + n);

			writer.write("SET cc=java" + n);
			writer.write("SET res=resources");

			writer.write("CALL \"%res%\\FindJDK.bat\"" + n);

			writer.write("java -cp IRCBot.jar com.mak001.ircBot.Boot -gui" + n);

			writer.write(":end" + n);
			writer.write("PAUSE" + n);
			writer.write("EXIT" + n);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeJDK() {
		try {
			String n = Settings.lineSeparator;
			writer = new BufferedWriter(new FileWriter(new File(Settings.userHome + Settings.fileSeparator
					+ "Resources" + Settings.fileSeparator + "FindJDK.bat")));
			writer.write("@ECHO OFF" + n);

			writer.write("ECHO Looking for JDK" + n);

			writer.write("SET KEY_NAME=HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit" + n);
			writer.write("FOR /F \"tokens=3\" %%A IN ('REG QUERY \"%KEY_NAME%\" /v CurrentVersion 2^>NUL') DO SET jdkv=%%A"
					+ n);
			writer.write("SET jdk=" + n);

			writer.write("IF DEFINED jdkv (" + n);
			writer.write("FOR /F \"skip=2 tokens=3,4\" %%A IN ('REG QUERY \"%KEY_NAME%\\%jdkv%\" /v JavaHome 2^>NUL') DO SET jdk=%%A %%B"
					+ n);
			writer.write(") ELSE (" + n);
			writer.write("	FOR /F \"tokens=*\" %%G IN ('DIR /B \"%ProgramFiles%\\Java\\jdk*\"') DO SET jdk=%%G" + n);
			writer.write(")" + n);

			writer.write("SET jdk=%jdk%\\bin" + n);
			writer.write("SET javac=\"%jdk%\\javac.exe\"" + n);

			writer.write("IF NOT EXIST %javac% (" + n);
			writer.write("javac -version 2>NUL" + n);
			writer.write("IF \"%ERRORLEVEL%\" NEQ \"0\" GOTO :notfound" + n);
			writer.write(") ELSE (" + n);
			writer.write("	GOTO :setpath" + n);
			writer.write(")" + n);
			writer.write("GOTO :eof" + n);

			writer.write(":notfound" + n);
			writer.write("ECHO JDK is not installed, please download and install it from:" + n);
			writer.write("ECHO http://java.sun.com/javase/downloads" + n);
			writer.write("ECHO." + n);
			writer.write("PAUSE" + n);
			writer.write("EXIT" + n);

			writer.write(":setpath" + n);
			writer.write("SET PATH=%jdk%;%PATH%" + n);
			writer.write("GOTO :eof" + n);

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void update() {
		try {
			writer = new BufferedWriter(new FileWriter(new File(Settings.userHome + Settings.fileSeparator + "Settings"
					+ Settings.fileSeparator + "Settings.txt")));

			for (String key : Settings.getSettings().keySet()) {
				writer.write(key + "=" + Settings.get(key) + Settings.lineSeparator);
			}

			String chanOut = "";
			for (String chan : Settings.getChannels()) {
				chanOut = (chanOut.equals("") ? "" : chanOut + ", ") + chan;
			}
			writer.write(Settings.CHANNELS + "=" + chanOut + Settings.lineSeparator);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
