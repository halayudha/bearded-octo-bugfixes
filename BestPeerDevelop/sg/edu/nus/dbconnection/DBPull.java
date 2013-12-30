package sg.edu.nus.dbconnection;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

public class DBPull {
	private DBProperty property = new DBProperty();
	Connection dbconn;
	private Statement stmt;
	private Vector<Vector<String>> mappingList;
	private String sourceDB;

	public void setMapping(Vector<Vector<String>> mappingList, String sourceDB,
			String destinationDB) {
		this.mappingList = mappingList;
		this.sourceDB = sourceDB;
	}

	public void ExportDB() {
		dbconn = property.getBestpeerDBConn();

		try {
			stmt = dbconn.createStatement();
			String sql, phase1, phase2;
			for (int i = 0; i < this.mappingList.size(); i++) {
				Vector<String> mapping = this.mappingList.get(i);
				phase1 = "";
				phase2 = "";
				for (int j = 2; j < mapping.size(); j += 2) {
					phase1 += mapping.get(j) + ",";
					phase2 += mapping.get(j + 1) + ",";
				}
				sql = "insert into " + mapping.get(0) + "("
						+ phase1.substring(0, phase1.length() - 1) + ")" + " "
						+ "select " + phase2.substring(0, phase2.length() - 1)
						+ " " + "from " + this.sourceDB + "." + mapping.get(1);
				stmt.execute(sql);
			}
			stmt.close();
			dbconn.close();
		} catch (Exception e) {
			System.out.println("Error while exporting data");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DBPull dbPull = new DBPull();
		Vector<Vector<String>> mappingList = new Vector<Vector<String>>();
		Vector<String> mapping1 = new Vector<String>();
		mapping1.add("album");
		mapping1.add("album");
		mapping1.add("album_title");
		mapping1.add("album_title");
		mappingList.add(mapping1);
		Vector<String> mapping2 = new Vector<String>();
		mapping2.add("singer");
		mapping2.add("singer");
		mapping2.add("singer_name");
		mapping2.add("singer_name");
		mappingList.add(mapping2);
		dbPull.setMapping(mappingList, "localdb", "bestpeerdb");
		dbPull.ExportDB();
	}
}
