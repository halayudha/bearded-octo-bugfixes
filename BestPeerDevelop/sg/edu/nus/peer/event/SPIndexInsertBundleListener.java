/*
 * @(#) SPIndexInsertBundleListener.java 1.0 2007-2-13
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
import sg.edu.nus.protocol.body.SPIndexInsertBody;
import sg.edu.nus.protocol.body.SPIndexInsertBundleBody;

/**
 * Translating Lucene Document in message into TermDocument and then
 * wrapping corresponding event for inserting index.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-13
 * @see sg.edu.nus.peer.event.SPIndexInsertListener
 */

public class SPIndexInsertBundleListener extends ActionAdapter {

	private ServerPeer serverPeer;

	/**
	 * Construct SPIndexUpdateBundleListener with specified parameter.
	 * 
	 * @param gui the handler of the main frame
	 */
	public SPIndexInsertBundleListener(AbstractMainFrame gui) {
		super(gui);
		this.serverPeer = (ServerPeer) gui.peer();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		try {
			SPIndexInsertBundleBody bundleBody = (SPIndexInsertBundleBody) msg
					.getBody();
			String peerID = bundleBody.getPeerID();
			InetAddress ipaddr = bundleBody.getInetAddress();
			int port = bundleBody.getPort();
			PhysicalInfo server = serverPeer.getPhysicalInfo();
			TermDocument termDoc = TermDocument.toTermDocument(bundleBody
					.getDocument());

			if (debug) {
				System.out.println("get document from ["
						+ ipaddr.getHostAddress() + ":" + port + "]");
				System.out.println("send document to [" + server.getIP() + ":"
						+ server.getPort() + "]");
			}

			Head head = new Head(MsgType.SP_INSERT.getValue());
			Body body = new SPIndexInsertBody(peerID, ipaddr, port, termDoc,
					server, null, null);
			Message message = new Message(head, body);
			serverPeer.sendMessage(server, message);
		} catch (Exception e) {
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