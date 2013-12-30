package sg.edu.nus.gui.sharing;

/*
 * @(#) DatabaseExplorer.java 1.0 2006-12-23
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

import java.awt.CardLayout;
import java.sql.Connection;

import javax.swing.JDialog;
import javax.swing.JPanel;

import sg.edu.nus.gui.dbview.DBConnectView;
import sg.edu.nus.gui.dbview.DBTreeView;
import sg.edu.nus.gui.dbview.SharedDBConnectView;
import sg.edu.nus.gui.dbview.SharedDBTreeView;

;

/**
 * A <code>DatabaseView</code> with corresponding operations
 * such as making connection , viewing datas in the tree/table format and sharing/unsharing data in DB.
 * 
 * @author Han Xixian
 * @version 1.0 2008-07-12
 * @see DBConnectView
 * @see DBTreeView
 */

public class SDBExplorerPanel extends JPanel {
	// private members
	private static final long serialVersionUID = 9109701308723161369L;

	private JDialog clientgui;

	/*
	 * Using the <code>CardLayout</code> to hold two Panels: DBConnectView and DBTreeView.
	 * User can toggle between the two panels by clicking buttons, which corresponds to 
	 * 		connect/disconnet to the relational database. 
	 */
	public SharedDBConnectView dbConnectView;
	public SharedDBTreeView dbTreeView;

	private Connection con;

	/**
	 * Constructor.
	 * @param parent the parent container hold this panel
	 */
	public SDBExplorerPanel(JDialog gui) {
		// this.parentComponent = parent;
		this.clientgui = gui;
		this.setLayout(new CardLayout());

		dbConnectView = new SharedDBConnectView(this);
		dbTreeView = new SharedDBTreeView(this.clientgui, this);

		this.add(dbConnectView.toString(), dbConnectView);
		this.add(dbTreeView.toString(), dbTreeView);

	}

	/**
	 * Toggle the panel: DBConnectView/DBTreeView according to whether or not the 
	 * database is connected.
	 * 
	 * @param connected - <code>true</code> if the database is connected.  
	 */
	public void isConnected(boolean connected) {
		if (connected)
			((CardLayout) this.getLayout()).show(this, dbTreeView.toString());
		else {
			((CardLayout) this.getLayout())
					.show(this, dbConnectView.toString());
			this.con = null;
		}
	}

	/**
	 * Set the connection(java.sql.Connection) value
	 * 
	 * @param c - the connection value
	 */
	public void setConnection(Connection c) {
		this.con = c;
	}

	/**
	 * Return the connection(java.sql.Connection) value
	 * 
	 * @return the connection value
	 */
	public Connection getConnection() {
		return this.con;
	}
}
