package sg.edu.nus.accesscontrol.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import sg.edu.nus.gui.GuiLoader;

@SuppressWarnings("unchecked")
public class MyJTable extends JTable {

	private static final long serialVersionUID = 7139542930147257794L;

	private boolean DEBUG = false;

	private boolean ALLOW_COLUMN_SELECTION = false;

	private boolean ALLOW_ROW_SELECTION = true;

	Vector<Vector> rows = new Vector<Vector>();

	Vector<String> columnNames = new Vector<String>();

	DefaultTableModel tabModel = new DefaultTableModel();

	int selectedRow = -1;

	public MyJTable() {

		super();

		this.setModel(tabModel);

		this.setPreferredScrollableViewportSize(new Dimension(500, 170));

		addListeners();

		tabModel.setDataVector(rows, columnNames);

		myInit();
	}

	public MyJTable(Object[][] data, String[] colNames) {

		this.setModel(tabModel);

		setColumnNames(colNames);

		setTableData(data);

		this.setPreferredScrollableViewportSize(new Dimension(500, 170));

		addListeners();

		myInit();

	}

	public MyJTable(Vector data, Vector colNames) {

		this.setModel(tabModel);

		setTableData(rows, columnNames);

		this.setPreferredScrollableViewportSize(new Dimension(500, 170));

		addListeners();

		myInit();
	}

	private void myInit() {
		// setting some color...

		setTableHeaderTextColor(new Color(168, 167, 236));

		setBackground(GuiLoader.themeBkColor);

		setForeground(GuiLoader.contentTextColor);

		setSelectionBackground(GuiLoader.selectionBkColor); // blue theme

		setSelectionForeground(GuiLoader.contentTextColor);

		setGridColor(new Color(221, 234, 252));

	}

	public JScrollPane getScrollWrapper(int widthViewPort, int heightViewPort) {

		JScrollPane scroll = new JScrollPane(this);

		this.setPreferredScrollableViewportSize(new Dimension(widthViewPort,
				heightViewPort));

		return scroll;
	}

	public void setTableHeaderBkColor(Color tableHeaderBkColor) {
		getTableHeader().setBackground(tableHeaderBkColor);
	}

	public void setTableHeaderTextColor(Color tableHeaderTextColor) {
		getTableHeader().setForeground(tableHeaderTextColor);
	}

	public void setColumnNames(String[] colNames) {

		columnNames.removeAllElements();
		rows.removeAllElements();

		for (int i = 0; i < colNames.length; i++) {
			columnNames.add(new String(colNames[i]));
		}

		tabModel.setDataVector(rows, columnNames);

	}

	public void setTableData(Object[][] data) {

		rows.removeAllElements();

		if (data != null) {
			for (int i = 0; i < data.length; i++) {

				Vector row = new Vector();

				for (int j = 0; j < data[i].length; j++) {
					row.add(data[i][j]);
				}

				rows.add(row);

				this.addNotify();
			}
		}

		tabModel.setDataVector(rows, columnNames);

	}

	public void setTableData(Vector<Vector> rows, Vector<String> columnNames) {

		this.rows = rows;

		this.columnNames = columnNames;

		tabModel.setDataVector(rows, columnNames);
	}

	public Vector getTableVector() {
		return tabModel.getDataVector();
	}

	public String[][] getTableArray() {
		Vector data = tabModel.getDataVector();
		int row_count = data.size();

		if (row_count > 0) {

			String[][] arr2D = new String[row_count][];
			for (int i = 0; i < row_count; i++) {
				Vector row = (Vector) data.get(i);
				arr2D[i] = new String[row.size()];
				for (int j = 0; j < row.size(); j++) {
					arr2D[i][j] = (String) row.get(j);
				}
			}

			return arr2D;
		} else {
			return null;
		}
	}

	private void addListeners() {

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (ALLOW_ROW_SELECTION) { // true by default
			ListSelectionModel rowSM = getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					// Ignore extra messages.
					if (e.getValueIsAdjusting())
						return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
					} else {

						selectedRow = lsm.getMinSelectionIndex();

					}
				}
			});
		} else {
			setRowSelectionAllowed(false);
		}

		if (ALLOW_COLUMN_SELECTION) { // false by default
			if (ALLOW_ROW_SELECTION) {
				// We allow both row and column selection, which
				// implies that we *really* want to allow individual
				// cell selection.
				setCellSelectionEnabled(true);
			}
			setColumnSelectionAllowed(true);

			ListSelectionModel colSM = getColumnModel().getSelectionModel();

			colSM.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					// Ignore extra messages.
					if (e.getValueIsAdjusting())
						return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						System.out.println("No columns are selected.");
					} else {
						int selectedCol = lsm.getMinSelectionIndex();
						System.out.println("Column " + selectedCol
								+ " is now selected.");
					}
				}
			});
		}

		if (DEBUG) {
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(MyJTable.this);
				}
			});
		}
	}

	private void deleteRow(int index) {
		if (index >= 0 && index < this.getRowCount())// At least one Row in
		// Table
		{
			rows.removeElementAt(index);
			this.addNotify();
		}

	}// Delete Row

	public void deleteSelectedRow() {
		deleteRow(selectedRow);
	}

	public void addRow(Object[] row) {
		Vector r = new Vector();

		for (int i = 0; i < row.length; i++) {
			r.addElement(row[i]);
			;
		}

		rows.addElement(r);
		this.addNotify();
	}

	public void addRow() {
		Vector r = new Vector();

		r = createBlankElement();

		rows.addElement(r);
		this.addNotify();
	}

	private Vector createBlankElement() {
		Vector t = new Vector();
		for (int i = 0; i < columnNames.size(); i++) {
			t.addElement((String) " ");
		}
		return t;
	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

}
