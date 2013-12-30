/*
 * @(#) ToolBar.java 1.0 2006-1-2
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import sg.edu.nus.accesscontrol.bootstrap.DlgRoleManagement;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.info.CacheDbIndex;

/**
 * Implement the tool bar of the bootstrap's UI.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-2
 */

public final class ToolBar extends JToolBar implements ActionListener {

	// private member
	private static final long serialVersionUID = 9216101996763678645L;
	private BootstrapGUI bootstrapGUI;

	/* define buttons */
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnConfig;
	private JButton btnRoleManage;
	private JButton btnLoadBalancing;
	private JButton btnReplication;

	
	/* define command names */
	private final String[] commands = {
			LanguageLoader.getProperty("gui.start"),
			LanguageLoader.getProperty("gui.stop"),
			LanguageLoader.getProperty("gui.conf"),
			LanguageLoader.getProperty("gui.exit"),
			LanguageLoader.getProperty("gui.roleManagement"),
			LanguageLoader.getProperty("gui.loadBalancing"),
			LanguageLoader.getProperty("gui.replication")   };

	/**
	 * Construct the tool bar.
	 * 
	 * @param gui the reference of the <code>BootstrapGUI</code>
	 */
	public ToolBar(BootstrapGUI gui) {
		this.bootstrapGUI = gui;
		this.addButtons();
		this.setFloatable(false);
		this.setRollover(true);
	}

	/**
	 * Init buttons of the tool bar.
	 */
	private void addButtons() {
		/* add buttons bellow */

		btnStart = makeButton("Start", commands[0], "Start Service",
				commands[0]);
		this.add(btnStart);

		btnStop = makeButton("Stop", commands[1], "Stop Service", commands[1]);
		this.add(btnStop);

		this.addSeparator();

		btnConfig = makeButton("Configure", commands[2],
				"Configure Bootstrap Server", commands[2]);
		this.add(btnConfig);

		// VHTam
		btnRoleManage = makeButton("role", commands[4],
				"Configure Role for Access control", commands[4]);
		this.add(btnRoleManage);
		// end VHTam

		this.addSeparator();
		
		btnLoadBalancing = makeButton("loadbalancing", commands[5],
				"Configure Load Balancing", commands[5]);
		this.add(btnLoadBalancing);

		// VHTam
		btnReplication = makeButton("replication", commands[6],
				"Configure Replication", commands[6]);
		this.add(btnReplication);
		
		this.addSeparator();

		JButton button = makeButton("Exit", commands[3], "Exit", commands[3]);
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

		/* if start server */
		if (cmd.equals(commands[0])) {
			bootstrapGUI.startServer();
		}
		/* if stop server */
		else if (cmd.equals(commands[1])) {
			bootstrapGUI.logout(false, true, false);
		}
		/* if configure */
		else if (cmd.equals(commands[2])) {

			String title = "Configure Bootstrap Server";
			DlgBootstrapConfig dlg = new DlgBootstrapConfig(bootstrapGUI,
					title, 350, 400, "" + Bootstrap.LOCAL_SERVER_PORT, ""
							+ Bootstrap.CAPACITY);

			dlg.setDbType(AbstractPeer.bestpeer_db.getDbType());
			dlg.setDbServer(AbstractPeer.bestpeer_db.getServerAddress());
			dlg.setDbPort(AbstractPeer.bestpeer_db.getPort());
			dlg.setDbName(AbstractPeer.bestpeer_db.getDbName());
			dlg.setUserName(AbstractPeer.bestpeer_db.getUser());
			dlg.setPassword(AbstractPeer.bestpeer_db.getPwd());

			dlg.setVisible(true);

			if (dlg.isOkPressed()) {
				Bootstrap.LOCAL_SERVER_PORT = dlg.getPort();
				Bootstrap.CAPACITY = dlg.getCapacity();

				AbstractPeer.bestpeer_db.update(dlg.getDbType(), dlg
						.getDbServer(), dlg.getDbPort(), dlg.getUserName(), dlg
						.getPassword(), dlg.getDbName());

				if (AbstractPeer.conn_bestpeerdb != null)
					try {
						AbstractPeer.conn_bestpeerdb.close();
					} catch (SQLException e) {
					}
				AbstractPeer.conn_bestpeerdb = AbstractPeer.bestpeer_db
						.createDbConnection();
			}

		}
		/* if exit */
		else if (cmd.equals(commands[3])) {
			bootstrapGUI.processWhenWindowClosing();
			System.exit(0);
			
		}

		/* if role management */
		// VHTam
		else if (cmd.equals(commands[4])) {
			DlgRoleManagement dlg = new DlgRoleManagement(null, LanguageLoader
					.getProperty("title.roleSetting"),
					Bootstrap.conn_bestpeerdb);

			dlg.setVisible(true);

		}
		else if (cmd.equals(commands[5])) {
			JOptionPane.showMessageDialog(null, "Configure Load Balancing");
		}
		else if (cmd.equals(commands[6])) {
			JOptionPane.showMessageDialog(null, "Configure Replication");
		}
	}

	/**
	 * Set tool bar items enable or disable.
	 * 
	 * @param flag the signal to determine if enable tool bar items
	 */
	public void setEnable(boolean flag) {
		btnStart.setEnabled(!flag);
		btnConfig.setEnabled(!flag);
		btnStop.setEnabled(flag);
	}

}