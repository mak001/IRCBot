package com.mak001.ircbot.gui.full;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mak001.ircbot.gui.CustomFrame;
import com.mak001.ircbot.gui.InputPanel;

public class GUI extends CustomFrame {

	private static final long serialVersionUID = -196642858509025009L;
	JEditorPane editorPane = new JEditorPane();
	Document doc;

	public GUI() {
		doc = editorPane.getDocument();
		doc.addDocumentListener(lineLimiter);
		setSize(700, 347);
		
		InputPanel panel = new InputPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(248, Short.MAX_VALUE)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
		);
		getContentPane().setLayout(groupLayout);

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
