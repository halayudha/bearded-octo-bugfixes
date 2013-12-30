/*
 * @(#) SPInsertBundleListener.java 1.0 2006-3-6
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;
import java.util.Vector;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.IndexInfo;
import sg.edu.nus.peer.info.IndexPair;
import sg.edu.nus.peer.info.IndexValue;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.SPInsertBody;
import sg.edu.nus.protocol.body.SPInsertBundleBody;

/**
 * Implement a listener for processing SP_INSERT_BUNDLE message.
 * 
 * @author Vu Quang Hieu 
 * @version 1.0 2006-3-6
 */

public class SPInsertBundleListener extends ActionAdapter {

	public SPInsertBundleListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Message result = null;
		Head head = new Head();
		Body insertBody;

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			PhysicalInfo physicalInfo = serverpeer.getPhysicalInfo();

			/* get the message body */
			SPInsertBundleBody body = (SPInsertBundleBody) msg.getBody();
			Vector<IndexPair> insertedData = body.getData();
			// PhysicalInfo peerID = body.getPhysicalSender();
			String docID = body.getDocID();

			head.setMsgType(MsgType.SP_INSERT.getValue());
			for (int i = 0; i < insertedData.size(); i++) {
				IndexPair indexPair = (IndexPair) insertedData.get(i);
				IndexInfo indexInfo = new IndexInfo(docID);
				IndexValue indexValue = new IndexValue(IndexValue.STRING_TYPE,
						indexPair.getKeyword(), indexInfo);
				insertBody = new SPInsertBody(physicalInfo, null, indexValue,
						null);
				result = new Message(head, insertBody);
				serverpeer.sendMessage(physicalInfo, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"Super peer bundle insertion failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_INSERT_BUNDLE.getValue())
			return true;
		return false;
	}

}