package sg.edu.nus.peer.event;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.util.MetaDataAccess;

/**
 * handling the login message from the web interface
 * @author wusai
 *
 */
public class SPWebLoginListener extends ActionAdapter {

	public SPWebLoginListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		// query the local database to get information
		// ServerPeer server = ((ServerPeer) gui.peer());
		// String user = server.getServerPeerAdminName();

		String[] tables = MetaDataAccess
				.metaGetTables(ServerPeer.conn_metabestpeerdb);
		ArrayList<String[]> colname = new ArrayList<String[]>();

		for (int i = 0; i < tables.length; i++) {
			String[][] columns = MetaDataAccess.metaGetColumns(
					ServerPeer.conn_metabestpeerdb, tables[i]);
			String[] cname = new String[columns.length];
			for (int j = 0; j < cname.length; j++) {
				cname[j] = columns[j][0];
			}
			colname.add(cname);
		}

		//

	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.WEB_LOGIN.getValue())
			return true;
		return false;
	}
}
