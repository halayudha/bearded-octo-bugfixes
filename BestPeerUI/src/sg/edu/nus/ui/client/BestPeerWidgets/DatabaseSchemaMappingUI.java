/**
 * Created on Apr 27, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.google.gwt.user.client.ui.ListBox;
import com.gwtext.client.widgets.form.FieldSet;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

//import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.Callback;
import java.sql.Connection;
import java.sql.DatabaseMetaData;


/**
 * @author David Jiang
 * @added by Wang Jinbao
 */
public class DatabaseSchemaMappingUI extends BestPeerPanel {
	public static interface I18nConstants extends Constants {
		String mapping();
		String tableMapping();
		String attributeMapping();
		String undoMapping();
		String localSchema();
		String globalSchema();
		String mappingResults();
		String missError();
		String missLocalSchema();
		String missGlobalSchema();
		String loadingSchemaText();
		String filter();
		String unfilter();
		String confirmFilter();
		String tableToFilter();
		String tableAfterFilter();
	}
	private static int schemaLoaded = 0;
	public class AsyncSchemaLoader implements Callback {
		private SchemaTree tree = null;
		public AsyncSchemaLoader(SchemaTree tree) {
			this.tree = tree;
		}
		@Override
		public void onFailure() {
			
		}

		@Override
		public void onReady(JSONObject resultSet) {
			
			String schemaType = ((JSONString)resultSet.get("schema")).stringValue();
			//build local db tree
			if(schemaType.equals("local")){				
				JSONArray tnamelist = resultSet.get("table_name").isArray();
				localTables = new ArrayList<String>();
				getColumns = new HashMap<String, String[]>();
				if(tnamelist!=null){
					for(int i=0; i<tnamelist.size(); i++){
						String currentTable = ((JSONString)tnamelist.get(i)).stringValue();
						localTables.add(currentTable);
						TreeNode currentTableNode = new TreeNode(currentTable);
						localSchemaTree.getRootNode().appendChild(currentTableNode);
						JSONArray namemap = resultSet.get("n_"+currentTable).isArray();
						JSONArray typemap = resultSet.get("t_"+currentTable).isArray();
						String[] localTableColumns = new String[namemap.size()];
						if(namemap!=null&&typemap!=null){
							for(int j=0; j<namemap.size(); j++){
								String columeName = namemap.get(j).toString();//((JSONString)namemap.get(j)).stringValue();
								String columeType = typemap.get(j).toString();//((JSONString)typemap.get(j)).stringValue();
								TreeNode currentColumeNode = new TreeNode(columeName.substring(1, columeName.length()-1)+" ("+columeType.substring(1, columeType.length()-1)+")");
								currentTableNode.appendChild(currentColumeNode);
								localTableColumns[j] = columeName.substring(1, columeName.length()-1);
							}
						}
						localSchemaTree.getRootNode().setExpanded(true);
						currentTableNode.setExpanded(true);
						getColumns.put(currentTable, localTableColumns);
					}
				}
			}
			//build global db tree
			else if(schemaType.equals("global")){
				JSONArray tnamelist = resultSet.get("table_name").isArray();
				if(tnamelist!=null){
					for(int i=0; i<tnamelist.size(); i++){
						String currentTable = ((JSONString)tnamelist.get(i)).stringValue();
						TreeNode currentTableNode = new TreeNode(currentTable);
						globalSchemaTree.getRootNode().appendChild(currentTableNode);
						JSONArray namemap = resultSet.get("n_"+currentTable).isArray();
						JSONArray typemap = resultSet.get("t_"+currentTable).isArray();
						if(namemap!=null&&typemap!=null){
							for(int j=0; j<namemap.size(); j++){
								String columeName = namemap.get(j).toString();//((JSONString)namemap.get(j)).stringValue();
								String columeType = typemap.get(j).toString();//((JSONString)typemap.get(j)).stringValue();
								TreeNode currentColumeNode = new TreeNode(columeName.substring(1, columeName.length()-1)+" ("+columeType.substring(1, columeType.length()-1)+")");
								currentTableNode.appendChild(currentColumeNode);
							}
						}
					}
					globalSchemaTree.getRootNode().setExpanded(true);
				}
			}
			
			if(++schemaLoaded == 2)
				WaitingMessageBox.hide();
			
		}
	}
	
	private Panel viewPanel = null;
	private Toolbar toolbar;
	private SchemaTree localSchemaTree = null;
	private SchemaTree globalSchemaTree = null;
	private SchemaTree resultTree = null;
	private com.gwtext.client.widgets.Window window = null;
	private ListBox box1 = null;
	private ListBox box2 =null;
	private ArrayList<String> localTables;
	private HashMap<String, String[]> getColumns;
	
	public DatabaseSchemaMappingUI() {
		setTopToolbar(new Toolbar());
	}
	
	final private SchemaTree createSchemaTree() {
		SchemaTree tree = new SchemaTree();
		tree.setWidth(300);
		tree.setHeight(485);
		tree.setUseArrows(true);
		return tree;
	}
	
	private Panel createLocalSchemaPanel() {
		localSchemaTree = createSchemaTree();
		TreeNode root = new TreeNode("Local DB Schema");
		localSchemaTree.setRootNode(root);
		localSchemaTree.setRootVisible(true);		
		root.setExpanded(true);		
		localSchemaTree.setTitle(BestPeerDesktop.constants.localSchema());
		localSchemaTree.loadSchema("local", new AsyncSchemaLoader(localSchemaTree));
		return localSchemaTree;
	}
	
	
	private Panel createResultsPanel() {
		resultTree = createSchemaTree();
		resultTree.setTitle(BestPeerDesktop.constants.mappingResults());
		
		TreeNode root = new TreeNode("root");
		root.setExpanded(true);
		resultTree.setRootNode(root);
		resultTree.setRootVisible(false);
		return resultTree;
	}
	
	private Panel createGlobalSchemaPanel() {
		globalSchemaTree = createSchemaTree();
		TreeNode root = new TreeNode("Global DB Schema");
		globalSchemaTree.setRootNode(root);
		globalSchemaTree.setRootVisible(true);		
		root.setExpanded(true);
		
		globalSchemaTree.setTitle(BestPeerDesktop.constants.globalSchema());
		globalSchemaTree.loadSchema("global", new AsyncSchemaLoader(globalSchemaTree));
		return globalSchemaTree;
	}
	
	private HashMap<String, String> schemaMap = new HashMap<String, String>();
	private void addMappingNode(TreeNode localNode, TreeNode globalNode) {
		TreeNode tableNode1 = (TreeNode)localNode.getParentNode();
		TreeNode tableNode2 = (TreeNode)globalNode.getParentNode();
		if(localSchemaTree.isRootNode(tableNode1) || globalSchemaTree.isRootNode(tableNode2)){
			Window.alert("Please select attribute");
			return;
		}
		// Add mapping node to the result tree
		String tableId = schemaMap.get(tableNode1.getId()+tableNode2.getId());
		if(tableId == null){
			TreeNode mapTblNode = new TreeNode("local."+tableNode1.getText()+" -> global."+tableNode2.getText());
			mapTblNode.setExpanded(true);
			schemaMap.put(tableNode1.getId()+tableNode2.getId(), mapTblNode.getId());
			resultTree.getRootNode().appendChild(mapTblNode); // Add table node
		}
		
		TreeNode parentNode = resultTree.getNodeById(schemaMap.get(tableNode1.getId()+tableNode2.getId()));
		parentNode.appendChild(new TreeNode(localNode.getText() + " -> " + globalNode.getText()));
	}
	
	public class MyButtonAdapter extends ButtonListenerAdapter {

		public MyButtonAdapter() {
			super();
		}
		public void onClick(Button button, EventObject e){
			if(button.getText().equals(BestPeerDesktop.constants.confirmFilter())){
				int num = localSchemaTree.getRootNode().getChildNodes().length;
				for(int i=0; i<num; i++){
					localSchemaTree.getRootNode().removeChild(localSchemaTree.getRootNode().getChildNodes()[0]);					
				}
				for(int i=0; i<box2.getItemCount(); i++){
					String currentTable = box2.getItemText(i);
					TreeNode currentTableNode = new TreeNode(currentTable);	
					localSchemaTree.getRootNode().appendChild(currentTableNode);
					String[] localTableColumns = getColumns.get(currentTable);
					for(int j=0; j<localTableColumns.length; j++){
						TreeNode currentColumeNode = new TreeNode(localTableColumns[j]);
						currentTableNode.appendChild(currentColumeNode);
					}
					localSchemaTree.getRootNode().setExpanded(true);
					currentTableNode.setExpanded(true);
				}				
				window.close();
			}
			else if(button.getText().equals(BestPeerDesktop.constants.unfilter())){
				int num = localSchemaTree.getRootNode().getChildNodes().length;
				for(int i=0; i<num; i++){
					localSchemaTree.getRootNode().removeChild(localSchemaTree.getRootNode().getChildNodes()[0]);					
				}
				for(int i=0; i<localTables.size(); i++){
					String currentTable = localTables.get(i);
					TreeNode currentTableNode = new TreeNode(currentTable);	
					localSchemaTree.getRootNode().appendChild(currentTableNode);
					String[] localTableColumns = getColumns.get(currentTable);
					for(int j=0; j<localTableColumns.length; j++){
						TreeNode currentColumeNode = new TreeNode(localTableColumns[j]);
						currentTableNode.appendChild(currentColumeNode);
					}
					localSchemaTree.getRootNode().setExpanded(true);
					currentTableNode.setExpanded(true);
				}
			}
			else if(button.getText().equals(" >>> ")){
				int idx = box1.getSelectedIndex();
				String tname = box1.getItemText(idx);
				boolean addin = true;
				for(int i=0; i<box2.getItemCount(); i++){
					if(tname.equals(box2.getItemText(i))){
						addin = false;
						break;
					}
				}
				if(addin){
					box2.addItem(tname);
				}
			}
			else if(button.getText().indexOf("<<<")!=-1){
				int idx = box2.getSelectedIndex();
				box2.removeItem(idx);
			}
		}
	}
	
	@Override
	protected void afterRender() {
		// First render myself
		toolbar = getTopToolbar();
		ToolbarButton filterButton = new ToolbarButton(BestPeerDesktop.constants.filter());
		filterButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				window = new com.gwtext.client.widgets.Window();
				window.setTopToolbar(new Toolbar());
				window.setSize(550, 300);
				window.setLayout(new HorizontalLayout(30));
			    ToolbarButton trb = new ToolbarButton(BestPeerDesktop.constants.confirmFilter());
			    trb.addListener(new MyButtonAdapter());
			    window.getTopToolbar().addButton(trb);
				FieldSet fs1 = new FieldSet();
				fs1.setCollapsible(true);
				fs1.setAutoHeight(true);
				fs1.setTitle(BestPeerDesktop.constants.tableToFilter());
				FieldSet fs2 = new FieldSet();
				fs2.setCollapsible(true);
				fs2.setAutoHeight(true);
				fs2.setTitle(BestPeerDesktop.constants.tableAfterFilter());
				box1 = new ListBox();				
				box1.setWidth("200px");
				box1.setHeight("150px");
				box1.setVisibleItemCount(10);	
				for(int i=0; i<localTables.size(); i++){
					box1.addItem(localTables.get(i));
				}
				box2 = new ListBox();
				box2.setWidth("200px");
				box2.setHeight("150px");
				box2.setVisibleItemCount(10);
				Button button1 = new Button(" >>> ");
				button1.addListener(new MyButtonAdapter());
				Button button2 = new Button(" <<< ");
				button2.addListener(new MyButtonAdapter());
				fs1.add(box1);
				fs1.add(button1);
				fs2.add(box2);
				fs2.add(button2);
				window.add(fs1);
				window.add(fs2);
				window.show();
				
			}
		});
		toolbar.addButton(filterButton);		
		ToolbarButton unFilterButton = new ToolbarButton(BestPeerDesktop.constants.unfilter());
		unFilterButton.addListener(new MyButtonAdapter());
		toolbar.addButton(unFilterButton);
		ToolbarButton mappingButton = new ToolbarButton(BestPeerDesktop.constants.mapping());
		mappingButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {				
				TreeNode localNode = localSchemaTree.getSelectedNode();
				TreeNode globalNode = globalSchemaTree.getSelectedNode();
				if(localNode == null) {
					MessageBox.show(new MessageBoxConfig() {
						{
							setTitle(BestPeerDesktop.constants.missError());
							setMsg(BestPeerDesktop.constants.missLocalSchema());
							setButtons(MessageBox.OK);
							setIconCls(MessageBox.ERROR);
						}
					});
					return;
				}
				if(globalNode == null) {
					MessageBox.show(new MessageBoxConfig() {
						{
							setTitle(BestPeerDesktop.constants.missError());
							setMsg(BestPeerDesktop.constants.missGlobalSchema());
							setButtons(MessageBox.OK);
							setIconCls(MessageBox.ERROR);
						}
					});
					return;
				}
				addMappingNode(localNode, globalNode);
	    }
		});
		toolbar.addButton(mappingButton);
		//toolbar.addSeparator();
		ToolbarButton undoMappingButton = new ToolbarButton(BestPeerDesktop.constants.undoMapping());
		undoMappingButton.addListener(new ButtonListenerAdapter() {
			@Override
			public void onClick(Button button, EventObject e) {
				TreeNode node = resultTree.getSelectedNode();
				if(node == null)
					return;
				TreeNode parentNode = (TreeNode)node.getParentNode();
				node.getParentNode().removeChild(node);
				if(parentNode.getChildNodes() == null || parentNode.getChildNodes().length == 0){
					for(Map.Entry<String, String> mapEntry : schemaMap.entrySet()) {
						if(mapEntry.getValue().equalsIgnoreCase(parentNode.getId()))
							schemaMap.remove(mapEntry.getKey());
					}
					resultTree.getRootNode().removeChild(parentNode);
				}
				
			}
		});
		toolbar.addButton(undoMappingButton);
		
		// Let parent do the rest rendering work
		super.afterRender();
	}


	@Override
	public Panel getViewPanel() {
		if(viewPanel == null){
			viewPanel = new Panel();
			viewPanel.setLayout(new HorizontalLayout(10));
			viewPanel.add(createLocalSchemaPanel());
			viewPanel.add(createGlobalSchemaPanel());
			viewPanel.add(createResultsPanel());
			
		}
		return viewPanel;
	}
	

}
