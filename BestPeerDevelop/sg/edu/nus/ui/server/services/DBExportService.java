package sg.edu.nus.ui.server.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sg.edu.nus.db.synchronizer.DataExport;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * @author Wu Sai
 * 
 */
public class DBExportService {

	public JSONObject doService(JSONObject params) {

		System.out.println("Receive export data request from web interface");
		JSONObject result = new JSONObject();

		List allTables = (List) params.get("table_name");

		Vector<String> tableName = new Vector<String>();
		Hashtable<String, Vector<String>> columnsOfTable = new Hashtable<String, Vector<String>>();
		for (int i = 0; i < allTables.size(); i++) {
			String tname = (String) allTables.get(i);
			tableName.add(tname);
			List columns = (List) params.get(tname);
			Vector<String> columnName = new Vector<String>();
			for (int j = 0; j < columns.size(); j++) {
				columnName.add((String) columns.get(j));
			}
			columnsOfTable.put(tname, columnName);
		}

		MetaDataAccess.metaInsertLocalTableExported(
				ServerPeer.conn_metabestpeerdb, tableName);

		MetaDataAccess.metaInsertLocalColumnsExported(
				ServerPeer.conn_metabestpeerdb, columnsOfTable);

		// export data...

		DataExport.exportData(tableName, columnsOfTable);

		// the parameters to send back
		result.put("type", "export_data");
		result.put("schema", "exported");
		Connection conn = ServerPeer.conn_exportDatabase;

		JSONArray tnamelist = new JSONArray();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			// String databaseName = "globaldb";

			ResultSet tables, columns;
			String[] tableTypes = { "TABLE" };

			tables = dbmd.getTables(null, null, null, tableTypes);

			while (tables.next()) {
				String tName = tables.getString("TABLE_NAME");
				tnamelist.add(tName);

				JSONArray namemap = new JSONArray();
				JSONArray typemap = new JSONArray();

				columns = dbmd.getColumns(null, null, tName, null);
				while (columns.next()) {
					String cName = columns.getString("COLUMN_NAME");
					int type = columns.getInt("DATA_TYPE");
					namemap.add(cName);
					typemap.add(new Integer(type));
				}
				columns.close();
				result.put("n_" + tName, namemap);
				result.put("t_" + tName, typemap);
			}
			tables.close();

			Connection conn_index = ServerPeer.conn_bestpeerindexdb;

			Statement stmt = conn_index.createStatement();
			String sql = "select * from local_index order by val";
			ResultSet rs = stmt.executeQuery(sql);
			String lastname = "";
			JSONArray indexed = new JSONArray();
			while (rs.next()) {
				String cname = rs.getString("ind");
				String tname = rs.getString("val");

				if (lastname.length() == 0)
					lastname = tname;

				if (!tname.equals(lastname)) {
					result.put("i_" + lastname, indexed);
					indexed = new JSONArray();
					lastname = tname;
				}
				indexed.add(cname);
			}
			rs.close();
			stmt.close();

			result.put("table_name", tnamelist);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

}
