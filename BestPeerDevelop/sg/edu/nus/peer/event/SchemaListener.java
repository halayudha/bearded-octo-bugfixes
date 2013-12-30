package sg.edu.nus.peer.event;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SchemaBody;
import sg.edu.nus.util.MetaDataAccess;

public class SchemaListener extends ActionAdapter {

	public SchemaListener(AbstractMainFrame gui) {
		super(gui);
	}

	@Override
	public void actionPerformed(PhysicalInfo dest, Message message)
			throws EventHandleException {
		if (gui.peer().getPeerType() == PeerType.BOOTSTRAP.getValue()) {
			LogManager.LogException("Wrong peer type", new Exception());
			return;
		}

		ServerPeer serverpeer = (ServerPeer) gui.peer();
		SchemaBody body = (SchemaBody) message.getBody();
		String schema = body.getSchema();

		// update metadata about schema
		MetaDataAccess.updateSchema(ServerPeer.conn_metabestpeerdb, body.getGlobalSchemas(), body.getGlobalSchemaColumns());

		// create global schema database
		serverpeer.performSchemaUpdate(schema);
	}

	@Override
	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SCHEMA.getValue())
			return true;
		return false;
	}

}
