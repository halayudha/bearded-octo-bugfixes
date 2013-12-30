/*
 * @(#) Pane.java 1.0 2005-12-30
 *
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.server;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.table.*;
import sg.edu.nus.logging.LogEvent;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;

/**
 * Implement the content pane of the bootstrap GUI.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 */

public final class Pane extends JTabbedPane implements ActionListener {

	// private member
	private static final long serialVersionUID = 5345664329770865421L;

	/* the handler of the main frame */
	private ServerGUI servergui;

	/* components in content pane */
	private JButton endButton;
	private JButton clearButton;
	private JButton propButton;

	/* components in link panel */
	private JTextField tfParentNode;
	private JTextField tfLeftChildNode;
	private JTextField tfRightChildNode;
	private JTextField tfLeftAdjacentNode;
	private JTextField tfRightAdjacentNode;
	private JTextField tfMinValue;
	private JTextField tfMaxValue;

	/* table model for all tables */
	private SortedTable peerTable;
	private SortedTable linkTable;
	private SortedTable eventTable;

	private SortedTableModel peerTableModel;
	private SortedTableModel linkTableModel;
	private SortedTableModel eventTableModel;

	/* TableSorter is a wrapper of TableModel. 
	 * When a table is in sorting status,
	 * only using the method getValueAt(row, col)
	 * will return the correct Objects. This is 
	 * because the TableSorter wraps the position
	 * of all rows and its getValueAt(row, col) 
	 * provides corresponding position transformation
	 * operation. */
	private TableSorter peerTableSorter;
	private TableSorter linkTableSorter;
	private TableSorter eventTableSorter;

	private TableMouseListener tableMouseListener = new TableMouseListener();
	private TableListSelectionListener tableListSelectionListener = new TableListSelectionListener();

	private final String[] btnName = { "End Peer", "Clear Event",
			"Show Property" };
	private final String[] command = { "end", "clear", "show" };
	private String[] imgName = { "peer", "table", "event" };

	/**
	 * Init the content pane with layered panel.
	 */
	public Pane(ServerGUI gui) {
		this.servergui = gui;
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		String imgLoc = AbstractMainFrame.SRC_PATH;

		JPanel panel = null;
		panel = this.makePeerPane();
		this.addTab("Online Peers",
				new ImageIcon(imgLoc + imgName[0] + ".png"), panel);

		panel = this.makeRoutingTablePane();
		this.addTab("Routing Table",
				new ImageIcon(imgLoc + imgName[1] + ".png"), panel);

		panel = this.makeEventPane();
		this.addTab("Network Events", new ImageIcon(imgLoc + imgName[2]
				+ ".png"), panel);

		this.setSelectedIndex(0);
	}

	/**
	 * Make the peer panel.
	 * 
	 * @return the peer panel
	 */
	private JPanel makePeerPane() {
		JPanel peerPanel = new JPanel(new BorderLayout());

		/* make table */
		String[] header = { "Peer ID", "Type", "IP Address", "Port" };
		int[] width = { 100, 100, 160, 80 };

		peerTableModel = new SortedTableModel(header);
		peerTableSorter = new TableSorter(peerTableModel);
		peerTable = this.makeTable(peerTableSorter, width);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(new JScrollPane(peerTable), BorderLayout.CENTER);
		peerPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		endButton = this.makeButton(btnName[0], command[0]);
		endButton.setEnabled(false);
		panel.add(endButton, BorderLayout.EAST);
		peerPanel.add(panel, BorderLayout.SOUTH);

		return peerPanel;
	}

	private JPanel makeRoutingTablePane() {
		/* make local link panel */
		JPanel bottomPane = new JPanel(new GridLayout(0, 2, 0, 0));
		bottomPane.setBorder(BorderFactory.createTitledBorder(null,
				"Local Peer Link Information",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		Color color = bottomPane.getBackground();

		/* make left part of local link panel */
		JPanel leftPanel = new JPanel(new GridLayout(0, 1, 8, 5));

		JPanel tmpPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Parent Link:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 13));
		tmpPanel.add(label, BorderLayout.WEST);
		tfParentNode = this.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfParentNode, BorderLayout.CENTER);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel("Left Child Link:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		tmpPanel.add(label, BorderLayout.WEST);
		tfLeftChildNode = this.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfLeftChildNode, BorderLayout.CENTER);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel("Left Adjct Link:");
		tmpPanel.add(label, BorderLayout.WEST);
		tfLeftAdjacentNode = this.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfLeftAdjacentNode, BorderLayout.CENTER);
		leftPanel.add(tmpPanel);

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel("Minimal Value:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
		tmpPanel.add(label, BorderLayout.WEST);
		tfMinValue = this.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfMinValue, BorderLayout.CENTER);
		leftPanel.add(tmpPanel);

		leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));
		/* end make left panel */

		/* make right panel */
		JPanel rightPanel = new JPanel(new GridLayout(0, 1, 0, 5));

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel();
		tmpPanel.add(label, BorderLayout.CENTER);
		rightPanel.add(tmpPanel);

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel("Right Child Link:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		tmpPanel.add(label, BorderLayout.WEST);
		tfRightChildNode = this.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfRightChildNode, BorderLayout.CENTER);
		rightPanel.add(tmpPanel);

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel("Right Adjct Link:");
		tmpPanel.add(label, BorderLayout.WEST);
		tfRightAdjacentNode = this
				.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfRightAdjacentNode, BorderLayout.CENTER);
		rightPanel.add(tmpPanel);

		tmpPanel = new JPanel(new BorderLayout());
		label = new JLabel("Maximal Value:");
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		tmpPanel.add(label, BorderLayout.WEST);
		tfMaxValue = this.makeTextField(false, color, JTextField.RIGHT);
		tmpPanel.add(tfMaxValue, BorderLayout.CENTER);
		rightPanel.add(tmpPanel);

		rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 3));
		/* end make right panel */

		bottomPane.add(leftPanel);
		bottomPane.add(rightPanel);

		JPanel localLinkPane = new JPanel(new BorderLayout());
		localLinkPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		localLinkPane.add(bottomPane, BorderLayout.CENTER);
		/* end make local link panel */

		/* make remote link panel */
		JPanel remoteLinkPane = new JPanel(new BorderLayout());
		remoteLinkPane
				.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		String[] header = { "ID", "IP", "Left Child", "Right Child",
				"Min Value", "Max Value" };
		int[] width = { 40, 80, 120, 120, 80, 80 };

		linkTableModel = new SortedTableModel(header);
		linkTableSorter = new TableSorter(linkTableModel);
		linkTable = this.makeTable(linkTableSorter, width);

		JScrollPane linkScrollPane = new JScrollPane(linkTable);
		remoteLinkPane.add(linkScrollPane, BorderLayout.CENTER);

		/* add local link panel and remote link panel */
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(localLinkPane, BorderLayout.NORTH);
		panel.add(remoteLinkPane, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Make the text field component.
	 * 
	 * @param editable whether the component is editable
	 * @param color the background color
	 * @param align the alignment style
	 * @return a text field component
	 */
	private JTextField makeTextField(boolean editable, Color color, int align) {
		JTextField textField = new JTextField();
		textField.setEditable(editable);
		textField.setBorder(BorderFactory.createLineBorder(color));
		textField.setBackground(color);
		textField.setHorizontalAlignment(align);
		return textField;
	}

	/**
	 * Make the event panel.
	 * 
	 * @return the event panel
	 */
	private JPanel makeEventPane() {
		JPanel eventPanel = new JPanel(new BorderLayout());

		/* make table */
		String[] header = { LanguageLoader.getProperty("table.type"),
				LanguageLoader.getProperty("table.date"),
				LanguageLoader.getProperty("table.time"),
				LanguageLoader.getProperty("table.desp"),
				LanguageLoader.getProperty("table.host"),
				LanguageLoader.getProperty("table.ip"),
				LanguageLoader.getProperty("table.user"), };
		eventTableModel = new SortedTableModel(header);
		eventTableSorter = new TableSorter(eventTableModel);
		eventTable = this.makeTable(eventTableSorter,
				AbstractMainFrame.EVENT_TABLE_WIDTH);

		/*
		 * I AM NOT SURE WHY THE FOLLOWING CODE CANNOT WORK!!! 
		 * -----------------
		 * logerTable.setDefaultRenderer(JLabel.class, new LogerTableCellRenderer());
		 * -----------------
		 * 
		 * SO, I USE A VERY STUPID METHOD TO ACHIEVE MY PURPOSE.
		 * NOTE THAT: THIS METHOD MAY CAUSE UNEXPECTED ERROR!
		 */
		TableColumn column = null;
		for (int i = 0; i < eventTable.getColumnCount(); i++) {
			column = eventTable.getColumnModel().getColumn(i);
			column.setCellRenderer(new EventTableCellRenderer());
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(new JScrollPane(eventTable), BorderLayout.CENTER);
		eventPanel.add(panel, BorderLayout.CENTER);

		/* lay out the buttons from left to right */
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(Box.createHorizontalGlue());

		clearButton = this.makeButton(btnName[1], command[1]);
		panel.add(clearButton);

		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		propButton = this.makeButton(btnName[2], command[2]);
		propButton.setEnabled(false);
		panel.add(propButton);

		eventPanel.add(panel, BorderLayout.SOUTH);

		return eventPanel;
	}

	/**
	 * Make the individual table with specified table model.
	 * 
	 * @param sorter the table sorter
	 * @param width the width of each column
	 * @return the table
	 */
	private SortedTable makeTable(TableSorter sorter, int[] width) {
		SortedTable table = new SortedTable(sorter, width);

		sorter.setTableHeader(table.getTableHeader());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add event listener
		table.addMouseListener(tableMouseListener);
		table.getTableHeader().addMouseListener(tableMouseListener);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(tableListSelectionListener);

		return table;
	}

	/**
	 * Make individual button.
	 * 
	 * @param name the button name
	 * @param cmd the command string
	 * @return the instance of <code>JButton</code>
	 */
	private JButton makeButton(String name, String cmd) {
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
		button.addActionListener(this);
		return button;
	}

	public void updateNodeInfo() {
		this.tfParentNode.setText("NULL");
		this.tfParentNode.setText("NULL");
		this.tfLeftChildNode.setText("NULL");
		this.tfLeftChildNode.setText("NULL");
		this.tfLeftAdjacentNode.setText("NULL");
		this.tfLeftAdjacentNode.setText("NULL");
		this.tfRightChildNode.setText("NULL");
		this.tfRightChildNode.setText("NULL");
		this.tfRightAdjacentNode.setText("NULL");
		this.tfRightAdjacentNode.setText("NULL");
		this.tfMinValue.setText("");
		this.tfMaxValue.setText("");

		for (int i = linkTableModel.getRowCount() - 1; i >= 0; i--)
			linkTableModel.removeRow(i);
	}

	public void updateNodeInfo(TreeNode treeNode) {
		if (treeNode.getParentNode() == null) {
			this.tfParentNode.setText("NULL");
		} else {
			this.tfParentNode.setText(treeNode.getParentNode().toString());
		}

		if (treeNode.getLeftChild() == null) {
			this.tfLeftChildNode.setText("NULL");
		} else {
			this.tfLeftChildNode.setText(treeNode.getLeftChild().toString());
		}

		if (treeNode.getLeftAdjacentNode() == null) {
			this.tfLeftAdjacentNode.setText("NULL");
		} else {
			this.tfLeftAdjacentNode.setText(treeNode.getLeftAdjacentNode()
					.toString());
		}

		if (treeNode.getRightChild() == null) {
			this.tfRightChildNode.setText("NULL");
		} else {
			this.tfRightChildNode.setText(treeNode.getRightChild().toString());
		}

		if (treeNode.getRightAdjacentNode() == null) {
			this.tfRightAdjacentNode.setText("NULL");
		} else {
			this.tfRightAdjacentNode.setText(treeNode.getRightAdjacentNode()
					.toString());
		}

		this.tfMinValue
				.setText(treeNode.getContent().getMinValue().getString());
		this.tfMaxValue
				.setText(treeNode.getContent().getMaxValue().getString());

		for (int i = linkTableModel.getRowCount() - 1; i >= 0; i--)
			linkTableModel.removeRow(i);

		for (int i = 0; i < treeNode.getLeftRoutingTable().getTableSize(); i++) {
			RoutingItemInfo nodeInfo = treeNode.getLeftRoutingTable()
					.getRoutingTableNode(i);
			Object[] arrObject = new Object[6];
			arrObject[0] = "L" + i;
			if (nodeInfo != null) {
				arrObject[1] = nodeInfo.getPhysicalInfo().toString();
				if (nodeInfo.getLeftChild() == null) {
					arrObject[2] = "NULL";
				} else {
					arrObject[2] = nodeInfo.getLeftChild().toString();
				}

				if (nodeInfo.getRightChild() == null) {
					arrObject[3] = "NULL";
				} else {
					arrObject[3] = nodeInfo.getRightChild().toString();
				}
				arrObject[4] = nodeInfo.getMinValue().getString();
				arrObject[5] = nodeInfo.getMaxValue().getString();
			} else {
				for (int j = 1; j < 6; j++) {
					arrObject[j] = "NULL";
				}
			}

			int rowIdx = linkTableModel.getRowCount();
			linkTableModel.insertRow(rowIdx, arrObject);
		}

		for (int i = 0; i < treeNode.getRightRoutingTable().getTableSize(); i++) {
			RoutingItemInfo nodeInfo = treeNode.getRightRoutingTable()
					.getRoutingTableNode(i);
			Object[] arrObject = new Object[6];
			arrObject[0] = "R" + i;
			if (nodeInfo != null) {
				arrObject[1] = nodeInfo.getPhysicalInfo().toString();
				if (nodeInfo.getLeftChild() == null) {
					arrObject[2] = "NULL";
				} else {
					arrObject[2] = nodeInfo.getLeftChild().toString();
				}

				if (nodeInfo.getRightChild() == null) {
					arrObject[3] = "NULL";
				} else {
					arrObject[3] = nodeInfo.getRightChild().toString();
				}

				arrObject[4] = nodeInfo.getMinValue().toString();
				arrObject[5] = nodeInfo.getMaxValue().toString();
			} else {
				for (int j = 1; j < 6; j++) {
					arrObject[j] = "NULL";
				}
			}

			int rowIdx = linkTableModel.getRowCount();
			linkTableModel.insertRow(rowIdx, arrObject);
		}
	}

	public synchronized void fireRoutingTableRowRemoved(PeerInfo info) {
		// TODO: discuss with QH
	}

	/**
	 * Get the number of rows in the peer table.
	 * 
	 * @return the number of rows in the peer table
	 */
	public synchronized int getPeerTableRowCount() {
		return peerTableModel.getRowCount();
	}

	/**
	 * Insert a data into the peer table with the row number.
	 * 
	 * @param row the row that the data to be inserted
	 * @param data the data to be inserted
	 */
	public synchronized void firePeerTableRowInserted(int row, Object[] data) {
		peerTableModel.insertRow(row, data);
	}

	/**
	 * Remove a row from the peer table with the row number.
	 * 
	 * @param row the row to be removed from the table
	 */
	public synchronized void firePeerTableRowRemoved(int row) {
		peerTableModel.removeRow(row);
	}

	/**
	 * Remove a row from the peer table with specified data value. 
	 * 
	 * @param info the information of the peer
	 */
	public synchronized void firePeerTableRowRemoved(PeerInfo info) {
		Object obj;
		Object[] target = info.toObjectArray();
		boolean found = false;

		int row = peerTableModel.getRowCount();
		int col = peerTableModel.getColumnCount();
		int i;
		for (i = row - 1; i >= 0; i--) {
			found = true;
			for (int j = 0; j < col; j++) {
				obj = peerTableModel.getValueAt(i, j);
				if (!obj.equals(target[j])) {
					found = false;
					break;
				}
			}
			if (found)
				break;
		}
		if (found)
			peerTableModel.removeRow(i);
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		/* if end peers */
		if (cmd.equals(command[0])) {
			int rowIndex = peerTable.getSelectedRow();
			if (rowIndex != -1) {
				String ipaddr = (String) peerTableModel.getValueAt(rowIndex, 2);
				Integer port = (Integer) peerTableModel.getValueAt(rowIndex, 3);
				((ServerPeer) servergui.peer()).forceOut(ipaddr.trim(), port
						.intValue());
				this.removeOnlinePeer(rowIndex);
			}
		}
		/* if clear event */
		else if (cmd.equals(command[1])) {
			for (int i = eventTableModel.getRowCount() - 1; i >= 0; i--)
				eventTableModel.removeRow(i);

			if (eventDialog != null) {
				eventDialog.dispose();
				eventDialog = null;
			}
		}
		/* if show property */
		else if (cmd.equals(command[2])) {
			if (eventTable.getSelectedRow() != -1) {
				if (eventDialog == null) {
					eventDialog = new EventDialog(servergui, false);
					eventDialog.setLogerEvent(getLogEvent());
				} else {
					eventDialog.setLogerEvent(getLogEvent());
					eventDialog.setVisible(true);
				}
			}
		}
	}

	/**
	 * Remove all online peers and update UI.
	 */
	public synchronized void removeOnlinePeers() {
		int size = peerTableModel.getRowCount();
		for (int i = size - 1; i >= 0; i--)
			peerTableModel.removeRow(i);
		PeerMaintainer.getInstance().removeAll();
	}

	/**
	 * Remove a row from the table that stores all online peers.
	 * 
	 * @param row the row to be removed
	 */
	public synchronized void removeOnlinePeer(int row) {
		peerTableModel.removeRow(row);
	}

	// ------------ for event table bellow -----------------

	private void showEventPopupMenu(Component c, int x, int y) {
		new EventPopupMenu().show(c, x, y);
	}

	/**
	 * Add <code>LogEvent</code> to the event panel.
	 * 
	 * @param event the <code>LogEvent</code> to be added to the event panel
	 */
	public synchronized void log(LogEvent event) {
		eventTableModel.insertRow(eventTableModel.getRowCount(), event
				.toArray());
		eventTableModel.fireTableDataChanged();
	}

	/**
	 * Returns the String representation of <code>LogEvent</code>.
	 * 
	 * @return the String representation of <code>LogEvent</code>
	 */
	public synchronized String[] getLogEvent() throws IndexOutOfBoundsException {
		if (LogEvent.EVENT_ITEMS != eventTable.getColumnCount()) {
			throw new IndexOutOfBoundsException(
					"The number of table column does not match LogEvent");
		}

		String[] result = new String[LogEvent.EVENT_ITEMS];

		int rowIndex = eventTable.getSelectedRow();
		if (rowIndex != -1) {
			Object value = null;
			int columnCount = eventTable.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				value = eventTableSorter.getValueAt(rowIndex, i);
				if (value instanceof JLabel) {
					result[i] = ((JLabel) value).getText();
				} else {
					result[i] = (String) value;
				}
			}
			propButton.setEnabled(true);
		}

		return result;
	}

	/**
	 * Remove all log events.
	 */
	public synchronized void removeLogEvents() {
		int size = eventTableModel.getRowCount();
		for (int i = size - 1; i >= 0; i--)
			eventTableModel.removeRow(i);
	}

	// ------------ list handler for table --------------

	// used by TableListSelectionListener, TableMouseListener and LogerPopupMenu
	private EventDialog eventDialog;

	final class TableListSelectionListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {
			ListSelectionModel lsm = (ListSelectionModel) event.getSource();
			if (!lsm.isSelectionEmpty()) {
				if (eventDialog != null) {
					eventDialog.setLogerEvent(getLogEvent());
				}
			}
		}

	}

	// ------------ mouse handler for queue and loger ------------------

	final class TableMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent event) {
			Object src = event.getSource();
			if (src == eventTable) {
				if ((event.getButton() == MouseEvent.BUTTON1)
						&& (event.getClickCount() == 2)) {
					if (eventDialog == null) {
						eventDialog = new EventDialog(servergui, false);
						eventDialog.setLogerEvent(getLogEvent());
					} else {
						eventDialog.setLogerEvent(getLogEvent());
						eventDialog.setVisible(true);
						eventDialog.repaint();
					}
				}
			} else if (src == eventTable.getTableHeader()) {
				propButton.setEnabled(false);
			} else if (src == peerTable) {
				int rowIndex = 0;
				int rowCount = peerTable.getRowCount();
				if (rowCount > 0) {
					rowIndex = peerTable.rowAtPoint(new Point(event.getX(),
							event.getY()));
					peerTable.setRowSelectionInterval(rowIndex, rowIndex);
					endButton.setEnabled(true);
				}
			} else if (src == peerTable.getTableHeader()) {
				endButton.setEnabled(false);
			}
		}

		public void mousePressed(MouseEvent event) {
			Object src = event.getSource();
			int rowIndex = 0;
			int rowCount = 0;
			if (src == eventTable) {
				rowCount = eventTable.getRowCount();
				if (rowCount > 0) // if contain data
				{
					rowIndex = eventTable.rowAtPoint(new Point(event.getX(),
							event.getY()));
					eventTable.setRowSelectionInterval(rowIndex, rowIndex);
					propButton.setEnabled(true);
				}
			} else if (src == eventTable.getTableHeader()) {
				propButton.setEnabled(false);
			} else if (src == peerTable) {
				rowCount = peerTable.getRowCount();
				if (rowCount > 0) {
					rowIndex = peerTable.rowAtPoint(new Point(event.getX(),
							event.getY()));
					peerTable.setRowSelectionInterval(rowIndex, rowIndex);
					endButton.setEnabled(true);
				}
			} else if (src == peerTable.getTableHeader()) {
				endButton.setEnabled(false);
			}
			this.showPopupMenu(event);
		}

		public void mouseReleased(MouseEvent event) {
			this.showPopupMenu(event);
		}

		private void showPopupMenu(MouseEvent event) {
			Object src = event.getSource();
			if (event.isPopupTrigger() && (src == eventTable)) {
				showEventPopupMenu(event.getComponent(), event.getX(), event
						.getY());
			}
		}

	}

	// ----------- popup menu for loger --------------

	final class EventPopupMenu extends JPopupMenu implements ActionListener {

		// private member
		private static final long serialVersionUID = -5630677845941564070L;
		private JMenuItem propMenuItem;
		private String command = "property";

		public EventPopupMenu() {
			this.addMenu();
		}

		private void addMenu() {
			propMenuItem = this.makeMenuItem("Properties", command);
			this.add(propMenuItem);
		}

		private JMenuItem makeMenuItem(String name, String cmd) {
			JMenuItem menuItem = new JMenuItem(name);
			menuItem.setActionCommand(cmd);
			menuItem.addActionListener(this);

			return menuItem;
		}

		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();
			if (cmd.equals(command)) {
				if (eventDialog == null) {
					eventDialog = new EventDialog(servergui, false);
					eventDialog.setLogerEvent(getLogEvent());
				} else {
					eventDialog.setLogerEvent(getLogEvent());
					eventDialog.setVisible(true);
					eventDialog.repaint();
				}
			}
		}

	}

	// ----------- table cell render for loger -------------

	final class EventTableCellRenderer extends DefaultTableCellRenderer {

		// private members
		private static final long serialVersionUID = -1895272885977337809L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}

		public void setValue(Object value) {
			setIcon(null);
			if (value instanceof JLabel) {
				JLabel label = (JLabel) value;
				setText(label.getText());
				setIcon(label.getIcon());
			} else {
				super.setValue(value);
			}
		}

	}

	// ----------- display event recorded by loger ----------------

	final class EventDialog extends AbstractEventDialog {

		// private member
		private static final long serialVersionUID = 3524367182104191097L;

		public EventDialog(Frame gui, boolean model) {
			super(gui, "Event Properties", model, 360, 400);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();

			int rowIndex = eventTable.getSelectedRow();
			int rowCount = eventTable.getRowCount();

			// if up
			if (cmd.equals(command[0])) {
				if (rowCount > 0) {
					if (rowIndex > 0) {
						eventTable.setRowSelectionInterval(rowIndex - 1,
								rowIndex - 1);
						this.setLogerEvent(getLogEvent());
					}
				}
			}
			// if down
			else if (cmd.equals(command[1])) {
				if (rowCount > 0) {
					if (rowIndex < (rowCount - 1)) {
						eventTable.setRowSelectionInterval(rowIndex + 1,
								rowIndex + 1);
						this.setLogerEvent(getLogEvent());
					}
				}
			}
			// if close dialog
			else if (cmd.equals(command[2])) {
				this.dispose();
			}
		}

		@Override
		protected void processWhenWindowClosing() {
		}

	}

}