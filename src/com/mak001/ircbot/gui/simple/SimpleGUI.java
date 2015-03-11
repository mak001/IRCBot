package com.mak001.ircbot.gui.simple;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class SimpleGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JEditorPane editorPane;

	public SimpleGUI() {
		setTitle("ircBot output");
		setSize(700, 272);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		editorPane = new JEditorPane("text/html", "");
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void updateEditorPane(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				Document doc = editorPane.getDocument();
				try {
					doc.insertString(doc.getLength(), text, null);
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				editorPane.setCaretPosition(doc.getLength() - 1);
			}
		});
	}
}
