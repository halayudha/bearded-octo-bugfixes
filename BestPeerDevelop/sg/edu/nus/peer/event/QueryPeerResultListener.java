/**
 * Created on Sep 4, 2008
 */
package sg.edu.nus.peer.event;

import sg.edu.nus.bestpeer.queryprocessing.RemoteTableInfo;
import sg.edu.nus.bestpeer.queryprocessing.Win32;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.QueryPeerResultBody;
import sg.edu.nus.util.NameUtil;

/**
 * @author David Jiang
 *
 */
public class QueryPeerResultListener extends ActionAdapter {

	public QueryPeerResultListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg) {
		QueryPeerResultBody resultBody = (QueryPeerResultBody) msg.getBody();
		RemoteTableInfo tableInfo = new RemoteTableInfo();
		tableInfo.setRemoteTableHost(resultBody.getRemoteTableHost());
		tableInfo.setRemoteTableName(resultBody.getRemoteTableName());

		// Notify the query sender
		String sql = resultBody.getQueryString();
		Win32.Event event = Win32.OpenEvent(NameUtil.queryEventName(sql));
		if (event != null) {
			synchronized (event) {
				event.addEventData(tableInfo);
				event.decWaitCounts();
			}
			Win32.SetEvent(event);
		}
	}

	public boolean isConsumed(Message message) throws EventHandleException {
		if (message.getHead().getMsgType() == MsgType.QUERY_PEER_RESULT
				.getValue())
			return true;
		return false;
	}

}
