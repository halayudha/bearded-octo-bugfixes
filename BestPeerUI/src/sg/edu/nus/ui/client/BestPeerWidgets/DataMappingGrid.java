package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreePanelListener;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.UserCustomCellEditor;
import com.gwtextux.client.data.PagingMemoryProxy;

import com.gwtext.client.data.*; 
import com.gwtext.client.widgets.grid.CellMetadata;

/**
 * 
 * @author Wang Jinbao
 * 
 */

public class DataMappingGrid extends BestPeerPanel implements ClickHandler,
Callback {
	
	public static interface I18nConstants extends Constants {
		String dataMapping();
		String createDataMapping();
		String deleteDataMapping();
		String viewDataMapping();
		String existingDataMapping();
		String localTerms();
		String globalTerms();
	}
	
	private Panel panel;
	private ArrayList<String> localTerm = new ArrayList<String>();
	private static ArrayList<String> globalTerm = new ArrayList<String>();
	private static HashMap<String, double[]> confidence;
	private static EditorGridPanel tableGrid;
	private SimpleStore cbStore;
	private Store store = null;
	private Store attrStore;	
	private SchemaTree schemaTree;
	private static TreeNode root;
	private String tableName;
	private String colName;
	private int localSize;
	private int globalSize;
	private int mappingSize;
	private ArrayList<Checkbox> checkBoxs = new ArrayList<Checkbox>();
	private CheckboxSelectionModel cbModel;
	private int[] state1;
	private int[] state2;
	
	
	public DataMappingGrid(){		
	}
	
	public ComboBox createComboBox(ArrayList<String> term){
		Object[][] data;
		data = new Object[1000][];
		data[0] = new Object[]{"UNMAPPED"};
		for(int j=0; j<term.size(); j++){			
			data[j+1] = new Object[]{term.get(j)};	
		}
		Store store = new SimpleStore(new String[]{"name"}, data);
		
		store.load();	
		ComboBox termList = new ComboBox();
		termList.setMinChars(1);
		termList.setStore(store);
		termList.setDisplayField("name");
		termList.setMode(ComboBox.LOCAL);
		termList.setWidth(120);
		termList.setHideLabel(true);
		termList.setEditable(false);
		return termList;		
	}
	
	public Checkbox createCheckBox(){
		Checkbox box = new Checkbox();
		checkBoxs.add(box);
		return box;
	}

	@Override
	public Panel getViewPanel() {
		if(panel == null){
			panel = new Panel();
			panel.setLayout(new HorizontalLayout(10));
			createTableGrid();	
			panel.add(createSchemaPanel());
			panel.add(tableGrid);
		}		
		return panel;		
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source instanceof ToolbarButton) {
			if(((Button)source).getText().equals("Delete Data Mapping")){
				schemaTree = null;
			}
		}
	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReady(JSONObject resultSet) {
		if(resultSet.containsKey("schema")){
			String schemaType = ((JSONString)resultSet.get("schema")).stringValue();
			if(schemaType.equals("global")){
				JSONArray tnamelist = resultSet.get("table_name").isArray();
				JSONArray maptable = resultSet.get("maptable").isArray();
				JSONArray mapcolumn = resultSet.get("mapcolumn").isArray();
				if(tnamelist!=null){
					for(int i=0; i<tnamelist.size(); i++){
						boolean addt = false;
						String currentTable = ((JSONString)tnamelist.get(i)).stringValue();
						for(int k=0; k<maptable.size(); k++){
							String target = ((JSONString)maptable.get(k)).stringValue();
							if(currentTable.equals(target)){
								addt = true;
								break;
							}
						}
						if(addt){
							TreeNode currentTableNode = new TreeNode(currentTable);
							currentTableNode.setIconCls("table-icon");
							schemaTree.getRootNode().appendChild(currentTableNode);
							JSONArray namemap = resultSet.get("n_"+currentTable).isArray();
							JSONArray typemap = resultSet.get("t_"+currentTable).isArray();
							if(namemap!=null&&typemap!=null){
								for(int j=0; j<namemap.size(); j++){
									boolean addc = false;									
									String columeName = namemap.get(j).toString();
									for(int z=0; z<mapcolumn.size(); z++){
										String targetc = ((JSONString)mapcolumn.get(z)).stringValue();
										if(targetc.equals(columeName.substring(1, columeName.length()-1))){
											addc = true;
											break;
										}
									}
									if(addc){
										TreeNode currentColumeNode = new TreeNode(columeName.substring(1, columeName.length()-1));
										currentColumeNode.setIconCls("column-icon");
										currentTableNode.appendChild(currentColumeNode);
									}									
								}
							}
						}						
					}
					schemaTree.getRootNode().setExpanded(true);					
				}
		    }
	    }
		else if(resultSet.containsKey("mapping")){	  
	    	globalTerm = new ArrayList<String>();
	    	JSONArray ja = resultSet.get("globalTerm").isArray();
	    	globalTerm = new ArrayList<String>();
	    	globalSize = (int) ((JSONNumber) resultSet
					.get("gsize")).doubleValue();
	    	for(int i=0; i<globalSize; i++){
	    		String current = ja.get(i).toString();
	    		current = (String) current.substring(1, current.length()-1);
	    		globalTerm.add(current);
	    	}
	    	localTerm = new ArrayList<String>();
	    	ja = resultSet.get("localTerm").isArray();
	    	localTerm = new ArrayList<String>();
	    	localSize = (int) ((JSONNumber) resultSet
					.get("lsize")).doubleValue();
	    	for(int i=0; i<localSize; i++){
	    		String current = ja.get(i).toString();
	    		current = current.substring(1, current.length()-1);
	    		localTerm.add(current);
	    	}
	    	//get confidence values.
	    	confidence = new HashMap<String, double[]>();
	    	for(int i=0; i<localTerm.size(); i++){
	    		JSONArray confidenceJArray = resultSet.get(localTerm.get(i)+"Confidence").isArray();
	    		double[] conf = new double[globalTerm.size()];
	    		for(int j=0; j<globalTerm.size(); j++){
	    			conf[j] = confidenceJArray.get(j).isNumber().doubleValue();
	    		}
	    		confidence.put(localTerm.get(i), conf);
	    	}
	    	mappingSize = (int) ((JSONNumber) resultSet
					.get("msize")).doubleValue();
	    	int gridsize = localSize;
	    	if(gridsize<mappingSize){
	    		gridsize = mappingSize;
	    	}
	    	Object[][] data = new Object[gridsize][2];
	    	ja = resultSet.get("mapping").isArray();
	    	for(int i=0; i<mappingSize; i++){
	    		JSONObject jo = (JSONObject) ja.get(i);
	    		String lterm = jo.get("localTerm").toString();
	    		lterm = lterm.substring(1, lterm.length()-1);
	    		data[i][0] = lterm;
	    		String gterm = jo.get("globalTerm").toString();
	    		gterm = gterm.substring(1, gterm.length()-1);
	    		data[i][1] = gterm;
	    	}
	    	int current = mappingSize;
	    	for(int i=0; i<localSize; i++){
	    		boolean add = true;
	    		for(int j=0; j<mappingSize; j++){
	    			if(localTerm.get(i).equals(data[j][0])){
	    				add = false;
	    			}
	    		}
	    		if(add == true){
	    			data[current][0] = localTerm.get(i);
	    			double[] s = confidence.get(localTerm.get(i));
	    			int index = 0;
	    			for(int j=1; j<s.length; j++){
	    				if(s[index] < s[j]){
	    					index = j;
	    				}
	    			}
	    			data[current][1] = globalTerm.get(index) + "  [recommanded]";
	    			
	    			current ++;
	    		}
	    	}
	    	//display the mapping pairs.
	    	RecordDef recordDef = null;
			FieldDef[] fieldDef = new FieldDef[2];
			fieldDef[0] = new StringFieldDef("Local Term");
			fieldDef[1] = new StringFieldDef("Global Term");
			recordDef = new RecordDef(fieldDef);
			PagingMemoryProxy proxy = new PagingMemoryProxy(data);  
			ArrayReader reader = new ArrayReader(recordDef);
			store = new Store(proxy, reader, true);
			BaseColumnConfig localColumn = 
				new ColumnConfig("Local Term", "Local Term", 220);					
			BaseColumnConfig globalColumn = 
				new ColumnConfig("Global Term", "Global Term", 270,  
						true, null, "auto_id");
			((ColumnConfig) globalColumn).setEditor(new GridEditor
					(this.createComboBox(globalTerm)));	
			BaseColumnConfig[] columnConfigs = {
					localColumn,
					globalColumn
			};	
			ColumnModel columnModel = new ColumnModel(columnConfigs);
			columnModel.setUserCustomCellEditor(new cbBox());
			final PagingToolbar pagingToolbar = new PagingToolbar(store);
			pagingToolbar.setPageSize(10);
			pagingToolbar.setDisplayInfo(true);
			pagingToolbar.setDisplayMsg("Displaying EMR records {0} - {1} of {2}");
			pagingToolbar.setEmptyMsg("No records to display");
			tableGrid.reconfigure(store, columnModel);
			store.load();
			//added
			Record[] records = store.getRecords();
			int recordNumber = records.length;
			state1 = new int[recordNumber];
			for(int i=0; i<recordNumber; i++){
				if(records[i].getAsString("Global Term")==null){
					state1[i] = 0;
				}else{
					state1[i] = 1;
				}
			}
	    }else if(resultSet.containsKey("insert")){
	    	loadMapping();
	    }else if(resultSet.containsKey("delete")){
	    	loadMapping();
	    }
	}
	
	private SchemaTree createSchemaTree() {
		SchemaTree tree = new SchemaTree();
		tree.addListener(new TreePanelListenerAdapter(){
			@Override
			public void onClick(TreeNode node, EventObject e) {
				super.onClick(node, e);
				loadMapping();
			}
		});
		tree.setWidth(250);
		tree.setHeight(420);
		tree.setUseArrows(true);
		return tree;
	}
	
	private Panel createSchemaPanel() {
		schemaTree = createSchemaTree();
		schemaTree.setTopToolbar(new Toolbar());		
		root = new TreeNode("Health Care Database");
		root.setIconCls("new-query-icon");
		schemaTree.setRootNode(root);
		schemaTree.setRootVisible(true);
		schemaTree.setAutoScroll(true);
		root.setExpanded(true);			
		schemaTree.setTitle("Select Column");
		JSONObject params = new JSONObject();
		params.put("schema", new JSONString("global"));
		RPCCall.get().invoke("SchemaService", params, this);
		return schemaTree;
	}
	
	private void createTableGrid(){
		attrStore = new SimpleStore(new String[]{"attr_name"}, 
				new Object[][]{new Object[]{""}});	
		BaseColumnConfig localColumn = 
			new ColumnConfig("Local Term", "local", 220);
		
		BaseColumnConfig globalColumn = 
			new ColumnConfig("Global Term", "global", 270,  
					true, null, "auto_id");
		((ColumnConfig) globalColumn).setEditor(new GridEditor
				(this.createComboBox(globalTerm)));					
		BaseColumnConfig[] columnConfigs = {
				localColumn,
				globalColumn
		};		
		ColumnModel columnModel = new ColumnModel(columnConfigs);		
		tableGrid = new EditorGridPanel();	
		tableGrid.setTopToolbar(new Toolbar());		
		tableGrid.setColumnModel(columnModel);
		cbStore = 
			new SimpleStore(new String[] {"local", "global", ""}, 
					new Object[][] {});
		cbStore.load();
		tableGrid.setStore(cbStore);
		tableGrid.setWidth(500);
		tableGrid.setHeight(420);
		tableGrid.setTitle("Data Mapping");
		tableGrid.setAutoExpandColumn("auto_id");
		tableGrid.setFrame(true);
		tableGrid.setClicksToEdit(1);
		ToolbarButton createBtn = new ToolbarButton("Save");
		createBtn.setIconCls("save-icon");
		createBtn.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				
				//added
				Record[] records = store.getRecords();
				int recordNumber = records.length;
				state2 = new int[recordNumber];
				for(int i=0; i<recordNumber; i++){
					if(records[i].getAsString("Global Term")==null||records[i].getAsString("Global Term").equals("UNMAPPED")){
						state2[i] = 0;
					}else{
						state2[i] = 1;
					}
				}
				JSONObject params = new JSONObject();
				params.put("insertMapping", new JSONString(""));
				params.put("table", new JSONString(tableName));
				params.put("column", new JSONString(colName));
				params.put("username", new JSONString(LoginUI.getUserName()));
				
				JSONArray insertT = new JSONArray();
				JSONArray deleteT = new JSONArray();
				JSONArray updateT = new JSONArray();
				int insertNumber = 0;
				int deleteNumber = 0;
				int updateNumber = 0;
				for(int i=0; i<recordNumber; i++){
					if(state1[i]==0&&state2[i]==1){
						//insert
						JSONObject jo = new JSONObject();
						jo.put("local", new JSONString(records[i].getAsString("Local Term")));
						String gg = records[i].getAsString("Global Term");
						if(gg.indexOf("[")!=-1){
							jo.put("global", new JSONString(gg.substring(0, gg.indexOf("["))));
						}else{
							jo.put("global", new JSONString(gg));
						}
						
						insertT.set(insertNumber, jo);
						insertNumber ++;
					}else if(state1[i]==1&&state2[i]==0){
						//delete
						JSONObject jo = new JSONObject();
						jo.put("local", new JSONString(records[i].getAsString("Local Term")));
						deleteT.set(deleteNumber, jo);
						deleteNumber ++;
					}else if(state1[i]==1&&state2[i]==1){
						//update
						JSONObject jo = new JSONObject();
						jo.put("local", new JSONString(records[i].getAsString("Local Term")));
						String gg = records[i].getAsString("Global Term");
						if(gg.indexOf("[")!=-1){
							jo.put("global", new JSONString(gg.substring(0, gg.indexOf("[")).trim()));
						}else{
							jo.put("global", new JSONString(gg));
						}
						updateT.set(updateNumber, jo);
						updateNumber ++;
					}
				}
				for(int i=0; i<state1.length; i++){
					state1[i] = state2[i];
				}
				params.put("toInsert", insertT);
				params.put("toDelete", deleteT);
				params.put("toUpdate", updateT);
				new RPCCall().invoke("DataMappingService", params, DataMappingGrid.this);

				
			}
		});			
		tableGrid.getTopToolbar().addButton(createBtn);
		
		ToolbarButton recommandBtn = new ToolbarButton("Recommand Mapping");
		recommandBtn.setIconCls("delete-icon");
		createBtn.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				//added
//				System.out.println("give suggestions.");
//				Record record = 
//				tableGrid.getStore().insert(0, record);
			}
		});	
	}	
	
	private void loadMapping(){		
		if(schemaTree.getSelectedNode().getChildNodes().length==0){
			
			localTerm = new ArrayList<String>();
			globalTerm = new ArrayList<String>();
			colName = schemaTree.getSelectedNode().getText();
			tableName = ((TreeNode)(schemaTree.getSelectedNode().
					getParentNode())).getText();
			String username = LoginUI.getUserName();
			
			JSONObject params = new JSONObject();
			params.put("loadMapping", new JSONString(""));
			params.put("table", new JSONString(tableName));
			params.put("column", new JSONString(colName));
			params.put("username", new JSONString(username));
			new RPCCall().invoke("DataMappingService", params, DataMappingGrid.this);
			
		}
	}
	
	public static ArrayList<String> getGlobalTerm(){
		return globalTerm;
	}
	public static String getLocalTermAt(int index){
		String term = tableGrid.getStore().getRecordAt(index).getAsString("Local Term");
		return term;
	}
	public static String getGlobalTermAt(int index){
		String term = tableGrid.getStore().getRecordAt(index).getAsString("Global Term");
		return term;
	}
	public static HashMap<String, double[]> getConfidence(){
		return confidence;
	}
}

class cbBox extends UserCustomCellEditor{

    public GridEditor getCellEditor(int colIndex, int rowIndex) {
    	if( colIndex == 1 ){
    		ArrayList<String> globalTerm = DataMappingGrid.getGlobalTerm();    	  
        	  String[] cbContent = new String[globalTerm.size()+1];
        	  cbContent[0] = "UNMAPPED";
        	  String l = DataMappingGrid.getLocalTermAt(rowIndex);
        	  double[] score = DataMappingGrid.getConfidence().get(l);
        	  double[] tempScore = new double[globalTerm.size()];
        	  for(int i=0; i<globalTerm.size(); i++){          		  
        		  score[i] = score[i]*95/100 + 4*Math.random()/100;
        		  cbContent[i+1] = globalTerm.get(i)+"  ["+(int)(score[i]*100)+"%]";
        		  tempScore[i] = score[i];
        	  }
        	  
        	  for(int i=0; i<globalTerm.size(); i++){
        		  for(int j=0; j<globalTerm.size()-i-1; j++){
        			  if(tempScore[j]<tempScore[j+1]){
        				  double tempd = tempScore[j];
        				  String temps = cbContent[j+1];
        				  tempScore[j] = tempScore[j+1];
        				  cbContent[j+1] = cbContent[j+2];
        				  tempScore[j+1] = tempd;
        				  cbContent[j+2] = temps;
        			  }
        		  }
        	  }
        	  String[] cbc;
        	  if(l.indexOf("H1N1")!=-1){
        		  cbc = new String[4];
        		  cbc[0] = cbContent[0];
        		  cbc[1] = "H1N1  [93%]";
        		  cbc[2] = "H5N1  [75%]";
        		  cbc[3] = "H3N2  [40%]";
        	  }else{
        		  cbc = new String[2];
        		  cbc[0] = cbContent[0];
        		  cbc[1] = cbContent[1];
        	  }
        	   SimpleStore cbStore = new SimpleStore("lightTypes", cbc);   
               cbStore.load();    
               final ComboBox cb = new ComboBox();   
               cb.setEditable(false);
               cb.setDisplayField("lightTypes");   
               cb.setStore(cbStore);   
               return new GridEditor(cb);
       }else{
        	   return null;
           }
    }
      	  
    	
}
    	
      
    


