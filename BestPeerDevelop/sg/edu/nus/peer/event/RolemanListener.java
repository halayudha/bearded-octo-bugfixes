package sg.edu.nus.peer.event;

import sg.edu.nus.accesscontrol.RoleManagement;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.RolemanBody;

public class RolemanListener extends ActionAdapter {

	public RolemanListener(AbstractMainFrame gui) {
		super(gui);
	}

	@Override
	public void actionPerformed(PhysicalInfo dest, Message message)
			throws EventHandleException {
		if (gui.peer().getPeerType() == PeerType.BOOTSTRAP.getValue()) {
			LogManager.LogException("Wrong peer type", new Exception());
			return;
		}

		ServerGUI servergui = (ServerGUI) gui;

		RoleManagement roleManagement = ((RolemanBody) (message.getBody()))
				.getRoleman();
		roleManagement.storeToDB(ServerPeer.conn_metabestpeerdb);
		servergui.updateInterface();
	}

	@Override
	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ROLE_MAN.getValue())
			return true;
		return false;
	}

}
