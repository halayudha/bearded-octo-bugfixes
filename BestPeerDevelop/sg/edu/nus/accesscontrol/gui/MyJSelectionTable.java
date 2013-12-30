package sg.edu.nus.accesscontrol.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.logging.LogManager;

@SuppressWarnings("unchecked")
public class MyJSelectionTable extends JTable implements MouseListener {

	private static final long serialVersionUID = 5228388024120735591L;

	SelectionTableModel tabModel = null;

	Vector<String> columnNames = null;
	String selectionColumnName = null;

	public MyJSelectionTable(ResultSet rs) {

		tabModel = new SelectionTableModel(rs);

		this.setModel(tabModel);

		guiInit();
	}

	public MyJSelectionTable(Vector<Vector> data, Vector<String> columnNames,
			String selectionColumnName) {

		initTableModel(data, columnNames, selectionColumnName);

		guiInit();
	}

	private void initTableModel(Vector<Vector> data,
			Vector<String> columnNames, String selectionColumnName) {

		this.columnNames = columnNames;
		this.selectionColumnName = selectionColumnName;

		tabModel = new SelectionTableModel(data, columnNames,
				selectionColumnName);

		this.setModel(tabModel);

	}

	public MyJSelectionTable(Object[][] data, String[] columnNames,
			String selectionColumnName) {

		Vector<Vector> vData = new Vector<Vector>();

		if (data != null) {
			for (int i = 0; i < data.length; i++) {

				Vector row = new Vector();

				for (int j = 0; j < data[i].length; j++) {
					row.add(data[i][j]);
				}

				vData.add(row);
			}
		}

		Vector<String> vColumnNames = new Vector<String>();
		for (int i = 0; i < columnNames.length; i++) {
			vColumnNames.add(columnNames[i]);
		}

		initTableModel(vData, vColumnNames, selectionColumnName);

		guiInit();
	}

	public MyJSelectionTable(String[] columnNames, String selectionColumnName) {
		this(null, columnNames, selectionColumnName);
	}

	public void setTableData(Object[][] data) {

		Vector<Vector> vData = new Vector<Vector>();
		if (data != null) {
			for (int i = 0; i < data.length; i++) {

				Vector row = new Vector();

				for (int j = 0; j < data[i].length; j++) {
					row.add(data[i][j]);
				}

				vData.add(row);
			}
		}

		tabModel = new SelectionTableModel(vData, columnNames,
				selectionColumnName);

		this.setModel(tabModel);
	}

	private void guiInit() {
		// setting some color...
		setTableHeaderTextColor(new Color(168, 167, 236));

		setBackground(GuiLoader.themeBkColor);

		setForeground(GuiLoader.contentTextColor);

		setSelectionBackground(GuiLoader.selectionBkColor); // blue theme

		setSelectionForeground(GuiLoader.contentTextColor);

		setGridColor(new Color(221, 234, 252));

		getTableHeader().addMouseListener(this);
	}

	public void setTableHeaderBkColor(Color tableHeaderBkColor) {
		getTableHeader().setBackground(tableHeaderBkColor);
	}

	public void setTableHeaderTextColor(Color tableHeaderTextColor) {
		getTableHeader().setForeground(tableHeaderTextColor);
	}

	public JScrollPane getScrollWrapper(int viewPortWidth, int viewPortHeight) {
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(this);

		this.setPreferredScrollableViewportSize(new Dimension(viewPortWidth,
				viewPortHeight));

		return scrollPane;
	}

	public JScrollPane getScrollWrapper(Dimension viewPortDimension) {
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(this);

		this.setPreferredScrollableViewportSize(viewPortDimension);

		return scrollPane;
	}

	public JScrollPane getScrollWrapper() {
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(this);

		return scrollPane;
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	Boolean selectAll = Boolean.FALSE;

	public void mouseClicked(MouseEvent e) {

		int columnIndex = this.getTableHeader().columnAtPoint(e.getPoint());

		if (columnIndex == 0) {// first column click

			selectAll = !selectAll;

			int row = 0;
			int column = 0;
			for (row = 0; row < getRowCount(); row++) {
				setValueAt(selectAll, row, column);
			}

		}
	}

	public ArrayList getCheckedRows() {
		return tabModel.getSelectedRows();
	}

	class SelectionTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 5693870274627843975L;

		// All the data from the result set will be cached in the arraylist
		// first.

		ArrayList cache = new ArrayList();;

		ResultSetMetaData rsmd;

		Boolean[] selColVals; // boolean value indicates this row is checked

		String selColName = "Assigned?";

		Vector<String> otherColNames = null;

		int columnCount = -1;

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
				LogManager.LogException(
						"Exception caught while selectiing table model", e);
			}
		}

		public SelectionTableModel(Vector<Vector> data,
				Vector<String> columnNames, String selectionColumnName) {
			try {

				otherColNames = columnNames;

				selColName = selectionColumnName;

				columnCount = columnNames.size() + 1;

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
				LogManager.LogException(
						"Exception caught while selectiing table model", e);
			}
		}

		public ArrayList getData() {
			return cache;
		}

		public Boolean[] getSelectionColumnValues() {
			return selColVals;
		}

		public ArrayList getSelectedRows() {
			ArrayList arrList = new ArrayList();
			for (int i = 0; i < selColVals.length; i++) {
				if (selColVals[i].booleanValue()) {
					arrList.add(cache.get(i));
				}
			}
			return arrList;
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
				LogManager.LogException(
						"Exception caught while retrieving column name", e);
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
				selColVals[rowIndex] = Boolean.TRUE;

			} else {
				selColVals[rowIndex] = Boolean.FALSE;
			}
			super.fireTableCellUpdated(rowIndex, columnIndex);
		}

		public Class getColumnClass(int columnIndex) {
			if (columnIndex == 0)
				return Boolean.class;
			else
				return Object.class;

		}
	}
}
