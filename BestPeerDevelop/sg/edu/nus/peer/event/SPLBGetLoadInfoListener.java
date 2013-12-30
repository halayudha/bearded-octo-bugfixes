/*
 * @(#) SPLBGetLoadInfoListener.java 1.0 2006-2-22
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
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.SPLBGetLoadInfoBody;
import sg.edu.nus.protocol.body.SPLBGetLoadInfoReplyBody;

/**
 * Implement a listener for processing SP_LB_GET_LOAD_INFO message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLBGetLoadInfoListener extends ActionAdapter {

	public SPLBGetLoadInfoListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		Message result = null;
		Head thead = new Head();
		Body tbody = null;

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body */
			SPLBGetLoadInfoBody body = (SPLBGetLoadInfoBody) msg.getBody();

			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			int numOfElement = treeNode.getContent().getData().size();
			boolean direction = body.getDirection();

			// special case, send to its adjacent which then leaves and
			// transfers worklist back to itself
			if (treeNode.getLogicalInfo().equals(body.getLogicalSender())) {
				if (!direction) {
					treeNode.setRNElement(-1);
					if (treeNode.getRNElement() != -2) {
						new SPLoadBalance(serverpeer, treeNode).balanceLoad();
					}
				} else {
					treeNode.setRNElement(-1);
					if (treeNode.getRNElement() != -2) {
						new SPLoadBalance(serverpeer, treeNode).balanceLoad();
					}
				}
				return;
			}

			if (!body.getDirection()) {
				tbody = new SPLBGetLoadInfoReplyBody(serverpeer
						.getPhysicalInfo(), treeNode.getLogicalInfo(),
						numOfElement, treeNode.getContent().getOrder(), true,
						body.getLogicalSender());

				thead.setMsgType(MsgType.SP_LB_GET_LOAD_INFO_REPLY.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(body.getPhysicalSender(), result);
			} else {
				tbody = new SPLBGetLoadInfoReplyBody(serverpeer
						.getPhysicalInfo(), treeNode.getLogicalInfo(),
						numOfElement, treeNode.getContent().getOrder(), false,
						body.getLogicalSender());

				thead.setMsgType(MsgType.SP_LB_GET_LOAD_INFO_REPLY.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(body.getPhysicalSender(), result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"SP_LB_GET_LOAD_INFO operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_LB_GET_LOAD_INFO
				.getValue())
			return true;
		return false;
	}

}