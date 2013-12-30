package sg.edu.nus.gui.test.peer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import sg.edu.nus.bestpeer.indexdata.RangeIndex;
import sg.edu.nus.dbconnection.DBIndex;
import sg.edu.nus.gui.dbview.DBTreeNode;
import sg.edu.nus.gui.dbview.ExportDBGlobalDBTree;
import sg.edu.nus.gui.dbview.ExportGlobalDBTreeView;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.util.MetaDataAccess;

/** 
 * ToolBar of ExportDBPanel
 * used to perform operation of exporting local database to external database
 * 
 * 
 * @author Han Xixian
 * @version August 7th 2008
 *
 */
public class ExportDBToolBar extends JToolBar implements ActionListener {
	// private member
	private static final long serialVersionUID = 9216101996763678645L;
	private ExportDBPanel clientgui;

	/* define buttons */
	private JButton btnPubIndex = null;
	private JButton btnDelIndex = null;
	private JButton btnReturn = null;

	/* define command names */
	private final String[] commands = { "ExportDBToolbar.PublishIndex",
			"ExportDBToolbar.DeleteIndex", "ExportDBToolbar.return", };

	/**
	 * Construct the tool bar.
	 * 
	 * @param gui the reference of the <code>ClientGUI</code>
	 */
	public ExportDBToolBar(ExportDBPanel gui) {
		this.clientgui = gui;
		this.addButtons();
		this.setFloatable(false);
		this.setRollover(true);
	}

	/**
	 * Initialize buttons of the tool bar.
	 */
	private void addButtons() {
		/* add buttons bellow */
		btnPubIndex = new JButton();
		btnPubIndex.setActionCommand(commands[0]);
		btnPubIndex.setText(LanguageLoader.getProperty(commands[0]));
		btnPubIndex.setToolTipText(LanguageLoader.getProperty(commands[0]));
		btnPubIndex.addActionListener(this);

		btnDelIndex = new JButton();
		btnDelIndex.setActionCommand(commands[1]);
		btnDelIndex.setText(LanguageLoader.getProperty(commands[1]));
		btnDelIndex.setToolTipText(LanguageLoader.getProperty(commands[1]));
		btnDelIndex.addActionListener(this);

		btnReturn = new JButton();
		btnReturn.setActionCommand(commands[2]);
		btnReturn.setText(LanguageLoader.getProperty(commands[2]));
		btnReturn.setToolTipText(LanguageLoader.getProperty(commands[2]));
		btnReturn.addActionListener(this);

		this.add(this.btnPubIndex);
		this.add(this.btnDelIndex);
		this.add(this.btnReturn);
	}

	/**
	 * Execute an action.
	 * 
	 * @param event the action event
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		if (cmd.equals(commands[0])) {
			
			publishIndex();
			
		} else if (cmd.equals(commands[1])) {

			deleteIndex();

		} else if (cmd.equals(commands[2])) {
			OperatePanel operatePanel = this.clientgui.getOperatePanel();

			DBExplorerPanel dbmanagerPanel = new DBExplorerPanel(operatePanel);

			operatePanel.setComponentAt(OperatePanel.TAB_DBMANAGER_INDEX,
					dbmanagerPanel);

		}
	}
	
	private void publishIndex(){
		try {
			// get the index that have been built already
			String sql = "select * from local_index";
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement idxstmt = conn.createStatement();
			ResultSet rs = idxstmt.executeQuery(sql);
			Vector<String> indexedColumn = new Vector<String>();
			while (rs.next()) {
				String cname = rs.getString("ind");
				String table = rs.getString("val");
				indexedColumn.add(table + "." + cname);
			}
			rs.close();
			// idxstmt.close();

			OperatePanel operatePanel = clientgui.getOperatePanel();
			ServerGUI gui = operatePanel.getServergui();
			ServerPeer serverpeer = gui.getServerpeer();

			ExportGlobalDBTreeView exportGlobalTreeView = clientgui
					.getExportGlobalTreeView();
			ExportDBGlobalDBTree dbTree = exportGlobalTreeView.getDbTree();

			Vector<String> listOfTables = new Vector<String>();

			Hashtable<String, Vector<RangeIndex>> rangeIndexInTables = new Hashtable<String, Vector<RangeIndex>>();

			Hashtable<String, Vector<String>> columnInTables = new Hashtable<String, Vector<String>>();

			DBTreeNode root = (DBTreeNode) dbTree.getModel().getRoot();

			int tablecount = root.getChildCount();

			for (int i = 0; i < tablecount; i++) {
				DBTreeNode tNode = (DBTreeNode) root.getChildAt(i);

				if (!tNode.isSelected())
					continue;

				tNode.setHasInsertIndex(true);

				String table = tNode.getSourceSchemaName().split("\\.")[1];
				listOfTables.add(table);

				int columncount = tNode.getChildCount();

				ArrayList<String> columns = new ArrayList<String>();

				Vector<RangeIndex> rangeIndexOfTable = new Vector<RangeIndex>();

				for (int j = 0; j < columncount; j++) {
					DBTreeNode cNode = (DBTreeNode) tNode.getChildAt(j);

					if (!cNode.isSelected())
						continue;

					cNode.setHasInsertIndex(true);

					String column = cNode.getSourceSchemaName()
							.split("\\.")[2];

					if (indexedColumn.contains(table + "." + column))
						continue;

					columns.add(column);
					if (columnInTables.containsKey(column)) {
						Vector<String> tables = columnInTables.get(column);
						tables.add(table);
					} else {
						Vector<String> tables = new Vector<String>();
						tables.add(table);
						columnInTables.put(column, tables);
					}

					// VHTam: indexing range...
					conn = ServerPeer.conn_exportDatabase;
					Statement stmt = conn.createStatement();
					sql = "select max(" + column + ") from " + table;
					rs = stmt.executeQuery(sql);
					rs.next();
					String maxVal = rs.getString(1);
					rs.close();
					sql = "select min(" + column + ") from " + table;
					rs = stmt.executeQuery(sql);
					rs.next();
					String minVal = rs.getString(1);
					rs.close();

					String columnType = MetaDataAccess.metaGetColumnType(
							ServerPeer.conn_metabestpeerdb, table, column);
					boolean isStringType = columnType.contains("varchar") || columnType.contains("text");
					int type = isStringType ? RangeIndex.STRING_TYPE
							: RangeIndex.NUMERIC_TYPE;
					RangeIndex rangeIndex = new RangeIndex(table, column,
							type, minVal, maxVal, serverpeer
									.getPhysicalInfo());

					rangeIndexOfTable.add(rangeIndex);
				}

				rangeIndexInTables.put(table, rangeIndexOfTable);

				for (int j = 0; j < columns.size(); j++) {
					sql = "insert into local_index values('"
							+ columns.get(j) + "','" + table + "')";
					idxstmt.addBatch(sql);
				}
				idxstmt.executeBatch();
			}

			idxstmt.close();

			if (listOfTables.size() == 0) {
				return;
			}

			if (serverpeer != null) {
				TreeNode[] treeNode = serverpeer.getTreeNodes();
				if (treeNode != null) {
					DBIndex dbIndex = new DBIndex(serverpeer, serverpeer
							.getPhysicalInfo(), treeNode[0]
							.getLogicalInfo(), listOfTables,
							rangeIndexInTables, columnInTables);
					dbIndex.indexDatabase();
				}
			}

			//deselect the interface check box
			for (int i = 0; i < tablecount; i++) {
				DBTreeNode tableNode = (DBTreeNode) root.getChildAt(i);

				if (tableNode.isSelected())
					tableNode.setSelected(false);

				int columnCount = tableNode.getChildCount();

				for (int j = 0; j < columnCount; j++) {
					DBTreeNode columnNode = (DBTreeNode) tableNode
							.getChildAt(j);

					if (columnNode.isSelected())
						columnNode.setSelected(false);
				}
			}

			dbTree.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteIndex(){
		try {
			// get the index that have been built already
			String sql = "select * from local_index";
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement idxstmt = conn.createStatement();
			ResultSet rs = idxstmt.executeQuery(sql);
			Vector<String> indexedColumn = new Vector<String>();
			while (rs.next()) {
				String cname = rs.getString("ind");
				String table = rs.getString("val");
				indexedColumn.add(table + "." + cname);
			}
			rs.close();

			OperatePanel operatePanel = clientgui.getOperatePanel();
			ServerGUI gui = operatePanel.getServergui();
			ServerPeer serverpeer = gui.getServerpeer();

			ExportGlobalDBTreeView exportGlobalTreeView = clientgui
					.getExportGlobalTreeView();
			ExportDBGlobalDBTree dbTree = exportGlobalTreeView.getDbTree();

			Vector<String> listOfTables = new Vector<String>();

			Hashtable<String, Vector<RangeIndex>> rangeIndexInTables = new Hashtable<String, Vector<RangeIndex>>();

			Hashtable<String, Vector<String>> columnInTables = new Hashtable<String, Vector<String>>();

			DBTreeNode root = (DBTreeNode) dbTree.getModel().getRoot();

			int tablecount = root.getChildCount();

			for (int i = 0; i < tablecount; i++) {
				DBTreeNode tNode = (DBTreeNode) root.getChildAt(i);

				if (!tNode.isSelected())
					continue;

				tNode.setHasInsertIndex(false);

				String table = tNode.getSourceSchemaName().split("\\.")[1];
				listOfTables.add(table);

				int columncount = tNode.getChildCount();

				ArrayList<String> columns = new ArrayList<String>();

				Vector<RangeIndex> rangeIndexOfTable = new Vector<RangeIndex>();

				for (int j = 0; j < columncount; j++) {
					DBTreeNode cNode = (DBTreeNode) tNode.getChildAt(j);

					if (!cNode.isSelected())
						continue;

					//different from publish
					cNode.setHasInsertIndex(false);

					String column = cNode.getSourceSchemaName()
							.split("\\.")[2];

					//only delete column that have been published
					if (indexedColumn.contains(table + "." + column)) {

						columns.add(column);
						if (columnInTables.containsKey(column)) {
							Vector<String> tables = columnInTables.get(column);
							tables.add(table);
						} else {
							Vector<String> tables = new Vector<String>();
							tables.add(table);
							columnInTables.put(column, tables);
						}

						// VHTam: indexing range...
						conn = ServerPeer.conn_exportDatabase;
						Statement stmt = conn.createStatement();
						sql = "select max(" + column + ") from " + table;
						rs = stmt.executeQuery(sql);
						rs.next();
						String maxVal = rs.getString(1);
						rs.close();
						sql = "select min(" + column + ") from " + table;
						rs = stmt.executeQuery(sql);
						rs.next();
						String minVal = rs.getString(1);
						rs.close();

						String columnType = MetaDataAccess.metaGetColumnType(
								ServerPeer.conn_metabestpeerdb, table, column);
						boolean isStringType = columnType.contains("varchar") || columnType.contains("text");
						int type = isStringType ? RangeIndex.STRING_TYPE
								: RangeIndex.NUMERIC_TYPE;
						
						RangeIndex rangeIndex = new RangeIndex(table, column,
								type, minVal, maxVal, serverpeer
										.getPhysicalInfo());

						rangeIndexOfTable.add(rangeIndex);
					}
				}

				rangeIndexInTables.put(table, rangeIndexOfTable);

				//delete column index info in local store
				for (int j = 0; j < columns.size(); j++) {
					sql = "delete from local_index where ind = '"
							+ columns.get(j) + "' and val = '" + table + "'";
					idxstmt.addBatch(sql);
				}
				idxstmt.executeBatch();
			}

			idxstmt.close();

			if (listOfTables.size() == 0) {
				return;
			}

			if (serverpeer != null) {
				TreeNode[] treeNode = serverpeer.getTreeNodes();
				if (treeNode != null) {
					DBIndex dbIndex = new DBIndex(serverpeer, serverpeer
							.getPhysicalInfo(), treeNode[0]
							.getLogicalInfo(), listOfTables,
							rangeIndexInTables, columnInTables);
					dbIndex.deleteIndex();
				}
			}

			//deselect the interface check box
			for (int i = 0; i < tablecount; i++) {
				DBTreeNode tableNode = (DBTreeNode) root.getChildAt(i);

				if (tableNode.isSelected())
					tableNode.setSelected(false);

				int columnCount = tableNode.getChildCount();

				for (int j = 0; j < columnCount; j++) {
					DBTreeNode columnNode = (DBTreeNode) tableNode
							.getChildAt(j);

					if (columnNode.isSelected())
						columnNode.setSelected(false);
				}
			}

			dbTree.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
