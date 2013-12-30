package sg.edu.nus.gui.customcomponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class SimpleTablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2519859305519819636L;

	private boolean DEBUG = false;

	private boolean ALLOW_COLUMN_SELECTION = false;

	private boolean ALLOW_ROW_SELECTION = true;

	@SuppressWarnings("unchecked")
	Vector<Vector> rows = new Vector<Vector>();

	Vector<String> columnNames = new Vector<String>();

	DefaultTableModel tabModel = new DefaultTableModel();

	final JTable table = new JTable(tabModel);

	int selectedRow = -1;

	JScrollPane scrollPane = null;

	public SimpleTablePanel() {

		super(new BorderLayout());

		table.setPreferredScrollableViewportSize(new Dimension(400, 170));

		//
		addListeners();

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);

		tabModel.setDataVector(rows, columnNames);
	}

	public SimpleTablePanel(Object[][] data, String[] colNames) {

		super(new BorderLayout());

		setColumnNames(colNames);

		setTableData(data);

		table.setPreferredScrollableViewportSize(new Dimension(400, 170));

		//
		addListeners();

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);
	}

	public JScrollPane getTableScrollPane() {
		return scrollPane;
	}

	public void setColumnNames(String[] colNames) {

		columnNames.removeAllElements();

		for (int i = 0; i < colNames.length; i++) {
			columnNames.add(new String(colNames[i]));
		}

		tabModel.setDataVector(rows, columnNames);

	}

	@SuppressWarnings("unchecked")
	public void setTableData(Object[][] data) {

		rows.removeAllElements();

		for (int i = 0; i < data.length; i++) {

			Vector row = new Vector();

			for (int j = 0; j < data[i].length; j++) {
				row.add(data[i][j]);
			}

			rows.add(row);

			table.addNotify();
		}

		tabModel.setDataVector(rows, columnNames);

	}

	private void addListeners() {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (ALLOW_ROW_SELECTION) { // true by default
			ListSelectionModel rowSM = table.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					// Ignore extra messages.
					if (e.getValueIsAdjusting())
						return;

					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
					} else {

						selectedRow = lsm.getMinSelectionIndex();

						String firstValue = (String) table.getValueAt(
								selectedRow, 0);

						System.out.println("Row " + selectedRow
								+ " is now selected. " + firstValue);

					}
				}
			});
		} else {
			table.setRowSelectionAllowed(false);
		}

		if (ALLOW_COLUMN_SELECTION) { // false by default
			if (ALLOW_ROW_SELECTION) {
				// We allow both row and column selection, which
				// implies that we *really* want to allow individual
				// cell selection.
				table.setCellSelectionEnabled(true);
			}
			table.setColumnSelectionAllowed(true);
			ListSelectionModel colSM = table.getColumnModel()
					.getSelectionModel();
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
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}
	}

	private void deleteRow(int index) {
		if (index != -1)// At least one Row in Table
		{
			rows.removeElementAt(index);
			table.addNotify();
		}

	}// Delete Row

	public void deleteSelectedRow() {
		deleteRow(selectedRow);
	}

	@SuppressWarnings("unchecked")
	public void addRow(Object[] row) {
		Vector r = new Vector();
		//
		for (int i = 0; i < row.length; i++) {
			r.addElement(row[i]);
			;
		}
		//
		rows.addElement(r);
		table.addNotify();
	}

	@SuppressWarnings("unchecked")
	public void addRow() {
		Vector r = new Vector();
		//
		r = createBlankElement();
		//
		rows.addElement(r);
		table.addNotify();
	}

	@SuppressWarnings("unchecked")
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

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		String[] columnNames = { "First Name", "Last Name", "Sport",
				"# of Years", "Vegetarian" };

		Object[][] data = {
				{ "Mary", "Campione", "Snowboarding", new Integer(5),
						new Boolean(false) },
				{ "Alison", "Huml", "Rowing", new Integer(3), new Boolean(true) },
				{ "Kathy", "Walrath", "Knitting", new Integer(2),
						new Boolean(false) },
				{ "Sharon", "Zakhour", "Speed reading", new Integer(20),
						new Boolean(true) },
				{ "Philip", "Milne", "Pool", new Integer(10),
						new Boolean(false) } };

		// Create and set up the window.
		JFrame frame = new JFrame("SimpleTableSelectionDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		// SimpleTableSelectionDemo newContentPane = new
		// SimpleTableSelectionDemo(data, columnNames);

		SimpleTablePanel newContentPane = new SimpleTablePanel();

		newContentPane.setColumnNames(columnNames);

		//
		newContentPane.addRow();
		newContentPane.addRow(data[1]);
		//

		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println("Error setting native LAF: " + e);
				}

				createAndShowGUI();
			}
		});
	}
}
