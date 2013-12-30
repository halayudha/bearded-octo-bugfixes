package sg.edu.nus.gui.test.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultTreeModel;

import sg.edu.nus.gui.dbview.DBTreeNode;
import sg.edu.nus.gui.dbview.GlobalSchemaDBTree;
import sg.edu.nus.gui.dbview.GlobalSchemaDBTreeView;
import sg.edu.nus.gui.dbview.LocalSchemaDBTree;
import sg.edu.nus.gui.dbview.LocalSchemaDBTreeView;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.request.ServerSchemaMatchingManager;
import sg.edu.nus.util.TableComparer;

/**
 * Implement the tool bar of the common peer's UI.
 * 
 * @author Han Xixian
 * @version 1.0 2008-7-13
 */

public final class DBExplorerToolbar extends JToolBar implements ActionListener {

	// private member
	private static final long serialVersionUID = 9216101996763678645L;
	private DBExplorerPanel clientgui;

	/* define buttons */
	private JButton btnConstructMapping = null;
	private JButton btnCancelMapping = null;
	private JButton btnExportDB = null;
	private JButton btnPublishIndex = null;
	private JButton btnClose = null;
	private JButton btnRecommendMapping = null;

	/**
	 * ServerSchemaMatchingManager is used for test
	 */
	ServerSchemaMatchingManager schemaMappingManager = null;

	/* define command names */
	private final String[] commands = { "DBExplorerToolbar.ConstructMapping",
			"DBExplorerToolbar.CancelMapping", "DBExplorerToolbar.ExportDB",
			"DBExplorerToolbar.PublishIndex", "DBExplorerToolbar.Close",
			"DBExplorerToolbar.RecommendMapping"};

	/**
	 * Construct the tool bar.
	 * 
	 * @param gui the reference of the <code>ClientGUI</code>
	 */
	public DBExplorerToolbar(DBExplorerPanel gui) {
		this.clientgui = gui;
		this.addButtons();
		this.setFloatable(false);
		this.setRollover(true);

		schemaMappingManager = new ServerSchemaMatchingManager(null);
	}

	/**
	 * Init buttons of the tool bar.
	 */
	private void addButtons() {
		/* add buttons bellow */
		btnConstructMapping = new JButton();
		btnConstructMapping.setActionCommand(commands[0]);
		btnConstructMapping.setText(LanguageLoader.getProperty(commands[0]));
		btnConstructMapping.setToolTipText(LanguageLoader
				.getProperty(commands[0]));
		btnConstructMapping.addActionListener(this);

		btnCancelMapping = new JButton();
		btnCancelMapping.setActionCommand(commands[1]);
		btnCancelMapping.setText(LanguageLoader.getProperty(commands[1]));
		btnCancelMapping.setToolTipText(LanguageLoader.getProperty(commands[1]));
		btnCancelMapping.addActionListener(this);

		btnExportDB = new JButton();
		btnExportDB.setActionCommand(commands[2]);
		btnExportDB.setText(LanguageLoader.getProperty(commands[2]));
		btnExportDB.setToolTipText(LanguageLoader.getProperty(commands[2]));
		btnExportDB.addActionListener(this);

		btnPublishIndex = new JButton();
		btnPublishIndex.setActionCommand(commands[3]);
		btnPublishIndex.setText(LanguageLoader.getProperty(commands[3]));
		btnPublishIndex.setToolTipText(LanguageLoader.getProperty(commands[3]));
		btnPublishIndex.addActionListener(this);

		btnClose = new JButton();
		btnClose.setActionCommand(commands[4]);
		btnClose.setText(LanguageLoader.getProperty(commands[4]));
		btnClose.setToolTipText(LanguageLoader.getProperty(commands[4]));
		btnClose.addActionListener(this);

		btnRecommendMapping = new JButton();
		btnRecommendMapping.setActionCommand(commands[5]);
		btnRecommendMapping.setText(LanguageLoader.getProperty(commands[5]));
		btnRecommendMapping.setToolTipText(LanguageLoader.getProperty(commands[5]));
		btnRecommendMapping.addActionListener(this);

		this.add(this.btnRecommendMapping);
		this.add(this.btnConstructMapping);
		this.add(this.btnCancelMapping);
		this.add(this.btnExportDB);
		// this.add(this.btnPublishIndex);
		this.add(this.btnClose);
	}

	/***
	 * Find DBTreeNode by globalPath
	 * @param globalTreeRoot Root of GlobalSchemaDBTree
	 * @param globalPath Specified format string
	 * @return DBTreeNode
	 */
	public DBTreeNode findDBTreeNode(DBTreeNode TreeRoot, String globalPath) {
		String[] arrPath = globalPath.split("\\.");

		if (TreeRoot == null)
			return null;

		if (arrPath.length == 2) {
			int tableCount = TreeRoot.getChildCount();

			for (int i = 0; i < tableCount; i++) {
				DBTreeNode tableNode = (DBTreeNode) TreeRoot.getChildAt(i);

				if (tableNode.getSourceSchemaName().equals(arrPath[1])) {
					return tableNode;
				}
			}
		} else if (arrPath.length == 3) {
			int tableCount = TreeRoot.getChildCount();

			for (int i = 0; i < tableCount; i++) {
				DBTreeNode tableNode = (DBTreeNode) TreeRoot.getChildAt(i);

				if (tableNode.getSourceSchemaName().equals(arrPath[1])) {
					int columnCount = tableNode.getChildCount();

					for (int j = 0; j < columnCount; j++) {
						DBTreeNode columnNode = (DBTreeNode) tableNode.getChildAt(j);

						if (columnNode.getSourceSchemaName().equals(arrPath[2]))
							return columnNode;
					}
				}
			}
		}

		return null;
	}

	/***
	 * Check current tableNode whether it should specify join condition
	 * @param globalTreeRoot
	 * @param tableNode
	 */
	public void CheckForJoin(DBTreeNode localDBTreeRoot,
			DBTreeNode globalTableNode) {
		/**
		 * vecTableName is used for storing table names 
		 * whose columns have schema mapping to tableNode
		 */
		Vector<String> vecTableName = new Vector<String>();

		int globalColumnCount = globalTableNode.getChildCount();

		for (int i = 0; i < globalColumnCount; i++) {
			DBTreeNode columnNode = (DBTreeNode) globalTableNode.getChildAt(i);

			for (int j = 0; j < columnNode.getTargetSchemaName().size(); j++) {
				String[] arrNode = columnNode.getTargetSchemaName().get(j).split("\\.");

				if (!vecTableName.contains(arrNode[1]))
					vecTableName.add(arrNode[1]);
			}
		}

		if (vecTableName.size() == 2) {
			DBTreeNode table1Node = this.findDBTreeNode(localDBTreeRoot,
					localDBTreeRoot.getSourceSchemaName() + "." + vecTableName.get(0));
			DBTreeNode table2Node = this.findDBTreeNode(localDBTreeRoot,
					localDBTreeRoot.getSourceSchemaName() + "." + vecTableName.get(1));

			Vector<String> columnOfT1 = new Vector<String>();
			int countOfT1 = table1Node.getChildCount();
			for (int i = 0; i < countOfT1; i++) {
				DBTreeNode columnNode = (DBTreeNode) table1Node.getChildAt(i);
				columnOfT1.add(columnNode.getSourceSchemaName());
			}

			Vector<String> columnOfT2 = new Vector<String>();
			int countOfT2 = table2Node.getChildCount();
			for (int i = 0; i < countOfT2; i++) {
				DBTreeNode columnNode = (DBTreeNode) table2Node.getChildAt(i);
				columnOfT2.add(columnNode.getSourceSchemaName());
			}

			SpecifyJoinConditionDlg dlg = new SpecifyJoinConditionDlg(columnOfT1, table1Node.getSourceSchemaName(), columnOfT2, table2Node.getSourceSchemaName());
			dlg.repaint();
		}
	}

	/**
	 * Execute an action.
	 * 
	 * @param event the action event
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		if (cmd.equals(commands[0])) {
			/**
			 * Get all component in DBExplorerPanel to perform processing
			 */
			LocalSchemaDBTreeView localDBTreeView = this.clientgui.getLocalDBTreeView();
			GlobalSchemaDBTreeView globalDBTreeView = this.clientgui.getGlobalDBTreeView();
			DBExplorerMappingPanel mappingPanel = this.clientgui.getMappingPanel();

			/**
			 * Get LocalSchemaDBTree and GlobalSchemaDBTree to perform tree operation
			 */
			LocalSchemaDBTree localDBTree = localDBTreeView.getDbTree();
			GlobalSchemaDBTree globalDBTree = globalDBTreeView.getDbTree();

			/**
			 * Two temporary DBTreeNode
			 */
			DBTreeNode localNode = null;
			DBTreeNode globalNode = null;

			try {
				/**
				 * DBTreeNode selected currently from 
				 * LocalSchemaDBTree and GlobalSchemaDBTree
				 */
				localNode = (DBTreeNode) localDBTree.getSelectionPath().getLastPathComponent();
				globalNode = (DBTreeNode) globalDBTree.getSelectionPath().getLastPathComponent();
			} catch (Exception e) {
				// JOptionPane.showMessageDialog(this,
				// "Please specify two columns");
				return;
			}

			/**
			 * If obtained DBTreeNode is null, then do nothing
			 */
			if (localNode == null)
				return;

			if (globalNode == null)
				return;

			/**
			 * If two DBTreeNodes are not "COLUMN" type, then do nothing
			 */
			if (!localNode.getNodeType().equals("COLUMN")
					|| !globalNode.getNodeType().equals("COLUMN")) {
				// JOptionPane.showMessageDialog(this.clientgui,
				// "Please specify two column!");
				return;
			}

			/**
			 * Obtain formatted string for schema mapping operation
			 * 
			 * The format is like: "DBName.TableName.ColumnName"
			 */
			DBTreeNode localTableNode = (DBTreeNode) localNode.getParent();
			DBTreeNode localRootNode = (DBTreeNode) localTableNode.getParent();
			String localPath = localRootNode.getSourceSchemaName() + "."
					+ localTableNode.getSourceSchemaName() + "."
					+ localNode.getSourceSchemaName();

			DBTreeNode globalTableNode = (DBTreeNode) globalNode.getParent();
			DBTreeNode globalRootNode = (DBTreeNode) globalTableNode
					.getParent();
			String globalPath = globalRootNode.getSourceSchemaName() + "."
					+ globalTableNode.getSourceSchemaName() + "."
					+ globalNode.getSourceSchemaName();

			/*
			 * Current DBTreeNode from LocalSchemaDBTree has no schema mapping
			 * Do it directly
			 */
			if (localNode.getTargetSchemaName().size() == 0) {
				localNode.getTargetSchemaName().add(globalPath);
				globalNode.getTargetSchemaName().add(localPath);

				localNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
				globalNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);

				this.schemaMappingManager.addMatch(localPath, globalPath);

				this.CheckForJoin(localRootNode, globalTableNode);

				localNode.setSelected(true);

				DBTreeNode tNode = (DBTreeNode) localNode.getParent();
				tNode.setSelected(true);
			}
			/*
			 * Current DBTreeNode from LocalSchemaDBTree has schema mapping
			 * Process it carefully
			 */
			else {
				String oldGlobalSchema = localNode.getTargetSchemaName().get(0);

				/**
				 * Current schema mapping has existed, do nothing
				 */
				if (oldGlobalSchema.equals(globalPath)) {
					return;
				}
				/**
				 * Change current schema mapping of DBTreeNode from LocalSchemaDBTree
				 */
				else {
					/**
					 * Get old schema mapping DBTreeNode from GlobalSchemaDBTree
					 */
					DBTreeNode oldSchemaMappingNode = findDBTreeNode(globalRootNode, oldGlobalSchema);

					if (oldSchemaMappingNode == null)
						return;

					/**
					 * Delete old schema mapping
					 */
					oldSchemaMappingNode.getTargetSchemaName().remove(localPath);
					mappingPanel.delSinglePoint(localPath, oldGlobalSchema, "LOCAL");
					mappingPanel.delSinglePoint(localPath, oldGlobalSchema, "GLOBAL");

					if (oldSchemaMappingNode.getTargetSchemaName().size() == 0)
						oldSchemaMappingNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);

					this.schemaMappingManager.removeMatch(localPath,oldGlobalSchema);

					/**
					 * Add new Schema mapping
					 */
					localNode.getTargetSchemaName().set(0, globalPath);
					globalNode.getTargetSchemaName().add(localPath);

					localNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
					globalNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);

					this.schemaMappingManager.addMatch(localPath, globalPath);
				}

			}

			this.clientgui.repaint();
		} else if (cmd.equals(commands[1])) {
			LocalSchemaDBTreeView localDBTreeView = this.clientgui.getLocalDBTreeView();
			GlobalSchemaDBTreeView globalDBTreeView = this.clientgui.getGlobalDBTreeView();
			DBExplorerMappingPanel mappingPanel = this.clientgui.getMappingPanel();

			LocalSchemaDBTree localDBTree = localDBTreeView.getDbTree();
			GlobalSchemaDBTree globalDBTree = globalDBTreeView.getDbTree();

			DBTreeNode localNode = null;
			DBTreeNode globalNode = null;

			try {
				localNode = (DBTreeNode) localDBTree.getSelectionPath().getLastPathComponent();
				globalNode = (DBTreeNode) globalDBTree.getSelectionPath().getLastPathComponent();
			} catch (Exception e) {
				// JOptionPane.showMessageDialog(this,
				// "Please specify two columns");
				return;
			}

			if (localNode == null)
				return;

			if (globalNode == null)
				return;

			DBTreeNode localTableNode = (DBTreeNode) localNode.getParent();
			DBTreeNode localRootNode = (DBTreeNode) localTableNode.getParent();
			String localPath = localRootNode.getSourceSchemaName() + "."
					+ localTableNode.getSourceSchemaName() + "."
					+ localNode.getSourceSchemaName();

			DBTreeNode globalTableNode = (DBTreeNode) globalNode.getParent();
			DBTreeNode globalRootNode = (DBTreeNode) globalTableNode
					.getParent();
			String globalPath = globalRootNode.getSourceSchemaName() + "."
					+ globalTableNode.getSourceSchemaName() + "."
					+ globalNode.getSourceSchemaName();

			if ((localNode.getTargetSchemaName().size() == 0)
					|| (globalNode.getTargetSchemaName().size() == 0)) {
				return;
			} else if (!localNode.getTargetSchemaName().get(0).equals(
					globalPath)) {
				return;
			} else {
				mappingPanel.delSinglePoint(localPath, globalPath, "LOCAL");
				mappingPanel.delSinglePoint(localPath, globalPath, "GLOBAL");

				localNode.getTargetSchemaName().clear();
				localNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);

				globalNode.getTargetSchemaName().remove(localPath);

				if (globalNode.getTargetSchemaName().size() == 0)
					globalNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);

				this.schemaMappingManager.removeMatch(localPath, globalPath);

				Connection conn = ServerPeer.conn_schemaMapping;

				try {
					Statement stmt = conn.createStatement();
					String delSQL = "delete from joincondition where ("
							+ "table1='" + localTableNode.getSourceSchemaName()
							+ "'" + " and column1='"
							+ localNode.getSourceSchemaName() + "') " + "or("
							+ "table2='" + localTableNode.getSourceSchemaName()
							+ "'" + " and column2='"
							+ localNode.getSourceSchemaName() + "')";

					stmt.executeUpdate(delSQL);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				localNode.setSelected(false);

				DBTreeNode tNode = (DBTreeNode) localNode.getParent();
				int childnum = tNode.getChildCount();

				DBTreeNode cNode = null;

				for (int i = 0; i < childnum; i++) {
					cNode = (DBTreeNode) tNode.getChildAt(i);
					if (cNode.isSelected())
						return;
				}

				tNode.setSelected(false);

			}

			this.clientgui.repaint();
		}
		/* if export data */
		else if (cmd.equals(commands[2])) {
			OperatePanel operatePanel = this.clientgui.getOperatePanel();

			ExportDBPanel exportDBPanel = new ExportDBPanel(operatePanel);
			operatePanel.setComponentAt(OperatePanel.TAB_DBMANAGER_INDEX, exportDBPanel);
		} else if (cmd.equals(commands[3])) {

		} else if (cmd.equals(commands[4])) {
			OperatePanel operatePanel = this.clientgui.getOperatePanel();

			DBManagerPanel dbmanagerPanel = new DBManagerPanel(operatePanel);

			operatePanel.setComponentAt(OperatePanel.TAB_DBMANAGER_INDEX, dbmanagerPanel);

		}
		// added by Bing Tian and Zhenjie -- begin
		else if (cmd.equals(commands[5])) {
			/**
			 * Get all component in DBExplorerPanel to perform processing
			 */
			LocalSchemaDBTreeView localDBTreeView = this.clientgui.getLocalDBTreeView();
			GlobalSchemaDBTreeView globalDBTreeView = this.clientgui.getGlobalDBTreeView();
			// DBExplorerMappingPanel mappingPanel = this.clientgui.getMappingPanel();

			/**
			 * Get LocalSchemaDBTree and GlobalSchemaDBTree
			 */
			LocalSchemaDBTree localDBTree = localDBTreeView.getDbTree();
			GlobalSchemaDBTree globalDBTree = globalDBTreeView.getDbTree();

			/**
			 * Get LocalSchemaDBTree root and GlobalSchemaDBTree root
			 */
			DBTreeNode localRoot = (DBTreeNode) localDBTree.getModel().getRoot();

			DBTreeNode selection = (DBTreeNode) globalDBTree.getSelectionPath().getLastPathComponent();

			if (!selection.getNodeType().equals("TABLE")) {
				// JOptionPane.showMessageDialog(this.clientgui,
				// "Please specify a global table");
				return;
			}

			TableComparer tc = new TableComparer(localRoot, selection);
			int size = localRoot.getChildCount();
			DBTreeNode[] childList = new DBTreeNode[localRoot.getChildCount()];
			for (int i = 0; i < size; i++) {
				childList[i] = (DBTreeNode) localRoot.getChildAt(0);
				localRoot.remove(0);
			}
			for (int i = 0; i < size; i++) {
				//childList[tc.get_index(i)].addScore(tc.get_score(i));
				localRoot.add(childList[tc.get_index(i)]);
			}
			DefaultTreeModel model = new DefaultTreeModel(localRoot);
			localDBTree.setModel(model);
			this.clientgui.repaint();
		}
		// added by Bing Tian and Zhenjie -- end
	}
}
