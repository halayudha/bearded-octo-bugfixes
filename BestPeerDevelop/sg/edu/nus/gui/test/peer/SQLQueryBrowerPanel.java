package sg.edu.nus.gui.test.peer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.gui.dbview.GlobalQueryDBTree;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

/**
 * SQLQueryBrowerPanel is interface for query.
 * 
 * @author Han Xixia
 * 
 */

public class SQLQueryBrowerPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 577818194892265117L;

	private OperatePanel operatePanel = null;

	/**
	 * sqlInputField is used for input SQL commands to BESTPEER
	 */
	private JTextArea sqlInputArea = null;

	/**
	 * btnExecution is used to execute the specified command
	 * btnCancelExecution is used to cancel the execution of current SQL command
	 */
	private JButton btnExecution = null;
	
	private ImageIcon iconExecution = null;

	private GlobalQueryDBTree globalDBTree = null;

	private JTable resultTable = null;
	private DatabaseQueryTableModel qtm = null;

	/**
	 * This is the default constructor
	 */
	public SQLQueryBrowerPanel(OperatePanel operatePanel) {
		super();
		this.operatePanel = operatePanel;

		qtm = new DatabaseQueryTableModel();

		// operatePanel.get
		initialize();
	}

	/**
	 * Initialize the component of this panel
	 */
	public void initialize() {
		/**
		 * Set layout manager
		 */
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);

		/**
		 * Initialize input sql Area
		 *		 
		 */

		this.sqlInputArea = new JTextArea();
		this.sqlInputArea.setRows(6);
		this.sqlInputArea.setColumns(60);
		this.sqlInputArea.setFont(new Font("Times New Roman", Font.BOLD, 15));
		String initialValue = "select  products.product_name, sales.quantity_sale, sales.date_sale \n from products, sales \n where products.product_id = sales.product_id";
		this.sqlInputArea.setText(initialValue);

		JScrollPane scrollInputArea = new JScrollPane(this.sqlInputArea);
		scrollInputArea.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(4), LanguageLoader
						.getProperty("PanelTitle.sqlinputarea")
						+ "/ Connection:"
						+ AbstractPeer.bestpeer_db.getURL()));

		/**
		 * Initialize the execution button
		 */
		this.iconExecution = new ImageIcon(
				"./sg/edu/nus/gui/res/execute.png");

		btnExecution = new JButton(iconExecution);
		this.btnExecution.setPreferredSize(new Dimension(35, 48));

		this.btnExecution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerGUI gui = operatePanel.getServergui();

				ServerPeer serverpeer = gui.peer();

				String userName = serverpeer.getServerPeerAdminName();

				try {
					/**
					 * Determine current sql command in input field
					 */

					String sqlCommand = SQLQueryBrowerPanel.this.sqlInputArea
							.getText();

					SelectExecutor selectExecutor = new SelectExecutor(
							sqlCommand);
					DatabaseQueryTableModel model = getTableModel();
					selectExecutor.setTableModelForViewingResult(model);
					selectExecutor.setUserName(userName);

					selectExecutor.setQueryID(ServerPeer.getGlobalQID());

					selectExecutor.start();
				} catch (Exception event) {
					event.printStackTrace();
				}
			}
		});

		/*
		 * Set result to output table.
		 */
		this.resultTable = new JTable(qtm);
		JScrollPane scrollTable = new JScrollPane(this.resultTable);
		scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.sqlresultoutput")));

		globalDBTree = new GlobalQueryDBTree(this);
		globalDBTree.setPreferredSize(new Dimension(150, 400));
		JScrollPane scrollTree = new JScrollPane(this.globalDBTree);

		/**
		 * Set GridBagConstraint of component
		 */
		GridBagConstraints constraint1 = new GridBagConstraints();
		constraint1.gridx = 0;
		constraint1.gridy = 0;
		constraint1.gridwidth = 1;
		constraint1.gridheight = 1;
		constraint1.weightx = 100;
		constraint1.weighty = 0;
		constraint1.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints constraint2 = new GridBagConstraints();
		constraint2.gridx = 1;
		constraint2.gridy = 0;
		constraint2.gridwidth = 1;
		constraint2.gridheight = 1;
		constraint2.weightx = 0;
		constraint2.weighty = 0;
		constraint2.fill = GridBagConstraints.NONE;

		GridBagConstraints constraint3 = new GridBagConstraints();
		constraint3.gridx = 0;
		constraint3.gridy = 1;
		constraint3.gridwidth = 1;
		constraint3.gridheight = 1;
		constraint3.weightx = 100;
		constraint3.weighty = 100;
		constraint3.fill = GridBagConstraints.BOTH;

		GridBagConstraints constraint4 = new GridBagConstraints();
		constraint4.gridx = 1;
		constraint4.gridy = 1;
		constraint4.gridwidth = 1;
		constraint4.gridheight = 1;
		constraint4.weightx = 0;
		constraint4.weighty = 100;
		constraint4.fill = GridBagConstraints.VERTICAL;

		JPanel upperlevelPanel = new JPanel();
		upperlevelPanel.setLayout(layout);

		upperlevelPanel.add(scrollInputArea, constraint1);
		upperlevelPanel.add(this.btnExecution, constraint2);

		JPanel lowerlevelPanel = new JPanel();
		lowerlevelPanel.setLayout(layout);
		lowerlevelPanel.add(scrollTable, constraint3);
		lowerlevelPanel.add(scrollTree, constraint4);

		GridBagConstraints constraint5 = new GridBagConstraints();
		constraint5.gridx = 0;
		constraint5.gridy = 0;
		constraint5.gridwidth = 1;
		constraint5.gridheight = 1;
		constraint5.weightx = 100;
		constraint5.weighty = 0;
		constraint5.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints constraint6 = new GridBagConstraints();
		constraint6.gridx = 0;
		constraint6.gridy = 1;
		constraint6.gridwidth = 1;
		constraint6.gridheight = 1;
		constraint6.weightx = 100;
		constraint6.weighty = 100;
		constraint6.fill = GridBagConstraints.BOTH;

		this.add(upperlevelPanel, constraint5);
		this.add(lowerlevelPanel, constraint6);
	}

	public DatabaseQueryTableModel getTableModel() {
		return this.qtm;
	}
}
