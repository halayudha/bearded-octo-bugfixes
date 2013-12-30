/*
 * @(#) SPUpdateRoutetableDirectlyListener.java 1.0 2006-2-22
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
import sg.edu.nus.protocol.body.SPUpdateRoutingTableDirectlyBody;
import sg.edu.nus.protocol.body.SPUpdateRoutingTableReplyBody;

/**
 * Implement a listener for processing SP_UPDATE_ROUTING_TABLE_DIRECTLY message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPUpdateRouteTableDirectlyListener extends ActionAdapter {

	public SPUpdateRouteTableDirectlyListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Message result = null;
		Head thead = new Head();
		Body tbody = null;

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPUpdateRoutingTableDirectlyBody body = (SPUpdateRoutingTableDirectlyBody) msg
					.getBody();

			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			/* update routing table */
			int index = body.getIndex();
			boolean direction = body.getDirection();
			RoutingItemInfo updatedNodeInfo = body.getInfoRequester();
			if (!direction) {
				treeNode.getLeftRoutingTable().setRoutingTableNode(
						updatedNodeInfo, index);
			} else {
				treeNode.getRightRoutingTable().setRoutingTableNode(
						updatedNodeInfo, index);
			}

			/* reply if necessary */
			if (updatedNodeInfo != null) {
				RoutingItemInfo nodeInfo = new RoutingItemInfo(serverpeer
						.getPhysicalInfo(), treeNode.getLogicalInfo(), treeNode
						.getLeftChild(), treeNode.getRightChild(), treeNode
						.getContent().getMinValue(), treeNode.getContent()
						.getMaxValue());

				tbody = new SPUpdateRoutingTableReplyBody(serverpeer
						.getPhysicalInfo(), treeNode.getLogicalInfo(),
						nodeInfo, index, !direction, updatedNodeInfo
								.getLogicalInfo());

				thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_REPLY
						.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(updatedNodeInfo.getPhysicalInfo(),
						result);

				if (treeNode.isNotifyImbalance()
						&& (updatedNodeInfo.getLogicalInfo().equals(treeNode
								.getMissingNode()))) {
					treeNode.notifyImbalance(false);
					treeNode.setMissingNode(null);
				}
			}

			/* update GUI */
			((ServerGUI) gui).updatePane(treeNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"Super peer directly updates routing table failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_UPDATE_ROUTING_TABLE_DIRECTLY
				.getValue())
			return true;
		return false;
	}

}