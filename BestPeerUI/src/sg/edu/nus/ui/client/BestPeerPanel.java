/**
 * Created on Apr 26, 2009
 */
package sg.edu.nus.ui.client;

import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * @author David Jiang
 *
 */
public abstract class BestPeerPanel extends Panel {
	public abstract Panel getViewPanel();
	
	protected BestPeerPanel() {
		setTitle(getTitle());
		setClosable(true);
		setPaddings(20);
		setLayout(new FitLayout());
		setBorder(false);
		setAutoScroll(true);
		addListener( new PanelListenerAdapter() {
			public void onActivate(Panel panel) {
				BestPeerPanel.this.onActivate();
			}
		});
	}
	
	protected void onActivate() {
		getViewPanel();
	}

	@Override
	protected void afterRender() {
    Panel mainPanel = new Panel();
    mainPanel.setBorder(false);
    mainPanel.setLayout(new BorderLayout());

    Panel viewPanel = getViewPanel();
    viewPanel.setAutoScroll(true);
    viewPanel.setBorder(false);

    BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
    mainPanel.add(viewPanel, centerLayoutData);
    
    add(mainPanel);
	}
}
