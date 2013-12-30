/**
 * Created on Apr 23, 2009
 */
package sg.edu.nus.ui.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for BestPeer functions
 *  
 * @author David Jiang
 *
 */
public class LeftPanel extends Composite {
	
	/**
	 * Support i18n
	 */
	public static interface LeftPanelConstants extends Constants {
		String dbManager();
		String queryBrowserBoxTitle();
		String accessControlBoxTitle();
		String dataMapping();
	}
	private DecoratedStackPanel stackPanel = new DecoratedStackPanel();
	
	private void add(Widget widget, AbstractImagePrototype imageProto,
      String caption) {
    stackPanel.add(widget, createHeaderHTML(imageProto, caption), true);
  }
	
	public LeftPanel(BestPeerUIConstants i18nConstants) {
		add(new BestPeerDBManagerBox(i18nConstants), null, i18nConstants.dbManager());
		add(new BestPeerQueryBrowserBox(i18nConstants), null, BestPeerDesktop.constants.queryBrowserBoxTitle());
		add(new BestPeerAccessControlBox(), null, BestPeerDesktop.constants.accessControlBoxTitle());
		this.initWidget(stackPanel);
	}
	
	@Override
	protected void onLoad() {
		// Show LoginBox when the browser first loads page
		stackPanel.showStack(0);
	}	
	
	private String createHeaderHTML(AbstractImagePrototype imageProto,
      String caption) {
    String captionHTML = "<table class='caption' cellpadding='0' cellspacing='0'>"
        + "<tr><td class='lcaption'>"
        + "</td><td class='rcaption'><b style='white-space:nowrap'>"
        + caption
        + "</b></td></tr></table>";
    return captionHTML;
  }
}
