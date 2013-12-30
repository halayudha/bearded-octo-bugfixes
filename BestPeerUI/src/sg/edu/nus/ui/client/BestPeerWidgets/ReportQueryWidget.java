package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.ArrayList;
import com.google.gwt.user.client.Random;


import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.GenericConfig;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;

import com.gwtext.client.widgets.Window;
import com.google.gwt.user.client.ui.ListBox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.grid.*;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtextux.client.data.PagingMemoryProxy;
import com.gwtext.client.widgets.form.FieldSet;

import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.chart.yui.ColumnChart;
import com.gwtext.client.widgets.chart.yui.LineChart;
import com.gwtext.client.widgets.chart.yui.NumericAxis;
import com.gwtext.client.widgets.chart.yui.SeriesDefY;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

public class ReportQueryWidget extends BestPeerPanel implements Callback{
	
	public static interface I18nConstants extends Constants {
		String reportQuery();
		String submitReportQuery();
		String loadReportQuery();
		String inputParams();
		String params();
	}
	private Panel panel;
	private ListBox reportQueryBox;
	private ArrayList<String> query = new ArrayList<String>();
	private ArrayList<String> param = new ArrayList<String>();
	private ArrayList<String> dis = new ArrayList<String>();
	private ArrayList<String> type = new ArrayList<String>();
	private ArrayList<String> creator = new ArrayList<String>();
	private int idx;
	private Window window;
	private Window window_analysis;
	private static final GridPanel grid = new GridPanel();
	private GridPanel discriptionPanel = new GridPanel();
	private Store store ;
	private String[] parameters;
	private String lastsql;
	String url;
	private int localSize;
	private int globalSize;
	private ArrayList<String> globalTerm;
	private ArrayList<String> localTerm;
	public ReportQueryWidget(){
		setTopToolbar(new Toolbar());
		createBox();
	}
	
	public void onFailure(){
		
	}
	
	private static ArrayList<String> columns = new ArrayList<String>();  //store the columns of query result, to draw analysis graph.
	
	public void onReady(JSONObject resultSet){
		if(resultSet.containsKey("queryName")){
			reportQueryBox.clear();
			JSONArray names = resultSet.get("queryName").isArray();
			JSONArray queries = resultSet.get("reportName").isArray();
			JSONArray params = resultSet.get("param").isArray();
			JSONArray discriptions = resultSet.get("discription").isArray();
			JSONArray type = resultSet.get("type").isArray();
			JSONArray creator = resultSet.get("creator").isArray();
			query = new ArrayList<String>();
			param = new ArrayList<String>();
			dis = new ArrayList<String>();
			this.type = new ArrayList<String>();
			this.creator = new ArrayList<String>();
			for(int i=0; i<queries.size(); i++){
				String name = names.get(i).toString();
				name = name.substring(1, name.length()-1);
				String sql = queries.get(i).toString();
				sql = sql.substring(1, sql.length()-1);
				String disc = discriptions.get(i).toString();
				disc = disc.substring(1, disc.length()-1);
				String t = type.get(i).toString();
				t = t.substring(1, t.length()-1);
				String c = creator.get(i).toString();
				c = c.substring(1, c.length()-1);
				reportQueryBox.addItem(name);
				query.add(sql);
				String par = params.get(i).toString();
				par = par.substring(1, par.length()-1);
				param.add(par);
				dis.add(disc);
				this.type.add(t);
				this.creator.add(c);
			}
			
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
		}
		else if(resultSet.containsKey("url")){
			url = ((JSONString) resultSet.get("url")).stringValue();
			com.google.gwt.user.client.Window.open(url, "", ""); 
		}
		else{
			MessageBox.hide();
			String status = ((JSONString)resultSet.get("status")).stringValue();
			if(status.equals("false"))
			{
			}
			else{
				 result.activate(0);
				 JSONArray namelist = (JSONArray)resultSet.get("column_name");
				 int size = namelist.size();
				 String[] column = new String[size];
				 columns = new ArrayList<String>();
				 for(int i=0; i<size; i++){
					 column[i] = ((JSONString)namelist.get(i)).stringValue();
					 columns.add(((JSONString)namelist.get(i)).stringValue());
				 }
				 JSONArray typelist = (JSONArray)resultSet.get("column_type");
				 int[] type = new int[size];
				 for(int i=0; i<size; i++){
					type[i] = (int)((JSONNumber)typelist.get(i)).doubleValue();
				 }
				 int recordNumber = (int)((JSONNumber)resultSet.get("column_number")).doubleValue();				 
				 Object[][] record = new String[recordNumber][size];
				 for(int i=0; i<recordNumber; i++){
					 String listname = "list" + i;
					 JSONArray nextlist = (JSONArray)resultSet.get(listname);
					 for(int j=0; j<size; j++)
					 {
						 record[i][j] =  ((JSONString)nextlist.get(j)).stringValue();
					 }
				 }				 
				 this.initTable(column, type, record);	
				 Store store = ReportQueryWidget.getStore();
				 xbox.setStore(store);
				 fs_y.clear();
				 ComboBox cb = createComboBox(columns);
				 fs_y.add(cb);
				 ybox = new ArrayList<ComboBox>();
				 ybox.add(cb);
				 fs_y.doLayout();
				 panel.doLayout();
				 result.activate(0);
			}
		}
	}
	
	public void createBox(){
		reportQueryBox = new ListBox();
		reportQueryBox.setWidth("200px");
		reportQueryBox.setVisibleItemCount(30);
		reportQueryBox.setHeight("490px");
		
		reportQueryBox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				// TODO Auto-generated method stub
				int idx = reportQueryBox.getSelectedIndex();
				Object[][] data = new Object[1][];
				data[0] = new Object[]{reportQueryBox.getValue(idx), 
						type.get(idx), dis.get(idx), creator.get(idx)};
				cbStore = 
					new SimpleStore(new String[] {"name", "type", "discription", "creator"}, 
							data);
				cbStore.load();
				discriptionPanel.reconfigure(cbStore, columnModel);
				
			}});

	}
	
	
	TabPanel result = null;
	Panel g_analysis = null;
	private SimpleStore cbStore;
	ColumnModel columnModel;
	public Panel getViewPanel(){
		if(panel == null){
			panel = new Panel();
			panel.setLayout(new HorizontalLayout(5));
			panel.setAutoScroll(true);
			
			BorderLayoutData rightLayoutData = new BorderLayoutData(RegionPosition.CENTER);
			rightLayoutData.setMargins(new Margins(5, 5, 5, 5));

			Panel rightPanel = new Panel();
			rightPanel.setLayout(new VerticalLayout(5));
			System.out.println("start building grid.");
			discriptionPanel = new GridPanel();
			discriptionPanel.setTitle("Report Query Discription");
			discriptionPanel.setWidth(750);
			discriptionPanel.setHeight(90);

			BaseColumnConfig queryName = 
				new ColumnConfig("Report Query Name", "name", 160);	
			BaseColumnConfig queryType = 
				new ColumnConfig("Type", "type", 100);
			BaseColumnConfig queryDiscription = 
				new ColumnConfig("Discription", "discription", 350);
			BaseColumnConfig queryCreator = 
				new ColumnConfig("Creator", "creator", 100);
			BaseColumnConfig[] columnConfigs = {
					queryName,
					queryType,
					queryDiscription,
					queryCreator
			};		
			columnModel = new ColumnModel(columnConfigs);
			cbStore = 
				new SimpleStore(new String[] {"name", "type", "discription", "creator"}, 
						new Object[][] {});
			cbStore.load();
			discriptionPanel.setColumnModel(columnModel);
			discriptionPanel.setStore(cbStore);
			System.out.println("finish building grid.");
			
			
			ToolbarButton btn = new ToolbarButton(BestPeerDesktop.constants.submitReportQuery());
			btn.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(Button button, EventObject e){
					idx = reportQueryBox.getSelectedIndex();
					if(!param.get(idx).equals("no_param")){						
						parameters = param.get(idx).split(":");
						window = new Window(BestPeerDesktop.constants.inputParams());
						window.setWidth(400);
						window.setAutoHeight(true);
						window.setBorder(false);
						window.setFrame(true);
						FormPanel form = new FormPanel();
						FieldSet fs = new FieldSet();
						fs.setTitle("param values");
						fs.setLabelWidth(100);
						
						for(int i=0; i<parameters.length; i++){
							String[] tn_params = parameters[i].split("\\|");
							if(tn_params[1].indexOf("date")!=-1){
								DateField dateField = new DateField();   
								dateField.setFormat("Y-m-d");   
						        dateField.setMinValue("01-01-06");   
						        dateField.setHeight(25);
						        dateField.setWidth(250);
						        dateField.setLabel(tn_params[1]);
						        fs.add(dateField);
							}else if(tn_params[1].equals("test_name")){
								ComboBox cb;
								if(LoginUI.getUserType().equals("physician")){
									cb = ReportQueryWidget.createComboBox(globalTerm, tn_params[1]);
									fs.add(cb);
								}else
								{
									cb = ReportQueryWidget.createComboBox(localTerm, tn_params[1]);
									fs.add(cb);
								}
							}else if(tn_params[1].equals("patient_id")){
								Store store = new SimpleStore(new String[] { "name" }, new String[][] {
										new String[] { "S8284186E" }, new String[] { "S8285084H" } });
								store.load();
								ComboBox cb = new ComboBox();
								cb.setMinChars(1);
								cb.setStore(store);
								cb.setDisplayField("name");
								cb.setMode(ComboBox.LOCAL);
								cb.setBlankText("");
								cb.setWidth(250);
								cb.setLabel(tn_params[1]);
								fs.add(cb);
							}else{
								Store store = new SimpleStore(new String[] { "name" }, new String[][] {
										});
								store.load();
								ComboBox cb = new ComboBox();
								cb.setMinChars(1);
								cb.setStore(store);
								cb.setDisplayField("name");
								cb.setMode(ComboBox.LOCAL);
								cb.setWidth(250);
								cb.setLabel(tn_params[1]);
								fs.add(cb);
							}
						}												
						
						com.gwtext.client.widgets.Button okBtn = new com.gwtext.client.widgets.Button("OK");
						okBtn.addListener(new ButtonListenerAdapter(){
							@Override
							public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
								String sql = query.get(idx); 
								System.out.println(sql);
								Component[] formPanels = window.getComponents();
								Component[] fsets = ((FormPanel)formPanels[0]).getComponents();
								Component[] component = ((FieldSet)fsets[0]).getComponents();
								for(int i=0; i<parameters.length; i++){
									System.out.println(parameters[i]);
									String[] tn_params = parameters[i].split("\\|");
									if(((TextField) component[i]).getText().equals("")){
										MessageBox.alert("params empty!");
									}
									else{
										System.out.println("in else.....");
										String componentType = component[i].getClass().toString();
										String par = ((TextField) component[i]).getText().toString();
										System.out.println(par);
										if(componentType.indexOf("DateField")!=-1){
											if(Ext.isIE()){
												System.out.println("working in IE.");
												String[] pars = par.split("\\ ");
												par = pars[5];
												if(pars[1].equals("Jan"))
													par += "-01";
												else if(pars[1].equals("Feb"))
													par += "-02";
												else if(pars[1].equals("Mar"))
													par += "-03";
												else if(pars[1].equals("Apr"))
													par += "-04";
												else if(pars[1].equals("May"))
													par += "-05";
												else if(pars[1].equals("Jun"))
													par += "-06";
												else if(pars[1].equals("Jul"))
													par += "-07";
												else if(pars[1].equals("Aug"))
													par += "-08";
												else if(pars[1].equals("Sep"))
													par += "-09";
												else if(pars[1].equals("Oct"))
													par += "-10";
												else if(pars[1].equals("Nov"))
													par += "-11";
												else if(pars[1].equals("Dec"))
													par += "-12";
												if(Integer.parseInt(pars[2])<10)
													par += "-0"+pars[2];
												else
													par += "-"+pars[2];
											}else{
												System.out.println("working in FireFox");
												String[] pars = par.split("\\ ");
												par = pars[3];
												if(pars[1].equals("Jan"))
													par += "-01";
												else if(pars[1].equals("Feb"))
													par += "-02";
												else if(pars[1].equals("Mar"))
													par += "-03";
												else if(pars[1].equals("Apr"))
													par += "-04";
												else if(pars[1].equals("May"))
													par += "-05";
												else if(pars[1].equals("Jun"))
													par += "-06";
												else if(pars[1].equals("Jul"))
													par += "-07";
												else if(pars[1].equals("Aug"))
													par += "-08";
												else if(pars[1].equals("Sep"))
													par += "-09";
												else if(pars[1].equals("Oct"))
													par += "-10";
												else if(pars[1].equals("Nov"))
													par += "-11";
												else if(pars[1].equals("Dec"))
													par += "-12";
												if(Integer.parseInt(pars[2])<10)
													par += "-0"+pars[2];
												else
													par += "-"+pars[2];
											}
											
										}										
										System.out.println(par);
										int type_integer = Integer.parseInt(tn_params[0]);
										if(type_integer==4 || type_integer==6){
											sql = sql.replace("$"+tn_params[1]+"$", par);
										}
										else{
											sql = sql.replace("$"+tn_params[1]+"$", "'"+par+"'");
										}
									}
							    }
								window.close();		
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
								JSONObject params = new JSONObject();					
								params.put("username", new JSONString(LoginUI.getUserName()) );
								params.put("sql", new JSONString(sql));
								new RPCCall().invoke("SQLService", params, ReportQueryWidget.this);	
								lastsql = sql;
							}
						});
						okBtn.setMinWidth(80);
						form.setFrame(true);
						form.setBorder(false);
						form.add(fs);
						form.addButton(okBtn);
						window.add(form);
						window.show();
					}
					else{
						String sql = query.get(idx);
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
						JSONObject params = new JSONObject();					
						params.put("username", new JSONString(LoginUI.getUserName()) );
						params.put("sql", new JSONString(sql));
						new RPCCall().invoke("SQLService", params, ReportQueryWidget.this);
						lastsql = sql;
					}					
				}
			});
			Panel leftPanel = new Panel("Report Query List");
			leftPanel.setHeight(90 + 420 + 10);
			leftPanel.setBorder(false);
			leftPanel.add(reportQueryBox);
			
			BorderLayoutData leftLayoutData = new BorderLayoutData(RegionPosition.WEST);
			leftLayoutData.setMargins(new Margins(5, 5, 0, 5));
			leftLayoutData.setCMargins(new Margins(5, 5, 5, 5));
			leftLayoutData.setSplit(false);
			panel.add(leftPanel, leftLayoutData);
			
			ToolbarButton loadBtn = new ToolbarButton("Refresh Query List");
			loadBtn.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
					JSONObject params = new JSONObject();					
					params.put("username", new JSONString(LoginUI.getUserName()) );
					params.put("load", new JSONString("blablabla"));
					new RPCCall().invoke("ReportTableService", params, ReportQueryWidget.this);
				}
			});
			String[] columns = { "	", "	", "	", "	", "	",
			"	" };
			int[] type = { 12, 12, 12, 12, 12, 12 };
			initTable(columns, type, getCompanyData());
			this.getTopToolbar().addButton(loadBtn);
			this.getTopToolbar().addSpacer();
			this.getTopToolbar().addSeparator();
			this.getTopToolbar().addSpacer();
			this.getTopToolbar().addButton(btn);
			btn.setIconCls("run-query-icon");
			loadBtn.setIconCls("new-query-icon");
			rightPanel.add(discriptionPanel);
			
			result = new TabPanel();
			result.setTitleCollapse(true);
			result.setWidth(750);
			result.setHeight(420);
			
			result.add(grid);
			rightPanel.add(result);
			panel.add(rightPanel, rightLayoutData);
			JSONObject params = new JSONObject();					
			params.put("username", new JSONString(LoginUI.getUserName()) );
			params.put("load", new JSONString("blablabla"));
			new RPCCall().invoke("ReportTableService", params, ReportQueryWidget.this);
		}
		return panel;
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
			grid.setWidth(750);  
			grid.setHeight(400);
			
			grid.setFrame(true);
			grid.setStripeRows(true);
			grid.setTitle(BestPeerDesktop.constants.queryResult());

			final PagingToolbar pagingToolbar = new PagingToolbar(store);
			pagingToolbar.setPageSize(13);
			pagingToolbar.setDisplayInfo(true);
			pagingToolbar
					.setDisplayMsg("Displaying EMR records {0} - {1} of {2}");
			pagingToolbar.setEmptyMsg("No records to display");

			grid.setBottomToolbar(pagingToolbar);
			grid.setTopToolbar(new Toolbar());
			ToolbarButton btn1 = new ToolbarButton("Save results to pdf");
			btn1.setIconCls("pdf-icon");
			btn1.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
					/**
					 * David's version
					 */
					MessageBox.prompt("Saving results", "Please enter report title",
							new PromptCallback() {
								@Override
								public void execute(String btnID, String text) {
									if (btnID.equalsIgnoreCase("ok") && text != null) {
										JSONObject params = new JSONObject();
										params.put("username", new JSONString(LoginUI.getUserName()));
										params.put("sql", new JSONString(lastsql));
										params.put("filetype", new JSONString("pdf"));
										params.put("title", new JSONString(text));
										new RPCCall().invoke("PrintResultsService", params,
												ReportQueryWidget.this);
									}
								}
							});
				}
			});
			ToolbarButton btn2 = new ToolbarButton("Build Analysis Graph");
			btn2.setIconCls("word-icon");
			btn2.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
					window_analysis = new Window("Graph Configuration");
					window_analysis.setSize(360, 385);
					window_analysis.setFrame(true);
					window_analysis.setBorder(false);
					window_analysis.add(analysisPanel());
					window_analysis.show();
				}
			});
			grid.getTopToolbar().addButton(btn1);
			grid.getTopToolbar().addButton(btn2);
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
	
	public static ComboBox createComboBox(ArrayList<String> terms, String label){
		Object[][] data;
		data = new Object[100][];
		int current = 0;
		for(int j=0; j<terms.size(); j++){			
			String term = terms.get(j);
			data[j] = new Object[]{term};
		}
		data[current] = new Object[]{""};
		Store store = new SimpleStore(new String[]{"name"}, data);
		store.load();	
		ComboBox queryCondition = new ComboBox();
		queryCondition.setForceSelection(true);
		queryCondition.setMinChars(1);
		queryCondition.setFieldLabel(label);
		queryCondition.setStore(store);
		queryCondition.setDisplayField("name");
		queryCondition.setMode(ComboBox.LOCAL);
		queryCondition.setWidth(250);
		return queryCondition;
	}
	
	 	 
	 FormPanel analysis;
	 static ComboBox xbox;
	 static ArrayList<ComboBox> ybox = new ArrayList<ComboBox>();
	 static ComboBox typeBox;
	 FieldSet fs_x;
	 FieldSet fs_y;
	 
	 private FormPanel analysisPanel(){
		 if(analysis!=null){
			 analysis.clear();
			 analysis.destroy();
		 }
			 
		 analysis = new FormPanel();
		 analysis.setId("analysis");
		 analysis.setFrame(true);
		 analysis.setBorder(false);
		 analysis.setLayout(new VerticalLayout(10));
		 
		 com.gwtext.client.widgets.Button button2 = new com.gwtext.client.widgets.Button("Show Analysis Gragh");
		 button2.addListener(new ButtonListenerAdapter(){
			 @Override
			 public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				 System.out.println("pressed.");
				 ComboBox typeBox = ReportQueryWidget.getTypeBox();
				 String type = typeBox.getText();
				 String x_axis_name = xbox.getText();
				 String y_axis_name = ybox.get(0).getText();
				
				 if(type.equals("") || x_axis_name.equals("") || y_axis_name.equals(""))
					 return;
				 
				 System.out.println("x: "+x_axis_name);
				 System.out.println("y: "+y_axis_name);
				 System.out.println("type: "+type);
				 
				 GridPanel grid = ReportQueryWidget.getGrid();
				 ColumnModel columnModel = grid.getColumnModel();
				 int columnnumber = columnModel.getColumnCount();
				 int x_idx = 0;
				 ArrayList<Integer> y_idx = new ArrayList<Integer>();
				 for(int i=0; i<columnnumber; i++){
					 if(columnModel.getColumnHeader(i).equals(x_axis_name)){
						 x_idx = i;
					 }
					 for(int j=0; j<ybox.size(); j++){
						 if(columnModel.getColumnHeader(i).equals(ybox.get(j).getText()))
						 {
							y_idx.add(i); 
						 }
					 }
				 }				 
				 
				 if(type.equals("Line Chart")){
					 
					 int min = 99999;
					 //generate the data 
					 Store store = grid.getStore();
					 Object[][] data =  new Object[store.getTotalCount()][1+y_idx.size()];
					 String[] field = store.getFields();
					 
					 for(int i=0; i<store.getTotalCount(); i++){
						 Record record = store.getRecordAt(i);						 
						 data[i][0] = record.getAsString(field[x_idx]);
						 for(int j=0; j<y_idx.size(); j++){
							 int idx = y_idx.get(j).intValue();						 
							 data[i][j+1] = record.getAsString(field[idx]);
							 double value = 0;
							 try{
								value = Double.parseDouble((String)data[i][j+1]);
							 }
							 catch(Exception ex)
							 {
								 MessageBox.alert("Y axis is not the digital value");
								 return;
							 }
							 if(value<min)
								 min = (int)value;
						 }
					 }		 
					 
					
					 LineChart chart = createLineChart("line_chart", xbox.getText(), ybox.get(0).getText(), xbox.getText(), 
						                                             ybox.get(0).getText(), data, min);
					 g_analysis = new Panel("Chart Show");
					 g_analysis.setSize(750, 400);
					 g_analysis.setClosable(true);
					 result.add(g_analysis);
					 result.activate(1);
					 g_analysis.add(chart);
					 g_analysis.doLayout();
					System.out.println("line chart draw ok.");
				 }
				 else if(type.equals("Pie Chart")){
					 System.out.println("to draw a pie chart");
					//generate the data 
					 Store store = grid.getStore();
					 final Object[][] data =  new Object[store.getTotalCount()][3];
					 String[] field = store.getFields();
					 if(y_idx.size()>1)
					 {
						 MessageBox.alert("Only one y axis is allowed in pie chart.");
					 }
					 
					 for(int i=0; i<store.getTotalCount(); i++){
						 Record record = store.getRecordAt(i);						 
						 data[i][0] = record.getAsString(field[x_idx]);
						 //for pie chart, only one y axis is allowed
						 data[i][1] = record.getAsString(field[y_idx.get(0)]);
						 //color
						 data[i][2] = getRandomColor(i);
					 }					 
					 
					 System.out.println("prepare data ok.");
					 PieChartExt chart = createPieChart("pie_chart", xbox.getText(), ybox.get(0).getText(), "color", data);
					 
					 ColumnConfig xConfig = new ColumnConfig(xbox.getText(), xbox.getText(), 100, true); 
				     ColumnConfig yConfig = new ColumnConfig(ybox.get(0).getText(), ybox.get(0).getText(), 100, true);
				     ColumnConfig legendConfig = new ColumnConfig("color", "color", 100, true);   
				        legendConfig.setRenderer(new Renderer() {   
				            public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store recordStore) {   
				                cellMetadata.setHtmlAttribute("style=\"background:" + value + ";\"");   
				                return "";   
				            }   
				        });
				        ColumnModel cModel = new ColumnModel(new ColumnConfig[]{   
				                xConfig,   
				                yConfig,   
				                legendConfig   
				        });   
				        
				        MemoryProxy proxy = new MemoryProxy(data); 
					    RecordDef recordDef = new RecordDef(   
				                new FieldDef[]{   
				                        new StringFieldDef(xbox.getText()),   
				                        new IntegerFieldDef(ybox.get(0).getText()),   
				                        new StringFieldDef("color")   
				                }   
				        );   
					    ArrayReader reader = new ArrayReader(recordDef);
					    final Store recordStore = new Store(proxy, reader);   
				        recordStore.load();   
				        
				        EditorGridPanel resultGrid = new EditorGridPanel();   
				        resultGrid.setStore(recordStore);   
				        resultGrid.setClicksToEdit(1);   
				        resultGrid.setColumnModel(cModel);   
				        resultGrid.setWidth(330);
				        
				     g_analysis = new Panel("Chart Show");
					 g_analysis.setSize(750, 400);
					 g_analysis.setClosable(true);
					 result.add(g_analysis);
					 result.activate(1);
					 g_analysis.clear();
					 
					 Panel out = new Panel();
					 out.setSize(730, 380);
					 out.setFrame(false);
					 out.setBorder(false);
					 out.setLayout(new HorizontalLayout(10));
					 out.add(chart);
					 out.add(resultGrid);
					 
					 g_analysis.add(out);
					 g_analysis.doLayout();
					 System.out.println("pie chart draw ok.");
				 }
				 else if(type.equals("Column Chart")){
					 Store store = grid.getStore();
					 Object[][] data =  new Object[store.getTotalCount()][1+y_idx.size()];
					 String[] field = store.getFields();
					 
					 for(int i=0; i<store.getTotalCount(); i++){
						 Record record = store.getRecordAt(i);						 
						 data[i][0] = record.getAsString(field[x_idx]);
						 for(int j=0; j<y_idx.size(); j++){
							 int idx = y_idx.get(j).intValue();						 
							 data[i][j+1] = record.getAsString(field[idx]);
							 try{
								Integer.parseInt((String)data[i][j+1]);
							 }
							 catch(Exception ex)
							 {
								 MessageBox.alert("Y axis is not the digital value");
								 return;
							 }
						 }
					 }
					 ArrayList<String> names = new ArrayList<String>();
					 for(int i=0; i<ybox.size(); i++){
						 names.add(ybox.get(i).getText());				
					 }
					 ColumnChart chart = createColumnChart(xbox.getText(), names, data);
					 
				     ColumnConfig[] yConfig = new ColumnConfig[ybox.size()+1];
				     yConfig[0] = new ColumnConfig(xbox.getText(), xbox.getText(), 100, true); 
				     for(int i=0; i<yConfig.length-1; i++){
				    	 yConfig[i+1] = new ColumnConfig(ybox.get(i).getText(), ybox.get(i).getText(), 100, true); 
				     }
					
				     MemoryProxy proxy = new MemoryProxy(data);
					 FieldDef[] fieldDef = new FieldDef[ybox.size()+1];
					 fieldDef[0] = new StringFieldDef(xbox.getText());
					 for(int i=0; i<ybox.size(); i++){
					  	fieldDef[i+1] = new IntegerFieldDef(ybox.get(i).getText()); 
					 }
					    	
					 RecordDef recordDef = new RecordDef(fieldDef);  
			         ArrayReader reader = new ArrayReader(recordDef);   
				     Store resultStore = new Store(proxy, reader);   
				     resultStore.load();
				     
			         ColumnModel cModel = new ColumnModel(yConfig);   
			         EditorGridPanel resultGrid = new EditorGridPanel();   
				     resultGrid.setStore(resultStore);   
				     resultGrid.setClicksToEdit(1);   
				     resultGrid.setColumnModel(cModel);   
				     resultGrid.setWidth(300);
					 
				     g_analysis = new Panel("Chart Show");
				     g_analysis.setSize(750, 400);
					 g_analysis.setClosable(true);
					 result.add(g_analysis);
					 result.activate(1);
					 g_analysis.clear();
					 g_analysis.add(chart);
					 g_analysis.add(resultGrid);
					 g_analysis.doLayout();
					 System.out.println("column chart draw ok.");
				 }
				 window_analysis.close();					
			 }
		 });
		 
		 analysis.setSize(350, 350);
		 analysis.setPaddings(25);
		 analysis.setFrame(true);
		 analysis.setBorder(false);	
		 
		 
		 FieldSet fs_x = new FieldSet("X Axis");
		 fs_x.setLayout(new HorizontalLayout(10));
		 fs_x.setId("fs_g");
		 fs_x.setBorder(true);
		 fs_x.setFrame(false);
		 fs_x.setWidth(250);
		 fs_x.setPaddings(10, 25, 25, 10);
		 fs_x.setLabelWidth(200);
		 
		 FieldSet fs_y = new FieldSet("Y Axis");
		 fs_y.setLayout(new HorizontalLayout(10));
		 fs_y.setId("fs_g");
		 fs_y.setBorder(true);
		 fs_y.setFrame(false);
		 fs_y.setWidth(250);
		 fs_y.setPaddings(10, 25, 25, 10);
		 fs_y.setLabelWidth(200);
		 
		 FieldSet fs_t = new FieldSet("Graph Type");
		 fs_t.setLayout(new HorizontalLayout(10));
		 fs_t.setId("fs_g");
		 fs_t.setBorder(true);
		 fs_t.setFrame(false);
		 fs_t.setWidth(250);
		 fs_t.setPaddings(10, 25, 25, 10);
		 fs_t.setLabelWidth(200);		 
		
		 
		 ArrayList<String> graphType = new ArrayList<String>();
		 graphType.add("Line Chart");
		 graphType.add("Pie Chart");
		 graphType.add("Column Chart");
		 ComboBox typeBox = createComboBox(graphType);
		 typeBox.setEmptyText("Choose Graph Type");
		 ReportQueryWidget.typeBox = typeBox;		 
		 
		 ComboBox xBox = createComboBox(columns);
		 xbox = xBox;
		 ComboBox yBox = createComboBox(columns);
		 ybox.clear();
		 ybox.add(yBox);
		 
		 fs_x.add(xBox);
		 fs_y.add(yBox);
		 fs_t.add(typeBox);
		 analysis.add(fs_x);
		 analysis.add(fs_y);
		 analysis.add(fs_t);
		 analysis.addButton(button2);
		 return analysis;
	 }
	 
	 public ComboBox createComboBox(ArrayList<String> list){
			Object[][] data;
			data = new Object[100][];
			for(int j=0; j<list.size(); j++){	
				String col = list.get(j);
				data[j] = new Object[]{col};
			}
			Store store = new SimpleStore(new String[]{"name"}, data);
			store.load();	
			ComboBox axisCol = new ComboBox();
			axisCol.setForceSelection(true);
			axisCol.setMinChars(1);
			axisCol.setFieldLabel("axis");
			axisCol.setStore(store);
			axisCol.setDisplayField("name");
			axisCol.setMode(ComboBox.LOCAL);
			axisCol.setEmptyText("Select A Column");
			axisCol.setWidth(160);
			axisCol.setHideLabel(false);	
			axisCol.setEditable(false);
			return axisCol;
		}
	 
	 private static Store getStore(){
		 Object[][] data;
			data = new Object[100][];
			for(int j=0; j<columns.size(); j++){	
				String col = columns.get(j);
				data[j] = new Object[]{col};
			}
			Store store = new SimpleStore(new String[]{"name"}, data);
			store.load();
			return store;
	 }
	 
	 public static ComboBox getTypeBox(){
		 return typeBox;
	 }
	 
	 public static ComboBox getXAxis(){
		 return xbox;
	 }
	 
	 public static ArrayList<ComboBox> getYAxis(){
		 return ybox;
	 }
	 
	 public static GridPanel getGrid(){
		 return grid;
	 }
	
	 public static LineChart createLineChart(String title, String xField, String yField,
				String xLabel, String yLabel, Object[][] data, int min) {
			final Store store = new SimpleStore(new String[] { xField, yField },
					data);
			store.load();

			NumericAxis currencyAxis = new NumericAxis();
			currencyAxis.setMinimum(min);

			SeriesDefY[] seriesDef = new SeriesDefY[] { new SeriesDefY(yLabel,
					yField) };

			LineChart chart = new LineChart();
			chart.setTitle(title);
			chart.setWMode("transparent");
			chart.setStore(store);
			chart.setSeries(seriesDef);
			chart.setXField(xField);

			chart.setYAxis(currencyAxis);
			chart.setDataTipFunction("getDataTipText");
			chart.setExpressInstall("js/yui/assets/expressinstall.swf");
			chart.setWidth(700);
			chart.setHeight(200);			
		
			return chart;
		}
	 
	 public static PieChartExt createPieChart(String title,
				String xLabel, String yLabel, String colorLabel, Object[][] data) {
		    MemoryProxy proxy = new MemoryProxy(data); 
		    RecordDef recordDef = new RecordDef(   
	                new FieldDef[]{   
	                        new StringFieldDef(xLabel),   
	                        new IntegerFieldDef(yLabel),   
	                        new StringFieldDef("colors")   
	                }   
	        );   
		    ArrayReader reader = new ArrayReader(recordDef);
		    final Store store = new Store(proxy, reader);   
		    store.load();   
	       
	        PieChartExt chart = new PieChartExt();   
	        chart.setTitle(title);   
	        chart.setWMode("transparent");   
	        chart.setStore(store);   
	        chart.setDataField(yLabel);  	        
	        chart.setCategoryField(xLabel);   
	        
	  
	        chart.setExpressInstall("js/yui/assets/expressinstall.swf");   
	        chart.setWidth(350);   
	        chart.setHeight(350);  
	        
	        return chart;
	 }
	 
	 public static ColumnChart createColumnChart(String xLabel, ArrayList<String> yLabel, Object[][] data){
		    MemoryProxy proxy = new MemoryProxy(data);
		    FieldDef[] fieldDef = new FieldDef[yLabel.size()+1];
		    fieldDef[0] = new StringFieldDef(xLabel);
		    for(int i=0; i<yLabel.size(); i++){
		    	fieldDef[i+1] = new IntegerFieldDef(yLabel.get(i)); 
		    }
		    	
		    RecordDef recordDef = new RecordDef(fieldDef);  
		    
		    SeriesDefY[] seriesDef = new SeriesDefY[yLabel.size()];
		    
		    for(int i=0; i<yLabel.size(); i++){
		    	GenericConfig myStyle = new GenericConfig();
		    	myStyle = new GenericConfig(); 
		    	myStyle.setProperty("image", "images/tube.png"); 
		    	myStyle.setProperty("mode", "no-repeat");   
		    	myStyle.setProperty("color", getRandomColor(i));
		    	myStyle.setProperty("size", 40);   
		    	seriesDef[i] = new SeriesDefY(yLabel.get(i), yLabel.get(i), myStyle);   
		    }
	  
	        ArrayReader reader = new ArrayReader(recordDef);   
	        final Store store = new Store(proxy, reader);   
	        store.load();
		    ColumnChart chart = new ColumnChart();   
	        chart.setWMode("transparent");   
	        chart.setStore(store);   
	        chart.setSeries(seriesDef);   
	        chart.setXField(xLabel);   
	        
	        chart.setExpressInstall("js/yui/assets/expressinstall.swf");   
	        chart.setWidth(700);   
	        chart.setHeight(200);
	        chart.setTitle("Column Chart");
	        return chart;
	 }
	 
	 public static String getRandomColor(int idx){
		 String[] colors = {"#00b8bf", "#8dd5e7", "#c0fff6", "#ffa928", "#edff9f", "#D00050",
				            "#c6c6c6", "#c3eafb", "#fcffad"};
		 
		 if(idx < colors.length)
			 return colors[idx];
		 int r = Random.nextInt(256);
		 int g = Random.nextInt(256);
		 int b = Random.nextInt(256);
		 String R = Integer.toHexString(r);
		 String G = Integer.toHexString(g);
		 String B = Integer.toHexString(b);
		 return "#"+R+G+B;
		 
	 }   
}