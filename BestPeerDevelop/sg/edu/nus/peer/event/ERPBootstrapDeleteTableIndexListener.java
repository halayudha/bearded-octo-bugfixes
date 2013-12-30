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
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.BoundaryValue;
import sg.edu.nus.peer.info.LocalTableIndex;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.CacheDbIndexBody;
import sg.edu.nus.protocol.body.ERPDeleteTableIndexBody;
import sg.edu.nus.util.PeerMath;

public class ERPBootstrapDeleteTableIndexListener extends ActionAdapter {

	public ERPBootstrapDeleteTableIndexListener(AbstractMainFrame gui) {
		super(gui);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {

		super.actionPerformed(dest, msg);

			/* get the message body and the tree node */
			ERPDeleteTableIndexBody body = (ERPDeleteTableIndexBody) msg
					.getBody();
			
			/* delete table indices */
			PhysicalInfo physicalOwner = body.getPhysicalOwner();
			Vector<LocalTableIndex> listOfTables = body.getListOfTables();
			
			for (int i = 0; i < listOfTables.size(); i++) {
				LocalTableIndex tableIndex = listOfTables.get(i);
				//String tableName = tableIndex.getTableName();
				this.deleteTableIndex(tableIndex, physicalOwner.toString());
			}

			((BootstrapGUI)gui).cacheDbIndex.loadFromDB(Bootstrap.conn_bestpeerdb);
			//send cache db index to peer
			PeerMaintainer maintainer = PeerMaintainer.getInstance();
			PeerInfo[] servers = maintainer.getServers();
			for (int i=0; i<servers.length; i++){
				PhysicalInfo server = new PhysicalInfo(servers[i].getInetAddress(), servers[i].getPort());
				Message cacheDbIndex = new Message(new Head(MsgType.CACHE_DB_INDEX.getValue()), new CacheDbIndexBody(((BootstrapGUI)gui).cacheDbIndex));
				try {
					gui.peer().sendMessage(server, cacheDbIndex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

	}

	public void deleteTableIndex(LocalTableIndex tableIndex,
			String physicalOwner) {
		String tableName = tableIndex.getTableName();
		String indexValue = ":" + physicalOwner;

		if (debug) 
			System.out.println("Process delete index.......");
		
		try {
			
			Connection conn = Bootstrap.conn_bestpeerdb;
			
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
