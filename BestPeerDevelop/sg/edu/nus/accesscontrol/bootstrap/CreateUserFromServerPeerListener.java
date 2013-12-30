package sg.edu.nus.accesscontrol.bootstrap;

import javax.swing.JOptionPane;

import sg.edu.nus.accesscontrol.normalpeer.MsgBodyUserToBootstrap;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.event.ActionAdapter;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;

/**
 * Bootstrap-peer's listener for created-user acount from peers.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class CreateUserFromServerPeerListener extends ActionAdapter {

	public CreateUserFromServerPeerListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		MsgBodyUserToBootstrap body = (MsgBodyUserToBootstrap) msg.getBody();
		String userName = body.getUserName();
		String userDesc = body.getUserDesc();
		String pwd = body.getPasswd();
		String sender = body.getSender();

		MsgBodyUserToPeer sendBody = new MsgBodyUserToPeer(userName, userDesc,
				pwd, sender);

		broadCastUserInfoToPeer(sendBody);

		// TODO : store into database
		// UtilAccessControl.showMessage(gui,
		// "New user from Server Peer: "+sender+"\n"+userName+"\n"+userDesc+"\n"+pwd);

	}

	private void broadCastUserInfoToPeer(MsgBodyUserToPeer body) {
		PeerMaintainer maintainer = PeerMaintainer.getInstance();

		PeerInfo[] servers = maintainer.getServers();

		boolean error = false;

		for (int i = 0; i < servers.length; i++) {
			try {
				Head head = new Head(
						MsgType.ACCESS_CONTROL_BOOTSTRAP_USER_UPDATE.getValue());
				Message message = new Message(head, body);
				gui.peer().sendMessage(
						new PhysicalInfo(servers[i].getInetAddress(),
								servers[i].getPort()), message);
			} catch (Exception e) {
				LogManager.LogException(
						"Exception while broadcasting user infomation to "
								+ servers[i].getInetAddress() + ":"
								+ servers[i].getPort(), e);

				error = true;
			}
		}

		if (error) {
			JOptionPane.showMessageDialog(null,
					"Not all normal can receive new user info!");
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ACCESS_CONTROL_NORMAL_PEER_USER_UPDATE
				.getValue())
			return true;
		return false;
	}
}