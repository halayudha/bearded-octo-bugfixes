package sg.edu.nus.gui.customcomponent;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class SelectionTableLPanel extends JPanel {

	private static final long serialVersionUID = 9149437380825754438L;

	JTable table;

	SelectionTableModel tabModel;

	JScrollPane scrollPane;

	public SelectionTableLPanel(ResultSet rs) {

		tabModel = new SelectionTableModel(rs);

		table = new JTable(tabModel);

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);
	}

	@SuppressWarnings("unchecked")
	public SelectionTableLPanel(Vector<Vector> data,
			Vector<String> columnNames, String selectionColumnName) {

		tabModel = new SelectionTableModel(data, columnNames,
				selectionColumnName);

		table = new JTable(tabModel);

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);

	}

	public JScrollPane getTableWithScrollpane() {
		return scrollPane;
	}

	// ////////////////////

	class SelectionTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 5693870274627843975L;

		// All the data from the result set will be cached in the arraylist
		// first.

		@SuppressWarnings("unchecked")
		ArrayList cache = new ArrayList();;

		ResultSetMetaData rsmd;

		Boolean[] selColVals; // boolean value indicates this row is checked

		String selColName = "Assigned?";

		Vector<String> otherColNames = null;

		int columnCount = -1;

		@SuppressWarnings("unchecked")
		public SelectionTableModel(ResultSet rs) {
			try {
				rsmd = rs.getMetaData();

				columnCount = rsmd.getColumnCount() + 1; // for the first
				// checked column

				// get column name from rs
				otherColNames = new Vector<String>();
				for (int c = 1; c <= rsmd.getColumnCount(); c++) {
					otherColNames.add(rsmd.getColumnName(c));
				}

				// get data from rs

				int cols = columnCount;

				// If show the data in table, retreival datas from resultset
				// into cache.
				while (rs.next()) {
					Object[] row = new Object[cols - 1];
					for (int j = 0; j < row.length; j++) {
						row[j] = rs.getObject(j + 1);
					}
					cache.add(row);
				}
				selColVals = new Boolean[cache.size()];

				// initial value of shared
				for (int i = 0; i < cache.size(); i++) {
					selColVals[i] = Boolean.FALSE;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		public SelectionTableModel(Vector<Vector> data,
				Vector<String> columnNames, String selectionColumnName) {
			try {

				otherColNames = columnNames;

				selColName = selectionColumnName;

				columnCount = columnNames.size() + 1;

				// /

				int cols = columnCount;

				// If show the data in table, retreival datas from resultset
				// into cache.
				for (int i = 0; i < data.size(); i++) {
					Object[] row = new Object[cols - 1];
					for (int j = 0; j < row.length; j++) {
						row[j] = data.get(i).get(j);
					}
					cache.add(row);
				}

				selColVals = new Boolean[cache.size()];

				// initial value of selection column
				for (int i = 0; i < cache.size(); i++) {
					selColVals[i] = Boolean.FALSE;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * return the number of rows in the model. The DBTree uses this method to
		 * determine how many rows it should display.
		 * 
		 * @return the number of rows in the model
		 */
		public int getRowCount() {
			return cache.size();
		}

		/**
		 * Return the number of columns in the model. The DBTree uses this method to 
		 * determine how many columns it should create and display.
		 * 
		 * @return the number of columns in the model 
		 */
		public int getColumnCount() {

			return columnCount;

		}

		/**
		 * Return the name of column at columnIndex. This is used to initialize
		 * the table's column header name.
		 * 
		 * @param columnIndex - the index of the column
		 * @return the name of column
		 */
		public String getColumnName(int columnIndex) {
			try {
				if (columnIndex == 0)
					return selColName;
				else {
					return otherColNames.get(columnIndex - 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "#error#";
			}
		}

		/**
		 * Return the value for the cell at columnIndex and rowIndex
		 * 
		 * @param rowIndex - the row whose value is to be queried.
		 * @param columnIndex - the column whose value is to be queried.
		 * 
		 * @return the value Object at the specified cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < cache.size()) {
				if (columnIndex == 0) {
					return selColVals[rowIndex];
				}
				return ((Object[]) cache.get(rowIndex))[columnIndex - 1];

			} else
				return null;
		}

		/**
		 * only the first column can be modified
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return true;
			else
				return false;
		}

		public void setValueAt(Object value, int rowIndex, int columnIndex) {

			if (columnIndex != 0)
				return;

			Boolean TRUE = new Boolean(true);

			if (TRUE.equals(value)) {
				Object[] row = (Object[]) cache.get(rowIndex);
				String keyvalue = String.valueOf(row[0]);
				System.out.println(rowIndex + " " + columnIndex + " "
						+ keyvalue);
				selColVals[rowIndex] = Boolean.TRUE;

			} else {
				Object[] row = (Object[]) cache.get(rowIndex);
				String keyvalue = String.valueOf(row[0]);
				selColVals[rowIndex] = Boolean.FALSE;
				System.out.println(rowIndex + " " + columnIndex + " "
						+ keyvalue);
			}
			super.fireTableCellUpdated(rowIndex, columnIndex);
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int columnIndex) {
			if (columnIndex == 0)
				return Boolean.class;
			else
				return Object.class;

		}
	}

	// below code is just for testing this class

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {

	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
