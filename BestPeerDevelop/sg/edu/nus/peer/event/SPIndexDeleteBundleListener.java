/*
 * @(#) SPIndexDeleteBundleListener.java 1.0 2007-2-13
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
import sg.edu.nus.protocol.body.SPIndexDeleteBody;
import sg.edu.nus.protocol.body.SPIndexDeleteBundleBody;

/**
 * Translating Lucene Document in message into TermDocument and then
 * wrapping corresponding event for deleting index.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-13
 * @see sg.edu.nus.peer.event.SPIndexDeleteListener
 */

public class SPIndexDeleteBundleListener extends ActionAdapter {

	private ServerPeer serverPeer;

	/**
	 * Construct SPIndexUpdateBundleListener with specified parameter.
	 * 
	 * @param gui the handler of the main frame
	 */
	public SPIndexDeleteBundleListener(AbstractMainFrame gui) {
		super(gui);
		this.serverPeer = (ServerPeer) gui.peer();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {
			SPIndexDeleteBundleBody bundleBody = (SPIndexDeleteBundleBody) msg
					.getBody();
			String peerID = bundleBody.getPeerID();
			InetAddress ipaddr = bundleBody.getInetAddress();
			int port = bundleBody.getPort();
			PhysicalInfo server = serverPeer.getPhysicalInfo();
			TermDocument termDoc = TermDocument.toTermDocument(bundleBody
					.getDocument());

			Head head = new Head(MsgType.SP_DELETE.getValue());
			Body body = new SPIndexDeleteBody(peerID, ipaddr, port, termDoc,
					server, null, null);
			Message message = new Message(head, body);
			serverPeer.sendMessage(server, message);
		} catch (Exception e) {
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