/*
 * @(#) SPJoinForceForwardListener.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.ChildNodeInfo;
import sg.edu.nus.peer.info.ContentInfo;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPJoinForcedForwardBody;
import sg.edu.nus.util.PeerMath;

/**
 * Implement a listener for processing SP_JOIN_FORCED_FORWARD message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPJoinForceForwardListener extends ActionAdapter {

	public SPJoinForceForwardListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Message result = null;
		Head thead = new Head();

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPJoinForcedForwardBody body = (SPJoinForcedForwardBody) msg
					.getBody();

			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			boolean direction = body.getDirection();

			if (!direction) {
				if (!treeNode.getRightAdjacentNode().getLogicalInfo().equals(
						body.getLogicalSender())) {
					thead.setMsgType(MsgType.SP_JOIN_FORCED_FORWARD.getValue());
					result = new Message(thead, body);
					serverpeer.sendMessage(treeNode.getRightAdjacentNode()
							.getPhysicalInfo(), result);
					return;
				}
			} else {
				if (!treeNode.getLeftAdjacentNode().getLogicalInfo().equals(
						body.getLogicalSender())) {
					thead.setMsgType(MsgType.SP_JOIN_FORCED_FORWARD.getValue());
					result = new Message(thead, body);
					serverpeer.sendMessage(treeNode.getLeftAdjacentNode()
							.getPhysicalInfo(), result);
					return;
				}
			}

			ContentInfo content;
			ChildNodeInfo childNode;
			int childLevel;
			int childNumber;
			int numOfExpectedRTReply;
			if (treeNode.getLeftChild() == null) {
				childLevel = treeNode.getLogicalInfo().getLevel() + 1;
				childNumber = treeNode.getLogicalInfo().getNumber() * 2 - 1;
				childNode = new ChildNodeInfo(body.getPhysicalRequester(),
						new LogicalInfo(childLevel, childNumber));
				treeNode.setLeftChild(childNode);
				numOfExpectedRTReply = PeerMath.getNumberOfExpectedRTReply(
						true, treeNode.getRightChild() != null, treeNode
								.getLeftRoutingTable(), treeNode
								.getLeftRoutingTable());

				content = SPGeneralAction.splitData(treeNode, false, serverpeer
						.getOrder());
			} else {
				childLevel = treeNode.getLogicalInfo().getLevel() + 1;
				childNumber = treeNode.getLogicalInfo().getNumber() * 2;
				childNode = new ChildNodeInfo(body.getPhysicalRequester(),
						new LogicalInfo(childLevel, childNumber));
				treeNode.setRightChild(childNode);
				numOfExpectedRTReply = PeerMath.getNumberOfExpectedRTReply(
						false, false, treeNode.getLeftRoutingTable(), treeNode
								.getLeftRoutingTable());
				content = SPGeneralAction.splitData(treeNode, true, serverpeer
						.getOrder());
			}
			RoutingItemInfo nodeInfo = SPGeneralAction.doAccept(serverpeer,
					treeNode, body.getPhysicalRequester(), new LogicalInfo(
							childLevel, childNumber), content,
					numOfExpectedRTReply, false, false);

			SPGeneralAction.updateRoutingTable(serverpeer, treeNode, nodeInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"SP_JOIN_FORCED_FORWARD operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_JOIN_FORCED_FORWARD
				.getValue())
			return true;
		return false;
	}

}