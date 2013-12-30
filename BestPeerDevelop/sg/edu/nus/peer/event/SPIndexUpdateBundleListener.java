/*
 * @(#) SPIndexUpdateBundleListener.java 1.0 2007-2-13
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.net.InetAddress;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.indexkeyword.TermDocument;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.Body;
import sg.edu.nus.protocol.body.SPIndexUpdateBody;
import sg.edu.nus.protocol.body.SPIndexUpdateBundleBody;

/**
 * Translating Lucene Document in message into TermDocument and then
 * wrapping corresponding event for updating index.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-13
 * @see sg.edu.nus.peer.event.SPIndexUpdateListener
 */

public class SPIndexUpdateBundleListener extends ActionAdapter {

	private ServerPeer serverPeer;

	/**
	 * Construct SPIndexUpdateBundleListener with specified parameter.
	 * 
	 * @param gui the handler of the main frame
	 */
	public SPIndexUpdateBundleListener(AbstractMainFrame gui) {
		super(gui);
		this.serverPeer = (ServerPeer) gui.peer();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {
			SPIndexUpdateBundleBody bundleBody = (SPIndexUpdateBundleBody) msg
					.getBody();
			String peerID = bundleBody.getPeerID();
			InetAddress ipaddr = bundleBody.getInetAddress();
			int port = bundleBody.getPort();
			PhysicalInfo server = serverPeer.getPhysicalInfo();
			TermDocument newDoc = TermDocument.toTermDocument(bundleBody
					.getDocument());
			TermDocument oldDoc = TermDocument.toTermDocument(bundleBody
					.getOldDocument());

			Head head = new Head(MsgType.SP_UPDATE.getValue());
			Body body = new SPIndexUpdateBody(peerID, ipaddr, port, newDoc,
					oldDoc, server, null, null);
			Message message = new Message(head, body);
			serverPeer.sendMessage(server, message);
		} catch (Exception e) {
			throw new EventHandleException("Super peer bundle update failure",
					e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.SP_UPDATE_BUNDLE.getValue())
			return true;
		return false;
	}

}