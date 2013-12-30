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
import sg.edu.nus.protocol.body.ERPInsertTableIndexBody;
import sg.edu.nus.util.PeerMath;

/**
 * Implement a listener for processing ERP_INSERT_TABLE_INDEX message.
 * bootstrap will play as central server store info of index
 * of course other peers still play their role as BATON
 * bootstrap is only extra support to the sysem
 * @author Hoang Tam Vo 
 */

public class ERPBootstrapInsertTableIndexListener extends ActionAdapter {

	private boolean debug = true;

	public ERPBootstrapInsertTableIndexListener(AbstractMainFrame gui) {
		super(gui);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);


		try {
			/* get the message body and the tree node */
			ERPInsertTableIndexBody body = (ERPInsertTableIndexBody) msg
					.getBody();
			

			/* insert table indices */
			PhysicalInfo physicalOwner = body.getPhysicalOwner();
			Vector<LocalTableIndex> listOfTables = body.getListOfTables();
			
			for (int i = 0; i < listOfTables.size(); i++) {
				LocalTableIndex tableIndex = listOfTables.get(i);
				String tableName = tableIndex.getTableName();
				
				this.insertTableIndex(tableIndex, physicalOwner.toString());
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
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException("Fail to insert table index", e);
		}
	}

	/**
	 * insert or update table index
	 * @param tableName
	 */
	public void insertTableIndex(LocalTableIndex tableIndex,
			String physicalOwner) {
		String tableName = tableIndex.getTableName();
		String indexValue = physicalOwner;

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
				String value = rs.getString(1);
				rs.close();
				if (!value.contains(indexValue)){
					value += ":" + indexValue;
					sql = "update table_index ";
					sql += " set val = '" + value + "'";
					sql += " where ind = '" + tableName + "'";
					if (debug)
						System.out.println(sql);
					stmt.execute(sql);
				}
			} else {
				rs.close();
				sql = "insert into table_index(ind, val) values (";
				sql += "'" + tableName + "',";
				sql += "'" + indexValue + "')";
				if (debug)
					System.out.println(sql);
				stmt.execute(sql);
			}


			// index range
			Vector<RangeIndex> rangeIndexOfTable = tableIndex.getRangeIndexOfTable();
			for (int i=0; i < rangeIndexOfTable.size(); i++){
				RangeIndex rangeIndex = rangeIndexOfTable.get(i);
				String index_table_name = " range_index_number ";
				if (rangeIndex.isStringType()){
					index_table_name = " range_index_string ";
				}
				sql = "select lower_bound, upper_bound";
				sql += " from " + index_table_name;
				sql += " where table_name = '" + tableName + "' and column_name = '"+rangeIndex.getColumnName()+"' and val ='" + rangeIndex.getOwner()+"'";
				
				rs = stmt.executeQuery(sql);
				if (rs.next())
				{
					rs.close();
					sql = "update " + index_table_name;
					sql += " set lower_bound = " + rangeIndex.getMinValue()+ ",";
					sql += " upper_bound = " + rangeIndex.getMaxValue();
					sql += " where table_name = '" + tableName + "' and column_name = '"+rangeIndex.getColumnName()+"' and val ='" + rangeIndex.getOwner()+"'";
				}
				else
				{
					rs.close();
					sql = "insert into "+ index_table_name +" values (";
					sql += "'" + tableName + "',";
					sql += "'" + rangeIndex.getColumnName() + "',";
					sql += "'" + rangeIndex.getOwner() + "',";
					sql += rangeIndex.getMinValue()+ ",";
					sql += rangeIndex.getMaxValue()+ ")";
				}
				
				if (debug) 
					System.out.println(sql);
				stmt.execute(sql);
			}
			
			stmt.close();
		} catch (Exception e) {
			System.out.println("Error while inserting table index");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ERP_INSERT_TABLE_INDEX
				.getValue())
			return true;
		return false;
	}
}
