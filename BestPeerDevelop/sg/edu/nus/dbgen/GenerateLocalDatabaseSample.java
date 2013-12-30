package sg.edu.nus.dbgen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

import javax.swing.JOptionPane;

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

public class GenerateLocalDatabaseSample {
	private Connection conn = null;
	private boolean isConnected = false;

	public GenerateLocalDatabaseSample() {
	}

	public void ConnectToDatabase() {
		int count = 0;
		DBProperty.setConfigFile("./conf/super.ini");
		while (count < 60) {
			try {
				DBProperty dbProperty = new DBProperty();
				conn = dbProperty.getERPDB().createDbConnection();
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
					"sqlscript/localdbsample.sql"));

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

	public void ConnectAndExecute() {
		ConnectToDatabase();
		if (isConnected)
			ExecuteSQLCommand();
		else
			JOptionPane
					.showMessageDialog(null,
							"Cannot connect to the database, please check your settings");

	}

	public void GenerateSampleSql() {
		// generate data for peer 1
		System.out.println("--------------------------");
		System.out.println("//generate data for peer 1");
		char company = 'A';
		int nCompany = 26;
		for (int i = 0; i < 26; i++) {
			String id = new String("" + company);
			String name = "NAME of " + id;
			String desc = "DESCRIPTION of " + id;
			String sql = "insert into company values ('" + id + "','" + name
					+ "','" + desc + "')";
			System.out.println(sql);

			company = (char) (company + 1);
		}

		// generate data for peer 2
		System.out.println();
		System.out.println("--------------------------");
		System.out.println("//generate data for peer 2");
		int numProductPerCompany = 3;
		company = 'A';
		for (int i = 0; i < nCompany / 2; i++) {
			for (int j = 0; j < numProductPerCompany; j++) {
				String compName = new String("" + company);
				String prodId = "product_" + compName + "_" + (j + 1);
				String prodName = "NAME of " + prodId;
				String prodDesc = "DESCRIPTION of " + prodId;
				String sql = "insert into products values ('" + prodId + "','"
						+ prodName + "','" + prodDesc + "','" + compName + "')";
				System.out.println(sql);

			}
			company = (char) (company + 1);
		}

		// generate data for peer 3
		System.out.println();
		System.out.println("--------------------------");
		System.out.println("//generate data for peer 3");
		for (int i = nCompany / 2; i < nCompany; i++) {
			for (int j = 0; j < numProductPerCompany; j++) {
				String compName = new String("" + company);
				String prodId = "product_" + compName + "_" + (j + 1);
				String prodName = "NAME of " + prodId;
				String prodDesc = "DESCRIPTION of " + prodId;
				String sql = "insert into products values ('" + prodId + "','"
						+ prodName + "','" + prodDesc + "','" + compName + "')";
				System.out.println(sql);

			}
			company = (char) (company + 1);
		}

		// generate data for peer 4, 5, 6
		for (int peerNum = 1; peerNum <= 3; peerNum++) {
			System.out.println();
			System.out.println("--------------------------");
			System.out.println("//generate data for peer " + (peerNum + 3));
			company = 'A';
			int quantitySale = peerNum * 100;
			float benefit = peerNum * 1000;
			Random rand = new Random();
			rand.setSeed(System.currentTimeMillis());

			for (int i = 0; i < nCompany; i++) {
				String compName = new String("" + company);
				String prodId = "product_" + compName + "_" + peerNum;
				int date = (rand.nextInt(30) + 1);
				String dateStr = date >= 10 ? "" + date : "0" + date;
				String dateSale = "2009-01-" + dateStr;
				String sql = "insert into sales values ('" + prodId + "',"
						+ quantitySale + "," + benefit + ",'" + dateSale + "')";
				System.out.println(sql);

				dateSale = "2008-12-" + dateStr;
				sql = "insert into sales values ('" + prodId + "',"
						+ (quantitySale / 2) + "," + (benefit / 2) + ",'"
						+ dateSale + "')";
				System.out.println(sql);

				quantitySale += 5;
				benefit += 20;
				company = (char) (company + 1);
			}

		}

	}

	public static void main(String[] args) {
		GenerateLocalDatabaseSample app = new GenerateLocalDatabaseSample();
		app.ConnectAndExecute();
	}

}
