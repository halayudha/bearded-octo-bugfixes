package sg.edu.nus.peer.event;

import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.EntrancePointDialog;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.Session;
import sg.edu.nus.peer.info.BoundaryValue;
import sg.edu.nus.peer.info.ContentInfo;
import sg.edu.nus.peer.info.IndexValue;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingTableInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.FeedbackBody;

/**
 * 
 * @author chensu
 * @version 2009-4-28
 *
 */
public class LoginAckListener extends ActionAdapter {

	public LoginAckListener(AbstractMainFrame gui) {
		super(gui);
	}

	@Override
	public void actionPerformed(PhysicalInfo dest, Message message)
			throws EventHandleException {
		if (gui.peer().getPeerType() == PeerType.BOOTSTRAP.getValue()) {
			LogManager.LogException("Wrong peer type", new Exception());
			return;
		}

		int msgType = message.getHead().getMsgType();
		boolean loginSuccess = true;

		ServerPeer serverpeer = (ServerPeer) gui.peer();
		ServerGUI servergui = (ServerGUI) gui;

		if (msgType == MsgType.LACK.getValue()) {
			// a list of online super peers returned
			FeedbackBody feedbackBody = (FeedbackBody) message.getBody();
			PeerInfo[] onlineSuperPeers = feedbackBody.getOnlineSuperPeers();

			serverpeer.initSession(serverpeer.userName, serverpeer.pwd);
			// window.dispose();

			// display entrance points. Now I just pass a null value into the
			// constructor, in the future, here should pass a list of online
			// super peers from message.
			//new EntrancePointDialog(servergui, onlineSuperPeers);
			
			/*
			 * select a peer to join
			 */
			randomSelectPeerToJoin(onlineSuperPeers);

		} else if (msgType == MsgType.UNIQUE_SUPER_PEER.getValue()) {
			// only the current peer is the unique super peer in the network,
			// and now is online

			serverpeer.initSession(serverpeer.userName, serverpeer.pwd);
			// window.dispose();

			// it is the root of the tree structure
			ContentInfo content = new ContentInfo(new BoundaryValue(
					IndexValue.MIN_KEY.getString(), Long.MIN_VALUE),
					new BoundaryValue(IndexValue.MAX_KEY.getString(),
							Long.MAX_VALUE), 10, new Vector<IndexValue>());
			TreeNode treeNode = new TreeNode(new LogicalInfo(0, 1), null, null,
					null, null, null, new RoutingTableInfo(0),
					new RoutingTableInfo(0), content, 0, 1);

			serverpeer.addListItem(treeNode);
			servergui.updatePane(treeNode);

			servergui.startService();
			servergui.setMenuEnable(true);
		} else if (msgType == MsgType.NO_ONLINE_SUPER_PEER.getValue()) {
			// no super peer is online
			JOptionPane.showMessageDialog(servergui, "No super peer online");
		} else if (msgType == MsgType.WRONG_PASS.getValue()) {
			// login failed due to incorrect password
			JOptionPane.showMessageDialog(servergui, "Wrong password");
			loginSuccess = false;
		} else if (msgType == MsgType.USER_NOT_EXIST.getValue()) {
			// login failed due to unexisting user
			JOptionPane.showMessageDialog(servergui, "User doesn't exist");
			loginSuccess = false;
		} else if (msgType == MsgType.RNCK.getValue()) {
			JOptionPane.showMessageDialog(servergui, "Register failed");
			loginSuccess = false;
		} else if (msgType == MsgType.USER_EXISTED.getValue()) {
			JOptionPane.showMessageDialog(servergui, "User has existed");
			loginSuccess = false;
		} else {
			// unknown message type
			JOptionPane.showMessageDialog(servergui,
					"Unknown response message type: " + msgType);
			LogManager.LogWarning("Unknown response message type: " + msgType);
			loginSuccess = false;
		}

		if (loginSuccess) {
			String title = servergui.getTitle();

			try {
				servergui.setTitle(title + " | PEER "
						+ Session.getInstance().getUserID() + " @ "
						+ serverpeer.getPhysicalInfo().getIP() + ":"
						+ serverpeer.getPhysicalInfo().getPort());
			} catch (Exception e) {
				e.printStackTrace();
			}

			servergui.updateInterface();

			servergui.peer().startTomcat();
		} else {
			servergui.restoreTitle();
			/**
			 * added by chensu, 2009-05-06
			 * enable the login button if login fails
			 */
			servergui.getOperatePanel().getLoginPanel().resetLoginBtn();
			serverpeer.stopEventManager();
		}
	}

	/**
	 * Random select a peer in peer list to join
	 *
	 */
	private void randomSelectPeerToJoin(PeerInfo[] onlineSuperPeers){
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		int peerID = rand.nextInt(onlineSuperPeers.length);
		String ip  = onlineSuperPeers[peerID].getInetAddress();
		int port   = onlineSuperPeers[peerID].getPort();

		ServerPeer serverpeer = (ServerPeer) gui.peer();
		ServerGUI servergui = (ServerGUI) gui;
		
		if (serverpeer.performJoinRequest(ip, port))
		{
			servergui.startService();
		}
	}

	@Override
	public boolean isConsumed(Message msg) throws EventHandleException {
		int msgType = msg.getHead().getMsgType();
		if (msgType == MsgType.LACK.getValue()
				|| msgType == MsgType.USER_NOT_EXIST.getValue()
				|| msgType == MsgType.WRONG_PASS.getValue()
				|| msgType == MsgType.UNIQUE_SUPER_PEER.getValue()
				|| msgType == MsgType.NO_ONLINE_SUPER_PEER.getValue()
				|| msgType == MsgType.USER_EXISTED.getValue()
				|| msgType == MsgType.RNCK.getValue())
			return true;
		return false;
	}
}
