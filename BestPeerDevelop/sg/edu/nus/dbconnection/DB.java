package sg.edu.nus.dbconnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import sg.edu.nus.logging.LogManager;
import sg.edu.nus.util.DB_TYPE;

public class DB {
	DB_TYPE type;
	String driver = "";
	String address = "";
	String port = "";
	String name = "";
	String user = "";
	String pwd = "";

	private Connection conn;
	private Statement stmt;

	public DB() {
	}

	public DB(String dbtype, String address, String port, String user,
			String pwd, String name) {
		this.type = DB_TYPE.getByType(dbtype);

		this.driver = DB_TYPE.getDriverByType(dbtype);
		this.address = address;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
		this.name = name;
	}

	public String getURL() {
		return DB_TYPE.getURL(type, address, port);
	}

	public String getDNS() {
		return DB_TYPE.getDNS(type, address, port, name);
	}

	public String getDNS(String dbName) {
		return DB_TYPE.getDNS(type, address, port, dbName);
	}

	public Connection createDbConnection() {
		// create connection
		try {
			Class.forName(driver);
			System.out.println(getDNS());
			return DriverManager.getConnection(getDNS(), user, pwd);
		} catch (ClassNotFoundException cnfe) {
			LogManager.LogException("Couldn't find driver " + driver, cnfe);
		} catch (Exception e) {
			LogManager.LogException("Fail to connect to: " + getDNS(), e);
		}
		return null;
	}

	public Connection createDbConnection(String dbName) {
		// create connection
		try {
			Class.forName(driver);
			return DriverManager.getConnection(getDNS(dbName), user, pwd);
		} catch (Exception e) {
			LogManager.LogException("Fail to connect to: " + getDNS(dbName), e);
		}
		return null;
	}

	public Connection createServerConnection() {
		// create connection
		try {
			Class.forName(driver);
			return DriverManager.getConnection(getURL(), user, pwd);
		} catch (Exception e) {
			LogManager.LogException("Fail to connect to: " + getURL(), e);
		}
		return null;
	}

	public String getDbName() {
		return name;
	}

	public String getDriver() {
		return driver;
	}

	public String getServerAddress() {
		return address;
	}

	public String getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}

	public void update(String dbtype, String address, String port, String user,
			String pwd, String name) {
		this.type = DB_TYPE.getByType(dbtype);
		this.driver = DB_TYPE.getDriverByType(dbtype);
		this.address = address;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
		this.name = name;
	}

	public String getDbType() {
		return this.type.getName();
	}

	public boolean testDbConnection() {
		Connection conn = this.createDbConnection();
		if (conn == null)
			return false;
		try {
			conn.close();
		} catch (Exception e) {
			// do nothing
		}
		return true;
	}

	public void checkConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				conn = this.createDbConnection();
				stmt = conn.createStatement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean processUpdate(String updateSQL) {
		try {
			checkConnection();
			stmt.executeUpdate(updateSQL);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ResultSet processQuery(String querySQL) {
		try {
			checkConnection();
			ResultSet rs = stmt.executeQuery(querySQL);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Statement getStatement() {
		checkConnection();
		return this.stmt;
	}
}
