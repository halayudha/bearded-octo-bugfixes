package sg.edu.nus.peer.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
import sg.edu.nus.protocol.body.ERPDeleteDataIndexBody;
import sg.edu.nus.protocol.body.ERPInsertDataIndexBody;

public class ERPDeleteDataIndexListener extends ActionAdapter {

	public ERPDeleteDataIndexListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Message result = null;
		Head head = new Head();

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body and the tree node */
			ERPDeleteDataIndexBody body = (ERPDeleteDataIndexBody) msg
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

			/* delete data indices */
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
						this.deleteDataIndex(dataIndex, physicalOwner
								.toString());
				} else // termFormat == ERPInsertDataIndexBody.NUMERIC_TYPE
				{
					long term = Long.parseLong(dataIndex.getTerm());
					if (term < minValue.getLongValue())
						leftIndices.add(dataIndex);
					else if (term >= maxValue.getLongValue())
						rightIndices.add(dataIndex);
					else
						this.deleteDataIndex(dataIndex, physicalOwner
								.toString());
				}
			}

			/* forward remaining indices */
			body.setPhysicalSender(serverpeer.getPhysicalInfo());
			if (leftIndices.size() > 0) {
				String milestoneValue = leftIndices.get(leftIndices.size() - 1)
						.getTerm();

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
					body.setLogicalDestination(transferInfo.getLogicalInfo());

					head.setMsgType(MsgType.ERP_DELETE_DATA_INDEX.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(transferInfo.getPhysicalInfo(),
							result);
				} else {
					if (treeNode.getLeftChild() != null) {
						body.setLogicalDestination(treeNode.getLeftChild()
								.getLogicalInfo());

						head.setMsgType(MsgType.ERP_DELETE_DATA_INDEX
								.getValue());
						result = new Message(head, body);
						serverpeer.sendMessage(treeNode.getLeftChild()
								.getPhysicalInfo(), result);
					} else {
						if (treeNode.getLeftAdjacentNode() != null) {
							body.setLogicalDestination(treeNode
									.getLeftAdjacentNode().getLogicalInfo());

							head.setMsgType(MsgType.ERP_DELETE_DATA_INDEX
									.getValue());
							result = new Message(head, body);
							serverpeer.sendMessage(treeNode
									.getLeftAdjacentNode().getPhysicalInfo(),
									result);
						} else {
							// Should not come here
						}
					}
				}
			}

			if (rightIndices.size() > 0) {
				String milestoneValue = leftIndices.get(0).getTerm();

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
					body.setLogicalDestination(transferInfo.getLogicalInfo());

					head.setMsgType(MsgType.ERP_DELETE_DATA_INDEX.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(transferInfo.getPhysicalInfo(),
							result);
				} else {
					if (treeNode.getRightChild() != null) {
						body.setLogicalDestination(treeNode.getRightChild()
								.getLogicalInfo());

						head.setMsgType(MsgType.ERP_DELETE_DATA_INDEX
								.getValue());
						result = new Message(head, body);
						serverpeer.sendMessage(treeNode.getRightChild()
								.getPhysicalInfo(), result);
					} else {
						if (treeNode.getRightAdjacentNode() != null) {
							body.setLogicalDestination(treeNode
									.getRightAdjacentNode().getLogicalInfo());

							head.setMsgType(MsgType.ERP_DELETE_DATA_INDEX
									.getValue());
							result = new Message(head, body);
							serverpeer.sendMessage(treeNode
									.getRightAdjacentNode().getPhysicalInfo(),
									result);
						} else {
							// Should not come here
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error while deleting data index");
			e.printStackTrace();
		}
	}

	public void deleteDataIndex(LocalDataIndex dataIndex, String physicalOwner) {
		String term = dataIndex.getTerm();
		String indexValue = ":" + physicalOwner + "_";
		int bitmapLength = dataIndex.getBitmapValue().length();

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
				String value = ":" + rs.getString(0);
				rs.close();

				int length = indexValue.length() + bitmapLength;
				int start = value.indexOf(indexValue);
				value = value.substring(0, start)
						+ value.substring(start + length);

				if (value.equals(""))
					sql = "delete from table_index where ind = '" + term + "'";
				else
					sql = "update table_index set val = '" + value + "'"
							+ " where ind = '" + term + "'";
				stmt.execute(sql);
			} else {
				// nothing to do, should not come here
			}

			stmt.close();
		} catch (Exception e) {
			System.out.println("Error while deleting data index");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ERP_DELETE_DATA_INDEX
				.getValue())
			return true;
		return false;
	}
}
