/*
 * @(#) EntrancePointDialog.java 1.0 2006-1-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved. 
 */

package sg.edu.nus.gui.server;

import java.awt.event.ActionEvent;

import sg.edu.nus.gui.AbstractEntrancePointDialog;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PeerInfo;

/**
 * This class is used to get the bootstrap server returned
 * online super peers and allows the super user to select
 * an arbitrary one to join the super peer network.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-10
 */
public final class EntrancePointDialog extends AbstractEntrancePointDialog {

	// private members
	private static final long serialVersionUID = -1924839997613927760L;
	private ServerPeer serverpeer;

	/**
	 * Construct the entrance point dialog.
	 *
	 * @param gui the handler of the main frame
	 * @param data an array of online super peers 
	 */
	String title = LanguageLoader.getProperty("title.dlgOnlinePeers");

	public EntrancePointDialog(ServerGUI gui, PeerInfo[] data) {
		super(gui, "", true, 360, 280, data);
		this.setTitle(title);
		this.serverpeer = gui.peer();

		/* show dialog */
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		/* if join network */
		if (cmd.equals(command[0])) {
			int rowIdx = entranceTable.getSelectedRow();
			String ip = "" + entranceTableSorter.getValueAt(rowIdx, 1);
			int port = Integer.parseInt(""
					+ entranceTableSorter.getValueAt(rowIdx, 2));

			if (serverpeer.performJoinRequest(ip, port)) {
				((ServerGUI) gui).startService();
				dispose();
			} else
				((ServerGUI) gui).getOperatePanel().getLoginPanel()
						.resetLoginBtn();
		}
		/* if cancel join */
		else if (cmd.equals(command[1])) {
			serverpeer.performCancelJoinRequest();
			serverpeer.clearSession();
			dispose();

			/**
			 * added by chensu, 2009-05-06
			 */
			serverpeer.stopEventManager();
			((ServerGUI) gui).getOperatePanel().getLoginPanel().resetLoginBtn();
		}
	}

	@Override
	protected void processWhenWindowClosing() {
		serverpeer.performCancelJoinRequest();
		serverpeer.clearSession();
	}

}