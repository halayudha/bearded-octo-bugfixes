package sg.edu.nus.peer.event;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.TableRetrieval;
import sg.edu.nus.protocol.body.TableTupleBody;

/**
 * Handle the message of retrieving data of a specific table
 * 
 * @author chris
 * 
 */
public class TableRetrievalListener extends ActionAdapter {

	public TableRetrievalListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {
			TableRetrieval down = (TableRetrieval) msg.getBody();
			String tname = down.getTable();
			String storedTable = down.getStoredTable();
			PhysicalInfo requestor = down.getSender();

			ServerPeer serverPeer = (ServerPeer) gui.peer();
			PhysicalInfo localIP = serverPeer.getPhysicalInfo();

			// try to send back the data
			TupleStream stream = new TupleStream(tname, storedTable, localIP,
					requestor, serverPeer);
			new Thread(stream).start();
		} catch (Exception e) {
			System.out.println("Fail to process the TableRetrieval Message");
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.TABLE_RETRIEVAL.getValue())
			return true;
		return false;
	}

	public class TupleStream implements Runnable {
		private String tableName;

		private String storedTableName;

		private PhysicalInfo localIP;

		private PhysicalInfo requestor;

		private ServerPeer serverPeer;

		public TupleStream(String tname, String storedTable,
				PhysicalInfo localIP, PhysicalInfo requestor,
				ServerPeer serverPeer) {
			this.tableName = tname;
			this.storedTableName = storedTable;
			this.localIP = localIP;
			this.requestor = requestor;
			this.serverPeer = serverPeer;
		}

		public void run() {
			try {
				Connection conn = ServerPeer.conn_exportDatabase;
				Statement stmt = conn.createStatement();
				String sql = "select * from " + tableName;

				ResultSet rs = stmt.executeQuery(sql);
				int size = 1000;
				Vector<String[]> tuples = new Vector<String[]>();

				ResultSetMetaData rsmd = rs.getMetaData();

				int count = 0; // total result number
				int cnumber = rsmd.getColumnCount();

				while (rs.next()) {
					String[] value = new String[cnumber];
					for (int i = 1; i <= cnumber; i++) {
						Object obj = rs.getObject(i);
						String colVal = "";

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
							colVal += "'";

						String svalue = obj.toString();
						svalue = svalue.replace("'", "\\'");
						svalue = svalue.replace("\"", "\\\"");
						colVal += svalue;

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
							colVal += "'";

						value[i - 1] = colVal;
					}
					tuples.add(value);
					if (tuples.size() == size) {
						TableTupleBody tupleBody = new TableTupleBody(localIP,
								tuples, storedTableName, 0);
						Head result_head = new Head();
						result_head.setMsgType(MsgType.TABLE_DATA.getValue());
						Message msg = new Message(result_head, tupleBody);
						serverPeer.sendMessage(requestor, msg);
						count += size;
						tuples.clear();
					}
				}

				count += tuples.size();
				TableTupleBody tupleBody = new TableTupleBody(localIP, tuples,
						storedTableName, count);
				System.out.println("send tuples :" + tuples.size());
				tupleBody.setFinishFlag();
				Head result_head = new Head();
				result_head.setMsgType(MsgType.TABLE_DATA.getValue());
				Message msg = new Message(result_head, tupleBody);
				serverPeer.sendMessage(requestor, msg);

				// remove the temporary table
				sql = "drop table " + this.tableName;
				stmt.executeUpdate(sql);
				stmt.close();

			} catch (Exception e) {
				// try to finish the stream
				try {
					TableTupleBody tupleBody = new TableTupleBody(localIP,
							new Vector<String[]>(), storedTableName, 0);
					tupleBody.setFinishFlag();
					Head result_head = new Head();
					result_head.setMsgType(MsgType.TABLE_DATA.getValue());
					Message msg = new Message(result_head, tupleBody);
					serverPeer.sendMessage(requestor, msg);

					Connection conn = ServerPeer.conn_exportDatabase;
					Statement stmt = conn.createStatement();
					stmt.executeUpdate("drop table " + this.tableName);
				} catch (Exception e1) {

				}
			}
		}
	}
}
