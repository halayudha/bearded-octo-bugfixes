package sg.edu.nus.bestpeer.joinprocessing;

import java.io.IOException;
import java.sql.Connection;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.event.ActionAdapter;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;

/**
 * 
 * @author VHTam
 *
 */

public class TableStatSearchListener extends ActionAdapter {

	public TableStatSearchListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		Message result = null;
		Head thead = null;

		MsgBodyTableStatSearch ub = (MsgBodyTableStatSearch) msg.getBody();
		String tableName = ub.getTableName();

		Connection conn = ServerPeer.conn_metabestpeerdb;
		PartitionStatistics stat = new PartitionStatistics(tableName);
		stat.getStatFromMetaData(conn);

		thead = new Head(MsgType.TABLE_STAT_RESULT.getValue());

		Body tbody = new MsgBodyTableStatResult(stat);

		result = new Message(thead, tbody);

		/**
		 * @FIXME	use oos temporarily, need to remove oos
		 * @author chensu
		 */
		result.serialize(dest, ((MsgBodyTableStatSearch) (msg.getBody())).oos);
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.TABLE_STAT_SEARCH.getValue())
			return true;
		return false;
	}

}
