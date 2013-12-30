/*
 * @(#) LoginListener.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import sg.edu.nus.accesscontrol.RoleManagement;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.gui.bootstrap.Pane;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.CacheDbIndexBody;
import sg.edu.nus.protocol.body.ConfirmBody;
import sg.edu.nus.protocol.body.FeedbackBody;
import sg.edu.nus.protocol.body.RolemanBody;
import sg.edu.nus.protocol.body.SchemaBody;
import sg.edu.nus.protocol.body.TableTupleBody;
import sg.edu.nus.protocol.body.UserBody;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Implement a listener for processing LOGIN message.
 * <p>
 * <code>DBConnector</code> is used to initiate the connection with the back-end
 * database.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 * 
 */

public class LoginListener extends ActionAdapter {

	public LoginListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {

			// store login status
			int loginSuccess = MsgType.USER_NOT_EXIST.getValue();

			UserBody ub = (UserBody) msg.getBody();
			String user = ub.getUserID();
			String pwd = ub.getPassword();
			String ip = ub.getIP();
			int port = ub.getPort();
			String type = ub.getPeerType();

			gui.peer();
			
			loginSuccess = MetaDataAccess.metaCheckLoginLocalAdmin(
					AbstractPeer.conn_bestpeerdb, user, pwd);

			// login fails
			if (loginSuccess != MsgType.LACK.getValue()) {
				Message message = new Message(new Head(loginSuccess),
						new ConfirmBody());
				gui.peer().sendMessage(dest, message);
				return;
			}

			// if login successes
			PeerMaintainer maintainer = PeerMaintainer.getInstance();
			PeerInfo peerInfo = new PeerInfo(user, ip, port, type);

			Head ackHead = new Head();
			Body ackBody = null;

			if (maintainer.hasServer()) {
				// have online super peers in the network
				ackHead = new Head(MsgType.LACK.getValue());
				ackBody = new FeedbackBody();
				((FeedbackBody) ackBody).setOnlineSuperPeers(maintainer
						.getServers());
			} else if (type.equals(PeerType.SUPERPEER.getValue())) {
				// the current peer is a super peer and there does not exist any
				// other online super peers, then the current super peer
				// directly
				// joins the network
				ackHead = new Head(MsgType.UNIQUE_SUPER_PEER.getValue());
				ackBody = new ConfirmBody();

				// register this peer
				maintainer.put(peerInfo);

				// update table component
				Pane pane = ((BootstrapGUI) gui).getPane();
				pane.firePeerTableRowInserted(pane.getPeerTableRowCount(),
						peerInfo.toObjectArray());
			} else {
				// the current peer is a client peer and there does not exist
				// any
				// other online super peers, then the current peer cannot join
				// the
				// system
				ackHead = new Head(MsgType.NO_ONLINE_SUPER_PEER.getValue());
				ackBody = new ConfirmBody();
			}

			// send login ack to peer
			Message ack = new Message(ackHead, ackBody);
			// ack.serialize(dest, oos);
			gui.peer().sendMessage(dest, ack);

			// send schema to peer
			String schemaStr = ((BootstrapGUI) gui).getPane().getSchemaTreePane().getSchemaString();
			SchemaBody schemaBody = new SchemaBody(schemaStr,MetaDataAccess.metaGetTablesWillAllInfo(AbstractPeer.conn_bestpeerdb), MetaDataAccess.metaGetColumnsWithAllInfo(AbstractPeer.conn_bestpeerdb)); 
			Message schema = new Message(new Head(MsgType.SCHEMA.getValue()), schemaBody);
			// schema.serialize(dest, oos);
			gui.peer().sendMessage(dest, schema);

			// send role setting to peer
			Message role = new Message(new Head(MsgType.ROLE_MAN.getValue()),
					new RolemanBody(new RoleManagement(
							(AbstractPeer.conn_bestpeerdb))));
			// role.serialize(dest, oos);
			gui.peer().sendMessage(dest, role);

			//send cache db index
			Message cacheDbIndex = new Message(new Head(MsgType.CACHE_DB_INDEX.getValue()), new CacheDbIndexBody(((BootstrapGUI)gui).cacheDbIndex));
			gui.peer().sendMessage(dest, cacheDbIndex);
			
			//send GlobalTerm Semantic Mapping to peer
			TupleStream globalTemrStream = new TupleStream(MetaDataAccess.TABLE_SEMANTIC_MAPPING, MetaDataAccess.TABLE_SEMANTIC_MAPPING, dest, (Bootstrap)gui.peer(),AbstractPeer.conn_bestpeerdb);
			new Thread(globalTemrStream).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.LOGIN.getValue())
			return true;
		return false;
	}

	public class TupleStream implements Runnable {
		
		Connection conn = null;
		
		private String tableName;

		private String storedTableName;

		private PhysicalInfo localIP = null;

		private PhysicalInfo requestor;

		private Bootstrap peer;

		public TupleStream(String tname, String storedTable,
				PhysicalInfo requestor,	Bootstrap peer, Connection conn) {
			this.tableName = tname;
			this.storedTableName = storedTable;
			this.requestor = requestor;
			this.peer = peer;
			try {
				localIP = peer.getPhysicalInfo();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			this.conn = conn;
		}

		public void run() {
			try {
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
						peer.sendMessage(requestor, msg);
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
				peer.sendMessage(requestor, msg);

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
					peer.sendMessage(requestor, msg);

				} catch (Exception e1) {

				}
			}
		}
	}
}