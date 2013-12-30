/*
 * @(#) SPLBRotateUpdateRTListener.java 1.0 2006-2-22
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
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.SPLBRotateUpdateRoutingTableBody;
import sg.edu.nus.protocol.body.SPLBRotateUpdateRoutingTableReplyBody;

/**
 * Implement a listener for processing SP_LB_ROTATE_UPDATE_ROUTING_TABLE message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLBRotateUpdateRTListener extends ActionAdapter {

	public SPLBRotateUpdateRTListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Head thead = new Head();
		Body tbody = null;

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPLBRotateUpdateRoutingTableBody body = (SPLBRotateUpdateRoutingTableBody) msg
					.getBody();

			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			// 1. update routing table
			int index = body.getIndex();
			RoutingItemInfo updatedNodeInfo = body.getInfoRequester();

			RoutingItemInfo tempInfo;
			boolean direction = body.getDirection();
			if (!direction) {
				tempInfo = treeNode.getLeftRoutingTable().getRoutingTableNode(
						index);
				if (tempInfo == null) {
					return;
				}
				treeNode.getLeftRoutingTable().setRoutingTableNode(
						updatedNodeInfo, index);
			} else {
				tempInfo = treeNode.getRightRoutingTable().getRoutingTableNode(
						index);
				if (tempInfo == null) {
					return;
				}
				treeNode.getRightRoutingTable().setRoutingTableNode(
						updatedNodeInfo, index);
			}

			// check existing knowledge

			RoutingItemInfo nodeInfo = new RoutingItemInfo(serverpeer
					.getPhysicalInfo(), treeNode.getLogicalInfo(), treeNode
					.getLeftChild(), treeNode.getRightChild(), treeNode
					.getContent().getMinValue(), treeNode.getContent()
					.getMaxValue());

			tbody = new SPLBRotateUpdateRoutingTableReplyBody(serverpeer
					.getPhysicalInfo(), treeNode.getLogicalInfo(), nodeInfo,
					index, !direction, updatedNodeInfo.getLogicalInfo());

			thead.setMsgType(MsgType.SP_LB_ROTATE_UPDATE_ROUTING_TABLE_REPLY
					.getValue());
			Message message = new Message(thead, tbody);
			serverpeer.sendMessage(updatedNodeInfo.getPhysicalInfo(), message);

			// update GUI
			((ServerGUI) gui).updatePane(treeNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"SP_LB_ROTATE_UPDATE_ROUTING_TABLE operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_LB_ROTATE_UPDATE_ROUTING_TABLE
				.getValue())
			return true;
		return false;
	}

}