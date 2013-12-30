package sg.edu.nus.accesscontrol.bootstrap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import sg.edu.nus.accesscontrol.AccCtrlLanguageLoader;
import sg.edu.nus.accesscontrol.gui.MyDBTree;
import sg.edu.nus.accesscontrol.gui.MyJSelectionTable;
import sg.edu.nus.accesscontrol.gui.MyJTable;
import sg.edu.nus.accesscontrol.gui.PanelAccessControlManagement;
import sg.edu.nus.gui.GuiHelper;
import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.gui.customcomponent.CWTabbedPaneUI;
import sg.edu.nus.gui.customcomponent.GradientLabel;
import sg.edu.nus.gui.customcomponent.GradientOvalButton;
import sg.edu.nus.gui.customcomponent.GradientRoundRectButton;
import sg.edu.nus.gui.customcomponent.GridBagPanel;
import sg.edu.nus.gui.customcomponent.HeaderFooterPanel;
import sg.edu.nus.gui.customcomponent.SimpleGradientPanel;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.util.MetaDataAccess;

public class PanelRoleManagement extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2632132355617506108L;

	String roleNameToModify = "new role name";

	boolean flagCreateNewRole = true;

	private String strCmdApply = "apply";
	private String strCmdRevert = "revert";

	private String strCmdAddRole = "add_role";
	private String strCmdRemoveRole = "remove_role";
	private String strCmdAddPrivilege = "add_privilege";
	private String strCmdRemovePrivilege = "remove_privilege";

	private JTabbedPane tabPane = new JTabbedPane();

	int selectedRoleIndex = 0;

	JTextField txtRoleName = null;
	JTextArea txtGeneralRoleDesc = null;
	JPasswordField passwd = null;
	JPasswordField confirmPasswd = null;
	JTextArea txtRoleDesc = null;
	JList listRoles = null;
	GradientRoundRectButton btnAddRole = null;
	GradientRoundRectButton btnRemoveRole = null;
	GradientRoundRectButton btnAddPrivilege = null;
	GradientRoundRectButton btnRemovePrivilege = null;
	MyJTable tableGranted = null;
	MyJTable tableGrantedPrivilege = null;
	MyJSelectionTable tableSelectionColumn = null;
	JList listPrivileges = null;
	MyDBTree dbTree = null;
	JTextArea textRowLevel = null;

	int widthLong = 240;
	int widthCol = 240;
	int widthPane = 700;

	// general data
	String[] availRoles = null;
	String[] availPrivileges = null;
	String[] availRolesDesc = null;
	Object[][] dataSectionColumn = null;

	// specific data

	String roleDesc = "describe user here";

	String[][] grantedRoles = null;
	String[][] grantedPrivileges = null;

	String old_roleDesc = "describe user here";

	String old_password = "";

	String[][] old_grantedRoles = null;
	String[][] old_grantedPrivileges = null;

	public PanelAccessControlManagement parentPanel = null;

	Connection conn = null;

	DefaultMutableTreeNode root = null;

	// constructor
	public PanelRoleManagement(Connection conn) {

		super(new BorderLayout());

		this.conn = conn;

		flagCreateNewRole = true;

		initUI();

		initListeners();

	}

	public PanelRoleManagement(Connection conn, String roleNameToModify) {

		super(new BorderLayout());

		this.conn = conn;

		flagCreateNewRole = false;

		this.roleNameToModify = roleNameToModify;

		initUI();

		initListeners();
	}

	private void loadData() {

		// load common component
		updateGeneralInterface();

		if (flagCreateNewRole) {
			return; // no need load info of existing user
		}

		// load data for sepcific user
		txtRoleName.setEditable(false);

		String[][] roleDescResult = MetaDataAccess.metaGetRoleDesc(conn,
				roleNameToModify);
		roleDesc = roleDescResult[0][0];

		grantedRoles = MetaDataAccess.metaGetRoleGrantedRole(conn,
				roleNameToModify);

		grantedPrivileges = MetaDataAccess
				.metaGetRoleGrantedPrivilegeWithPrivilegeId(conn,
						roleNameToModify);

		updateSpecificInterface();

		storeStateForRevert();

	}

	public void updateGeneralInterface() {

		String[][] availRolesWithDesc = MetaDataAccess
				.metaGetRolesWithDescripton(conn);
		availRoles = new String[availRolesWithDesc.length];
		availRolesDesc = new String[availRolesWithDesc.length];
		for (int i = 0; i < availRolesWithDesc.length; i++) {
			availRoles[i] = availRolesWithDesc[i][0];
			availRolesDesc[i] = availRolesWithDesc[i][1];
		}

		availPrivileges = MetaDataAccess.metaGetAvailPrivileges(conn);

		listRoles.setListData(availRoles);
		listRoles.setSelectedIndex(0);
		txtRoleDesc.setText(availRolesDesc[0]);
		tableSelectionColumn.setTableData(dataSectionColumn);
		listPrivileges.setListData(availPrivileges);
		listPrivileges.setSelectedIndex(0);

		updateDBTree();

	}

	private void updateDBTree() {
		root.removeAllChildren();

		// reload tree
		DefaultTreeModel treeModel = (DefaultTreeModel) dbTree.getModel();
		treeModel.reload();

		String table = AccCtrlLanguageLoader.getProperty("db_tree.table");
		String view = AccCtrlLanguageLoader.getProperty("db_tree.view");
		String[] tables = MetaDataAccess.metaGetTables(conn);

		dbTree.setNumberFirstLevelChild(2);
		if (tables == null) {
			tables = new String[] {};
		}
		dbTree.setFirstLevelChildData(table, tables);
		dbTree.setFirstLevelChildData(view, new String[] {});
		dbTree.buildTree();

		treeModel.reload();
	}

	private void updateSpecificInterface() {

		txtRoleName.setText(roleNameToModify);
		txtGeneralRoleDesc.setText(roleDesc);

		tableGranted.setTableData(grantedRoles);
		tableGrantedPrivilege.setTableData(grantedPrivileges);
	}

	private void initListeners() {

		listRoles.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting())
					return;
				selectedRoleIndex = listRoles.getSelectedIndex();
				if (selectedRoleIndex >= 0) {
					txtRoleDesc.setText(availRolesDesc[selectedRoleIndex]);
				}
			}
		});

		btnAddRole.setActionCommand(strCmdAddRole);
		btnAddRole.addActionListener(this);

		btnRemoveRole.setActionCommand(strCmdRemoveRole);
		btnRemoveRole.addActionListener(this);

		btnAddPrivilege.setActionCommand(strCmdAddPrivilege);
		btnAddPrivilege.addActionListener(this);

		btnRemovePrivilege.setActionCommand(strCmdRemovePrivilege);
		btnRemovePrivilege.addActionListener(this);

		dbTree.setTableSelectionColumn(tableSelectionColumn);

	}

	private void initUI() {

		widthLong = widthCol = GuiLoader.widthColumn;

		this.setPreferredSize(new Dimension(widthPane, 635));

		tabPane.setUI(new CWTabbedPaneUI());

		String tab_title = AccCtrlLanguageLoader
				.getProperty("role_config.tab_general");
		tabPane.addTab(tab_title, createTabGeneral());

		tab_title = AccCtrlLanguageLoader.getProperty("role_config.tab_roles");
		tabPane.addTab(tab_title, createTabRoles());

		tab_title = AccCtrlLanguageLoader.getProperty("role_config.tab_schema");
		tabPane.addTab(tab_title, createTabSchemaPrivilege());

		this.add(createHeaderPanel(), BorderLayout.NORTH);

		this.add(tabPane, BorderLayout.CENTER);

		this.add(createCommandPanel(), BorderLayout.SOUTH);

		SimpleGradientPanel sideLeftPanel = new SimpleGradientPanel();
		sideLeftPanel.setDirection(SimpleGradientPanel.VERTICAL);
		sideLeftPanel.setLayout(new BorderLayout());
		JPanel insideleft = GuiHelper.createBlankColorPanel(20,
				GuiLoader.themeBkColor);
		sideLeftPanel.add(insideleft, BorderLayout.SOUTH);

		JPanel sideRightPanel = GuiHelper.createBlankThemePanel(20);

		this.add(sideLeftPanel, BorderLayout.WEST);
		this.add(sideRightPanel, BorderLayout.EAST);

		// load data from data to graphic interface
		loadData();

	}

	private JPanel createHeaderPanel() {

		SimpleGradientPanel pane = new SimpleGradientPanel();
		pane.setLayout(new BorderLayout());

		String header_role = AccCtrlLanguageLoader
				.getProperty("role_config.header_role");
		String header_action = flagCreateNewRole ? AccCtrlLanguageLoader
				.getProperty("role_config.header_create_role")
				: AccCtrlLanguageLoader
						.getProperty("role_config.header_modify_role");
		String htmlLabel = "<html><font size = 5 >" + header_role
				+ "</font><br>" + "<font color=\"#0000ff\">" + header_action
				+ "<br>";

		JLabel lblComment = new JLabel(htmlLabel);
		lblComment.setBorder(new EmptyBorder(new Insets(5, 9, 5, 5)));

		pane.add(lblComment, BorderLayout.WEST);

		return pane;
	}

	private JPanel createCommandPanel() {

		JPanel pane = GuiHelper.createThemePanel();

		pane.setBorder(new EmptyBorder(new Insets(6, 6, 6, 6)));

		// one way of layout

		JButton btnApply = createButtonApply();
		pane.add(btnApply);

		int buttonDistance = GuiLoader.buttonDistance;
		pane.add(GuiHelper.createBlankThemePanel(3, buttonDistance, 3,
				buttonDistance));

		JButton btnRevert = createButtonRevert();
		pane.add(btnRevert);

		return pane;
	}

	private JButton createButtonApply() {

		String caption = AccCtrlLanguageLoader.getProperty("button.apply");

		JButton btn = null;
		btn = new GradientOvalButton(caption);

		btn.setActionCommand(strCmdApply);
		btn.addActionListener(this);

		return btn;
	}

	private JButton createButtonRevert() {

		String caption = AccCtrlLanguageLoader.getProperty("button.revert");

		JButton btn = null;
		btn = new GradientOvalButton(caption);

		btn.setActionCommand(strCmdRevert);
		btn.addActionListener(this);
		return btn;
	}

	private JPanel createTabGeneral() {

		HeaderFooterPanel pane = new HeaderFooterPanel();
		pane.setBorder(new EmptyBorder(new Insets(GuiLoader.contentInset + 40,
				0, 0, 0)));

		String title = AccCtrlLanguageLoader
				.getProperty("panel_role_general.title");
		pane.setTitle(title);
		pane.setImage(GuiHelper.getIcon("image_role"));

		JPanel contentPane = createTabGeneralContent();

		pane.add(contentPane);

		return pane;
	}

	private JPanel createTabGeneralContent() {

		GridBagPanel panel = new GridBagPanel(new Insets(0, 0, 0, 0));

		int widthMedium = widthLong / 2;

		int heightLabel = 20;
		int heightTextArea = 80;

		String lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_role_general.role_name");

		GradientLabel lbl1 = GuiHelper.createGradientLabel(lblCaption);
		lbl1.setPreferredSize(new Dimension(widthMedium, heightLabel));

		panel.addComponent(lbl1, 1, 2);

		txtRoleName = GuiHelper.createLnFTextField();
		txtRoleName
				.setPreferredSize(new Dimension(widthMedium, heightLabel + 4));

		// data
		txtRoleName.setText(roleNameToModify);

		panel.addComponent(txtRoleName, 2, 2);

		// insert image label on the left

		lblCaption = "";
		JLabel lblImage = GuiHelper.createLnFLabel(lblCaption);
		ImageIcon icon = GuiHelper.getIcon("image_information");

		lblImage.setIcon(icon);

		lblImage.setPreferredSize(new Dimension(widthMedium / 2, heightLabel));
		panel.addFilledComponent(lblImage, 1, 1, 1, 2, GridBagConstraints.BOTH);

		// insert hidden label for seperation...
		lblCaption = " ";
		JLabel lblHidden = GuiHelper.createLnFLabel(lblCaption);
		lblHidden.setPreferredSize(new Dimension(widthLong, heightLabel));
		panel.addComponent(lblHidden, 3, 2);

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_role_general.role_desc");
		GradientLabel lbl2 = GuiHelper.createGradientLabel(lblCaption);
		lbl2.setPreferredSize(new Dimension(widthLong, heightLabel));

		panel.addComponent(lbl2, 4, 2);

		txtGeneralRoleDesc = GuiHelper.createLnFTextArea();
		JScrollPane scroll = GuiHelper.createLnFScrollPane(txtGeneralRoleDesc);

		// data
		txtGeneralRoleDesc.setText("");
		scroll.setPreferredSize(new Dimension(widthLong, heightTextArea));

		panel.addComponent(scroll, 5, 2);

		return panel;
	}

	private JPanel createTabRoles() {

		HeaderFooterPanel pane = new HeaderFooterPanel();
		pane.setBorder(new EmptyBorder(new Insets(GuiLoader.contentInset + 30,
				0, 0, 0)));

		String title = AccCtrlLanguageLoader
				.getProperty("panel_role_hierarchy.title");
		pane.setTitle(title);
		pane.setImage(GuiHelper.getIcon("image_role"));

		JPanel contentPane = createTabRoleContent();

		pane.add(contentPane);

		return pane;
	}

	private JPanel createTabRoleContent() {

		JPanel tabRoleContentPanel = new JPanel(new BorderLayout());

		int distanceCol = 10;

		GridBagPanel superPanel = new GridBagPanel(new Insets(0, distanceCol,
				20, distanceCol));

		// panel available role
		GridBagPanel panel = new GridBagPanel(new Insets(0, distanceCol, 0,
				distanceCol));

		int heightCol = 90;
		int heightLabel = 20;
		Dimension mediumRightField = new Dimension(widthCol, heightLabel);
		Dimension hugeRightField = new Dimension(widthCol, heightCol);
		Dimension mediumLeftField = new Dimension(widthCol, heightLabel);
		Dimension hugeLeftField = new Dimension(widthCol, heightCol);

		String lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_role_hierarchy.available_role");

		GradientLabel lbl1 = GuiHelper.createGradientLabel(lblCaption);
		lbl1.setStartColor(new Color(232, 242, 254));
		lbl1.setPreferredSize(mediumLeftField);

		panel.addComponent(lbl1, 1, 1);

		listRoles = GuiHelper.createLnFList();
		JScrollPane scroll1 = GuiHelper.createLnFScrollPane(listRoles);

		scroll1.setPreferredSize(hugeLeftField);

		panel.addComponent(scroll1, 2, 1);

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_role_hierarchy.role_desc");
		GradientLabel lbl2 = GuiHelper.createGradientLabel(lblCaption);
		lbl2.setStartColor(new Color(232, 242, 254));
		lbl2.setPreferredSize(mediumRightField);

		panel.addComponent(lbl2, 1, 2);

		txtRoleDesc = GuiHelper.createLnFTextArea();
		JScrollPane scroll2 = GuiHelper.createLnFScrollPane(txtRoleDesc);
		txtRoleDesc.setEditable(false);

		scroll2.setPreferredSize(hugeRightField);

		panel.addComponent(scroll2, 2, 2);

		// panel add/remove button

		JPanel panelBtnAddRemove = new JPanel();
		panelBtnAddRemove.setBackground(GuiLoader.contentBkColor);
		int heightBtn = 20;
		int widthBtn = 75;
		panelBtnAddRemove.setPreferredSize(new Dimension(2 * widthCol + 4
				* distanceCol, heightBtn + 10));

		String btncaption = AccCtrlLanguageLoader.getProperty("button.add");
		btnAddRole = new GradientRoundRectButton(btncaption);

		btncaption = AccCtrlLanguageLoader.getProperty("button.remove");
		btnRemoveRole = new GradientRoundRectButton(btncaption);

		btnAddRole.setPreferredSize(new Dimension(widthBtn, heightBtn));
		btnRemoveRole.setPreferredSize(new Dimension(widthBtn, heightBtn));
		btnAddRole.setForeground(GuiLoader.contentTextColor.darker());
		btnRemoveRole.setForeground(GuiLoader.contentTextColor.darker());

		JPanel panelInvisble = GuiHelper.createBlankColorPanel(5, widthBtn / 2,
				GuiLoader.contentBkColor);

		panelBtnAddRemove.add(btnAddRole);
		panelBtnAddRemove.add(panelInvisble);
		panelBtnAddRemove.add(btnRemoveRole);

		superPanel.addComponent(panel, 1, 1);
		superPanel.addComponent(panelBtnAddRemove, 2, 1);

		tabRoleContentPanel.add(superPanel, BorderLayout.CENTER);

		JPanel tablePanel = new JPanel(new BorderLayout());

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_role_hierarchy.granted_role");
		GradientLabel lblGranted = GuiHelper.createGradientLabel(lblCaption);
		lblGranted.setForeground(GuiLoader.contentTextColor.darker());
		lblGranted.setForeground(GuiLoader.titleColor);

		lblGranted.setPreferredSize(new Dimension(widthCol * 2 + 2
				* distanceCol, heightLabel));

		tablePanel.add(lblGranted, BorderLayout.NORTH);

		tableGranted = new MyJTable();
		String colName1 = AccCtrlLanguageLoader
				.getProperty("panel_role_hierarchy.role_name");
		String colName2 = AccCtrlLanguageLoader
				.getProperty("panel_role_hierarchy.role_desc");

		String[] columnNames = { colName1, colName2 };
		tableGranted.setColumnNames(columnNames);

		JScrollPane scroll = tableGranted.getScrollWrapper(widthCol * 2,
				heightCol);
		scroll.setBorder(GuiHelper.createLnFBorder());
		scroll.setBackground(GuiLoader.themeBkColor);

		tablePanel.setBackground(GuiLoader.contentBkColor);
		tablePanel.add(scroll, BorderLayout.CENTER);
		tablePanel.setBorder(new EmptyBorder(new Insets(0, 2 * distanceCol - 3,
				0, 2 * distanceCol - 3)));

		tabRoleContentPanel.add(tablePanel, BorderLayout.SOUTH);

		return tabRoleContentPanel;
	}

	private JPanel createTabSchemaPrivilege() {

		HeaderFooterPanel pane = new HeaderFooterPanel();
		int inset = GuiLoader.contentInset - 9;
		pane.setBorder(new EmptyBorder(new Insets(inset, inset, inset, inset)));

		String title = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.title_user");
		pane.setTitle(title);
		pane.setImage(GuiHelper.getIcon("image_role"));

		JPanel contentPane = createTabSchemaPrivilegeContent();

		pane.add(contentPane);

		return pane;
	}

	private JPanel createTabSchemaPrivilegeContent() {

		JPanel tabRoleContentPanel = new JPanel(new BorderLayout());

		GridBagPanel superPanel = new GridBagPanel();

		// panel available role
		int distanceCol = 10;

		GridBagPanel panel = new GridBagPanel(new Insets(0, distanceCol, 0,
				distanceCol));

		int heightCol = 90;
		int heightLabel = 20;

		Dimension mediumRightField = new Dimension(widthCol, heightLabel);
		Dimension hugeRightField = new Dimension(widthCol, heightCol);
		Dimension mediumLeftField = new Dimension(widthCol, heightLabel);
		Dimension hugeLeftField = new Dimension(widthCol, heightCol);

		String lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.available_table");

		GradientLabel lbl1 = GuiHelper.createGradientLabel(lblCaption);
		lbl1.setPreferredSize(mediumLeftField);
		lbl1.setStartColor(new Color(232, 242, 254));

		panel.addComponent(lbl1, 1, 1);

		// add dbtree

		dbTree = new MyDBTree(conn);
		String dbName = MetaDataAccess.metaGetCorporateDbName(conn);
		root = new DefaultMutableTreeNode(dbName);
		dbTree.setRootNode(root);

		JScrollPane scrollTree = new JScrollPane(dbTree);
		scrollTree.setPreferredSize(hugeLeftField);
		scrollTree.setBorder(GuiHelper.createLnFBorder());

		panel.addComponent(scrollTree, 2, 1);

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.privilege_name");
		GradientLabel lbl2 = GuiHelper.createGradientLabel(lblCaption);
		lbl2.setPreferredSize(mediumRightField);
		lbl2.setStartColor(new Color(232, 242, 254));

		panel.addComponent(lbl2, 1, 2);

		listPrivileges = GuiHelper.createLnFList();
		JScrollPane scroll1 = GuiHelper.createLnFScrollPane(listPrivileges);

		scroll1.setPreferredSize(hugeLeftField);

		panel.addComponent(scroll1, 2, 2);

		// add hidden label to seperate
		JLabel hiddenLabel = GuiHelper.createLnFLabel("");
		hiddenLabel.setPreferredSize(new Dimension(widthCol, heightLabel / 2));
		panel.addComponent(hiddenLabel, 3, 1);

		// add row-level & col-level
		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.column_level");
		GradientLabel lblColLevel = GuiHelper.createGradientLabel(lblCaption);
		lblColLevel.setForeground(GuiLoader.contentTextColor.darker());

		lblColLevel.setPreferredSize(mediumLeftField);

		panel.addComponent(lblColLevel, 4, 1);

		String colName1 = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted");
		String colName2 = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.column_name");

		String[] columnNames = { colName2 };

		tableSelectionColumn = new MyJSelectionTable(columnNames, colName1);

		JScrollPane scroll = tableSelectionColumn.getScrollWrapper();
		scroll.setPreferredSize(hugeLeftField);

		scroll.setBorder(GuiHelper.createLnFBorder());
		scroll.setBackground(GuiLoader.themeBkColor);

		panel.addComponent(scroll, 5, 1);

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.row_level");
		GradientLabel lblRowLevel = GuiHelper.createGradientLabel(lblCaption);
		lblRowLevel.setForeground(GuiLoader.contentTextColor.darker());

		lblRowLevel.setPreferredSize(mediumRightField);

		panel.addComponent(lblRowLevel, 4, 2);

		textRowLevel = GuiHelper.createLnFTextArea();

		JScrollPane scrollTextRowLevel = GuiHelper
				.createLnFScrollPane(textRowLevel);
		scrollTextRowLevel.setPreferredSize(hugeRightField);

		panel.addComponent(scrollTextRowLevel, 5, 2);

		// panel add/remove button

		JPanel panelBtnAddRemove = new JPanel();
		panelBtnAddRemove.setBackground(GuiLoader.contentBkColor);
		int heightBtn = 20;
		int widthBtn = 75;
		panelBtnAddRemove.setPreferredSize(new Dimension(2 * widthCol + 4
				* distanceCol, heightBtn + 10));

		String btncaption = AccCtrlLanguageLoader.getProperty("button.add");

		btnAddPrivilege = new GradientRoundRectButton(btncaption);

		btncaption = AccCtrlLanguageLoader.getProperty("button.remove");
		btnRemovePrivilege = new GradientRoundRectButton(btncaption);

		btnAddPrivilege.setPreferredSize(new Dimension(widthBtn, heightBtn));
		btnRemovePrivilege.setPreferredSize(new Dimension(widthBtn, heightBtn));
		btnAddPrivilege.setForeground(GuiLoader.contentTextColor.darker());
		btnRemovePrivilege.setForeground(GuiLoader.contentTextColor.darker());

		JPanel panelInvisble = GuiHelper.createBlankColorPanel(5, widthBtn / 2,
				GuiLoader.contentBkColor);

		panelBtnAddRemove.add(btnAddPrivilege);
		panelBtnAddRemove.add(panelInvisble);
		panelBtnAddRemove.add(btnRemovePrivilege);

		superPanel.addComponent(panel, 1, 1);

		superPanel.addComponent(panelBtnAddRemove, 2, 1);

		tabRoleContentPanel.add(superPanel, BorderLayout.CENTER);
		
		// ///// add table privilege

		JPanel tablePanel = new JPanel(new BorderLayout());

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege");
		GradientLabel lblGranted = GuiHelper.createGradientLabel(lblCaption);
		lblGranted.setForeground(GuiLoader.contentTextColor.darker());
		lblGranted.setForeground(GuiLoader.titleColor);

		lblGranted.setPreferredSize(new Dimension(widthCol * 2 + 2
				* distanceCol, heightLabel));

		tablePanel.add(lblGranted, BorderLayout.NORTH);

		tableGrantedPrivilege = new MyJTable();

		colName1 = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege_name");
		colName2 = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege_object");
		String colName3 = AccCtrlLanguageLoader
				.getProperty("panel_schema_privilege.granted_privilege_type");

		columnNames = new String[] { colName1, colName2, colName3 };
		tableGrantedPrivilege.setColumnNames(columnNames);

		JScrollPane scrollGrantedPrivilege = tableGrantedPrivilege
				.getScrollWrapper(widthCol * 2, 3 * heightCol / 4);
		scrollGrantedPrivilege.setBorder(GuiHelper.createLnFBorder());
		scrollGrantedPrivilege.setBackground(GuiLoader.themeBkColor);

		tablePanel.setBackground(GuiLoader.contentBkColor);
		tablePanel.add(scrollGrantedPrivilege, BorderLayout.CENTER);
		tablePanel.setBorder(new EmptyBorder(new Insets(0, 2 * distanceCol - 3,
				0, 2 * distanceCol - 3)));

		tabRoleContentPanel.add(tablePanel, BorderLayout.SOUTH);

		return tabRoleContentPanel;
	}

	// //////////////////////////////

	private void storeStateForRevert() {
		old_roleDesc = new String(roleDesc);

		if (grantedRoles != null) {
			old_grantedRoles = new String[grantedRoles.length][];
			for (int i = 0; i < grantedRoles.length; i++) {
				old_grantedRoles[i] = new String[grantedRoles[i].length];
				for (int j = 0; j < grantedRoles[i].length; j++) {
					old_grantedRoles[i][j] = new String(grantedRoles[i][j]);
				}
			}
		} else {
			old_grantedRoles = null;
		}

		if (grantedPrivileges != null) {
			old_grantedPrivileges = new String[grantedPrivileges.length][];
			for (int i = 0; i < grantedPrivileges.length; i++) {
				old_grantedPrivileges[i] = new String[grantedPrivileges[i].length];
				for (int j = 0; j < grantedPrivileges[i].length; j++) {
					old_grantedPrivileges[i][j] = new String(
							grantedPrivileges[i][j]);
				}
			}
		} else {
			old_grantedPrivileges = null;
		}
	}

	private void revertState() {
		roleDesc = new String(old_roleDesc);

		if (old_grantedRoles != null) {
			grantedRoles = new String[old_grantedRoles.length][];
			for (int i = 0; i < old_grantedRoles.length; i++) {
				grantedRoles[i] = new String[old_grantedRoles[i].length];
				for (int j = 0; j < old_grantedRoles[i].length; j++) {
					grantedRoles[i][j] = new String(old_grantedRoles[i][j]);
				}
			}
		} else {
			grantedRoles = null;
		}

		if (old_grantedPrivileges != null) {
			grantedPrivileges = new String[old_grantedPrivileges.length][];
			for (int i = 0; i < old_grantedPrivileges.length; i++) {
				grantedPrivileges[i] = new String[old_grantedPrivileges[i].length];
				for (int j = 0; j < old_grantedPrivileges[i].length; j++) {
					grantedPrivileges[i][j] = new String(
							old_grantedPrivileges[i][j]);
				}
			}
		} else {
			grantedPrivileges = null;
		}
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(strCmdApply)) {

			// store for revert first
			storeStateForRevert();

			String show = "";
			show += "Role Name: " + txtRoleName.getText() + "\n";

			roleDesc = txtGeneralRoleDesc.getText();
			show += "Role Desc: " + txtGeneralRoleDesc.getText() + "\n";

			grantedRoles = (String[][]) tableGranted.getTableArray();
			grantedPrivileges = (String[][]) tableGrantedPrivilege
					.getTableArray();

			updateDatabase(true);

			if (flagCreateNewRole) {

				// update it interface
				parentPanel.reloadSecurityTree();

			}

		} else if (e.getActionCommand().equals(strCmdRevert)) {

			revertState();

			updateSpecificInterface();

			updateDatabase(false);

			if (flagCreateNewRole) {
			}

		} else if (e.getActionCommand().equals(strCmdAddRole)) {
			String[] row = new String[2];
			row[0] = availRoles[selectedRoleIndex];
			row[1] = availRolesDesc[selectedRoleIndex];
			tableGranted.addRow(row);

		} else if (e.getActionCommand().equals(strCmdRemoveRole)) {
			tableGranted.deleteSelectedRow();
		} else if (e.getActionCommand().equals(strCmdAddPrivilege)) {

			String tableName = dbTree.getSelectedTableName();

			if (tableName == null) {
				return;
			}

			String rowLevel = textRowLevel.getText();
			ArrayList columns = tableSelectionColumn.getCheckedRows();
			String privilegeId = (String) listPrivileges.getSelectedValue();

			String[] row = new String[3];
			row[0] = privilegeId;

			if (rowLevel.length() == 0 && columns.size() == 0) {
				row[1] = tableName;
				row[2] = MetaDataAccess.WHOLE_TABLE;
				tableGrantedPrivilege.addRow(row);
				return;
			}

			if (rowLevel.length() > 0) {
				row[1] = tableName + MetaDataAccess.DOT_SEPERATOR
						+ MetaDataAccess.WHERE + " " + rowLevel;
				row[2] = MetaDataAccess.ROW_LEVEL;
				tableGrantedPrivilege.addRow(row);
			}

			for (int i = 0; i < columns.size(); i++) {
				Object[] data_items = (Object[]) columns.get(i);
				String columnName = (String) data_items[0];
				row[1] = tableName + MetaDataAccess.DOT_SEPERATOR + columnName;
				row[2] = MetaDataAccess.COLUMN_LEVEL;
				tableGrantedPrivilege.addRow(row);
			}

		} else if (e.getActionCommand().equals(strCmdRemovePrivilege)) {
			tableGrantedPrivilege.deleteSelectedRow();
		}

	}

	private void updateDatabase(boolean apply) {
		// delete old data in database and insert updated data from interface
		String sql = null;

		try {

			Statement stmt = conn.createStatement();
			
			String sqlList = "Sql for Update role config: \n";
			
			if (flagCreateNewRole) { // create new user

				roleNameToModify = txtRoleName.getText();
				if (apply) {
					sql = "insert into " + MetaDataAccess.TABLE_ROLES
							+ " values('" + roleNameToModify + "','" + roleDesc
							+ "')";
					
					sqlList += sql +"\n";
					stmt.executeUpdate(sql);

				} else {// revert: delete user
					// delete old and insert new user-role assignment
					sql = "delete from " + MetaDataAccess.TABLE_ROLES
							+ " where role_name ='" + roleNameToModify + "'";
					
					sqlList += sql +"\n";
					stmt.executeUpdate(sql);

				}

			} else {// modify user detail
				sql = "update " + MetaDataAccess.TABLE_ROLES
						+ " set role_desc='" + roleDesc + "' where role_name='"
						+ roleNameToModify + "'";
				
				sqlList += sql +"\n";
				stmt.executeUpdate(sql);

			}

			// delete old and insert new role hierchy assignment
			sql = "delete from " + MetaDataAccess.TABLE_ROLE_HIER
					+ " where super_role_name ='" + roleNameToModify + "'";
			
			sqlList += sql +"\n";
			stmt.executeUpdate(sql);

			if (grantedRoles != null) {
				for (int i = 0; i < grantedRoles.length; i++) {
					sql = "insert into " + MetaDataAccess.TABLE_ROLE_HIER
							+ " values('" + roleNameToModify + "','"
							+ grantedRoles[i][0] + "')";
					
					sqlList += sql +"\n";
					stmt.executeUpdate(sql);
				}
			}

			// delete old and insert new user-permission assignment
			sql = "delete from " + MetaDataAccess.TABLE_ROLE_PERM
					+ " where role_name ='" + roleNameToModify + "'";
			
			sqlList += sql +"\n";
			stmt.executeUpdate(sql);

			if (grantedPrivileges != null) {
				for (int i = 0; i < grantedPrivileges.length; i++) {
					sql = "insert into " + MetaDataAccess.TABLE_ROLE_PERM
							+ " values('" + roleNameToModify + "','"
							+ grantedPrivileges[i][0] + "','"
							+ grantedPrivileges[i][1] + "','"
							+ grantedPrivileges[i][2] + "')";
					
					sqlList += sql +"\n";
					stmt.executeUpdate(sql);
				}
			}
			
			System.out.println(sqlList);

		} catch (SQLException e1) {
			LogManager.LogException(
					"Exception while updating role infomation to database", e1);
		}

	}

}
