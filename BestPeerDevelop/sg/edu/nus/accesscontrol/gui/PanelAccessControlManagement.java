package sg.edu.nus.accesscontrol.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.sql.Connection;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import sg.edu.nus.accesscontrol.AccCtrlLanguageLoader;
import sg.edu.nus.accesscontrol.bootstrap.PanelRoleManagement;
import sg.edu.nus.accesscontrol.normalpeer.PanelUserManagement;
import sg.edu.nus.gui.GuiHelper;
import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.gui.customcomponent.GradientLabel;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.util.MetaDataAccess;

public class PanelAccessControlManagement extends JPanel {

	private static final long serialVersionUID = -91604488462674939L;

	MySecurityTree tree = null;

	PanelUserManagement userPane = null;

	PanelRoleManagement rolePane = null;

	JPanel commonPane = new JPanel(new CardLayout());
	JPanel treePane = null;
	JScrollPane scrollPaneTree = null;

	final String UserType = MySecurityTree.USER;
	final String RoleType = MySecurityTree.ROLE;

	Hashtable<String, Object> panelNames = new Hashtable<String, Object>();

	Connection conn = null;

	DefaultMutableTreeNode root = null;

	String strCreateUser = LanguageLoader.getProperty("label.createNewUser");
	String strCreateRole = LanguageLoader.getProperty("label.createNewRole");

	public ServerGUI serverGui = null;

	boolean showRoleOnly = false;

	public PanelAccessControlManagement(Connection conn) {
		this.conn = conn;
		showRoleOnly = true;

		init();
	}

	public PanelAccessControlManagement(Connection conn, ServerGUI serverGui) {

		this.conn = conn;
		this.serverGui = serverGui;

		init();

	}

	private void init() {

		if (!showRoleOnly) {
			userPane = new PanelUserManagement(conn);
			userPane.parentPanel = this;
			commonPane.add(userPane, getPanelName(UserType, strCreateUser));
			panelNames.put(getPanelName(UserType, strCreateUser), userPane);
		}

		rolePane = new PanelRoleManagement(conn);
		rolePane.parentPanel = this;
		commonPane.add(rolePane, getPanelName(RoleType, strCreateRole));
		panelNames.put(getPanelName(RoleType, strCreateRole), rolePane);

		treePane = new JPanel(new BorderLayout());

		buildTree();

		// create slipane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treePane, commonPane);
		splitPane.setDividerLocation(150);

		// add to the main panel
		this.setLayout(new BorderLayout());
		this.add(splitPane, BorderLayout.CENTER);
	}

	private void buildTree() {

		treePane.removeAll();

		// create tree panel
		String securityName = AccCtrlLanguageLoader
				.getProperty("security_tree.root_name");
		String roleNode = AccCtrlLanguageLoader
				.getProperty("security_tree.role");
		String userNode = AccCtrlLanguageLoader
				.getProperty("security_tree.user");

		root = new DefaultMutableTreeNode(securityName);

		String[] roles = MetaDataAccess.metaGetRoles(conn);
		String[] users = MetaDataAccess.metaGetUsers(conn);

		String[] roleNodes = new String[roles.length + 1];
		System.arraycopy(roles, 0, roleNodes, 0, roles.length);
		roleNodes[roleNodes.length - 1] = strCreateRole;

		String[] userNodes = new String[users.length + 1];
		System.arraycopy(users, 0, userNodes, 0, users.length);
		userNodes[userNodes.length - 1] = strCreateUser;

		tree = new MySecurityTree();
		tree.setParent(this);

		tree.setRootNode(root);

		if (showRoleOnly) {
			tree.setNumberFirstLevelChild(1);
			tree.setFirstLevelChildData(roleNode, roleNodes);
		} else {
			tree.setNumberFirstLevelChild(2);
			tree.setFirstLevelChildData(roleNode, roleNodes);
			tree.setFirstLevelChildData(userNode, userNodes);
		}

		tree.buildTree();

		scrollPaneTree = new JScrollPane(tree);
		scrollPaneTree.setBorder(GuiHelper.createLnFBorder());

		treePane.add(scrollPaneTree, BorderLayout.CENTER);

		// add header and footer panel
		String strHeader = AccCtrlLanguageLoader
				.getProperty("security_tree.header");
		String strFooter;
		if (showRoleOnly) {
			strFooter = AccCtrlLanguageLoader
					.getProperty("security_tree.footer_role");
		} else {
			strFooter = AccCtrlLanguageLoader
					.getProperty("security_tree.footer_user");
		}

		GradientLabel treeHeader = new GradientLabel(strHeader);
		treeHeader.setForeground(GuiLoader.titleColor);
		treePane.add(treeHeader, BorderLayout.NORTH);

		GradientLabel treeFooter = new GradientLabel(strFooter);
		treeFooter.setStartColor(GuiLoader.selectionBkColor);
		treeFooter.setForeground(GuiLoader.titleColor);
		treePane.add(treeFooter, BorderLayout.SOUTH);

	}

	public void reloadSecurityTree() {

		root.removeAllChildren();

		// reload tree
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		treeModel.reload();

		// re-add node
		String roleNode = AccCtrlLanguageLoader
				.getProperty("security_tree.role");
		String userNode = AccCtrlLanguageLoader
				.getProperty("security_tree.user");

		String[] roles = MetaDataAccess.metaGetRoles(conn);
		String[] users = MetaDataAccess.metaGetUsers(conn);

		String[] roleNodes = new String[roles.length + 1];
		System.arraycopy(roles, 0, roleNodes, 0, roles.length);
		roleNodes[roleNodes.length - 1] = strCreateRole;

		String[] userNodes = new String[users.length + 1];
		System.arraycopy(users, 0, userNodes, 0, users.length);
		userNodes[userNodes.length - 1] = strCreateUser;

		if (showRoleOnly) {
			tree.setNumberFirstLevelChild(1);
			tree.setFirstLevelChildData(roleNode, roleNodes);
		} else {
			tree.setNumberFirstLevelChild(2);
			tree.setFirstLevelChildData(roleNode, roleNodes);
			tree.setFirstLevelChildData(userNode, userNodes);
		}

		tree.buildTree();

		treeModel.reload();
	}

	private String getPanelName(String type, String name) {
		return type + ":" + name;
	}

	public void updatePanel(String type, String name) {

		CardLayout cardLayout = (CardLayout) (commonPane.getLayout());
		String cardName = null;

		if (type.contains(UserType)) {
			cardName = getPanelName(UserType, name);
		} else {
			cardName = getPanelName(RoleType, name);
		}

		// check if this name has already created panel
		// if yes: just retrieve the old panel as follows
		if (panelNames.containsKey(cardName)) {

			cardLayout.show(commonPane, cardName);
			// reload general feature incase metadata received from bootstrap
			if (type.contains(UserType)) {

				PanelUserManagement pane = (PanelUserManagement) panelNames
						.get(cardName);
				pane.updateGeneralInterface();

			} else {

				PanelRoleManagement pane = (PanelRoleManagement) panelNames
						.get(cardName);
				pane.updateGeneralInterface();
			}
		} else { // if no: create new panel and add to card layout
			if (type.contains(UserType)) {
				userPane = new PanelUserManagement(conn, name);
				commonPane.add(userPane, cardName);
				cardLayout.show(commonPane, cardName);

				panelNames.put(cardName, userPane);
			} else {
				rolePane = new PanelRoleManagement(conn, name);
				commonPane.add(rolePane, cardName);
				cardLayout.show(commonPane, cardName);

				panelNames.put(cardName, rolePane);
			}

		}

	}

}
