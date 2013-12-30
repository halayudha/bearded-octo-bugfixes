/*
 * @(#) DBTree.java 1.0 2008-07-16
 * 
 * Copyright 2008, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.test.peer.DBExplorerMappingPanel;
import sg.edu.nus.gui.test.peer.DBExplorerPanel;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.Session;

/**
 * LocalSchemaDBTree is used for schema mapping, and display table and column
 * information of local database.
 * 
 * @author Han Xixian
 * @version 1.0 2008-7-16
 * @see javax.swing.JTree
 */

public class LocalSchemaDBTree extends JTree {
	// private members
	private static final long serialVersionUID = 1458145543006995835L;

	private LocalSchemaDBTreeMouseListener mouseListener;

	// the parent container which holds this tree
	private Component parentComponent;

	private Vector<String> temporalShareInfo = null;
	private Vector<String> schemaMappingInfo = null;

	private Vector<Share_SchemaMapping_Info> s_sm_infoSet = null;

	/**
	 * Construct an empty tree on the parent component
	 * 
	 * @param parent
	 *            - the parent container which holds this tree
	 */
	public LocalSchemaDBTree(Component parent) {
		this.parentComponent = parent;

		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// add new mouse listener
		mouseListener = new LocalSchemaDBTreeMouseListener(this);
		this.addMouseListener(mouseListener);

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(null);
		this.setModel(model);

		// add customized TreeCellRenderer
		this.setCellRenderer(new LocalSchemaTreeCellRenderer_test());
		this.setCellEditor(new LocalSchemaDBTreeNodeEditor(this));
		this.setEditable(true);

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TreePath path = LocalSchemaDBTree.this.getPathForLocation(e
						.getX(), e.getY());

				DBTreeNode node = null;

				if (path != null)
					node = (DBTreeNode) path.getLastPathComponent();

				if (node == null) {
					return;
				}

				if (e.getClickCount() == 2) {
					if (!LocalSchemaDBTree.this.isExpanded(path))
						LocalSchemaDBTree.this.expandPath(path);
					else
						LocalSchemaDBTree.this.collapsePath(path);
				}
			}
		});

		temporalShareInfo = new Vector<String>();

		schemaMappingInfo = new Vector<String>();

		s_sm_infoSet = new Vector<Share_SchemaMapping_Info>();
	}

	/**
	 * Set up a tree with the data extracted from the metadata and use the input
	 * root node.
	 * 
	 * Modified by Han Xixian 2008-6-6 When setting up this tree, table or
	 * column sharing information is also checked. Note: Sharing information is
	 * stored in file sharedInfo.dat *
	 * 
	 * @param metadata
	 *            - metadata to hold all the table information of the connected
	 *            database
	 * @param root
	 *            - the root node
	 * 
	 * @throws SQLException
	 *             - if a database access error occurs
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
			tableNode = new DBTreeNode(tableName);
			tableNode.setNodeType("TABLE");

			Statement stmt = conn.createStatement();
			String query = "select count(*) count from matches where "
					+ "sourceTable = '" + tableName + " '";

			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int count = rs.getInt("count");
			if (count > 0) {
				tableNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
				tableNode.setSelected(true);
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

					columnNode = new DBTreeNode(columnName);
					columnNode.setSelected(true);

					columnNode.getTargetSchemaName().add(targetSchemaName);

					columnNode.setNodeType("COLUMN");
					columnNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
				} else {
					columnNode = new DBTreeNode(columnName);

					columnNode.setNodeType("COLUMN");
				}

				rs.close();
				stmt.close();

				tableNode.add(columnNode);
			}

			columns.close();
		}

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);
	}

	/***
	 * 
	 * @param targetSchemaName
	 * @return
	 */

	public boolean isTargetSchemaNodeExpanded(String targetSchemaName) {
		if (targetSchemaName == null)
			return false;

		String[] arrData = targetSchemaName.split("\\.");

		LocalSchemaDBTreeView dbtreeView = (LocalSchemaDBTreeView) LocalSchemaDBTree.this.parentComponent;
		DBExplorerPanel explorerPanel = (DBExplorerPanel) dbtreeView
				.getParentComponent();

		GlobalSchemaDBTreeView globalschemaDBTreeView = explorerPanel
				.getGlobalDBTreeView();
		GlobalSchemaDBTree globalSchemaDBTree = globalschemaDBTreeView
				.getDbTree();

		DBTreeNode root = (DBTreeNode) globalSchemaDBTree.getModel().getRoot();
		int tableCount = root.getChildCount();

		for (int i = 0; i < tableCount; i++) {
			DBTreeNode tableNode = (DBTreeNode) root.getChildAt(i);

			String TableName = tableNode.getSourceSchemaName();

			if (TableName.equals(arrData[1])) {

				TreeNode[] arrNode = tableNode.getPath();
				TreePath pathOfTable = new TreePath(arrNode);

				if (globalSchemaDBTree.isExpanded(pathOfTable))
					return true;
			}
		}

		return false;
	}

	/***
	 * paintComponent is used to paint schema mapping info
	 * 
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		DBTreeNode root = (DBTreeNode) this.getModel().getRoot();

		int tableCount = root.getChildCount();

		for (int i = 0; i < tableCount; i++) {
			DBTreeNode tableNode = (DBTreeNode) root.getChildAt(i);

			TreeNode[] arrNode = tableNode.getPath();
			TreePath pathOfTable = new TreePath(arrNode);

			if (!this.isExpanded(pathOfTable)) {
				int columnCount = tableNode.getChildCount();

				for (int j = 0; j < columnCount; j++) {
					DBTreeNode columnNode = (DBTreeNode) tableNode
							.getChildAt(j);

					if ((columnNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
							&& (columnNode.getTargetSchemaName().size() != 0)) {

						LocalSchemaDBTreeView dbtreeView = (LocalSchemaDBTreeView) LocalSchemaDBTree.this.parentComponent;
						DBExplorerPanel explorerPanel = (DBExplorerPanel) dbtreeView
								.getParentComponent();
						DBExplorerMappingPanel mappingPanel = explorerPanel
								.getMappingPanel();

						for (int z = 0; z < columnNode.getTargetSchemaName()
								.size(); z++) {
							mappingPanel.delSinglePoint(root
									.getSourceSchemaName()
									+ "."
									+ tableNode.getSourceSchemaName()
									+ "." + columnNode.getSourceSchemaName(),
									columnNode.getTargetSchemaName().get(z),
									"LOCAL");
						}

						mappingPanel.repaint();
					}

				}
				continue;
			}

			int columnCount = tableNode.getChildCount();

			for (int j = 0; j < columnCount; j++) {
				DBTreeNode columnNode = (DBTreeNode) tableNode.getChildAt(j);

				for (int z = 0; z < columnNode.getTargetSchemaName().size(); z++) {
					if ((columnNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
							&& (columnNode.getTargetSchemaName() != null)
							&& (this.isTargetSchemaNodeExpanded(columnNode
									.getTargetSchemaName().get(z)))) {
						TreeNode[] arrNodeOfColumn = columnNode.getPath();
						TreePath pathOfColumn = new TreePath(arrNodeOfColumn);

						Rectangle rect = this.getPathBounds(pathOfColumn);

						int x_coordinate_start_line = rect.x + rect.width;
						int y_coordinate_start_line = rect.y + rect.height / 2;

						Dimension sizeOfTree = this.getSize();

						int x_coordinate_end_line = sizeOfTree.width;
						int y_coordinate_end_line = y_coordinate_start_line;

						g2.setPaint(Color.GREEN);
						g2.setStroke(new BasicStroke(2));
						g2.drawLine(x_coordinate_start_line,
								y_coordinate_start_line, x_coordinate_end_line,
								y_coordinate_end_line);

						LocalSchemaDBTreeView dbtreeView = (LocalSchemaDBTreeView) LocalSchemaDBTree.this.parentComponent;
						DBExplorerPanel explorerPanel = (DBExplorerPanel) dbtreeView
								.getParentComponent();

						Point absolutePosition = this.getLocationOnScreen();

						DBExplorerMappingPanel mappingPanel = explorerPanel
								.getMappingPanel();
						mappingPanel.addSinglePoint(root.getSourceSchemaName()
								+ "." + tableNode.getSourceSchemaName() + "."
								+ columnNode.getSourceSchemaName(), columnNode
								.getTargetSchemaName().get(z), "LOCAL", 0,
								absolutePosition.y + y_coordinate_end_line);

						mappingPanel.repaint();
					}
				}

			}
		}
	}

	public void ModifyShareTableInfo(String opType, String tableName) {
		try {
			String filename = ServerPeer.erp_db.getDbName();

			BufferedReader reader = new BufferedReader(new FileReader("./"
					+ filename + ".dat"));
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"./temporal.dat"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] arrData = line.split("\\|");
				if (arrData[0].equals("TABLE") && arrData[1].equals(tableName)) {
					if (opType.equals("SHARE"))
						writer.write(arrData[0] + "|" + arrData[1] + "|" + 1
								+ "\r\n");
					else
						writer.write(arrData[0] + "|" + arrData[1] + "|" + 0
								+ "\r\n");
				} else
					writer.write(line + "\r\n");
			}

			reader.close();
			writer.close();

			File file = new File("./" + filename + ".dat");
			file.delete();
			File file2 = new File("./temporal.dat");
			file2.renameTo(new File("./" + filename + ".dat"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ModifyShareColumnInfo(String opType, String str) {
		String[] arr = str.split("\\|");
		String tableName = arr[0];
		String columnName = arr[1];

		try {
			String filename = ServerPeer.erp_db.getDbName();

			BufferedReader reader = new BufferedReader(new FileReader("./"
					+ filename + ".dat"));
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"./temporal.dat"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] arrData = line.split("\\|");
				if (arrData[0].equals("COLUMN") && arrData[1].equals(tableName)
						&& (arrData[2].equals(columnName))) {
					if (opType.equals("SHARE"))
						writer.write(arrData[0] + "|" + arrData[1] + "|"
								+ arrData[2] + "|" + 1 + "\r\n");
					else
						writer.write(arrData[0] + "|" + arrData[1] + "|"
								+ arrData[2] + "|" + 0 + "\r\n");
				} else
					writer.write(line + "\r\n");
			}

			reader.close();
			writer.close();

			File file = new File("./" + filename + ".dat");
			file.delete();
			File file2 = new File("./temporal.dat");
			file2.renameTo(new File("./" + filename + ".dat"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DBTreeNode FindTableNode(DBTreeNode root, String tableName) {
		DBTreeNode tNode = null;

		int tableCount = root.getChildCount();
		for (int j = 0; j < tableCount; j++) {
			DBTreeNode currentNode = (DBTreeNode) root.getChildAt(j);

			if (currentNode.getSourceSchemaName().equals(tableName)) {
				tNode = currentNode;
				break;
			}
		}

		return tNode;
	}

	public DBTreeNode FindColumnNode(DBTreeNode tNode, String columnName) {
		DBTreeNode cNode = null;

		int ColumnCount = tNode.getChildCount();
		for (int z = 0; z < ColumnCount; z++) {
			DBTreeNode currentNode = (DBTreeNode) tNode.getChildAt(z);

			if (currentNode.getSourceSchemaName().equals(columnName)) {
				cNode = currentNode;
				break;
			}
		}

		return cNode;
	}

	public boolean schemaMappingExist(String tableName, String columnName) {
		for (int i = 0; i < this.schemaMappingInfo.size(); i++) {
			String schemaMapping = this.schemaMappingInfo.get(i);
			String[] arrSM = schemaMapping.split("->");
			String localSchema = arrSM[0];
			String[] arrLocalSchema = localSchema.split("\\.");
			if (arrLocalSchema[1].equals(tableName)
					&& arrLocalSchema[2].equals(columnName))
				return true;
		}

		return false;
	}

	public boolean existColumnOperation(Vector<String> vec) {

		return true;
	}

	public void saveModification() {

		for (int i = 0; i < s_sm_infoSet.size(); i++) {
			Share_SchemaMapping_Info ssmi = this.s_sm_infoSet.get(i);

			if ((ssmi.getOperationType().equals("SHARE"))
					&& (ssmi.getNewTargetSchema() == null)
					&& (ssmi.getNodeType().equals("COLUMN"))) {
				JOptionPane.showMessageDialog(null,
						"Shared Info lack of Schema Mapping Infomation!");

				return;
			}
		}

		DBTreeNode root = (DBTreeNode) this.getModel().getRoot();

		for (int i = 0; i < s_sm_infoSet.size(); i++) {
			Share_SchemaMapping_Info ssmi = this.s_sm_infoSet.get(i);

			if (ssmi.getNodeType().equals("TABLE")
					&& (ssmi.getOperationType().equals("SHARE"))) {
				String tableName = ssmi.getTableName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				tNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
			} else if (ssmi.getNodeType().equals("TABLE")
					&& (ssmi.getOperationType().equals("UNSHARE"))) {
				String tableName = ssmi.getTableName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				tNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);
			} else if (ssmi.getNodeType().equals("COLUMN")
					&& (ssmi.getOperationType().equals("SHARE"))) {
				String tableName = ssmi.getTableName();
				String columnName = ssmi.getColumnName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				DBTreeNode cNode = this.FindColumnNode(tNode, columnName);
				cNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);

			} else if (ssmi.getNodeType().equals("COLUMN")
					&& (ssmi.getOperationType().equals("UNSHARE"))) {
				String tableName = ssmi.getTableName();
				String columnName = ssmi.getColumnName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				DBTreeNode cNode = this.FindColumnNode(tNode, columnName);
				cNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);

			} else if (ssmi.getNodeType().equals("COLUMN")
					&& (ssmi.getOperationType().equals("MODIFYSCHEMAMAPPING"))) {
				String tableName = ssmi.getTableName();
				String columnName = ssmi.getColumnName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				DBTreeNode cNode = this.FindColumnNode(tNode, columnName);
				cNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);

			}
		}

		this.s_sm_infoSet.clear();

		this.parentComponent.repaint();
	}

	public void cancelModification() {
		DBTreeNode root = (DBTreeNode) this.getModel().getRoot();

		for (int i = 0; i < s_sm_infoSet.size(); i++) {
			Share_SchemaMapping_Info ssmi = this.s_sm_infoSet.get(i);

			if (ssmi.getNodeType().equals("TABLE")) {
				String tableName = ssmi.getTableName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				tNode.setShareStatus(ssmi.getOriginalStatus());
			} else if (ssmi.getNodeType().equals("COLUMN")) {
				String tableName = ssmi.getTableName();
				String columnName = ssmi.getColumnName();

				DBTreeNode tNode = this.FindTableNode(root, tableName);
				DBTreeNode cNode = this.FindColumnNode(tNode, columnName);
				cNode.setShareStatus(ssmi.getOriginalStatus());

				if (ssmi.getNewTargetSchema() != null) {
					if (ssmi.getOldTargetSchema() == null) {
						cNode.setUserObject(ssmi.getSourceSchema());
						this.updateUI();
					} else {
						cNode.setUserObject(ssmi.getSourceSchema() + "->"
								+ ssmi.getOldTargetSchema());
						this.updateUI();
					}
				}

				if ((ssmi.getOldTargetSchema() != null)
						&& (ssmi.getOriginalStatus() == DBTreeNode.SHARESTATUS_SHARE)) {
					if (ssmi.getOldTargetSchema() != null) {
						cNode.setUserObject(ssmi.getSourceSchema() + "->"
								+ ssmi.getOldTargetSchema());
						this.updateUI();
					}
				}

			}

		}

		this.s_sm_infoSet.clear();

		this.parentComponent.repaint();
	}

	/**
	 * Show <code>JPopupMenu</code>.
	 * 
	 * @param x
	 *            - the x coordinate of the mouse event
	 * @param y
	 *            - the y coordinate of the mouse event
	 */
	public void showPopupMenu(Component c, int x, int y) {
		TreePath path = getClosestPathForLocation(x, y);
		this.setSelectionPath(path);

		new DBTreePopupMenu().show(c, x, y);
	}

	/**
	 * Create a <code>MouseListener</code> for <code>DBTree</code>.
	 * 
	 * @author Huang Yukai
	 * @version 1.0 2006-12-26
	 */

	final class LocalSchemaDBTreeMouseListener implements MouseListener {
		// private members
		private LocalSchemaDBTree tree;

		/**
		 * Constructor.
		 * 
		 * @param tree
		 *            the handler of <code>DBTree</code>
		 */
		public LocalSchemaDBTreeMouseListener(LocalSchemaDBTree tree) {
			this.tree = tree;
		}

		/**
		 * Invoked when the mouse has been clicked on a component.
		 */
		public void mouseClicked(MouseEvent e) {
			this.showPopupMenu(e);
		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e) {
			this.showPopupMenu(e);
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		public void mouseReleased(MouseEvent e) {
			this.showPopupMenu(e);
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		public void mouseEntered(MouseEvent e) {
		}

		/**
		 * Invoked when the mouse exits a component.
		 */
		public void mouseExited(MouseEvent e) {
		}

		// Show the popup menu.
		// @see DBTreePopupMenu
		private void showPopupMenu(MouseEvent e) {
			if (e.isPopupTrigger()) {
				tree.showPopupMenu(e.getComponent(), e.getX(), e.getY());
			}

		}
	}

	// ----------- used for popup menu --------------

	/**
	 * Construct a popup menu for <code>DBTree</code>.
	 * 
	 * @author Huang Yukai
	 * @version 1.0 2006-12-26
	 * @see javax.swing.JPopupMenu
	 */

	final class DBTreePopupMenu extends JPopupMenu implements ActionListener {
		// private members
		private static final long serialVersionUID = 6764829618615422807L;

		// menu items
		private JMenuItem openMenuItem;
		private JMenuItem schemaMenuItem;
		private JMenuItem searchMenuItem;
		private JMenuItem shareMenuItem;
		private JMenuItem unshareMenuItem;
		private JMenuItem reshareMenuItem;
		private JMenuItem refreshMenuItem;

		private DefaultMutableTreeNode node;

		private String[] itemName = { "Open Table", "View Schema", "Search...",
				"Share", "Unshare", "Reshare", "Refresh" };

		private String[] commands = { "open", "schema", "search", "share",
				"unshare", "reshare", "refresh" };

		private String[] images = { "openTable", "schema", "empty", "share",
				"unshare", "reshare", "refresh" };

		/**
		 * Constructor
		 * 
		 */
		public DBTreePopupMenu() {
			this.makeMenu();
		}

		/*
		 * Construct the menu under different situations.
		 */
		private void makeMenu() {

			node = (DefaultMutableTreeNode) LocalSchemaDBTree.this
					.getSelectionPath().getLastPathComponent();

			int offset = -14;

			openMenuItem = this.makeMenuItem(itemName[0], commands[0],
					images[0]);
			openMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset,
					2, 0));

			schemaMenuItem = this.makeMenuItem(itemName[1], commands[1],
					images[1]);
			schemaMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset,
					2, 0));

			searchMenuItem = this.makeMenuItem(itemName[2], commands[2],
					images[2]);
			searchMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset,
					2, 0));

			shareMenuItem = this.makeMenuItem(itemName[3], commands[3],
					images[3]);
			shareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset,
					2, 0));

			unshareMenuItem = this.makeMenuItem(itemName[4], commands[4],
					images[4]);
			unshareMenuItem.setBorder(BorderFactory.createEmptyBorder(2,
					offset, 2, 0));

			reshareMenuItem = this.makeMenuItem(itemName[5], commands[5],
					images[5]);
			reshareMenuItem.setBorder(BorderFactory.createEmptyBorder(2,
					offset, 2, 0));

			refreshMenuItem = this.makeMenuItem(itemName[6], commands[6],
					images[6]);
			refreshMenuItem.setBorder(BorderFactory.createEmptyBorder(2,
					offset, 2, 0));

			this.add(shareMenuItem);
			this.add(unshareMenuItem);

			// Only after connecting to the PeerDB system, then show
			// "Share/Unshare/Reshare" functions
			try {
				Session.getInstance();
			} catch (RuntimeException e) {
				/* Not connect to the peerDB */
				shareMenuItem.setEnabled(false);
				unshareMenuItem.setEnabled(false);
				reshareMenuItem.setEnabled(false);
			}
			this.addSeparator();
			this.add(refreshMenuItem);

		}

		/**
		 * Make individual menu item.
		 * 
		 * @param name
		 *            - the menu item name
		 * @param cmd
		 *            - the command string
		 * @param img
		 *            - the image name
		 * 
		 * @return the instance of <code>JMenuItem</code>
		 */
		private JMenuItem makeMenuItem(String name, String cmd, String img) {
			/* look for the image */
			String imageLoc = AbstractMainFrame.SRC_PATH + img + ".png";

			JMenuItem menuItem = new JMenuItem(name);
			menuItem.setActionCommand(cmd);
			menuItem.addActionListener(this);

			try { // image found
				menuItem.setIcon(new ImageIcon(imageLoc, name));
			} catch (Exception e) { // no image found
				System.err.println("Miss such image: " + imageLoc);
			}

			return menuItem;
		}

		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();

			// if view the data in the selected table
			if (cmd == commands[0]) {
				String tableName = ((TableNode) node).getName();
				((DBTreeView) LocalSchemaDBTree.this.parentComponent)
						.showTable(tableName);
			}
			// if view the schema of selected table
			else if (cmd.equals(commands[1])) {
				String tableName = ((TableNode) node).getName();
				((DBTreeView) LocalSchemaDBTree.this.parentComponent)
						.showSchema(tableName);
			}
			// if search local resource and other peers
			else if (cmd.equals(commands[2])) {
				// new
				// SearchDialog((ClientGUI)((DatabaseExplorer)((DBTreeView)DBTree.this.parentComponent).getParentComponent()).getParentComponent());
			} else if (cmd.equals(commands[3])) {
				Vector<Share_SchemaMapping_Info> ssmInfoSet = LocalSchemaDBTree.this
						.getS_sm_infoSet();

				DBTreeNode dbNode = (DBTreeNode) node;

				if (dbNode.getNodeType().equals("TABLE")) {
					DBTreeNode tNode = dbNode;

					if ((tNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
							|| (tNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE))
						return;

					if (tNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE) {
						Share_SchemaMapping_Info s_sm_info_table = new Share_SchemaMapping_Info();
						s_sm_info_table.setNodeType("TABLE");
						s_sm_info_table.setTableName(tNode
								.getSourceSchemaName());
						s_sm_info_table.setColumnName(null);
						s_sm_info_table.setSourceSchema(tNode
								.getSourceSchemaName());
						s_sm_info_table.setNewTargetSchema(null);
						s_sm_info_table.setOperationType("SHARE");
						s_sm_info_table
								.setOriginalStatus(DBTreeNode.SHARESTATUS_UNSHARE);

						ssmInfoSet.add(s_sm_info_table);
					} else {
						for (int i = 0; i < ssmInfoSet.size(); i++) {
							Share_SchemaMapping_Info ssmi = ssmInfoSet.get(i);

							if (ssmi.getNodeType().equals("TABLE")
									&& (ssmi.getTableName().equals(tNode
											.getSourceSchemaName()))) {
								ssmi.setOperationType("SHARE");
								break;
							}
						}
					}

					tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);

					int childCount = tNode.getChildCount();
					for (int i = 0; i < childCount; i++) {
						DBTreeNode cNode = (DBTreeNode) tNode.getChildAt(i);

						if (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE) {
							Share_SchemaMapping_Info s_sm_info_column = new Share_SchemaMapping_Info();
							s_sm_info_column.setNodeType("COLUMN");
							s_sm_info_column.setTableName(tNode
									.getSourceSchemaName());
							s_sm_info_column.setColumnName(cNode
									.getSourceSchemaName());
							s_sm_info_column.setSourceSchema(cNode
									.getSourceSchemaName());
							s_sm_info_column.setNewTargetSchema(null);
							s_sm_info_column.setOperationType("SHARE");
							s_sm_info_column
									.setOriginalStatus(DBTreeNode.SHARESTATUS_UNSHARE);
							ssmInfoSet.add(s_sm_info_column);
						} else {
							for (int j = 0; j < ssmInfoSet.size(); j++) {
								Share_SchemaMapping_Info ssmi = ssmInfoSet
										.get(j);

								if (ssmi.getNodeType().equals("COLUMN")
										&& (ssmi.getColumnName().equals(cNode
												.getSourceSchemaName()))) {
									ssmi.setOperationType("SHARE");
									break;
								}
							}
						}

						cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);

					}
				} else // Column share operation
				{
					DBTreeNode cNode = dbNode;
					DBTreeNode tNode = (DBTreeNode) (cNode.getParent());

					if ((cNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
							|| (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE))
						return;

					if (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE) {
						Share_SchemaMapping_Info s_sm_info_column = new Share_SchemaMapping_Info();
						s_sm_info_column.setNodeType("COLUMN");
						s_sm_info_column.setTableName(tNode
								.getSourceSchemaName());
						s_sm_info_column.setColumnName(cNode
								.getSourceSchemaName());
						s_sm_info_column.setSourceSchema(cNode
								.getSourceSchemaName());
						s_sm_info_column.setNewTargetSchema(null);
						s_sm_info_column.setOperationType("SHARE");
						s_sm_info_column
								.setOriginalStatus(DBTreeNode.SHARESTATUS_UNSHARE);
						ssmInfoSet.add(s_sm_info_column);
						cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);
					} else {
						for (int j = 0; j < ssmInfoSet.size(); j++) {
							Share_SchemaMapping_Info ssmi = ssmInfoSet.get(j);

							if (ssmi.getNodeType().equals("COLUMN")
									&& (ssmi.getColumnName().equals(cNode
											.getSourceSchemaName()))) {
								ssmi.setOperationType("SHARE");
								break;
							}
						}
					}

					if (tNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE) {
						tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);
						Share_SchemaMapping_Info s_sm_info_table = new Share_SchemaMapping_Info();
						s_sm_info_table.setNodeType("TABLE");
						s_sm_info_table.setTableName(tNode
								.getSourceSchemaName());
						s_sm_info_table.setColumnName(null);
						s_sm_info_table.setSourceSchema(tNode
								.getSourceSchemaName());
						s_sm_info_table.setNewTargetSchema(null);
						s_sm_info_table.setOperationType("SHARE");
						s_sm_info_table
								.setOriginalStatus(DBTreeNode.SHARESTATUS_UNSHARE);
						ssmInfoSet.add(s_sm_info_table);
						tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);
					}

				}

				parentComponent.repaint();
			}
			// if unshare a file or directory
			/*
			 * If unshared a table or column, then change the color of unshared
			 * table and column
			 */
			// Added by Han Xixian, 2008-6-3
			else if (cmd.equals(commands[4])) {
				Vector<String> vec = LocalSchemaDBTree.this
						.getTemporalShareInfo();
				Vector<Share_SchemaMapping_Info> ssmInfoSet = LocalSchemaDBTree.this
						.getS_sm_infoSet();

				DBTreeNode dbNode = (DBTreeNode) node;

				if (dbNode.getNodeType().equals("TABLE")) {
					DBTreeNode tNode = dbNode;

					if ((tNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE)
							|| (tNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPUNSHARE))
						return;

					if (tNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE) {
						for (int i = 0; i < ssmInfoSet.size(); i++) {
							Share_SchemaMapping_Info ssmi = ssmInfoSet.get(i);

							if (ssmi.getNodeType().equals("TABLE")
									&& (ssmi.getTableName().equals(tNode
											.getSourceSchemaName()))) {
								ssmi.setOperationType("UNSHARE");
								break;
							}
						}
					} else {
						Share_SchemaMapping_Info s_sm_info_table = new Share_SchemaMapping_Info();
						s_sm_info_table.setNodeType("TABLE");
						s_sm_info_table.setTableName(tNode
								.getSourceSchemaName());
						s_sm_info_table.setColumnName(null);
						s_sm_info_table.setSourceSchema(tNode
								.getSourceSchemaName());
						s_sm_info_table.setNewTargetSchema(null);
						s_sm_info_table.setOperationType("UNSHARE");
						s_sm_info_table
								.setOriginalStatus(DBTreeNode.SHARESTATUS_SHARE);

						ssmInfoSet.add(s_sm_info_table);
					}

					tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);

					int childCount = tNode.getChildCount();
					for (int i = 0; i < childCount; i++) {
						DBTreeNode cNode = (DBTreeNode) tNode.getChildAt(i);

						if ((cNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE)
								|| (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPUNSHARE))
							continue;

						if (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE) {
							for (int j = 0; j < ssmInfoSet.size(); j++) {
								Share_SchemaMapping_Info ssmi = ssmInfoSet
										.get(j);

								if (ssmi.getNodeType().equals("COLUMN")
										&& (ssmi.getColumnName().equals(cNode
												.getSourceSchemaName()))) {
									ssmi.setOperationType("UNSHARE");
									break;
								}
							}
						} else {
							String[] arrData = cNode.toString().split("->");

							Share_SchemaMapping_Info s_sm_info_column = new Share_SchemaMapping_Info();
							s_sm_info_column.setNodeType("COLUMN");
							s_sm_info_column.setTableName(tNode
									.getSourceSchemaName());
							s_sm_info_column.setColumnName(cNode
									.getSourceSchemaName());
							s_sm_info_column.setSourceSchema(cNode
									.getSourceSchemaName());
							s_sm_info_column.setNewTargetSchema(null);
							s_sm_info_column.setOldTargetSchema(arrData[1]);
							s_sm_info_column.setOperationType("UNSHARE");
							s_sm_info_column
									.setOriginalStatus(DBTreeNode.SHARESTATUS_SHARE);
							ssmInfoSet.add(s_sm_info_column);

							cNode.setUserObject(arrData[0]);
							this.updateUI();
						}
						cNode
								.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);

						vec.add("UNSHARE|COLUMN|" + tNode.getSourceSchemaName()
								+ "|" + cNode.getSourceSchemaName());
					}
				} else // Column Unshare Operation
				{
					DBTreeNode cNode = dbNode;
					DBTreeNode tNode = (DBTreeNode) (cNode.getParent());

					if ((cNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE)
							|| (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPUNSHARE))
						return;

					if (cNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE) {
						for (int i = 0; i < ssmInfoSet.size(); i++) {
							Share_SchemaMapping_Info ssmi = ssmInfoSet.get(i);

							if (ssmi.getNodeType().equals("COLUMN")
									&& (ssmi.getColumnName().equals(cNode
											.getSourceSchemaName()))) {
								ssmi.setOperationType("UNSHARE");
								break;
							}
						}
					} else {

						if (cNode.toString().indexOf("->") != -1) {
							String[] arrData = cNode.toString().split("->");

							Share_SchemaMapping_Info s_sm_info_column = new Share_SchemaMapping_Info();
							s_sm_info_column.setNodeType("COLUMN");
							s_sm_info_column.setTableName(tNode
									.getSourceSchemaName());
							s_sm_info_column.setColumnName(cNode
									.getSourceSchemaName());
							s_sm_info_column.setSourceSchema(cNode
									.getSourceSchemaName());
							s_sm_info_column.setNewTargetSchema(null);
							s_sm_info_column.setOldTargetSchema(arrData[1]);
							s_sm_info_column.setOperationType("UNSHARE");
							s_sm_info_column
									.setOriginalStatus(DBTreeNode.SHARESTATUS_SHARE);
							ssmInfoSet.add(s_sm_info_column);

							cNode.setUserObject(arrData[0]);
						} else {
							Share_SchemaMapping_Info s_sm_info_column = new Share_SchemaMapping_Info();
							s_sm_info_column.setNodeType("COLUMN");
							s_sm_info_column.setTableName(tNode
									.getSourceSchemaName());
							s_sm_info_column.setColumnName(cNode
									.getSourceSchemaName());
							s_sm_info_column.setSourceSchema(cNode
									.getSourceSchemaName());
							s_sm_info_column.setNewTargetSchema(null);
							s_sm_info_column.setOldTargetSchema(null);
							s_sm_info_column.setOperationType("UNSHARE");
							s_sm_info_column
									.setOriginalStatus(DBTreeNode.SHARESTATUS_SHARE);
							ssmInfoSet.add(s_sm_info_column);

						}
					}
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);

					int childCount = tNode.getChildCount();

					boolean shareTableNode = false;

					for (int i = 0; i < childCount; i++) {
						DBTreeNode ctNode = (DBTreeNode) tNode.getChildAt(i);

						if ((ctNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
								|| (ctNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE)) {
							shareTableNode = true;
							break;
						}
					}

					if (shareTableNode == false) {
						tNode
								.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);

						Share_SchemaMapping_Info s_sm_info_table = new Share_SchemaMapping_Info();
						s_sm_info_table.setNodeType("TABLE");
						s_sm_info_table.setTableName(tNode
								.getSourceSchemaName());
						s_sm_info_table.setColumnName(null);
						s_sm_info_table.setSourceSchema(cNode
								.getSourceSchemaName());
						s_sm_info_table.setNewTargetSchema(null);
						s_sm_info_table.setOperationType("UNSHARE");
						s_sm_info_table
								.setOriginalStatus(DBTreeNode.SHARESTATUS_SHARE);
						ssmInfoSet.add(s_sm_info_table);

						vec.add("UNSHARE|TABLE|" + tNode.getSourceSchemaName());
					}
				}

				parentComponent.repaint();
			}
			// if update index
			else if (cmd.equals(commands[5])) {

			}
			// if refresh currrent directory
			else if (cmd.equals(commands[6])) {
				DBTreeNode dbNode = (DBTreeNode) node;

				TreeNode[] arrTreeNode = dbNode.getPath();
				TreePath path = new TreePath(arrTreeNode);

				Rectangle bound = LocalSchemaDBTree.this.getPathBounds(path);

				String nodeBounds = "TreeNode Bounds: " + bound.x + ", "
						+ bound.y + ", " + bound.width + ", " + bound.height
						+ "\r\n";

				String dbtreeViewBounds = "DBTreeViewBounds: "
						+ LocalSchemaDBTree.this.getLocationOnScreen().x + ", "
						+ LocalSchemaDBTree.this.getLocationOnScreen().y;

				JOptionPane.showMessageDialog(null, nodeBounds
						+ dbtreeViewBounds);
			}
		}
	}

	/**
	 * @return the temporalShareInfo
	 */
	public Vector<String> getTemporalShareInfo() {
		return temporalShareInfo;
	}

	/**
	 * @param temporalShareInfo
	 *            the temporalShareInfo to set
	 */
	public void setTemporalShareInfo(Vector<String> temporalShareInfo) {
		this.temporalShareInfo = temporalShareInfo;
	}

	/**
	 * @return the schemaMappingInfo
	 */
	public Vector<String> getSchemaMappingInfo() {
		return schemaMappingInfo;
	}

	/**
	 * @param schemaMappingInfo
	 *            the schemaMappingInfo to set
	 */
	public void setSchemaMappingInfo(Vector<String> schemaMappingInfo) {
		this.schemaMappingInfo = schemaMappingInfo;
	}

	/**
	 * @return the s_sm_infoSet
	 */
	public Vector<Share_SchemaMapping_Info> getS_sm_infoSet() {
		return s_sm_infoSet;
	}

	/**
	 * @param set
	 *            the s_sm_infoSet to set
	 */
	public void setS_sm_infoSet(Vector<Share_SchemaMapping_Info> set) {
		s_sm_infoSet = set;
	}
}
