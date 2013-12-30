/**
 * Created on Sep 3, 2008
 */
package sg.edu.nus.peer.event;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.CacheDbIndexBody;

/**
 * @author VHTam
 * 
 */
public class CacheDbIndexListener extends ActionAdapter {

	public CacheDbIndexListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		
		CacheDbIndexBody body = (CacheDbIndexBody) msg.getBody();
		
		((ServerGUI)gui).cacheDbIndex = body.getCacheDbIndex();
		
	}

	public boolean isConsumed(Message message) throws EventHandleException {
		if (message.getHead().getMsgType() == MsgType.CACHE_DB_INDEX.getValue())
			return true;
		return false;
	}
}
