package sg.edu.nus.gui.test.peer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import sg.edu.nus.db.synchronizer.DataExport;
import sg.edu.nus.db.synchronizer.GlobalSchema;
import sg.edu.nus.gui.customcomponent.ProgressDialog;
import sg.edu.nus.gui.dbview.DBTreeNode;
import sg.edu.nus.gui.dbview.ExportDBGlobalDBTree;
import sg.edu.nus.gui.dbview.ExportDBLocalDBTree;
import sg.edu.nus.gui.dbview.ExportGlobalDBTreeView;
import sg.edu.nus.gui.dbview.ExportLocalDBTreeView;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * This panel hold buttons which are used to operate data export.
 * 
 * @author Han Xixian
 * 
 * @version 2008-8-7
 * 
 */
public class ExportDBButtonPanel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4820710620874711414L;
	/**
	 * The button is used for export data from database inside firewall to
	 * database outside the firewall.
	 */
	private JButton btnRight = null;

	private ImageIcon rightIcon = null;

	private ExportDBPanel exportDBPanel;

	private String[] commands = { "export out", "export in" };


	public ExportDBButtonPanel(ExportDBPanel exportDBPanel) {
		super();

		this.exportDBPanel = exportDBPanel;

		initPane();
	}

	public void initPane() {
		this.rightIcon = new ImageIcon("./sg/edu/nus/gui/res/btnRight.png");

		this.btnRight = new JButton(this.rightIcon);

		this.btnRight.setActionCommand(commands[0]);

		this.btnRight.addActionListener(this);

		this.btnRight.setPreferredSize(new Dimension(45, 22));

		Box vbox = Box.createVerticalBox();
		vbox.add(Box.createVerticalStrut(200));
		vbox.add(this.btnRight);

		vbox.setPreferredSize(new Dimension(45, 600));

		this.add(vbox);

		this.setPreferredSize(new Dimension(50, 600));
	}

	/**
	 * Read all join condition tuple to GlobalSchema
	 * 
	 * @param gschema
	 */
	public void LoadJoinCondition(GlobalSchema gschema) {
		Connection conn = ServerPeer.conn_schemaMapping;

		try {
			Statement stmt = conn.createStatement();

			String sql = "select * from joincondition";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String table1Name = rs.getString("table1");
				String column1Name = rs.getString("column1");
				String table2Name = rs.getString("table2");
				String column2Name = rs.getString("column2");

				gschema.insertJoinRelation("localdb", table1Name, table2Name,
						column1Name, column2Name);
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Button click event processing function
	 */
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if (command.equals(commands[0])) {

			try {

				ProgressDialog progressDialog = new ProgressDialog(
						LanguageLoader

						.getProperty("title.exportingData"));

				progressDialog.setVisible(true);

				ExportLocalDBTreeView exportdb_localDBTreeView = this.exportDBPanel

				.getExportLocalTreeView();

				ExportDBLocalDBTree exportdbdb_localDBTree = exportdb_localDBTreeView

				.getDbTree();

				DBTreeNode root = (DBTreeNode) exportdbdb_localDBTree

				.getModel().getRoot();

				int tableCount = root.getChildCount();

				Vector<String> tables = new Vector<String>();

				Hashtable<String, Vector<String>> columnsOfTable = new Hashtable<String, Vector<String>>();

				for (int i = 0; i < tableCount; i++) {

					DBTreeNode tableNode = (DBTreeNode) root.getChildAt(i);

					if (!tableNode.isSelected())

						continue;

					String tableName = tableNode.getSourceSchemaName().split(
							"\\.")[1];

					tables.add(tableName);

					int columnCount = tableNode.getChildCount();

					Vector<String> tableColumns = new Vector<String>();

					for (int j = 0; j < columnCount; j++) {

						DBTreeNode columnNode = (DBTreeNode) tableNode

						.getChildAt(j);

						if (!columnNode.isSelected())

							continue;

						String colName = columnNode.getSourceSchemaName()
								.split("\\.")[2];

						tableColumns.add(colName);

					}

					columnsOfTable.put(tableName, tableColumns);

				}

				// first store info to database for later periodically export

				MetaDataAccess.metaInsertLocalTableExported(
						ServerPeer.conn_metabestpeerdb, tables);

				MetaDataAccess.metaInsertLocalColumnsExported(
						ServerPeer.conn_metabestpeerdb, columnsOfTable);

				// export data...

				DataExport.exportData(tables, columnsOfTable);

				// ///////////////////////////

				// update interface of dbtree

				Connection conn2 = ServerPeer.conn_exportDatabase;

				DatabaseMetaData dbmd = conn2.getMetaData();

				ExportGlobalDBTreeView exportdb_globalDBTreeView = this.exportDBPanel

				.getExportGlobalTreeView();

				ExportDBGlobalDBTree exportdbdb_globalDBTree = exportdb_globalDBTreeView

				.getDbTree();

				exportdbdb_globalDBTree.setModel(null);

				exportdbdb_globalDBTree.setupTree(dbmd, ServerPeer.EXPORTED_DB);

				for (int i = 0; i < tableCount; i++) {

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

				progressDialog.setVisible(false);

			} catch (Exception event) {

				event.printStackTrace();

			}

		} else if (command.equals(commands[1])) {

		}
	}
}
