/*
 * @(#) SPLBGetLoadInfoReplyListener.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPLBGetLoadInfoReplyBody;

/**
 * Implement a listener for processing SP_LB_GET_LOAD_INFO_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLBGetLoadInfoReplyListener extends ActionAdapter {

	public SPLBGetLoadInfoReplyListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPLBGetLoadInfoReplyBody body = (SPLBGetLoadInfoReplyBody) msg
					.getBody();

			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			if (!body.getDirection()) {
				treeNode.setLNElement(body.getNumOfElement());
				treeNode.setLNOrder(body.getOrder());
				if (treeNode.getRNElement() != -2) {
					new SPLoadBalance(serverpeer, treeNode).balanceLoad();
				}
			} else {
				treeNode.setRNElement(body.getNumOfElement());
				treeNode.setRNOrder(body.getOrder());
				if (treeNode.getRNElement() != -2) {
					new SPLoadBalance(serverpeer, treeNode).balanceLoad();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"SP_LB_GET_LOAD_INFO_REPLY operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_LB_GET_LOAD_INFO_REPLY
				.getValue())
			return true;
		return false;
	}

}