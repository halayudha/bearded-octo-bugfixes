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
import sg.edu.nus.peer.info.LocalColumnIndex;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ERPDeleteColumnIndexBody;
import sg.edu.nus.util.PeerMath;

public class ERPDeleteColumnIndexListener extends ActionAdapter {

	public ERPDeleteColumnIndexListener(AbstractMainFrame gui) {
		super(gui);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {

		super.actionPerformed(dest, msg);


		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();

			/* get the message body and the tree node */
			ERPDeleteColumnIndexBody body = (ERPDeleteColumnIndexBody) msg
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
					this.deleteColumnIndex(columnIndex, physicalOwner
							.toString());
			}

			Hashtable<PhysicalInfo, ERPDeleteColumnIndexBody> forsent = new Hashtable<PhysicalInfo, ERPDeleteColumnIndexBody>();
			while (leftIndices.size() > 0) {
				String milestoneValue = leftIndices.get(0).getColumnName();

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

					if (forsent.containsKey(transferInfo.getPhysicalInfo())) {
						ERPDeleteColumnIndexBody mybody = forsent
								.get(transferInfo.getPhysicalInfo());
						mybody.getListOfColumns().add(leftIndices.get(0));
					} else {
						ERPDeleteColumnIndexBody mybody = new ERPDeleteColumnIndexBody(
								serverpeer.getPhysicalInfo(), body
										.getPhysicalOwner(),
								new Vector<LocalColumnIndex>(), transferInfo
										.getLogicalInfo());
						mybody.getListOfColumns().add(leftIndices.get(0));
						forsent.put(transferInfo.getPhysicalInfo(), mybody);
					}
				} else {
					if (treeNode.getLeftChild() != null) {
						if (forsent.containsKey(treeNode.getLeftChild()
								.getPhysicalInfo())) {
							ERPDeleteColumnIndexBody mybody = forsent
									.get(treeNode.getLeftChild()
											.getPhysicalInfo());
							mybody.getListOfColumns().add(leftIndices.get(0));
						} else {
							ERPDeleteColumnIndexBody mybody = new ERPDeleteColumnIndexBody(
									serverpeer.getPhysicalInfo(), body
											.getPhysicalOwner(),
									new Vector<LocalColumnIndex>(), treeNode
											.getLeftChild().getLogicalInfo());
							mybody.getListOfColumns().add(leftIndices.get(0));
							forsent.put(treeNode.getLeftChild()
									.getPhysicalInfo(), mybody);
						}
					} else {
						if (treeNode.getLeftAdjacentNode() != null) {
							if (forsent.containsKey(treeNode
									.getLeftAdjacentNode().getPhysicalInfo())) {
								ERPDeleteColumnIndexBody mybody = forsent
										.get(treeNode.getLeftAdjacentNode()
												.getPhysicalInfo());
								mybody.getListOfColumns().add(
										leftIndices.get(0));
							} else {
								ERPDeleteColumnIndexBody mybody = new ERPDeleteColumnIndexBody(
										serverpeer.getPhysicalInfo(), body
												.getPhysicalOwner(),
										new Vector<LocalColumnIndex>(),
										treeNode.getLeftAdjacentNode()
												.getLogicalInfo());
								mybody.getListOfColumns().add(
										leftIndices.get(0));
								forsent.put(treeNode.getLeftAdjacentNode()
										.getPhysicalInfo(), mybody);
							}
						} else {
							if (treeNode.getLogicalInfo().getNumber() == 1) {
								// insert data here and update range of values
								for (int i = 0; i < leftIndices.size(); i++)
									this.deleteColumnIndex(leftIndices.get(i),
											physicalOwner.toString());
								SPGeneralAction.updateRangeValues(serverpeer,
										treeNode);
							} else {
								// Should not come here
							}
						}
					}
				}
				leftIndices.remove(0);
			}

			while (rightIndices.size() > 0) {
				String milestoneValue = rightIndices.get(0).getColumnName();

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

					if (forsent.containsKey(transferInfo.getPhysicalInfo())) {
						ERPDeleteColumnIndexBody mybody = forsent
								.get(transferInfo.getPhysicalInfo());
						mybody.getListOfColumns().add(rightIndices.get(0));
					} else {
						ERPDeleteColumnIndexBody mybody = new ERPDeleteColumnIndexBody(
								serverpeer.getPhysicalInfo(), body
										.getPhysicalOwner(),
								new Vector<LocalColumnIndex>(), transferInfo
										.getLogicalInfo());
						mybody.getListOfColumns().add(rightIndices.get(0));
						forsent.put(transferInfo.getPhysicalInfo(), mybody);
					}
				} else {
					if (treeNode.getRightChild() != null) {
						if (forsent.containsKey(treeNode.getRightChild()
								.getPhysicalInfo())) {
							ERPDeleteColumnIndexBody mybody = forsent
									.get(treeNode.getRightChild()
											.getPhysicalInfo());
							mybody.getListOfColumns().add(rightIndices.get(0));
						} else {
							ERPDeleteColumnIndexBody mybody = new ERPDeleteColumnIndexBody(
									serverpeer.getPhysicalInfo(), body
											.getPhysicalOwner(),
									new Vector<LocalColumnIndex>(), treeNode
											.getRightChild().getLogicalInfo());
							mybody.getListOfColumns().add(rightIndices.get(0));
							forsent.put(treeNode.getRightChild()
									.getPhysicalInfo(), mybody);
						}
					} else {
						if (treeNode.getRightAdjacentNode() != null) {
							if (forsent.containsKey(treeNode
									.getRightAdjacentNode().getPhysicalInfo())) {
								ERPDeleteColumnIndexBody mybody = forsent
										.get(treeNode.getRightAdjacentNode()
												.getPhysicalInfo());
								mybody.getListOfColumns().add(
										rightIndices.get(0));
							} else {
								ERPDeleteColumnIndexBody mybody = new ERPDeleteColumnIndexBody(
										serverpeer.getPhysicalInfo(), body
												.getPhysicalOwner(),
										new Vector<LocalColumnIndex>(),
										treeNode.getRightAdjacentNode()
												.getLogicalInfo());
								mybody.getListOfColumns().add(
										rightIndices.get(0));
								forsent.put(treeNode.getRightAdjacentNode()
										.getPhysicalInfo(), mybody);
							}
						} else {
							if (treeNode.getLogicalInfo().getNumber() == PeerMath
									.pow(2, treeNode.getLogicalInfo()
											.getLevel())) {
								// insert data here and update range of values
								for (int i = 0; i < rightIndices.size(); i++)
									this.deleteColumnIndex(rightIndices.get(i),
											physicalOwner.toString());
								SPGeneralAction.updateRangeValues(serverpeer,
										treeNode);
							} else {
								// Should not come here
							}
						}
					}
					rightIndices.remove(0);
				}
			}

			// send the message out
			Enumeration enu = forsent.keys();
			while (enu.hasMoreElements()) {
				PhysicalInfo receiver = (PhysicalInfo) enu.nextElement();
				ERPDeleteColumnIndexBody mybody = forsent.get(receiver);
				Head myhead = new Head();
				myhead.setMsgType(MsgType.ERP_DELETE_COLUMN_INDEX.getValue());
				serverpeer.sendMessage(receiver, new Message(myhead, mybody));
			}
			forsent.clear();
		} catch (Exception e) {
			System.out.println("Error while deleting column index");
			e.printStackTrace();
		}
	}

	/**
	 * delete column index
	 * @param columnIndex
	 * @param physicalOwner
	 */
	public void deleteColumnIndex(LocalColumnIndex columnIndex,
			String physicalOwner) {
		String columnName = columnIndex.getColumnName();

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
				value = value.substring(0, start) + value.substring(end);
				value.substring(0, value.length() - 2);

				if (value.equals(""))
					sql = "delete from column_index where ind = '" + columnName
							+ "'";
				else
					sql = "update column_index set val = '" + value + "'"
							+ " where ind = '" + columnName + "'";
				stmt.execute(sql);
			} else {
				// nothing to do, should not come here
			}

			stmt.close();
		} catch (Exception e) {
			System.out.println("Error while deleting column index");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ERP_DELETE_COLUMN_INDEX
				.getValue())
			return true;
		return false;
	}
}
