/*
 * @(#) RegisterDialog.java 1.0 2006-1-5
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.server;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import sg.edu.nus.gui.AbstractRegisterDialog;
import sg.edu.nus.peer.ServerPeer;

;

/**
 * Implement the register dialog which is used for allowing a user to 
 * register himself to the super peer network 
 * by specifying his identifier and password.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-5
 */

public final class RegisterDialog extends AbstractRegisterDialog {
	// private members
	private static final long serialVersionUID = -8430923395387086485L;
	private ServerPeer serverpeer;

	/**
	 * Construct the register dialog.
	 * 
	 * @param gui the handler of the main frame
	 */
	public RegisterDialog(ServerGUI gui) {
		super(gui, "Register User Account", true, 360, 280,
				ServerPeer.BOOTSTRAP_SERVER_LIST.split(":"), ""
						+ ServerPeer.BOOTSTRAP_SERVER_PORT);
		this.serverpeer = gui.peer();

		/* show dialog */
		this.setVisible(true);
	}

	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	protected boolean checkValue() {
		String str = tfPort.getText().trim();
		try {
			int port = Integer.parseInt(str);
			if (port == ServerPeer.RUN_PORT) {
				JOptionPane.showMessageDialog(gui,
						"THIS PORT IS RESERVED BY SYSTEM!");
				return false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(gui, "Input format error!");
			return false;
		}

		/* remember to call super class's method */
		return super.checkValue();
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		/* if register */
		if (cmd.equals(command[0])) {
			if (checkValue()) {
				try {
					/* step 1: update configuration */
					ServerPeer.BOOTSTRAP_SERVER = (String) cbServer
							.getSelectedItem();
					ServerPeer.BOOTSTRAP_SERVER_PORT = Integer.parseInt(tfPort
							.getText().trim());

					ServerPeer.write();

					/* step 2: perform login request */
					if (ServerPeer.tryStartService()) {
						/* create message to be sent out */
						String user = tfUserID.getText().trim();
						String pwd = new String(tfPassword.getPassword());
						String email = tfEmail.getText().trim();

						/* perform login request here */
						serverpeer.performRegisterRequest(this, user, pwd,
								email);
					} else {
						JOptionPane.showMessageDialog(gui,
								"A program is running on the port "
										+ ServerPeer.LOCAL_SERVER_PORT
										+ "\r\n Please config your local "
										+ "server port");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		/* if cancel register */
		else if (cmd.equals(command[1])) {
			dispose();
		}
	}

	@Override
	protected void processWhenWindowClosing() {
	}

}