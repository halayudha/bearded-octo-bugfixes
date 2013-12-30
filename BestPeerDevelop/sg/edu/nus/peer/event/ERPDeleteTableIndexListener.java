package sg.edu.nus.peer.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.bestpeer.indexdata.RangeIndex;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.BoundaryValue;
import sg.edu.nus.peer.info.LocalTableIndex;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ERPDeleteTableIndexBody;
import sg.edu.nus.util.PeerMath;

public class ERPDeleteTableIndexListener extends ActionAdapter {

	public ERPDeleteTableIndexListener(AbstractMainFrame gui) {
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
			ERPDeleteTableIndexBody body = (ERPDeleteTableIndexBody) msg
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
			Vector<LocalTableIndex> listOfTables = body.getListOfTables();
			BoundaryValue minValue = treeNode.getContent().getMinValue();
			BoundaryValue maxValue = treeNode.getContent().getMaxValue();
			Vector<LocalTableIndex> leftIndices = new Vector<LocalTableIndex>();
			Vector<LocalTableIndex> rightIndices = new Vector<LocalTableIndex>();
			for (int i = 0; i < listOfTables.size(); i++) {
				LocalTableIndex tableIndex = listOfTables.get(i);
				String tableName = tableIndex.getTableName();
				if (tableName.compareTo(minValue.getStringValue()) < 0)
					leftIndices.add(tableIndex);
				else if (tableName.compareTo(maxValue.getStringValue()) >= 0)
					rightIndices.add(tableIndex);
				else
					this.deleteTableIndex(tableIndex, physicalOwner.toString());
			}

			/* forward remaining indices */

			body.setPhysicalSender(serverpeer.getPhysicalInfo());
			Hashtable<PhysicalInfo, ERPDeleteTableIndexBody> forsent = new Hashtable<PhysicalInfo, ERPDeleteTableIndexBody>();

			while (leftIndices.size() > 0) {
				String milestoneTable = leftIndices.get(0).getTableName();

				int index = treeNode.getLeftRoutingTable().getTableSize() - 1;
				int found = -1;
				while ((index >= 0) && (found == -1)) {
					RoutingItemInfo nodeInfo = treeNode.getLeftRoutingTable()
							.getRoutingTableNode(index);
					if (nodeInfo != null) {
						if (nodeInfo.getMaxValue().compareTo(milestoneTable) > 0) {
							found = index;
						}
					}
					index--;
				}

				if (found != -1) {
					RoutingItemInfo transferInfo = treeNode
							.getLeftRoutingTable().getRoutingTableNode(found);

					if (forsent.containsKey(transferInfo.getPhysicalInfo())) {
						ERPDeleteTableIndexBody mybody = forsent
								.get(transferInfo.getPhysicalInfo());
						mybody.getListOfTables().add(leftIndices.get(0));
					} else {
						ERPDeleteTableIndexBody mybody = new ERPDeleteTableIndexBody(
								serverpeer.getPhysicalInfo(), body
										.getPhysicalOwner(),
								new Vector<LocalTableIndex>(), transferInfo
										.getLogicalInfo());
						mybody.getListOfTables().add(leftIndices.get(0));
						forsent.put(transferInfo.getPhysicalInfo(), mybody);
					}
				} else {
					if (treeNode.getLeftChild() != null) {
						if (forsent.containsKey(treeNode.getLeftChild()
								.getPhysicalInfo())) {
							ERPDeleteTableIndexBody mybody = forsent
									.get(treeNode.getLeftChild()
											.getPhysicalInfo());
							mybody.getListOfTables().add(leftIndices.get(0));
						} else {
							ERPDeleteTableIndexBody mybody = new ERPDeleteTableIndexBody(
									serverpeer.getPhysicalInfo(), body
											.getPhysicalOwner(),
									new Vector<LocalTableIndex>(), treeNode
											.getLeftChild().getLogicalInfo());
							mybody.getListOfTables().add(leftIndices.get(0));
							forsent.put(treeNode.getLeftChild()
									.getPhysicalInfo(), mybody);
						}

					} else {
						if (treeNode.getLeftAdjacentNode() != null) {
							if (forsent.containsKey(treeNode
									.getLeftAdjacentNode().getPhysicalInfo())) {
								ERPDeleteTableIndexBody mybody = forsent
										.get(treeNode.getLeftAdjacentNode()
												.getPhysicalInfo());
								mybody.getListOfTables()
										.add(leftIndices.get(0));
							} else {
								ERPDeleteTableIndexBody mybody = new ERPDeleteTableIndexBody(
										serverpeer.getPhysicalInfo(), body
												.getPhysicalOwner(),
										new Vector<LocalTableIndex>(), treeNode
												.getLeftAdjacentNode()
												.getLogicalInfo());
								mybody.getListOfTables()
										.add(leftIndices.get(0));
								forsent.put(treeNode.getLeftAdjacentNode()
										.getPhysicalInfo(), mybody);
							}
						} else {
							if (treeNode.getLogicalInfo().getNumber() == 1) {
								// insert data here and update range of values
								for (int i = 0; i < leftIndices.size(); i++)
									this.deleteTableIndex(leftIndices.get(i),
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
				String milestoneTable = rightIndices.get(0).getTableName();

				int index = treeNode.getRightRoutingTable().getTableSize() - 1;
				int found = -1;
				while ((index >= 0) && (found == -1)) {
					RoutingItemInfo nodeInfo = (RoutingItemInfo) treeNode
							.getRightRoutingTable().getRoutingTableNode(index);
					if (nodeInfo != null) {
						if (nodeInfo.getMinValue().compareTo(milestoneTable) <= 0) {
							found = index;
						}
					}
					index--;
				}

				if (found != -1) {
					RoutingItemInfo transferInfo = treeNode
							.getRightRoutingTable().getRoutingTableNode(found);
					if (forsent.containsKey(transferInfo.getPhysicalInfo())) {
						ERPDeleteTableIndexBody mybody = forsent
								.get(transferInfo.getPhysicalInfo());
						mybody.getListOfTables().add(rightIndices.get(0));
					} else {
						ERPDeleteTableIndexBody mybody = new ERPDeleteTableIndexBody(
								serverpeer.getPhysicalInfo(), body
										.getPhysicalOwner(),
								new Vector<LocalTableIndex>(), transferInfo
										.getLogicalInfo());
						mybody.getListOfTables().add(rightIndices.get(0));
						forsent.put(transferInfo.getPhysicalInfo(), mybody);
					}

				} else {
					if (treeNode.getRightChild() != null) {
						if (forsent.containsKey(treeNode.getRightChild()
								.getPhysicalInfo())) {
							ERPDeleteTableIndexBody mybody = forsent
									.get(treeNode.getRightChild()
											.getPhysicalInfo());
							mybody.getListOfTables().add(rightIndices.get(0));
						} else {
							ERPDeleteTableIndexBody mybody = new ERPDeleteTableIndexBody(
									serverpeer.getPhysicalInfo(), body
											.getPhysicalOwner(),
									new Vector<LocalTableIndex>(), treeNode
											.getRightChild().getLogicalInfo());
							mybody.getListOfTables().add(rightIndices.get(0));
							forsent.put(treeNode.getRightChild()
									.getPhysicalInfo(), mybody);
						}
					} else {
						if (treeNode.getRightAdjacentNode() != null) {
							if (forsent.containsKey(treeNode
									.getRightAdjacentNode().getPhysicalInfo())) {
								ERPDeleteTableIndexBody mybody = forsent
										.get(treeNode.getRightAdjacentNode()
												.getPhysicalInfo());
								mybody.getListOfTables().add(
										rightIndices.get(0));
							} else {
								ERPDeleteTableIndexBody mybody = new ERPDeleteTableIndexBody(
										serverpeer.getPhysicalInfo(), body
												.getPhysicalOwner(),
										new Vector<LocalTableIndex>(), treeNode
												.getRightAdjacentNode()
												.getLogicalInfo());
								mybody.getListOfTables().add(
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
									this.deleteTableIndex(rightIndices.get(i),
											physicalOwner.toString());
								SPGeneralAction.updateRangeValues(serverpeer,
										treeNode);
							} else {
								// Should not come here
							}
						}
					}
				}

				rightIndices.remove(0);
			}

			// ready for sent out
			Enumeration enu = forsent.keys();
			while (enu.hasMoreElements()) {
				PhysicalInfo receiver = (PhysicalInfo) enu.nextElement();
				ERPDeleteTableIndexBody mybody = forsent.get(receiver);
				Head myhead = new Head();
				myhead.setMsgType(MsgType.ERP_DELETE_TABLE_INDEX.getValue());
				serverpeer.sendMessage(receiver, new Message(myhead, mybody));
			}
			forsent.clear();

		} catch (Exception e) {
			System.out.println("Error while deleting table index");
			e.printStackTrace();
		}
	}

	public void deleteTableIndex(LocalTableIndex tableIndex,
			String physicalOwner) {
		String tableName = tableIndex.getTableName();
		String indexValue = ":" + physicalOwner;

		if (debug) 
			System.out.println("Process delete index.......");
		
		try {
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement stmt = conn.createStatement();
			String sql;

			// check if an existing index has been created for this table
			sql = "select val";
			sql += " from table_index";
			sql += " where ind = '" + tableName + "'";

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String value = ":" + rs.getString(1);
				rs.close();

				int length = indexValue.length();
				int start = value.indexOf(indexValue);
				value = value.substring(0, start)
						+ value.substring(start + length);

				if (value.equals(""))
					sql = "delete from table_index where ind = '" + tableName
							+ "'";
				else {
					if (value.startsWith(":")){
						value = value.substring(1);//do not include first ":"
					}
					sql = "update table_index set val = '" + value + "'"
							+ " where ind = '" + tableName + "'";
				}
				if (debug) 
					System.out.println("delete index sql: "+ sql);
				
				stmt.execute(sql);
			} else {
				// nothing to do, should not come here
			}

			// index range
			Vector<RangeIndex> rangeIndexOfTable = tableIndex.getRangeIndexOfTable();
			for (int i=0; i < rangeIndexOfTable.size(); i++){
				RangeIndex rangeIndex = rangeIndexOfTable.get(i);
				String index_table_name = " range_index_number ";
				if (rangeIndex.isStringType()){
					index_table_name = " range_index_string ";
				}
				sql = "delete ";
				sql += " from " + index_table_name;
				sql += " where table_name = '" + tableName + "' and column_name = '"+rangeIndex.getColumnName()+"' and val ='" + rangeIndex.getOwner()+"'";
								
				if (debug) 
					System.out.println(sql);
				stmt.execute(sql);
			}

			
			stmt.close();
		} catch (Exception e) {
			System.out.println("Error while deleting table index");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ERP_DELETE_TABLE_INDEX
				.getValue())
			return true;
		return false;
	}
}
