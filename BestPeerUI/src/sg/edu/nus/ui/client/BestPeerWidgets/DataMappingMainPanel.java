package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.ArrayList;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtext.client.core.EventObject;
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
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtextux.client.data.PagingMemoryProxy;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.form.ComboBox;

public class DataMappingMainPanel extends BestPeerPanel implements ClickHandler,
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
	private Panel termPanel;
	private GridPanel grid = new GridPanel();
	private Store store ;
	private ArrayList<String> localTerm = new ArrayList<String>();
	private ArrayList<String> globalTerm = new ArrayList<String>();
	
	
	public DataMappingMainPanel(){
		setTopToolbar(new Toolbar());
	}
	
	public ComboBox createComboBox(ArrayList<String> term, String termType){
		Object[][] data;
		data = new Object[100][];
		int current = 0;
		for(int j=0; j<term.size(); j++){			
			data[j] = new Object[]{term.get(j)};	
			current ++;
		}
		data[current] = new Object[]{""};
		Store store = new SimpleStore(new String[]{"name"}, data);
		store.load();	
		ComboBox termList = new ComboBox();
		termList.setForceSelection(true);
		termList.setMinChars(1);
		termList.setFieldLabel(termType);
		termList.setStore(store);
		termList.setDisplayField("name");
		termList.setMode(ComboBox.LOCAL);
		termList.setEmptyText("Select A Column");
		termList.setWidth(160);
		termList.setHideLabel(true);
		return termList;		
	}

	@Override
	public Panel getViewPanel() {
		// TODO Auto-generated method stub
		if(panel == null){
			panel = new Panel();
			panel.setLayout(new VerticalLayout(5));
			termPanel = new Panel();
			
			ToolbarButton createBtn = new ToolbarButton(BestPeerDesktop.constants.createDataMapping());
			createBtn.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
					new SchemaWindow();
				}
			});
			
			ToolbarButton deleteBtn = new ToolbarButton(BestPeerDesktop.constants.deleteDataMapping());
			deleteBtn.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
					System.out.println("delete");
				}
			});
			
			ToolbarButton viewBtn = new ToolbarButton(BestPeerDesktop.constants.viewDataMapping());
			viewBtn.addListener(new ButtonListenerAdapter(){
				@Override
				public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
					System.out.println("view");
				}
			});			
			
			this.getTopToolbar().addButton(createBtn);
			this.getTopToolbar().addSpacer();
			this.getTopToolbar().addSeparator();
			this.getTopToolbar().addButton(deleteBtn);
			this.getTopToolbar().addSpacer();
			this.getTopToolbar().addSeparator();
			this.getTopToolbar().addButton(viewBtn);
			
			termPanel.setLayout(new HorizontalLayout(5));
			FieldSet localFieldSet = new FieldSet();
			localFieldSet.setTitle(BestPeerDesktop.constants.localTerms());
			localFieldSet.setWidth("200px");
			localFieldSet.setHeight("150px");
			localFieldSet.add(createComboBox(localTerm, "Local Terms"));
			termPanel.add(localFieldSet);

			FieldSet globalFieldSet = new FieldSet();
			globalFieldSet.setTitle(BestPeerDesktop.constants.globalTerms());
			globalFieldSet.setWidth("200px");
			globalFieldSet.setHeight("150px");
			globalFieldSet.add(createComboBox(globalTerm, "Global Terms"));
			termPanel.add(globalFieldSet);
			
			String[] columns = { "	", "	", "	", "	", "	",
			"	" };
			int[] type = { 12, 12, 12, 12, 12, 12 };
			initTable(columns, type, getCompanyData());
			
			
		}
		
		panel.add(termPanel);
		panel.add(grid);
		return panel;
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReady(JSONObject resultSet) {
		// TODO Auto-generated method stub
		
	}
	
	public void initTable(String[] columns, int[] type, Object[][] data) {
	    // Set up the header row. It's one greater than the number of visible rows.
	    //
		int min = Math.min(columns.length, type.length);
		RecordDef recordDef = null;
		FieldDef[] fieldDef = new FieldDef[min];
		for(int i=0; i<min; i++)
		{
			switch(type[i]){
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
			grid.setHeight(350);
			
			grid.setFrame(true);
			grid.setStripeRows(true);
			grid.setTitle(BestPeerDesktop.constants.existingDataMapping());

			final PagingToolbar pagingToolbar = new PagingToolbar(store);
			pagingToolbar.setPageSize(10);
			pagingToolbar.setDisplayInfo(true);
			pagingToolbar
					.setDisplayMsg("Displaying EMR records {0} - {1} of {2}");
			pagingToolbar.setEmptyMsg("No records to display");
			
		}
		else {
			grid.reconfigure(store, columnModel);
			PagingToolbar pagingBar = (PagingToolbar)grid.getBottomToolbar();
			pagingBar.unbind(pagingBar.getStore());
			pagingBar.bind(store);
		}
		store.load(0, 10);
	}
	
	private Object[][] getCompanyData() {
		return new Object[][] {
				 };
	}
	
	
}