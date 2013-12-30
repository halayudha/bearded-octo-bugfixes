/**
 * Created on Apr 24, 2009
 */
package sg.edu.nus.ui.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Yueguo Chen
 *
 */
public class PasswordDialog extends DialogBox {
	public static interface I18nConstants extends Constants {
		String passwordDlgTitle();
		String passwordDlgContent();
		String passwordCloseButton();
	}
	public PasswordDialog() {
		setText(BestPeerDesktop.constants.passwordDlgTitle());
		VerticalPanel outer = new VerticalPanel();
		
		HTML text = new HTML(BestPeerDesktop.constants.passwordDlgContent());
		text.setStyleName(".desktop-AboutText");
		outer.add(text);
		
		Button closeButton = 
			new Button(BestPeerDesktop.constants.passwordCloseButton(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				hide();
			}
		});
		outer.add(closeButton);
		outer.setCellHorizontalAlignment(closeButton, HorizontalPanel.ALIGN_CENTER);
		
		setWidget(outer);
	}
	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		// TODO Auto-generated method stub
		switch (key) {
		case KeyCodes.KEY_ENTER:
		case KeyCodes.KEY_ESCAPE:
			hide();
			break;
		}
		return true;
	}	
}
