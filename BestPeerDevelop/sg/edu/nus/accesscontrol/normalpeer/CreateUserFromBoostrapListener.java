package sg.edu.nus.accesscontrol.normalpeer;

import javax.swing.JOptionPane;

import sg.edu.nus.accesscontrol.bootstrap.MsgBodyUserToPeer;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.event.ActionAdapter;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Normal peer's listener for create user from bootstrap and other peers.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class CreateUserFromBoostrapListener extends ActionAdapter {

	public CreateUserFromBoostrapListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		MsgBodyUserToPeer body = (MsgBodyUserToPeer) msg.getBody();
		String userName = body.getUserName();
		String userDesc = body.getUserDesc();
		String pwd = body.getPasswd();
		String sender = body.getSender();

		// TODO : store into database
		ServerPeer serverPeer = ((ServerGUI) gui).peer();
		String me = serverPeer.getServerPeerAdminName();
		if (sender.equals(me)) {
			return;// do not need to store this user, since it's me create this
		}

		MetaDataAccess.metaAddNewUser(ServerPeer.conn_metabestpeerdb, userName,
				userDesc, pwd);

		JOptionPane.showMessageDialog(gui,
				"A new user created from server peer " + sender + "\n");

		((ServerGUI) gui).updateInterface();

	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ACCESS_CONTROL_BOOTSTRAP_USER_UPDATE
				.getValue())
			return true;
		return false;
	}
}
