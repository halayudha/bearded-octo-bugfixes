/**
 * Created on Sep 4, 2008
 */
package sg.edu.nus.peer.event;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import sg.edu.nus.accesscontrol.AccessControlHelper;
import sg.edu.nus.accesscontrol.SqlHandler;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.QueryPeerBody;
import sg.edu.nus.protocol.body.QueryPeerResultBody;

/**
 * @author David Jiang
 * 
 */
public class QueryPeerListener extends ActionAdapter {

	public QueryPeerListener(AbstractMainFrame gui) {
		super(gui);
	}

	private synchronized String generateTempTableName(String originalTable) {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		long idx = rand.nextLong();
		if (idx < 0)
			idx = -idx;
		idx = idx % 1000000;

		String table = originalTable + Long.toString(idx);

		return table;
	}

	public void actionPerformed(PhysicalInfo dest, Message msg) {

		QueryPeerBody body = (QueryPeerBody) msg.getBody();

		String sqlString = new String(body.getSqlCommand());

		// enforce access control
		String userName = body.getUserName();
		if (userName != null) {
			sqlString = AccessControlHelper.checkAccessControl(userName,
					sqlString);
		}

		Connection conn = ServerPeer.conn_exportDatabase;

		String table_name = generateTempTableName("Temporary");

		ResultSetMetaData rsmd;
		ResultSet rs = null;
		Statement querystmt = null;
		Statement updatestmt = null;
		
		//delete temp table if exist
		try {
			querystmt = conn.createStatement();
			updatestmt = conn.createStatement();
			String drop = "drop table " + table_name;
			updatestmt.executeUpdate(drop);
		} catch (Exception e) {
			// e.printStackTrace();
		}

		int colCnt = 0;
		int rowCnt = 0;
		try {
			// for first version, we limit the size of returned
			// tuples. in the future, we should dynamically
			// retrieve the tuples
			int number = 2020;
			sqlString += " limit " + number;
			System.out.println("***********  " + sqlString);
			rs = querystmt.executeQuery(sqlString);
			rsmd = rs.getMetaData();
			colCnt = rsmd.getColumnCount();
			
			//create temporary to store the result
			StringBuffer buf = new StringBuffer("create table ");
			buf.append(table_name);
			buf.append(" (");
			
			for (int i = 1; i <= colCnt; ++i) {
				String colName = getColumnName(rsmd.getColumnName(i));

				buf.append(colName + " ");
				String type_name = rsmd.getColumnTypeName(i);
				if (type_name.equals("VARCHAR"))
					// buf.append(type_name + " (10)");
					buf.append("TEXT");
				else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT
						|| rsmd.getColumnType(i) == java.sql.Types.BIT
						|| rsmd.getColumnType(i) == java.sql.Types.BOOLEAN
						|| rsmd.getColumnType(i) == java.sql.Types.DECIMAL
						|| rsmd.getColumnType(i) == java.sql.Types.DOUBLE
						|| rsmd.getColumnType(i) == java.sql.Types.FLOAT
						|| rsmd.getColumnType(i) == java.sql.Types.INTEGER
						|| rsmd.getColumnType(i) == java.sql.Types.NUMERIC
						|| rsmd.getColumnType(i) == java.sql.Types.REAL
						|| rsmd.getColumnType(i) == java.sql.Types.TINYINT)
					buf.append("double");

				else{
					buf.append(type_name);
				}
				
				if (i < colCnt)
					buf.append(", ");
			}
			buf.append(")");
			updatestmt.executeUpdate(buf.toString());

			while (rs.next()) {
				buf = null;
				buf = new StringBuffer("insert into " + table_name + " values(");
				for (int i = 1; i <= colCnt; ++i) {
					if (rsmd.getColumnType(i) != java.sql.Types.BIGINT
							&& rsmd.getColumnType(i) != java.sql.Types.BIT
							&& rsmd.getColumnType(i) != java.sql.Types.BOOLEAN
							&& rsmd.getColumnType(i) != java.sql.Types.DECIMAL
							&& rsmd.getColumnType(i) != java.sql.Types.DOUBLE
							&& rsmd.getColumnType(i) != java.sql.Types.FLOAT
							&& rsmd.getColumnType(i) != java.sql.Types.INTEGER
							&& rsmd.getColumnType(i) != java.sql.Types.NUMERIC
							&& rsmd.getColumnType(i) != java.sql.Types.REAL
							&& rsmd.getColumnType(i) != java.sql.Types.TINYINT)
						buf.append("'");
					String svalue = rs.getString(i);
					svalue = svalue.replace("'", "\\'");
					svalue = svalue.replace("\"", "\\\"");
					buf.append(svalue);
					if (rsmd.getColumnType(i) != java.sql.Types.BIGINT
							&& rsmd.getColumnType(i) != java.sql.Types.BIT
							&& rsmd.getColumnType(i) != java.sql.Types.BOOLEAN
							&& rsmd.getColumnType(i) != java.sql.Types.DECIMAL
							&& rsmd.getColumnType(i) != java.sql.Types.DOUBLE
							&& rsmd.getColumnType(i) != java.sql.Types.FLOAT
							&& rsmd.getColumnType(i) != java.sql.Types.INTEGER
							&& rsmd.getColumnType(i) != java.sql.Types.NUMERIC
							&& rsmd.getColumnType(i) != java.sql.Types.REAL
							&& rsmd.getColumnType(i) != java.sql.Types.TINYINT)
						buf.append("'");
					if (i < colCnt)
						buf.append(",");
				}
				buf.append(")");
				updatestmt.executeUpdate(buf.toString());
				rowCnt++;
			}

			updatestmt.close();
			querystmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Return results
		ServerPeer serverpeer = (ServerPeer) gui.peer();
		QueryPeerResultBody result_body = new QueryPeerResultBody();
		Message query_result = null;
		PhysicalInfo sender = body.getSender();
		Head result_head = new Head();
		result_head.setMsgType(MsgType.QUERY_PEER_RESULT.getValue());
		result_body.setRemoteTableName(table_name);
		result_body.setQueryString(body.getSqlCommand());
		try {
			result_body.setRemoteTableHost(serverpeer.getPhysicalInfo());
		} catch (UnknownHostException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		query_result = new Message(result_head, result_body);
		try {
			serverpeer.sendMessage(sender, query_result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getColumnName(String initialColumn) {
		String[] aggregateTypes = { "sum", "max", "min", "avg", "count" };
		String newColumnName = new String(initialColumn);

		for (int i = 0; i < aggregateTypes.length; i++) {
			if (initialColumn.contains(aggregateTypes[i] + "(")
					|| initialColumn.contains(aggregateTypes[i] + " (")) {
				// recompute
				int pos1 = initialColumn.indexOf("(");
				int pos2 = initialColumn.indexOf(")");
				newColumnName = initialColumn.substring(pos1 + 1, pos2);
				newColumnName = newColumnName.trim();
				break;
			}
		}
		if (newColumnName.contains(".")) {
			int pos = newColumnName.indexOf(".");
			newColumnName = newColumnName.substring(pos + 1);
		}
		return newColumnName;
	}

	public boolean isConsumed(Message message) throws EventHandleException {
		if (message.getHead().getMsgType() == MsgType.QUERY_PEER.getValue())
			return true;
		return false;
	}
}
