/*
 * @(#) SPLBRotationPullListener.java 1.0 2006-10-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPLBRotationPullBody;

/**
 * Implement a listener for processing SP_LB_ROTATION_PULL message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-10-10
 */

public class SPLBRotationPullListener extends ActionAdapter {

	public SPLBRotationPullListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPLBRotationPullBody body = (SPLBRotationPullBody) msg.getBody();

			TreeNode treeNode = body.getTreeNode();
			PhysicalInfo physicalSender = body.getPhysicalSender();
			boolean direction = body.getDirection();

			treeNode.setStatus(TreeNode.ACTIVE);
			treeNode.addCoOwnerList(physicalSender);

			// clean the previous slave if having
			SPGeneralAction.deleteTreeNode(serverpeer, treeNode);

			// add new node
			serverpeer.addListItem(treeNode);
			boolean result = SPGeneralAction.transferFakeNode(serverpeer,
					treeNode, direction, false, body.getPhysicalSender());

			if (result) {
				// ActivateStablePosition activateStablePosition =
				new ActivateStablePosition(serverpeer, treeNode,
						ServerPeer.TIME_TO_STABLE_POSITION);
			}
			// update GUI
			((ServerGUI) gui).updatePane(treeNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"SP_LB_ROTATION_PULL operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_LB_ROTATION_PULL
				.getValue())
			return true;
		return false;
	}

}