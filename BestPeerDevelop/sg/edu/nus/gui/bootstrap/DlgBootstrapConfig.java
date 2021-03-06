package sg.edu.nus.gui.bootstrap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

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
import sg.edu.nus.gui.AbstractDialog;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.ui.server.BestPeerServlet;
import sg.edu.nus.util.DB_TYPE;

/**
 * Config service port of bootstrap peer and database connection for meta data
 * of bootstrap
 * 
 * @author VHTam
 * 
 */
public class DlgBootstrapConfig extends AbstractDialog implements
		ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6213129159456017176L;

	private static final String CONFIG_FILE = "./conf/bootstrap.ini";

	// protected members
	/**
	 * Get the user input of the socket port used for monitoring network events.
	 */
	protected JTextField tfPort;

	/**
	 * Get the maximum number of threads simultaneously to listen network
	 * events.
	 */
	protected JTextField tfCapacity;

	/**
	 * The name of buttons.
	 */
	protected final String[] btnName = {
			LanguageLoader.getProperty("button.ok"),
			LanguageLoader.getProperty("button.cancel"),
			LanguageLoader.getProperty("button.checkConnection") };

	/**
	 * The command string used for buttons.
	 */
	protected final String[] command = { "ok", "cancel", "Test Connection" };

	JComboBox jcbType = null;

	JTextField jtfServer = null;

	JTextField jtfPort = null;

	JTextField jtfDbName = null;

	JTextField jtfDbPort = null;

	JTextField jtfUser = null;

	JPasswordField jpfPwd = null;

	JLabel jlblPort = null;

	JLabel jlblType = null;

	JLabel jlblServer = null;

	JLabel jlblDbName = null;

	JLabel jlblDbPort = null;

	JLabel jlblUser = null;

	JLabel jlblPwd = null;

	String strPort = null;

	String strCapacity = null;

	String strType = null;

	String strServer = null;

	String strDbPort = null;

	String strDbName = null;

	String strUser = null;

	String strPwd = null;

	String title = null;

	/**
	 * 
	 * Construct the configuration dialog.
	 * 
	 * @param gui
	 *            the handler of the main frame
	 * @param title
	 *            the dialog title to be displayed
	 * @param model
	 *            determine whether this is a model dialog
	 * @param height
	 *            the height of this window
	 * @param width
	 *            the width of this window
	 * @param port
	 *            the port of the local server
	 * @param capacity
	 *            the maximal number of threads used for handling network events
	 */
	public DlgBootstrapConfig(Frame gui, String title, int height, int width,
			String port, String capacity) {
		super(gui, title, true, height, width);
		this.setLayout(new BorderLayout());

		this.title = LanguageLoader.getProperty("title.configBoostrap");
		this.setTitle(LanguageLoader.getProperty("title.configBoostrap"));
		JPanel panel = null;

		/* make configure panel and add it to content pane */
		panel = makeContentPane(port, capacity);
		this.add(panel, BorderLayout.CENTER);

		/* make button panel and add it to content pane */
		panel = makeButtonPane();
		this.add(panel, BorderLayout.SOUTH);
	}

	public int getPort() {
		return Integer.parseInt(strPort);
	}

	public String getDbPort() {
		return strDbPort;
	}

	public int getCapacity() {
		return Integer.parseInt(strCapacity);
	}

	public String getDbType() {
		return strType;
	}

	public String getDbServer() {
		return strServer;
	}

	public String getDbName() {
		return strDbName;
	}

	public String getUserName() {
		return strUser;
	}

	public String getPassword() {
		return strPwd;
	}

	public void setPort(int port) {
		tfPort.setText(Integer.toString(port));

	}

	public void setCapacity(int capacity) {
		tfCapacity.setText(Integer.toString(capacity));
	}

	public void setDbType(String type) {
		jcbType.setSelectedItem(DB_TYPE.getByType(type));
	}

	public void setDbServer(String server) {
		jtfServer.setText(server);
	}

	public void setDbPort(String port) {
		jtfDbPort.setText(port);
	}

	public void setPort(String port) {
		jtfPort.setText(port);
	}

	public void setDbName(String dbname) {
		jtfDbName.setText(dbname);
	}

	public void setUserName(String user) {
		jtfUser.setText(user);
	}

	public void setPassword(String pwd) {
		jpfPwd.setText(pwd);
	}

	/**
	 * Construct the panel used for input configuration information.
	 * 
	 * @param port
	 *            the port of the local server
	 * @param capacity
	 *            the maximal number of threads used for handling network events
	 * @return the instance of <code>JPanel</code> with labels and text fields
	 */
	protected JPanel makeContentPane(String port, String capacity) {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel textPane = new JPanel(new GridLayout(8, 2, 5, 15));

		JLabel label = null;
		label = new JLabel(LanguageLoader.getProperty("label.serverport"));
		textPane.add(label);
		tfPort = new JTextField(port, 5);
		textPane.add(tfPort);

		label = new JLabel(LanguageLoader.getProperty("label.connection"));
		textPane.add(label);
		tfCapacity = new JTextField(capacity, 4);
		textPane.add(tfCapacity);

		//
		String dbType = LanguageLoader.getProperty("label.dbDriver");
		String dbServer = LanguageLoader.getProperty("label.url");
		String dbPort = LanguageLoader.getProperty("label.port");
		String dbName = LanguageLoader.getProperty("label.dbname");
		String userName = LanguageLoader.getProperty("label.username");
		String password = LanguageLoader.getProperty("label.password");

		jlblType = new JLabel(dbType);
		jlblServer = new JLabel(dbServer);
		jlblDbPort = new JLabel(dbPort);
		jlblDbName = new JLabel(dbName);
		jlblUser = new JLabel(userName);
		jlblPwd = new JLabel(password);

		DBProperty.setConfigFile("./conf/bootstrap.ini");
		DBProperty dbProperty = new DBProperty();
		jtfServer = new JTextField(dbProperty.getBestpeerDB().getServerAddress());
		jtfDbPort = new JTextField(dbProperty.getBestpeerDB().getPort());
		jtfDbName = new JTextField(dbProperty.getBestpeerDB().getDbName());
		jtfUser = new JTextField(dbProperty.getBestpeerDB().getUser());
			
		jpfPwd = new JPasswordField();
		jcbType = createDriverCombobox();

		textPane.add(jlblType);
		textPane.add(jcbType);

		textPane.add(jlblServer);
		textPane.add(jtfServer);

		textPane.add(jlblDbPort);
		textPane.add(jtfDbPort);

		textPane.add(jlblDbName);
		textPane.add(jtfDbName);

		textPane.add(jlblUser);
		textPane.add(jtfUser);

		textPane.add(jlblPwd);
		textPane.add(jpfPwd);

		//
		textPane.setBorder(BorderFactory.createTitledBorder(null, title,
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(textPane, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Make the button panel.
	 * 
	 * @return the instance of <code>JPanel</code> with buttons
	 */
	protected JPanel makeButtonPane() {
		JButton button = null;
		JPanel panel = new JPanel();

		/* set panel layout */
		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		/* add buttons to panel */
		button = makeButton(btnName[0], command[0]);
		panel.add(button);

		button = makeButton(btnName[2], command[2]);
		panel.add(button);

		button = makeButton(btnName[1], command[1]);
		panel.add(button);

		return panel;
	}

	/**
	 * Make individual button.
	 * 
	 * @param name
	 *            the button name
	 * @param cmd
	 *            the command string
	 * @return the instance of <code>JButton</code>
	 */
	protected JButton makeButton(String name, String cmd) {
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
		button.addActionListener(this);
		return button;
	}

	private JComboBox createDriverCombobox() {

		JComboBox combo = new JComboBox();

		// modified by chensu, 2009-05-07
		for (int i = 0; i < DB_TYPE.support_dbs.length; i++) {
			combo.addItem(DB_TYPE.support_dbs[i]);
		}

		return combo;
	}

	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 *         otherwise, return <code>false</code>.
	 */
	private boolean checkPortAndCapacity() {
		/* check port text field */
		String str = tfPort.getText().trim();

		if (str == "") {
			JOptionPane.showMessageDialog(gui, LanguageLoader
					.getProperty("message.msg1"));
			tfPort.grabFocus();
			return false;
		} else {
			try {
				int port = Integer.parseInt(str);
				if ((port <= 0) || (port > 65535)) {
					JOptionPane.showMessageDialog(gui, LanguageLoader
							.getProperty("message.msg2"));
					tfPort.grabFocus();
					return false;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(gui, LanguageLoader
						.getProperty("message.msg2"));
				tfPort.grabFocus();
				return false;
			}
		}

		/* check capacity text field */
		str = tfCapacity.getText().trim();
		if (str == "") {
			JOptionPane.showMessageDialog(gui, LanguageLoader
					.getProperty("message.msg3"));
			tfCapacity.grabFocus();
			return false;
		} else {
			try {
				int capacity = Integer.parseInt(str);
				if (capacity <= 0) {
					JOptionPane.showMessageDialog(gui, LanguageLoader
							.getProperty("message.msg2"));
					tfCapacity.grabFocus();
					return false;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(gui, LanguageLoader
						.getProperty("message.msg2"));
				tfCapacity.grabFocus();
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 *         otherwise, return <code>false</code>.
	 */
	protected boolean checkValue() {
		String str = tfPort.getText().trim();
		try {
			int port = Integer.parseInt(str);
			if (port == Bootstrap.RUN_PORT) {
				JOptionPane.showMessageDialog(gui, LanguageLoader
						.getProperty("message.msg4"));
				return false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(gui, LanguageLoader
					.getProperty("message.msg2"));
			return false;
		}

		return checkPortAndCapacity();
	}

	boolean ok = false;

	public boolean isOkPressed() {
		return ok;
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		/* if change configuration */
		if (cmd.equals(command[0])) {
			if (checkValue()) {
				ok = true;

				/* flush change to file */
				strPort = tfPort.getText().trim();
				strCapacity = tfCapacity.getText().trim();

				strType = ((DB_TYPE) (jcbType.getSelectedItem())).getName();
				strServer = jtfServer.getText().trim();
				strDbPort = jtfDbPort.getText().trim();
				strDbName = jtfDbName.getText().trim();
				strUser = jtfUser.getText().trim();
				strPwd = new String(jpfPwd.getPassword());

				// write to file
				writeBootstrapConfig();

				dispose();
			}
		}
		/* if do not change configuration */
		else if (cmd.equals(command[1])) {
			ok = false;

			dispose();

		} else if (cmd.equals(command[2])) {
			strPort = tfPort.getText().trim();
			strCapacity = tfCapacity.getText().trim();

			strType = ((DB_TYPE) (jcbType.getSelectedItem())).getName();
			strServer = jtfServer.getText().trim();
			strDbPort = jtfDbPort.getText().trim();
			strDbName = jtfDbName.getText().trim();
			strUser = jtfUser.getText().trim();
			strPwd = new String(jpfPwd.getPassword());

			DB db = new DB(((DB_TYPE) jcbType.getSelectedItem()).toString(),
					strServer, strDbPort, strUser, strPwd, strDbName);
			// String dbtype, String address, String port,
			// String user, String pwd, String name
			Connection conn = db.createDbConnection();

			if (conn == null)
				JOptionPane.showMessageDialog(null,
						"Bootstrap DB Setting is wrong");
			else {
				JOptionPane.showMessageDialog(null,
						"Bootstrap DB Setting is successful!");
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private void writeBootstrapConfig() {
		try {
			FileOutputStream out = new FileOutputStream(CONFIG_FILE);
			Properties keys = new Properties();

			keys.put("LOCALSERVERPORT", strPort);
			keys.put("CAPACITY", strCapacity);

			keys.put("bestpeerdb_type", strType);
			keys.put("bestpeerdb_server", strServer);
			keys.put("bestpeerdb_port", strDbPort);
			keys.put("bestpeerdb_dbname", strDbName);
			keys.put("bestpeerdb_username", strUser);
			keys.put("bestpeerdb_password", strPwd);

			keys.store(out, "Bootstrap Server Configuration");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showDialogAlone(){
		// load language config
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"conf/config.ini"));

			String line = null;

			while ((line = reader.readLine()) != null) {
				String[] arrData = line.split("=");
				if (arrData[0].equals("Language")) {
					int language = Integer.parseInt(arrData[1].trim());

					if (language == 0)
						LanguageLoader
								.newLanguageLoader(LanguageLoader.english);
					else if (language == 1)
						LanguageLoader
								.newLanguageLoader(LanguageLoader.chinese);
					else if (language == 2)
						LanguageLoader.newLanguageLoader(LanguageLoader.locale);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		DlgBootstrapConfig dlg = new DlgBootstrapConfig(null,
				"Configure Bootstrap Server", 350, 400, "30000", "10");

		// show dlg
		dlg.setVisible(true);
		
	}
	
	@Override
	protected void processWhenWindowClosing() {

	}

	/*
	 * Run dialog as a standalone config dlg for bootstrap server
	 */
	public static void main(String[] args) {

		DlgBootstrapConfig.showDialogAlone();
		
		System.exit(0);
	}

}