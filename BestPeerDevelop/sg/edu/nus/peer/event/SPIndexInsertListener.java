/*
 * @(#) SPIndexInsertListener.java 1.0 2007-1-26
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
import sg.edu.nus.protocol.body.SPIndexInsertBody;
import sg.edu.nus.search.IndexEvent;
import sg.edu.nus.search.IndexEventServerBindException;
import sg.edu.nus.search.IndexEventType;
import sg.edu.nus.search.event.SPIndexPublishBody;

/**
 * Simply passing received message to <code>ServerIndexManager</code>
 * for processing, which executes jobs sequentially.  
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-26
 * @see sg.edu.nus.peer.search.SPIndexPublishListener
 */

public class SPIndexInsertListener extends ActionAdapter {
	private ServerPeer serverPeer;

	/**
	 * Construct SPIndexInsertListener with specified parameter.
	 * 
	 * @param gui the handler of the main frame 
	 */
	public SPIndexInsertListener(AbstractMainFrame gui) {
		super(gui);
		this.serverPeer = (ServerPeer) gui.peer();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		SPIndexInsertBody body = (SPIndexInsertBody) msg.getBody();
		IndexEvent indexEvent = new IndexEvent(IndexEventType.INSERT);
		indexEvent.setBody(new SPIndexPublishBody(body));
		try {
			serverPeer.getIndexManager().accept(indexEvent);

			if (debug)
				System.out.println("document is pushed to index manager at ["
						+ body.getPhysicalSender().getIP() + ":"
						+ body.getPhysicalSender().getPort() + "]");
		} catch (IndexEventServerBindException e) {
			throw new EventHandleException(
					"Index manager accepts index event failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_INSERT.getValue())
			return true;
		return false;
	}

}