/*
 * @(#) SPSearchExactBundleListener.java 1.0 2006-3-6
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.util.Vector;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.IndexValue;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.SPSearchExactBody;
import sg.edu.nus.protocol.body.SPSearchExactBundleBody;

/**
 * Implement a listener for processing SP_SEARCH_EXACT_BUNDLE message.
 * 
 * @author Vu Quang Hieu 
 * @version 1.0 2006-3-6
 */

public class SPSearchExactBundleListener extends ActionAdapter {

	public SPSearchExactBundleListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		Message result = null;
		Head head = new Head();
		Body searchBody;

		try {
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			PhysicalInfo physicalInfo = serverpeer.getPhysicalInfo();

			/* get the message body */
			SPSearchExactBundleBody body = (SPSearchExactBundleBody) msg
					.getBody();
			Vector<IndexValue> searchedData = body.getData();

			head.setMsgType(MsgType.SP_SEARCH_EXACT.getValue());
			for (int i = 0; i < searchedData.size(); i++) {
				searchBody = new SPSearchExactBody(physicalInfo, null, body
						.getPhysicalRequester(), body.getLogicalRequester(),
						searchedData.get(i), null);
				result = new Message(head, searchBody);
				serverpeer.sendMessage(physicalInfo, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException(
					"Super peer locates index range for exact search failure",
					e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_SEARCH_EXACT_BUNDLE
				.getValue())
			return true;
		return false;
	}

}