/*
 * @(#) CheckImbalance.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.peer.request;

import java.util.Timer;
import java.util.TimerTask;

import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.RoutingTableInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.SPNotifyImbalanceBody;
import sg.edu.nus.util.PeerMath;

/**
 * Used for checking the status of the workload at peers.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class CheckImbalance {

	// private members
	private ServerPeer serverpeer;
	private Timer timer;

	public CheckImbalance(ServerPeer serverpeer, int seconds) {
		this.serverpeer = serverpeer;
		this.timer = new Timer();
		this.timer.schedule(new ReminderCheckImbalance(), 0, seconds * 1000);
	}

	class ReminderCheckImbalance extends TimerTask {
		public void run() {
			Head head = new Head();
			Body body = null;
			try {
				if (serverpeer.getListSize() == 1) {
					TreeNode treeNode = serverpeer.getListItem(0);
					if (((treeNode.getLeftChild() != null) || (treeNode
							.getRightChild() != null))
							&& (!treeNode.isNotifyImbalance())) {
						LogicalInfo missingNode;
						int missNodeLevel = treeNode.getLogicalInfo()
								.getLevel();
						int missNodeNumber;
						RoutingTableInfo leftRoutingTable = treeNode
								.getLeftRoutingTable();
						for (int i = 0; i < leftRoutingTable.getTableSize(); i++) {
							if (leftRoutingTable.getRoutingTableNode(i) == null) {
								missNodeNumber = treeNode.getLogicalInfo()
										.getNumber()
										- PeerMath.pow(2, i);
								missingNode = new LogicalInfo(missNodeLevel,
										missNodeNumber);
								treeNode.notifyImbalance(true);
								treeNode.setMissingNode(missingNode);
								body = new SPNotifyImbalanceBody(serverpeer
										.getPhysicalInfo(), treeNode
										.getLogicalInfo(), missingNode, true,
										treeNode.getParentNode()
												.getLogicalInfo());

								head.setMsgType(MsgType.SP_NOTIFY_IMBALANCE
										.getValue());
								Message message = new Message(head, body);
								serverpeer.sendMessage(treeNode.getParentNode()
										.getPhysicalInfo(), message);
								return;
							}
						}

						RoutingTableInfo rightRoutingTable = treeNode
								.getRightRoutingTable();
						for (int i = 0; i < rightRoutingTable.getTableSize(); i++) {
							if (rightRoutingTable.getRoutingTableNode(i) == null) {
								missNodeNumber = treeNode.getLogicalInfo()
										.getNumber()
										+ PeerMath.pow(2, i);
								missingNode = new LogicalInfo(missNodeLevel,
										missNodeNumber);
								treeNode.notifyImbalance(true);
								treeNode.setMissingNode(missingNode);

								body = new SPNotifyImbalanceBody(serverpeer
										.getPhysicalInfo(), treeNode
										.getLogicalInfo(), missingNode, false,
										treeNode.getParentNode()
												.getLogicalInfo());

								head.setMsgType(MsgType.SP_NOTIFY_IMBALANCE
										.getValue());
								Message message = new Message(head, body);
								serverpeer.sendMessage(treeNode.getParentNode()
										.getPhysicalInfo(), message);

								return;
							}
						}
					}
				}
			} catch (Exception e) {
				LogManager.LogException(
						"Exception happens in ReminderCheckImbalance", e);
			}
		}
	}
}
