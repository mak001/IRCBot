package com.mak001.ircbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JEditorPane;

import org.jibble.pircbot.Channel;

import com.mak001.ircbot.Boot;


public class ChannelPanel extends JPanel {


	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JEditorPane editorPane;
	private JList<String> list;

	private Channel channel;

	public ChannelPanel() {

		textField = new JTextField();
		textField.setColumns(10);
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
			}
		});

		JScrollPane scrollPane = new JScrollPane();

		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.TRAILING)
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 502,
																Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 143,
																GroupLayout.PREFERRED_SIZE))
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addComponent(textField, GroupLayout.DEFAULT_SIZE, 567,
																Short.MAX_VALUE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 78,
																GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
										.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnSend)
										.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)).addContainerGap()));

		editorPane = new JEditorPane();
		scrollPane_1.setViewportView(editorPane);

		list = new JList<String>();
		scrollPane.setViewportView(list);
		setLayout(groupLayout);
	}

	public void setChannel(Channel chan) {
		channel = chan;
	}

	public Channel getChannel() {
		return channel;
	}

	public JEditorPane getMessagePane() {
		return editorPane;
	}

	public JList<String> getUserList() {
		return list;
	}

	private void sendMessage() {
		if (!textField.getText().equals("")) Boot.getBot().sendMessage(channel.getName(), textField.getText());
		textField.setText("");
	}

}
