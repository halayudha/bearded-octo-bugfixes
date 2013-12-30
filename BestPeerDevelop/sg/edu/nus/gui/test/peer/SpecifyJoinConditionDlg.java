package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

/**
 * This class is used under the condition that
 * when two tables from local schema perform
 * schema mapping to single global schema, then
 * SpecifyJoinConditionDlg is invoked to ask user
 * to specify join condition
 * 
 * @author Han Xixian
 * 
 * @version August 12 2008
 *
 */

public class SpecifyJoinConditionDlg extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1570551575505937646L;
	/**
	 * JLabel used to describe information
	 */
	private JLabel lbTable1Name = null;
	private JLabel lbJoinCondition = null;
	private JLabel lbTable2Name = null;

	/**
	 * Record joined table Name
	 */
	private String strTable1 = null;
	private String strTable2 = null;

	/**
	 * jcbSourceColumn is to list the source columns
	 */
	private JComboBox jcbColumn1 = null;

	/**
	 * jcbJoinCondition is to list the join condition
	 */
	private JComboBox jcbJoinCondition = null;

	/**
	 * jcbTargetColumn is to list target columns
	 */
	private JComboBox jcbColumn2 = null;

	/**
	 * btnSave is to save the join condition
	 */
	private JButton btnSave = null;

	/**
	 * jcbPanel is the JPanel to hold JComboBox
	 */
	private JPanel jcbPanel = null;

	/**
	 * Constructor of this class, make this dialog model dialog
	 * 
	 * @param sourceColumn
	 * @param table1Name
	 * @param targetColumn
	 * @param table2Name
	 *  
	 */
	public SpecifyJoinConditionDlg(Vector<String> sourceColumn,
			String table1Name, Vector<String> targetColumn, String table2Name) {
		super();

		this.setModal(true);

		/**
		 * Keep table name to be joined
		 */
		this.strTable1 = table1Name;
		this.strTable2 = table2Name;

		/**
		 * Get the JLabel instance
		 */
		this.lbTable1Name = new JLabel(LanguageLoader
				.getProperty("SpecifyJoinCondition.table")
				+ ": " + table1Name);
		this.lbJoinCondition = new JLabel(LanguageLoader
				.getProperty("SpecifyJoinCondition.joincondition"));
		this.lbTable2Name = new JLabel(LanguageLoader
				.getProperty("SpecifyJoinCondition.table")
				+ ": " + table2Name);

		/**
		 * Get the JComboBox instance
		 * jcbColumn1 and jcbColumn2 are constructed by transferred parameters 
		 */
		this.jcbColumn1 = new JComboBox(sourceColumn.toArray());
		this.jcbColumn2 = new JComboBox(targetColumn.toArray());
		String[] arrCondition = { "=", ">", "<" };
		this.jcbJoinCondition = new JComboBox(arrCondition);

		/**
		 * Press btnSave, then the join condition is stored in table:
		 * bestpeerschemamapping.joincondition
		 */
		this.btnSave = new JButton(LanguageLoader
				.getProperty("SpecifyJoinCondition.save"));
		this.btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String column1 = (String) SpecifyJoinConditionDlg.this.jcbColumn1
						.getSelectedItem();
				String condition = (String) SpecifyJoinConditionDlg.this.jcbJoinCondition
						.getSelectedItem();
				String column2 = (String) SpecifyJoinConditionDlg.this.jcbColumn2
						.getSelectedItem();

				if (condition.equals("="))
					condition = "equals";
				else if (condition.equals(">"))
					condition = "larger";
				else if (condition.equals("<"))
					condition = "less";

				Connection conn = ServerPeer.conn_schemaMapping;

				try {
					Statement stmt = conn.createStatement();

					/**
					 * If joincondition table doesn't exist, create it.
					 */

					if (!SpecifyJoinConditionDlg.this.hasTable(conn,
							"joincondition")) {
						String createJConditionTable = "create table joincondition ("
								+ "table1 varchar(20),"
								+ "column1 varchar(20),"
								+ "table2 varchar(20),"
								+ "column2 varchar(20),"
								+ "jcondition varchar(10),"
								+ "primary key (table1, column1, table2, column2))";

						stmt.executeUpdate(createJConditionTable);
					}

					String insertSql = "insert into joincondition values ( '"
							+ SpecifyJoinConditionDlg.this.strTable1 + "', '"
							+ column1 + "','"
							+ SpecifyJoinConditionDlg.this.strTable2 + "','"
							+ column2 + "','" + condition + "')";
					stmt.executeUpdate(insertSql);
					stmt.close();
				} catch (SQLException event) {
					event.printStackTrace();
				}

				SpecifyJoinConditionDlg.this.dispose();
			}
		});

		this.jcbPanel = new JPanel();

		this.setTitle(LanguageLoader.getProperty("SpecifyJoinCondition.title"));

		this.initialPane();
	}

	/**
	 * Get all tables in the database
	 * @return
	 */
	public Vector<String> getTables(Connection conn) {
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getTables(null, null, null, null);
			Vector<String> tables = new Vector<String>();
			while (rSet.next()) {
				String tableName = rSet.getString(3);
				tables.addElement(tableName);
			}
			rSet.close();
			return tables;
		} catch (Exception e) {
			System.out
					.println("Error while retrieving names of tables in the database: "
							+ e.getMessage());
			return null;
		}
	}

	/**
	 * Check if a table exists in the database
	 * @param tableName
	 * @return
	 */
	public boolean hasTable(Connection conn, String tableName) {
		Vector<String> tables = this.getTables(conn);
		for (int i = 0; i < tables.size(); i++) {
			if (((String) tables.get(i)).compareToIgnoreCase(tableName) == 0)
				return true;
		}
		return false;
	}

	/**
	 * Initial dialog panel
	 */
	public void initialPane() {
		this.setLayout(new BorderLayout());
		jcbPanel.setLayout(new GridBagLayout());

		/**
		 * Set lbTable1Name's GridBagConstraints
		 */
		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 0;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.HORIZONTAL;
		constraints1.insets.left = 10;
		constraints1.insets.right = 10;
		constraints1.insets.bottom = 10;

		/**
		 * Set lbJoinCondition's GridBagConstraints
		 */
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 0;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.HORIZONTAL;
		constraints2.insets.left = 10;
		constraints2.insets.right = 10;
		constraints2.insets.bottom = 10;

		/**
		 * Set lbTable2Name's GridBagConstraints
		 */
		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 2;
		constraints3.gridy = 0;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 0;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.HORIZONTAL;
		constraints3.insets.left = 10;
		constraints3.insets.right = 10;
		constraints3.insets.bottom = 10;

		/**
		 * Set jcbColumn1's GridBagConstraints
		 */
		GridBagConstraints constraints4 = new GridBagConstraints();
		constraints4.gridx = 0;
		constraints4.gridy = 1;
		constraints4.gridwidth = 1;
		constraints4.gridheight = 1;
		constraints4.weightx = 0;
		constraints4.weighty = 0;
		constraints4.fill = GridBagConstraints.HORIZONTAL;
		constraints4.insets.left = 10;
		constraints4.insets.right = 10;
		constraints4.insets.bottom = 50;

		/**
		 * Set jcbJoinCondition's GridBagConstraints
		 */
		GridBagConstraints constraints5 = new GridBagConstraints();
		constraints5.gridx = 1;
		constraints5.gridy = 1;
		constraints5.gridwidth = 1;
		constraints5.gridheight = 1;
		constraints5.weightx = 0;
		constraints5.weighty = 0;
		constraints5.fill = GridBagConstraints.HORIZONTAL;
		constraints5.insets.left = 10;
		constraints5.insets.right = 10;
		constraints5.insets.bottom = 50;

		/**
		 * Set jcbColumn2's GridBagConstraints
		 */
		GridBagConstraints constraints6 = new GridBagConstraints();
		constraints6.gridx = 2;
		constraints6.gridy = 1;
		constraints6.gridwidth = 1;
		constraints6.gridheight = 1;
		constraints6.weightx = 0;
		constraints6.weighty = 0;
		constraints6.fill = GridBagConstraints.HORIZONTAL;
		constraints6.insets.left = 10;
		constraints6.insets.right = 10;
		constraints6.insets.bottom = 50;

		/**
		 * set btnSave's GridBagConstraints
		 */
		GridBagConstraints constraints7 = new GridBagConstraints();
		constraints7.gridx = 2;
		constraints7.gridy = 3;
		constraints7.gridwidth = 1;
		constraints7.gridheight = 1;
		constraints7.weightx = 0;
		constraints7.weighty = 0;
		constraints7.fill = GridBagConstraints.NONE;

		jcbPanel.add(this.lbTable1Name, constraints1);
		jcbPanel.add(this.lbJoinCondition, constraints2);
		jcbPanel.add(this.lbTable2Name, constraints3);
		jcbPanel.add(this.jcbColumn1, constraints4);
		jcbPanel.add(this.jcbJoinCondition, constraints5);
		jcbPanel.add(this.jcbColumn2, constraints6);
		jcbPanel.add(this.btnSave, constraints7);

		jcbPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.joincondition")));
		this.add(jcbPanel, BorderLayout.CENTER);

		this.setSize(400, 300);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setLocation(screenSize.width / 4, screenSize.height / 4);
		this.setBackground(OperatePanel.panel_color);

		this.setVisible(true);

		this.repaint();
	}
}
