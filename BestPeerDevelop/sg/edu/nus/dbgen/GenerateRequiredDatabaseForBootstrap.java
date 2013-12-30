package sg.edu.nus.dbgen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import javax.swing.JOptionPane;

import sg.edu.nus.dbconnection.DBProperty;

public class GenerateRequiredDatabaseForBootstrap {
	private Connection conn = null;
	private boolean isConnected = false;

	public GenerateRequiredDatabaseForBootstrap() {

	}

	public void ConnectToDatabase() {
		DBProperty.setConfigFile("./conf/bootstrap.ini");
		DBProperty prop = new DBProperty();

		int count = 0;
		while (count < 60) {
			try {
				conn = prop.getBestpeerDBConn();
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
					"sqlscript/bootstrapsqlcommand.sql"));

			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.indexOf("--") == -1 && !line.equals("")) {
					System.out.println(line);

					try {
						Statement stmt = conn.createStatement();

						stmt.execute(line);
					} catch (Exception e) {
						e.printStackTrace();
						System.out
								.println("--------------ERROR-----------------");
						System.out.println("FAIL to process the sql statement:"
								+ line);
						System.out
								.println("PLEASE check all Sql and run again");
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
		GenerateRequiredDatabaseForBootstrap app = new GenerateRequiredDatabaseForBootstrap();

		app.ConnectToDatabase();

		if (app.isConnected)
			app.ExecuteSQLCommand();
		else
			JOptionPane
					.showMessageDialog(null,
							"Cannot connect to the database, please check your settings");
	}
}
