package sg.edu.nus.gui.bootstrap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import sg.edu.nus.gui.DynamicTreePanel;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Panel for editing global schema
 * @author VHTam
 *
 */
public class SchemaTreePanel extends JPanel implements ActionListener,
		TreeSelectionListener {

	private static final long serialVersionUID = -1737802199212366111L;

	private BootstrapGUI bootstrapGUI;

	private final String ADD_TABLE_COMMAND = "add_table";
	private final String REMOVE_COMMAND = "remove";
	private final String DISTRIBUTE_COMMAND = "distribute";
	public static final String TABLE = "table";
	public static final String COLUMN = "column";

	private String btnAddCaption = LanguageLoader
			.getProperty("button.addTable");
	private String btnRemoveCaption = LanguageLoader
			.getProperty("button.deleteTable");

	String addTable = LanguageLoader.getProperty("button.addTable");
	String deleteTable = LanguageLoader.getProperty("button.deleteTable");
	String addColumn = LanguageLoader.getProperty("button.addColumn");
	String deleteColumn = LanguageLoader.getProperty("button.deleteColumn");

	private DynamicTree treePanel;
	JButton addTableButton = null;
	JButton addColumnButton = null;
	JButton removeButton = null;

	public SchemaTreePanel(BootstrapGUI gui) {

		super(new BorderLayout());

		bootstrapGUI = gui;

		// Create the components.
		treePanel = new DynamicTree(MetaDataAccess
				.metaGetCorporateDbName(AbstractPeer.conn_bestpeerdb));

		treePanel.addTreeListener(this);
		treePanel.setCellRender(new TreeCellRenderer());

		populateTree(treePanel);

		addTableButton = new JButton(btnAddCaption);
		addTableButton.setActionCommand(ADD_TABLE_COMMAND);
		addTableButton.addActionListener(this);

		removeButton = new JButton(btnRemoveCaption);
		removeButton.setActionCommand(REMOVE_COMMAND);
		removeButton.addActionListener(this);
		removeButton.setEnabled(false);

		JButton distributeButton = new JButton(LanguageLoader
				.getProperty("button.distributeGlobalSchema"));
		distributeButton.setActionCommand(DISTRIBUTE_COMMAND);
		distributeButton.addActionListener(this);

		// Lay everything out.
		treePanel.setPreferredSize(new Dimension(300, 150));
		add(treePanel, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		btnPanel.add(addTableButton);
		btnPanel.add(removeButton);

		btnPanel.add(distributeButton);

		add(btnPanel, BorderLayout.SOUTH);
	}

	public void valueChanged(TreeSelectionEvent e) {

		TreePath path = treePanel.getSelectionPath();
		if (path == null)
			return;

		int level = path.getPathCount();

		updateAddBtnCaption(level);
	}

	public void populateTree(DynamicTree treePanel) {
		String[] tables = MetaDataAccess
				.metaGetTables(AbstractPeer.conn_bestpeerdb);
		if (tables != null) {
			for (int i = 0; i < tables.length; i++) {
				String tableName = tables[i];
				DefaultMutableTreeNode tableNode = treePanel.addObject(null,
						tableName);
				String[][] columnWithType = MetaDataAccess
						.metaGetColumnsWithType(AbstractPeer.conn_bestpeerdb,
								tableName);
				for (int j = 0; j < columnWithType.length; j++) {
					String colNodeName = columnWithType[j][0] + " : "
							+ columnWithType[j][1];
					treePanel.addObject(tableNode, colNodeName);
				}
			}
		}

	}

	public void updateAddBtnCaption(int selectedLevel) {

		if (selectedLevel == 1) {
			btnAddCaption = addTable;
			removeButton.setEnabled(false);
		} else if (selectedLevel == 2) {
			btnAddCaption = addColumn;
			btnRemoveCaption = deleteTable;
			removeButton.setEnabled(true);
		} else {
			btnAddCaption = addColumn;
			btnRemoveCaption = deleteColumn;
			removeButton.setEnabled(true);
		}

		addTableButton.setText(btnAddCaption);
		removeButton.setText(btnRemoveCaption);

	}

	private String getStringFromUser(String command, String title){
		String s = JOptionPane.showInputDialog(this,command, title, JOptionPane.INFORMATION_MESSAGE);
		if (s==null){
			return null;
		}
		s = s.toLowerCase();
		
		if (isKeyword(s)){
			return getStringFromUser(command, title);
		}
		return s;
	}
	
	private boolean isKeyword(String s) {
		String[] sqlKeywords = new String[] { "table", "from", "where",
				"order", "group", "by", "select", "create", "desc", "asc",
				"int", "float", "double", "varchar" };
		for (int i = 0; i < sqlKeywords.length; i++) {
			if (s.equals(sqlKeywords[i])) {
				JOptionPane.showMessageDialog(this, "Illeagal name: match with SQL keyword. Please input again!");
				return true;
			}
		}
		return false;
	}
	
	private boolean alreadyExist(String name){
		String[] tables = MetaDataAccess.metaGetTables(AbstractPeer.conn_bestpeerdb);
		if (tables == null){
			for (int i=0; i<tables.length; i++){
				if (name.equals(tables[i]))
					return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (ADD_TABLE_COMMAND.equals(command)) {
			// Add button clicked
			TreePath currentSelection = treePanel.getSelectionPath();
			String nodeName = "";

			if (currentSelection == null) {

				Vector nodes = treePanel.getNodesAtLevel(1);
				DefaultMutableTreeNode dbName = (DefaultMutableTreeNode) nodes
						.get(0);
				nodeName = getStringFromUser("Please input table name:", "Add new table");
				if (nodeName != null) {
					treePanel.addObject(dbName, nodeName, true);
					MetaDataAccess.metaAddNewTable(
							AbstractPeer.conn_bestpeerdb, nodeName);
				}

				return;
			}

			int levelSelected = currentSelection.getPathCount();

			if (levelSelected == 1) {
				
				nodeName = getStringFromUser("Please input table name:", "Add new table");
				
				if (nodeName != null) {
					treePanel.addObject(nodeName);
					MetaDataAccess.metaAddNewTable(
							AbstractPeer.conn_bestpeerdb, nodeName);
				}

			} else if (levelSelected == 2) {
				DlgSchemaAddColumn dlg = new DlgSchemaAddColumn(null,
						"Adding column", true);
				dlg.setVisible(true);
				String colName = dlg.getColumnName();
				String colType = dlg.getColumnType();

				DefaultMutableTreeNode tableNode = treePanel.getSelectedNode();
				String tableName = tableNode.toString();

				if (colName != null) {
					colName = colName.toLowerCase();
					if (isKeyword(colName)){
						return;
					}
					nodeName = colName + " : " + colType;
					treePanel.addObject(nodeName);

					MetaDataAccess.metaAddNewColumn(
							AbstractPeer.conn_bestpeerdb, tableName, colName,
							colType);
				}

			} else { // level == 3
				DlgSchemaAddColumn dlg = new DlgSchemaAddColumn(null,
						"Adding column", true);
				dlg.setVisible(true);
				String colName = dlg.getColumnName();
				String colType = dlg.getColumnType();
				if (colName != null) {
					colName = colName.toLowerCase();
					if (isKeyword(colName)){
						return;
					}
					nodeName = colName + " : " + colType;
					DefaultMutableTreeNode parentSelected = treePanel
							.getParentSelectedNode();
					String tableName = parentSelected.toString();
					treePanel.addObject(parentSelected, nodeName);

					MetaDataAccess.metaAddNewColumn(
							AbstractPeer.conn_bestpeerdb, tableName, colName,
							colType);
				}

				return;
			}

		} else if (REMOVE_COMMAND.equals(command)) {
			// Remove button clicked
			treePanel.removeCurrentNode();
			
			MetaDataAccess.updateSchema(AbstractPeer.conn_bestpeerdb, getSchemaString());

		} else if (DISTRIBUTE_COMMAND.equals(command)) {

			int result = JOptionPane.showConfirmDialog(bootstrapGUI,
					"Would you like to send this schema to peers?",
					"Schema change confirmation", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {

				String schemaSql = getSchemaString();

				System.out.println("bootstrap peer send schema!");
				System.out.println(schemaSql);

				MetaDataAccess.updateSchema(AbstractPeer.conn_bestpeerdb,
						schemaSql);

				bootstrapGUI.sendSchema(schemaSql);

			}

		}
	}

	@SuppressWarnings("unchecked")
	public String getSchemaString() {
		Vector tables = treePanel.getNodesAtLevel(2);
		String schemaSql = "";

		for (int i = 0; i < tables.size(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tables
					.get(i);
			String tableName = node.toString();
			schemaSql += "CREATE " + MetaDataAccess.KEYWORD_TABLE + tableName
					+ " ( ";

			Vector columns = treePanel.getChildsOfNodes(node);

			for (int j = 0; j < columns.size(); j++) {
				DefaultMutableTreeNode col = (DefaultMutableTreeNode) columns
						.get(j);
				String[] columnProperty = col.toString().split(":");
				String colName = columnProperty[0];
				String colType = columnProperty[1];
				if (j < columns.size() - 1) {
					schemaSql += colName + " " + colType + ", ";
				} else {
					schemaSql += colName + " " + colType;
				}
			}

			schemaSql += " )" + MetaDataAccess.SCHEMA_SEPERATOR; // begin new
			// table

		}

		return schemaSql;
	}

	class TreeCellRenderer extends DefaultTreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9114016636139683601L;

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

			int level = DynamicTreePanel.getLevelOfNode(tree, node);

			String imgPath = null;

			if (level == 1) {
				imgPath = "./sg/edu/nus/res/dbSymbol.png";
			} else if (level == 2) {
				imgPath = "./sg/edu/nus/res/tableNode.png";
			} else {
				imgPath = "./sg/edu/nus/res/leafNode.png";
			}
			//
			ImageIcon icon = new ImageIcon(imgPath);

			// set the image icon to render the node

			this.setIcon(icon);

			return this;
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("DynamicTreeDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		SchemaTreePanel newContentPane = new SchemaTreePanel(null);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
