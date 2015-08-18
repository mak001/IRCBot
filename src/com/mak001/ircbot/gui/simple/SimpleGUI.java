package com.mak001.ircbot.gui.simple;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mak001.ircbot.Boot;
import com.mak001.ircbot.gui.LimitLinesDocumentListener;
import com.seaglasslookandfeel.SeaGlassLookAndFeel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimpleGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private final Document doc;
	private int LINE_LIMIT = 100; // TODO

	private JEditorPane editorPane;
	private JTextField textField;

	private ActionListener sendAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			Boot.getConsoleCommands().processCommandLineCommand(textField.getText());
			textField.setText("");
		}
	};

	private KeyListener enterKeyListener = new KeyAdapter() {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				Boot.getConsoleCommands().processCommandLineCommand(textField.getText());
				textField.setText("");
			}
		}
	};

	public SimpleGUI() {
		doc = editorPane.getDocument();
		doc.addDocumentListener(new LimitLinesDocumentListener(LINE_LIMIT));
		try {
			UIManager.setLookAndFeel(new SeaGlassLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("ircBot output");
		setSize(700, 272);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		editorPane = new JEditorPane("text/html", "");
		editorPane.setEditable(false);
		scrollPane.setViewportView(editorPane);

		textField = new JTextField();
		textField.addKeyListener(enterKeyListener);
		textField.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(sendAction);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING, false)
										.addGroup(
												Alignment.TRAILING,
												groupLayout.createSequentialGroup().addComponent(textField)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnSend))
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 684,
												GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				groupLayout
						.createSequentialGroup()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE).addComponent(btnSend))));
		getContentPane().setLayout(groupLayout);

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
