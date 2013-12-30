/**
 * Created on Sep 1, 2008
 */
package sg.edu.nus.peer.event;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import sg.edu.nus.bestpeer.indexdata.ExactQuery;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.peer.Bootstrap;
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
 * @author VHTam
 * 
 */

public class TableIndexSearchBootstrapListener extends ActionAdapter {

	public TableIndexSearchBootstrapListener(AbstractMainFrame gui) {
		super(gui);
	}

	/**
	 * find table owner by both table index and range index
	 * read from database
	 */
	public String findTableOwnersFromDb(String tableName, ExactQuery exactQuery) {
		String owners = null;

		try {
			String sql;
			Connection conn = Bootstrap.conn_bestpeerdb;
			Statement stmt = conn.createStatement();
			ResultSet rs;

			// query range index

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

	/**
	 * find table owner by both table index and range index
	 * read from memory
	 */
	public String findTableOwnersFromMem(String tableName, ExactQuery exactQuery) {
	
		String owners = ((BootstrapGUI)gui).cacheDbIndex.findTableOwners(tableName, exactQuery);
		
		return owners;
	}
	
	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {

		Bootstrap bootstrap = (Bootstrap) gui.peer();

		Message result = null;
		Head head = new Head();
		TableSearchBody body = (TableSearchBody) msg.getBody();
		String tableName = body.getTableName();

		TableSearchResultBody resultBody = new TableSearchResultBody("", false);
		resultBody.setTableName(tableName);
		resultBody.setEventName(body.getEventName());

		String owners = findTableOwnersFromMem(body.getTableName(), body.getExactQuery());
		if (owners==null){
			owners = findTableOwnersFromDb(body.getTableName(), body.getExactQuery());
		}
		
		if (owners != null) {
			resultBody.setFound(true);
			resultBody.setTableOwners(owners);
		}
		head.setMsgType(MsgType.TABLE_INDEX_SEARCH_RESULT.getValue());
		try {
			bootstrap.sendMessage(body.getPhysicalRequester(), new Message(
					head, resultBody));

		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"Fail to send meesage to query requester");
		}

	}

	public boolean isConsumed(Message message) throws EventHandleException {
		if (message.getHead().getMsgType() == MsgType.TABLE_INDEX_SEARCH
				.getValue())
			return true;
		return false;
	}
}
