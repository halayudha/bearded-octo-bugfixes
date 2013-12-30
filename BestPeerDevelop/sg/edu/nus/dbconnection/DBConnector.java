/*
 * @(#) DBConnector.java 1.0 2006-1-27
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */
package sg.edu.nus.dbconnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * This class is used for providing the default information in order to connect 
 * to the back-end database.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-27
 */

public class DBConnector {
	/**
	 * Initiate the MySQL driver to be prepared for building
	 * connection with the database.
	 */
	public static void registerDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
	}

	Connection dbconn;

	DBConnector(DB db) {
		dbconn = db.createDbConnection();
	}

	public static ResultSet executeQuery(Connection conn, String selectSQL) {
		try {

			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(selectSQL);

			return rs;
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	public static Vector<Vector<String>> getDataFromResultSet(ResultSet rs) {
		try {

			ResultSetMetaData rsmd = rs.getMetaData();
			int nCol = rsmd.getColumnCount();

			Vector<Vector<String>> rows = new Vector<Vector<String>>();

			while (rs.next()) {
				Vector<String> row = new Vector<String>();
				for (int j = 0; j < nCol; j++) {
					row.add(rs.getString(j + 1));
				}
				rows.add(row);
			}

			return rows;

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	public static String[][] getDataArrayStringFromResultSet(ResultSet rs) {
		String[][] dataArray2D = null;

		Vector<Vector<String>> rows = getDataFromResultSet(rs);

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

	public static Vector<String> getColumnNames(ResultSet rs) {
		try {
			Vector<String> columnIds = new Vector<String>();

			ResultSetMetaData rsmd = rs.getMetaData();
			int nCol = rsmd.getColumnCount();

			for (int i = 0; i < nCol; i++) {
				columnIds.add(rsmd.getColumnName(i + 1));
			}

			return columnIds;

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return null;
	}

	public static void printRS(ResultSet rs) {
		if (rs == null) {
			System.out.println("NULL result set");
		}
		try {
			ResultSetMetaData md = rs.getMetaData();
			int count = md.getColumnCount();
			for (int i = 1; i <= count; i++) {
				System.out.print(md.getColumnLabel(i));
				System.out.print('\t');
			}
			System.out.println();
			while (rs.next()) {
				for (int i = 1; i <= count; i++) {
					System.out.print(rs.getObject(i));
					System.out.print('\t');
				}
				System.out.println();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get all tables in the database
	 * @return
	 */
	public static Vector<String> getTables(Connection conn) {
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getTables(null, null, null, null);
			Vector<String> tables = new Vector<String>();
			while (rSet.next()) {
				String tableName = rSet.getString(3);
				tables.addElement(tableName);
			}
			rSet.close();
			return tables;
		} catch (Exception e) {
			System.out
					.println("Error while retrieving names of tables in the database: "
							+ e.getMessage());
			return null;
		}
	}
	
    public static Connection getConnection(String DSN, String USER, String PWD)
    {
    	
    	try
    	{
    		return DriverManager.getConnection(DSN, USER, PWD);
    	}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Cannot create connection!!!!");
		}
		return null;
    }
    
	public static Vector<String> getColumnsOfTable(Connection conn, String tableName) {

		try {
			DatabaseMetaData metaData = conn.getMetaData();
			
			ResultSet rSet = metaData.getColumns(null,
					null, tableName, "%");
			
			ResultSetMetaData rsmd = rSet.getMetaData();
			int nCol = rsmd.getColumnCount();

			Vector<String> columns = new Vector<String>();
			while (rSet.next()) {
				String colName = rSet.getString(4);
				columns.addElement(colName);
			}
			rSet.close();
			return columns;
		} catch (Exception e) {
			System.out
					.println("Error while retrieving names of tables in the database: "
							+ e.getMessage());
			return null;
		}		
	}
		
}