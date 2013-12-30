package sg.edu.nus.accesscontrol.normalpeer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.util.MetaDataAccess;

public class PanelUserManagement extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2632132355617506108L;

	boolean flagCreateNewUser = true;

	private String strCmdApply = "apply";
	private String strCmdRevert = "revert";

	private String strCmdAddRole = "add_role";
	private String strCmdRemoveRole = "remove_role";
	private String strCmdAddPrivilege = "add_privilege";
	private String strCmdRemovePrivilege = "remove_privilege";

	private JTabbedPane tabPane = new JTabbedPane();

	int selectedRoleIndex = 0;

	JTextField txtUserName = null;
	JTextArea txtUserDesc = null;
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
	String userNameToModify = "new user name";
	String userDesc = "describe user here";
	String password = "";
	String[][] grantedRoles = null;
	String[][] grantedPrivileges = null;

	String old_userDesc = "describe user here";
	String old_password = "";
	String[][] old_grantedRoles = null;
	String[][] old_grantedPrivileges = null;

	public PanelAccessControlManagement parentPanel = null;

	Connection conn = null;

	DefaultMutableTreeNode root = null;

	// constructor
	public PanelUserManagement(Connection conn) {

		super(new BorderLayout());

		this.conn = conn;

		flagCreateNewUser = true;

		initUI();

		initListeners();

	}

	public PanelUserManagement(Connection conn, String userNameToModify) {

		super(new BorderLayout());

		this.conn = conn;

		flagCreateNewUser = false;

		this.userNameToModify = userNameToModify;

		initUI();

		initListeners();
	}

	private void loadData() {

		// load common component
		updateGeneralInterface();

		if (flagCreateNewUser) {
			return; // no need load info of existing user
		}

		// load data for sepcific user
		txtUserName.setEditable(false);

		String[][] userDescPass = MetaDataAccess.metaGetUserDescPasswd(conn,
				userNameToModify);
		userDesc = userDescPass[0][0];

		password = userDescPass[0][1];

		grantedRoles = MetaDataAccess.metaGetUserGrantedRole(conn,
				userNameToModify);

		grantedPrivileges = MetaDataAccess
				.metaGetUserGrantedPrivilegeWithPrivilegeId(conn,
						userNameToModify);

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

		txtUserName.setText(userNameToModify);
		txtUserDesc.setText(userDesc);
		passwd.setText(password);
		confirmPasswd.setText(password);

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

		//
		String tab_title = AccCtrlLanguageLoader
				.getProperty("role_config.tab_general");
		tabPane.addTab(tab_title, createTabGeneral());

		tab_title = AccCtrlLanguageLoader.getProperty("role_config.tab_roles");
		tabPane.addTab(tab_title, createTabRoles());

		tab_title = AccCtrlLanguageLoader.getProperty("role_config.tab_schema");
		tabPane.addTab(tab_title, createTabSchemaPrivilege());

		// /
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

		String header_user = AccCtrlLanguageLoader
				.getProperty("user_config.header_user");

		String header_action = flagCreateNewUser ? AccCtrlLanguageLoader
				.getProperty("user_config.header_create_user")
				: AccCtrlLanguageLoader
						.getProperty("user_config.header_modify_user");
		String htmlLabel = "<html><font size = 5 >" + header_user
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
				.getProperty("panel_user_general.title");
		pane.setTitle(title);
		pane.setImage(GuiHelper.getIcon("image_user"));

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
				.getProperty("panel_user_general.user_name");

		GradientLabel lbl1 = GuiHelper.createGradientLabel(lblCaption);
		lbl1.setPreferredSize(new Dimension(widthMedium, heightLabel));

		panel.addComponent(lbl1, 1, 2);

		txtUserName = GuiHelper.createLnFTextField();
		txtUserName
				.setPreferredSize(new Dimension(widthMedium, heightLabel + 4));

		panel.addComponent(txtUserName, 2, 2);

		// insert image label on the left

		lblCaption = "";
		JLabel lblImage = GuiHelper.createLnFLabel(lblCaption);
		ImageIcon icon = GuiHelper.getIcon("image_information");

		lblImage.setIcon(icon);

		lblImage.setPreferredSize(new Dimension(widthMedium / 2, heightLabel));
		panel.addFilledComponent(lblImage, 1, 1, 1, 2, GridBagConstraints.BOTH);

		// /

		// insert hidden label for seperation...
		lblCaption = " ";
		JLabel lblHidden = GuiHelper.createLnFLabel(lblCaption);
		lblHidden.setPreferredSize(new Dimension(widthLong, heightLabel));
		panel.addComponent(lblHidden, 3, 2);

		// /

		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_user_general.user_desc");
		GradientLabel lbl2 = GuiHelper.createGradientLabel(lblCaption);
		lbl2.setPreferredSize(new Dimension(widthLong, heightLabel));
		// lbl2.setStartColor(new Color(232,242,254));

		panel.addComponent(lbl2, 4, 2);

		txtUserDesc = GuiHelper.createLnFTextArea();
		JScrollPane scroll = GuiHelper.createLnFScrollPane(txtUserDesc);

		scroll.setPreferredSize(new Dimension(widthLong, heightTextArea));

		panel.addComponent(scroll, 5, 2);

		// insert hidden label for seperation...
		lblCaption = " ";
		JLabel lblHidden2 = GuiHelper.createLnFLabel(lblCaption);
		lblHidden2.setPreferredSize(new Dimension(widthLong, heightLabel));
		panel.addComponent(lblHidden2, 6, 2);

		GridBagPanel authenticatePanel = new GridBagPanel(new Insets(8, 10, 12,
				10));

		Border border = BorderFactory.createLineBorder(GuiLoader.titleColor
				.brighter());

		TitledBorder titledBorder = new TitledBorder(border);
		Font font = new Font("titledBorderFont", Font.ITALIC, 12);
		titledBorder.setTitleColor(GuiLoader.titleColor);
		titledBorder.setTitleFont(font);

		String strAuthenticate = AccCtrlLanguageLoader
				.getProperty("panel_user_general.authentication");
		titledBorder.setTitle(strAuthenticate);

		authenticatePanel.setBorder(titledBorder);

		String strCaptionPasswd = AccCtrlLanguageLoader
				.getProperty("panel_user_general.passwd");
		JLabel lblPasswd = GuiHelper.createLnFLabel(strCaptionPasswd);
		authenticatePanel.addComponent(lblPasswd, 1, 1);

		passwd = GuiHelper.createLnFPasswordField();

		Dimension passwdSize = new Dimension(118, 20);
		passwd.setPreferredSize(passwdSize);
		authenticatePanel.addComponent(passwd, 1, 2);

		String strCaptionConfPasswd = AccCtrlLanguageLoader
				.getProperty("panel_user_general.confirm_passwd");
		JLabel lblConfirmPasswd = GuiHelper
				.createLnFLabel(strCaptionConfPasswd);
		authenticatePanel.addComponent(lblConfirmPasswd, 2, 1);

		confirmPasswd = GuiHelper.createLnFPasswordField();

		confirmPasswd.setPreferredSize(passwdSize);
		authenticatePanel.addComponent(confirmPasswd, 2, 2);

		panel.addComponent(authenticatePanel, 7, 2);

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

		// //
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

		//
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

		//
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

		// / panel granted roles

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
	
	private void storeStateForRevert() {
		old_userDesc = new String(userDesc);
		old_password = new String(password);

		// old_grantedRoles = grantedRoles;
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
		userDesc = new String(old_userDesc);
		password = new String(old_password);

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
			show += "User Name: " + txtUserName.getText() + "\n";

			userDesc = txtUserDesc.getText();
			show += "User Desc: " + txtUserDesc.getText() + "\n";

			password = new String(passwd.getPassword());
			String conf_password = new String(confirmPasswd.getPassword());

			if (!password.equals(conf_password)) {
				JOptionPane
						.showMessageDialog(null,
								"Please ensure password is the same as confirm passoword");
				return;
			}

			show += "Password: " + new String(passwd.getPassword()) + "\n";
			show += "Confirm Password: "
					+ new String(confirmPasswd.getPassword()) + "\n";

			grantedRoles = (String[][]) tableGranted.getTableArray();
			grantedPrivileges = (String[][]) tableGrantedPrivilege
					.getTableArray();

			updateDatabase(true);

			if (flagCreateNewUser) {
				// update it interface
				parentPanel.reloadSecurityTree();

				// send info to Bootstrap
				sendUserInfoToBootstrap(txtUserName.getText(), userDesc,
						password);

			}

		} else if (e.getActionCommand().equals(strCmdRevert)) {

			revertState();

			updateSpecificInterface();

			updateDatabase(false);

			if (flagCreateNewUser) {
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

	private void sendUserInfoToBootstrap(String userName, String userDesc,
			String password) {

		if (parentPanel.serverGui == null) {
			return;
		}

		try {
			String ip = ServerPeer.BOOTSTRAP_SERVER;
			int port = ServerPeer.BOOTSTRAP_SERVER_PORT;

			Head head = new Head(MsgType.ACCESS_CONTROL_NORMAL_PEER_USER_UPDATE
					.getValue());

			ServerPeer serverpeer = parentPanel.serverGui.peer();

			Body body = new MsgBodyUserToBootstrap(userName, userDesc,
					password, serverpeer.getServerPeerAdminName());

			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);

		} catch (Exception e) {
			LogManager
					.LogException(
							"Exception caught while sending user infomation to bootstrap server.",
							e);
		}
	}

	private void updateDatabase(boolean apply) {
		// delete old data in database and insert updated data from interface
		String sql = null;

		try {

			Statement stmt = conn.createStatement();
			
			if (flagCreateNewUser) { // create new user

				userNameToModify = txtUserName.getText();
				if (apply) {
					sql = "insert into " + MetaDataAccess.TABLE_USERS
							+ " values('" + userNameToModify + "','" + userDesc
							+ "','" + password + "')";
					stmt.executeUpdate(sql);
				} else {// revert: delete user
					// delete old and insert new user-role assignment
					sql = "delete from " + MetaDataAccess.TABLE_USERS
							+ " where user_name ='" + userNameToModify + "'";
					stmt.executeUpdate(sql);
				}

			} else {// modify user detail
				sql = "update " + MetaDataAccess.TABLE_USERS
						+ " set user_desc='" + userDesc + "' where user_name='"
						+ userNameToModify + "'";
				stmt.executeUpdate(sql);

				sql = "update " + MetaDataAccess.TABLE_USERS
						+ " set password='" + password + "' where user_name='"
						+ userNameToModify + "'";
				stmt.executeUpdate(sql);
			}

			// delete old and insert new user-role assignment
			sql = "delete from " + MetaDataAccess.TABLE_USER_ROLE
					+ " where user_name ='" + userNameToModify + "'";
			stmt.executeUpdate(sql);

			if (grantedRoles != null) {
				for (int i = 0; i < grantedRoles.length; i++) {
					sql = "insert into " + MetaDataAccess.TABLE_USER_ROLE
							+ " values('" + userNameToModify + "','"
							+ grantedRoles[i][0] + "')";
					stmt.executeUpdate(sql);
				}
			}

			// delete old and insert new user-permission assignment
			sql = "delete from " + MetaDataAccess.TABLE_USER_PERM
					+ " where user_name ='" + userNameToModify + "'";
			stmt.executeUpdate(sql);

			if (grantedPrivileges != null) {
				for (int i = 0; i < grantedPrivileges.length; i++) {
					sql = "insert into " + MetaDataAccess.TABLE_USER_PERM
							+ " values('" + userNameToModify + "','"
							+ grantedPrivileges[i][0] + "','"
							+ grantedPrivileges[i][1] + "','"
							+ grantedPrivileges[i][2] + "')";
					stmt.executeUpdate(sql);
				}
			}

		} catch (SQLException e1) {
			LogManager.LogException(
					"Exception caught while updating databases", e1);
		}

	}
}
