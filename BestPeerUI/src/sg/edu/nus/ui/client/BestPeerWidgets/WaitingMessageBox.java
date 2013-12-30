/**
 * Created on May 5, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.WaitConfig;

/**
 * @author David Jiang
 *
 */
public class WaitingMessageBox {
	private static MessageBoxConfig config = new MessageBoxConfig();
	private static WaitConfig waitConfig = new WaitConfig();
	public static void setParams(String msg) {
		config.setMsg(msg);
		config.setWidth(200);
		config.setWait(true);
		waitConfig.setInterval(200);
		config.setWaitConfig(waitConfig);
	}
	public static void show(String msg) {
		setParams(msg);
		MessageBox.show(config);
	}
	
	public static void updateText(String message) {
		MessageBox.updateText(message);
	}
	
	public static void hide() {
		MessageBox.hide();
	}
}
