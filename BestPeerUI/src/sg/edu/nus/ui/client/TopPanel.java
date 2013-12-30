/**
 * Created on Apr 23, 2009
 */
package sg.edu.nus.ui.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author David Jiang
 *
 */
public class TopPanel extends Composite implements ClickHandler {
	public static interface Images extends ImageBundle {
		AbstractImagePrototype logo();
	}
	
	public static interface I18nConstants extends Constants {
		String topMessage();
		String about();
	}
	
	private HTML aboutLink = new HTML("<a href='javascript:;'>" + BestPeerDesktop.constants.about() + "</a>");
	BestPeerUIImages images;
	public TopPanel(){
		images = BestPeerDesktop.images;
		HorizontalPanel outer = new HorizontalPanel();
		VerticalPanel inner = new VerticalPanel();
		
		outer.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		inner.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		
		HorizontalPanel links = new HorizontalPanel();
		links.setSpacing(4);
		links.add(aboutLink);
		
		final Image logo = images.logo().createImage();
		outer.add(logo);
		outer.setCellHorizontalAlignment(logo, HorizontalPanel.ALIGN_LEFT);
		outer.add(inner);
		inner.add(new HTML("<b>" + BestPeerDesktop.constants.topMessage() + "</b>"));
		inner.add(links);
		
		aboutLink.addClickHandler(this);
		
		initWidget(outer);
		setStyleName("desktop-TopPanel");
		links.setStyleName("desktop-TopPanelLinks");
	}
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Object sender = event.getSource();
		if(sender == aboutLink){
			AboutDialog dlg = new AboutDialog();
			dlg.show();
			dlg.center();
		}
	}

}
