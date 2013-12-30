package sg.edu.nus.accesscontrol;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.sqlparser.SelectStatement;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Implement helper class for access control processing.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class AccessControlHelper {

	private static void addPrivilege(String[][] privileges,
			Vector<String> privilegeTalbes, Vector<String> privilegeColumns,
			Vector<String> privilegeRows) {
		if (privileges == null) {
			return;
		}

		for (int i = 0; i < privileges.length; i++) {
			if (privileges[i][1].contains(MetaDataAccess.WHOLE_TABLE)) {
				privilegeTalbes.add(privileges[i][0]);
			} else if (privileges[i][1].contains(MetaDataAccess.COLUMN_LEVEL)) {
				privilegeColumns.add(privileges[i][0]);
			} else if (privileges[i][1].contains(MetaDataAccess.ROW_LEVEL)) {
				privilegeRows.add(privileges[i][0]);
			}
		}

	}

	private static void addPrivilegeOfRole(String role,
			Vector<String> privilegeTalbes, Vector<String> privilegeColumns,
			Vector<String> privilegeRows) {

		String[][] privileges = MetaDataAccess.metaGetRoleGrantedPrivilege(
				ServerPeer.conn_metabestpeerdb, role);

		addPrivilege(privileges, privilegeTalbes, privilegeColumns,
				privilegeRows);

	}

	public static void getAllPrivelegeOfUser(String userName,
			Vector<String> privilegeTalbes, Vector<String> privilegeColumns,
			Vector<String> privilegeRows) {

		String[][] privileges = MetaDataAccess.metaGetUserGrantedPrivilege(
				ServerPeer.conn_metabestpeerdb, userName);

		addPrivilege(privileges, privilegeTalbes, privilegeColumns,
				privilegeRows);

		String[][] grantedRoles = MetaDataAccess.metaGetUserGrantedRole(
				ServerPeer.conn_metabestpeerdb, userName);
		
		if (grantedRoles != null) {
			for (int i = 0; i < grantedRoles.length; i++) {
				String role = grantedRoles[i][0];
				addPrivilegeOfRole(role, privilegeTalbes, privilegeColumns,
						privilegeRows);

				// deal with role hierarchies
				String[][] roleHierchies = MetaDataAccess
						.metaGetRoleGrantedRole(ServerPeer.conn_metabestpeerdb,
								role);
				if (roleHierchies != null) {
					for (int j = 0; j < roleHierchies.length; j++) {
						String subRole = roleHierchies[j][0];
						addPrivilegeOfRole(subRole, privilegeTalbes,
								privilegeColumns, privilegeRows);
					}
				}
			}
		}

	}

	private static boolean isMappingTableName(String tableName){
		return tableName.contains("mapping_table");
	}
	
	private static boolean isMappingColumnName(String colName){
		return colName.contains("_global_term");
	}
	
	/**
	 * Check access control function 
	 * @param userName: user who want to retrieve data
	 * @param orginalSql: the sql intend to execute on a peer to for the user's request 
	 * @return the modidified sql that does not violate constraints any more, for e.g: removing un-authorized column, adding where condition for row level acesscontrol
	 * @return null if the original totally violates access control and can not be re-written
	 */
	public static String checkAccessControl(String userName, String orginalSql) {

		Vector<String> privilegeTalbes = new Vector<String>();
		Vector<String> privilegeColumns = new Vector<String>();
		Vector<String> privilegeRows = new Vector<String>();

		getAllPrivelegeOfUser(userName, privilegeTalbes, privilegeColumns,
				privilegeRows);

		SqlHandler sqlHandler = new SqlHandler();
		sqlHandler.parseSql(new String(orginalSql));

		ArrayList<String> tables = sqlHandler.getTableList();
		ArrayList<String> columns = sqlHandler.getColumnList();
		String whereCond = sqlHandler.getWhereClause();
		
		// check table privilege
		boolean wholeTableAccess = true;

		for (int i = 0; i < tables.size(); i++) {
			String queryTable = tables.get(i);
			if (isMappingTableName(queryTable))
				continue;
			boolean exists = findString(queryTable, privilegeTalbes);
			wholeTableAccess = wholeTableAccess && exists;
		}

		if (wholeTableAccess) {
			// check if physical column has been exported by user
			ArrayList<String> newColumns = new ArrayList<String>();
			String queryTable = tables.get(0);
			Vector<String> exportedColumns = MetaDataAccess
			.metaGetLocalColumnsExportedOfTable(
					ServerPeer.conn_metabestpeerdb, queryTable);

			for (int c = 0; c < columns.size(); c++) {
				String orgColName = columns.get(c);
				
				if (isMappingColumnName(orgColName)){
					newColumns.add(orgColName);
					continue;
				}
							
				boolean exist = false;
				for (int k = 0; k < exportedColumns.size(); k++) {
					String expCol = exportedColumns.get(k);
					if (equalColumnName(orgColName,expCol)) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					if (orgColName.equals("*")) {
						newColumns.add("*");
					} else {
						String[][] columnTypes = MetaDataAccess
								.metaGetColumnsWithType(
										ServerPeer.conn_metabestpeerdb,
										queryTable);
						for (int col = 0; col < columnTypes.length; col++) {
							if (equalColumnName(orgColName,columnTypes[col][0])) {
								String virtualValue = "0"; // 0 for col with
															// number
								// value, "null" for
								// column with string
								// value
								if (columnTypes[col][1].contains("varchar") || columnTypes[col][1].contains("text")) {
									virtualValue = "'null'";
								}
								newColumns.add(virtualValue);
							}
						}
					}
				}
				else {
					newColumns.add(orgColName);
				}
			}
			sqlHandler.setColumnList(newColumns);
		}
		
		else {//not whole table access, so check row and column access

			boolean grantedRowLevel = false;

			for (int i = 0; i < privilegeRows.size(); i++) {
				String privilege = privilegeRows.get(i);
				int seperator = privilege.indexOf('.');
				String grantedTable = privilege.substring(0, seperator);
				seperator = seperator + "WHERE".length() + 1;

				String condition = privilege.substring(seperator, privilege
						.length());
				condition = condition.replace("|", "'");
				// ME: access control, user can only see his records...
				condition = condition.replace("=me", "= '"+userName+"'");

				if (findString(grantedTable, tables)) {
					grantedRowLevel = true;
					if (whereCond == null) {
						whereCond = "(" + condition + ")";
					} else {
						whereCond += " AND (" + condition + ")";
					}
				}
			}

			if (!grantedRowLevel) {// user have no access on any row also
				// rewrite the query sothat it return no results
				String abnormal = " 1 > 2 ";
				if (whereCond == null) {
					whereCond = "(" + abnormal + ")";
				} else {
					whereCond += " AND (" + abnormal + ")";
				}
			}
			sqlHandler.setWhereClause(whereCond);

			// cheking column level access
			
			ArrayList<String> newColumns = new ArrayList<String>();
			String queryTable = tables.get(0);
			for (int c = 0; c < columns.size(); c++) {
				String orgColName = columns.get(c);
				
				if (isMappingColumnName(orgColName)){
					newColumns.add(orgColName);
					continue;
				}
				
				// /check if physical column exist
				Vector<String> exportedColumns = MetaDataAccess
						.metaGetLocalColumnsExportedOfTable(
								ServerPeer.conn_metabestpeerdb, queryTable);
				boolean exist = false;
				for (int k = 0; k < exportedColumns.size(); k++) {
					String expCol = exportedColumns.get(k);
					if (equalColumnName(orgColName,expCol)) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					if (orgColName.equals("*")) {
						newColumns.add("*");
					} else {
						String[][] columnTypes = MetaDataAccess
								.metaGetColumnsWithType(
										ServerPeer.conn_metabestpeerdb,
										queryTable);
						for (int col = 0; col < columnTypes.length; col++) {
							if (equalColumnName(orgColName, columnTypes[col][0])) {
								String virtualValue = "0"; // 0 for col with
															// number
								// value, "null" for
								// column with string
								// value
								if (columnTypes[col][1].contains("varchar") || columnTypes[col][1].contains("text")) {
									virtualValue = "'null'";
								}
								newColumns.add(virtualValue);
							}
						}
					}
					continue; // process next column
				}

				// /check privileges...
				boolean colGranted = false;
				for (int i = 0; i < privilegeColumns.size(); i++) {
					String privilege = privilegeColumns.get(i);
					int seperator = privilege.indexOf('.');
					String grantedTable = privilege.substring(0, seperator);

					if (queryTable.equals(grantedTable)) {

						seperator = seperator + 1;
						String grantedColumn = privilege.substring(seperator,
								privilege.length());
							if (equalColumnName(orgColName,grantedColumn)) {							
							colGranted = true;
							newColumns.add(orgColName);
							break;
						}
					}
				}
				if (!colGranted) {
					if (orgColName.equals("*")) {
						newColumns.add("*");
					} else {
						String[][] columnTypes = MetaDataAccess
								.metaGetColumnsWithType(
										ServerPeer.conn_metabestpeerdb,
										queryTable);
						for (int col = 0; col < columnTypes.length; col++) {
							if (equalColumnName(orgColName,columnTypes[col][0])) {
								String virtualValue = "0"; // 0 for col with
															// number
								// value, "null" for
								// column with string
								// value
								if (columnTypes[col][1].contains("varchar") || columnTypes[col][1].contains("text")) {
									virtualValue = "'null'";
								}
								newColumns.add(virtualValue);
							}
						}
					}
				}
			}
			sqlHandler.setColumnList(newColumns);
		}
		
		// check access control and rewriting query
		return sqlHandler.regenerateSqlString();
	}

	private static boolean equalColumnName(String queryColName, String metaColName){
		boolean equal = false;
		String colName = getColumnName(queryColName);
		equal = colName.equals(metaColName);
		return equal;
	}
	
	public static String getColumnName(String initialColumn) {
		String[] aggregateTypes = { "sum", "max", "min", "avg", "count" };
		String newColumnName = new String(initialColumn);

		for (int i = 0; i < aggregateTypes.length; i++) {
			if (initialColumn.contains(aggregateTypes[i] + "(")
					|| initialColumn.contains(aggregateTypes[i] + " (")) {
				// recompute
				int pos1 = initialColumn.indexOf("(");
				int pos2 = initialColumn.indexOf(")");
				newColumnName = initialColumn.substring(pos1 + 1, pos2);
				newColumnName = newColumnName.trim();
				break;
			}
		}
		
		//remove table name
		if (newColumnName.contains(".")) {
			int pos = newColumnName.indexOf(".");
			newColumnName = newColumnName.substring(pos + 1);
		}
		
		return newColumnName;
	}
	
	public static String[][] getAccessControlColumns(String userName,
			String queryTable, String[][] orgColumns) {

		String[][] controlledColumns = new String[orgColumns.length][2];

		Vector<String> privilegeTalbes = new Vector<String>();
		Vector<String> privilegeColumns = new Vector<String>();
		Vector<String> privilegeRows = new Vector<String>();

		getAllPrivelegeOfUser(userName, privilegeTalbes, privilegeColumns,
				privilegeRows);

		// check table privilege
		boolean wholeTableAccess = findString(queryTable, privilegeTalbes);

		if (wholeTableAccess) {
			return orgColumns; // full access to table//no need to change
			// anything
		}

		// check column level access

		for (int c = 0; c < orgColumns.length; c++) {
			String orgColName = orgColumns[c][0];
			boolean colGranted = false;
			for (int i = 0; i < privilegeColumns.size(); i++) {
				String privilege = privilegeColumns.get(i);
				int seperator = privilege.indexOf('.');
				String grantedTable = privilege.substring(0, seperator);

				if (queryTable.equals(grantedTable)) {

					seperator = seperator + 1;
					String grantedColumn = privilege.substring(seperator,
							privilege.length());
					if (orgColName.contains(grantedColumn)) {// contains: for
						// case of
						// sum(colname)
						colGranted = true;
						controlledColumns[c][0] = new String(orgColumns[c][0]);
						controlledColumns[c][1] = new String(orgColumns[c][1]);
						break;
					}
				}
			}
			if (!colGranted) {
				String virtualValue = "0"; // 0 for col with number value,
				// "null" for column with string
				// value
				if (orgColumns[c][1].contains("varchar")|| orgColumns[c][1].contains("text")) {
					virtualValue = "null";
				}
				controlledColumns[c][0] = new String(virtualValue);
				controlledColumns[c][1] = new String(orgColumns[c][1]);
			}
		}

		return controlledColumns;
	}

	public static String getAccessControlClause(String queryTable,
			String userName) {

		Vector<String> privilegeTalbes = new Vector<String>();
		Vector<String> privilegeColumns = new Vector<String>();
		Vector<String> privilegeRows = new Vector<String>();

		getAllPrivelegeOfUser(userName, privilegeTalbes, privilegeColumns,
				privilegeRows);

		// check table privilege
		boolean wholeTableAccess = findString(queryTable, privilegeTalbes);

		if (wholeTableAccess) {
			return null; // full access to table
		}

		// because user is not granted whole table access
		// will check row level access
		// boolean grantedRowLevel = false;
		String whereCond = " 1 > 2 "; // abnormal clause for the case this user
		// is not granted any access.

		for (int i = 0; i < privilegeRows.size(); i++) {
			String privilege = privilegeRows.get(i);
			int seperator = privilege.indexOf('.');
			String grantedTable = privilege.substring(0, seperator);
			seperator = seperator + "WHERE".length() + 1;

			String condition = privilege.substring(seperator, privilege
					.length());
			condition = condition.replace("|", "'");

			condition = SelectStatement.pushTableNameToWhereClause(
					grantedTable, condition);

			if (grantedTable.equals(queryTable)) {
				whereCond = condition;
			}
		}
		return whereCond;

	}

	public static boolean findString(String s, ArrayList<String> arrList) {
		boolean exists = false;
		for (int j = 0; j < arrList.size(); j++) {
			String org = arrList.get(j);
			if (org.equals(s)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public static boolean findString(String s, Vector<String> vector) {
		boolean exists = false;
		for (int j = 0; j < vector.size(); j++) {
			String org = vector.get(j);
			if (org.equals(s)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

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

	public static void printData(String[] data) {

		if (data == null)
			return;

		for (int i = 0; i < data.length; i++) {
			System.out.print(data[i] + "\t");
			System.out.println();
		}
	}

	public static void printData(Vector<String> data) {

		if (data == null)
			return;

		for (int i = 0; i < data.size(); i++) {
			System.out.print(data.get(i) + "\t");
		}

		System.out.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Test Helper");

		String sql = "select * from products";

		System.out.println(checkAccessControl("guest", sql));

		sql = "select product_id, quantity_sale, benefit, date_sale  from sales";

		System.out.println(checkAccessControl("guest", sql));

		System.out.println("End Helper");
	}

}
