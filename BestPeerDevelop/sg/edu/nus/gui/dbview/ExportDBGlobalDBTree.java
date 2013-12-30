/*
 * @(#) DBTree.java 1.0 2006-12-26
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

/**
 * GlobalSchemaDBTree is used for schema mapping, and display the 
 * table and column of global schema.
 * <p>
 * 
 * @author Han Xixian
 * @version 1.0 2008-08-07 
 * @see javax.swing.JTree
 */

public class ExportDBGlobalDBTree extends JTree {
	// private members
	private static final long serialVersionUID = 1458145543006995860L;

	// the parent container which holds this tree
	// private ExportGlobalDBTreeView parentComponent;

	private ExportGlobalDBTreeMouseListener mouseListener;

	/**
	 * Construct an empty tree on the parent component
	 * 
	 * @param parent -  the parent container which holds this tree
	 */
	public ExportDBGlobalDBTree(ExportGlobalDBTreeView parent) {
		// this.parentComponent = parent;

		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(null);
		this.setModel(model);

		// add customized TreeCellRenderer
		this.setCellRenderer(new ExportDBGlobalTreeCellRenderer());

		mouseListener = new ExportGlobalDBTreeMouseListener(this);
		this.addMouseListener(mouseListener);
	}

	public void setupTree(DatabaseMetaData metadata, String databaseName)
			throws SQLException {
		DBTreeNode root = new DBTreeNode(databaseName);
		root.setNodeType("DATABASE");
		ResultSet tables, columns;
		String[] tableTypes = { "TABLE" };

		tables = metadata.getTables(null, null, null, tableTypes);

		DBTreeNode tableNode;
		DBTreeNode columnNode;

		Connection conn_index = ServerPeer.conn_bestpeerindexdb;

		Statement stmt = conn_index.createStatement();

		String sql = "select * from local_index";
		ResultSet rs = stmt.executeQuery(sql);
		Hashtable<String, Vector<String>> indexed = new Hashtable<String, Vector<String>>();
		while (rs.next()) {
			String cname = rs.getString("ind");
			String tname = rs.getString("val");
			if (indexed.containsKey(tname)) {
				indexed.get(tname).add(cname);
			} else {
				Vector<String> cols = new Vector<String>();
				cols.add(cname);
				indexed.put(tname, cols);
			}
		}
		rs.close();

		String[] arrGlobalTables = MetaDataAccess.metaGetTables(ServerPeer.conn_metabestpeerdb);
		
		while (tables.next())
		{
			String tableName = tables.getString("TABLE_NAME");

			if (!isTableInGlobalSchema(tableName, arrGlobalTables))
				continue;

			tableNode = new DBTreeNode(databaseName + "." + tableName);

			tableNode.setNodeType("TABLE");

			if (indexed.containsKey(tableName)) {
				tableNode.setHasInsertIndex(true);
			}

			root.add(tableNode);
			columns = metadata.getColumns(null, null, tableName, null);
			
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				columnNode = new DBTreeNode(databaseName + "." + tableName
						+ "." + columnName);

				columnNode.setNodeType("COLUMN");

				if (indexed.containsKey(tableName)) {
					if (indexed.get(tableName).contains(columnName))
						columnNode.setHasInsertIndex(true);
				}

				tableNode.add(columnNode);
			}
			columns.close();
		}
		
		tables.close();
		
		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);

		this.repaint();
	}

	private boolean isTableInGlobalSchema(String table, String[] arrTables){
		for (String tableName: arrTables) {
			if (table.equals(tableName))
				return true;
		}
		return false;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		Dimension dimension = this.getSize();
		int rightBoundLine = dimension.width;

		g2d.setFont(new Font("Indexed attribute", Font.BOLD, 12));

		g2d.setPaint(Color.GREEN);
		g2d.drawString(LanguageLoader.getProperty("label.Indexed"),
				rightBoundLine - 80, 20);

		g2d.setPaint(Color.BLACK);
		g2d.drawString(LanguageLoader.getProperty("label.NotIndexed"),
				rightBoundLine - 80, 40);

		Rectangle2D symbol_index = new Rectangle2D.Double(rightBoundLine - 92,
				12, 8, 8);
		g2d.setPaint(Color.GREEN);
		g2d.fill(symbol_index);

		Rectangle2D symbol_unIndex = new Rectangle2D.Double(
				rightBoundLine - 92, 32, 8, 8);
		g2d.setPaint(Color.BLACK);
		g2d.fill(symbol_unIndex);

		double leftX = rightBoundLine - 95;
		double topY = 5;
		double width = 90;
		double height = 40;

		Rectangle2D boundary = new Rectangle2D.Double(leftX, topY, width,
				height);
		g2d.setPaint(Color.BLACK);
		g2d.draw(boundary);

		this.repaint();
	}

	public void showPopupMenu(Component c, int x, int y) {
		TreePath path = getClosestPathForLocation(x, y);
		this.setSelectionPath(path);

		new ExportGlobalDBTreePopupMenu().show(c, x, y);
	}

	final class ExportGlobalDBTreeMouseListener extends MouseAdapter {
		// private members
		// private ExportDBGlobalDBTree tree;

		/**
		 * Constructor.
		 * 
		 * @param tree the handler of <code>DBTree</code>
		 */
		public ExportGlobalDBTreeMouseListener(ExportDBGlobalDBTree tree) {
			// this.tree = tree;
		}

		/**
		 * Invoked when the mouse has been clicked on a component.
		 */
		public void mouseClicked(MouseEvent e) {
			TreePath path = ExportDBGlobalDBTree.this.getPathForLocation(e
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

					ExportDBGlobalDBTree.this.repaint();
				} else if (e.getClickCount() == 2) {
					if (!ExportDBGlobalDBTree.this.isExpanded(path))
						ExportDBGlobalDBTree.this.expandPath(path);
					else
						ExportDBGlobalDBTree.this.collapsePath(path);
				}
			}
		}

	}

	// ----------- used for popup menu --------------

	final class ExportGlobalDBTreePopupMenu extends JPopupMenu implements
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
		public ExportGlobalDBTreePopupMenu() {
			this.makeMenu();
		}

		/*
		 * Construct the menu under different situations.
		 */
		private void makeMenu() {
			ExportDBGlobalDBTree.this.getSelectionPath().getLastPathComponent();

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
				
			}
			// if view the schema of selected table
			else if (cmd.equals(commands[1])) {
				JOptionPane.showMessageDialog(null, "Delete Index");
			}
		}
	}
}
