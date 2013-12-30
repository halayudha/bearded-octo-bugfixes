package sg.edu.nus.peer.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.BoundaryValue;
import sg.edu.nus.peer.info.LocalColumnIndex;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ERPUpdateColumnIndexBody;

public class ERPUpdateColumnIndexListener extends ActionAdapter {
	public ERPUpdateColumnIndexListener(AbstractMainFrame gui) {
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
			ERPUpdateColumnIndexBody body = (ERPUpdateColumnIndexBody) msg
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

			/* delete table indices */
			PhysicalInfo physicalOwner = body.getPhysicalOwner();
			Vector<LocalColumnIndex> listOfColumns = body.getListOfColumns();
			BoundaryValue minValue = treeNode.getContent().getMinValue();
			BoundaryValue maxValue = treeNode.getContent().getMaxValue();
			Vector<LocalColumnIndex> leftIndices = new Vector<LocalColumnIndex>();
			Vector<LocalColumnIndex> rightIndices = new Vector<LocalColumnIndex>();

			for (int i = 0; i < listOfColumns.size(); i++) {
				LocalColumnIndex columnIndex = listOfColumns.get(i);
				String columnName = columnIndex.getColumnName();
				if (columnName.compareTo(minValue.getStringValue()) < 0)
					leftIndices.add(columnIndex);
				else if (columnName.compareTo(maxValue.getStringValue()) >= 0)
					rightIndices.add(columnIndex);
				else
					this.updateColumnIndex(columnIndex, physicalOwner
							.toString());
			}

			/* forward remaining indices */
			body.setPhysicalSender(serverpeer.getPhysicalInfo());
			if (leftIndices.size() > 0) {
				String milestoneValue = leftIndices.get(leftIndices.size() - 1)
						.getColumnName();

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

					head.setMsgType(MsgType.ERP_UPDATE_COLUMN_INDEX.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(transferInfo.getPhysicalInfo(),
							result);
				} else {
					if (treeNode.getLeftChild() != null) {
						body.setLogicalDestination(treeNode.getLeftChild()
								.getLogicalInfo());

						head.setMsgType(MsgType.ERP_UPDATE_COLUMN_INDEX
								.getValue());
						result = new Message(head, body);
						serverpeer.sendMessage(treeNode.getLeftChild()
								.getPhysicalInfo(), result);
					} else {
						if (treeNode.getLeftAdjacentNode() != null) {
							body.setLogicalDestination(treeNode
									.getLeftAdjacentNode().getLogicalInfo());

							head.setMsgType(MsgType.ERP_UPDATE_COLUMN_INDEX
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
				String milestoneValue = leftIndices.get(0).getColumnName();

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

					head.setMsgType(MsgType.ERP_UPDATE_COLUMN_INDEX.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(transferInfo.getPhysicalInfo(),
							result);
				} else {
					if (treeNode.getRightChild() != null) {
						body.setLogicalDestination(treeNode.getRightChild()
								.getLogicalInfo());

						head.setMsgType(MsgType.ERP_UPDATE_COLUMN_INDEX
								.getValue());
						result = new Message(head, body);
						serverpeer.sendMessage(treeNode.getRightChild()
								.getPhysicalInfo(), result);
					} else {
						if (treeNode.getRightAdjacentNode() != null) {
							body.setLogicalDestination(treeNode
									.getRightAdjacentNode().getLogicalInfo());

							head.setMsgType(MsgType.ERP_UPDATE_COLUMN_INDEX
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
			System.out.println("Error while updating column index");
			e.printStackTrace();
		}
	}

	/**
	 * @param columnIndex
	 * @param physicalOwner
	 */
	public void updateColumnIndex(LocalColumnIndex columnIndex,
			String physicalOwner) {
		String columnName = columnIndex.getColumnName();
		String updateValue = physicalOwner;
		Vector<String> listOfTables = columnIndex.getListOfTables();
		for (int i = 0; i < listOfTables.size(); i++)
			updateValue += "_" + listOfTables.get(i);

		try {
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement stmt = conn.createStatement();
			String sql;

			// check if an existing index has been created for this colum
			sql = "select val";
			sql += " from column_index";
			sql += " where ind = '" + columnName + "'";

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String value = ":" + rs.getString(0) + ":";
				rs.close();

				String indexValue = ":" + physicalOwner;
				int start = value.indexOf(indexValue);
				int end = value.indexOf(indexValue, start + 1);
				value = value.substring(0, start) + ":" + updateValue + ":"
						+ value.substring(end);
				value.substring(0, value.length() - 2);

				sql = "update column_index set val = '" + value + "'"
						+ " where ind = '" + columnName + "'";
				stmt.execute(sql);
			} else {
				// nothing to do, should not come here
			}

			stmt.close();
		} catch (Exception e) {
			System.out.println("Error while updating column index");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ERP_UPDATE_COLUMN_INDEX
				.getValue())
			return true;
		return false;
	}
}
