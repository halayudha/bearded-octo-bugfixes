/*
 * @(#) PongListener.java 1.0 2006-2-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;

/**
 * Implement a listener when processing a PONG message.
 *  
 * @author Xu Linhao
 * @version 1.0 2006-2-4
 */

public class PongListener extends ActionAdapter {

	public PongListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		Head thead = null;
		Body tbody = null;

		/* register MySQL driver */
		// disabled by chensu
		// DBConnector.registerDriver();
		try {

			/* construct the reply message */
			new Message(thead, tbody);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventHandleException("Pong operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.PONG.getValue())
			return true;
		return false;
	}

}