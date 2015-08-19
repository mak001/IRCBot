package com.mak001.ircbot.gui;

import javax.swing.JFrame;


public class CustomFrame extends JFrame {

	private static final long serialVersionUID = 8064366074338484349L;
	protected int LINE_LIMIT = 100;
	protected LimitLinesDocumentListener lineLimiter;

	public CustomFrame() {
		setTitle("ircBot");
		CustomMenuBar menu = new CustomMenuBar(this);
		setJMenuBar(menu);

		lineLimiter = new LimitLinesDocumentListener(LINE_LIMIT);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setLineLimit(int limit) {
		LINE_LIMIT = limit;
		lineLimiter.setLimitLines(limit);
	}

}
