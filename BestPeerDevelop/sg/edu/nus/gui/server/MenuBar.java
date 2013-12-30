/*
 * @(#) MenuBar.java 1.0 2005-12-30
 *
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import sg.edu.nus.gui.AboutDialog;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.FeedbackDialog;

/**
 * Implement the menu bar of the super peer GUI.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 */

public final class MenuBar extends JMenuBar implements ActionListener {

	// private members
	private static final long serialVersionUID = 6547238827081164724L;

	/* the handler of the main frame */
	private ServerGUI servergui;

	/* 
	 * the handler of the menu to be 
	 * controlled for enable or disable 
	 */
	private JMenu optionMenu;
	private JMenu actionMenu;
	private JMenu viewMenu;

	/* common munu item */
	private JMenuItem loginMenuItem;
	private JMenuItem logotMenuItem;
	private JMenuItem confgMenuItem;
	private JMenuItem regstMenuItem;

	/* define menu items here */
	private final String[] menuName = { "File", "Option", "Action", "View",
			"Help" };

	private final String[][] itemName = {
			{ "Sign In", "Sign Out", "Configure...", "Register...", "Exit" },
			{ "Always On Top" },
			{ "Create New User", "Synchronize Roles", "User-Role Assignment",
					"Stabilize", "Open Log File...", "Save Log File As...",
					"Clear All Events" },
			{ "Refresh Now", "Update Speed", "High", "Normal", "Low" },
			{ "Feedback & Error History", "Super Peer Help Topics",
					"About Super Peer" } };

	private KeyStroke[][] keyStroke = {
	// for file menu items
			{
					KeyStroke.getKeyStroke(KeyEvent.VK_F5,
							ActionEvent.CTRL_MASK),
					KeyStroke.getKeyStroke(KeyEvent.VK_F6,
							ActionEvent.CTRL_MASK),
					null,
					null,
					KeyStroke
							.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK) },
			// for option menu items
			{ null },
			// for action menu items
			{
					null,
					null,
					null,
					null,
					KeyStroke
							.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
					KeyStroke
							.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),
					KeyStroke.getKeyStroke(KeyEvent.VK_C,
							(ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)), },
			// for view menu items
			{ null, null, null, null, null },
			// for help menu items
			{
					KeyStroke.getKeyStroke(KeyEvent.VK_F1,
							ActionEvent.CTRL_MASK),
					null,
					KeyStroke.getKeyStroke(KeyEvent.VK_F2,
							ActionEvent.CTRL_MASK), } };

	private final String[][] commands = {
			{ "login", "logout", "config", "register", "exit" },
			{ "top" },
			{ "createUser", "roleSyn", "ura", "stabilize", "open", "save",
					"clear" },
			{ "refresh", "update", "high", "normal", "low" },
			{ "feedback", "help", "about" } };

	/**
	 * Init the menu bar and menu items.
	 */
	public MenuBar(ServerGUI gui) {
		this.servergui = gui;
		this.addMenus();
	}

	/**
	 * Make menus.
	 */
	private void addMenus() {
		JMenu menu = null;
		JMenu subMenu = null;
		JMenuItem menuItem = null;

		// add 'File' menu
		menu = this.makeMenu(menuName[0], 'F');

		loginMenuItem = this.makeMenuItem(itemName[0][0], commands[0][0],
				keyStroke[0][0]);
		menu.add(loginMenuItem);

		logotMenuItem = this.makeMenuItem(itemName[0][1], commands[0][1],
				keyStroke[0][1]);
		menu.add(logotMenuItem);
		menu.addSeparator();

		confgMenuItem = this.makeMenuItem(itemName[0][2], commands[0][2],
				keyStroke[0][2]);
		menu.add(confgMenuItem);
		menu.addSeparator();

		regstMenuItem = this.makeMenuItem(itemName[0][3], commands[0][3],
				keyStroke[0][3]);
		menu.add(regstMenuItem);
		menu.addSeparator();

		menuItem = this.makeMenuItem(itemName[0][4], commands[0][4],
				keyStroke[0][4]);
		menu.add(menuItem);

		this.add(menu);

		// add 'Option' menu
		optionMenu = this.makeMenu(menuName[1], 'O');

		menuItem = this.makeCheckBoxMenuItem(itemName[1][0], commands[1][0],
				false);
		optionMenu.add(menuItem);

		this.add(optionMenu);

		// add 'Action' menu
		actionMenu = this.makeMenu(menuName[2], 'A');

		menuItem = this.makeMenuItem(itemName[2][0], commands[2][0],
				keyStroke[2][0]);
		actionMenu.add(menuItem);
		menuItem = this.makeMenuItem(itemName[2][1], commands[2][1],
				keyStroke[2][1]);
		actionMenu.add(menuItem);
		menuItem = this.makeMenuItem(itemName[2][2], commands[2][2],
				keyStroke[2][2]);
		actionMenu.add(menuItem);

		actionMenu.addSeparator();

		menuItem = this.makeMenuItem(itemName[2][3], commands[2][3],
				keyStroke[2][3]);
		actionMenu.add(menuItem);
		actionMenu.addSeparator();

		menuItem = this.makeMenuItem(itemName[2][4], commands[2][4],
				keyStroke[2][4]);
		actionMenu.add(menuItem);

		menuItem = this.makeMenuItem(itemName[2][5], commands[2][5],
				keyStroke[2][5]);
		actionMenu.add(menuItem);
		actionMenu.addSeparator();

		menuItem = this.makeMenuItem(itemName[2][6], commands[2][6],
				keyStroke[2][6]);
		actionMenu.add(menuItem);

		this.add(actionMenu);

		// add 'Log' menu
		viewMenu = this.makeMenu(menuName[3], 'V');

		menuItem = this.makeMenuItem(itemName[3][0], commands[3][0],
				keyStroke[3][0]);
		viewMenu.add(menuItem);
		viewMenu.addSeparator();

		subMenu = this.makeMenu(itemName[3][1], 'S');

		ButtonGroup bg = new ButtonGroup();
		menuItem = this.makeRadioButtonMenuItem(itemName[3][2], commands[3][2],
				false);
		subMenu.add(menuItem);
		bg.add(menuItem);

		menuItem = this.makeRadioButtonMenuItem(itemName[3][3], commands[3][3],
				true);
		subMenu.add(menuItem);
		bg.add(menuItem);

		menuItem = this.makeRadioButtonMenuItem(itemName[3][4], commands[3][4],
				false);
		subMenu.add(menuItem);
		bg.add(menuItem);

		viewMenu.add(subMenu);

		this.add(viewMenu);

		// add 'Help' menu
		menu = this.makeMenu(menuName[4], 'H');

		menuItem = this.makeMenuItem(itemName[4][0], commands[4][0],
				keyStroke[4][0]);
		menu.add(menuItem);

		menuItem = this.makeMenuItem(itemName[4][1], commands[4][1],
				keyStroke[4][1]);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = this.makeMenuItem(itemName[4][2], commands[4][2],
				keyStroke[4][2]);
		menu.add(menuItem);

		this.add(menu);
	}

	/**
	 * Make individual menu.
	 * 
	 * @param name the menu name
	 * @param mnemonic the shortcut key
	 * @return the instance of <code>JMenu</code>
	 */
	private JMenu makeMenu(String name, char mnemonic) {
		JMenu menu = new JMenu(name);
		menu.setMnemonic(mnemonic);
		return menu;
	}

	/**
	 * Make individual menu item.
	 * 
	 * @param name the menu item name
	 * @param cmd the command string
	 * @param keyStroke the accelerator key
	 * @return the instance of <code>MenuItem</code>
	 */
	private JMenuItem makeMenuItem(String name, String cmd, KeyStroke keyStroke) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setActionCommand(cmd);
		menuItem.setAccelerator(keyStroke);
		menuItem.addActionListener(this);
		return menuItem;
	}

	/**
	 * Make individual menu item.
	 * 
	 * @param name the menu item name
	 * @param cmd the command string
	 * @param sel the selected state
	 * @return the instance of <code>MenuItem</code>
	 */
	private JCheckBoxMenuItem makeCheckBoxMenuItem(String name, String cmd,
			boolean sel) {
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name, sel);
		menuItem.setActionCommand(cmd);
		menuItem.addActionListener(this);
		return menuItem;
	}

	/**
	 * Make individual menu item.
	 * 
	 * @param name the menu item name
	 * @param cmd the command string
	 * @param sel the selected state
	 * @return the instance of <code>MenuItem</code>
	 */
	private JRadioButtonMenuItem makeRadioButtonMenuItem(String name,
			String cmd, boolean sel) {
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name, sel);
		menuItem.setActionCommand(cmd);
		menuItem.addActionListener(this);
		return menuItem;
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		/* if sign in */
		if (cmd.equals(commands[0][0])) {
			new LoginDialog(servergui);
		}
		/* if sign out */
		else if (cmd.equals(commands[0][1])) {
			servergui.logout(true, true, true);
		}
		/* if config server */
		else if (cmd.equals(commands[0][2])) {
			new ConfigDialog(servergui);
		}
		/* if register a new user */
		else if (cmd.equals(commands[0][3])) {
			new RegisterDialog(servergui);
		}
		/* if exit system */
		else if (cmd.equals(commands[0][4])) {
			servergui.processWhenWindowClosing();
			System.exit(0);
		}
		/* if set alway on top */
		else if (cmd.equals(commands[1][0])) {
			if (servergui.isAlwaysOnTop()) {
				servergui.setAlwaysOnTop(false);
				return;
			}
			servergui.setAlwaysOnTop(true);
		}

		// VHTam: add following code to the next //end VHTam
		/* if createUser */
		else if (cmd.equals(commands[2][0])) {
			// JOptionPane.showMessageDialog(servergui,
			// "Menu item: "+commands[2][0]);

		}
		/* if Role Synchronize */
		else if (cmd.equals(commands[2][1])) {
			// JOptionPane.showMessageDialog(servergui,
			// "Menu item: "+commands[2][1]);
			servergui.peer().performRoleSynchronize();
		}
		/* if User role assignment */
		else if (cmd.equals(commands[2][2])) {
			JOptionPane.showMessageDialog(servergui, "Menu item: "
					+ commands[2][2]);
		}
		// end VHTam

		/* if stabilize */
		else if (cmd.equals(commands[2][3])) {
			JOptionPane.showMessageDialog(servergui, "Menu item: "
					+ commands[2][3]);
		}
		/* if open log file */
		else if (cmd.equals(commands[2][4])) {
			JOptionPane.showMessageDialog(servergui, "Menu item: "
					+ commands[2][4]);
		}
		/* if save log file */
		else if (cmd.equals(commands[2][5])) {
			JOptionPane.showMessageDialog(servergui, "Menu item: "
					+ commands[2][5]);
		}
		/* if clear log events */
		else if (cmd.equals(commands[2][6])) {
			JOptionPane.showMessageDialog(servergui, "Menu item: "
					+ commands[2][6]);
		}

		/* if refresh online peers now */
		else if (cmd.equals(commands[3][0])) {
			servergui.scheduleUDPSender(AbstractMainFrame.NORM_FREQ);
		}
		/* if refresh in high speed */
		else if (cmd.equals(commands[3][2])) {
			servergui.scheduleUDPSender(AbstractMainFrame.HIGH_FREQ);
		}
		/* if refresh in normal speed */
		else if (cmd.equals(commands[3][3])) {
			servergui.scheduleUDPSender(AbstractMainFrame.NORM_FREQ);
		}
		/* if refresh in low speed */
		else if (cmd.equals(commands[3][4])) {
			servergui.scheduleUDPSender(AbstractMainFrame.LOW_FREQ);
		}
		/* if send feedback */
		else if (cmd.equals(commands[4][0])) {
			new FeedbackDialog(servergui, "Feedback & Error History", true,
					500, 400);
		}
		/* if open help topic */
		else if (cmd.equals(commands[4][1])) {

		}
		/* if open about dialog */
		else if (cmd.equals(commands[4][2])) {
			new AboutDialog(servergui, "About Super Peer",
					AbstractMainFrame.SRC_PATH + "logo.jpg",
					"Super Peer\r\n\r\nVersion 1.0\r\n"
							+ "Copyright National University of Singapore.\r\n"
							+ "All rights reserved.\r\nhttp://www.nus.edu.sg");
		}
	}

	/**
	 * Set menu items enable or disable.
	 * 
	 * @param flag the signal to determine if enable menu items
	 */
	public void setEnable(boolean flag) {
		optionMenu.setEnabled(flag);
		actionMenu.setEnabled(flag);
		viewMenu.setEnabled(flag);
		logotMenuItem.setEnabled(flag);

		loginMenuItem.setEnabled(!flag);
		regstMenuItem.setEnabled(!flag);
		confgMenuItem.setEnabled(!flag);
	}

}