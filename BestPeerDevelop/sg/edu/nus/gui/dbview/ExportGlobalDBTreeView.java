/*
 * @(#) GlobalSchemeDBTreeView.java 1.0 2006-12-23
 * 
 * Copyright 2008, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sg.edu.nus.gui.test.peer.ExportDBPanel;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

/**
 * A tree view enables user to explore the data in the database in a <code>DBTree</code>.
 *   
 * @author Han Xixian
 * @version 1.0 2008-07-12
 */

public class ExportGlobalDBTreeView extends JPanel {
	// private members
	private static final long serialVersionUID = 3609575253483334889L;

	private Component parentComponent;

	// the customized tree to show the tables in the connected database.
	private ExportDBGlobalDBTree dbTree;

	private ExportDBPanel clientgui;

	public static String currentGlobalDatabaseName = ServerPeer.GLOBAL_DB;

	/**
	 * Constructor
	 * 
	 * @param parent - the parent container that hold this panel.
	 *                 the parent container must be <code>DatabaseExplorer</code> instance.
	 */
	public ExportGlobalDBTreeView(ExportDBPanel gui,
			Component/*DatabaseExplorer*/parent) {
		this.clientgui = gui;
		this.parentComponent = parent;

		try {
			this.initPane();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPane() throws Exception {
		this.setLayout(new BorderLayout());

		String databaseName = ServerPeer.EXPORTED_DB;

		Connection conn = ServerPeer.conn_exportDatabase;

		DatabaseMetaData dbmetadata = conn.getMetaData();

		dbTree = new ExportDBGlobalDBTree(this);
		dbTree.setupTree(dbmetadata, databaseName);

		JScrollPane scrollPane = new JScrollPane(dbTree);
		this.add(scrollPane, BorderLayout.CENTER);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.ExportedDB")));

		this.setPreferredSize(new Dimension(400, 600));

	}

	/**
	 * Construct a <code>DBTree</code> with the information: dbmetadata and databaseName
	 * 
	 * @param dbmetadata -  the metadata for the connected database
	 * @param databaseName -  the name of the connected database
	 * @throws SQLException - if a database access error occurs
	 */

	/**
	 * Remove the DBTree from the panel before disconnect to the database
	 *
	 */
	public void destroyTree() {
		dbTree.setModel(null);
	}

	/**
	 * Return the parent container which holds this panel
	 * 
	 * @return the parent container(<code>DatabaseExplorer</code>)
	 */
	public Component getParentComponent() {
		return parentComponent;
	}

	/**
	 * Return a string: "DBTreeView"
	 * 
	 * @return "DBTreeView"
	 */
	public String toString() {
		return "DBTreeView";
	}

	/**
	 * @return the dbTree
	 */
	public ExportDBGlobalDBTree getDbTree() {
		return dbTree;
	}

	/**
	 * @param dbTree the dbTree to set
	 */
	public void setDbTree(ExportDBGlobalDBTree dbTree) {
		this.dbTree = dbTree;
	}

	/**
	 * @return the clientgui
	 */
	public ExportDBPanel getClientgui() {
		return clientgui;
	}

	/**
	 * @param clientgui the clientgui to set
	 */
	public void setClientgui(ExportDBPanel clientgui) {
		this.clientgui = clientgui;
	}
}
