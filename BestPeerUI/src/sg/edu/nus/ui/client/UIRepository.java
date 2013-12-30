/**
 * Created on Apr 26, 2009
 */
package sg.edu.nus.ui.client;

import sg.edu.nus.ui.client.BestPeerWidgets.AccessControlUI;
import sg.edu.nus.ui.client.BestPeerWidgets.DatabaseConfigureUI;
import sg.edu.nus.ui.client.BestPeerWidgets.DatabaseSchemaMappingUI;

import sg.edu.nus.ui.client.BestPeerWidgets.DataExportUI;
import sg.edu.nus.ui.client.BestPeerWidgets.DataMappingGrid;
import sg.edu.nus.ui.client.BestPeerWidgets.DataMappingMainPanel;
import sg.edu.nus.ui.client.BestPeerWidgets.FormQueryWidget;
import sg.edu.nus.ui.client.BestPeerWidgets.SQLQueryWidget;
import sg.edu.nus.ui.client.BestPeerWidgets.ReportQueryWidget;
import sg.edu.nus.ui.client.BestPeerWidgets.StatisticsUI;

import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;

/**
 * A simple PnP UI interface, use a config file in the future
 * 
 * @author David Jiang
 *
 */
public class UIRepository {
	private static Store store = null;
	private static ArrayReader reader = null;
	private static MemoryProxy proxy = null;
	public static Store getStore() {
		if(store == null) {
			proxy = new MemoryProxy(getData());
			
			RecordDef recordDef = new RecordDef(new FieldDef[] {
					new StringFieldDef("id"),
					new StringFieldDef("category"),
					new StringFieldDef("title"),
					new StringFieldDef("iconCls"),
					new ObjectFieldDef("uiobject")
			});
			
			reader = new ArrayReader(0, recordDef);
			store = new Store(proxy, reader);
			store.load();
		}
		return store;
	}
	
	
	/**
	 * Put all UI objects here
	 */
	private static Object[][] getData() {
		return new Object[][] {
				new Object[] {"datamapping", null, BestPeerDesktop.constants.dataMapping(), "query-panel-icon", null},
				new Object[] {"datamappingmainpanel", "datamapping", BestPeerDesktop.constants.dataMapping(), "form-query-icon", new DataMappingGrid()},
				new Object[] {"query-p", null, BestPeerDesktop.constants.queryBrowserBoxTitle(), "query-panel-icon", null},
				new Object[] {"formquery", "query-p", BestPeerDesktop.constants.queryFormInterface(), "form-query-icon", new FormQueryWidget()},
				new Object[] {"reportquery", "query-p", BestPeerDesktop.constants.reportQuery(), "report-query-icon", new ReportQueryWidget()},
				
		};
	}
	
	public static void disableUI(){
		
	}
	
}
