/**
 * Created on Sep 1, 2008
 */
package sg.edu.nus.peer.event;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import sg.edu.nus.bestpeer.indexdata.ExactQuery;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.AdjacentNodeInfo;
import sg.edu.nus.peer.info.ChildNodeInfo;
import sg.edu.nus.peer.info.ContentInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.RoutingTableInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.TableSearchBody;
import sg.edu.nus.protocol.body.TableSearchResultBody;

/**
 * @author David Jiang
 * 
 */
public class TableIndexSearchListener extends ActionAdapter {

	public TableIndexSearchListener(AbstractMainFrame gui) {
		super(gui);
	}

	public String findTableOwners(String tableName) {
		String owners = null;
		String sql = "select val from table_index where ind='";
		sql += tableName;
		sql += "'";

		try {
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				owners = rs.getString("val");
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return owners;
	}

	/**
	 * added by VHTam find table owner by both table index and data index
	 */
	public String findTableOwners(TableSearchBody body) {
		String tableName = body.getTableName();
		String owners = null;

		try {
			String sql;
			Connection conn = ServerPeer.conn_bestpeerindexdb;
			Statement stmt = conn.createStatement();
			ResultSet rs;

			// query range index
			ExactQuery exactQuery = body.getExactQuery();

			if (exactQuery != null) {

				String index_table_name = " range_index_number ";
				if (exactQuery.getValue().contains("'")) {
					index_table_name = " range_index_string ";
				}

				sql = "select val ";
				sql += " from " + index_table_name;
				sql += " where table_name = '" + tableName
						+ "' and column_name = '" + exactQuery.getColumnName()
						+ "' and lower_bound <= " + exactQuery.getValue()
						+ " and upper_bound >= " + exactQuery.getValue();
				rs = stmt.executeQuery(sql);
				String nodes = "";
				while (rs.next()) {
					nodes += rs.getString("val") + ":";
				}
				if (!nodes.equals("")) {
					owners = nodes.substring(0, nodes.length() - 1);
					System.out.println("DEBUG: find owners with data index "
							+ owners);
				}
				rs.close();
			}
			// if no data index, so search for table index
			if (owners == null) {

				sql = "select val from table_index where ind='";
				sql += tableName;
				sql += "'";
				rs = stmt.executeQuery(sql);

				if (rs.next())
					owners = rs.getString("val");
				rs.close();

				System.out.println("DEBUG: find owners " + owners);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return owners;
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {

		ServerPeer serverpeer = (ServerPeer) gui.peer();
		Message result = null;
		Head head = new Head();
		TableSearchBody body = (TableSearchBody) msg.getBody();
		String tableName = body.getTableName();

		TreeNode treeNode = serverpeer
				.getTreeNode(body.getLogicalDestination());
		if (treeNode == null) {
			TreeNode[] listOfTreeNodes = serverpeer.getTreeNodes();
			if (listOfTreeNodes.length == 0) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			} else
				treeNode = listOfTreeNodes[0];
		}

		TableSearchResultBody resultBody = new TableSearchResultBody("", false);
		resultBody.setTableName(tableName);
		resultBody.setEventName(body.getEventName());
		ContentInfo content = treeNode.getContent();
		if (content.isInRange(tableName)) {
			String owners = findTableOwners(body);
			if (owners != null) {
				resultBody.setFound(true);
				resultBody.setTableOwners(owners);
			}
			head.setMsgType(MsgType.TABLE_INDEX_SEARCH_RESULT.getValue());
			try {
				serverpeer.sendMessage(body.getPhysicalRequester(),
						new Message(head, resultBody));

			} catch (Exception e) {
				e.printStackTrace();
				throw new EventHandleException(
						"Fail to send meesage to query requester");
			}
			return;
		}

		try {
			body.setPhysicalSender(serverpeer.getPhysicalInfo());
			body.setLogicalSender(treeNode.getLogicalInfo());

			// Search left children
			if (content.compareTo(tableName) < 0) {
				RoutingTableInfo leftRoutingTable = treeNode
						.getLeftRoutingTable();
				RoutingItemInfo node = null;
				for (int i = leftRoutingTable.getTableSize() - 1; i >= 0; --i) {
					node = leftRoutingTable.getRoutingTableNode(i);
					if (node != null
							&& tableName.compareTo(node.getMaxValue()
									.getStringValue()) <= 0)
						break;
					node = null;
				}
				if (node != null) {
					body.setLogicalDestination(node.getLogicalInfo());
					head.setMsgType(MsgType.TABLE_INDEX_SEARCH.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(node.getPhysicalInfo(), result);

					return;
				}

				ChildNodeInfo leftChild = treeNode.getLeftChild();
				if (leftChild != null) {
					body.setLogicalDestination(leftChild.getLogicalInfo());
					head.setMsgType(MsgType.TABLE_INDEX_SEARCH.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(leftChild.getPhysicalInfo(), result);

					return;
				}

				AdjacentNodeInfo leftAdjacentNode = treeNode
						.getLeftAdjacentNode();
				if (leftAdjacentNode != null) {
					body.setLogicalDestination(leftAdjacentNode
							.getLogicalInfo());
					head.setMsgType(MsgType.TABLE_INDEX_SEARCH.getValue());
					result = new Message(head, body);
					serverpeer.sendMessage(leftAdjacentNode.getPhysicalInfo(),
							result);

					return;
				}

				// Fail to find the indexed value
				// TODO: add code to inform the sender
				System.out.println("The key is out of indexed range");
				return;
			}

			// Search right children
			RoutingTableInfo rightRoutingTable = treeNode
					.getRightRoutingTable();
			RoutingItemInfo node = null;
			for (int i = rightRoutingTable.getTableSize() - 1; i >= 0; --i) {
				node = rightRoutingTable.getRoutingTableNode(i);
				if (node != null
						&& tableName.compareTo(node.getMinValue()
								.getStringValue()) > 0)
					break;
				node = null;
			}
			if (node != null) {
				body.setLogicalDestination(node.getLogicalInfo());
				head.setMsgType(MsgType.TABLE_INDEX_SEARCH.getValue());
				result = new Message(head, body);
				serverpeer.sendMessage(node.getPhysicalInfo(), result);

				return;
			}

			ChildNodeInfo rightChild = treeNode.getRightChild();
			if (rightChild != null) {
				body.setLogicalDestination(rightChild.getLogicalInfo());
				head.setMsgType(MsgType.TABLE_INDEX_SEARCH.getValue());
				result = new Message(head, body);

				serverpeer.sendMessage(rightChild.getPhysicalInfo(), result);

				return;
			}

			AdjacentNodeInfo rightAdjacentNode = treeNode
					.getRightAdjacentNode();
			if (rightAdjacentNode != null) {
				body.setLogicalDestination(rightAdjacentNode.getLogicalInfo());
				head.setMsgType(MsgType.TABLE_INDEX_SEARCH.getValue());
				result = new Message(head, body);
				serverpeer.sendMessage(rightAdjacentNode.getPhysicalInfo(),
						result);

				return;
			}

			// TODO: add code to do failure
			System.out.println("The key is out of indexed range");
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new EventHandleException("Fail to search table index", e);
		}
	}

	public boolean isConsumed(Message message) throws EventHandleException {
		if (message.getHead().getMsgType() == MsgType.TABLE_INDEX_SEARCH
				.getValue())
			return true;
		return false;
	}
}
