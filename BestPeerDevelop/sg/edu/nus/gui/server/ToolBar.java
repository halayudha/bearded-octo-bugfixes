/*
 * @(#) ToolBar.java 1.0 2006-1-2
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.LanguageLoader;

/**
 * Implement the tool bar of the super peer's UI.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-2
 */

public final class ToolBar extends JToolBar implements ActionListener {

	// private member
	private static final long serialVersionUID = 9216101996763678645L;
	private ServerGUI servergui;

	/* define buttons */
	private JButton btnLogin;
	private JButton btnLogout;
	private JButton btnConfig;

	private JButton btnDBManager;
	private JButton btnDBConfigure;

	/* define command names */
	private final String[] commands = {
			LanguageLoader.getProperty("button.login"),
			LanguageLoader.getProperty("button.logout"),
			LanguageLoader.getProperty("button.conf"),
			LanguageLoader.getProperty("button.exit"),
			LanguageLoader.getProperty("button.DBManager"),
			LanguageLoader.getProperty("button.DBConfig") };

	/**
	 * Construct the tool bar.
	 * 
	 * @param gui the reference of the <code>ServerGUI</code>
	 */
	public ToolBar(ServerGUI gui) {
		this.servergui = gui;
		this.addButtons();
		this.setFloatable(false);
		this.setRollover(true);
	}

	/**
	 * Init buttons of the tool bar.
	 */
	private void addButtons() {
		/* add buttons bellow */
		btnLogin = makeButton(commands[0], commands[0], "Login", commands[0]);
		this.add(btnLogin);

		btnLogout = makeButton(commands[1], commands[1], "Logout", commands[1]);
		this.add(btnLogout);

		this.addSeparator();

		btnConfig = makeButton(commands[2], commands[2],
				"Configure Event Manager", commands[2]);
		this.add(btnConfig);

		this.addSeparator();

		this.btnDBConfigure = makeButton(commands[5], commands[5], "DBConfig",
				commands[5]);

		this.add(btnDBConfigure);

		this.btnDBManager = makeButton(commands[4], commands[4], "DBManager",
				commands[4]);
		btnDBManager.setEnabled(false);
		this.add(btnDBManager);

		this.addSeparator();

		JButton button = makeButton(commands[3], commands[3], "Exit",
				commands[3]);
		this.add(button);

	}

	/**
	 * Create a button with system specified parameters.
	 * 
	 * @param image the URL of the icon image
	 * @param cmd the command name of the button
	 * @param tip the tool tip text
	 * @param name the name to be displayed
	 * 
	 * @return the instance of a tool bar button
	 */
	private JButton makeButton(String image, String cmd, String tip, String name) {
		/* look for the image */
		String imageLoc = AbstractMainFrame.SRC_PATH + image + ".png";

		/* create and initialize the button */
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
		button.setToolTipText(tip);
		button.addActionListener(this);

		try { // image found
			button.setIcon(new ImageIcon(imageLoc, name));
		} catch (Exception e) { // no image found
		}

		return button;
	}

	/**
	 * Execute an action.
	 * 
	 * @param event the action event
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		/* if login */
		if (cmd.equals(commands[0])) {
			new LoginDialog(servergui);
		}
		/* if logout */
		else if (cmd.equals(commands[1])) {
			servergui.logout(true, true, true);
		}
		/* if configure */
		else if (cmd.equals(commands[2])) {
			new ConfigDialog(servergui);
		}
		/* if exit program */
		else if (cmd.equals(commands[3])) {
			servergui.processWhenWindowClosing();
			System.exit(0);
		}

	}

	/**
	 * Set tool bar items enable or disable.
	 * 
	 * @param flag the signal to determine if enable tool bar items
	 */
	public void setEnable(boolean flag) {
		btnLogin.setEnabled(!flag);
		btnConfig.setEnabled(!flag);
		btnLogout.setEnabled(flag);
		btnDBConfigure.setEnabled(flag);
	}

	public void setEnableDBManager(boolean flag) {
		btnDBManager.setEnabled(flag);
	}
}