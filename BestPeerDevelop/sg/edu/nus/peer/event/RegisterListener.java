/*
 * @(#) RegisterListener.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.gui.bootstrap.Pane;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.ConfirmBody;
import sg.edu.nus.protocol.body.FeedbackBody;
import sg.edu.nus.protocol.body.UserBody;

/**
 * Implement a listener for processing REGISTER message. 
 * <p>
 * <code>DBConnector</code> is used to initiate the
 * connection with the back-end database.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class RegisterListener extends ActionAdapter {

	public RegisterListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		Message result = null;
		Head thead = null;

		/* register MySQL driver */
		// disabled by chensu
		// DBConnector.registerDriver();
		try {
			UserBody ub = (UserBody) msg.getBody();
			String user = ub.getUserID();
			String pwd = ub.getPassword();
			String email = ub.getEmail();
			String ip = ub.getIP();
			int port = ub.getPort();
			String type = ub.getPeerType();

			/* insert a user and his password into the user account table */
			String sql = "SELECT * FROM USER_ACCOUNT WHERE user_id='" + user
					+ "' AND type='" + type + "'";

			// Connection conn = DBConnector.getConnection();
			Connection conn = AbstractPeer.bestpeer_db.createDbConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			/* 
			 * if exists such a user,
			 * return a USER_EXIST message
			 */
			if (rs.next()) {
				stmt.close();
				conn.close();

				thead = new Head();
				thead.setMsgType(MsgType.USER_EXISTED.getValue());
				Body tbody = new ConfirmBody();
				Message message = new Message(thead, tbody);
				gui.peer().sendMessage(dest, message);
				return;
			}

			/* insert a user and his password into the user account table */
			sql = "INSERT INTO USER_ACCOUNT VALUES('" + user + "','" + pwd
					+ "','" + email + "','" + type + "')";

			conn = AbstractPeer.bestpeer_db.createDbConnection();
			stmt = conn.createStatement();
			int success = stmt.executeUpdate(sql);

			if (success == 1) // if insert successfully
			{
				PeerMaintainer maintainer = PeerMaintainer.getInstance();
				PeerInfo peerInfo = new PeerInfo(user, ip, port, type);

				/*
				 * if have online super peers
				 */
				if (maintainer.hasServer()) {
					thead = new Head();
					thead.setMsgType(MsgType.RACK.getValue());
					FeedbackBody tbody = new FeedbackBody();
					tbody.setOnlineSuperPeers(maintainer.getServers());

					result = new Message(thead, tbody);
				}

				/*
				 * if no online super peers exists
				 */
				else if (type.equals(PeerType.SUPERPEER.getValue())) {
					thead = new Head();
					thead.setMsgType(MsgType.UNIQUE_SUPER_PEER.getValue());
					Body tbody = new ConfirmBody();

					result = new Message(thead, tbody);

					/* register this peer to online peer manager */
					maintainer.put(peerInfo);

					/* update table component */
					Pane pane = ((BootstrapGUI) gui).getPane();
					pane.firePeerTableRowInserted(pane.getPeerTableRowCount(),
							peerInfo.toObjectArray());
				} else {
					thead = new Head();
					thead.setMsgType(MsgType.NO_ONLINE_SUPER_PEER.getValue());
					Body tbody = new ConfirmBody();

					result = new Message(thead, tbody);
				}
			} else // if insert failed
			{
				thead = new Head();
				thead.setMsgType(MsgType.RNCK.getValue());
				Body tbody = new ConfirmBody();

				result = new Message(thead, tbody);
			}

			/* close connection and statement */
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException("User register operation failure", e);
		}

		try {
			gui.peer().sendMessage(dest, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.REGISTER.getValue())
			return true;
		return false;
	}

}