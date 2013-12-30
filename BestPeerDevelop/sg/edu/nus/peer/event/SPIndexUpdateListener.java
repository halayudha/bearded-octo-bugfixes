/*
 * (#) SPIndexUpdateListener.java 1.0 2007-1-26
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPIndexUpdateBody;
import sg.edu.nus.search.IndexEvent;
import sg.edu.nus.search.IndexEventServerBindException;
import sg.edu.nus.search.IndexEventType;
import sg.edu.nus.search.event.SPIndexPublishBody;
import sg.edu.nus.search.event.SPIndexRemoveBody;

/**
 * Simply passing received message to <code>ServerIndexManager</code>
 * for processing, which executes jobs sequentially.  
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-26
 * @see sg.edu.nus.peer.search.SPIndexPublishListener
 * @see sg.edu.nus.peer.search.SPIndexRemoveListener
 */

public class SPIndexUpdateListener extends ActionAdapter {
	private ServerPeer serverPeer;

	/**
	 * Construct SPIndexUpdateListener with specified parameter.
	 * 
	 * @param gui the handler of the main frame
	 */
	public SPIndexUpdateListener(AbstractMainFrame gui) {
		super(gui);
		this.serverPeer = (ServerPeer) gui.peer();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		SPIndexUpdateBody body = (SPIndexUpdateBody) msg.getBody();
		/* event for deleting old document */
		IndexEvent deleteEvent = new IndexEvent(IndexEventType.DELETE);
		deleteEvent.setBody(new SPIndexRemoveBody(body));
		/* event for inserting new document */
		IndexEvent insertEvent = new IndexEvent(IndexEventType.INSERT);
		insertEvent.setBody(new SPIndexPublishBody(body));
		try {
			serverPeer.getIndexManager().accept(deleteEvent);
			serverPeer.getIndexManager().accept(insertEvent);
		} catch (IndexEventServerBindException e) {
			throw new EventHandleException(
					"Index manager accepts index event failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_UPDATE.getValue())
			return true;
		return false;
	}

}