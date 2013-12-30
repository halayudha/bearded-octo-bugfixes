/**
 * Created on Apr 24, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.PasswordDialog;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.event.logical.shared.SelectionHandler;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;

import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.form.Label;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.gwtext.client.widgets.tree.event.TreePanelListener;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;
import com.gwtext.client.core.EventObject;
/**
 * @author Yueguo Chen
 *
 */
public class AccessControlUI extends BestPeerPanel implements Callback{

	public static interface I18nConstants extends Constants {
		String accessControlTitle();
		String accessControlUsers();
		String accessControlRoles();
		String accessControlUserManagement();
		String accessControlRoleManagement();
		String accessControlGeneralInfo();
		String accessControlRoleInfo();
		String accessControlSchemaPriv();
		String userName();
		String userDescription();
		String enterPassword();
		String ensurePassword();
		String updateOne();
		String createOne();	
		
		String roleList();
		String grantedRoleList();
		String grantRole();
		String degrantRole();
		
		String tableView();
		String columns();
		String grantedPriv();
		String addGrantedRow();
		String removeGrantedRow();	
		String privSchema();
		String privGrantedPriv();
		String privDescription();
		String privDatabase();
		String privTable();
		String privView();	
		String privField();	
		String privGranted();	
		String privColumnLevel();
		String privTableLevel();
		String roleName();
		String roleDescription();
	}

	private static final int SelectionHandler = 0;
	private ArrayList<String> globalTables = new ArrayList<String>();
	private HashMap<String, String[]> getColumns = new HashMap<String, String[]>();
	private TreePanel userRolePanel = new TreePanel();
	private TreePanel schemaTreePanel = new TreePanel();
	private Panel panel; //whole panel
	
	public AccessControlUI(){
		System.out.println("Start using access control.");
		createSchemaTree();
	}
	
	private static class MyPopup extends PopupPanel {
		public MyPopup(String message) {
			super(true);
			setWidget(new Label(message));
		}
	}
	
	@Override
	public Panel getViewPanel() {
		if(panel==null){
			panel = new Panel();
			final Panel generalwrapperPanel = new Panel(); //panel of general information
			final Panel operatedObjectPanel = new Panel(); //role or user
			final ListBox allRoleListBox = new ListBox();
			final ListBox allAssginedRoleListBox = new ListBox();
			final FlexTable columnTable = new FlexTable();//contains columns of a table in schema priv
			final FlexTable privTable = new FlexTable();//contains all priv info	
			
			//outer panels
			
			panel.setBorder(false);
			panel.setPaddings(15);
			panel.setLayout(new HorizontalLayout(2));	
			HorizontalSplitPanel hspanel = new HorizontalSplitPanel(); //a horizontal panel
			hspanel.setTitle("hspanel");
			hspanel.setSize("800px", "800px");
			hspanel.setSplitPosition("20%");
			
			//1.left tree control panel
			Panel treepanel = new Panel(); //left control panel
			treepanel.setBodyBorder(true);
			treepanel.setAutoScroll(true);
			treepanel.setHeight("400px");
			treepanel.setAutoDestroy(false);
			treepanel.setBorder(true);
			Tree tree = new Tree();
			tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
		        public void onSelection(SelectionEvent<TreeItem> event) {
		          TreeItem item = event.getSelectedItem();
		          TreeItem parent = item.getParentItem();
		          if (parent!=null && parent.getText().equals(BestPeerDesktop.constants.accessControlUsers())){
		        	  operatedObjectPanel.setHtml(BestPeerDesktop.constants.accessControlUserManagement());
		        	  onChooseUser(item.getText(), generalwrapperPanel);
		        	  
		          }
		          else if(parent!=null && parent.getText().equals(BestPeerDesktop.constants.accessControlRoles())){
		        	  operatedObjectPanel.setHtml(BestPeerDesktop.constants.accessControlRoleManagement());
		        	  onChooseRole(item.getText(), generalwrapperPanel);
		          }
		        }
		    });		
			TreeItem root = new TreeItem(BestPeerDesktop.constants.accessControlTitle());
			tree.addItem(root);
			TreeItem roleitem = new TreeItem(BestPeerDesktop.constants.accessControlRoles());
			TreeItem useritem = new TreeItem(BestPeerDesktop.constants.accessControlUsers());
			roleitem.addItem(new TreeItem("govern"));
			roleitem.addItem(new TreeItem("public"));
			roleitem.addItem(new TreeItem("saleA"));
			roleitem.addItem(new TreeItem("saleQ"));
			useritem.addItem(new TreeItem("u1"));
			useritem.addItem(new TreeItem("u2"));
			useritem.addItem(new TreeItem("u3"));    
			root.addItem(roleitem);
			root.addItem(useritem);	
			root.setState(true);
			treepanel.add(tree);
			// todo: read users and roles from database.
			
			//2 right access control tab panel
			//2.2-4 content
			TabPanel operationTabPanel = new TabPanel();
			operationTabPanel.setWidth("600px");
			operationTabPanel.setHeight("550px");
			operationTabPanel.setBodyBorder(false);
			operationTabPanel.setEnableTabScroll(true);
			operationTabPanel.setAutoScroll(true);
			operationTabPanel.setAutoDestroy(false);
			operationTabPanel.setActiveTab(0);	
				
				//2.2 
		    	generalwrapperPanel.setTitle(BestPeerDesktop.constants.accessControlGeneralInfo());	    	
		    	
		    	//2.3 role info panel
				Panel roleInfoWrapperPanel = new Panel();
				roleInfoWrapperPanel.setTitle(BestPeerDesktop.constants.accessControlRoleInfo());
				HorizontalPanel roleInfoPanel = new HorizontalPanel();
				roleInfoPanel.setSpacing(3);
					//left part
					FlexTable leftroleInfolayout = new FlexTable();
					leftroleInfolayout.setCellSpacing(2);
					allRoleListBox.setWidth("100px");
					allRoleListBox.setHeight("300px");
					allRoleListBox.addItem("govern");
					allRoleListBox.addItem("public");
					allRoleListBox.addItem("saleA");
					allRoleListBox.addItem("saleQ");
					allRoleListBox.setVisibleItemCount(20);
					leftroleInfolayout.setHTML(1, 0, BestPeerDesktop.constants.roleList());
					leftroleInfolayout.setWidget(2, 0, allRoleListBox);
					FlexCellFormatter lcellFormatter = leftroleInfolayout.getFlexCellFormatter();
					lcellFormatter.setColSpan(2, 0, 1);	 
					//central part
					FlexTable centerroleInfolayout = new FlexTable();
					centerroleInfolayout.setCellSpacing(8);
					centerroleInfolayout.setHTML(1, 0, " ");
					centerroleInfolayout.setHTML(2, 0, " ");
					centerroleInfolayout.setHTML(3, 0, " ");
					centerroleInfolayout.setHTML(4, 0, " ");	
					Button grantBT = new Button(BestPeerDesktop.constants.grantRole(), new ClickHandler(){
				        public void onClick(ClickEvent event) {
				        	int selectitem = allRoleListBox.getSelectedIndex();
				        	if(selectitem>=0){
				        		String rolename = allRoleListBox.getValue(selectitem);
				        		int total = allAssginedRoleListBox.getItemCount();
				        		boolean found=false;
				        		for (int i=0; i<total; i++){
				        			String thisname = allAssginedRoleListBox.getValue(i);
				        			if(allAssginedRoleListBox.getValue(i).equalsIgnoreCase(rolename)){
				        				found = true;
				        				break;
				        			}
				        		}
				        		if (!found){
				        			allAssginedRoleListBox.addItem(rolename);
				        		}
				    		}
				        }
				    });

					centerroleInfolayout.setWidget(3, 0, grantBT);
					Button degrantBT = new Button(BestPeerDesktop.constants.degrantRole(), new ClickHandler(){
				        public void onClick(ClickEvent event) {
					        	int selectitem = allAssginedRoleListBox.getSelectedIndex();
					        	if(selectitem>=0){
					        		allAssginedRoleListBox.removeItem(selectitem);
					    		}
				           	}
				         });
					centerroleInfolayout.setWidget(4, 0, degrantBT);
					FlexCellFormatter cellFormatter = centerroleInfolayout.getFlexCellFormatter();
				    cellFormatter.setColSpan(4, 0, 1);	    
				    //right part
				    FlexTable rightroleInfolayout = new FlexTable();
				    rightroleInfolayout.setCellSpacing(2);
				    allAssginedRoleListBox.setWidth("100px");
				    allAssginedRoleListBox.setHeight("300px");
				    allAssginedRoleListBox.addItem("public");	
				    allAssginedRoleListBox.setVisibleItemCount(20);
				    rightroleInfolayout.setHTML(1, 0, BestPeerDesktop.constants.grantedRoleList());
				    rightroleInfolayout.setWidget(2, 0, allAssginedRoleListBox);
				    FlexCellFormatter rcellFormatter = rightroleInfolayout.getFlexCellFormatter();
				    rcellFormatter.setColSpan(2, 0, 1);	 	    
				roleInfoPanel.add(leftroleInfolayout);
			    roleInfoPanel.add(centerroleInfolayout);
			    roleInfoPanel.add(rightroleInfolayout);
				roleInfoWrapperPanel.add(roleInfoPanel);    	
		    	
				//2.4
				Panel schemaPrivWrapperPanel = new Panel();
				schemaPrivWrapperPanel.setTitle(BestPeerDesktop.constants.accessControlSchemaPriv());
				VerticalPanel schemaPrivPanel = new VerticalPanel();
				schemaPrivPanel.setSpacing(3);
					//top part
					Panel topWrapperPanel = new Panel();
					topWrapperPanel.setPaddings(15);
					HorizontalPanel topPanel = new HorizontalPanel();
					topPanel.setSpacing(2);
					topWrapperPanel.setBodyBorder(true);
					//top.1
					final Tree schematree = new Tree();
					TreeItem schemaroot = new TreeItem(BestPeerDesktop.constants.privDatabase());
					schematree.addItem(schemaroot);
					TreeItem tableitem = new TreeItem(BestPeerDesktop.constants.privTable());
					TreeItem viewitem = new TreeItem(BestPeerDesktop.constants.privView());			
					tableitem.addItem(new TreeItem("products"));
					tableitem.addItem(new TreeItem("company"));
					tableitem.addItem(new TreeItem("sales"));				
					schemaroot.addItem(tableitem);
					schemaroot.addItem(viewitem);	
					schemaroot.setState(true);
					schematree.addSelectionHandler(new SelectionHandler<TreeItem>() {
				        public void onSelection(SelectionEvent<TreeItem> event) {
				          TreeItem item = event.getSelectedItem();
				          TreeItem parent = item.getParentItem();
				          if (parent!=null && parent.getText().equals(BestPeerDesktop.constants.privTable())){
				        	  onChooseTable(item.getText(), columnTable);			        	
				          }
				        }
				    });					
					//top.2
					columnTable.setBorderWidth(2);
					columnTable.setText(0, 0, BestPeerDesktop.constants.privGranted());
					columnTable.setText(0, 1, BestPeerDesktop.constants.privField());	
					topPanel.add(schemaTreePanel);
					schemaTreePanel.addListener(new TreePanelListenerAdapter(){
						@Override
						public void onClick(TreeNode node, EventObject e){
							System.out.println(node.getText());
						}
					});
					
					topPanel.add(columnTable);
					topWrapperPanel.add(topPanel);
				
					//central part
					FlexTable columnlayout = new FlexTable();
					columnlayout.setCellSpacing(2);	 
					Button addGrantBT = new Button(BestPeerDesktop.constants.addGrantedRow(), new ClickHandler(){
				        public void onClick(ClickEvent event) {
				        	onCreateSchemaPriv(schematree.getSelectedItem().getText(), columnTable, privTable);
			           	}
			        });
					columnlayout.setWidget(0, 1, addGrantBT);
					Button removeGrantBT = new Button(BestPeerDesktop.constants.removeGrantedRow(), new ClickHandler(){
				        public void onClick(ClickEvent event) {
				        	onRemoveSchemaPriv(privTable);
			           	}
			        });
					columnlayout.setWidget(0, 2, removeGrantBT);
					FlexCellFormatter clcellFormatter = columnlayout.getFlexCellFormatter();
					clcellFormatter.setRowSpan(0, 1, 2);
				
					//bottom part
					FlexTable privwrapperlayout = new FlexTable();
					privwrapperlayout.setCellSpacing(2);	
					privTable.setBorderWidth(2);
					privTable.setText(0, 0, BestPeerDesktop.constants.removeGrantedRow());
					privTable.setText(0, 1, BestPeerDesktop.constants.privSchema());
					privTable.setText(0, 2, BestPeerDesktop.constants.privGrantedPriv());
					privTable.setText(0, 3, BestPeerDesktop.constants.privDescription());		
					FlexCellFormatter pcellFormatter = privTable.getFlexCellFormatter();
				    pcellFormatter.setColSpan(6, 0, 4);	
				    privwrapperlayout.setHTML(0, 0, BestPeerDesktop.constants.grantedPriv());
				    privwrapperlayout.setWidget(1, 0, privTable);
				    FlexCellFormatter bcellFormatter = privwrapperlayout.getFlexCellFormatter();
				    bcellFormatter.setColSpan(6, 0, 1);
			    schemaPrivPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			    schemaPrivPanel.add(topWrapperPanel);
			    schemaPrivPanel.add(columnlayout);
			    schemaPrivPanel.add(privwrapperlayout);
			    schemaPrivWrapperPanel.add(schemaPrivPanel);		
				//end of 2.4
					
			operationTabPanel.add(generalwrapperPanel);	
		    operationTabPanel.add(roleInfoWrapperPanel);
		    operationTabPanel.add(schemaPrivWrapperPanel);			
			//end of 2
			
			hspanel.setLeftWidget(treepanel);
			hspanel.add(operationTabPanel);
			panel.add(hspanel);
		}		
		return panel;
	}

	void onChooseUser(String astring, Panel aPanel){	
		aPanel.removeAll();
		FlexTable generallayout = new FlexTable();
		generallayout.setCellSpacing(5);
		generallayout.setHTML(0, 0, BestPeerDesktop.constants.userName());
		TextBox userName = new TextBox();
		userName.setValue(astring);
		generallayout.setWidget(0, 1, userName);
		generallayout.setHTML(1, 0, BestPeerDesktop.constants.userDescription());
		TextBox userDescription = new TextBox();
	    //TODO set value for user userDescription
		generallayout.setWidget(1, 1, userDescription);
		generallayout.setHTML(2, 0, BestPeerDesktop.constants.enterPassword());
		final PasswordTextBox passwordInput1 = new PasswordTextBox();
		generallayout.setWidget(2, 1, passwordInput1);
		generallayout.setHTML(3, 0, BestPeerDesktop.constants.ensurePassword());
		final PasswordTextBox passwordInput2 = new PasswordTextBox();
		generallayout.setWidget(3, 1, passwordInput2);
		
		Button updateUserBT = new Button(BestPeerDesktop.constants.updateOne(), new ClickHandler(){
	        public void onClick(ClickEvent event) {
	        	if(!passwordInput1.getText().equals(passwordInput2.getText())){
	    			PasswordDialog dlg = new PasswordDialog();
	    			dlg.show();
	    			dlg.center();
	    		}
           	}
         });
		generallayout.setWidget(4, 0, updateUserBT);
		Button createUserBT = new Button(BestPeerDesktop.constants.createOne());
		generallayout.setWidget(4, 1, createUserBT);				
		
		FlexCellFormatter generalcellFormatter = generallayout.getFlexCellFormatter();
		generalcellFormatter.setColSpan(5, 0, 2);
		generalcellFormatter.setHorizontalAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER);
		aPanel.add(generallayout);	
		aPanel.doLayout();
	}
	
	void onChooseRole(String astring, Panel aPanel){	
		aPanel.removeAll();
		FlexTable generallayout = new FlexTable();
		generallayout.setCellSpacing(3);
		FlexCellFormatter cellFormatter = generallayout.getFlexCellFormatter();
		generallayout.setHTML(0, 0, BestPeerDesktop.constants.roleName());
	    TextBox roleName = new TextBox();
	    roleName.setValue(astring);
	    generallayout.setWidget(0, 1, roleName); 
	    generallayout.setHTML(1, 0, BestPeerDesktop.constants.roleDescription());
	    TextBox roleDescription = new TextBox();
	    //TODO set value for user userDescription
	    generallayout.setWidget(1, 1, roleDescription);
	    Button updateRoleBT = new Button(BestPeerDesktop.constants.updateOne());
	    generallayout.setWidget(2, 0, updateRoleBT);
	    Button creatRoleBT = new Button(BestPeerDesktop.constants.createOne());
	    generallayout.setWidget(2, 1, creatRoleBT);
	    
	    cellFormatter.setColSpan(3, 0, 2);
	    cellFormatter.setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
		aPanel.add(generallayout);	
		aPanel.doLayout();		
	}
	
	void onChooseTable(String astring, FlexTable aflexTable){
		
		//TODO hard code now
		aflexTable.clear();
		aflexTable.setBorderWidth(0);
		aflexTable.setBorderWidth(2);
		aflexTable.setText(0, 0, BestPeerDesktop.constants.privGranted());
		aflexTable.setText(0, 1, BestPeerDesktop.constants.privField());		
		if (astring.equals("products")){
			aflexTable.setWidget(1, 0, new CheckBox());
			aflexTable.setText(1, 1, "product_id");
			aflexTable.setWidget(2, 0, new CheckBox());
			aflexTable.setText(2, 1, "product_name");	
			aflexTable.setWidget(3, 0, new CheckBox());
			aflexTable.setText(3, 1, "product_desc");	
			aflexTable.setWidget(4, 0, new CheckBox());
			aflexTable.setText(4, 1, "company_id");				
		}
		else if(astring.equals("company")){
			aflexTable.removeRow(4);
			aflexTable.setWidget(1, 0, new CheckBox());
			aflexTable.setText(1, 1, "company_id");
			aflexTable.setWidget(2, 0, new CheckBox());
			aflexTable.setText(2, 1, "company_name");	
			aflexTable.setWidget(3, 0, new CheckBox());
			aflexTable.setText(3, 1, "company_desc");	
		}		
		else if(astring.equals("sales")){
			aflexTable.setWidget(1, 0, new CheckBox());
			aflexTable.setText(1, 1, "product_id");
			aflexTable.setWidget(2, 0, new CheckBox());
			aflexTable.setText(2, 1, "quantity_sale");	
			aflexTable.setWidget(3, 0, new CheckBox());
			aflexTable.setText(3, 1, "benefit");	
			aflexTable.setWidget(4, 0, new CheckBox());
			aflexTable.setText(4, 1, "date_sale");				
		}
	}
	
	void onCreateSchemaPriv(String tablename, FlexTable columnTable, FlexTable privTable){
		int numofrows=columnTable.getRowCount();
		boolean found = false;
		for (int i=1; i<numofrows; i++){
			if (((CheckBox)columnTable.getWidget(i, 0)).getValue()){
				int numofrowsinPriv=privTable.getRowCount();
				privTable.setWidget(numofrowsinPriv, 0, new CheckBox());
				privTable.setText(numofrowsinPriv, 1, "SELECT");
				privTable.setText(numofrowsinPriv, 2, tablename+"."+columnTable.getText(i, 1));
				privTable.setText(numofrowsinPriv, 3, BestPeerDesktop.constants.privColumnLevel());
				found = true;
			}
		}
		if (!found){
			int numofrowsinPriv=privTable.getRowCount();
			privTable.setWidget(numofrowsinPriv, 0, new CheckBox());
			privTable.setText(numofrowsinPriv, 1, "SELECT");
			privTable.setText(numofrowsinPriv, 2, tablename);
			privTable.setText(numofrowsinPriv, 3, BestPeerDesktop.constants.privTableLevel());
		}
	}
	
	void onRemoveSchemaPriv(FlexTable privTable){
		int numofrows=privTable.getRowCount();
		for (int i=numofrows-1; i>=1; i--){
			if (((CheckBox)privTable.getWidget(i, 0)).getValue()){
				privTable.removeRow(i);
			}
		}		
	}
	public void onFailure(){
		
	}
	
	public void onReady(JSONObject resultSet){
		
		if(resultSet.containsKey("schema")){
			globalTables = new ArrayList<String>();
			getColumns = new HashMap<String, String[]>();
			JSONArray tnamelist = resultSet.get("table_name").isArray();
			for(int i=0; i<tnamelist.size(); i++){
				String currentTable = ((JSONString)tnamelist.get(i)).stringValue();
				TreeNode currentTableNode = new TreeNode(currentTable);
				schemaTreePanel.getRootNode().getChildNodes()[0].appendChild(currentTableNode);
				globalTables.add(currentTable);
				JSONArray namemap = resultSet.get("n_"+currentTable).isArray();
				String[] currentColumns = new String[namemap.size()];
				for(int j=0; j<namemap.size(); j++){
					String columeName = namemap.get(j).toString();//((JSONString)namemap.get(j)).stringValue();
					currentColumns[j] = columeName.substring(1, columeName.length()-1);	
				}
				getColumns.put(currentTable, currentColumns);
			}
		}
	}
	
	public void loadSchema(String schemaName, Callback callback) {
		JSONObject params = new JSONObject();
		params.put("schema", new JSONString(schemaName));
		RPCCall.get().invoke("SchemaService", params, callback);		
	}
	
	public void loadUserInfo(){
		
	}
	public void loadRoleInfo(){
		
	}
	
	public void createSchemaTree(){		
		TreeNode root = new TreeNode("DataBase");		
		schemaTreePanel.setRootNode(root);
		schemaTreePanel.setRootVisible(true);
		schemaTreePanel.setSize(200, 300);
		TreeNode table = new TreeNode("tables");
		root.appendChild(table);
		TreeNode view = new TreeNode("view");
		root.appendChild(view);
		table.setExpanded(true);
		view.setExpanded(true);
		root.setExpanded(true);	
		loadSchema("global",this);
	}
}
