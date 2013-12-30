/*
 * @(#) SPLeaveFindReplaceListener.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPLeaveReplacementBody;

/**
 * Implement a listener for processing SP_LEAVE_FIND_REPLACEMENT_NODE message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLeaveReplacementListener extends ActionAdapter {

	public SPLeaveReplacementListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPLeaveReplacementBody body = (SPLeaveReplacementBody) msg
					.getBody();

			TreeNode treeNode = body.getTreeNode();
			treeNode.setContent(body.getContent());
			SPGeneralAction.saveData(body.getContent().getData());
			treeNode.setStatus(TreeNode.ACTIVE);
			treeNode.setRole(TreeNode.MASTER);
			treeNode.addCoOwnerList(body.getPhysicalSender());
			serverpeer.addListItem(treeNode);
			SPGeneralAction.updateRotateRoutingTable(serverpeer, treeNode);

			serverpeer.setActivateStablePosition(new ActivateStablePosition(
					serverpeer, treeNode, ServerPeer.TIME_TO_STABLE_POSITION));

			((ServerGUI) gui).updatePane(treeNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"Replace a super peer's position failure when it leaves network",
					e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_LEAVE_REPLACEMENT
				.getValue())
			return true;
		return false;
	}

}