package com.mak001.ircbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


public class CustomMenuBar extends JMenuBar {

	private static final long serialVersionUID = -4096784194114031437L;
	private final CustomFrame frame;

	public CustomMenuBar(CustomFrame frame) {
		this.frame = frame;

		// ---- FILE MENU ----//
		JMenu mnFile = new JMenu("File");
		add(mnFile);

		JMenuItem mntmTest = new JMenuItem("test");
		mnFile.add(mntmTest);

		// ---- EDIT MENU ----//
		JMenu mnEdit = new JMenu("Edit");
		add(mnEdit);

		JMenuItem mntmLineLimit = new JMenuItem("Line Limit");
		mntmLineLimit.addActionListener(lineLimitListener);
		mnEdit.add(mntmLineLimit);

	}

	private ActionListener lineLimitListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String input = JOptionPane.showInputDialog(frame, "Set a new Line Limit.");
			if (input != null && !input.equals("")) {
				try {
					int i = Integer.parseInt(input);
					frame.setLineLimit(i);
				} catch (Exception ex) {
					System.out.println("Not a valid input.");
				}
			} else {
				System.out.println("Not a valid input.");
			}
		}
	};
}
