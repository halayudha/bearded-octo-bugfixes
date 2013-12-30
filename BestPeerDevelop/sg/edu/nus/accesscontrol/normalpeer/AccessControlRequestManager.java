package sg.edu.nus.accesscontrol.normalpeer;

import javax.swing.JOptionPane;

import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;

/**
 * Implement access control requests from normal peer to bootstrap peer.
 * 
 * @author VHTam
 */

public class AccessControlRequestManager {

	// private members
	private ServerGUI servergui;
	private ServerPeer serverpeer;

	/**
	 * Construct a request manager for server peer.
	 * 
	 * @param serverpeer
	 *            the handler of the <code>ServerPeer</code>
	 */
	public AccessControlRequestManager(ServerPeer serverpeer) {
		this.serverpeer = serverpeer;
		this.servergui = (ServerGUI) serverpeer.getMainFrame();
	}

	public void performAccessControlRequest(String user, String pwd, String ip,
			int port) {
		try {
			Head head = new Head(MsgType.ACCESS_CONTROL_ROLE_REQUEST.getValue());

			PhysicalInfo info = serverpeer.getPhysicalInfo();
			MsgBodyRoleUpdate body = new MsgBodyRoleUpdate(user, pwd, info
					.getIP(), info.getPort(), serverpeer.getPeerType());

			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);
		} catch (Exception e) {
			LogManager
					.LogException(
							"Fail to update Access control Role setting with bootstrap server",
							e);
			JOptionPane.showMessageDialog(servergui,
					"Cannot update Access control Role setting with Bootstrap");
		}
	}
}
