/*
 * @(#) SPDeleteBundleListener.java 1.0 2006-3-6
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
import sg.edu.nus.protocol.body.SPDeleteBody;
import sg.edu.nus.protocol.body.SPDeleteBundleBody;

/**
 * Implement a listener for processing SP_DELETE_BUNDLE message.
 * 
 * @author Vu Quang Hieu 
 * @version 1.0 2006-3-6
 */

public class SPDeleteBundleListener extends ActionAdapter {

	public SPDeleteBundleListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		super.actionPerformed(dest, msg);

		Message result = null;
		Head head = new Head();
		Body deleteBody;

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			PhysicalInfo physicalInfo = serverpeer.getPhysicalInfo();

			/* get the message body */
			SPDeleteBundleBody body = (SPDeleteBundleBody) msg.getBody();
			Vector<IndexPair> deletedData = body.getData();
			// PhysicalInfo peerID = body.getPhysicalSender();
			String docID = body.getDocID();

			head.setMsgType(MsgType.SP_DELETE.getValue());
			for (int i = 0; i < deletedData.size(); i++) {
				IndexPair indexPair = (IndexPair) deletedData.get(i);
				IndexInfo indexInfo = new IndexInfo(docID);
				IndexValue indexValue = new IndexValue(IndexValue.STRING_TYPE,
						indexPair.getKeyword(), indexInfo);
				deleteBody = new SPDeleteBody(physicalInfo, null, indexValue,
						null);
				result = new Message(head, deleteBody);
				serverpeer.sendMessage(physicalInfo, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"Super peer bundle deletion failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_DELETE_BUNDLE.getValue())
			return true;
		return false;
	}

}