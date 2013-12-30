package sg.edu.nus.gui.dbview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sg.edu.nus.gui.test.peer.ExportDBPanel;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

public class ExportLocalDBTreeView extends JPanel {
	// private members
	private static final long serialVersionUID = 3609575253483334882L;

	private Component parentComponent;

	// the customized tree to show the tables in the connected database.
	private ExportDBLocalDBTree dbTree;

	private ExportDBPanel exportDBGui;

	/**
	 * Constructor
	 * 
	 * @param parent - the parent container that hold this panel.
	 *                 the parent container must be <code>DatabaseExplorer</code> instance.
	 */
	public ExportLocalDBTreeView(ExportDBPanel gui,
			Component/*DatabaseExplorer*/parent) {
		this.exportDBGui = gui;
		this.parentComponent = parent;

		try {
			this.initPane();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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

		String databaseName = ServerPeer.erp_db.getDbName();

		Connection conn = ServerPeer.conn_localSchema;

		DatabaseMetaData dbmetadata = conn.getMetaData();

		dbTree = new ExportDBLocalDBTree(this);
		dbTree.setupTree(dbmetadata, databaseName);

		JScrollPane scrollPane = new JScrollPane(dbTree);

		this.add(scrollPane, BorderLayout.CENTER);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.localsharedatabase")));

		this.setPreferredSize(new Dimension(400, 600));
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

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/**
	 * @return the dbTree
	 */
	public ExportDBLocalDBTree getDbTree() {
		return dbTree;
	}

	/**
	 * @param dbTree the dbTree to set
	 */
	public void setDbTree(ExportDBLocalDBTree dbTree) {
		this.dbTree = dbTree;
	}

	/**
	 * @return the dbmanagerGui
	 */
	public ExportDBPanel getExportDBGui() {
		return exportDBGui;
	}

	/**
	 * @param dbmanagerGui the dbmanagerGui to set
	 */
	public void setDbmanagerGui(ExportDBPanel exportDBGui) {
		this.exportDBGui = exportDBGui;
	}
}
