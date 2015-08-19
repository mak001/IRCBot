package com.mak001.ircbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.mak001.ircbot.Boot;


public class InputPanel extends JPanel {

	private static final long serialVersionUID = 8187668193894919L;
	private JTextField textField;

	public InputPanel() {

		textField = new JTextField();
		textField.setColumns(10);
		textField.addKeyListener(enterKeyListener);

		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(sendAction);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnEnter).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addComponent(textField, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
										.addComponent(btnEnter, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)).addContainerGap()));
		setLayout(groupLayout);

	}

	private void performAction() {
		Boot.getConsoleCommands().processCommandLineCommand(textField.getText());
		textField.setText("");
	}

	private ActionListener sendAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			performAction();
		}
	};

	private KeyListener enterKeyListener = new KeyAdapter() {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && textField.isFocusOwner()) {
				performAction();
			}
		}
	};
}
