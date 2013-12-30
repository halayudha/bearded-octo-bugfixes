package sg.edu.nus.ui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.Window;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.WindowMgr;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.Component;

import sg.edu.nus.ui.client.BestPeerWidgets.LoginUI;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * Load TopPanel, FunctionPanel, and UserActionPanel to desktop
 * 
 * @author David Jiang
 * @version 0.1 23-04-2009
 */
public class BestPeerDesktop 
	implements EntryPoint{//, ResizeHandler {
	
	/**
	 * Support i18n
	 */
	public static interface BPDesktopConstants extends Constants {
		String BestPeerTitle();
		String BestPeerPanelsTitle();
	}
	
	private static BestPeerDesktop desktop = null;
	
	// Load images
	public static final BestPeerUIImages images = GWT.create(BestPeerUIImages.class);
	
	// Load i18n strings
	public static final BestPeerUIConstants constants = GWT.create(BestPeerUIConstants.class);
	
	private TopPanel topPanel = new TopPanel();
	private BPDesktopConstants i18nConstants; 
	private ScreenManager screenManager;
	private static Panel lPanel;
	private static Panel rPanel;
	
	public static final BestPeerDesktop get() {
		return desktop;
	}

	public BestPeerDesktop () {
		i18nConstants = constants;
		desktop = this;
	}
	
	protected Panel createLeftPanel() {
		Toolbar toolbar = new Toolbar();
		toolbar.addFill();
		toolbar.addItem(new ToolbarTextItem("Select Theme"));
		toolbar.addSpacer();
		toolbar.addSpacer();
		toolbar.addField(new ThemeChanger());
		
		Panel leftPanel = new Panel();
		leftPanel.setTopToolbar(toolbar);
		leftPanel.setId("left-nav");
		leftPanel.setLayout(new FitLayout());
		leftPanel.setTitle(constants.BestPeerPanelsTitle());
		leftPanel.setWidth(210);
		leftPanel.setCollapsible(true);
		leftPanel.add(screenManager.getStackView());
		return leftPanel;
	}
	
	protected TabPanel createRightPanel() {
		TabPanel p = new TabPanel();
		p.setBodyBorder(false);
		p.setEnableTabScroll(true);
		p.setAutoScroll(true);
		p.setAutoDestroy(false);
		p.setActiveTab(0);
		p.addListener(new TabPanelListenerAdapter() {
			public boolean doBeforeTabChange(TabPanel src, Panel newPanel, Panel oldPanel){
				WindowMgr.hideAll();
				return true;
			}
			public void onRemove(Container self, Component component) {
				component.hide();
			}
		});
		p.setLayoutOnTabChange(true);
		p.setHeader(false);
		return p;
	}
	private Panel mainPanel = new Panel();
	private void loadBestPeerUI() {
		// Set application title in broswer's title bar
		Window.setTitle(i18nConstants.BestPeerTitle());
		
		//Panel mainPanel = new Panel();
		mainPanel.setLayout(new BorderLayout());
		
		// Add top panel
		BorderLayoutData topLayoutData = new BorderLayoutData(RegionPosition.NORTH);
		topLayoutData.setSplit(false);
		//Panel topPanel = createTopPanel();
		mainPanel.add(this.topPanel, topLayoutData);
				
		// Add right panel
		BorderLayoutData rightLayoutData = new BorderLayoutData(RegionPosition.CENTER);
		rightLayoutData.setMargins(new Margins(5, 5, 5, 5));
		Panel rightPanelWrapper = new Panel();
		rightPanelWrapper.setLayout(new FitLayout());
		rightPanelWrapper.setBorder(false);
		rightPanelWrapper.setBodyBorder(false);
		TabPanel rightPanel = createRightPanel();
		screenManager = new ScreenManager(rightPanel);
		// Build login/logout panel
		final Panel loginPanel = new Panel();
		loginPanel.setTitle(BestPeerDesktop.constants.loginTitle());		
		loginPanel.setLayout(new FitLayout());
		final LoginUI loginUI = new LoginUI(screenManager);
		loginPanel.add(loginUI);
		loginPanel.setIconCls("login-icon");
		rightPanel.add(loginPanel);
		rightPanelWrapper.add(rightPanel);
		mainPanel.add(rightPanelWrapper, rightLayoutData);
		
		// Add left panel
		BorderLayoutData leftLayoutData = new BorderLayoutData(RegionPosition.WEST);
		leftLayoutData.setMargins(new Margins(5, 5, 0, 5));
		leftLayoutData.setCMargins(new Margins(5, 5, 5, 5));
		leftLayoutData.setSplit(false);
		Panel leftPanel = createLeftPanel();
		mainPanel.add(leftPanel, leftLayoutData);
		
		lPanel = leftPanel;
		rPanel = rightPanel;
		
		// Add main panel to browser's client area. GWT-ext support resizing automatically
		@SuppressWarnings("unused")
		Viewport viewport = new Viewport(mainPanel);
		leftPanel.collapse();
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		loadBestPeerUI();
	}

    public static void showLeftPanel(){
    	lPanel.setCollapsed(false);
    }
    public static Panel getRightPanel(){
    	return rPanel;
    }
    
}
