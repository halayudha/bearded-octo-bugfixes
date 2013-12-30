/**
 * Created on Apr 24, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author David Jiang
 *
 */
public abstract class DeckWidgetBase extends LazyPanel implements 
		SelectionHandler<Integer>{

	private static final String DEFAULT_STYLE_NAME = "bp-DeckWidget";
	//private static String loadingImage;
	
	private DeckPanel deckPanel = null;
	private TabBar tabBar = null;
	
	public DeckWidgetBase () {
		tabBar = new TabBar();
	}
	
	public void add(Widget w, String tabText) {
		tabBar.addTab(tabText);
		deckPanel.add(w);
	}
	
	public TabBar getTabBar() {
		return tabBar;
	}
	
	protected abstract void onCreateWidget();
	
	public abstract Widget onInitialize();
	
	public void onInitializeComplete() { }
	
	@Override
	protected Widget createWidget() {
    deckPanel = new DeckPanel();

    setStyleName(DEFAULT_STYLE_NAME);

    // Add a tab handler
    tabBar.addSelectionHandler(this);
    
    final VerticalPanel vPanel = new VerticalPanel();

    onCreateWidget();
    
    // Initialize the showcase widget (if any) and add it to the page
    Widget widget = onInitialize();
    if (widget != null) {
      vPanel.add(widget);
    }
    onInitializeComplete();

    return deckPanel;
	}

	@Override
	protected void onLoad() {
		ensureWidget();

    // Select the first tab
    if (getTabBar().getTabCount() > 0) 
      tabBar.selectTab(0);	
	}
	
	@Override
	public void onSelection(SelectionEvent<Integer> event) {
		int tabIndex = event.getSelectedItem().intValue();
		deckPanel.showWidget(tabIndex);
	}
}
