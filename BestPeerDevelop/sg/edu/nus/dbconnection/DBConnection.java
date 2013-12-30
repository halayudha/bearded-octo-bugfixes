package sg.edu.nus.dbconnection;

import java.sql.*;

/**
 * Create a database connection instance
 * @author Wu Sai
 *
 */
public class DBConnection {

	public static String MYSQLL_DRIVER = "com.mysql.jdbc.Driver";

	public String connectString;

	public String user;

	public String password;

	public String dbName;

	public String driver = DBConnection.MYSQLL_DRIVER;

	public static DBConnection instance = null;

	private Connection conn;

	private Statement stmt;

	private boolean isConnected = false;

	private DBConnection() {
		connectString = "jdbc:mysql://localhost:3306/";
		user = "";
		password = "";
	}

	public static DBConnection getInstance() {
		if (instance == null)
			instance = new DBConnection();
		return instance;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public void setDBName(String name) {
		this.dbName = name;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setConnectString(String connString) {
		this.connectString = connString;
	}

	public boolean setupConnection() {
		if (isConnected)
			return true;
		try {
			isConnected = true;
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(connectString + dbName, user,
					password);
			stmt = conn.createStatement();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Statement createStatement() {
		try {
			Statement st = conn.createStatement();
			return st;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean processUpdate(String updateSQL) {
		try {
			stmt.executeUpdate(updateSQL);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ResultSet processQuery(String querySQL) {
		try {

			ResultSet rs = stmt.executeQuery(querySQL);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public DatabaseMetaData getMetadata() {
		try {
			return conn.getMetaData();
		} catch (Exception e) {
			return null;
		}
	}

	public void close() {
		try {
			stmt.close();
			conn.close();
			this.isConnected = false;
		} catch (Exception e) {

		}
	}
}
