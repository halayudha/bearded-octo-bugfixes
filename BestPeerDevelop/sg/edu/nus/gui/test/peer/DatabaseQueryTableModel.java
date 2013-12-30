package sg.edu.nus.gui.test.peer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.ServerPeer;

/**
 * This DatabaseQueryTableModel is a basic implementation of TableModel interface 
 * that fills out a Vector from a query's result set
 * 
 * 
 * Here we test this class by a temporal database operation.
 * 
 * @author Han Xixian
 * @version August-6-2008
 *
 */

public class DatabaseQueryTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6456832955674354035L;

	/**
	 * cache is used to store the tuple information obtained by specified query.
	 */
	Vector<String[]> cache = null;

	/**
	 * headers is used to specify the column information of table
	 */
	private String[] headers = null;

	/**
	 * These parameters are used for testing.
	 */
	private Connection conn = null;
	private String databaseName = "localdb";

	public DatabaseQueryTableModel() {
		cache = new Vector<String[]>();
		headers = new String[1];
		conn = ServerPeer.conn_exportDatabase;
	}

	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName the databaseName to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * Initialize Database to obtain the connection to specified database
	 */
	public void initDB() {
		if (this.databaseName == null)
			return;

		if (conn != null)
			return;

		try {
			conn = AbstractPeer.bestpeer_db.createDbConnection(databaseName);
		} catch (Exception e) {
			LogManager.LogException("Can't initialize Database", e);
		}
	}

	/**
	 * close the connection to specified database
	 */
	public void closeDB() {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			LogManager.LogException("Can't close Database", e);
		}
	}

	/**
	 * setQuery is used to execute the specified query, and return the results
	 * @param sql
	 */
	public void setQuery(String sql) {
		if (this.databaseName == null)
			return;

		cache.clear();
		headers = null;

		try {
			Statement stmt = conn.createStatement();

			stmt.setFetchSize(1000);

			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			headers = new String[rsmd.getColumnCount()];

			for (int i = 1; i <= headers.length; i++)
				headers[i - 1] = rsmd.getColumnName(i);

			int rowid = 0;
			while (rs.next()) {
				String[] tuple = new String[rsmd.getColumnCount()];
				cache.add(tuple);
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					this.setValueAt(rs.getString(i), rowid, i - 1);
				}
				rowid++;
			}
			this.fireTableStructureChanged();
			this.fireTableDataChanged();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public void setResultSetAndDisplay(ResultSet rs) {
		cache.clear();
		headers = null;

		try {
			ResultSetMetaData rsmd = rs.getMetaData();

			headers = new String[rsmd.getColumnCount()];

			for (int i = 1; i <= headers.length; i++){
				String colName = rsmd.getColumnName(i);
				if (colName.contains(".")){//like sum(products123.product_id)
					int pos = colName.indexOf(".");
					colName = colName.substring(pos+1, colName.length()-1);
				}
				headers[i - 1] = colName;
			}

			int rowid = 0;
			while (rs.next()) {
				String[] tuple = new String[rsmd.getColumnCount()];
				cache.add(tuple);
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					this.setValueAt(rs.getString(i), rowid, i - 1);
				}
				rowid++;
			}

			this.fireTableStructureChanged();
			this.fireTableDataChanged();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public int getColumnCount() {

		return headers.length;
	}

	public int getRowCount() {

		return cache.size();
	}

	public Object getValueAt(int row, int col) {

		return cache.get(row)[col];
	}

	public String getColumnName(int i) {
		return headers[i];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.cache.get(rowIndex)[columnIndex] = (String) aValue;
	}
}
