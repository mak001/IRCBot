package com.mak001.ircbot.gui.full;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mak001.ircbot.gui.LimitLinesDocumentListener;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	JEditorPane editorPane = new JEditorPane();
	private final int LINE_LIMIT = 100;
	Document doc;

	public GUI() {
		doc = editorPane.getDocument();
		doc.addDocumentListener(new LimitLinesDocumentListener(LINE_LIMIT));
		setTitle("ircBot output");
		setSize(700, 347);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

	}

	public void updateEditorPane(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
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
