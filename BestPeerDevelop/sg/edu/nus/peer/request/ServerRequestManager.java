/*
 * @(#) ServerRequestManager.java 1.0 2006-2-17
 * 
 * Copyright 2006, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.peer.request;

import java.awt.Window;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JOptionPane;

import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.gui.test.peer.LoginPanel;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.Session;
import sg.edu.nus.peer.event.SPGeneralAction;
import sg.edu.nus.peer.info.LocalTableIndex;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.ERPInsertTableIndexBody;
import sg.edu.nus.protocol.body.SPJoinBody;
import sg.edu.nus.protocol.body.SPLeaveNotifyBody;
import sg.edu.nus.protocol.body.SPLeaveNotifyClientBody;
import sg.edu.nus.protocol.body.SPLeaveUrgentBody;
import sg.edu.nus.protocol.body.SPPassClientBody;
import sg.edu.nus.protocol.body.UserBody;

/**
 * Implement all request operation of the super peer.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-17
 */

public class ServerRequestManager {

	// private members
	private ServerGUI servergui;
	private ServerPeer serverpeer;

	public ServerSchemaMatchingManager schemaMapper;

	public ServerRequestManager() {

	}

	/**
	 * Construct a request manager for server peer.
	 * 
	 * @param serverpeer
	 *            the handler of the <code>ServerPeer</code>
	 */
	public ServerRequestManager(ServerPeer serverpeer) {
		this.serverpeer = serverpeer;
		this.servergui = (ServerGUI) serverpeer.getMainFrame();
		this.schemaMapper = new ServerSchemaMatchingManager(serverpeer);
	}

	/**
	 * @author chensu
	 * @date 2009-4-28
	 * 
	 * @param window
	 * @param user
	 * @param pwd
	 * @param ip
	 * @param port
	 */
	public void performLoginRequest(LoginPanel window, String user, String pwd,
			String ip, int port) {
		try {
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Message loginReq = new Message(new Head(MsgType.LOGIN.getValue()),
					new UserBody(user, pwd, null, info.getIP(), info.getPort(),
							serverpeer.getPeerType()));

			// send login message to bootstrap server
			if (serverpeer.sendMessage(new PhysicalInfo(ip, port), loginReq)) {
				serverpeer.startEventManager(ServerPeer.LOCAL_SERVER_PORT,
						ServerPeer.CAPACITY);
			} else {
				/**
				 * added by chensu, 2009-05-06
				 * enable the login button if fail to send login request to bootstrap server (connection fail)
				 */
				this.servergui.getOperatePanel().getLoginPanel()
						.resetLoginBtn();
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(servergui, "Failed to connect to bootstrap!");
			this.servergui.getOperatePanel().getLoginPanel()
			.resetLoginBtn();
	//

		}
	}

	public void performLoginRequest(String user, String pwd,
			String ip, int port) {
		try {
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Message loginReq = new Message(new Head(MsgType.LOGIN.getValue()),
					new UserBody(user, pwd, null, info.getIP(), info.getPort(),
							serverpeer.getPeerType()));

			// send login message to bootstrap server
			if (serverpeer.sendMessage(new PhysicalInfo(ip, port), loginReq)) {
				serverpeer.startEventManager(ServerPeer.LOCAL_SERVER_PORT,
						ServerPeer.CAPACITY);
			} else {
				/**
				 * added by chensu, 2009-05-06
				 * enable the login button if fail to send login request to bootstrap server (connection fail)
				 */
				this.servergui.getOperatePanel().getLoginPanel()
						.resetLoginBtn();
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(servergui, "Failed to connect to bootstrap!");
			this.servergui.getOperatePanel().getLoginPanel()
			.resetLoginBtn();
	//

		}
	}
	
	/**
	 * Perform a REGISTER request to the bootstrap server, who is responsible
	 * for registering the peer information to the back-end database and
	 * returning all online super peer information to the newcomer for letting
	 * it to select one as the bootstrap to join the super peer network.
	 * 
	 * @param window
	 *            the handler of the login window
	 * @param user
	 *            the user identifier
	 * @param pwd
	 *            the password
	 * @param ip
	 *            the IP address of the bootstrap server
	 * @param port
	 *            the port of the bootstrap server
	 * @param email
	 *            the email address of the register user
	 */
	public void performRegisterRequest(Window window, String user, String pwd,
			String ip, int port, String email) {
		try {
			Head head = new Head(MsgType.REGISTER.getValue());
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Body body = new UserBody(user, pwd, email, info.getIP(), info
					.getPort(), serverpeer.getPeerType());

			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);
		} catch (Exception e) {
			LogManager.LogException(
					"Fail to open connection to bootstrap server", e);

			JOptionPane.showMessageDialog(servergui,
					"Cannot connect to Bootstrap");
		}
	}

	/**
	 * Perform a LOGOUT (I_WILL_LEAVE) request to the bootstrap server, who is
	 * responsible for removing the registered information from the
	 * <code>OnlinePeerManager</code> and updating the UI components.
	 * <p>
	 * If the super peer signs out successfully, then another online super peer
	 * may be selected to replace the empty position of the super peer in order
	 * to maintain the correctness of the network structure.
	 * 
	 * @param ip
	 *            the IP address of the bootstrap server
	 * @param port
	 *            the port of the bootstrap server
	 * @return if logout successfully, return <code>true</code>; otherwise,
	 *         return <code>false</code>
	 */
	public boolean performLogoutRequest(String ip, int port) {
		try {
			Head head = new Head(MsgType.I_WILL_LEAVE.getValue());

			Session session = Session.getInstance();
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Body body = new UserBody(session.getUserID(), "", "", info.getIP(),
					info.getPort(), serverpeer.getPeerType());

			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);

			performLeaveRequest();

			return true;
		} catch (Exception e) {
			LogManager.LogException(
					"Fail to open connection to bootstrap server", e);

			JOptionPane.showMessageDialog(servergui,
					"Cannot connect to Bootstrap");
			return false;
		}
	}

	/**
	 * Perform a SP_LOGIN request to an online super peer, who will be
	 * responsible for routing the SP_JOIN request to the proper super peer, and
	 * initializing the routing table and other necessary information of the
	 * newcomer.
	 * 
	 * @param ip
	 *            the IP address of an online super peer
	 * @param port
	 *            the port of the online super per
	 * @return if join the super peer network successfully, return
	 *         <code>true</code>; otherwise, return <code>false</code>
	 */
	public boolean performJoinRequest(String ip, int port) {
		try {
			Head head = new Head(MsgType.SP_JOIN.getValue());
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Body body = new SPJoinBody(info, null, info, null);
			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);
			return true;
		} catch (Exception e) {
			LogManager.LogException(
					"Fail to open connection to bootstrap server", e);
			JOptionPane.showMessageDialog(servergui,
					"Cannot connect to selected super peer");
			return false;
		}
	}

	/**
	 * Perform a JOIN_SUCCESS request to the bootstrap server, who will add the
	 * registered information to <code>OnlinePeerManager</code>/
	 * 
	 * @param ip
	 *            the IP address of the bootstrap server
	 * @param port
	 *            the port of the bootstrap server
	 * @return if join operation is success, return <code>true</code>;
	 *         otherwise, return <code>false</code>
	 */
	public boolean performSuccessJoinRequest(String ip, int port) {
		try {
			Head head = new Head(MsgType.JOIN_SUCCESS.getValue());

			Session session = Session.getInstance();
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Body body = new UserBody(session.getUserID(), "", "", info.getIP(),
					info.getPort(), serverpeer.getPeerType());

			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);
			return true;
		} catch (Exception e) {
			LogManager.LogException(
					"Fail to open connection to bootstrap server", e);
			JOptionPane.showMessageDialog(servergui,
					"Cannot connect to Bootstrap");
			return false;
		}
	}

	/**
	 * Perform a JOIN_FAILURE request to the bootstrap server, who will remove
	 * the registered information from <code>OnlinePeerManager</code>.
	 * 
	 * @param ip
	 *            the IP address of the bootstrap server
	 * @param port
	 *            the port of the bootstrap server
	 * @return if join operation is canceled, return <code>true</code>;
	 *         otherwise, return <code>false</code>
	 */
	public boolean performCancelJoinRequest(String ip, int port) {
		try {
			Head head = new Head(MsgType.JOIN_FAILURE.getValue());

			Session session = Session.getInstance();
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Body body = new UserBody(session.getUserID(), "", "", info.getIP(),
					info.getPort(), serverpeer.getPeerType());

			Message message = new Message(head, body);
			serverpeer.sendMessage(new PhysicalInfo(ip, port), message);
			return true;
		} catch (Exception e) {
			LogManager.LogException(
					"Fail to open connection to bootstrap server", e);
			JOptionPane.showMessageDialog(servergui,
					"Connect to Bootstrap Failed");
			return false;
		}
	}

	/**
	 * Perform a LEAVE request when a super peer signs off
	 * 
	 */
	public void performLeaveRequest() {
		try {
			Head head = new Head();
			head.setMsgType(MsgType.SP_LEAVE_URGENT.getValue());
			Body body;
			Message message;
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			PhysicalInfo physicalReplacer = null;
			LogicalInfo logicalReplacer = null;
			PeerInfo[] attachedPeers;
			int i, j, listSize = serverpeer.getListSize();

			for (i = listSize - 1; i >= 0; i--) {
				TreeNode treeNode = serverpeer.removeListItem(i);
				if (treeNode.getRole() == 1) {
					treeNode.getContent().setData(SPGeneralAction.loadData());

					if ((treeNode.getParentNode() == null)
							&& (treeNode.getLeftChild() == null)
							&& (treeNode.getRightChild() == null))
						return;

					if (treeNode.getParentNode() != null) {
						// a leave urgent should be sent to the parent node
						physicalReplacer = treeNode.getParentNode()
								.getPhysicalInfo();
						logicalReplacer = treeNode.getParentNode()
								.getLogicalInfo();
					} else {
						// a leave urgent should be sent to a child
						if (treeNode.getLeftChild() != null) {
							physicalReplacer = treeNode.getLeftChild()
									.getPhysicalInfo();
							logicalReplacer = treeNode.getLeftChild()
									.getLogicalInfo();
						} else {
							physicalReplacer = treeNode.getRightChild()
									.getPhysicalInfo();
							logicalReplacer = treeNode.getRightChild()
									.getLogicalInfo();
						}
					}

					// 1. send a leave urgent request
					body = new SPLeaveUrgentBody(info, treeNode
							.getLogicalInfo(), treeNode, logicalReplacer);
					message = new Message(head, body);
					serverpeer.sendMessage(physicalReplacer, message);

					attachedPeers = PeerMaintainer.getInstance().getClients();
					if (attachedPeers.length > 0) {
						// 2. pass current attached client peers
						head.setMsgType(MsgType.SP_PASS_CLIENT.getValue());
						body = new SPPassClientBody(info, treeNode
								.getLogicalInfo(), attachedPeers,
								logicalReplacer);
						message = new Message(head, body);
						serverpeer.sendMessage(physicalReplacer, message);

						// 3. send a leave notify to attached client peers
						Head headNotifyClient = new Head();
						headNotifyClient
								.setMsgType(MsgType.SP_LEAVE_NOTIFY_CLIENT
										.getValue());
						int size = attachedPeers.length;
						for (j = 0; j < size; j++) {
							PeerInfo client = attachedPeers[j];
							PhysicalInfo clientpeer = new PhysicalInfo(client
									.getInetAddress(), client.getPort());
							body = new SPLeaveNotifyClientBody(info,
									physicalReplacer);
							message = new Message(headNotifyClient, body);
							serverpeer.sendMessage(clientpeer, message);
						}
					}

					// 4. send a leave notify to other linked nodes
					Head headNotify = new Head();
					headNotify.setMsgType(MsgType.SP_LEAVE_NOTIFY.getValue());
					if (treeNode.getLeftChild() != null) {
						body = new SPLeaveNotifyBody(info, treeNode
								.getLogicalInfo(), physicalReplacer, 0, 0,
								treeNode.getLeftChild().getLogicalInfo());
						message = new Message(headNotify, body);
						serverpeer.sendMessage(treeNode.getLeftChild()
								.getPhysicalInfo(), message);
					}
					if (treeNode.getRightChild() != null) {
						body = new SPLeaveNotifyBody(info, treeNode
								.getLogicalInfo(), physicalReplacer, 0, 0,
								treeNode.getRightChild().getLogicalInfo());
						message = new Message(headNotify, body);
						serverpeer.sendMessage(treeNode.getRightChild()
								.getPhysicalInfo(), message);

					}
					if (treeNode.getLeftAdjacentNode() != null) {
						body = new SPLeaveNotifyBody(info, treeNode
								.getLogicalInfo(), physicalReplacer, 2, 0,
								treeNode.getLeftAdjacentNode().getLogicalInfo());
						message = new Message(headNotify, body);
						serverpeer.sendMessage(treeNode.getLeftAdjacentNode()
								.getPhysicalInfo(), message);
					}
					if (treeNode.getRightAdjacentNode() != null) {
						body = new SPLeaveNotifyBody(info, treeNode
								.getLogicalInfo(), physicalReplacer, 1, 0,
								treeNode.getRightAdjacentNode()
										.getLogicalInfo());
						message = new Message(headNotify, body);
						serverpeer.sendMessage(treeNode.getRightAdjacentNode()
								.getPhysicalInfo(), message);

					}
					for (j = 0; j < treeNode.getLeftRoutingTable()
							.getTableSize(); j++) {
						RoutingItemInfo nodeInfo = treeNode
								.getLeftRoutingTable().getRoutingTableNode(j);
						if (nodeInfo != null) {
							body = new SPLeaveNotifyBody(info, treeNode
									.getLogicalInfo(), physicalReplacer, 4, j,
									nodeInfo.getLogicalInfo());
							message = new Message(headNotify, body);
							serverpeer.sendMessage(nodeInfo.getPhysicalInfo(),
									message);

						}
					}
					for (j = 0; j < treeNode.getRightRoutingTable()
							.getTableSize(); j++) {
						RoutingItemInfo nodeInfo = treeNode
								.getRightRoutingTable().getRoutingTableNode(j);
						if (nodeInfo != null) {
							body = new SPLeaveNotifyBody(info, treeNode
									.getLogicalInfo(), physicalReplacer, 3, j,
									nodeInfo.getLogicalInfo());
							message = new Message(headNotify, body);
							serverpeer.sendMessage(nodeInfo.getPhysicalInfo(),
									message);

						}
					}
				}
			}
		} catch (Exception e) {
			LogManager.LogException(
					"Fail to open connection to bootstrap server", e);
			JOptionPane.showMessageDialog(servergui,
					"Failed to send Leave requests to linked nodes");
			return;
		}
	}

	public boolean performStabilizeRequest(String ip, int port, Message msg) {
		return false;
	}

	public boolean performRefreshRequest(String ip, int port, Message msg) {
		return false;
	}

	public void performCheckImbalance(int seconds) {
		new CheckImbalance(serverpeer, seconds);
	}

	/**
	 * Mihai
	 * 
	 * drops the existing schema and creates a new one as indicated by the
	 * bootstrap node
	 * 
	 * @param newSchema
	 */
	public void performSchemaUpdate(String newSchema) {

		Connection conn = ServerPeer.conn_globalSchema;
		Statement stmt;

		try {

			stmt = conn.createStatement();

			// drop the existing database
			String sql = "DROP DATABASE IF EXISTS " + ServerPeer.GLOBAL_DB;
			stmt.executeUpdate(sql);
			// create a new one and give it this schema

			sql = "CREATE DATABASE " + ServerPeer.GLOBAL_DB;
			stmt.executeUpdate(sql);
			stmt.executeQuery("USE " + ServerPeer.GLOBAL_DB);

			String[] tables = newSchema.split(";");

			for (int i = 0; i < tables.length; i++) {
				if (tables[i].length() > 0) {
					stmt.executeUpdate(tables[i]);
				}
			}

		} catch (Exception e) {
			LogManager.LogException("Failed to update schema", e);
			JOptionPane.showMessageDialog(servergui, "Failed to update schema");
			return;
		}
		try {
			createMatchesTable(conn);
		} catch (Exception e) {
			LogManager.LogException("Failed to create schema mapping table", e);
			JOptionPane.showMessageDialog(servergui,
					"Failed to create schema mapping table");
		}

	}

	/**
	 * mihai, june 24th, 2008
	 * 
	 * creates a table to map the schema mappings.
	 */
	public void createMatchesTable(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS "
				+ ServerPeer.MATCHES_DB);
		stmt.executeQuery("USE " + ServerPeer.MATCHES_DB);
		stmt
				.executeUpdate("CREATE TABLE IF NOT EXISTS Matches ("
						+ "sourceDB VARCHAR(20) NOT NULL DEFAULT 'BestPeerServerLS' COMMENT 'the name of the source database',"
						+ "sourceTable VARCHAR(30) NOT NULL COMMENT 'the name of the source table',"
						+ "sourceVersion INT COMMENT 'the version of the source database, in case versioning is used',"
						+ "sourceColumn VARCHAR(50) NOT NULL COMMENT 'the name of the source column',"
						+ "sourceType VARCHAR(30) NOT NULL COMMENT 'the type of the source column',"
						+ "targetDB VARCHAR(20) NOT NULL DEFAULT 'BestPeerServerGS' COMMENT 'the name of the target database',"
						+ "targetTable VARCHAR(30) NOT NULL COMMENT 'the name of the target table',"
						+ "targetVersion INT COMMENT 'the version of the target database, in case versioning is used',"
						+ "targetColumn VARCHAR(50) NOT NULL COMMENT 'the name of the target column',"
						+ "targetType VARCHAR(30) NOT NULL COMMENT 'the type of the target column',"
						+ "PRIMARY KEY (sourceDB,sourceTable,sourceVersion,sourceColumn)"
						+ ") CHARACTER SET utf8 COLLATE utf8_bin");
	}

	/**
	 * Implemented by Quang Hieu, Jun 01, 2008 Share a table
	 * 
	 * @param tableName
	 */
	public void shareDatabase(Vector<String> listOfTables,
			Vector<Vector<String>> listOfIndexedColumns,
			Vector<Vector<String>> listOfUnindexedColumns) {
		Message result = null;
		Head head = new Head();

		try {
			PhysicalInfo physicalNodeInfo = this.serverpeer.getPhysicalInfo();

			// index tables
			Vector<LocalTableIndex> tableIndices = new Vector<LocalTableIndex>();
			for (int i = 0; i < listOfTables.size(); i++)
				tableIndices.add(new LocalTableIndex(listOfTables.get(i)));
			head.setMsgType(MsgType.ERP_INSERT_TABLE_INDEX.getValue());
			ERPInsertTableIndexBody body = new ERPInsertTableIndexBody(
					physicalNodeInfo, physicalNodeInfo, tableIndices, null);
			result = new Message(head, body);
			this.serverpeer.sendMessage(physicalNodeInfo, result);

			// index columns

			// index data
		} catch (Exception e) {
			System.out.println("Error while sharing table");
			e.printStackTrace();
		}
	}

	/**
	 * mihai, june 27th, 2008
	 * 
	 * @param sourceColumn
	 * @param targetColumn
	 * @return
	 * @see ServerGUI.matchColumns
	 */
	public int performColumnMatch(String sourceColumn, String targetColumn) {
		return schemaMapper.addMatch(sourceColumn, targetColumn);
	}

	/**
	 * mihai, june 27th, 2008
	 * 
	 * @param sourceColumn
	 * @param targetColumn
	 * @return
	 * @see ServerGUI.unmatchColumns
	 */
	public int performColumnUnmatch(String sourceColumn, String targetColumn) {
		return schemaMapper.removeMatch(sourceColumn, targetColumn);
	}
}
