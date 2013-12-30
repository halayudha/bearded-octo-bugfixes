package sg.edu.nus.dbgen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import javax.swing.JOptionPane;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;

/**
 * 
 * @author Han Xixian
 * 
 * @version Sep-4-2008
 * 
 *          This class is used to generate required databases and tables.
 * 
 */

public class GenerateRequiredDatabaseForServer {
	private Connection conn = null;
	private boolean isConnected = false;

	public GenerateRequiredDatabaseForServer() {
	}

	public void ConnectToDatabase() {
		int count = 0;
		DBProperty.setConfigFile("./conf/super.ini");
		while (count < 60) {
			try {
				DBProperty dbProperty = new DBProperty();
				DB db = new DB(dbProperty.bestpeerdb_type,
						dbProperty.bestpeerdb_server,
						dbProperty.bestpeerdb_port,
						dbProperty.bestpeerdb_username,
						dbProperty.bestpeerdb_password,
						dbProperty.bestpeerdb_dbName);
				conn = db.createDbConnection();
				isConnected = true;
				return;
			} catch (Exception e) {
				e.printStackTrace();
				count++;
			}

			try {
				Thread.sleep(1000);
			} catch (Exception e) {

			}
		}

	}

	public boolean isConnected() {
		return this.isConnected;
	}

	public void ExecuteSQLCommand() {
		if (conn == null) {
			return;
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"sqlscript/serversqlcommand.sql"));

			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.indexOf("--") == -1 && !line.equals("")) {
					System.out.println(line);

					try {
						Statement stmt = conn.createStatement();

						stmt.execute(line);
					} catch (Exception e) {
						System.out
								.println("--------------ERROR-----------------");
						System.out.println("FAIL to process the sql statement:"
								+ line);
						System.out
								.println("PLEASE check all Sql and run again");
						e.printStackTrace();
						System.exit(1);
					}
				}
			}

			System.out.println("Database and tables are successfully created");

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GenerateRequiredDatabaseForServer app = new GenerateRequiredDatabaseForServer();

		app.ConnectToDatabase();

		if (app.isConnected)
			app.ExecuteSQLCommand();
		else
			JOptionPane
					.showMessageDialog(null,
							"Cannot connect to the database, please check your settings");
	}
}
