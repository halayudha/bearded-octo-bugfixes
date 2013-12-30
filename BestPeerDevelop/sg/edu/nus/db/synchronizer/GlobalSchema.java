package sg.edu.nus.db.synchronizer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import sg.edu.nus.peer.ServerPeer;

/**
 * Querying the database to get
 * the global schema.
 * 
 * @author Wu Sai
 *
 */

public class GlobalSchema {

	/**
	 * the name of database that holds the mapping between
	 * the global schema and the local schema
	 */
	/**
	 * sorted array of the namespaces of the attributes
	 */
	private Hashtable<String, String> mapping;

	private Hashtable<String, String> reverseMapping;

	/**
	 * record the primary-foreign key relation
	 */
	private Hashtable<String, String> joinRelation;

	/**
	 * recording the type of a column 
	 */
	private Hashtable<String, String> type;

	/**
	 * connect to the corresponding database
	 */
	private Connection dbconnection;

	/**
	 * store the columns that have been built index in P2P network
	 * 
	 */
	private Hashtable<String, ArrayList<String>> indexedColumn;

	/**
	 * the size of the vector that represents all the 
	 * attributes
	 */
	public static int VECTOR_SIZE = 256;

	/**
	 * the table that stores the index vector
	 */
	public static String VECTOR_TABLE = "vector";

	/**
	 * the temp table that stores the index vector
	 */
	public static String VECTOR_TABLE_TMP = "vector_tmp";

	public static String VECTOR_UPDATE_TABLE = "vector_update";

	/**
	 * constructor
	 * @param driver driver string, e.g. MySQL or Oracle library
	 * @param connectString connect string, e.g. the url and protocol of the connection 
	 * @param user user name
	 * @param password password
	 *
	 */
	public GlobalSchema() {
		mapping = new Hashtable<String, String>();

		reverseMapping = new Hashtable<String, String>();

		joinRelation = new Hashtable<String, String>();

		type = new Hashtable<String, String>();


		indexedColumn = new Hashtable<String, ArrayList<String>>();

		// try to connect the database
		dbconnection = ServerPeer.conn_schemaMapping;

		loadMappingRelation();

		LoadJoinCondition();
	}


	/**
	 * indicate that a column of a specific table is indexed
	 * in the P2P network
	 * @param table table name (global schema)
	 * @param column column name (global schema)
	 */
	public void insertIndexedColumn(String table, String column) {
		if (indexedColumn.containsKey(table)) {
			ArrayList<String> columns = indexedColumn.get(table);
			if (!columns.contains(column)) {
				columns.add(column);
			}
		} else {
			ArrayList<String> columns = new ArrayList<String>();
			columns.add(column);
			indexedColumn.put(table, columns);
		}
	}

	/**
	 * given a table, returns its indexed columns
	 * @param table table name
	 * @return
	 */
	public ArrayList<String> getIndexedColumn(String table) {
		if (indexedColumn.containsKey(table)) {
			return indexedColumn.get(table);
		} else
			return null;
	}

	/**
	 * generate the namespace for a specific column
	 * @param db  database name
	 * @param table  table name
	 * @param column  column name
	 * @return
	 */
	public static String getNamespace(String db, String table, String column) {
		String delim = ".";
		return db + delim + table + delim + column;
	}

	/**
	 * load the mapping relation from the database
	 * to memory
	 */
	private void loadMappingRelation() {
		try {
			Statement st = dbconnection.createStatement();
			String sql = "select * from Matches";
			ResultSet rs = st.executeQuery(sql);

			// loop the result set
			while (rs.next()) {
				String sourceDB = rs.getString("sourceDB");
				String sourceTable = rs.getString("sourceTable");
				String sourceColumn = rs.getString("sourceColumn");
				String targetDB = ServerPeer.EXPORTED_DB;
				String targetTable = rs.getString("targetTable");
				String targetColumn = rs.getString("targetColumn");

				String sourceNamespace = getNamespace(sourceDB, sourceTable,
						sourceColumn);
				String targetNamespace = getNamespace(targetDB, targetTable,
						targetColumn);

				mapping.put(sourceNamespace, targetNamespace);

				reverseMapping.put(targetNamespace, sourceNamespace);
			}

			rs.close();
			st.close();
		} catch (Exception e) {
			System.out.println("fail to load data from mapping tables");
			e.printStackTrace();
		}
	}

	/**
	 * insert the type info of a specific column
	 * @param namespace namespace of the column
	 * @param datatype string representation of the type
	 */
	public void insertTypeInfo(String namespace, String datatype) {
		type.put(namespace, datatype);
	}

	/**
	 * given a column in a specific table of the specific database, returns
	 * its matched column in the global schema
	 * @param db  database name
	 * @param table  table name
	 * @param column  column name
	 * @return a string array that has 3 values, database name, table name and column name
	 */
	public String[] getMappingRelation(String db, String table, String column) {

		String sourceNamespace = getNamespace(db, table, column);

		if (mapping.containsKey(sourceNamespace)) {
			String targetNamespace = mapping.get(sourceNamespace);

			String[] result = targetNamespace.split("\\.");
			return result;
		} else
			return null;
	}

	/**
	 * given a target column in the global schema, returns its source
	 * column in the local schema
	 * @param db
	 * @param table
	 * @param column
	 * @return
	 */
	public String[] getReverseMappingRelation(String db, String table,
			String column) {

		String destinatedNamespace = getNamespace(db, table, column);

		if (reverseMapping.containsKey(destinatedNamespace)) {
			String sourceNamespace = reverseMapping.get(destinatedNamespace);

			String[] result = sourceNamespace.split("\\.");
			return result;
		} else
			return null;
	}

	/**
	 * given a column, returns its position in the vector
	 * @param db  database name
	 * @param table  table name
	 * @param column  column name
	 * @return
	 */
	public static int getVectorPosition(String db, String table, String column) {

		String sourceNamespace = getNamespace(db, table, column);
		int hashvalue = sourceNamespace.hashCode();
		if (hashvalue < 0)
			hashvalue = -hashvalue;
		return hashvalue % GlobalSchema.VECTOR_SIZE;
	}

	/**
	 * insert a new foreign key join relationship
	 * @param db the database name
	 * @param table1 name of the first table
	 * @param table2 name of the second table
	 * @param column1 name of the key attribute in the first table
	 * @param column2 name of the key attribute in the second table
	 */
	public void insertJoinRelation(String db, String table1, String table2,
			String column1, String column2) {

		String key1 = db + "." + table1 + " " + db + "." + table2;
		String key2 = db + "." + table2 + " " + db + "." + table1;

		String data = getNamespace(db, table1, column1) + "="
				+ getNamespace(db, table2, column2);

		joinRelation.put(key1, data);
		joinRelation.put(key2, data);
	}

	/**
	 * test whether two tables can join each other
	 * @param db
	 * @param table1
	 * @param table2
	 * @return the namespaces of the columns that can be used for joining
	 */
	public String[] getJoinableRelation(String db, String table1, String table2) {
		String key = db + "." + table1 + " " + db + "." + table2;

		if (joinRelation.containsKey(key)) {
			String data = joinRelation.get(key);
			int idx = data.indexOf("=");
			String[] result = new String[2];
			result[0] = data.substring(0, idx);
			result[1] = data.substring(idx + 1, data.length());
			return result;
		}
		return null;
	}

	/**
	 * get the data type of a specific column
	 * @param db
	 * @param table
	 * @param column
	 * @return
	 */
	public String getType(String db, String table, String column) {
		String namespace = getNamespace(db, table, column);
		if (type.containsKey(namespace)) {
			return type.get(namespace);
		} else
			return null;
	}
	
	/**
	 * Read all join condition tuple to GlobalSchema
	 * @param gschema
	 */
	public void LoadJoinCondition() {
		Connection conn = ServerPeer.conn_schemaMapping;

		try {
			Statement stmt = conn.createStatement();

			String sql = "select * from joincondition";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String table1Name = rs.getString("table1");
				String column1Name = rs.getString("column1");
				String table2Name = rs.getString("table2");
				String column2Name = rs.getString("column2");

				this.insertJoinRelation("localdb", table1Name, table2Name,
						column1Name, column2Name);
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
