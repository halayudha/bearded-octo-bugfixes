/**
 * Created on Sep 23, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * @author David Jiang
 *
 */
public abstract class ChartPanelBase extends Panel {
	protected Panel panel;
	
	protected ChartPanelBase() {
		setTitle(getTitle());
		setClosable(true);
		setLayout(new FitLayout());
		setBorder(false);
		setAutoScroll(true);
		addListener(new PanelListenerAdapter() {
			@Override
			public void onActivate(Panel panel) {
				ChartPanelBase.this.onActivate();
			}
		});
	}
	
	protected void onActivate() {
		Panel viewPanel = getViewPanel();
		if (viewPanel instanceof Window) {
			((Window) viewPanel).show();
		}
	}
	
	
	@Override
	protected void afterRender() {
		addViewPanel();
	}
	
	private void addViewPanel() {
		Panel mainPanel = new Panel();
		mainPanel.setBorder(false);
		mainPanel.setLayout(new BorderLayout());
		
		Panel viewPanel = getViewPanel();
		if(viewPanel instanceof Window) {
			viewPanel.show();
			viewPanel = new Panel();
		}
		viewPanel.setAutoScroll(true);
    viewPanel.setBorder(false);
    BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
    mainPanel.add(viewPanel, centerLayoutData);

		add(mainPanel);
	}

	public void moveChartOut() {
	}

	public void moveChartIn() {
	}
	
	public abstract Panel getViewPanel();

	private static Store store;

	public static Store getStore() {
		if (store == null) {
			MemoryProxy proxy = new MemoryProxy(getData());

			RecordDef recordDef = new RecordDef(new FieldDef[] {
					new StringFieldDef("id"), new ObjectFieldDef("screen") });

			ArrayReader reader = new ArrayReader(0, recordDef);
			store = new Store(proxy, reader);
			store.load();
		}
		return store;
	}

	private static Object[][] getData() {
		return new Object[][] { new Object[] { "lineChart", new LineChartPanel() }, };
	}

}
