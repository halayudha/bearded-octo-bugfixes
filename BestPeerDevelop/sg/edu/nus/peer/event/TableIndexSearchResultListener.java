/**
 * Created on Sep 3, 2008
 */
package sg.edu.nus.peer.event;

import sg.edu.nus.bestpeer.queryprocessing.Win32;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.TableSearchResultBody;

/**
 * @author David Jiang
 * 
 */
public class TableIndexSearchResultListener extends ActionAdapter {

	public TableIndexSearchResultListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		TableSearchResultBody body = (TableSearchResultBody) msg.getBody();

		Win32.Event event = Win32.OpenEvent(body.getEventName());
		synchronized (event) {
			if (event != null)
				event.setEventData(body.getTableOwners());
		}
		System.out.println("send notification!");
		Win32.SetEvent(event); // Notify all requesters
	}

	public boolean isConsumed(Message message) throws EventHandleException {
		if (message.getHead().getMsgType() == MsgType.TABLE_INDEX_SEARCH_RESULT
				.getValue())
			return true;
		return false;
	}
}
