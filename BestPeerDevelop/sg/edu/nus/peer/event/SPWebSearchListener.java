package sg.edu.nus.peer.event;

import java.io.IOException;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.WebSearchBody;

/**
 * handles a search message from the web interface
 * @author wusai
 *
 */
public class SPWebSearchListener extends ActionAdapter {
	/**
	 * constructor
	 * @param gui  gui of the parent class
	 */
	public SPWebSearchListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		WebSearchBody searchBody = (WebSearchBody) msg.getBody();
		SelectExecutor selectExecutor = new SelectExecutor(searchBody
				.getSQLQuery());
		selectExecutor.setQueryID(ServerPeer.getGlobalQID());
		selectExecutor.start();

	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.WEB_SEARCH.getValue())
			return true;
		return false;
	}
}
