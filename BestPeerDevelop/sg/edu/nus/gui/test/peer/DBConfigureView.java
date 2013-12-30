package sg.edu.nus.gui.test.peer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.print.attribute.standard.Severity;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.DB_TYPE;

public class DBConfigureView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5433637536293353288L;

	private OperatePanel operatePanel = null;

	/**
	 * ERP DB Configuration
	 */
	private JLabel erpdb_driverLabel = null;
	private JComboBox erpdb_driverComboBox = null;
	private JLabel erpdb_serverLabel = null;
	private JTextField erpdb_serverTextField = null;
	private JLabel erpdb_portLabel = null;
	private JTextField erpdb_portTextField = null;
	private JLabel erpdb_dbNameLabel = null;
	private JLabel erpdb_usernameLabel = null;
	private JTextField erpdb_dbNameTextField = null;
	private JTextField erpdb_usernameTextField = null;
	private JLabel erpdb_pwLabel = null;
	private JPasswordField erpdb_pwField = null;
	private JButton erpdb_btnSave = null;
	private JButton erpdb_btnCheckConn = null;

	private JLabel bestpeerdb_driverLabel = null;
	private JComboBox bestpeerdb_driverComboBox = null;
	private JLabel bestpeerdb_serverLabel = null;
	private JTextField bestpeerdb_serverTextField = null;
	private JLabel bestpeerdb_portLabel = null;
	private JTextField bestpeerdb_portTextField = null;
	private JLabel bestpeerdb_dbNameLabel = null;
	private JLabel bestpeerdb_usernameLabel = null;
	private JTextField bestpeerdb_dbNameTextField = null;
	private JTextField bestpeerdb_usernameTextField = null;
	private JLabel bestpeerdb_pwLabel = null;
	private JPasswordField bestpeerdb_pwField = null;
	private JButton bestpeerdb_btnSave = null;
	private JButton bestpeerdb_btnCheckConn = null;

	private JPanel erpdb_Panel = null;
	private JPanel bestpeerdb_Panel = null;

	private JButton btnContinue = null;

	/**
	 * This is the default constructor
	 */
	public DBConfigureView(OperatePanel operatePanel) {
		super();
		this.operatePanel = operatePanel;

		this.getErpdbConfigure();
		this.getbestpeerdbConfigure();

		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout());
		this.setBackground(OperatePanel.panel_color);

		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 0;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.NONE;
		constraints1.insets.right = 50;
		constraints1.insets.bottom = 20;

		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 0;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.insets.bottom = 20;

		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 1;
		constraints3.gridy = 1;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 0;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.NONE;
		constraints3.anchor = GridBagConstraints.EAST;

		this.add(erpdb_Panel, constraints1);
		this.add(bestpeerdb_Panel, constraints2);
		this.add(getBtnContinue(), constraints3);
	}

	private void getErpdbConfigure() {
		erpdb_driverLabel = new JLabel();
		erpdb_driverLabel.setText(LanguageLoader
				.getProperty("dbconfigure.driver"));

		erpdb_serverLabel = new JLabel();
		erpdb_serverLabel.setText(LanguageLoader
				.getProperty("dbconfigure.Server"));

		erpdb_portLabel = new JLabel();
		erpdb_portLabel.setText(LanguageLoader.getProperty("dbconfigure.Port"));

		erpdb_dbNameLabel = new JLabel();
		erpdb_dbNameLabel.setText(LanguageLoader
				.getProperty("dbconfigure.DBName"));

		erpdb_usernameLabel = new JLabel();
		erpdb_usernameLabel.setText(LanguageLoader
				.getProperty("dbconfigure.UserName"));

		erpdb_pwLabel = new JLabel();
		erpdb_pwLabel.setText(LanguageLoader
				.getProperty("dbconfigure.password"));

		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 50;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.NONE;
		constraints1.anchor = GridBagConstraints.EAST;
		constraints1.insets.bottom = 10;
		constraints1.insets.right = 10;

		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 50;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.insets.bottom = 10;

		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 0;
		constraints3.gridy = 1;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 50;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.NONE;
		constraints3.anchor = GridBagConstraints.EAST;
		constraints3.insets.bottom = 10;
		constraints3.insets.right = 10;

		GridBagConstraints constraints4 = new GridBagConstraints();
		constraints4.gridx = 1;
		constraints4.gridy = 1;
		constraints4.gridwidth = 1;
		constraints4.gridheight = 1;
		constraints4.weightx = 50;
		constraints4.weighty = 0;
		constraints4.fill = GridBagConstraints.NONE;
		constraints4.anchor = GridBagConstraints.WEST;
		constraints4.insets.bottom = 10;

		GridBagConstraints constraints5 = new GridBagConstraints();
		constraints5.gridx = 0;
		constraints5.gridy = 2;
		constraints5.gridwidth = 1;
		constraints5.gridheight = 1;
		constraints5.weightx = 50;
		constraints5.weighty = 0;
		constraints5.fill = GridBagConstraints.NONE;
		constraints5.anchor = GridBagConstraints.EAST;
		constraints5.insets.bottom = 10;
		constraints5.insets.right = 10;

		GridBagConstraints constraints6 = new GridBagConstraints();
		constraints6.gridx = 1;
		constraints6.gridy = 2;
		constraints6.gridwidth = 1;
		constraints6.gridheight = 1;
		constraints6.weightx = 50;
		constraints6.weighty = 0;
		constraints6.fill = GridBagConstraints.NONE;
		constraints6.anchor = GridBagConstraints.WEST;
		constraints6.insets.bottom = 10;

		GridBagConstraints constraints7 = new GridBagConstraints();
		constraints7.gridx = 0;
		constraints7.gridy = 3;
		constraints7.gridwidth = 1;
		constraints7.gridheight = 1;
		constraints7.weightx = 50;
		constraints7.weighty = 0;
		constraints7.fill = GridBagConstraints.NONE;
		constraints7.anchor = GridBagConstraints.EAST;
		constraints7.insets.bottom = 10;
		constraints7.insets.right = 10;

		GridBagConstraints constraints8 = new GridBagConstraints();
		constraints8.gridx = 1;
		constraints8.gridy = 3;
		constraints8.gridwidth = 1;
		constraints8.gridheight = 1;
		constraints8.weightx = 50;
		constraints8.weighty = 0;
		constraints8.fill = GridBagConstraints.NONE;
		constraints8.anchor = GridBagConstraints.WEST;
		constraints8.insets.bottom = 10;

		GridBagConstraints constraints9 = new GridBagConstraints();
		constraints9.gridx = 0;
		constraints9.gridy = 4;
		constraints9.gridwidth = 1;
		constraints9.gridheight = 1;
		constraints9.weightx = 50;
		constraints9.weighty = 0;
		constraints9.fill = GridBagConstraints.NONE;
		constraints9.anchor = GridBagConstraints.EAST;
		constraints9.insets.bottom = 10;
		constraints9.insets.right = 10;

		GridBagConstraints constraints10 = new GridBagConstraints();
		constraints10.gridx = 1;
		constraints10.gridy = 4;
		constraints10.gridwidth = 1;
		constraints10.gridheight = 1;
		constraints10.weightx = 50;
		constraints10.weighty = 0;
		constraints10.fill = GridBagConstraints.NONE;
		constraints10.anchor = GridBagConstraints.WEST;
		constraints10.insets.bottom = 10;

		GridBagConstraints constraints11 = new GridBagConstraints();
		constraints11.gridx = 0;
		constraints11.gridy = 5;
		constraints11.gridwidth = 1;
		constraints11.gridheight = 1;
		constraints11.weightx = 50;
		constraints11.weighty = 0;
		constraints11.fill = GridBagConstraints.NONE;
		constraints11.anchor = GridBagConstraints.EAST;
		constraints11.insets.bottom = 10;
		constraints11.insets.right = 10;

		GridBagConstraints constraints12 = new GridBagConstraints();
		constraints12.gridx = 1;
		constraints12.gridy = 5;
		constraints12.gridwidth = 1;
		constraints12.gridheight = 1;
		constraints12.weightx = 50;
		constraints12.weighty = 0;
		constraints12.fill = GridBagConstraints.NONE;
		constraints12.anchor = GridBagConstraints.WEST;
		constraints12.insets.bottom = 10;

		GridBagConstraints constraints13 = new GridBagConstraints();
		constraints13.gridx = 1;
		constraints13.gridy = 6;
		constraints13.gridwidth = 2;
		constraints13.gridheight = 1;
		constraints13.weightx = 50;
		constraints13.weighty = 0;
		constraints13.fill = GridBagConstraints.NONE;
		constraints13.anchor = GridBagConstraints.CENTER;
		constraints13.insets.bottom = 10;

		erpdb_Panel = new JPanel();
		erpdb_Panel.setLayout(new GridBagLayout());

		erpdb_Panel.setBackground(OperatePanel.panel_color);
		erpdb_Panel.add(erpdb_driverLabel, constraints1);
		erpdb_Panel.add(geterpdb_driverComboBox(), constraints2);
		erpdb_Panel.add(erpdb_serverLabel, constraints3);
		erpdb_Panel.add(geterpdb_serverTextField(), constraints4);
		erpdb_Panel.add(erpdb_portLabel, constraints5);
		erpdb_Panel.add(geterpdb_portTextField(), constraints6);
		erpdb_Panel.add(erpdb_dbNameLabel, constraints7);
		erpdb_Panel.add(geterpdb_dbNameTextField(), constraints8);
		erpdb_Panel.add(erpdb_usernameLabel, constraints9);
		erpdb_Panel.add(geterpdb_userField(), constraints10);
		erpdb_Panel.add(erpdb_pwLabel, constraints11);
		erpdb_Panel.add(geterpdb_pwField(), constraints12);

		JPanel panel = new JPanel();
		panel.add(geterpdb_btnCheckConn());
		panel.add(geterpdb_btnSave());
		panel.setBackground(OperatePanel.panel_color);

		erpdb_Panel.add(panel, constraints13);

		TitledBorder titledBorder = new TitledBorder(BorderFactory
				.createLineBorder(Color.BLUE));
		Font font = new Font("titledBorderFont", Font.ITALIC, 16);

		titledBorder.setTitleColor(Color.BLUE);
		titledBorder.setTitleFont(font);
		titledBorder.setTitle(LanguageLoader
				.getProperty("PanelTitle.erpdbconfigure"));

		erpdb_Panel.setBorder(titledBorder);
	}

	private void getbestpeerdbConfigure() {
		bestpeerdb_driverLabel = new JLabel();
		bestpeerdb_driverLabel.setText(LanguageLoader
				.getProperty("dbconfigure.driver"));

		bestpeerdb_serverLabel = new JLabel();
		bestpeerdb_serverLabel.setText(LanguageLoader
				.getProperty("dbconfigure.Server"));

		bestpeerdb_portLabel = new JLabel();
		bestpeerdb_portLabel.setText(LanguageLoader
				.getProperty("dbconfigure.Port"));

		bestpeerdb_dbNameLabel = new JLabel();
		bestpeerdb_dbNameLabel.setText(LanguageLoader
				.getProperty("dbconfigure.DBName"));

		bestpeerdb_usernameLabel = new JLabel();
		bestpeerdb_usernameLabel.setText(LanguageLoader
				.getProperty("dbconfigure.UserName"));

		bestpeerdb_pwLabel = new JLabel();
		bestpeerdb_pwLabel.setText(LanguageLoader
				.getProperty("dbconfigure.password"));

		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 50;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.NONE;
		constraints1.anchor = GridBagConstraints.EAST;
		constraints1.insets.bottom = 10;
		constraints1.insets.right = 10;

		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 50;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.insets.bottom = 10;

		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 0;
		constraints3.gridy = 1;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 50;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.NONE;
		constraints3.anchor = GridBagConstraints.EAST;
		constraints3.insets.bottom = 10;
		constraints3.insets.right = 10;

		GridBagConstraints constraints4 = new GridBagConstraints();
		constraints4.gridx = 1;
		constraints4.gridy = 1;
		constraints4.gridwidth = 1;
		constraints4.gridheight = 1;
		constraints4.weightx = 50;
		constraints4.weighty = 0;
		constraints4.fill = GridBagConstraints.NONE;
		constraints4.anchor = GridBagConstraints.WEST;
		constraints4.insets.bottom = 10;

		GridBagConstraints constraints5 = new GridBagConstraints();
		constraints5.gridx = 0;
		constraints5.gridy = 2;
		constraints5.gridwidth = 1;
		constraints5.gridheight = 1;
		constraints5.weightx = 50;
		constraints5.weighty = 0;
		constraints5.fill = GridBagConstraints.NONE;
		constraints5.anchor = GridBagConstraints.EAST;
		constraints5.insets.bottom = 10;
		constraints5.insets.right = 10;

		GridBagConstraints constraints6 = new GridBagConstraints();
		constraints6.gridx = 1;
		constraints6.gridy = 2;
		constraints6.gridwidth = 1;
		constraints6.gridheight = 1;
		constraints6.weightx = 50;
		constraints6.weighty = 0;
		constraints6.fill = GridBagConstraints.NONE;
		constraints6.anchor = GridBagConstraints.WEST;
		constraints6.insets.bottom = 10;

		GridBagConstraints constraints7 = new GridBagConstraints();
		constraints7.gridx = 0;
		constraints7.gridy = 3;
		constraints7.gridwidth = 1;
		constraints7.gridheight = 1;
		constraints7.weightx = 50;
		constraints7.weighty = 0;
		constraints7.fill = GridBagConstraints.NONE;
		constraints7.anchor = GridBagConstraints.EAST;
		constraints7.insets.bottom = 10;
		constraints7.insets.right = 10;

		GridBagConstraints constraints8 = new GridBagConstraints();
		constraints8.gridx = 1;
		constraints8.gridy = 3;
		constraints8.gridwidth = 1;
		constraints8.gridheight = 1;
		constraints8.weightx = 50;
		constraints8.weighty = 0;
		constraints8.fill = GridBagConstraints.NONE;
		constraints8.anchor = GridBagConstraints.WEST;
		constraints8.insets.bottom = 10;

		GridBagConstraints constraints9 = new GridBagConstraints();
		constraints9.gridx = 0;
		constraints9.gridy = 4;
		constraints9.gridwidth = 1;
		constraints9.gridheight = 1;
		constraints9.weightx = 50;
		constraints9.weighty = 0;
		constraints9.fill = GridBagConstraints.NONE;
		constraints9.anchor = GridBagConstraints.EAST;
		constraints9.insets.bottom = 10;
		constraints9.insets.right = 10;

		GridBagConstraints constraints10 = new GridBagConstraints();
		constraints10.gridx = 1;
		constraints10.gridy = 4;
		constraints10.gridwidth = 1;
		constraints10.gridheight = 1;
		constraints10.weightx = 50;
		constraints10.weighty = 0;
		constraints10.fill = GridBagConstraints.NONE;
		constraints10.anchor = GridBagConstraints.WEST;
		constraints10.insets.bottom = 10;

		GridBagConstraints constraints11 = new GridBagConstraints();
		constraints11.gridx = 0;
		constraints11.gridy = 5;
		constraints11.gridwidth = 1;
		constraints11.gridheight = 1;
		constraints11.weightx = 50;
		constraints11.weighty = 0;
		constraints11.fill = GridBagConstraints.NONE;
		constraints11.anchor = GridBagConstraints.EAST;
		constraints11.insets.bottom = 10;
		constraints11.insets.right = 10;

		GridBagConstraints constraints12 = new GridBagConstraints();
		constraints12.gridx = 1;
		constraints12.gridy = 5;
		constraints12.gridwidth = 1;
		constraints12.gridheight = 1;
		constraints12.weightx = 50;
		constraints12.weighty = 0;
		constraints12.fill = GridBagConstraints.NONE;
		constraints12.anchor = GridBagConstraints.WEST;
		constraints12.insets.bottom = 10;

		GridBagConstraints constraints13 = new GridBagConstraints();
		constraints13.gridx = 1;
		constraints13.gridy = 6;
		constraints13.gridwidth = 1;
		constraints13.gridheight = 1;
		constraints13.weightx = 50;
		constraints13.weighty = 0;
		constraints13.fill = GridBagConstraints.NONE;
		constraints13.anchor = GridBagConstraints.EAST;
		constraints13.insets.bottom = 10;

		bestpeerdb_Panel = new JPanel();
		bestpeerdb_Panel.setEnabled(false);
		bestpeerdb_Panel.setLayout(new GridBagLayout());

		bestpeerdb_Panel.setBackground(OperatePanel.panel_color);
		bestpeerdb_Panel.add(bestpeerdb_driverLabel, constraints1);
		bestpeerdb_Panel.add(getbestpeerdb_driverComboBox(), constraints2);
		bestpeerdb_Panel.add(bestpeerdb_serverLabel, constraints3);
		bestpeerdb_Panel.add(getbestpeerdb_serverTextField(), constraints4);
		bestpeerdb_Panel.add(bestpeerdb_portLabel, constraints5);
		bestpeerdb_Panel.add(getbestpeerdb_portTextField(), constraints6);
		bestpeerdb_Panel.add(bestpeerdb_dbNameLabel, constraints7);
		bestpeerdb_Panel.add(getbestpeerdb_dbNameTextField(), constraints8);
		bestpeerdb_Panel.add(bestpeerdb_usernameLabel, constraints9);
		bestpeerdb_Panel.add(getBestPeer_userField(), constraints10);
		bestpeerdb_Panel.add(bestpeerdb_pwLabel, constraints11);
		bestpeerdb_Panel.add(getbestpeerdb_pwField(), constraints12);

		JPanel panel = new JPanel();
		panel.add(getbestpeerdb_btnCheckConn());
		panel.add(getbestpeerdb_btnSave());
		panel.setBackground(OperatePanel.panel_color);

		bestpeerdb_Panel.add(panel, constraints13);

		TitledBorder titledBorder = new TitledBorder(BorderFactory
				.createLineBorder(Color.BLUE));
		Font font = new Font("titledBorderFont", Font.ITALIC, 16);

		titledBorder.setTitleColor(Color.BLUE);
		titledBorder.setTitleFont(font);
		titledBorder.setTitle(LanguageLoader
				.getProperty("PanelTitle.bestpeerdbconfigure"));

		bestpeerdb_Panel.setBorder(titledBorder);
	}

	/**
	 * This method initializes erpdb_driverComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox geterpdb_driverComboBox() {
		if (erpdb_driverComboBox == null) {
			erpdb_driverComboBox = new JComboBox();

			for (int i = 0; i < DB_TYPE.support_dbs.length; i++) {
				erpdb_driverComboBox.addItem(DB_TYPE.support_dbs[i]);
			}

			erpdb_driverComboBox.setSelectedItem(DB_TYPE
					.getByType(ServerPeer.erp_db.getDbType()));
		}

		return erpdb_driverComboBox;
	}

	/**
	 * This method initializes erpdb_urlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField geterpdb_serverTextField() {
		if (erpdb_serverTextField == null) {
			erpdb_serverTextField = new JTextField();
		}

		erpdb_serverTextField.setText(ServerPeer.erp_db.getServerAddress());

		erpdb_serverTextField.setColumns(25);

		return erpdb_serverTextField;
	}

	/**
	 * This method initializes erpdb_portTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField geterpdb_portTextField() {
		if (erpdb_portTextField == null) {
			erpdb_portTextField = new JTextField();
		}

		erpdb_portTextField.setText(ServerPeer.erp_db.getPort());
		erpdb_portTextField.setColumns(25);

		return erpdb_portTextField;
	}

	/**
	 * This method initializes erpdb_dbNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField geterpdb_dbNameTextField() {
		if (erpdb_dbNameTextField == null) {
			erpdb_dbNameTextField = new JTextField();
		}

		erpdb_dbNameTextField.setColumns(25);
		erpdb_dbNameTextField.setText(ServerPeer.erp_db.getDbName());

		return erpdb_dbNameTextField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField geterpdb_userField() {
		if (erpdb_usernameTextField == null) {
			erpdb_usernameTextField = new JTextField();
		}

		erpdb_usernameTextField.setColumns(25);

		erpdb_usernameTextField.setText(ServerPeer.erp_db.getUser());

		return erpdb_usernameTextField;
	}

	/**
	 * This method initializes erpdb_pwField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JPasswordField geterpdb_pwField() {
		if (erpdb_pwField == null) {
			erpdb_pwField = new JPasswordField();
		}

		erpdb_pwField.setColumns(25);

		return erpdb_pwField;
	}

	/**
	 * This method initializes erpdb_btnSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton geterpdb_btnSave() {
		if (erpdb_btnSave == null) {
			erpdb_btnSave = new JButton();
			erpdb_btnSave.setText(LanguageLoader.getProperty("button.save"));
			erpdb_btnSave
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

			erpdb_btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DBProperty dbProperty = new DBProperty();
					String server = DBConfigureView.this.erpdb_serverTextField
							.getText();
					String port = DBConfigureView.this.erpdb_portTextField
							.getText();
					String username = DBConfigureView.this.erpdb_usernameTextField
							.getText();
					String password = String
							.copyValueOf(DBConfigureView.this.erpdb_pwField
									.getPassword());
					String dbname = DBConfigureView.this.erpdb_dbNameTextField
							.getText();

					DB_TYPE dbtype = (DB_TYPE) (DBConfigureView.this.erpdb_driverComboBox
							.getSelectedItem());

					Vector<String> vec = new Vector<String>();
					vec.add(dbtype.getName());
					vec.add(server);
					vec.add(port);
					vec.add(username);
					vec.add(password);
					vec.add(dbname);

					dbProperty.put_erpdb_configure(vec);
					
					//refresh connection
					DB testdb = new DB(dbtype.getName(), server, port,
							username, password, dbname);
					if (ServerPeer.conn_localSchema!=null){
						try {
							ServerPeer.conn_localSchema.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					ServerPeer.conn_localSchema = testdb.createDbConnection();

					JOptionPane.showMessageDialog(null, "Successfully Save!");
				}
			});
		}
		return erpdb_btnSave;
	}

	private JButton geterpdb_btnCheckConn() {
		if (erpdb_btnCheckConn == null) {
			erpdb_btnCheckConn = new JButton();
			erpdb_btnCheckConn.setText(LanguageLoader
					.getProperty("button.checkConnection"));
			erpdb_btnCheckConn
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

			erpdb_btnCheckConn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String server = DBConfigureView.this.erpdb_serverTextField
							.getText();
					String port = DBConfigureView.this.erpdb_portTextField
							.getText();
					String username = DBConfigureView.this.erpdb_usernameTextField
							.getText();
					String password = String
							.copyValueOf(DBConfigureView.this.erpdb_pwField
									.getPassword());
					String dbname = DBConfigureView.this.erpdb_dbNameTextField
							.getText();

					DB_TYPE dbtype = (DB_TYPE) (DBConfigureView.this.erpdb_driverComboBox
							.getSelectedItem());

					DB testdb = new DB(dbtype.getName(), server, port,
							username, password, dbname);

					if (testdb.testDbConnection()) {
						JOptionPane.showMessageDialog(null,
								"ERP Setting is connected successful!");
					} else {
						JOptionPane.showMessageDialog(null,
								"Can't connected to ERP Database!");
					}
				}
			});
		}
		return erpdb_btnCheckConn;
	}

	/**
	 * This method initializes bestpeerdb_driverComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getbestpeerdb_driverComboBox() {
		if (bestpeerdb_driverComboBox == null) {
			bestpeerdb_driverComboBox = new JComboBox();
			for (int i = 0; i < DB_TYPE.support_dbs.length; i++) {
				bestpeerdb_driverComboBox.addItem(DB_TYPE.support_dbs[i]);
			}
			bestpeerdb_driverComboBox.setSelectedItem(ServerPeer.bestpeer_db
					.getDbType());
		}
		return bestpeerdb_driverComboBox;
	}

	/**
	 * This method initializes bestpeerdb_urlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getbestpeerdb_serverTextField() {
		if (bestpeerdb_serverTextField == null) {
			bestpeerdb_serverTextField = new JTextField();
		}

		bestpeerdb_serverTextField.setText(ServerPeer.bestpeer_db
				.getServerAddress());

		bestpeerdb_serverTextField.setColumns(25);

		return bestpeerdb_serverTextField;
	}

	/**
	 * This method initializes bestpeerdb_portTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getbestpeerdb_portTextField() {
		if (bestpeerdb_portTextField == null) {
			bestpeerdb_portTextField = new JTextField();
		}

		bestpeerdb_portTextField.setText(ServerPeer.bestpeer_db.getPort());
		bestpeerdb_portTextField.setColumns(25);

		return bestpeerdb_portTextField;
	}

	/**
	 * This method initializes bestpeerdb_dbNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getbestpeerdb_dbNameTextField() {
		if (bestpeerdb_dbNameTextField == null) {
			bestpeerdb_dbNameTextField = new JTextField();
		}

		bestpeerdb_dbNameTextField.setColumns(25);
		bestpeerdb_dbNameTextField.setText(ServerPeer.BESTPEERDB);

		return bestpeerdb_dbNameTextField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBestPeer_userField() {
		if (bestpeerdb_usernameTextField == null) {
			bestpeerdb_usernameTextField = new JTextField();
		}

		bestpeerdb_usernameTextField.setColumns(25);

		bestpeerdb_usernameTextField.setText(ServerPeer.bestpeer_db.getUser());

		return bestpeerdb_usernameTextField;
	}

	/**
	 * This method initializes bestpeerdb_pwField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JPasswordField getbestpeerdb_pwField() {
		if (bestpeerdb_pwField == null) {
			bestpeerdb_pwField = new JPasswordField();
		}

		bestpeerdb_pwField.setColumns(25);

		return bestpeerdb_pwField;
	}

	/**
	 * This method initializes bestpeerdb_btnSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getbestpeerdb_btnSave() {
		if (bestpeerdb_btnSave == null) {
			bestpeerdb_btnSave = new JButton();
			bestpeerdb_btnSave.setText(LanguageLoader
					.getProperty("button.save"));
			bestpeerdb_btnSave
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

			bestpeerdb_btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DBProperty dbProperty = new DBProperty();
					String server = DBConfigureView.this.bestpeerdb_serverTextField
							.getText();
					String port = DBConfigureView.this.bestpeerdb_portTextField
							.getText();
					String username = DBConfigureView.this.bestpeerdb_usernameTextField
							.getText();
					String password = String
							.copyValueOf(DBConfigureView.this.bestpeerdb_pwField
									.getPassword());
					String dbname = DBConfigureView.this.bestpeerdb_dbNameTextField
							.getText();

					DB_TYPE dbtype = (DB_TYPE) (DBConfigureView.this.bestpeerdb_driverComboBox
							.getSelectedItem());

					Vector<String> vec = new Vector<String>();
					vec.add(dbtype.getName());
					vec.add(server);
					vec.add(port);
					vec.add(username);
					vec.add(password);
					vec.add(dbname);

					dbProperty.put_bestpeer_configure(vec);

					JOptionPane.showMessageDialog(null, "Successfully Save!");
				}
			});
		}
		return bestpeerdb_btnSave;
	}

	private JButton getbestpeerdb_btnCheckConn() {
		if (bestpeerdb_btnCheckConn == null) {
			bestpeerdb_btnCheckConn = new JButton();
			bestpeerdb_btnCheckConn.setText(LanguageLoader
					.getProperty("button.checkConnection"));
			bestpeerdb_btnCheckConn
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

			bestpeerdb_btnCheckConn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String server = DBConfigureView.this.bestpeerdb_serverTextField
							.getText();
					String port = DBConfigureView.this.bestpeerdb_portTextField
							.getText();
					String username = DBConfigureView.this.bestpeerdb_usernameTextField
							.getText();
					String password = String
							.copyValueOf(DBConfigureView.this.bestpeerdb_pwField
									.getPassword());
					String dbname = DBConfigureView.this.bestpeerdb_dbNameTextField
							.getText();

					DB_TYPE dbtype = (DB_TYPE) DBConfigureView.this.bestpeerdb_driverComboBox
							.getSelectedItem();

					DB testdb = new DB(dbtype.getName(), server, port,
							username, password, dbname);

					if (testdb.testDbConnection()) {
						JOptionPane.showMessageDialog(null,
								"BESTPEER DB Setting is connected successful!");
					} else {
						JOptionPane.showMessageDialog(null,
								"Can't connect to BESTPEER DB!");
					}
				}
			});
		}
		return bestpeerdb_btnCheckConn;
	}

	private JButton getBtnContinue() {
		if (btnContinue == null) {
			btnContinue = new JButton();
			btnContinue.setText(LanguageLoader.getProperty("button.return"));
			btnContinue
					.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

			btnContinue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DBManagerPanel dbmPane = new DBManagerPanel(operatePanel);
					DBConfigureView.this.operatePanel.setComponentAt(
							OperatePanel.TAB_DBMANAGER_INDEX, dbmPane);
				}
			});
		}
		return btnContinue;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
