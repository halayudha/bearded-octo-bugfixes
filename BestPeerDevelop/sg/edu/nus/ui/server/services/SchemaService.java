package sg.edu.nus.ui.server.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * @author Wu Sai
 * 
 */
public class SchemaService {

	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		System.out.println("Receive Schema Request From Web Interface");
		String schematype = (String) params.get("schema");
		JSONObject result = new JSONObject();

		if (schematype.equals("local")) {
			result.put("schema", "local");
			Connection conn = ServerPeer.conn_localSchema;

			JSONArray tnamelist = new JSONArray();
			try {
				DatabaseMetaData dbmd = conn.getMetaData();

				ResultSet tables, columns;
				String[] tableTypes = { "TABLE" };

				tables = dbmd.getTables(null, null, null, tableTypes);

				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					tnamelist.add(tableName);

					JSONArray namemap = new JSONArray();
					JSONArray typemap = new JSONArray();

					columns = dbmd.getColumns(null, null, tableName, null);
					while (columns.next()) {
						String cName = columns.getString("COLUMN_NAME");
						String type = columns.getString("TYPE_NAME");
						namemap.add(cName);
						typemap.add(type);
					}
					columns.close();
					result.put("n_" + tableName, namemap);
					result.put("t_" + tableName, typemap);
				}
				tables.close();
				result.put("table_name", tnamelist);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		} 
		else if (schematype.equals("global")) {

			result.put("schema", "global");
			Connection conn = ServerPeer.conn_globalSchema;

			JSONArray tnamelist = new JSONArray();
			try {
				DatabaseMetaData dbmd = conn.getMetaData();

				ResultSet tables, columns;
				String[] tableTypes = { "TABLE" };

				tables = dbmd.getTables(null, null, null, tableTypes);

				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					tnamelist.add(tableName);

					JSONArray namemap = new JSONArray();
					JSONArray typemap = new JSONArray();
					JSONArray dtypemap = new JSONArray();

					columns = dbmd.getColumns(null, null, tableName, null);
					while (columns.next()) {
						String cName = columns.getString("COLUMN_NAME");
						String type = columns.getString("TYPE_NAME");
						int dtype = columns.getInt("DATA_TYPE");
						namemap.add(cName);
						typemap.add(type);
						dtypemap.add(new Integer(dtype));
					}
					columns.close();
					result.put("n_" + tableName, namemap);
					result.put("t_" + tableName, typemap);
					result.put("dt_" + tableName, dtypemap);
				}
				tables.close();
				result.put("table_name", tnamelist);
				
				String[][] mappingInfo = MetaDataAccess.
					dataMappingGetMappingColumns();
				JSONArray maptable = new JSONArray();
				JSONArray mapcolumn = new JSONArray();
				for(int k=0; k<mappingInfo.length; k++){
					maptable.add(mappingInfo[k][0]);
					mapcolumn.add(mappingInfo[k][1]);
				}
				result.put("maptable", maptable);
				result.put("mapcolumn", mapcolumn);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		} 
		else if (schematype.equals("localmatched")) {

			result.put("schema", "localmatched");
			Connection conn = ServerPeer.conn_globalSchema;
			Connection conn2 = ServerPeer.conn_schemaMapping;

			JSONArray tnamelist = new JSONArray();
			try {
				DatabaseMetaData dbmd = conn.getMetaData();
				Statement stmt = conn2.createStatement();

				ResultSet tables, columns;
				String[] tableTypes = { "TABLE" };

				tables = dbmd.getTables(null, null, null, tableTypes);

				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					String query = "select count(*) count from matches where "
							+ "sourceTable = '" + tableName + " '";
					ResultSet rs = stmt.executeQuery(query);
					rs.next();
					int count = rs.getInt("count");
					if (count == 0)// no match
						continue;
					rs.close();

					tnamelist.add(tableName);

					JSONArray namemap = new JSONArray();
					JSONArray typemap = new JSONArray();

					columns = dbmd.getColumns(null, null, tableName, null);
					while (columns.next()) {
						String cName = columns.getString("COLUMN_NAME");
						query = "select * from matches where "
								+ "sourceTable = '" + tableName
								+ " ' and sourceColumn = '" + cName + "'";
						rs = stmt.executeQuery(query);
						if (rs.next()) {
							int type = columns.getInt("DATA_TYPE");
							namemap.add(cName);
							typemap.add(new Integer(type));
						}
						rs.close();
					}
					columns.close();
					result.put("n_" + tableName, namemap);
					result.put("t_" + tableName, typemap);
				}
				tables.close();
				result.put("table_name", tnamelist);
				stmt.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		} 
		else if (schematype.equals("exported")) {

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
					String tableName = tables.getString("TABLE_NAME");
					tnamelist.add(tableName);

					JSONArray namemap = new JSONArray();
					JSONArray typemap = new JSONArray();

					columns = dbmd.getColumns(null, null, tableName, null);
					while (columns.next()) {
						String cName = columns.getString("COLUMN_NAME");
						int type = columns.getInt("DATA_TYPE");
						namemap.add(cName);
						typemap.add(new Integer(type));
					}
					columns.close();
					result.put("n_" + tableName, namemap);
					result.put("t_" + tableName, typemap);
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
					if (!indexed.contains(cname))
						indexed.add(cname);
				}
				result.put("i_" + lastname, indexed);
				rs.close();
				stmt.close();

				result.put("table_name", tnamelist);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		return result;
	}
}
