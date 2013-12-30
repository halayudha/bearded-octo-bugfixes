package sg.edu.nus.peer.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.BoundaryValue;
import sg.edu.nus.peer.info.LocalDataIndex;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ERPInsertDataIndexBody;
import sg.edu.nus.util.PeerMath;

/**
 * Implement a listener for processing ERP_INSERT_DATA_INDEX message.
 * 
 * @author Quang Hieu Vu, Wu Sai
 * @version 1.0 2008-05-28, Modified at 2008-09-29
 */

public class ERPInsertDataIndexListener extends ActionAdapter {

	public ERPInsertDataIndexListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Head head = new Head();

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body and the tree node */
			ERPInsertDataIndexBody body = (ERPInsertDataIndexBody) msg
					.getBody();
			TreeNode treeNode = serverpeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				TreeNode[] listOfTreeNodes = serverpeer.getTreeNodes();
				if (listOfTreeNodes.length == 0) {
					System.out
							.println("Tree node is null, do not process the message");
					return;
				} else
					treeNode = listOfTreeNodes[0];
			}

			/* insert data indices */
			PhysicalInfo physicalOwner = body.getPhysicalOwner();
			Vector<LocalDataIndex> listOfTerms = body.getListOfTerms();
			BoundaryValue minValue = treeNode.getContent().getMinValue();
			BoundaryValue maxValue = treeNode.getContent().getMaxValue();
			Vector<LocalDataIndex> leftIndices = new Vector<LocalDataIndex>();
			Vector<LocalDataIndex> rightIndices = new Vector<LocalDataIndex>();
			int termFormat = body.getTermFormat();

			for (int i = 0; i < listOfTerms.size(); i++) {
				LocalDataIndex dataIndex = listOfTerms.get(i);
				if (termFormat == ERPInsertDataIndexBody.STRING_TYPE) {
					String term = dataIndex.getTerm();
					if (term.compareTo(minValue.getStringValue()) < 0)
						leftIndices.add(dataIndex);
					else if (term.compareTo(maxValue.getStringValue()) >= 0)
						rightIndices.add(dataIndex);
					else
						this.insertDataIndex(dataIndex, physicalOwner
								.toString(), dataIndex.getBitmapValue());
				} else // termFormat == ERPInsertDataIndexBody.NUMERIC_TYPE
				{
					long term = Long.parseLong(dataIndex.getTerm());
					if (term < minValue.getLongValue())
						leftIndices.add(dataIndex);
					else if (term >= maxValue.getLongValue())
						rightIndices.add(dataIndex);
					else
						this.insertDataIndex(dataIndex, physicalOwner
								.toString(), dataIndex.getBitmapValue());
				}
			}

			/* forward remaining indices */
			// body.setPhysicalSender(serverpeer.getPhysicalInfo());
			// storing the messages into different groups
			Hashtable<PhysicalInfo, ERPInsertDataIndexBody> groupMsg = new Hashtable<PhysicalInfo, ERPInsertDataIndexBody>();

			for (int i = 0; i < leftIndices.size(); i++) {
				String milestoneValue = leftIndices.get(i).getTerm();

				int index = treeNode.getLeftRoutingTable().getTableSize() - 1;
				int found = -1;
				while ((index >= 0) && (found == -1)) {
					RoutingItemInfo nodeInfo = treeNode.getLeftRoutingTable()
							.getRoutingTableNode(index);
					if (nodeInfo != null) {
						if (nodeInfo.getMaxValue().compareTo(milestoneValue) > 0) {
							found = index;
						}
					}
					index--;
				}

				if (found != -1) {
					RoutingItemInfo transferInfo = treeNode
							.getLeftRoutingTable().getRoutingTableNode(found);
					if (groupMsg.containsKey(transferInfo.getPhysicalInfo())) {
						ERPInsertDataIndexBody nextBody = groupMsg
								.get(transferInfo.getPhysicalInfo());
						nextBody.getListOfTerms().add(leftIndices.get(i));
					} else {
						ERPInsertDataIndexBody nextBody = new ERPInsertDataIndexBody(
								serverpeer.getPhysicalInfo(), body
										.getPhysicalOwner(),
								new Vector<LocalDataIndex>(), body
										.getTermFormat(), transferInfo
										.getLogicalInfo());
						nextBody.getListOfTerms().add(leftIndices.get(i));
						groupMsg.put(transferInfo.getPhysicalInfo(), nextBody);
					}
				} else {
					if (treeNode.getLeftChild() != null) {
						if (groupMsg.containsKey(treeNode.getLeftChild()
								.getPhysicalInfo())) {
							ERPInsertDataIndexBody nextBody = groupMsg
									.get(treeNode.getLeftChild()
											.getPhysicalInfo());
							nextBody.getListOfTerms().add(leftIndices.get(i));
						} else {
							ERPInsertDataIndexBody nextBody = new ERPInsertDataIndexBody(
									serverpeer.getPhysicalInfo(), body
											.getPhysicalOwner(),
									new Vector<LocalDataIndex>(), body
											.getTermFormat(), treeNode
											.getLeftChild().getLogicalInfo());
							nextBody.getListOfTerms().add(leftIndices.get(i));
							groupMsg.put(treeNode.getLeftChild()
									.getPhysicalInfo(), nextBody);
						}
					} else {
						if (treeNode.getLeftAdjacentNode() != null) {
							if (groupMsg.containsKey(treeNode
									.getLeftAdjacentNode().getPhysicalInfo())) {
								ERPInsertDataIndexBody nextBody = groupMsg
										.get(treeNode.getLeftAdjacentNode()
												.getPhysicalInfo());
								nextBody.getListOfTerms().add(
										leftIndices.get(i));
							} else {
								ERPInsertDataIndexBody nextBody = new ERPInsertDataIndexBody(
										serverpeer.getPhysicalInfo(), body
												.getPhysicalOwner(),
										new Vector<LocalDataIndex>(), body
												.getTermFormat(), treeNode
												.getLeftAdjacentNode()
												.getLogicalInfo());
								nextBody.getListOfTerms().add(
										leftIndices.get(i));
								groupMsg.put(treeNode.getLeftAdjacentNode()
										.getPhysicalInfo(), nextBody);
							}
						} else {
							if (treeNode.getLogicalInfo().getNumber() == 1) {
								// insert data here and update range of values
								this.insertDataIndex(leftIndices.get(i),
										physicalOwner.toString(), leftIndices
												.get(i).getBitmapValue());
								SPGeneralAction.updateRangeValues(serverpeer,
										treeNode);
							} else {
								// Should not come here
							}
						}
					}
				}
			}

			for (int i = 0; i < rightIndices.size(); i++) {
				String milestoneValue = rightIndices.get(i).getTerm();

				int index = treeNode.getRightRoutingTable().getTableSize() - 1;
				int found = -1;
				while ((index >= 0) && (found == -1)) {
					RoutingItemInfo nodeInfo = (RoutingItemInfo) treeNode
							.getRightRoutingTable().getRoutingTableNode(index);
					if (nodeInfo != null) {
						if (nodeInfo.getMinValue().compareTo(milestoneValue) <= 0) {
							found = index;
						}
					}
					index--;
				}

				if (found != -1) {
					RoutingItemInfo transferInfo = treeNode
							.getRightRoutingTable().getRoutingTableNode(found);
					if (groupMsg.containsKey(transferInfo.getPhysicalInfo())) {
						ERPInsertDataIndexBody nextBody = groupMsg
								.get(transferInfo.getPhysicalInfo());
						nextBody.getListOfTerms().add(rightIndices.get(i));
					} else {
						ERPInsertDataIndexBody nextBody = new ERPInsertDataIndexBody(
								serverpeer.getPhysicalInfo(), body
										.getPhysicalOwner(),
								new Vector<LocalDataIndex>(), body
										.getTermFormat(), transferInfo
										.getLogicalInfo());
						nextBody.getListOfTerms().add(rightIndices.get(i));
						groupMsg.put(transferInfo.getPhysicalInfo(), nextBody);
					}
				} else {
					if (treeNode.getRightChild() != null) {
						if (groupMsg.containsKey(treeNode.getRightChild()
								.getPhysicalInfo())) {
							ERPInsertDataIndexBody nextBody = groupMsg
									.get(treeNode.getRightChild()
											.getPhysicalInfo());
							nextBody.getListOfTerms().add(rightIndices.get(i));
						} else {
							ERPInsertDataIndexBody nextBody = new ERPInsertDataIndexBody(
									serverpeer.getPhysicalInfo(), body
											.getPhysicalOwner(),
									new Vector<LocalDataIndex>(), body
											.getTermFormat(), treeNode
											.getRightChild().getLogicalInfo());
							nextBody.getListOfTerms().add(rightIndices.get(i));
							groupMsg.put(treeNode.getRightChild()
									.getPhysicalInfo(), nextBody);
						}
					} else {
						if (treeNode.getRightAdjacentNode() != null) {
							if (groupMsg.containsKey(treeNode
									.getRightAdjacentNode().getPhysicalInfo())) {
								ERPInsertDataIndexBody nextBody = groupMsg
										.get(treeNode.getRightAdjacentNode()
												.getPhysicalInfo());
								nextBody.getListOfTerms().add(
										rightIndices.get(i));
							} else {
								ERPInsertDataIndexBody nextBody = new ERPInsertDataIndexBody(
										serverpeer.getPhysicalInfo(), body
												.getPhysicalOwner(),
										new Vector<LocalDataIndex>(), body
												.getTermFormat(), treeNode
												.getRightAdjacentNode()
												.getLogicalInfo());
								nextBody.getListOfTerms().add(
										rightIndices.get(i));
								groupMsg.put(treeNode.getRightAdjacentNode()
										.getPhysicalInfo(), nextBody);
							}
						} else {
							if (treeNode.getLogicalInfo().getNumber() == PeerMath
									.pow(2, treeNode.getLogicalInfo()
											.getLevel())) {
								// insert data here and update range of values
								this.insertDataIndex(rightIndices.get(i),
										physicalOwner.toString(), rightIndices
												.get(i).getBitmapValue());
								SPGeneralAction.updateRangeValues(serverpeer,
										treeNode);
							} else {
								// Should not come here
							}
						}
					}
				}
			}

			// send out the message
			Enumeration<PhysicalInfo> enu = groupMsg.keys();

			while (enu.hasMoreElements()) {
				PhysicalInfo receiver = enu.nextElement();
				ERPInsertDataIndexBody msgBody = groupMsg.get(receiver);
				head.setMsgType(MsgType.ERP_INSERT_DATA_INDEX.getValue());
				Message newmsg = new Message(head, msgBody);
				serverpeer.sendMessage(receiver, newmsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException("Fail to insert data index", e);
		}
	}

	public void insertDataIndex(LocalDataIndex dataIndex, String physicalOwner,
			String bitmapCode) {
		String term = dataIndex.getTerm();
		String indexValue = physicalOwner + "_" + bitmapCode;

		try {
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement stmt = conn.createStatement();
			String sql;

			// check if an existing index has been created for this term
			sql = "select val";
			sql += " from data_index";
			sql += " where ind = '" + term + "'";

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String value = rs.getString(1);
				rs.close();
				value += ":" + indexValue;
				sql = "update data_index ";
				sql += " set val = '" + value + "'";
				sql += " where ind = '" + term + "'";
			} else {
				rs.close();
				sql = "insert into data_index(ind, val) values (";
				sql += "'" + term + "',";
				sql += "'" + indexValue + "')";
			}

			if (debug)
				System.out.println(sql);
			stmt.execute(sql);

			stmt.close();
		} catch (Exception e) {
			System.out.println("Error while inserting data index");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ERP_INSERT_DATA_INDEX
				.getValue())
			return true;
		return false;
	}
}
