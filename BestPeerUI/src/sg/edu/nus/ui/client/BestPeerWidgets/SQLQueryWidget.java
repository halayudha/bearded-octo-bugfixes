package sg.edu.nus.ui.client.BestPeerWidgets;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ProgressBar;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtextux.client.data.PagingMemoryProxy;
import com.gwtext.client.core.EventObject; 
import com.gwtext.client.core.Function;

import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;
/**
 * 
 * @author Wu Sai
 *
 */
public class SQLQueryWidget extends BestPeerPanel implements ClickHandler, Callback{
	
	private final GridPanel grid = new GridPanel();  

	private QueryDialog queryLog =  null;
	
	private final VerticalSplitPanel outer = new VerticalSplitPanel();
	
	private TextArea query;
	
	private Store store ;
	
	public Button submit = null;
    
   
	public static interface I18nConstants extends Constants {
		String querySQLInterface();
		String querySQLSubmit();
		String queryResult();
		String queryWaiting();
		String queryBeingProcessed();
		String queryComplete();
		String queryWarning();
		String queryWarning2();
	}
	
		
	
	/**
	   * A dialog box for displaying an error.
	   */
	private static class QueryDialog extends DialogBox implements ClickHandler{
	    private HTML body = new HTML("");
	    private final int timeout;
	    private final ProgressBar pbar = new ProgressBar();  
	    private final Button button;
	    private final  VerticalPanel panel = new VerticalPanel();
	  	    
	    public QueryDialog(int timeout) {
	    	
	      super(false, true);
	      setStylePrimaryName("SQLQueryDialog");
	      this.setText(BestPeerDesktop.constants.queryBeingProcessed());
	      panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	      this.timeout  = timeout;
	      button = new Button("OK", this);
	      button.addClickHandler(this);
	      pbar.setWidth(300);
	      pbar.setText(BestPeerDesktop.constants.queryWaiting());
	      pbar.reset();  
	      panel.add(body);
	      panel.add(pbar);
	      panel.add(button);	      
	      setWidget(panel);
	    }

	    public void showDialog(){		      
	    	  		     
		      panel.setSpacing(4);		      		      
		      button.setEnabled(false); 
		      pbar.reset();  
		       
		      pbar.wait(new WaitConfig() {
			  {
					setInterval(200);
					setDuration(timeout);
					setIncrement(15);
					setCallback(new Function() {
						public void execute() {
							button.setEnabled(true);
							pbar.setText(BestPeerDesktop.constants.queryComplete());
						}
					});
				}
			  }); 
		      
	    }
	    
	    public String getBody() {
	      return body.getHTML();
	    }

	    public void setBody(String html) {
	      body.setHTML(html);
	    }

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			this.hide();
		}
	    
	}	
	
	public SQLQueryWidget() {
		setStyleName("SQLQueryWidget");		
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
			grid.setWidth(800);  
			grid.setHeight(450);
			
			grid.setFrame(true);
			grid.setStripeRows(true);
			grid.setTitle(BestPeerDesktop.constants.queryResult());

			final PagingToolbar pagingToolbar = new PagingToolbar(store);
			pagingToolbar.setPageSize(10);
			pagingToolbar.setDisplayInfo(true);
			pagingToolbar
					.setDisplayMsg("Displaying companies {0} - {1} of {2}");
			pagingToolbar.setEmptyMsg("No records to display");

			grid.setBottomToolbar(pagingToolbar);
		}
		else {
			grid.reconfigure(store, columnModel);
			PagingToolbar pagingBar = (PagingToolbar)grid.getBottomToolbar();
			pagingBar.unbind(pagingBar.getStore());
			pagingBar.bind(store);
		}
		store.load(0, 10);		
	 }	
	

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Object sender = event.getSource();
		if(sender instanceof Button)
		{
			Button clicked = (Button)sender;
			if(clicked.getText().equals(submit.getText()))
			{
				queryLog = new QueryDialog(20000);
				queryLog.center();
				queryLog.showDialog();
				
				JSONObject params = new JSONObject();
				
				params.put("username", new JSONString(LoginUI.getUserName()) );

				params.put("sql", new JSONString(query.getText()));
				new RPCCall().invoke("SQLService", params, this);
			}
		}
	}

	private Panel sqlPanel = null;
	@Override
	public Panel getViewPanel() {
		if (sqlPanel == null) {
			sqlPanel = new Panel();
			sqlPanel.setTitle(BestPeerDesktop.constants.querySQLInterface());
			sqlPanel.setLayout(new VerticalLayout(15));
			
			VerticalPanel layout = new VerticalPanel();
			layout.setWidth("800px");
			layout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			submit = new Button(BestPeerDesktop.constants.querySQLSubmit());
			submit.addClickHandler(this);
			query = new TextArea();
			query.setCharacterWidth(95);
			query.setVisibleLines(3);
			layout.add(query);
			layout.add(submit);
			sqlPanel.add(layout);

			String[] columns = { "company", "price", "change", "% change", "update",
					"industry" };
			int[] type = { 12, 8, 8, 8, 91, 12 };

			this.initTable(columns, type, getCompanyData());

			sqlPanel.add(grid);
		}
		return sqlPanel;
	}
	
	
	private Object[][] getCompanyData() {
		return new Object[][] {
				new Object[] { "3m Co", new Double(71.72), new Double(0.02),
						new Double(0.03), "9/1 12:00am", "MMM", "Manufacturing" },
				new Object[] { "Alcoa Inc", new Double(29.01),
						new Double(0.42), new Double(1.47), "9/1 12:00am",
						"AA", "Manufacturing" },
				new Object[] { "Altria Group Inc", new Double(83.81),
						new Double(0.28), new Double(0.34), "9/1 12:00am",
						"MO", "Manufacturing" },
				new Object[] { "American Express Company", new Double(52.55),
						new Double(0.01), new Double(0.02), "9/1 12:00am",
						"AXP", "Finance" },
				new Object[] { "American International Group, Inc.",
						new Double(64.13), new Double(0.31), new Double(0.49),
						"9/1 12:00am", "AIG", "Services" },
				new Object[] { "AT&T Inc.", new Double(31.61),
						new Double(-0.48), new Double(-1.54), "9/1 12:00am",
						"T", "Services" },
				new Object[] { "Boeing Co.", new Double(75.43),
						new Double(0.53), new Double(0.71), "9/1 12:00am",
						"BA", "Manufacturing" },
				new Object[] { "Caterpillar Inc.", new Double(67.27),
						new Double(0.92), new Double(1.39), "9/1 12:00am",
						"CAT", "Services" },
				new Object[] { "Citigroup, Inc.", new Double(49.37),
						new Double(0.02), new Double(0.04), "9/1 12:00am", "C",
						"Finance" },
				new Object[] { "E.I. du Pont de Nemours and Company",
						new Double(40.48), new Double(0.51), new Double(1.28),
						"9/1 12:00am", "DD", "Manufacturing" } };
	}


	@Override
	public void onFailure() {
		// TODO Auto-generated method stub
		this.queryLog.hide();
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
		this.queryLog.hide();
		
		String status = ((JSONString)resultSet.get("status")).stringValue();
		if(status.equals("false"))
		{
			MessageBox.show(new MessageBoxConfig() {
				{
					setMsg(BestPeerDesktop.constants.queryWarning2());
					
					setWidth(300);				
				}
			});
		}
		else{
			 JSONArray namelist = (JSONArray)resultSet.get("column_name");
			 int size = namelist.size();
			 String[] column = new String[size];
			 for(int i=0; i<size; i++)
			 {
				 column[i] = ((JSONString)namelist.get(i)).stringValue();
			 }
			 JSONArray typelist = (JSONArray)resultSet.get("column_type");
			 int[] type = new int[size];
			 for(int i=0; i<size; i++)
			 {
				type[i] = (int)((JSONNumber)typelist.get(i)).doubleValue();
			 }
			 int recordNumber = (int)((JSONNumber)resultSet.get("column_number")).doubleValue();
			 
			 Object[][] record = new String[recordNumber][size];
			 for(int i=0; i<recordNumber; i++)
			 {
				 String listname = "list" + i;
				 JSONArray nextlist = (JSONArray)resultSet.get(listname);
				 for(int j=0; j<size; j++)
				 {
					 record[i][j] =  ((JSONString)nextlist.get(j)).stringValue();
				 }
			 }
			 
			 this.initTable(column, type, record);
			 this.sqlPanel.doLayout();
		}
	}
}
