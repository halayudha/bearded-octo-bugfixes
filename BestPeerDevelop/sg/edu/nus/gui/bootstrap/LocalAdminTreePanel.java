package sg.edu.nus.gui.bootstrap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import sg.edu.nus.gui.DynamicTreePanel;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Panel for editing local admin account
 * @author VHTam
 *
 */
public class LocalAdminTreePanel extends JPanel implements ActionListener,
		TreeSelectionListener {

	private static final long serialVersionUID = -1737802199212366111L;

	private BootstrapGUI bootstrapGUI;

	private final String ADD_COMMAND = "add_table";
	private final String REMOVE_COMMAND = "remove";
	private final String MODIFY_COMMAND = "distribute";

	public static final String TABLE = "table";
	public static final String COLUMN = "column";

	private String btnAddCaption = LanguageLoader.getProperty("button.add");
	private String btnModifyCaption = LanguageLoader
			.getProperty("button.modify");
	private String btnRemoveCaption = LanguageLoader
			.getProperty("button.delete");

	private DynamicTree treePanel;
	JButton addButton = null;
	JButton modifyButton = null;
	JButton removeButton = null;

	public LocalAdminTreePanel(BootstrapGUI gui) {

		super(new BorderLayout());

		bootstrapGUI = gui;

		gui.peer();
		// Create the components.
		treePanel = new DynamicTree(MetaDataAccess
				.metaGetCorporateDbName(AbstractPeer.conn_bestpeerdb));

		treePanel.addTreeListener(this);
		treePanel.setCellRender(new TreeCellRenderer());

		populateTree(treePanel);

		addButton = new JButton(btnAddCaption);
		addButton.setActionCommand(ADD_COMMAND);
		addButton.addActionListener(this);

		modifyButton = new JButton(btnModifyCaption);
		modifyButton.setActionCommand(MODIFY_COMMAND);
		modifyButton.addActionListener(this);
		modifyButton.setEnabled(false);

		removeButton = new JButton(btnRemoveCaption);
		removeButton.setActionCommand(REMOVE_COMMAND);
		removeButton.addActionListener(this);
		removeButton.setEnabled(false);

		// Lay everything out.
		treePanel.setPreferredSize(new Dimension(300, 150));
		add(treePanel, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		btnPanel.add(addButton);
		btnPanel.add(modifyButton);
		btnPanel.add(removeButton);

		add(btnPanel, BorderLayout.SOUTH);
	}

	int selectedLevel = -1;

	public void valueChanged(TreeSelectionEvent e) {

		TreePath path = treePanel.getSelectionPath();
		if (path == null)
			return;

		selectedLevel = path.getPathCount();
		if (selectedLevel == 3) {
			removeButton.setEnabled(true);
			modifyButton.setEnabled(true);
		} else {
			removeButton.setEnabled(false);
			modifyButton.setEnabled(false);
		}

	}

	DefaultMutableTreeNode localAdminNode = null;

	public void populateTree(DynamicTree treePanel) {

		localAdminNode = treePanel.addObject(null, "Peer local administrator");
		String[] admins = MetaDataAccess
				.metaGetLocalAdmins(AbstractPeer.conn_bestpeerdb);

		for (int i = 0; i < admins.length; i++) {
			treePanel.addObject(localAdminNode, admins[i]);
		}

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (ADD_COMMAND.equals(command)) {

			DlgAddLocalAdmin dlg = new DlgAddLocalAdmin(bootstrapGUI,
					"Add new local administrator", true);

			dlg.setVisible(true);

			if (dlg.isOkPressed()) {
				String userName = dlg.getUserName();
				String userDesc = dlg.getUserDesc();
				String password = dlg.getUserPwd();
				treePanel.addObject(localAdminNode, userName, true);
				MetaDataAccess.metaAddLocalAdmin(AbstractPeer.conn_bestpeerdb,
						userName, userDesc, password);
			}

		} else if (REMOVE_COMMAND.equals(command)) {
			// Remove button clicked
			if (selectedLevel == 3) {
				DefaultMutableTreeNode node = treePanel.getSelectedNode();
				String userName = node.toString();

				MetaDataAccess.metaDeleteLocalAdmin(
						AbstractPeer.conn_bestpeerdb, userName);

				treePanel.removeCurrentNode();
			}

		} else if (MODIFY_COMMAND.equals(command)) {

			DlgAddLocalAdmin dlg = new DlgAddLocalAdmin(bootstrapGUI,
					"Modify local administrator", true);
			DefaultMutableTreeNode node = treePanel.getSelectedNode();
			String userName = node.toString();

			dlg.setModifyUserName(userName);
			dlg.setVisible(true);

			if (dlg.isOkPressed()) {
				// delete old record
				MetaDataAccess.metaDeleteLocalAdmin(
						AbstractPeer.conn_bestpeerdb, userName);

				// add new one record into database
				String userDesc = dlg.getUserDesc();
				String password = dlg.getUserPwd();
				MetaDataAccess.metaAddLocalAdmin(AbstractPeer.conn_bestpeerdb,
						userName, userDesc, password);
			}

		}
	}

	class TreeCellRenderer extends DefaultTreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 53270629137538730L;

		/**
		 * Rewrite the <code>getTreeCellRendererComponent</code> function in the DefaultTreeCellRenderer class
		 * to set the custemized image for different kinds of tree node in the <code>DBTree</code>.
		 * 
		 * @return the Component that the renderer uses to draw the value 
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded,
					leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			int level = DynamicTreePanel.getLevelOfNode(tree, node);

			String imgPath = null;

			if (level == 1) {
				imgPath = "./sg/edu/nus/res/dbSymbol.png";
			} else if (level == 2) {
				imgPath = "./sg/edu/nus/res/user manager.png";
			} else {
				imgPath = "./sg/edu/nus/res/leafNode.png";
			}
			//
			ImageIcon icon = new ImageIcon(imgPath);

			// set the image icon to render the node

			this.setIcon(icon);

			return this;
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("DynamicTreeDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		SchemaTreePanel newContentPane = new SchemaTreePanel(null);
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
				createAndShowGUI();
			}
		});
	}
}