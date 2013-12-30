/*
 * @(#) SPIndexSearchListener.java 1.0 2007-1-26
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
import sg.edu.nus.protocol.body.SPIndexSearchBody;
import sg.edu.nus.search.IndexEvent;
import sg.edu.nus.search.IndexEventServerBindException;
import sg.edu.nus.search.IndexEventType;
import sg.edu.nus.search.event.SPIndexQueryBody;

/**
 * Simply passing received message to <code>ServerIndexManager</code>
 * for processing, which executes jobs sequentially.  
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-26
 * @see sg.edu.nus.peer.search.SPIndexQueryListener
 */

public class SPIndexSearchListener extends ActionAdapter {
	private ServerPeer serverPeer;

	/**
	 * Construct SPIndexSearchListener with specified parameter.
	 * 
	 * @param gui the handler of the main frame
	 */
	public SPIndexSearchListener(AbstractMainFrame gui) {
		super(gui);
		this.serverPeer = (ServerPeer) gui.peer();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		SPIndexSearchBody body = (SPIndexSearchBody) msg.getBody();
		IndexEvent indexEvent = new IndexEvent(IndexEventType.SEARCH);
		indexEvent.setBody(new SPIndexQueryBody(body));
		try {
			serverPeer.getIndexManager().accept(indexEvent);
		} catch (IndexEventServerBindException e) {
			throw new EventHandleException(
					"Index manager accepts index event failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_SEARCH_PAIR.getValue())
			return true;
		return false;
	}

}