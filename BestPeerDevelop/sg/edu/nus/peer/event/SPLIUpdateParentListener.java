/*
 * @(#) SPLIUpdateParentListener.java 1.0 2006-12-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPLIUpdateParentBody;

/**
 * Implement a listener for processing SP_LI_UPDATE_PARENT message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-12-10
 */

public class SPLIUpdateParentListener extends ActionAdapter {

	public SPLIUpdateParentListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPLIUpdateParentBody body = (SPLIUpdateParentBody) msg.getBody();

			/* get the correspondent tree node*/
			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			treeNode.getParentNode().setPhysicalInfo(body.getPhysicalParent());

		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException("Message processing fails", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_LI_UPDATE_PARENT
				.getValue())
			return true;
		return false;
	}

}