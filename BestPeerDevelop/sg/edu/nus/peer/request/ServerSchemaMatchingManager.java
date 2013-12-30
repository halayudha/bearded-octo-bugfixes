/**
 * 
 */
package sg.edu.nus.peer.request;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import sg.edu.nus.peer.ServerPeer;

/**
 * 
 * Provides methods to add matches between the local schema and the global schema
 * 
 * @author mihailupu
 *
 */
public class ServerSchemaMatchingManager {

	private Connection db_conn;
	private Statement stmt;

	public ServerSchemaMatchingManager(ServerPeer serverpeer) {

		try {
			db_conn = ServerPeer.conn_schemaMapping;
			stmt = db_conn.createStatement();
		} catch (Exception e) {
			System.out
					.println("Error while opening a connection to database server: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	public int addMatch(String sourceColumn, String targetColumn) {
		if (sourceColumn == null || sourceColumn.trim().equals("")
				|| targetColumn == null || targetColumn.trim().equals("")) {
			return -1;// error
		}

		// check to see that the source column is real
		String[] components = sourceColumn.split("\\.");
		String sdbName = components[0];
		String stableName = components[1];
		String scolumnName = components[2];

		// check to see if the target column is real
		components = targetColumn.split("\\.");
		String tdbName = components[0];
		String ttableName = components[1];
		String tcolumnName = components[2];

		String sVersion = "1";
		String tVersion = "1";
		String sType = "1";
		String tType = "1";

		try {
			stmt.executeQuery("USE " + ServerPeer.MATCHES_DB);
			
			stmt.executeUpdate("INSERT INTO Matches VALUES (\'" + sdbName
					+ "\',\'" + stableName + "\',\'" + sVersion + "\',\'"
					+ scolumnName + "\',\'" + sType + "\',\'" + tdbName
					+ "\',\'" + ttableName + "\',\'" + tVersion + "\',\'"
					+ tcolumnName + "\',\'" + tType + "\')");
			
			String sql = "INSERT INTO Matches VALUES (\'" + sdbName
			+ "\',\'" + stableName + "\',\'" + sVersion + "\',\'"
			+ scolumnName + "\',\'" + sType + "\',\'" + tdbName
			+ "\',\'" + ttableName + "\',\'" + tVersion + "\',\'"
			+ tcolumnName + "\',\'" + tType + "\')";
			
			System.out.println("Contruct mapping:\n" + sql);
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return -4;
		}
		return 0;
	}

	public int removeMatch(String sourceColumn, String targetColumn) {
		String[] components;
		String sdbName = null;
		String stableName = null;
		String scolumnName = null;
		String tdbName = null;
		String ttableName = null;
		String tcolumnName = null;

		if (sourceColumn != null && sourceColumn.trim().length() > 0) {
			components = sourceColumn.split("\\.");
			sdbName = components[0];
			stableName = components[1];
			scolumnName = components[2];
		}

		if (targetColumn != null && targetColumn.trim().length() > 0) {
			components = targetColumn.split("\\.");
			tdbName = components[0];
			ttableName = components[1];
			tcolumnName = components[2];
		}

		try {
			// stmt.executeQuery("USE "+ServerPeer.MATCHES_DB);

			String query = "DELETE from Matches WHERE 1 ";
			// add those conditions that are not null or empty
			if (sdbName != null && !sdbName.trim().equals(""))
				query = query + " AND sourceDB=\'" + sdbName + "\'";
			if (stableName != null && !stableName.trim().equals(""))
				query = query + " AND sourceTable=\'" + stableName + "\'";
			if (scolumnName != null && !scolumnName.trim().equals(""))
				query = query + " AND sourceColumn=\'" + scolumnName + "\'";

			// modify by Han Xixian July 22 2008
			if (tdbName != null && !tdbName.trim().equals(""))
				query = query + " AND targetDB=\'" + tdbName + "\'";
			if (ttableName != null && !ttableName.trim().equals(""))
				query = query + " AND targetTable=\'" + ttableName + "\'";
			if (tcolumnName != null && !tcolumnName.trim().equals(""))
				query = query + " AND targetColumn=\'" + tcolumnName + "\'";

			stmt.executeUpdate(query);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return -1;
		}
		return 0;
	}
}
