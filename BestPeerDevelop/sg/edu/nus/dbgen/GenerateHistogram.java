package sg.edu.nus.dbgen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Vector;

import sg.edu.nus.dbconnection.DBConnector;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.Histogram;
import sg.edu.nus.util.MetaDataAccess;

public class GenerateHistogram {

	Connection conn_metabestpeer = null;

	Connection conn_exportedDb = null;

	public GenerateHistogram() {
		conn_metabestpeer = ServerPeer.conn_metabestpeerdb;
		conn_exportedDb = ServerPeer.conn_exportDatabase;
	}

	public void generateHistogram() {

		MetaDataAccess.metaDeleteTableStatistics(conn_metabestpeer);
		MetaDataAccess.metaDeleteColumnStatistics(conn_metabestpeer);
		MetaDataAccess.metaDeleteColumnHistogram(conn_metabestpeer);

		String[] globalTables = MetaDataAccess.metaGetTables(conn_metabestpeer);
		Vector<String> tableVect = new Vector<String>();
		Vector<String> exportedTables = DBConnector.getTables(conn_exportedDb);

		for (int i = 0; i < globalTables.length; i++) {
			String globalTab = globalTables[i];
			for (int j = 0; j < exportedTables.size(); j++) {
				String exportedTab = exportedTables.get(j);
				if (globalTab.equals(exportedTab))
					tableVect.add(globalTab);
			}
		}
		
		for (int i = 0; i < tableVect.size(); i++) {

			String tableName = tableVect.get(i);

			// table size stat
			String sql = "select count(*) from " + tableName;
			ResultSet rs = DBConnector.executeQuery(conn_exportedDb, sql);
			String[][] result = DBConnector.getDataArrayStringFromResultSet(rs);
			MetaDataAccess.metaInsertTableStatistics(conn_metabestpeer,
					tableName, result[0][0]);

			String[][] columns = MetaDataAccess.metaGetColumnsWithType(
					conn_metabestpeer, tableName);
			for (int col = 0; col < columns.length; col++) {
				String columnName = columns[col][0];
				String columnType = columns[col][1];

				// column size stat
				sql = "SELECT count(distinct " + columnName + " ) FROM "
						+ tableName;
				rs = DBConnector.executeQuery(conn_exportedDb, sql);
				result = DBConnector.getDataArrayStringFromResultSet(rs);
				MetaDataAccess.metaInsertColumnStatistics(conn_metabestpeer,
						tableName, columnName, result[0][0]);

			}

		}

	}

	public static void main(String[] args) {
		System.out.println("Generate Histogram!");

		System.out.println("End Histogram!");
	}
}
