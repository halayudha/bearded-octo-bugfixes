package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.ArrayList;
import java.util.HashMap;

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
//import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ProgressBar;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.CardLayout;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtextux.client.data.PagingMemoryProxy;


/**
 * 
 * @author Wu Sai
 * 
 * @modified by Wang jinbao and David
 **/

public class FormQueryWidget extends BestPeerPanel implements ClickHandler,
		Callback {

	private Toolbar toolbar;
	private final GridPanel grid = new GridPanel();
	private FieldSet tableListFieldSet = new FieldSet();	
	private FieldSet selectedTableFieldSet = new FieldSet();
	private Panel rightPanel;	
	private final FieldSet fieldSet1 = new FieldSet(); //select condition attribute
	private final FieldSet fieldSet2 = new FieldSet(); //select condition input
	private final FieldSet fieldSet3 = new FieldSet(); //join attribute 1
	private final FieldSet fieldSet4 = new FieldSet(); //join attribute 2
	private final FieldSet fieldSet5 = new FieldSet(); //select type: select/aggregation
	private final FieldSet fieldSet6 = new FieldSet(); //select attribute in result
	private final ListBox tableListBox = new ListBox();	
	private final ListBox selectedTableListBox = new ListBox();	
	private Panel window;
	private Window window1;
	private String url;
	private String parameters;
	private Store store = null;
	private boolean gotSchema = false;
	private String lastsql;
	private static String lastSelect;
	private static String lastFrom;
	private static String lastWhere;
	private static HashMap<String, String> condition_column_clause;
	private static String lastGroupby;
	public ArrayList<String> tables = new ArrayList<String>();	
	public ArrayList<String> selectedTables = new ArrayList<String>();
	private HashMap<String, String[]> attributes = new HashMap<String, String[]>();
	private HashMap<String, Integer> types = new HashMap<String, Integer>();

	public static interface I18nConstants extends Constants {
		String queryFormInterface();
		String queryFormSubmit();
		String queryWaiting();
		String queryBeingProcessed();
		String queryComplete();
		String tableSelect();
		String tableSelected();
		String attributeSelect();		
		String createNewQuery();		
		String queryCondition();
		
		
		String addSelectColumn();
		String addAggregation();
		String addJoinColumn();
		String selectTable();
		String deleteTable();
		String selectTableSubmit();
		String selectTableClose();
	}

	public FormQueryWidget() {
		setTopToolbar(new Toolbar());
		tableListFieldSet.setCollapsible(true);
		tableListFieldSet.setAutoHeight(true);
		selectedTableFieldSet.setCollapsible(true);
		selectedTableFieldSet.setAutoHeight(true);			
		grid.setId("grid");
		createWindow();
	}
	
	public class toolbarButtonAdapter extends ButtonListenerAdapter {
		public toolbarButtonAdapter() {
			super();
		}
		public void onClick(com.gwtext.client.widgets.Button button, EventObject e) {
			if(button.getText().equals("create report query")){
				/**
				 * David's version, saving report query
				 */
				  
				window1 = new Window("Create Report Query");
				window1.setSize(575, 500);
				window1.setFrame(true);
				window1.setBorder(false);
				
				FormPanel form = new FormPanel();
				form.setSize(540, 470);
				form.setFrame(false);
				form.setBorder(false);
				form.setPaddings(10);
				form.setLabelWidth(200);
				form.setLayout(new VerticalLayout(10));
				form.setAutoHeight(true);
				
				final FieldSet fs_name = new FieldSet("Input Report Query Name");
				fs_name.setWidth(500);
				fs_name.setLabelWidth(160);
				fs_name.setPaddings(10);
				final TextField tf_name = new TextField("Report Query Name", "query_name", 250);
				fs_name.add(tf_name);
				
				final FieldSet fs_query_condition = new FieldSet("Input Query Conditon");				
				fs_query_condition.setWidth(500);
				fs_query_condition.setLabelWidth(160);
				fs_query_condition.setPaddings(10);
				for(int i=0; i<fieldSet1.getComponents().length; i++){
					ComboBox cb = (ComboBox)(fieldSet1.getComponent(i));
					if(!cb.getText().equals("")){
						TextField tf_query_condition = new TextField(cb.getText(), "query_condition"+i, 250);
						if(condition_column_clause.get(cb.getText())!=null){
							String ccc = condition_column_clause.get(cb.getText());
							tf_query_condition.setEmptyText(ccc);
						}
						fs_query_condition.add(tf_query_condition);
					}
				}
				
				final FieldSet fs_query_discription = new FieldSet("Input Query Discription");				
				fs_query_discription.setWidth(500);
				fs_query_discription.setPaddings(10);
				final TextField tf_query_discription = new TextField();
				tf_query_discription.setHideLabel(true);
				tf_query_discription.setSize(400, 40);
				fs_query_discription.add(tf_query_discription);
				
				final FieldSet fs_query_type = new FieldSet("Input Query Type");
				fs_query_type.setWidth(500);
				fs_query_type.setPaddings(10);
				fs_query_type.setLabelWidth(160);
				ArrayList<String> tAL = new ArrayList<String>();
				tAL.add("General");
				tAL.add("Analytical query");

				Store store = new SimpleStore(new String[] { "name" }, new String[][] {
						new String[] { "General" }, new String[] { "Analytical query" } });
				store.load();
				final ComboBox cb_type = new ComboBox();
				cb_type.setForceSelection(true);
				cb_type.setMinChars(1);
				cb_type.setStore(store);
				cb_type.setDisplayField("name");
				cb_type.setMode(ComboBox.LOCAL);
				cb_type.setEmptyText("Choose Query Type");
				cb_type.setWidth(250);
				cb_type.setLabel("Choose Query Type");
				cb_type.setEditable(false);
				fs_query_type.add(cb_type);				
				
				com.gwtext.client.widgets.Button okBtn = new com.gwtext.client.widgets.Button("Save Query");
				
				parameters = "";
				
				okBtn.addListener(new ButtonListenerAdapter(){					
					@Override
					public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
						if(tf_name.getText().equals("")){
							MessageBox.alert("Invalid name!");
						}
						else{
							TextField tf_name = (TextField)(fs_name.getComponent(0));
							String name = tf_name.getText();
							String condition = "";
							for(int i=0; i<fs_query_condition.getComponents().length; i++){
								TextField tf = (TextField)(fs_query_condition.getComponent(i));
								if(!tf.getText().equals("")){
									condition += tf.getFieldLabel() + tf.getText() + " and ";
									if(tf.getText().indexOf("$")!=-1){
										String[] temp = tf.getText().split("\\$");
										parameters += tf.getFieldLabel()+"|"+temp[1]+":";
									}
								}
							}
							if(!condition.equals(""))
								condition = condition.substring(0, condition.length()-4);
							String sql = "select " + FormQueryWidget.getLastSelect();
							sql += " from " + FormQueryWidget.getLastFrom();
							if(!FormQueryWidget.getLastCondition().equals("") || 
									!condition.equals("")){
								if(FormQueryWidget.getLastCondition().equals("")){
									System.out.println(0);
									sql += " where " + condition;
								}else if(condition.equals("")){
									sql += " where " + FormQueryWidget.getLastCondition();
								}else{
									sql += " where " + FormQueryWidget.getLastCondition() + " and " +condition;
								}
							}							
							if(!FormQueryWidget.getLastGroupBy().equals("")){
								sql += " group by " + FormQueryWidget.getLastGroupBy();
							}
							
							sql = sql.replace( "'","\\'");
							
							String discriptionString = tf_query_discription.getText();
							String typeString = cb_type.getText();
							JSONObject params = new JSONObject();
							params.put("reportName", new JSONString(name));
							params.put("sql", new JSONString(sql));				
							params.put("userName", new JSONString(LoginUI.getUserName()) );
							params.put("discription", new JSONString(discriptionString));
							params.put("queryType", new JSONString(typeString));
							if(parameters.equals("")){
								parameters = "no_param";
							}
							else{
								String[] temp1 = parameters.split(":");
								parameters = "";
								for(int i=0; i<temp1.length; i++){
									String[] temp2 = temp1[i].split("\\|");
									int t = types.get(temp2[0]);
									parameters += t+"|"+temp2[1]+":";
								}
							}
							params.put("parameters", new JSONString(parameters));							
							new RPCCall().invoke("ReportTableService", params, FormQueryWidget.this);
							System.out.println("select " + FormQueryWidget.getLastSelect());
							System.out.println(" from " + FormQueryWidget.getLastFrom());
							System.out.println(" where " + condition);
							if(!FormQueryWidget.getLastGroupBy().equals("")){
								System.out.println(" group by " + FormQueryWidget.getLastGroupBy());
							}
							window1.close();
						}
					}
				});
				form.add(fs_name);
				form.add(fs_query_condition);
				form.add(fs_query_discription);
				form.add(fs_query_type);
				form.addButton(okBtn);
				window1.setAutoScroll(true);
				window1.add(form);
				window1.show();
			}
			else if(button.getText().equals("Save results to pdf")){
				/**
				 * David's version
				 */
				MessageBox.prompt("Saving results", "Please enter pdf file name",
						new PromptCallback() {
							@Override
							public void execute(String btnID, String text) {
								if (btnID.equalsIgnoreCase("ok") && text != null) {
									//System.out.println("to pdf");
									JSONObject params = new JSONObject();
									//System.out.println(1);
									params.put("username", new JSONString(LoginUI.getUserName()));
									//System.out.println(1);
									params.put("sql", new JSONString(lastsql));
									//System.out.println(1);
									params.put("filetype", new JSONString("pdf"));
									//System.out.println(1);
									params.put("title", new JSONString(text));
									//System.out.println(1);
									new RPCCall().invoke("PrintResultsService", params,
											FormQueryWidget.this);
									//System.out.println(2);
								}
							}
						});
			}			
			else if(button.getText().equals(BestPeerDesktop.constants.queryFormSubmit())){
				processQuerySubmit();				
			}
			else if(button.getText().equals(BestPeerDesktop.constants.addSelectColumn())){
				//System.out.println("add a column for selection.");
				int numberOfAttribute = fieldSet1.getItems().length;
				ComboBox cb = createComboBox(selectedTables, numberOfAttribute);
				TextField tf = new TextField();
				tf.setFieldLabel("attribute"+numberOfAttribute);
				tf.setHideLabel(true);
				tf.setWidth(150);
				ComboBox cb5 = createComboBox1();
				ComboBox cb6 = createComboBox2();
				fieldSet1.add(cb);
				fieldSet2.add(tf);
				fieldSet5.add(cb5);
				fieldSet6.add(cb6);
				rightPanel.doLayout();
			}
			else if(button.getText().equals(BestPeerDesktop.constants.addJoinColumn())){
				int numberOfAttribute = fieldSet3.getItems().length;
				ComboBox joinAtt1 = createComboBox(selectedTables, numberOfAttribute);
				ComboBox joinAtt2 = createComboBox(selectedTables, numberOfAttribute);
				fieldSet3.add(joinAtt1);
				fieldSet4.add(joinAtt2);
				fieldSet3.setTitle("Join Attribute1");
				fieldSet4.setTitle("Join Attribute2");
				rightPanel.doLayout();				
			}
			else if(button.getText().equals(BestPeerDesktop.constants.addAggregation())){
				fieldSet5.setVisible(true);
				fieldSet5.doLayout();
			}
			else if(button.getText().equals(BestPeerDesktop.constants.selectTableSubmit())){
				selectedTables = new ArrayList<String>();
				for(int i=0; i<selectedTableListBox.getItemCount(); i++){
					selectedTables.add(selectedTableListBox.getItemText(i));
				}
				updateQueryConditionForm();	
			}
		}
	}

	public void addTableRecord(String table, String[] attribute, int[] type) {
		attributes.put(table, attribute);

		for (int i = 0; i < attribute.length; i++) {
			String namespace = table + "." + attribute[i];
			types.put(namespace, type[i]);
		}
	}

	public void clear() {
		attributes.clear();
	}
	
	public void createWindow(){		
		window = new Panel();	
		window.setId("window");
		ToolbarButton submitTables = new ToolbarButton(BestPeerDesktop.constants.selectTableSubmit());
		submitTables.setIconCls("run-query-icon");
		submitTables.addListener(new toolbarButtonAdapter());
		createTableList();
		updateSelectedTableFieldSet();
		window.setSize(550, 300);
		window.setLayout(new HorizontalLayout(10));	
		
		com.gwtext.client.widgets.Button selectButton = new com.gwtext.client.widgets.Button(BestPeerDesktop.constants.selectTable());
		selectButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				int idx = tableListBox.getSelectedIndex();
				String tname = tableListBox.getItemText(idx);
				boolean addin = true;
				for(int i=0; i<selectedTableListBox.getItemCount(); i++){
					if(tname.equals(selectedTableListBox.getItemText(i))){
						addin = false;
						break;
					}
				}
				if(addin){
					selectedTableListBox.addItem(tname);
				}
			}
		});	
		com.gwtext.client.widgets.Button deleteButton = new com.gwtext.client.widgets.Button(BestPeerDesktop.constants.deleteTable());
		deleteButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				int idx = selectedTableListBox.getSelectedIndex();
				selectedTableListBox.removeItem(idx);
			}
		});
		FieldSet buttonFieldSet = new FieldSet();
		buttonFieldSet.setAutoWidth(true);
		buttonFieldSet.setHeight(100);
		buttonFieldSet.setFrame(false);
		buttonFieldSet.setBorder(false);
		buttonFieldSet.setLayout(new VerticalLayout(50));
		FieldSet fs_sbtn = new FieldSet();
		fs_sbtn.setFrame(false);
		fs_sbtn.setBorder(false);
		fs_sbtn.setAutoWidth(true);
		fs_sbtn.setHeight(20);
		fs_sbtn.add(selectButton);
		FieldSet fs_dbtn = new FieldSet();
		fs_dbtn.setFrame(false);
		fs_dbtn.setBorder(false);
		fs_dbtn.setAutoWidth(true);
		fs_dbtn.setHeight(20);
		fs_dbtn.add(deleteButton);
		buttonFieldSet.add(fs_sbtn);
		buttonFieldSet.add(fs_dbtn);
		window.add(tableListFieldSet);
		window.add(buttonFieldSet);
		window.add(selectedTableFieldSet);		
		window.doLayout();
	}
	
	public void createTableList(){
		tableListFieldSet.clear();
		tableListBox.setWidth("200px");
		tableListBox.setHeight("150px");
		tableListBox.setVisibleItemCount(10);
		if (gotSchema) {
			if(tableListBox.getItemCount()==0){
				for (int i = 0; i < tables.size(); i++){
					tableListBox.addItem(tables.get(i));
				}
			}			
		}
		
		tableListFieldSet.setTitle("Candidate Tables");
		tableListFieldSet.setLayout(new VerticalLayout(10));
		tableListFieldSet.add(tableListBox);
	}
	
	public void updateSelectedTableFieldSet(){
		selectedTableListBox.setWidth("200px");
		selectedTableListBox.setHeight("150px");
		selectedTableListBox.setVisibleItemCount(10);		
		
		selectedTableFieldSet.clear();
		selectedTableFieldSet.setTitle("Query Data Source");
		selectedTableFieldSet.setLayout(new VerticalLayout(10));
		selectedTableFieldSet.add(selectedTableListBox);
	}

	public ComboBox createComboBox(ArrayList<String> selectedTable, int num){
		Object[][] data;
		data = new Object[100][];
		int current = 0;
		for(int j=0; j<selectedTable.size(); j++){			
			String table = selectedTable.get(j);
			String[] attName = attributes.get(table);
			String namespace;
			for (int i = 0; i < attName.length; i++) {			
				namespace = table + "." + attName[i];
				data[current] = new Object[]{namespace};
				current ++;
			}
		}
		data[current] = new Object[]{""};
		Store store = new SimpleStore(new String[]{"name"}, data);
		store.load();	
		ComboBox queryCondition = new ComboBox();
		queryCondition.setForceSelection(true);
		queryCondition.setMinChars(1);
		queryCondition.setFieldLabel("attribute"+num);
		queryCondition.setStore(store);
		//queryCondition.setStore(attrStore);
		queryCondition.setDisplayField("name");
		queryCondition.setMode(ComboBox.LOCAL);
		queryCondition.setEmptyText("Select A Column");
		queryCondition.setWidth(160);
		queryCondition.setHideLabel(true);	
		queryCondition.setEditable(false);
		return queryCondition;
	}
	
	public ComboBox createComboBox1(){
		Store store = new SimpleStore(new String[] { "name" }, new String[][] {
				new String[] { "group by" }, new String[] { "sum" },
				new String[] { "average" }, new String[] { "max" },
				new String[] { "min" }, new String[] { "count" } ,
				new String[] {""} });
		store.load();
		ComboBox type = new ComboBox();
		type.setForceSelection(true);
		type.setMinChars(1);
		type.setStore(store);
		type.setDisplayField("name");
		type.setMode(ComboBox.LOCAL);
		type.setEmptyText("Aggregation");
		type.setWidth(100);
		type.setHideLabel(true);
		type.setEditable(false);
		return type;
	}
	
	public ComboBox createComboBox2(){
		Store store = new SimpleStore(new String[] { "name" }, new String[][] {
				new String[]{""}, new String[] { "show" }, new String[] {"hide"}});
		store.load();
		ComboBox show = new ComboBox();
		show.setForceSelection(true);
		show.setMinChars(1);
		show.setStore(store);
		show.setDisplayField("name");
		show.setMode(ComboBox.LOCAL);
		show.setValue("show");
		show.setWidth(80);
		show.setHideLabel(true);
		show.setEditable(false);
		return show;
	}
		
	public void updateQueryConditionForm() {
		if(rightPanel==null){
			rightPanel = new Panel();
			rightPanel.setAutoHeight(true);
			rightPanel.setId("rightPanel");
		}else
			rightPanel.clear();
		rightPanel.clear();
		fieldSet1.clear();
		fieldSet2.clear();
		fieldSet3.clear();
		fieldSet4.clear();
		fieldSet5.clear();
		fieldSet6.clear();
		FormPanel formPanel = new FormPanel();
		formPanel.setLayout(new HorizontalLayout(5));
		formPanel.setFrame(false);
		formPanel.setBorder(false);
		Toolbar toolbar = new Toolbar();
		formPanel.setTopToolbar(toolbar);
		ToolbarButton tbtn1 = new ToolbarButton(BestPeerDesktop.constants.addSelectColumn());
		tbtn1.setIconCls("add-icon");
		tbtn1.addListener(new toolbarButtonAdapter());
		toolbar.addButton(tbtn1);		

		// David: Init fieldset
		fieldSet1.setFrame(true);
		fieldSet1.setTitle("Column");
		fieldSet2.setFrame(true);
		fieldSet2.setTitle("Filter Condition");
		fieldSet3.setFrame(true);
		fieldSet3.setTitle("Join Attribute1");
		fieldSet4.setFrame(true);
		fieldSet4.setTitle("Join Attribute2");
		fieldSet5.setFrame(true);
		fieldSet5.setTitle("Aggregrate Type");
		fieldSet6.setFrame(true);
		fieldSet6.setTitle("Show/Hide");
		for(int i=0; i<1; i++){
			ComboBox queryAttribute = createComboBox(selectedTables, i);			       
			fieldSet1.add(queryAttribute);
			TextField text = new TextField();
			text.setFieldLabel("attribute"+i);
			text.setHideLabel(true);
			text.setWidth(150);
			fieldSet2.add(text);
			ComboBox queryType = createComboBox1();
			fieldSet5.add(queryType);
			ComboBox show = createComboBox2();
			fieldSet6.add(show);
//			System.out.println("query attribute ok. "+queryAttribute.getWidth());
		}
		formPanel.add(fieldSet1);
		formPanel.add(fieldSet2);
		formPanel.add(fieldSet5);
		formPanel.add(fieldSet6);
		FormPanel joinPanel = new FormPanel();
		joinPanel.setTopToolbar(new Toolbar());
		joinPanel.setLayout(new HorizontalLayout(5));
		joinPanel.setFrame(false);
		joinPanel.setBorder(false);
		for(int i=0; i<1; i++){
			ComboBox joinAttribute1 = createComboBox(selectedTables, i);			       
			fieldSet3.add(joinAttribute1);
			ComboBox joinAttribute2 = createComboBox(selectedTables, i);			       
			fieldSet4.add(joinAttribute2);
		}
		joinPanel.add(fieldSet3);
		joinPanel.add(fieldSet4);
		ToolbarButton addJoinButton = new ToolbarButton(BestPeerDesktop.constants.addJoinColumn());
		addJoinButton.setIconCls("add-icon");
		addJoinButton.addListener(new toolbarButtonAdapter());
		joinPanel.getTopToolbar().addButton(addJoinButton);
		
		rightPanel.add(formPanel);
		rightPanel.add(joinPanel);
		if(selectedTables.size()==1)
			joinPanel.hide();
		rightPanel.setVisible(true);
		rightPanel.doLayout();
	}
	
	public void getMetaData() {
		this.tables.clear();
		this.attributes.clear();
		this.types.clear();
		JSONObject params = new JSONObject();
		params.put("schema", new JSONString("global"));
		new RPCCall().invoke("SchemaService", params, this);
	}

	Panel panel = null;
	@Override
	public Panel getViewPanel() {
		if (panel == null) {		
			panel = new Panel();
			panel.setTitle("Adhoc Query Wizard ----- Step 1: select data source (3 steps in total)");  
			panel.setHeight(450);
			
			final Panel wizardPanel = new Panel();  
	        wizardPanel.setHeight(450);
	       
	        wizardPanel.setLayout(new CardLayout());   
	        wizardPanel.setActiveItem(0);   
	        wizardPanel.setPaddings(15);   
	  
	        ButtonListenerAdapter listener = new ButtonListenerAdapter() {   
	            public void onClick(Button button, EventObject e) {   
	                String btnID = button.getId();   
	                CardLayout cardLayout = (CardLayout) wizardPanel.getLayout();   
	                String panelID = cardLayout.getActiveItem().getId();   
	  
	                if (btnID.equals("move-prev")) {   
	                    if (panelID.equals("grid")) {   
	                        cardLayout.setActiveItem(1);  
	                        panel.setTitle("Adhoc Query Wizard ----- Step 2: config query condition (3 steps in total)");
	                    } else {   
	                        cardLayout.setActiveItem(0);
	                        panel.setTitle("Adhoc Query Wizard ----- Step 1: select data source (3 steps in total)");
	                    }   
	                } else if(btnID.equals("move-next")){	  
	                    if (panelID.equals("window")) {   
	                        cardLayout.setActiveItem(1); 
	                        panel.setTitle("Adhoc Query Wizard ----- Step 2: config query condition (3 steps in total)");
	                        selectedTables = new ArrayList<String>();
	        				for(int i=0; i<selectedTableListBox.getItemCount(); i++){
	        					selectedTables.add(selectedTableListBox.getItemText(i));
	        				} 
	                        updateQueryConditionForm();
	                    } else if(panelID.equals("rightPanel")) {   
	                        cardLayout.setActiveItem(2);  
	                        panel.setTitle("Adhoc Query Wizard ----- Step 3: view query result (3 steps in total)");
	                        processQuerySubmit();
	                    }   
	                }   
	            }   
	        };   
	  
	        Toolbar toolbar = new Toolbar();   
	  
	        ToolbarButton backButton = new ToolbarButton("Back", listener); 
	        backButton.setIconCls("back-icon");
	        backButton.setId("move-prev");   
	        toolbar.addButton(backButton); 
	        
	        toolbar.addSpacer();
	        toolbar.addSeparator();
	        toolbar.addSpacer();
	  
	        ToolbarButton nextButton = new ToolbarButton("Next", listener); 
	        nextButton.setIconCls("next-icon");
	        nextButton.setId("move-next");   
	        toolbar.addButton(nextButton);   
	  
	        wizardPanel.setTopToolbar(toolbar);   
	  
	        this.updateQueryConditionForm();
			String[] columns = {null, null};
			int[] type = {12, 12};
			this.initTable(columns, type, getCompanyData());
			wizardPanel.add(window);
			wizardPanel.add(rightPanel);
			wizardPanel.add(grid);
			getMetaData();
			panel.add(wizardPanel);
		}
		else{panel.setTitle("Adhoc Query Wizard ----- Step 1: select data source (3 steps in total)");  
			Panel component = (Panel)(panel.getComponent(0));
			CardLayout cardLayout = (CardLayout) component.getLayout();
			this.selectedTableListBox.clear();
			cardLayout.setActiveItem(0);
		}
		
		return panel;
	}
	
	public String getProgress(int i){
		String[] progress = {
				"<b>Step1: select tables to query</b> >>> Step2: config query conditions >>> Step3: view query result",
				"Step1: select tables to query >>> <b>Step2: config query conditions</b> >>> Step3: view query result",
				"Step1: select tables to query >>> Step2: config query conditions >>> <b>Step3: view query result</b>"
		};
		if(i<progress.length)
			return progress[i];
		else
			return "wrong input!";
	}
	
	@Override
	protected void afterRender() {
		toolbar = getTopToolbar();
		ToolbarButton btn1 = new ToolbarButton(BestPeerDesktop.constants.createNewQuery());
		btn1.setIconCls("new-query-icon");
		
		ToolbarButton btn2 = new ToolbarButton(BestPeerDesktop.constants.queryFormSubmit());
		btn2.setIconCls("run-query-icon");	
		
		btn1.addListener(new toolbarButtonAdapter());
		btn2.addListener(new toolbarButtonAdapter());
		
		super.afterRender();
	}

	@Override
	public void onClick(ClickEvent event) {

		Object source = event.getSource();
		if (source instanceof Button) {
			if(((Button) source).getText().equals(BestPeerDesktop.constants.selectTableSubmit())){
				for(int i=0; i<selectedTableListBox.getItemCount(); i++){
					selectedTables.add(selectedTableListBox.getItemText(i));
				}				
				updateQueryConditionForm();				
				if(selectedTables.size()==1){
					Component[] compo = rightPanel.getItems();
					((FormPanel)compo[1]).setVisible(false);
				}
			}
			else if(((Button) source).getText().equals(BestPeerDesktop.constants.selectTable())){
				int idx = tableListBox.getSelectedIndex();
				String tname = tableListBox.getItemText(idx);
				boolean addin = true;
				for(int i=0; i<selectedTableListBox.getItemCount(); i++){
					if(tname.equals(selectedTableListBox.getItemText(i))){
						addin = false;
						break;
					}
				}
				if(addin){
					selectedTableListBox.addItem(tname);
				}
			}
			else if(((Button) source).getText().equals(BestPeerDesktop.constants.deleteTable())){
				int idx = selectedTableListBox.getSelectedIndex();
				selectedTableListBox.removeItem(idx);
			}			
		}
	}

	public void initTable(String[] columns, int[] type, Object[][] data) {
	    // Set up the header row. It's one greater than the number of visible rows.
	    //
		int min = Math.min(columns.length, type.length);
		RecordDef recordDef = null;
		FieldDef[] fieldDef = new FieldDef[min];
		for(int i=0; i<min; i++)
		{
			switch(type[i])
			{
			case 4:  //java.sql.Types.INTEGER
			case 5:  //java.sql.Types.BIGINT
			case 3:  //java.sql.Types.DECIMAL
			case 2:  //java.sql.Types.NUMERIC:
								fieldDef[i] = new IntegerFieldDef(columns[i]); break;
			case 8:  //java.sql.Types.DOUBLE
			case 6:  //java.sql.Types.FLOAT
			case 7:  //java.sql.Types.REAL
								fieldDef[i] = new FloatFieldDef(columns[i]); break;
			case 91: //java.sql.Types.DATE
								fieldDef[i] = new DateFieldDef(columns[i], "n/j h:ia"); break;
			case 16: //java.sql.Types.BOOLEAN
								fieldDef[i] = new BooleanFieldDef(columns[i]); break; 
			default:
				fieldDef[i] = new StringFieldDef(columns[i]);								
			}
		}
		
		recordDef = new RecordDef(fieldDef);
		
		PagingMemoryProxy proxy = new PagingMemoryProxy(data);  
		ArrayReader reader = new ArrayReader(recordDef);
		store = new Store(proxy, reader, true);
		
		ColumnConfig[] columnConfig = new ColumnConfig[min];
		
		for(int i=0; i<min; i++)
		{
			columnConfig[i] = new ColumnConfig(columns[i], columns[i], 500/min, true);
		}
		ColumnModel columnModel = new ColumnModel(columnConfig);
		if(grid.getStore() == null) {
			
			grid.setStore(store);		
			grid.setColumnModel(columnModel);
			grid.setWidth(600);  
			grid.setHeight(450);
			
			grid.setFrame(true);
			grid.setStripeRows(true);
			grid.setId("grid");

			final PagingToolbar pagingToolbar = new PagingToolbar(store);
			pagingToolbar.setPageSize(13);
			pagingToolbar.setDisplayInfo(true);
			pagingToolbar.setDisplayMsg("Displaying EMR records {0} - {1} of {2}");
			pagingToolbar.setEmptyMsg("No records to display");
			
			grid.setBottomToolbar(pagingToolbar);
			grid.setTopToolbar(new Toolbar());
			ToolbarButton btn1 = new ToolbarButton("Save results to pdf");
			btn1.setIconCls("pdf-icon");
			btn1.addListener(new toolbarButtonAdapter());
			ToolbarButton btn2 = new ToolbarButton("print results to rtf");
			btn2.setIconCls("word-icon");
			btn2.addListener(new toolbarButtonAdapter());
			ToolbarButton btn3 = new ToolbarButton("create report query");
			btn3.setIconCls("create-report-icon");
			btn3.addListener(new toolbarButtonAdapter());
			grid.getTopToolbar().addButton(btn1);
			grid.getTopToolbar().addButton(btn3);
		}
		else {
			grid.reconfigure(store, columnModel);
			PagingToolbar pagingBar = (PagingToolbar)grid.getBottomToolbar();
			pagingBar.unbind(pagingBar.getStore());
			pagingBar.bind(store);
		}
		store.load(0, 13);		
	 }
	
	private Object[][] getCompanyData() {
		return new Object[][] {
				 };
	}

	@Override
	public void onFailure() {
		MessageBox.show(new MessageBoxConfig() {
			{
				setMsg(BestPeerDesktop.constants.queryWarning());

				setWidth(300);
			}
		});
	}

	@Override
	public void onReady(JSONObject resultSet) {
		// TODO Auto-generated method stub

		if (!resultSet.containsKey("status")&&!resultSet.containsKey("url")) {
			JSONArray namelist = (JSONArray) resultSet.get("table_name");
			for (int i = 0; i < namelist.size(); i++) {
				String name = ((JSONString) namelist.get(i)).stringValue();
				tables.add(name);
				JSONArray columnlist = (JSONArray) resultSet.get("n_" + name);
				JSONArray dtypelist = (JSONArray) resultSet.get("dt_" + name);
				JSONArray typelist = (JSONArray) resultSet.get("t_" + name);
				String[] cname = new String[columnlist.size()];
				String[] sstype = new String[columnlist.size()];
				int[] dtype = new int[columnlist.size()];
				for (int j = 0; j < cname.length; j++) {
					cname[j] = ((JSONString) columnlist.get(j)).stringValue();
					sstype[j] = ((JSONString) typelist.get(j)).stringValue();
					dtype[j] = (int) (((JSONNumber) dtypelist.get(j))
							.doubleValue());
				}
				this.addTableRecord(name, cname, dtype);
			}

			if (!this.gotSchema) {
				// update the right panel
				if(tableListBox.getItemCount()==0){
					for (int i = 0; i < tables.size(); i++)
						tableListBox.addItem(tables.get(i));
				}				
				this.tableListFieldSet.doLayout();
				this.gotSchema = true;
			}
		} 
		else if(resultSet.containsKey("url")){
			url = ((JSONString) resultSet.get("url")).stringValue();
			url = ((JSONString) resultSet.get("url")).stringValue();
			com.google.gwt.user.client.Window.open(url, "", ""); 
		}
		else {
			MessageBox.hide();
			String status = ((JSONString) resultSet.get("status"))
					.stringValue();
			if (status.equals("false")) {
			} 
			else {
				MessageBox.hide();
				JSONArray namelist = (JSONArray) resultSet.get("column_name");
				int size = namelist.size();
				String[] column = new String[size];
				for (int i = 0; i < size; i++) {
					column[i] = ((JSONString) namelist.get(i)).stringValue();
				}
				JSONArray typelist = (JSONArray) resultSet.get("column_type");
				int[] type = new int[size];
				for (int i = 0; i < size; i++) {
					type[i] = (int) ((JSONNumber) typelist.get(i))
							.doubleValue();
				}
				int recordNumber = (int) ((JSONNumber) resultSet
						.get("column_number")).doubleValue();

				Object[][] record = new String[recordNumber][size];
				for (int i = 0; i < recordNumber; i++) {
					String listname = "list" + i;
					JSONArray nextlist = (JSONArray) resultSet.get(listname);
					for (int j = 0; j < size; j++) {
						record[i][j] = ((JSONString) nextlist.get(j))
								.stringValue();
					}
				}

				this.initTable(column, type, record);
				this.grid.doLayout();
			}
		}
	}
	private ArrayList<String> selectedColumn;
	private ArrayList<String> groupByColumn;
	private void processQuerySubmit(){
		selectedColumn = new ArrayList<String>();
		groupByColumn = new ArrayList<String>();
		String sql = "select ";
		String table = "";
		int ntable = 0;
		String content = "";
		int ncontent = 0;
		String condition = "";
		int ncondition = 0;
		String groupBy = "";
		int ngroupBy = 0;
		int naggr = 0;
		condition_column_clause = new HashMap<String, String>();
		for(int i=0; i<selectedTables.size(); i++){
			table += selectedTables.get(i) + ", ";
		}
		ntable = selectedTables.size();
		table = table.substring(0,table.length()-2)+" ";	
		
		Component[] field1 = fieldSet1.getItems();
		Component[] field2 = fieldSet2.getItems();
		Component[] field3 = fieldSet3.getItems();
		Component[] field4 = fieldSet4.getItems();
		Component[] field5 = fieldSet5.getItems();
		Component[] field6 = fieldSet6.getItems();
		for(int i=0; i<field1.length; i++){
			if(((ComboBox)field6[i]).getText().equals("show")&&!((ComboBox)field1[i]).getText().equals("")
					&&((ComboBox)field5[i]).getText().equals("")){
				content += ((ComboBox)field1[i]).getText() + ", ";	
				ncontent ++;
			}
			if(!((ComboBox)field5[i]).getText().equals("group by")&&!((ComboBox)field5[i]).getText().equals("")&&!((ComboBox)field1[i]).getText().equals("")){
				content += ((ComboBox)field5[i]).getText() + "(" + 
				           ((ComboBox)field1[i]).getText() + ")" + ", ";
				ncontent ++;
				naggr ++;
			}	
			if(!((ComboBox)field1[i]).getText().equals("")&&((ComboBox)field5[i]).getText().equals("group by")){
				groupBy += ((ComboBox)field1[i]).getText() + ", ";
				content += ((ComboBox)field1[i]).getText() + ", ";
				ngroupBy ++;
				ncontent ++;
			}
			if(!((ComboBox)field1[i]).getText().equals("")&&!((TextField)field2[i]).getText().equals("")){
				condition_column_clause.put(((ComboBox)field1[i]).getText(), ((TextField)field2[i]).getText());
				condition += ((ComboBox)field1[i]).getText() + ((TextField)field2[i]).getText();
				condition += " and ";
				ncondition ++;
			}
		}				
		for(int i=0; i<field3.length; i++){
			if(!((ComboBox)field3[i]).getText().equals("")&&!((ComboBox)field4[i]).getText().equals("")){
				condition += ((ComboBox)field3[i]).getText() + "=" + ((ComboBox)field4[i]).getText();
				condition += " and ";
				ncondition ++;
			}					
		}
		//refine condition, content, group by.
		
		if(!content.equals("")){
			if(ngroupBy > 0){
				content = "";
				ncontent = 0;
				for(int i=0; i<field1.length; i++){
					if(!((ComboBox)field5[i]).getText().equals("group by")&&!((ComboBox)field5[i]).getText().equals("")&&!((ComboBox)field1[i]).getText().equals("")){
						content += ((ComboBox)field5[i]).getText() + "(" + 
				           ((ComboBox)field1[i]).getText() + ")" + ", ";
						ncontent ++;
					}
				}
				for(int i=0; i<field1.length; i++){
					if(!((ComboBox)field1[i]).getText().equals("")&&((ComboBox)field5[i]).getText().equals("group by")){
						content += ((ComboBox)field1[i]).getText()+", ";
						ncontent ++;
					}
				}
			}
			content = content.substring(0, content.length()-2);
			System.out.println(content);
			sql += content;
		}
		sql += " from " + table;
		if(!condition.equals("")){
			condition = condition.substring(0, condition.length()-5);
			sql += " where " + condition;
		}
		if(!groupBy.equals("")){
			groupBy = groupBy.substring(0, groupBy.length()-2);
			sql += " group by " + groupBy;
		}
		
		JSONObject params = new JSONObject();
		params.put("sql", new JSONString(sql));
		
		params.put("username", new JSONString(LoginUI.getUserName()) );
	
		if((ncontent>0&&ntable>0&&ngroupBy>0&&naggr==(ncontent-ngroupBy)&&naggr>0)
				||(ncontent>0&&ntable>0&&ngroupBy==0&&(naggr==ncontent||naggr==0))){
			MessageBox.show(new MessageBoxConfig() {
				{
					setMsg(BestPeerDesktop.constants.queryBeingProcessed());
					setWidth(300);
					setWait(true);
					setWaitConfig(new WaitConfig() {
						{
							setInterval(200);
						}
					});
				}
			});
			new RPCCall().invoke("SQLService", params, FormQueryWidget.this);
			lastsql = sql;
			lastSelect = content;
			lastFrom = table;
			lastWhere = condition;
			lastGroupby = groupBy;
		}
		else{
			MessageBox.alert(sql+"\nCheck SQL Synax!");
		}
	}
	
	public static String getLastSelect(){
		return lastSelect;
	}
	
	public static String getLastFrom(){
		return lastFrom;
	}
	
	public static String getLastCondition(){
		return lastWhere;
	}
	
	public static String getLastGroupBy(){
		return lastGroupby;
	}
}
