package sg.edu.nus.dbconnection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.Vector;

import sg.edu.nus.logging.LogManager;

/**
 * Modified by chensu
 * 
 * @author chensu
 * @version 2009-05-07
 * 
 */
public class DBProperty {
	private Properties prop = null;

	public String erpdb_type = null;
	public String erpdb_server = null;
	public String erpdb_port = null;
	public String erpdb_username = null;
	public String erpdb_password = null;
	public String erpdb_dbName = null;

	public String bestpeerdb_type = null;
	public String bestpeerdb_server = null;
	public String bestpeerdb_port = null;
	public String bestpeerdb_username = null;
	public String bestpeerdb_password = null;
	public String bestpeerdb_dbName = null;

	public static String FileName = "./conf/connection.ini";

	/**
	 * Constructor: initialize, read property file name
	 * 
	 * @param FileName
	 *            Property file name
	 */
	public DBProperty() {
		try {
			FileInputStream infile = new FileInputStream(FileName);
			prop = new Properties();
			prop.load(infile);
			infile.close();
		} catch (FileNotFoundException e) {
			LogManager.LogException("Configure file not found:" + FileName, e);
			System.exit(1);
		} catch (IOException e) {
			LogManager
					.LogException("Cannot load configure file " + FileName, e);
			System.exit(1);
		}

		this.erpdb_type = prop.getProperty("erpdb_type");
		this.erpdb_server = prop.getProperty("erpdb_server");
		this.erpdb_port = prop.getProperty("erpdb_port");
		this.erpdb_username = prop.getProperty("erpdb_username");
		this.erpdb_password = prop.getProperty("erpdb_password");
		this.erpdb_dbName = prop.getProperty("erpdb_dbname");

		this.bestpeerdb_type = prop.getProperty("bestpeerdb_type");
		this.bestpeerdb_server = prop.getProperty("bestpeerdb_server");
		this.bestpeerdb_port = prop.getProperty("bestpeerdb_port");
		this.bestpeerdb_username = prop.getProperty("bestpeerdb_username");
		this.bestpeerdb_password = prop.getProperty("bestpeerdb_password");
		this.bestpeerdb_dbName = prop.getProperty("bestpeerdb_dbname");
	}

	public void put_erpdb_configure(Vector<String> vec) {
		prop.put("erpdb_type", vec.get(0));
		prop.put("erpdb_server", vec.get(1));
		prop.put("erpdb_port", vec.get(2));
		prop.put("erpdb_username", vec.get(3));
		prop.put("erpdb_password", vec.get(4));
		prop.put("erpdb_dbname", vec.get(5));

		try {
			FileOutputStream oFile = new FileOutputStream(FileName, false);
			prop.store(oFile, "update erpdb");
			oFile.close();
		} catch (Exception e) {
			LogManager.LogException("Fail to write config file.", e);
		}
	}

	public void put_bestpeer_configure(Vector<String> vec) {
		prop.put("bestpeerdb_type", vec.get(0));
		prop.put("bestpeerdb_server", vec.get(1));
		prop.put("bestpeerdb_port", vec.get(2));
		prop.put("bestpeerdb_username", vec.get(3));
		prop.put("bestpeerdb_password", vec.get(4));
		prop.put("bestpeerdb_dbname", vec.get(5));

		try {
			FileOutputStream oFile = new FileOutputStream(FileName, false);
			prop.store(oFile, "update bestpeerdb");
			oFile.close();
		} catch (Exception e) {
			LogManager.LogException("Fail to write config file.", e);
		}
	}

	public static void setConfigFile(String string) {
		FileName = string;
	}

	public DB getERPDB() {
		return new DB(this.erpdb_type, this.erpdb_server, this.erpdb_port,
				this.erpdb_username, this.erpdb_password, this.erpdb_dbName);
	}

	public DB getBestpeerDB() {
		return new DB(this.bestpeerdb_type, this.bestpeerdb_server,
				this.bestpeerdb_port, this.bestpeerdb_username,
				this.bestpeerdb_password, this.bestpeerdb_dbName);
	}

	public Connection getBestpeerDBConn() {
		return getBestpeerDB().createDbConnection();
	}

	public Connection getERPDBConn() {
		return getERPDB().createDbConnection();
	}
}
