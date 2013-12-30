/**
 * Created on Apr 23, 2009
 */
package sg.edu.nus.ui.client;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * @author David Jiang
 *
 */
public class RightPanel extends Composite {
	public static final String DEFAULT_STYLE_NAME = "RightPanel";
	
	private RoundedPanel contentDecorator;
	private Grid contentLayout;
	private SimplePanel contentWrapper;
	private HorizontalPanel bottomPanel;
	
	public RightPanel() {
    // Setup the main layout widget
    FlowPanel layout = new FlowPanel();
    initWidget(layout);

    // Add the main menu
    bottomPanel = new HorizontalPanel();
    bottomPanel.setWidth("100%");
    bottomPanel.setSpacing(0);
    bottomPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
    layout.add(bottomPanel);

    // Setup the content layout
    contentLayout = new Grid(2, 1);
    contentLayout.setCellPadding(0);
    contentLayout.setCellSpacing(0);
    contentDecorator = new RoundedPanel(contentLayout, RoundedPanel.ALL, 4);
    contentDecorator.setBorderColor("#c1eec8");
    contentDecorator.addStyleName(DEFAULT_STYLE_NAME + "-content-decorator");
    bottomPanel.add(contentDecorator);
    CellFormatter formatter = contentLayout.getCellFormatter();

    // Add the content title
    setContentTitle(new HTML("Content"));
    formatter.setStyleName(0, 0, DEFAULT_STYLE_NAME + "-content-title");

    // Add the content wrapper
    contentWrapper = new SimplePanel();
    contentLayout.setWidget(1, 0, contentWrapper);
    formatter.setStyleName(1, 0, DEFAULT_STYLE_NAME + "-content-wrapper");
    setContent(null);
	}
	
	// Call this to display a Widget
  public void setContent(Widget content) {
    if (content == null) {
      contentWrapper.setWidget(new HTML("&nbsp;"));
    } else {
      contentWrapper.setWidget(content);
    }
  }

  public void setContentTitle(Widget title) {
    contentLayout.setWidget(0, 0, title);
  }
  
  /**
   * Adjusts the widget's size such that it fits within the window's client
   * area.
   */
  public void adjustSize(int windowWidth, int windowHeight) {
    int contentWidth = windowWidth - contentDecorator.getAbsoluteLeft() - 9;
    if (contentWidth < 1) {
    	contentWidth = 1;
    }
    int contentWidthInner = Math.max(contentWidth - 10, 1);
    bottomPanel.setCellWidth(contentDecorator, contentWidth + "px");
    contentLayout.getCellFormatter().setWidth(0, 0, contentWidthInner + "px");
    contentLayout.getCellFormatter().setWidth(1, 0, contentWidthInner + "px");
    int currentTop = contentLayout.getCellFormatter().getElement(1, 0).getAbsoluteTop();
    int contentHeight = windowHeight - currentTop - 10;
    if (contentHeight < 1) {
      contentHeight = 1;
    }
    contentLayout.getCellFormatter().setHeight(1, 0, contentHeight + "px");
  }

}
