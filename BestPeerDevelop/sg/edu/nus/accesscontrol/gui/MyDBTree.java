package sg.edu.nus.accesscontrol.gui;

import java.awt.Component;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import sg.edu.nus.gui.GuiHelper;
import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.util.MetaDataAccess;

public class MyDBTree extends JTree implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5428767693581724380L;
	MyJSelectionTable tableSelectionColumn = null;
	String selectedTableName = null;
	Connection conn = null;

	public MyDBTree(Connection conn) {

		this.conn = conn;

		// add customized TreeCellRenderer
		this.setCellRenderer(new TreeCellRenderer());

		addTreeSelectionListener(this);
	}

	public void setTableSelectionColumn(MyJSelectionTable tableSelectionColumn) {
		this.tableSelectionColumn = tableSelectionColumn;
	}

	public String getSelectedTableName() {
		return selectedTableName;
	}

	public void valueChanged(TreeSelectionEvent e) {

		TreePath path = this.getSelectionPath();
		if (path == null)
			return;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();

		if (node.toString().contains("TableNode")) {

			if (tableSelectionColumn != null) {
				String tableName = ((TableNode) node).getName();
				String[][] columns = MetaDataAccess.metaGetColumns(conn,
						tableName);

				selectedTableName = tableName;

				tableSelectionColumn.setTableData(columns);
			}
		} else {
			String[][] columns = {};
			tableSelectionColumn.setTableData(columns);
		}
	}

	public void setupTree(DatabaseMetaData metadata, DefaultMutableTreeNode root)
			throws SQLException {
		ResultSet tables, columns;
		String[] tableTypes = { "TABLE" };

		tables = metadata.getTables(null, null, null, tableTypes);

		String tableName;
		TableNode tableNode;
		ColumnNode columnNode;
		// retrieval all the table information from the resultset
		while (tables.next()) {
			tableName = tables.getString("TABLE_NAME");

			tableNode = new TableNode(tableName);

			root.add(tableNode);

			columns = metadata.getColumns(null, null, tableName, null);

			while (columns.next()) {
				columnNode = new ColumnNode(columns.getString("COLUMN_NAME"));

				tableNode.add(columnNode);

			}

			columns.close();
		}
		tables.close();

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);

		// rem
	}

	DefaultMutableTreeNode root = null;
	String[][] data = null;
	int nodeIndex = 0;

	public void setRootNode(DefaultMutableTreeNode root) {
		this.root = root;
	}

	public void setNumberFirstLevelChild(int n) {
		data = new String[n][];
		nodeIndex = 0;
	}

	public void buildTree() {
		setupTree(data, root);
	}

	public void setFirstLevelChildData(String nodeName, String[] nodeData) {

		data[nodeIndex] = new String[nodeData.length + 1];

		data[nodeIndex][0] = nodeName;

		for (int i = 1; i <= nodeData.length; i++) {
			data[nodeIndex][i] = nodeData[i - 1];
		}

		nodeIndex++;
	}

	public void setupTree(Object[][] data, DefaultMutableTreeNode root) {
		TableNode tableNode;
		ObjectNode objNode;
		// retrieval all the table information from the resultset
		for (int i = 0; i < data.length; i++) {
			objNode = new ObjectNode((String) data[i][0]);
			String type = null;
			if (i == 0) {
				type = "Table";

			} else {
				type = "View";
			}

			objNode.type = type;

			root.add(objNode);

			for (int j = 1; j < data[i].length; j++) {
				tableNode = new TableNode((String) data[i][j]);
				tableNode.type = type;

				objNode.add(tableNode);

			}

		}
		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);

	}

	class ObjectNode extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1729422365832193879L;
		private String name;
		String type = "";

		public ObjectNode(String nodeName) {
			super(nodeName);
			this.name = nodeName;

		}

		public String getName() {
			return name;
		}

		public void setName(String nodeName) {
			this.name = nodeName;
		}

		public String toString() {
			return "ObjectNode";
		}

	}

	class TableNode extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8408513512565185963L;

		// the name of the table
		private String tableName;

		String type = "";

		/**
		 * Constructor
		 * 
		 * @param tableName -  the name of the table
		 */
		public TableNode(String tableName) {
			super(tableName);
			this.tableName = tableName;
		}

		/**
		 * Return the name of the table
		 * 
		 * @return name of table
		 */
		public String getName() {
			return tableName;
		}

		/**
		 * Set the string value as the name of table
		 * 
		 * @param tableName - the name of table
		 */
		public void setName(String tableName) {
			this.tableName = tableName;
		}

		/**
		 * Return a string: "TableNode".
		 * 
		 * @return "TableNode"
		 */
		public String toString() {
			return "TableNode";
		}
	}

	class ColumnNode extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2804184387103296032L;
		// the name of the column
		private String columnName;

		/**
		 * Constructor
		 * 
		 * @param columnName -  the name of the column
		 */
		public ColumnNode(String columnName) {
			super(columnName);
			this.columnName = columnName;
		}

		/**
		 * Return the name of the column
		 * 
		 * @return name of column
		 */
		public String getName() {
			return columnName;
		}

		/**
		 * Set the string value as the name of column
		 * 
		 * @param columnName - the name of column
		 */
		public void setName(String columnName) {
			this.columnName = columnName;
		}

		/**
		 * Return a string: "ColumnNode".
		 * 
		 * @return "ColumnNode"
		 */
		public String toString() {
			return "ColumnNode";
		}

	}

	class TreeCellRenderer extends DefaultTreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6447345558047094398L;

		/**
		 * Rewrite the <code>getTreeCellRendererComponent</code> function in the DefaultTreeCellRenderer class
		 * to set the custemized image for different kinds of tree node in the <code>DBTree</code>.
		 * 
		 * @return the Component that the renderer uses to draw the value 
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded,
					leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			String imgId = null;

			this.setTextNonSelectionColor(GuiLoader.contentLineColor);

			this.setBackgroundSelectionColor(GuiLoader.selectionBkColor);
			this.setTextSelectionColor(GuiLoader.contentTextColor);
			this.setBorderSelectionColor(GuiLoader.contentTextColor);
			this.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

			if (node.toString().equals("TableNode")) {
				imgId = ("image_leaf_node");
				this.setText(((TableNode) node).getName());
			} else if (value.toString().equals("ColumnNode")) {
				imgId = ("image_column_node");
				this.setText(((ColumnNode) node).getName());
			} else if (value.toString().equals("ObjectNode")) {
				String name = ((ObjectNode) node).type.toLowerCase();
				if (name.indexOf("table") != -1) {
					imgId = "image_table_node";
				} else if (name.indexOf("view") != -1) {
					imgId = "image_view_node";
				} else {
					imgId = ("image_object_node");
				}
				this.setText(((ObjectNode) node).getName());
			} else {
				imgId = ("image_dbobjects_node");
			}

			ImageIcon icon = GuiHelper.getIcon(imgId);

			// set the image icon to render the node

			this.setIcon(icon);

			return this;
		}
	}

}
