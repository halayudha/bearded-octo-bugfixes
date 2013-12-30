package sg.edu.nus.bestpeer.joinprocessing;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;
import java.util.Vector;

import sg.edu.nus.accesscontrol.AccessControlHelper;
import sg.edu.nus.bestpeer.queryprocessing.RemoteTableInfo;
import sg.edu.nus.bestpeer.queryprocessing.Win32;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.event.ActionAdapter;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.QueryPeerResultBody;
import sg.edu.nus.protocol.body.TableRetrieval;
import sg.edu.nus.sqlparser.Attribute;
import sg.edu.nus.sqlparser.Condition;
import sg.edu.nus.sqlparser.SelectStatement;
import sg.edu.nus.util.MetaDataAccess;
import sg.edu.nus.util.NameUtil;

public class JoinProcessingListener extends ActionAdapter {

	ServerPeer serverpeer = null;
	ServerGUI servergui = null;
	PhysicalInfo processingPeer = null;
	Random rand = null;
	String[][] columns = null;

	private static int distinctValue = 1;

	public synchronized int getDistinctValue() {
		int result = distinctValue;
		distinctValue++;
		return result;
	}

	public JoinProcessingListener(AbstractMainFrame gui) {
		super(gui);
		servergui = (ServerGUI) gui;
		serverpeer = (ServerPeer) gui.peer();
		try {
			processingPeer = serverpeer.getPhysicalInfo();
		} catch (UnknownHostException e) {
			LogManager.LogException("Can't find peer address for join queries",
					e);
		}
		rand = new Random();
		rand.setSeed(System.currentTimeMillis());
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		MsgBodyJoinProcessing msgBody = (MsgBodyJoinProcessing) msg.getBody();
		AgentJoinObject agentJoinObject = msgBody.getAgentJoinObject();

		int processingIndex = agentJoinObject.processingIndex;

		String tempTable = "TempJoin_" + processingPeer.getPort();

		TablePartition partition = msgBody.getAgentJoinObject()
				.getTablePartition(processingIndex);
		String[][] partitionColumns = MetaDataAccess.metaGetColumnsWithType(
				ServerPeer.conn_metabestpeerdb, partition.table_name);

		// DBServer server = new DBServer(ServerPeer.EXPORTED_DB);
		Connection conn = ServerPeer.conn_exportDatabase;
		try {

			Statement stmt = conn.createStatement();
			int limit = 990;
			if (processingIndex == 0) {

				// create first temp result
				tempTable = generateTempTableName(tempTable);
				String sqlSelect = " SELECT ";
				String sqlCreate = " CREATE table " + tempTable + "(";

				for (int i = 0; i < partitionColumns.length; i++) {
					sqlSelect += partitionColumns[i][0];
					sqlCreate += partitionColumns[i][0] + " "
							+ partitionColumns[i][1];
					if (i < partitionColumns.length - 1) {
						sqlSelect += ", ";
						sqlCreate += ", ";
					}
				}
				sqlSelect += " from " + partition.table_name + " limit "
						+ limit;
				sqlCreate += ")";

				// update tempResultcolumn for next node
				columns = partitionColumns;

				sqlCreate += " " + sqlSelect;
				// execute query
				stmt.execute(sqlCreate);

				// continue to send join query message to next peer
				forwardJoinProcessing(msgBody, tempTable);
			}

			if (processingIndex > 0) {

				// fetch remote tempresult
				String remoteTable = agentJoinObject.remoteTempTable;
				RemoteTableInfo tableInfo = new RemoteTableInfo();
				tableInfo.setRemoteTableHost(msgBody.getSender());
				tableInfo.setRemoteTableName(remoteTable);
				String[][] recievedColumns = msgBody.getColumns();

				String downloadedTableName = remoteTable;

				fetchRemoteTempTable(downloadedTableName, recievedColumns,
						tableInfo);

				// assume two join attribute are desinged to have same name
				String joinAttribName = null;
				for (int i = 0; i < partitionColumns.length; i++) {
					for (int j = 0; j < recievedColumns.length; j++) {
						if (partitionColumns[i][0]
								.equals(recievedColumns[j][0])) {
							joinAttribName = new String(partitionColumns[i][0]);
						}
					}
				}

				columns = new String[recievedColumns.length
						+ partitionColumns.length - 1][2];// count join
				// attribute
				// once
				int col = 0;
				for (int j = 0; j < recievedColumns.length; j++) {
					columns[col][0] = recievedColumns[j][0];
					columns[col][1] = recievedColumns[j][1];
					col++;
				}
				for (int i = 0; i < partitionColumns.length; i++) {
					if (!joinAttribName.equals(partitionColumns[i][0])) {
						columns[col][0] = partitionColumns[i][0];
						columns[col][1] = partitionColumns[i][1];
						col++;
					}
				}

				// join and create new temp result
				tempTable = generateTempTableName(tempTable);

				String sqlSelect = " SELECT ";
				String sqlCreate = " CREATE table " + tempTable + "(";

				for (int i = 0; i < columns.length; i++) {
					if (joinAttribName.equals(columns[i][0])) {
						sqlSelect += partition.table_name + "." + columns[i][0];
					} else {
						sqlSelect += columns[i][0];
					}
					sqlCreate += columns[i][0] + " " + columns[i][1];
					if (i < columns.length - 1) {
						sqlSelect += ", ";
						sqlCreate += ", ";
					}
				}
				sqlCreate += ")";
				sqlSelect += " from " + partition.table_name + ", "
						+ downloadedTableName;
				sqlSelect += " where " + partition.table_name + "."
						+ joinAttribName + " = " + downloadedTableName + "."
						+ joinAttribName;

				// enforce access control
				String userName = msgBody.getUserName();
				String accessControl = AccessControlHelper
						.getAccessControlClause(partition.table_name, userName);
				if (accessControl != null) {
					sqlSelect += " and " + accessControl;
				}
				sqlSelect += " limit  " + limit;
				sqlCreate += " " + sqlSelect;

				// execute query for new join

				stmt.execute(sqlCreate);

				// drop table downloaded from previous step
				String sqlDelete = " drop table " + downloadedTableName;

				stmt.execute(sqlDelete);

				// forward temp result...
				if (processingIndex == agentJoinObject.joinPath.size() - 1) {
					// refine/project column wanted by query
					String orgSql = msgBody.getQueryString();
					SelectStatement selectStat = new SelectStatement(orgSql);
					Vector projectionList = selectStat.getProjectionList();
					String finalTable = generateTempTableName("TempJoin_"
							+ processingPeer.getPort());
					String[][] finalColumns = new String[projectionList.size()][2];
					for (int i = 0; i < projectionList.size(); i++) {
						Attribute att = (Attribute) projectionList.get(i);
						finalColumns[i][0] = new String(att.getColName());
						for (int j = 0; j < columns.length; j++) {
							if (finalColumns[i][0].equals(columns[j][0])) {
								finalColumns[i][1] = new String(columns[j][1]);
								break;
							}
						}
					}
					sqlSelect = " SELECT ";
					sqlCreate = " CREATE table " + finalTable + "(";

					for (int i = 0; i < finalColumns.length; i++) {
						sqlSelect += finalColumns[i][0];
						sqlCreate += finalColumns[i][0] + " "
								+ finalColumns[i][1];
						if (i < finalColumns.length - 1) {
							sqlSelect += ", ";
							sqlCreate += ", ";
						}
					}
					sqlSelect += " from " + tempTable;
					sqlCreate += ")";

					Vector selectionList = selectStat.getSelectionList();
					if (selectionList.size() > 0) {
						sqlSelect += " where ";
						for (int i = 0; i < selectionList.size(); i++) {
							Condition cond = (Condition) selectionList.get(i);
							Attribute left = cond.getLhs();
							String colName = left.getColName();
							sqlSelect += colName + cond.getOperatorString();
							String rightValue = cond.getRightValueString(null);

							for (int j = 0; j < columns.length; j++) {
								if (colName.equals(columns[j][0])) {
									if (columns[j][1].contains("varchar")) {
										rightValue = "'" + rightValue + "'";
									}
								}
							}

							sqlSelect += rightValue;
							if (i < selectionList.size() - 1) {
								sqlSelect += " and ";
							}
						}
					}

					sqlCreate += " " + sqlSelect;

					// execute query

					stmt.execute(sqlCreate);

					// drop table temp
					sqlDelete = " drop table " + tempTable;

					// execute query
					stmt.execute(sqlDelete);

					// send result remotable to querying peer
					sendResultToQueryingPeer(finalTable, msgBody
							.getQueryString(), agentJoinObject.queryPeerAddress);
				} else {
					// continue to send join query message to next peer
					forwardJoinProcessing(msgBody, tempTable);

				}
			}
			stmt.close();
		} catch (Exception e) {
			LogManager.LogException(
					"Exception caught while processing join request", e);
		}
	}

	private void sendResultToQueryingPeer(String tempTableName,
			String queryString, PhysicalInfo queryingPeer) {

		try {
			QueryPeerResultBody result_body = new QueryPeerResultBody();
			Message query_result = null;
			Head result_head = new Head();
			result_head.setMsgType(MsgType.QUERY_PEER_RESULT.getValue());
			result_body.setRemoteTableName(tempTableName);
			result_body.setQueryString(queryString);
			result_body.setRemoteTableHost(processingPeer);
			query_result = new Message(result_head, result_body);
			serverpeer.sendMessage(queryingPeer, query_result);
		} catch (Exception e) {
			LogManager
					.LogException(
							"Exception caught while sending query result back to the querying peer.",
							e);
		}
	}

	private void forwardJoinProcessing(MsgBodyJoinProcessing msgBody,
			String tempTableName) {
		try {
			// update sender
			msgBody.setSender(processingPeer);
			// update forward_temp_resut
			msgBody.getAgentJoinObject().remoteTempTable = tempTableName;
			// update processing index
			msgBody.getAgentJoinObject().processingIndex++;
			// update columns for next step
			msgBody.setColumns(columns);
			int nextProcessingIndex = msgBody.getAgentJoinObject().processingIndex;
			Message joinProcessingMsg = null;
			Head thead = new Head();
			thead.setMsgType(MsgType.JOIN_QUERY_PROCESSING.getValue());
			joinProcessingMsg = new Message(thead, msgBody);
			PhysicalInfo nextProcessingPeer = msgBody.getAgentJoinObject()
					.getAdressOfJoinNode(nextProcessingIndex);
			serverpeer.sendMessage(nextProcessingPeer, joinProcessingMsg);
		} catch (Exception e) {
			LogManager.LogException(
					"Exception caught while forwarding join processing", e);
		}
	}

	private void fetchRemoteTempTable(String newTempTable, String[][] columns,
			RemoteTableInfo remoteTable) {

		String table = newTempTable;

		String sql = "create table " + table + " (";
		for (int i = 0; i < columns.length; i++) {
			sql += columns[i][0];
			sql += " " + columns[i][1];
			if (i < columns.length - 1) {
				sql += ", ";
			}
		}
		sql += ")";
		try {
			Connection conn = ServerPeer.conn_exportDatabase;
			// DBServer server = new DBServer(ServerPeer.EXPORTED_DB);

			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();

			// send out the messages for retrieving the data

			PhysicalInfo localIP = processingPeer;
			int expectedResponse = 0;

			// wait for the event
			Win32.Event event = Win32.CreateEvent(NameUtil
					.queryEventName(table));

			PhysicalInfo receiver = remoteTable.getRemoteTableHost();
			String tableName = remoteTable.getRemoteTableName();

			// send out the message for retrieving
			TableRetrieval tableRetrieval = new TableRetrieval(localIP,
					tableName, table);
			Head head = new Head(MsgType.TABLE_RETRIEVAL.getValue());
			Message msg = new Message(head, tableRetrieval);
			serverpeer.sendMessage(receiver, msg);
			expectedResponse++;

			Win32.WaitForMultipleObjects(event, expectedResponse,
					ServerPeer.queryWaitingTime);

			Win32.CloseEvent(event);
			// results have been received
		} catch (Exception e) {
			LogManager
					.LogException(
							"Exception caught while fetching temporary table from remote peer",
							e);
		}

	}

	private synchronized String generateTempTableName(String originalTable) {

		long idx = rand.nextLong();
		if (idx < 0)
			idx = -idx;
		idx = idx % 1000000;

		String table = originalTable + getDistinctValue() + "_"
				+ Long.toString(idx);

		return table;

	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.JOIN_QUERY_PROCESSING
				.getValue())
			return true;
		return false;
	}

}
