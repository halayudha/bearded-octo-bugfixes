package sg.edu.nus.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBConnector;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.protocol.MsgType;

/**
 * Helper class for accessing meta data of Corporate BestPeer Now for storing
 * global schema and access control management Later should integrate index db
 * into it...
 * 
 * @author VHTam
 * 
 */
public class MetaDataAccess {

	// follows are for global schema
	public static final String TABLE_CORPORAT_DBNAME = "CORPORATE_DB_NAME";
	public static final String TABLE_SCHEMAS = "GLOBAL_SCHEMAS";
	public static final String TABLE_COLUMNS = "GLOBAL_SCHEMA_COLUMNS";
	public static final String SCHEMA_SEPERATOR = ";";
	public static final String KEYWORD_TABLE = " TABLE ";
	public static final String TABLE_SCHEMAS_STAT = "GLOBAL_SCHEMAS_STAT";
	public static final String TABLE_COLUMNS_STAT = "GLOBAL_SCHEMA_COLUMNS_STAT";
	public static final String TABLE_COLUMNS_HIST = "GLOBAL_SCHEMA_COLUMNS_HIST";

	// follows are for access control
	public static final String TABLE_LOCAL_ADMINS = "LOCAL_ADMINS";
	public static final String TABLE_ROLE_HIER = "ROLE_HIERARCHY";
	public static final String TABLE_ROLE_PERM = "ROLE_PERMISSIONS";
	public static final String TABLE_USER_ROLE = "USER_ROLE_ASSIGNMENT";
	public static final String TABLE_ROLES = "ROLES";
	public static final String TABLE_USERS = "USERS";
	public static final String TABLE_PRIVILEGES = "PRIVILEGES";
	public static final String TABLE_USER_PERM = "USER_PERMISSIONS";

	public static final String TABLE_REPORT = "REPORT";
	
	//local data exported info
	public static final String TABLE_LOCAL_EXPORTED_SCHEMAS = "LOCAL_EXPORTED_SCHEMAS";
	public static final String TABLE_LOCAL_EXPORTED_SCHEMA_COLUMNS = "LOCAL_EXPORTED_SCHEMA_COLUMNS";
	
	//data mapping
	//public static final String TABLE_GLOBAL_TERM = "GLOBAL_TERM";
	public static final String TABLE_DATA_MAPPING = "MAPPING_TABLE";
	public static final String TABLE_SEMANTIC_MAPPING = "SEMANTIC_MAPPING";

	//keyword
	public static String WHOLE_TABLE = "whole table";
	public static String ROW_LEVEL = "row level";
	public static String COLUMN_LEVEL = "column level";
	public static String DOT_SEPERATOR = ".";
	public static String WHERE = "WHERE";

	public static void printData(String[][] data) {

		if (data == null)
			return;

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				System.out.print(data[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public static void printData(String[]data) {

		if (data == null)
			return;

		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);

		}
	}
	
	//
	public static String metaGetCorporateDbName(Connection con) {
		String[] tables = new String[] { TABLE_CORPORAT_DBNAME };
		String[] columns = new String[] { "db_name" };

		String[][] result = getDataArray2DFromDB(con, tables, columns);
		if (result == null) {
			return "Corporate Database";
		}

		return result[0][0];
	}

	// for schema metadata update from schema string
	public static void updateSchema(Connection con, String schemaStr) {

		if (schemaStr == null || schemaStr.length() == 0)
			return;

		// delete old metadata of global schema first
		String sql = "delete from " + TABLE_SCHEMAS;
		metaExecuteUpdate(con, sql);
		sql = "delete from " + TABLE_COLUMNS;
		metaExecuteUpdate(con, sql);

		// insert new metadata of new global schema
		// not suppport column with primary key
		String[] createSql = schemaStr.split(SCHEMA_SEPERATOR);
		for (int i = 0; i < createSql.length; i++) {
			String tableSql = createSql[i];

			int start = tableSql.indexOf(KEYWORD_TABLE)
					+ KEYWORD_TABLE.length();
			int end = tableSql.indexOf(" ", start);
			String tableName = tableSql.substring(start, end);
			tableName = tableName.trim();
			metaAddNewTable(con, tableName);

			start = tableSql.indexOf("(");
			end = tableSql.lastIndexOf(")");
			String columnStr = tableSql.substring(start + 1, end);

			String[] columns = columnStr.split(",");
			for (int j = 0; j < columns.length; j++) {
				String colNameWithType = columns[j].trim();
				int spacePos = colNameWithType.indexOf(" ");
				String colName = colNameWithType.substring(0, spacePos);
				colName = colName.trim();
				String colType = colNameWithType.substring(spacePos,
						colNameWithType.length());
				colType = colType.trim();
				metaAddNewColumn(con, tableName, colName, colType);
			}
		}

	}
	
	// for schema metadata update from schema string
	public static void updateSchema(Connection con, String[][] globalSchemas, String[][] globalSchemaColumns) {

		// delete old metadata of global schema first
		String sql = "delete from " + TABLE_SCHEMAS;
		metaExecuteUpdate(con, sql);
		sql = "delete from " + TABLE_COLUMNS;
		metaExecuteUpdate(con, sql);

		for (String[] row: globalSchemas){
			metaAddNewTable(con, row[0], row[1], row[2]);
		}

		for (String[] row: globalSchemaColumns){
			metaAddNewColumn(con, row[0], row[1], row[2], row[3]);
		}

	}

	/*
	 *  update metadata when create in table in global schema
	 */
	public static void metaAddNewTable(Connection con, String tableName, String tableDesc, String tableType) {
		try {
			Statement stm = con.createStatement();
			String sql = "insert into " + TABLE_SCHEMAS + " values('"
					+ tableName + "','";
			sql += tableDesc + "','";
			sql += tableType+"')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	/*
	 *  update metadata when add a column into table in global schema
	 */
	public static void metaAddNewColumn(Connection con, String tableName,
			String columnName, String columnDesc, String columnType) {
		try {
			Statement stm = con.createStatement();
			String sql = "insert into " + TABLE_COLUMNS + " values('"
					+ tableName + "','" + columnName + "','";
			sql += columnDesc + "','";
			sql += columnType + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}
	
	/*
	 *  update metadata when create in table in global schema
	 */
	public static void metaAddNewTable(Connection con, String tableName) {
		try {
			Statement stm = con.createStatement();
			String sql = "insert into " + TABLE_SCHEMAS + " values('"
					+ tableName + "','";
			sql += tableName + " description" + "','";
			sql += "table')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	/*
	 *  update metadata when add a column into table in global schema
	 */
	public static void metaAddNewColumn(Connection con, String tableName,
			String columnName, String columnType) {
		try {
			Statement stm = con.createStatement();
			String sql = "insert into " + TABLE_COLUMNS + " values('"
					+ tableName + "','" + columnName + "','";
			sql += columnName + " description" + "','";
			sql += columnType + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	/*
	 * execute update query into database
	 */
	public static void metaExecuteUpdate(Connection con, String sql) {
		try {
			Statement stm = con.createStatement();

			stm.executeUpdate(sql);

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	/**
	 * @author chensu
	 * @version 2009-4-29
	 */
	public static int metaCheckLoginLocalAdmin(Connection con,
			String localAdminName, String password) {

		String whereCond = " admin_name = '" + localAdminName + "'";

		String[] tables = new String[] { TABLE_LOCAL_ADMINS };
		String[] columns = new String[] { "password" };

		String[][] result = getDataArray2DFromDB(con, tables, columns,
				whereCond);

		if (result == null)
			return MsgType.USER_NOT_EXIST.getValue();

		if (result[0][0].compareTo(password) == 0)
			return MsgType.LACK.getValue();

		return MsgType.WRONG_PASS.getValue();
	}

	/*
	 * get local admins
	 */
	public static String[] metaGetLocalAdmins(Connection con) {
		String[] tables = new String[] { TABLE_LOCAL_ADMINS };
		String[] columns = new String[] { "admin_name" };

		String[][] temp = getDataArray2DFromDB(con, tables, columns);
		if (temp == null) {
			return null;
		}

		String[] result = new String[temp.length];
		for (int i = 0; i < temp.length; i++) {
			result[i] = new String(temp[i][0]);
		}
		return result;
	}

	/*
	 *  
	 */
	public static void metaAddLocalAdmin(Connection con, String userName,
			String userDesc, String password) {
		try {
			Statement stm = con.createStatement();
			String sql = "insert into " + TABLE_LOCAL_ADMINS + " values('"
					+ userName + "','" + userDesc + "','";
			sql += password + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	/*
	 *  
	 */
	public static void metaDeleteLocalAdmin(Connection con, String userName) {
		try {
			Statement stm = con.createStatement();
			String sql = "delete from " + TABLE_LOCAL_ADMINS
					+ " where admin_name = '";
			sql += userName + "'";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// /// read meta data for bestpeer from metadatabestpeer
	public static String[][] getDataArray2DFromDB(Connection con,
			String[] tableNames, String[] columnNames) {
		return getDataArray2DFromDB(con, tableNames, columnNames, null);
	}

	public static String[][] getDataArray2DFromDB(Connection con,
			String[] tableNames, String[] columnNames, String whereCond) {
		String[][] dataArray2D = null;

		Vector<Vector<String>> rows = getDataVectorFromDB(con, tableNames,
				columnNames, whereCond);

		if (rows.size() == 0) {
			return null;
		}

		int rowCount = rows.size();
		int colCount = rows.get(0).size();

		dataArray2D = new String[rowCount][colCount];

		for (int r = 0; r < rowCount; r++) {

			Vector<String> row = rows.get(r);

			for (int c = 0; c < colCount; c++) {

				dataArray2D[r][c] = row.get(c);
			}

		}

		return dataArray2D;
	}

	public static Vector<Vector<String>> getDataVectorFromDB(Connection con,
			String[] tableNames, String[] columnNames) {
		return getDataVectorFromDB(con, tableNames, columnNames, null);
	}

	public static Vector<Vector<String>> getDataVectorFromDB(Connection con,
			String[] tableNames, String[] columnNames, String whereCond) {

		String sql = "SELECT ";
		for (int i = 0; i < columnNames.length; i++) {
			String seperator = (i == columnNames.length - 1) ? "\n" : ", ";
			sql += columnNames[i] + seperator;
		}
		sql += "FROM ";
		for (int i = 0; i < tableNames.length; i++) {
			String seperator = (i == tableNames.length - 1) ? "\n" : ", ";
			sql += tableNames[i] + seperator;
		}

		if (whereCond != null) {
			sql += "WHERE " + whereCond;
		}


		Statement st;
		ResultSet rs;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			ResultSetMetaData rsmd = rs.getMetaData();
			int nCol = rsmd.getColumnCount();

			Vector<Vector<String>> rows = new Vector<Vector<String>>();

			while (rs.next()) {
				Vector<String> row = new Vector<String>();
				for (int j = 0; j < nCol; j++) {
					String item = rs.getString(j + 1);
					row.add(item);
				}
				rows.add(row);
			}

			return rows;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("---CAN NOT EXECUTE: " + sql);
		}

		return null;
	}

	public static String[] metaGetTables(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_SCHEMAS };
		String[] columnNames = { "schema_name" };
		String whereCond = "schema_type = 'table'";
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);
		if (data == null) {
			return null;
		}

		String[] result = new String[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = data[i][0];
		}

		return result;
	}

	public static String[][] metaGetTablesWillAllInfo(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_SCHEMAS };
		String[] columnNames = { "*" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);
		if (data == null) {
			return null;
		}

		return data;
	}
	
	public static String[][] metaGetColumns(Connection con, String tableName) {

		if (con == null) {
			return null;
		}

		tableName = tableName.toLowerCase();

		String[] tableNames = { MetaDataAccess.TABLE_COLUMNS };
		String[] columnNames = { "column_name" };
		String whereCond = "schema_name = '" + tableName + "'";
		String[][] result = MetaDataAccess.getDataArray2DFromDB(con,
				tableNames, columnNames, whereCond);

		return result;
	}

	public static String[][] metaGetColumnsWithType(Connection con,
			String tableName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_COLUMNS };
		String[] columnNames = { "column_name", "column_type" };
		String whereCond = "schema_name = '" + tableName + "'";
		String[][] result = MetaDataAccess.getDataArray2DFromDB(con,
				tableNames, columnNames, whereCond);

		return result;
	}

	public static String[][] metaGetColumnsWithAllInfo(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_COLUMNS };
		String[] columnNames = { "*"};
		String[][] result = MetaDataAccess.getDataArray2DFromDB(con,
				tableNames, columnNames);

		return result;
	}
	
	public static String metaGetColumnType(Connection con,
			String tableName, String columnName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_COLUMNS };
		String[] columnNames = { "column_name", "column_type" };
		String whereCond = "schema_name = '" + tableName + "'";
		String[][] result = MetaDataAccess.getDataArray2DFromDB(con,
				tableNames, columnNames, whereCond);

		for (int i=0; i<result.length; i++){
			if (result[i][0].equals(columnName))
				return new String(result[i][1]);
		}
		return null;
	}

	public static String[] metaGetRoles(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLES };
		String[] columnNames = { "role_name" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);
		String[] result = new String[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = data[i][0];
		}

		return result;
	}

	public static String[][] metaGetUserGrantedRole(Connection con,
			String userName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_USER_ROLE,
				MetaDataAccess.TABLE_ROLES };
		String[] columnNames = { MetaDataAccess.TABLE_ROLES + ".role_name",
				"role_desc" };
		String whereCond = MetaDataAccess.TABLE_ROLES + ".role_name" + " = "
				+ MetaDataAccess.TABLE_USER_ROLE + ".role_name" + " and "
				+ MetaDataAccess.TABLE_USER_ROLE + ".user_name = '" + userName
				+ "'";
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetUserDescPasswd(Connection con,
			String userName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_USERS };
		String[] columnNames = { "user_desc", "password" };
		String whereCond = "user_name = '" + userName + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetUserGrantedPrivilege(Connection con,
			String userName) {

		if (con == null) {
			return null;
		}
		String privilege_id = "select";

		String[] tableNames = { MetaDataAccess.TABLE_USER_PERM };
		String[] columnNames = { "object", "permission_type" };
		String whereCond = "user_name = '" + userName + "' and "
				+ "lower(privilege_id) = '" + privilege_id + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetUserGrantedPrivilegeWithPrivilegeId(
			Connection con, String userName) {

		if (con == null) {
			return null;
		}
		String privilege_id = "select";

		String[] tableNames = { MetaDataAccess.TABLE_USER_PERM };
		String[] columnNames = { "privilege_id", "object", "permission_type" };
		String whereCond = "user_name = '" + userName + "' and "
				+ "lower(privilege_id) = '" + privilege_id + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	// /////// reading information of a role

	public static String[][] metaGetRoleGrantedRole(Connection con,
			String roleName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLE_HIER,
				MetaDataAccess.TABLE_ROLES };
		String[] columnNames = { MetaDataAccess.TABLE_ROLES + ".role_name",
				"role_desc" };
		String whereCond = MetaDataAccess.TABLE_ROLES + ".role_name" + " = "
				+ MetaDataAccess.TABLE_ROLE_HIER + ".sub_role_name" + " and "
				+ MetaDataAccess.TABLE_ROLE_HIER + ".super_role_name = '"
				+ roleName + "'";
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetRoleDesc(Connection con, String roleName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLES };
		String[] columnNames = { "role_desc" };
		String whereCond = "role_name = '" + roleName + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetRoleGrantedPrivilege(Connection con,
			String roleName) {

		if (con == null) {
			return null;
		}
		String privilege_id = "select";

		String[] tableNames = { MetaDataAccess.TABLE_ROLE_PERM };
		String[] columnNames = { "object", "permission_type" };
		String whereCond = "role_name = '" + roleName + "' and "
				+ "lower(privilege_id) = '" + privilege_id + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetRoleGrantedPrivilegeWithPrivilegeId(
			Connection con, String roleName) {

		if (con == null) {
			return null;
		}
		String privilege_id = "select";

		String[] tableNames = { MetaDataAccess.TABLE_ROLE_PERM };
		String[] columnNames = { "privilege_id", "object", "permission_type" };
		String whereCond = "role_name = '" + roleName + "' and "
				+ "lower(privilege_id) = '" + privilege_id + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	// ///////

	public static String[] metaGetAvailPrivileges(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_PRIVILEGES };
		String[] columnNames = { "privilege_id" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);
		String[] result = new String[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = data[i][0];
		}

		return result;
	}

	public static String[][] metaGetRolesWithDescripton(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLES };
		String[] columnNames = { "role_name", "role_desc" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);

		return data;
	}

	public static String[] metaGetUsers(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_USERS };
		String[] columnNames = { "user_name" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);
		String[] result = new String[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = data[i][0];
		}

		return result;
	}

	/*
	 *  add new user
	 */
	public static void metaAddNewUser(Connection con, String userName,
			String userDesc, String pwd) {
		try {
			Statement stm = con.createStatement();
			String sql = "insert into " + TABLE_USERS + " values('" + userName
					+ "','";
			sql += userDesc + "','";
			sql += pwd + "')";
			// System.out.println(sql);

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// read and store role meta data

	// work with privilege table
	public static String[][] metaGetFullPrivileges(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_PRIVILEGES };
		String[] columnNames = { "*" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);

		return data;
	}

	public static void metaStorePrivileges(Connection con, String[][] data) {

		// delete old data first then store new one
		String sql = "delete from " + TABLE_PRIVILEGES;
		metaExecuteUpdate(con, sql);

		for (int i = 0; i < data.length; i++) {
			String priId = data[i][0];
			String priName = data[i][1];
			String priDesc = data[i][2];

			metaInsertPrivilege(con, priId, priName, priDesc);
		}

	}

	public static void metaInsertPrivilege(Connection con, String priId,
			String priName, String priDesc) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_PRIVILEGES + " values('"
					+ priId + "','";
			sql += priName + "','";
			sql += priDesc + "')";

			// System.out.println(sql);

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// work with role hierarchy
	public static String[][] metaGetFullRoleHierachy(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLE_HIER };
		String[] columnNames = { "*" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);

		return data;
	}

	public static void metaStoreRoleHierachy(Connection con, String[][] data) {

		if (data == null)
			return;
		// delete old data first then store new one
		String sql = "delete from " + TABLE_ROLE_HIER;
		metaExecuteUpdate(con, sql);

		for (int i = 0; i < data.length; i++) {
			String superRole = data[i][0];
			String subRole = data[i][1];

			metaInsertRoleHierarchy(con, superRole, subRole);
		}

	}

	public static void metaInsertRoleHierarchy(Connection con,
			String superRole, String subRole) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_ROLE_HIER + " values('"
					+ superRole + "','";
			sql += subRole + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// work with role
	public static String[][] metaGetFullRole(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLES };
		String[] columnNames = { "*" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);

		return data;
	}

	public static void metaStoreRole(Connection con, String[][] data) {

		// delete old data first then store new one
		String sql = "delete from " + TABLE_ROLES;
		metaExecuteUpdate(con, sql);

		for (int i = 0; i < data.length; i++) {
			String role_name = data[i][0];
			String role_desc = data[i][1];

			metaInsertRole(con, role_name, role_desc);
		}

	}

	public static void metaInsertRole(Connection con, String role_name,
			String role_desc) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_ROLES + " values('" + role_name
					+ "','";
			sql += role_desc + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// work with role permission
	public static String[][] metaGetFullRolePermission(Connection con) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_ROLE_PERM };
		String[] columnNames = { "*" };
		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames);

		return data;
	}

	public static void metaStoreRolePermission(Connection con, String[][] data) {

		if (data == null)
			return;
		// delete old data first then store new one
		String sql = "delete from " + TABLE_ROLE_PERM;
		metaExecuteUpdate(con, sql);

		for (int i = 0; i < data.length; i++) {
			String role_name = data[i][0];
			String privilege_id = data[i][1];
			String object = data[i][2];
			String permission_type = data[i][3];

			metaInsertRolePermission(con, role_name, privilege_id, object,
					permission_type);
		}

	}

	public static void metaInsertRolePermission(Connection con,
			String role_name, String privilege_id, String object,
			String permission_type) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_ROLE_PERM + " values('"
					+ role_name + "','";
			sql += privilege_id + "','";
			sql += object + "','";
			sql += permission_type + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// function for Histogram
	public static void metaDeleteDataFromTable(Connection con, String tableName) {

		// delete old data first then store new one
		String sql = "delete from " + tableName;
		metaExecuteUpdate(con, sql);
	}

	public static void metaDeleteTableStatistics(Connection con) {
		metaDeleteDataFromTable(con, TABLE_SCHEMAS_STAT);
	}

	public static void metaDeleteColumnStatistics(Connection con) {
		metaDeleteDataFromTable(con, TABLE_COLUMNS_STAT);
	}

	public static void metaDeleteColumnHistogram(Connection con) {
		metaDeleteDataFromTable(con, TABLE_COLUMNS_HIST);
	}

	public static void metaInsertTableStatistics(Connection con,
			String tableName, String tableSize) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_SCHEMAS_STAT + " values('"
					+ tableName + "', ";
			sql += tableSize + ")";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}
	}

	public static void metaInsertColumnStatistics(Connection con,
			String tableName, String columnName, String colSize) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_COLUMNS_STAT + " values('"
					+ tableName + "', '" + columnName + "', ";
			sql += colSize + ")";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	public static void metaInsertColumnHists(Connection con, String tableName,
			String columnName, int nBin, double max, double min,
			double binWidth, String histValues) {
		try {
			Statement stm = con.createStatement();

			String sql = "insert into " + TABLE_COLUMNS_HIST + " values('"
					+ tableName + "', '" + columnName + "', ";
			sql += nBin + "," + max + "," + min + "," + binWidth + ",'"
					+ histValues + "')";

			stm.executeUpdate(sql);

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}

	}

	// work with table statistics
	public static String[][] metaGetColumnStats(Connection con, String tableName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_COLUMNS_STAT };
		String[] columnNames = { "column_name", "n_distinct_value" };
		String whereCond = MetaDataAccess.TABLE_COLUMNS_STAT + ".schema_name"
				+ " = '" + tableName + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static String[][] metaGetColumnHists(Connection con, String tableName) {

		if (con == null) {
			return null;
		}

		String[] tableNames = { MetaDataAccess.TABLE_COLUMNS_HIST };
		String[] columnNames = { "column_name", "n_bin", "max", "min",
				"bin_width", "bin_values" };
		String whereCond = MetaDataAccess.TABLE_COLUMNS_HIST + ".schema_name"
				+ " = '" + tableName + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);

		return data;
	}

	public static int metaGetTableStats(Connection con, String tableName) {

		if (con == null) {
			return 0;
		}

		String[] tableNames = { MetaDataAccess.TABLE_SCHEMAS_STAT };
		String[] columnNames = { "table_size" };
		String whereCond = MetaDataAccess.TABLE_SCHEMAS_STAT + ".schema_name"
				+ " = '" + tableName + "'";

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames,
				columnNames, whereCond);
		if (data == null) {
			return 0;
		}

		return Integer.parseInt(data[0][0]);
	}

	public static void metaInsertLocalTableExported(Connection con, Vector<String> tables) {
		try {
			Statement stm = con.createStatement();
			//delete old data first
			//metaDeleteDataFromTable(con, TABLE_LOCAL_EXPORTED_SCHEMAS);
			
			Vector<String> exportedTables = metaGetLocalTableExported(con);
			
			//insert new meta data
			for (int i=0; i<tables.size(); i++){
				String tableName = tables.get(i);
				
				if (exportedTables!=null && exportedTables.contains(tableName)){
					continue;
				}
				
				String sql = "insert into " + TABLE_LOCAL_EXPORTED_SCHEMAS + " values('"
					+ tableName + "', '";
				sql += tableName+" description" + "')";
				stm.executeUpdate(sql);
			}

		} catch (SQLException e) {
			 
			e.printStackTrace();
		}
	}
	
	public static Vector<String> metaGetLocalTableExported(Connection con){
		String[] tableNames = { MetaDataAccess.TABLE_LOCAL_EXPORTED_SCHEMAS };
		String[] columnNames = { "schema_name" };

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames);
		if (data==null)
			return null;
		Vector<String> tables = new Vector<String>();
		for (int i=0; i<data.length; i++){
			tables.add(data[i][0]);
		}
		return tables;
	}

	public static void metaInsertLocalColumnsExported(Connection con, Hashtable<String, Vector<String>> columnsOfTable) {
		try {
			Statement stm = con.createStatement();
			
			Enumeration<String> tables = columnsOfTable.keys();
			
			while (tables.hasMoreElements()){
				
				String table = tables.nextElement();
				
				//delete all previous exported column of this table
				String sql = "delete from "+ TABLE_LOCAL_EXPORTED_SCHEMA_COLUMNS + " where schema_name = '" + table + "'";

				stm = con.createStatement();
				stm.executeUpdate(sql);
				
				//insert new one
				Vector<String> columns = columnsOfTable.get(table);
							
				for (int i=0; i<columns.size(); i++){
					String col = columns.get(i);
					
					sql = "insert into " + TABLE_LOCAL_EXPORTED_SCHEMA_COLUMNS + " values('"
						+ table + "', '";
					sql += col+"', '";
					sql += "column description')";
					stm.executeUpdate(sql);					
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Hashtable<String, Vector<String>> metaGetLocalColumnsExported(Connection con){
		Hashtable<String, Vector<String>> columnsOfTable = new Hashtable<String, Vector<String>>();	
		String[] tableNames = { MetaDataAccess.TABLE_LOCAL_EXPORTED_SCHEMA_COLUMNS };
		String[] columnNames = { "schema_name", "column_name" };

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames);
		if (data==null)
			return null;
		
		Vector<String> tables = metaGetLocalTableExported(con);
			
		for (int i=0; i<tables.size(); i++){
			String table = tables.get(i);
			Vector<String> columns = new Vector<String>();
			for (int j=0; j<data.length; j++){
				if (table.equals(data[j][0])){
					columns.add(data[j][1]);
				}
			}
			columnsOfTable.put(table, columns);
		}
		return columnsOfTable;
   }


	public static Vector<String> metaGetLocalColumnsExportedOfTable(Connection con, String globalTable){
		
		String[] tableNames = { "Matches" };
		String[] columnNames = { "sourceTable", "sourceColumn", "targetTable", "targetColumn" };
		String whereCond = "targetTable='"+globalTable+"'";
		
		String[][] matches = MetaDataAccess.getDataArray2DFromDB(ServerPeer.conn_schemaMapping, tableNames, columnNames, whereCond);
		if (matches==null)
			return null;

		tableNames = new String[]{ MetaDataAccess.TABLE_LOCAL_EXPORTED_SCHEMA_COLUMNS };
		columnNames = new String[]{ "schema_name", "column_name" };

		String[][] exported = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames);
		if (exported==null)
			return null;		

		Vector<String> columns = new Vector<String>();
		
		for (String[] match: matches){
			for (String[] export: exported){
				if (match[0].equals(export[0]) && match[1].equals(export[1])){
					columns.add(match[3]);
				}
			}
		}
		
		return columns;
		
	}
	//get dbindex
	public static String[][] metaGetTableIndex(Connection con){
		
		String[] tableNames = { "table_index" };
		String[] columnNames = { "ind", "val" };

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames);
		
		return data;
	}
	
	public static String[][] metaGetRangeIndexString(Connection con){
		
		String[] tableNames = { "range_index_string" };
		String[] columnNames = { "table_name", "column_name", "val", "lower_bound", "upper_bound" };

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames);
		
		return data;
	}

	public static String[][] metaGetRangeIndexNumber(Connection con){
		
		String[] tableNames = { "range_index_number" };
		String[] columnNames = { "table_name", "column_name", "val", "lower_bound", "upper_bound" };

		String[][] data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames);
		
		return data;
	}

	//for report query
	
	public static String[][] metaGetReportQuery(Connection con, String userName){
		String data[][] = null;
		
		String[] tableNames = { TABLE_REPORT, TABLE_USER_ROLE};
		String[] columnNames = {"report_name", "sql_string", "param", "description", "report_category", "create_by"  };
		String whereCond = TABLE_REPORT + ".role_name = " + TABLE_USER_ROLE + ".role_name and "  
		+ TABLE_USER_ROLE +".user_name"+ " = '" + userName + "'";
		
		data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames, whereCond);
		return data;

	}
	
	public static void metaInsertReportQuery(Connection con, String reportName, String userName, String sqlString, String param, String description, String report_category){
		
		String[][] data = MetaDataAccess.metaGetUserGrantedRole(con, userName);
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				String roleName = data[i][0];
				String sql = "insert into " + TABLE_REPORT + " values ('"
						+ reportName + "','" + roleName + "','" + sqlString
						+ "','" + param 
						+ "','" + description 
						+ "','" + report_category
						+ "','" + userName + "')";
				
				try {
					Statement stm = con.createStatement();
					stm.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean checkValidUser(Connection con, String userName, String password){	
		String[][] data = null;
		String[] tableNames = { TABLE_USERS};
		String[] columnNames = {"user_name"};
		String whereCond = TABLE_USERS + ".user_name = '" + userName +"' and " + TABLE_USERS + ".password ='"  
		+ password + "'";
		
		data = MetaDataAccess.getDataArray2DFromDB(con, tableNames, columnNames, whereCond);
		
		return (data!=null);
	}
	
	///////////////////
	//for data mapping
	//////////////////
	public static String[] dataMappingGetGlobalTerm(String tableName, String colName){
		return dataMappingGetGlobalTerm(ServerPeer.conn_exportDatabase, tableName, colName);
	}
	private static String[] dataMappingGetGlobalTerm(Connection conn, String tableName, String colName){
		String[][] data = null;
		

		String[] tableNames = {TABLE_SEMANTIC_MAPPING};
		String[] columnNames = {"global_term"};
		String whereCond = " table_name = '" + tableName +"' and " + " column_name ='"  
		+ colName + "'";
		
		data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames, whereCond);
		
		if (data==null){
			return null;
		}
		String[] result = new String[data.length];
		
		for (int i = 0; i<data.length; i++){
			result[i] = data[i][0];
		}
		return result;

	}
	
	public static String[][] dataMappingGetExistingMapping(String tableName, String colName,String userName){
		return dataMappingGetExistingMapping(ServerPeer.conn_exportDatabase, tableName, colName, userName);
	}
	private static String[][] dataMappingGetExistingMapping(Connection conn, String tableName, String colName,String userName){
		String[][] data = null;
		String[] tableNames = {TABLE_DATA_MAPPING};
		String[] columnNames = {"local_term", "global_term"};
		String whereCond = TABLE_DATA_MAPPING + ".table_name = '" + tableName +"' and " + TABLE_DATA_MAPPING + ".column_name ='"  
		+ colName + "'" + " and user_name ='"+userName+"'";
		
		data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames, whereCond);
		
		if (data==null){
			return null;
		}
		
		return data;
	}
	
	public static void dataMappingInsertMapping(String localTerm, String globalTerm, String tableName,
			String colName, String userName) {
		dataMappingInsertMapping(ServerPeer.conn_exportDatabase, localTerm, globalTerm, tableName, colName, userName);
	}	
	private static void dataMappingInsertMapping(Connection conn,
			String localTerm, String globalTerm, String tableName,
			String colName, String userName) {
		
		String sql = "insert into " + TABLE_DATA_MAPPING + " values ('" + localTerm
				+ "','" + globalTerm + "','" + tableName + "','" + colName + "','" + userName + "')";
		try {
			Statement stm = conn.createStatement();
			stm.executeUpdate(sql);
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void dataMappingDeleteMapping(String localTerm, String tableName,
			String colName, String userName) {
		dataMappingDeleteMapping(ServerPeer.conn_exportDatabase, localTerm, tableName, colName, userName);
	}
	private static void dataMappingDeleteMapping(Connection conn,
			String localTerm, String tableName, String colName, String userName) {
		String sql = "delete from " + TABLE_DATA_MAPPING + " where ";
		sql += " local_term = '" + localTerm + "'";
		sql += " and table_name = '" + tableName + "'";
		sql += " and column_name = '" + colName + "'";
		sql += " and user_name = '" + userName + "'";
		try {
			Statement stm = conn.createStatement();
			stm.executeUpdate(sql);
			System.out.println(sql);
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static String[] dataMappingGetLocalTerm(String tableName, String colName){
		return dataMappingGetLocalTerm(ServerPeer.conn_exportDatabase, tableName, colName);
	}
	private static String[] dataMappingGetLocalTerm(Connection conn, String tableName, String colName){
		String[][] data = null;
		String[] tableNames = {tableName};
		String[] columnNames = {colName};
		
		data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames);
		
		if (data==null){
			return null;
		}
		
		Vector<String> result = new Vector<String>();
		for (int i = 0; i<data.length; i++){
			if (!result.contains(data[i][0]))
				result.add(data[i][0]);
		}
		
		String[] arr = new String[result.size()];
		result.copyInto(arr);
		return arr;		
	} 
	
	public static boolean isMappingColumn(String colName, String tableName){
		return isMappingColumn(ServerPeer.conn_metabestpeerdb, colName, tableName); 
	}
	private static boolean isMappingColumn(Connection conn, String colName, String tableName){
		//check column description in global schema and return
		String[][] mappingColumns = dataMappingGetMappingColumns(conn);
		
		for (String[] row: mappingColumns){
			if (row[0].equals(tableName) && row[1].equals(colName))
				return true;
		}
		return false;
	}

	public static String[][] dataMappingGetMappingColumns(){
		return dataMappingGetMappingColumns(ServerPeer.conn_metabestpeerdb);
	}
	private static String[][] dataMappingGetMappingColumns(Connection conn){
		String[] tableNames = {TABLE_COLUMNS};
		String[] columnNames = {"schema_name", "column_name", "column_desc"};
		
		String[][] data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames);
		
		Vector<String[]> result = new Vector<String[]>();
		
		if (data==null){
			return null;
		}
		
		for (String[] row: data){
			//System.out.println(row[2]);
			if (row[2].contains("isMappingColumn")){
				result.add(row);
				
			}
		}
		
		return result.toArray(new String[0][0]);
		
	}
	
	public static String localTermToGlobalTerm(	String localTerm, String tableName,
			String colName, String userName) {
		return localTermToGlobalTerm(ServerPeer.conn_exportDatabase, localTerm, tableName, colName, userName);
	}
	private static String localTermToGlobalTerm(Connection conn,
			String localTerm, String tableName,	String colName, String userName) {
		String[][] data = null;
		String[] tableNames = {TABLE_DATA_MAPPING};
		String[] columnNames = {"global_term"};
		String whereCond = TABLE_DATA_MAPPING + ".table_name = '" + tableName +"' and " + TABLE_DATA_MAPPING + ".column_name ='"  
		+ colName + "'" + " and local_term ='"+localTerm+"'" + " and user_name ='"+userName+"'";
		
		data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames, whereCond);
		
		if (data!=null){
			return data[0][0];
		} 
		else {//for the case when user did not define data mapping, we check in semantic mapping to get the best term
			tableNames = new String[]{TABLE_SEMANTIC_MAPPING};
			columnNames = new String[]{"global_term", "semantics"};
			whereCond = TABLE_SEMANTIC_MAPPING + ".table_name = '" + tableName +"' and " + TABLE_SEMANTIC_MAPPING + ".column_name ='"  
			+ colName + "'";		
			data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames, whereCond);
			if (data!=null){
				for(String[] row: data){
					String globalTerm = row[0];
					String[] localTerms = row[1].split(";");
					for (String lTerm: localTerms){
						if (localTerm.equals(lTerm)){
							return globalTerm;
						}
					}
				}
			}
		}
		
		return localTerm;
		
	}

	public static String[] dataMappingGetSemanticMappings(String tableName, String colName, String globalTerm){
		return dataMappingGetSemanticMappings(ServerPeer.conn_exportDatabase, tableName, colName, globalTerm);
	}
	private static String[] dataMappingGetSemanticMappings(Connection conn, String tableName, String colName, String globalTerm){
		String[][] data = null;
		String[] tableNames = {TABLE_SEMANTIC_MAPPING};
		String[] columnNames = {"semantics"};
		String whereCond = TABLE_SEMANTIC_MAPPING + ".table_name = '" + tableName +"' and " + TABLE_SEMANTIC_MAPPING + ".column_name ='"  
		+ colName + "' and global_term = '" + globalTerm+"'";
		
		data = MetaDataAccess.getDataArray2DFromDB(conn, tableNames, columnNames, whereCond);
		
		if (data==null){
			return null;
		}
		String[] result = data[0][0].split(";");
		
		return result;

	}
	
	public static void main(String[] args){
		
		System.out.println("Test Meta data access!");
		
		 String DSN = "jdbc:mysql://localhost:3307/exported_db";
		 String USER = "root";
		 String PWD = "s3";
		 DBConnector.registerDriver();
		 Connection conn = DBConnector.getConnection(DSN, USER, PWD);

		 System.out.println("\n-----Semantic Mapping-------");
		 String[] semanticList = dataMappingGetSemanticMappings(conn, "test", "test_name","heart rate");
		 printData(semanticList);

		 System.out.println("\n-----Global term-------");
		 String[] globalTerm = dataMappingGetGlobalTerm(conn, "test", "test_name");
		 printData(globalTerm);

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
