package com.mak001.ircbot.gui.simple;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mak001.ircbot.gui.CustomFrame;
import com.mak001.ircbot.gui.InputPanel;
import com.seaglasslookandfeel.SeaGlassLookAndFeel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SimpleGUI extends CustomFrame {

	private static final long serialVersionUID = 1L;
	private final Document doc;

	private JEditorPane editorPane;

	public SimpleGUI() {
		try {
			UIManager.setLookAndFeel(new SeaGlassLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setSize(700, 300);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		editorPane = new JEditorPane("text/html", "");
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);

		doc = editorPane.getDocument();
		doc.addDocumentListener(lineLimiter);

		InputPanel panel = new InputPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
					.addGap(0))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
		);
		getContentPane().setLayout(groupLayout);
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
