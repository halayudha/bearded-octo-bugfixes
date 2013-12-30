/*
 * @(#) DBTree.java 1.0 2008-07-16
 * 
 * Copyright 2008, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import sg.edu.nus.peer.ServerPeer;

/**
 * LocalSchemaDBTree is used for schema mapping, and display table and column
 * information of local database.
 * 
 * @author Han Xixian
 * @version 1.0 2008-7-16 
 * @see javax.swing.JTree
 */

public class ExportDBLocalDBTree extends JTree {
	// private members
	private static final long serialVersionUID = 1458145543006995835L;

	// the parent container which holds this tree
	// private Component parentComponent;

	private ExportDBTreeMouseListener mouseListener;

	/**
	 * Construct an empty tree on the parent component
	 * 
	 * @param parent -  the parent container which holds this tree
	 */
	public ExportDBLocalDBTree(Component parent) {
		// this.parentComponent = parent;

		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// add new mouse listener

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(null);
		this.setModel(model);

		// add customized TreeCellRenderer
		this.setCellRenderer(new LocalSchemaTreeCellRenderer_test());
		this.setCellEditor(new LocalSchemaDBTreeNodeEditor(this));
		this.setEditable(true);

		mouseListener = new ExportDBTreeMouseListener(this);
		this.addMouseListener(mouseListener);

	}

	/**
	 * Set up a tree with the data extracted from the metadata and use the input root node.
	 * 
	 * Modified by Han Xixian 2008-6-6
	 * When setting up this tree, table or column sharing information is also checked.
	 * Note: Sharing information is stored in file sharedInfo.dat	 * 
	 * 
	 * @param metadata - metadata to hold all the table information of the connected database 
	 * @param root - the root node
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public void setupTree(DatabaseMetaData metadata, String databaseName)
			throws SQLException {
		Connection conn = ServerPeer.conn_schemaMapping;

		DBTreeNode root = new DBTreeNode(databaseName);

		root.setNodeType("DATABASE");

		ResultSet tables, columns;
		String[] tableTypes = { "TABLE" };

		tables = metadata.getTables(null, null, null, tableTypes);

		DBTreeNode tableNode;
		DBTreeNode columnNode;

		String tableName = null;

		while (tables.next()) {
			tableName = tables.getString("TABLE_NAME");
			tableNode = new DBTreeNode(databaseName + "." + tableName);
			tableNode.setNodeType("TABLE");

			Statement stmt = conn.createStatement();
			String query = "select count(*) count from matches where "
					+ "sourceTable = '" + tableName + " '";

			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int count = rs.getInt("count");
			if (count == 0)
				continue;

			if (count > 0) {
				tableNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
			}

			root.add(tableNode);
			columns = metadata.getColumns(null, null, tableName, null);
			rs.close();
			stmt.close();

			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");

				stmt = conn.createStatement();
				query = "select * from matches where " + "sourceTable = '"
						+ tableName + " ' and sourceColumn = '" + columnName
						+ "'";

				rs = stmt.executeQuery(query);

				if (rs.next()) {
					String targetDB = rs.getString("targetDB");
					String targetTable = rs.getString("targetTable");
					String targetColumn = rs.getString("targetColumn");

					String targetSchemaName = targetDB + "." + targetTable
							+ "." + targetColumn;

					columnNode = new DBTreeNode(databaseName + "." + tableName
							+ "." + columnName);

					columnNode.getTargetSchemaName().add(targetSchemaName);

					columnNode.setNodeType("COLUMN");
					columnNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);

					tableNode.add(columnNode);
				}

				rs.close();
				stmt.close();
			}

			columns.close();
		}

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);
	}

	/**
	 * Show <code>JPopupMenu</code>.
	 * 
	 * @param x - the x coordinate of the mouse event
	 * @param y - the y coordinate of the mouse event
	 */
	public void showPopupMenu(Component c, int x, int y) {
		TreePath path = getClosestPathForLocation(x, y);
		this.setSelectionPath(path);

		new ExportDBTreePopupMenu().show(c, x, y);
	}

	final class ExportDBTreeMouseListener extends MouseAdapter {
		// private members
		// private ExportDBLocalDBTree tree;

		/**
		 * Constructor.
		 * 
		 * @param tree the handler of <code>DBTree</code>
		 */
		public ExportDBTreeMouseListener(ExportDBLocalDBTree tree) {
			// this.tree = tree;
		}

		/**
		 * Invoked when the mouse has been clicked on a component.
		 */
		public void mouseClicked(MouseEvent e) {
			TreePath path = ExportDBLocalDBTree.this.getPathForLocation(e
					.getX(), e.getY());

			DBTreeNode node = null;

			if (path != null)
				node = (DBTreeNode) path.getLastPathComponent();

			if (node == null) {
				return;
			}

			if (SwingUtilities.isLeftMouseButton(e)) {
				if (e.getClickCount() == 1) {

					if (node.getNodeType().equals("TABLE")) {
						node.setSelected(!node.isSelected());

						if (node.isSelected()) {
							int childnum = node.getChildCount();

							for (int i = 0; i < childnum; i++) {
								DBTreeNode cNode = (DBTreeNode) node
										.getChildAt(i);
								cNode.setSelected(true);
							}
						} else {
							int childnum = node.getChildCount();

							for (int i = 0; i < childnum; i++) {
								DBTreeNode cNode = (DBTreeNode) node
										.getChildAt(i);
								cNode.setSelected(false);
							}
						}
					} else {
						node.setSelected(!node.isSelected());

						DBTreeNode tNode = (DBTreeNode) node.getParent();

						if (!node.isSelected()) {

							int childnum = tNode.getChildCount();

							DBTreeNode cNode = null;

							for (int i = 0; i < childnum; i++) {
								cNode = (DBTreeNode) tNode.getChildAt(i);
								if (cNode.isSelected())
									return;
							}

							tNode.setSelected(false);
						} else
							tNode.setSelected(true);
					}

					ExportDBLocalDBTree.this.repaint();
				} else if (e.getClickCount() == 2) {
					if (!ExportDBLocalDBTree.this.isExpanded(path))
						ExportDBLocalDBTree.this.expandPath(path);
					else
						ExportDBLocalDBTree.this.collapsePath(path);
				}
			}

		}
		
	}

	// ----------- used for popup menu --------------

	final class ExportDBTreePopupMenu extends JPopupMenu implements
			ActionListener {
		// private members
		private static final long serialVersionUID = 6764829618615422807L;

		// menu items
		private JMenuItem insertIndexMenuItem;
		private JMenuItem deleteIndexMenuItem;

		// private DefaultMutableTreeNode node;

		private String[] itemName = { "insert index", "delete index" };

		private String[] commands = { "insert index", "delete index" };

		/**
		 * Constructor
		 *
		 */
		public ExportDBTreePopupMenu() {
			this.makeMenu();
		}

		/*
		 * Construct the menu under different situations.
		 */
		private void makeMenu() {
			ExportDBLocalDBTree.this.getSelectionPath().getLastPathComponent();

			insertIndexMenuItem = this.makeMenuItem(itemName[0], commands[0]);
			deleteIndexMenuItem = this.makeMenuItem(itemName[1], commands[1]);

			this.add(insertIndexMenuItem);
			this.add(deleteIndexMenuItem);
		}

		/**
		 * Make individual menu item.
		 * 
		 * @param name - the menu item name
		 * @param cmd - the command string
		 * @param img - the image name
		 * 
		 * @return the instance of <code>JMenuItem</code>
		 */
		private JMenuItem makeMenuItem(String name, String cmd) {
			JMenuItem menuItem = new JMenuItem(name);
			menuItem.setActionCommand(cmd);
			menuItem.addActionListener(this);

			return menuItem;
		}

		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();

			// if view the data in the selected table
			if (cmd == commands[0]) {
				JOptionPane.showMessageDialog(null, "Insert Index");
			}
			// if view the schema of selected table
			else if (cmd.equals(commands[1])) {
				JOptionPane.showMessageDialog(null, "Delete Index");
			}
		}
	}
}
