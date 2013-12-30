package sg.edu.nus.dbconnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.bestpeer.indexdata.RangeIndex;
import sg.edu.nus.db.synchronizer.GlobalSchema;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.LocalColumnIndex;
import sg.edu.nus.peer.info.LocalDataIndex;
import sg.edu.nus.peer.info.LocalTableIndex;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ERPDeleteDataIndexBody;
import sg.edu.nus.protocol.body.ERPDeleteTableIndexBody;
import sg.edu.nus.protocol.body.ERPInsertColumnIndexBody;
import sg.edu.nus.protocol.body.ERPInsertDataIndexBody;
import sg.edu.nus.protocol.body.ERPInsertTableIndexBody;
import sg.edu.nus.protocol.body.ERPUpdateDataIndexBody;

/**
 * 
 * @author Quang Hieu
 * @author Wu Sai Modified by David Jiang
 */
public class DBIndex {

	private ServerPeer serverPeer;
	private PhysicalInfo physicalInfo;
	private LogicalInfo logicalInfo;
	private Vector<String> listOfTables;
	/**
	 * the hash table stores the tables and their columns
	 */
	private Hashtable<String, Vector<String>> columnInTables;

	private Hashtable<String, Vector<RangeIndex>> rangeIndexInTables;

	/**
	 * Constructor
	 * 
	 * @param dbServer
	 * @param serverPeer
	 * @param physicalInfo
	 * @param logicalInfo
	 * @param listOfTables
	 * @param listOfColumnsInTables
	 */
	public DBIndex(ServerPeer serverPeer, PhysicalInfo physicalInfo,
			LogicalInfo logicalInfo, Vector<String> listOfTables,
			Hashtable<String, Vector<RangeIndex>> rangeIndexInTables,
			Hashtable<String, Vector<String>> columnInTables)
	{
		this.serverPeer = serverPeer;
		this.physicalInfo = physicalInfo;
		this.logicalInfo = logicalInfo;
		this.listOfTables = listOfTables;
		this.columnInTables = columnInTables;
		this.rangeIndexInTables = rangeIndexInTables;
	}

	/**
	 * General method creating all types of indices
	 * 
	 */
	public void indexDatabase() {
		// 1. create table index
		this.createTableIndex();
	}
	
	/**
	 * General method deleting all types of indices
	 * 
	 */
	public void deleteIndex() {
		// 1. create table index
		this.deleteTableIndex();

	}
	
	/**
	 * Create table index for indexed tables
	 * 
	 */
	private void createTableIndex() {
		try {
			Vector<LocalTableIndex> listOfTables = new Vector<LocalTableIndex>();
			for (int i = 0; i < this.listOfTables.size(); i++) {

				String table = this.listOfTables.get(i);
				LocalTableIndex localTableIndex = new LocalTableIndex(table);
				localTableIndex.setRangeIndexOfTable(rangeIndexInTables
						.get(table));

				listOfTables.add(localTableIndex);
			}

			Head head = new Head();
			head.setMsgType(MsgType.ERP_INSERT_TABLE_INDEX.getValue());
			ERPInsertTableIndexBody body = new ERPInsertTableIndexBody(
					this.physicalInfo, this.physicalInfo, listOfTables,
					this.logicalInfo);
			Message message = new Message(head, body);
			
			//send to itself to begin BATON insert
			this.serverPeer.sendMessage(this.physicalInfo, message);
			
			//send to bootstrap to replicate index
			this.serverPeer.sendMessage(new PhysicalInfo(ServerPeer.BOOTSTRAP_SERVER, ServerPeer.BOOTSTRAP_SERVER_PORT), message);
			
		} catch (Exception e) {
			System.out.println("Fail to create table index");
			e.printStackTrace();
		}
	}

	private void deleteTableIndex() {
		try {
			Vector<LocalTableIndex> listOfTables = new Vector<LocalTableIndex>();
			for (int i = 0; i < this.listOfTables.size(); i++) {

				String table = this.listOfTables.get(i);
				LocalTableIndex localTableIndex = new LocalTableIndex(table);
				localTableIndex.setRangeIndexOfTable(rangeIndexInTables
						.get(table));

				listOfTables.add(localTableIndex);
			}

			Head head = new Head();
			head.setMsgType(MsgType.ERP_DELETE_TABLE_INDEX.getValue());
			ERPDeleteTableIndexBody body = new ERPDeleteTableIndexBody(
					this.physicalInfo, this.physicalInfo, listOfTables,
					this.logicalInfo);
			Message message = new Message(head, body);
			
			//send to itself to begin BATON delete
			this.serverPeer.sendMessage(this.physicalInfo, message);
			
			//send to boostrap since it also store one copy
			this.serverPeer.sendMessage(new PhysicalInfo(ServerPeer.BOOTSTRAP_SERVER, ServerPeer.BOOTSTRAP_SERVER_PORT), message);
			
		} catch (Exception e) {
			System.out.println("Fail to delete table index");
			e.printStackTrace();
		}
	}
	
	/**
	 * Create column index for indexed columns
	 * 
	 */
	public void createColumnIndex() {
		try {
			// create list of column index
			Vector<LocalColumnIndex> listOfColumns = new Vector<LocalColumnIndex>();
			for (Enumeration<String> e = columnInTables.keys(); e
					.hasMoreElements();) {
				String column = (String) e.nextElement();
				Vector<String> listOfTablesContainingColumn = columnInTables
						.get(column);
				LocalColumnIndex localColumnIndex = new LocalColumnIndex(
						column, listOfTablesContainingColumn);
				listOfColumns.add(localColumnIndex);
			}

			// send message for processing
			Head head = new Head();
			head.setMsgType(MsgType.ERP_INSERT_COLUMN_INDEX.getValue());
			ERPInsertColumnIndexBody body = new ERPInsertColumnIndexBody(
					this.physicalInfo, this.physicalInfo, listOfColumns,
					this.logicalInfo);
			Message message = new Message(head, body);
			this.serverPeer.sendMessage(this.physicalInfo, message);
		} catch (Exception e) {
			System.out.println("Fail to create column index");
			e.printStackTrace();
		}
	}

	/**
	 * Create data index for extracted data
	 * 
	 */
	public void createDataIndex() {
		try {

			Connection conn = ServerPeer.conn_exportDatabase;
			Statement stmt = conn.createStatement();
			Vector<LocalDataIndex> listOfData = new Vector<LocalDataIndex>();
			String sql = "Select * from " + GlobalSchema.VECTOR_TABLE;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String term = rs.getString("keyword");
				String bitmapCode = rs.getString("vector");
				LocalDataIndex localDataIndex = new LocalDataIndex(term,
						bitmapCode);
				listOfData.add(localDataIndex);
			}

			// send message for processing
			Head head = new Head();
			head.setMsgType(MsgType.ERP_INSERT_DATA_INDEX.getValue());
			ERPInsertDataIndexBody body = new ERPInsertDataIndexBody(
					this.physicalInfo, this.physicalInfo, listOfData,
					ERPInsertDataIndexBody.STRING_TYPE, this.logicalInfo);
			Message message = new Message(head, body);
			this.serverPeer.sendMessage(this.physicalInfo, message);
		} catch (Exception e) {
			System.out.println("Fail to create data index");
			e.printStackTrace();
		}
	}

	public void updateDataIndex() {
		try {
			Vector<LocalDataIndex> insertListOfData = new Vector<LocalDataIndex>();
			Vector<LocalDataIndex> deleteListOfData = new Vector<LocalDataIndex>();
			Vector<LocalDataIndex> updateListOfData = new Vector<LocalDataIndex>();

			Connection conn = ServerPeer.conn_exportDatabase;
			Statement stmt = conn.createStatement();

			String sql_queryVectorUpdate = "Select * from "
					+ GlobalSchema.VECTOR_UPDATE_TABLE;

			ResultSet rs_queryVectorUpdate = stmt
					.executeQuery(sql_queryVectorUpdate);
			while (rs_queryVectorUpdate.next()) {
				String term = rs_queryVectorUpdate.getString("keyword");
				String bitmapCode = rs_queryVectorUpdate.getString("vector");
				String operationType = rs_queryVectorUpdate
						.getString("operation");

				if (operationType.equals("insert")) {
					LocalDataIndex localDataIndex = new LocalDataIndex(term,
							bitmapCode);
					insertListOfData.add(localDataIndex);
				} else if (operationType.equals("delete")) {
					LocalDataIndex localDataIndex = new LocalDataIndex(term,
							bitmapCode);
					deleteListOfData.add(localDataIndex);
				} else if (operationType.equals("update")) {
					LocalDataIndex localDataIndex = new LocalDataIndex(term,
							bitmapCode);
					updateListOfData.add(localDataIndex);
				}
			}

			// send message for processing
			Head head_insert = new Head();
			Head head_delete = new Head();
			Head head_update = new Head();

			head_insert.setMsgType(MsgType.ERP_INSERT_DATA_INDEX.getValue());
			head_delete.setMsgType(MsgType.ERP_DELETE_DATA_INDEX.getValue());
			head_update.setMsgType(MsgType.ERP_UPDATE_DATA_INDEX.getValue());

			ERPInsertDataIndexBody body_insert = new ERPInsertDataIndexBody(
					this.physicalInfo, this.physicalInfo, insertListOfData,
					ERPInsertDataIndexBody.STRING_TYPE, this.logicalInfo);
			ERPInsertDataIndexBody body_delete = new ERPInsertDataIndexBody(
					this.physicalInfo, this.physicalInfo, deleteListOfData,
					ERPDeleteDataIndexBody.STRING_TYPE, this.logicalInfo);
			ERPInsertDataIndexBody body_update = new ERPInsertDataIndexBody(
					this.physicalInfo, this.physicalInfo, updateListOfData,
					ERPUpdateDataIndexBody.STRING_TYPE, this.logicalInfo);

			Message message_insert = new Message(head_insert, body_insert);
			Message message_delete = new Message(head_delete, body_delete);
			Message message_update = new Message(head_update, body_update);

			this.serverPeer.sendMessage(this.physicalInfo, message_insert);
			this.serverPeer.sendMessage(this.physicalInfo, message_delete);
			this.serverPeer.sendMessage(this.physicalInfo, message_update);

		} catch (Exception e) {
			System.out.println("Fail to create data index");
			e.printStackTrace();
		}
	}

	public String updateBitmapCode(String bitmapCode, int pos) {
		String prefix = "", posfix = "";
		int length = bitmapCode.length();
		if (pos > 0)
			prefix = bitmapCode.substring(0, pos);
		if (pos < length - 1)
			posfix = bitmapCode.substring(pos, length - 1);
		return prefix + "1" + posfix;
	}

	public String createBitmapCode(int length, int pos) {
		String bitmapCode = "";
		for (int i = 0; i < pos; i++)
			bitmapCode += "0";
		bitmapCode += "1";
		for (int i = pos + 1; i < length; i++)
			bitmapCode += "0";
		return bitmapCode;
	}
}
