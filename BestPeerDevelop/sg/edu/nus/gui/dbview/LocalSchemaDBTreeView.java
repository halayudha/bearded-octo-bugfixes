/*
 * @(#) DBTreeView.java 1.0 2006-12-23
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import sg.edu.nus.gui.test.peer.DBExplorerPanel;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

/**
 * A tree view enables user to explore the data in the database in a <code>LocalSchemaDBTree</code>.
 *   
 * @author Han Xixian
 * @version 1.0 2008-07-12
 */

public class LocalSchemaDBTreeView extends JPanel implements
		ListSelectionListener {
	// private members
	private static final long serialVersionUID = 3609575253483334882L;

	private Component parentComponent;

	// the customized tree to show the tables in the connected database.
	private LocalSchemaDBTree dbTree;

	private DBExplorerPanel dbmanagerGui;

	/**
	 * Constructor
	 * 
	 * @param parent - the parent container that hold this panel.
	 *                 the parent container must be <code>DatabaseExplorer</code> instance.
	 */
	public LocalSchemaDBTreeView(DBExplorerPanel gui,
			Component/*DatabaseExplorer*/parent) {
		this.dbmanagerGui = gui;
		this.parentComponent = parent;

		try {
			this.initPane();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// this.expandTree(dbTree);
	}

	public void expandTree(LocalSchemaDBTree tree) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel()
				.getRoot();
		ExpandAll(tree, new TreePath(root));
	}

	@SuppressWarnings("unchecked")
	public void ExpandAll(LocalSchemaDBTree tree, TreePath parent) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
				.getLastPathComponent();

		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e
						.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				ExpandAll(tree, path);
			}
		}

		tree.expandPath(parent);
	}

	/* Initialize all the components in the interface.
	 * 
	 * this(JPanel, BorderLayout)
	 *   - contentPane(JPanel, BorderLayout)
	 *   	- treePane(JPanel, BorderLayout)
	 *   		- dbTree(DBTree)
	 *   	- tablePane(JPanel, BorderLayout)
	 *   		- tableTopPane(JPanel, BorderLayout)
	 *   			- closelabel(JLabel, "-Close-")
	 *   			- tabelNameLabel(JLabel, "Tabel:xxxx")
	 *   		- tableDownPane(JPanel, BorderLayout)
	 *   			- dbTabel(DBTabel)
	 *   - statusPane(JPanel, Flowlayout)
	 * 			- connectButton(JButton, "disConnect")
	 */
	private void initPane() throws SQLException, ClassNotFoundException {
		this.setLayout(new BorderLayout());

		Connection conn = ServerPeer.conn_localSchema;
		DatabaseMetaData dbmetadata = conn.getMetaData();

		dbTree = new LocalSchemaDBTree(this);
		dbTree.setupTree(dbmetadata, ServerPeer.erp_db.getDbName());

		JScrollPane scrollPane = new JScrollPane(dbTree);

		this.add(scrollPane, BorderLayout.CENTER);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.localschema")));

		this.setPreferredSize(new Dimension(250, 600));
	}

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

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			e.getFirstIndex();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/**
	 * @return the dbTree
	 */
	public LocalSchemaDBTree getDbTree() {
		return dbTree;
	}

	/**
	 * @param dbTree the dbTree to set
	 */
	public void setDbTree(LocalSchemaDBTree dbTree) {
		this.dbTree = dbTree;
	}

	/**
	 * @return the dbmanagerGui
	 */
	public DBExplorerPanel getDbmanagerGui() {
		return dbmanagerGui;
	}

	/**
	 * @param dbmanagerGui the dbmanagerGui to set
	 */
	public void setDbmanagerGui(DBExplorerPanel dbmanagerGui) {
		this.dbmanagerGui = dbmanagerGui;
	}

	public void repaint() {
		super.repaint();
		if (dbTree != null)
			dbTree.repaint();
	}
}
