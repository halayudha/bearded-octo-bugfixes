/*
 * @(#) DBConnectView.java 1.0 2006-12-23
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sg.edu.nus.gui.sharing.DBExplorerPanel;
import sg.edu.nus.peer.ServerPeer;

/**
 * A <code>Database Connection</code> panel
 * to allow user connect to database with corresponding driver/url:port/dbname/user:password information. 
 * 
 * @author Huang Yukai
 * @version 1.0 2006-12-23
 */

public class DBConnectView extends JPanel {
	// private members
	private static final long serialVersionUID = -725795894692087025L;

	private Component parentComponent;

	private JPanel mainPane;
	private JPanel statusPane;
	private JButton connectButton;

	// Input the connecting information: driver/url:port/dbname/user:password
	private JComboBox driverComboBox;
	private JTextField urlTextField;
	private JTextField portTextField;
	private JTextField dbNameTextField;
	private JTextField usernameTextField;
	private JTextField pwField;

	// The database drivers that are supported in current version
	private String[] drivers = { "MySQL", "ODBC", "SQLSever", "Oracle", "DB2" };

	private Connection con;

	/**
	 * Constructor
	 * 
	 * @param parent - the parent container that hold this panel.
	 *                 the parent container must be <code>DatabaseExplorer</code> instance.
	 */
	public DBConnectView(Component/*DatabaseExplorer*/parent) {
		this.parentComponent = parent;
		this.initPane();
	}

	/*
	 * Initialize all the components in the interface.
	 *
	 */
	private void initPane() {
		this.setLayout(new BorderLayout());

		// construct the components
		JLabel driverLabel = new JLabel("Driver:");
		driverComboBox = this.listDrivers();

		JLabel urlLabel = new JLabel("URL:");
		urlTextField = new JTextField("localhost"/*"XX.XX.XX.XX"*/);

		JLabel portLabel = new JLabel("Port:");
		portTextField = new JTextField("3306");

		JLabel dbNameLabel = new JLabel("DB Name:");
		dbNameTextField = new JTextField(ServerPeer.bestpeer_db
				.getServerAddress());

		JLabel usernameLabel = new JLabel("User Name");
		usernameTextField = new JTextField(ServerPeer.bestpeer_db.getPort());

		JLabel pwLabel = new JLabel("Password:");
		pwField = new JPasswordField(ServerPeer.bestpeer_db.getPwd());

		// layout the components
		mainPane = new JPanel();
		mainPane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new java.awt.Insets(4, 4, 4, 4);

		constraints.weighty = 3;
		this.addComponent(mainPane, new JPanel(), constraints, 0, 0, 5, 1);

		constraints.weighty = 0.4; // 0.5 = 3/6
		constraints.weightx = 0.3;
		this.addComponent(mainPane, driverLabel, constraints, 0, 1, 1, 1);
		this.addComponent(mainPane, urlLabel, constraints, 0, 2, 1, 1);
		this.addComponent(mainPane, portLabel, constraints, 0, 3, 1, 1);
		this.addComponent(mainPane, dbNameLabel, constraints, 0, 4, 1, 1);
		this.addComponent(mainPane, usernameLabel, constraints, 0, 5, 1, 1);
		this.addComponent(mainPane, pwLabel, constraints, 0, 6, 1, 1);

		constraints.weightx = 0.7;
		this.addComponent(mainPane, driverComboBox, constraints, 1, 1, 4, 1);
		this.addComponent(mainPane, urlTextField, constraints, 1, 2, 4, 1);
		this.addComponent(mainPane, portTextField, constraints, 1, 3, 4, 1);
		this.addComponent(mainPane, dbNameTextField, constraints, 1, 4, 4, 1);
		this.addComponent(mainPane, usernameTextField, constraints, 1, 5, 4, 1);
		this.addComponent(mainPane, pwField, constraints, 1, 6, 4, 1);

		constraints.weighty = 8;
		this.addComponent(mainPane, new JPanel(), constraints, 0, 7, 5, 1);

		statusPane = new JPanel();
		statusPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		connectButton = new JButton("Connect");
		// Button event: connect to the database
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String driver = driverComboBox.getSelectedItem().toString();
				String url = urlTextField.getText();
				String port = portTextField.getText();
				String dbName = dbNameTextField.getText();
				String username = usernameTextField.getText();
				String password = pwField.getText();

				try {
					if (connectToDatabase(driver, url, port, dbName, username,
							password)) {
						// Succeed in connecting to the database
						// To show the DBTreeView panel
						DatabaseMetaData dbmd = con.getMetaData();
						((DBExplorerPanel) (DBConnectView.this.parentComponent))
								.setConnection(con);
						((DBExplorerPanel) (DBConnectView.this.parentComponent)).dbTreeView
								.showTree(dbmd, dbName);
						((DBExplorerPanel) (DBConnectView.this.parentComponent))
								.isConnected(true);
					}
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				} catch (ClassNotFoundException cnfe) {
					System.err.println("Not support such driver: " + driver);
				}

			}
		});

		statusPane.add(connectButton, BorderLayout.EAST);

		this.add(mainPane, BorderLayout.CENTER);
		this.add(statusPane, BorderLayout.SOUTH);
	}

	/*
	 * Add all the supported drivers to the combo box.
	 */
	private JComboBox listDrivers() {
		JComboBox cb = new JComboBox();
		for (int i = 0; i < drivers.length; i++)
			cb.addItem(drivers[i]);
		cb.setSelectedIndex(0);
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String item = DBConnectView.this.driverComboBox
						.getSelectedItem().toString();
				if (item.equalsIgnoreCase("ODBC")) {
					// put the initial value when using ODBC driver
				} else {
					DBConnectView.this.urlTextField.setEnabled(true);
					DBConnectView.this.portTextField.setEnabled(true);
					if (item.equalsIgnoreCase("SQLSever")) {
						// put the initial value when using SQLSever driver
					} else if (item.equalsIgnoreCase("Oracle")) {
						// put the initial value when using Oracle driver
					} else if (item.equalsIgnoreCase("DB2")) {
						// put the initial value when using DB2 driver
					} else if (item.equalsIgnoreCase("MySQL")) {
						// put the initial value when using MySQL driver
					}
				}
			}
		});
		return cb;
	}

	/*
	 * Put the component on the appointed position of the panel.
	 * 
	 * @param con - the panel where hold the component
	 * @param c - the component to be added to the panel
	 * @param constraints - the <code>GridBagConstraints</code> instance
	 * @param x - gridx
	 * @param y - gridy
	 * @param width - gridwidth
	 * @param height - gridheight
	 */
	private void addComponent(Container con, Component c,
			GridBagConstraints constraints, int x, int y, int width, int height) {
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		con.add(c, constraints);
	}

	/*
	 * Retrieve the connection according to the input information
	 * 
	 * @param driver - the database driver
	 * @param url - the URL of the database to which to connect
	 * @param port - the port of the database to which to connect
	 * @param dbName -  the name of the database to which to connect
	 * @param username - the database user on whose behalf the connection is being made
	 * @param pw - the user's password
	 * 
	 * @return <code>true</code> if connect to the database sucessfully
	 * 
	 * @throws SQLException - if a database access error occurs
	 * @throws ClassNotFoundException - if no such driver as the parameter is supported 
	 */
	public boolean connectToDatabase(String driver, String url, String port,
			String dbName, String username, String pw) throws SQLException,
			ClassNotFoundException {
		String driverFormat = null;
		String urlFormat = null;
		driver = driver.toUpperCase();
		url = url.toLowerCase();
		port = port.toLowerCase();
		username = username.toLowerCase();
		pw = pw.toLowerCase();

		if (driver.equals("SQLSever")) {
			driverFormat = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
			urlFormat = "jdbc:microsoft:sqlserver://" + url + ":" + port + ";"
					+ "DataBasename=" + dbName;
		} else if (driver.equals("DB2")) {
			driverFormat = "com.ibm.db2.jdbc.app.DB2Driver";
			urlFormat = "jdbc:db2://" + url + ":" + port + ":" + dbName;
		} else if (driver.equals("ORACLE")) {
			driverFormat = "oracle.jdbc.driver.OracleDriver";
			urlFormat = "jdbc:oracle:thin:@" + url + ":" + port + ":" + dbName;
		} else if (driver.equals("SYBASE")) {
			driverFormat = "com.sybase.jdbc2.jdbc.SybDriver";
			urlFormat = "jdbc:sybase:Tds:" + url + ":" + port + "/" + dbName;
		} else if (driver.equals("MYSQL")) {
			driverFormat = "com.mysql.jdbc.Driver";
			urlFormat = "jdbc:mysql://" + url + "/" + dbName;
		} else if (driver.equals("ODBC")) {
			driverFormat = "sun.jdbc.odbc.JdbcOdbcDriver";
			urlFormat = "jdbc:odbc:" + dbName;
		} else {
			// @throws ClassNotFoundException below
		}
		// Class.forName("com.mysql.jdbc.Driver"); //load the JDBC driver
		Class.forName(driverFormat); // load the JDBC driver
		con = DriverManager.getConnection(urlFormat, username, pw); // get a
		// connection
		return true;
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
	 * Return a string: "DBConnectView".
	 * 
	 * @return "DBConnectView"
	 */
	public String toString() {
		return "DBConnectView";
	}

}
