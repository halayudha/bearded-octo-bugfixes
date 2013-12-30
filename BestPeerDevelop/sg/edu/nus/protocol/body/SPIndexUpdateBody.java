/*
 * @(#) SPIndexUpdateBody.java 1.0 2007-1-26
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import sg.edu.nus.indexkeyword.TermDocument;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * Implement the message body used for updating index terms and their
 * corresponding fields from super peer network, for the purpose of
 * keeping distributed index freshly.
 * <p>
 * Not sure which old classes should be replaced and deprecated.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 * @see sg.edu.nus.peer.event.SPIndexUpdateListener
 */

public class SPIndexUpdateBody extends SPIndexInsertBody {

	private static final long serialVersionUID = -3667702856553236329L;

	/* old document to be updated */
	private TermDocument oldDoc;

	/**
	 * Construct SPIndexUpdateBody without specifying TermDocument.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 */
	public SPIndexUpdateBody(String pid, InetAddress ipaddr, int port) {
		super(pid, ipaddr, port);
	}

	/**
	 * Construct SPIndexUpdateBody with specified parameters that is used 
	 * for sending message from client peer to owner super peer.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be removed
	 */
	public SPIndexUpdateBody(String pid, InetAddress ipaddr, int port,
			TermDocument doc, TermDocument oldDoc) {
		super(pid, ipaddr, port, doc);
		this.oldDoc = oldDoc;
	}

	/**
	 * Construct SPIndexUpdateBody with specified parameters that is used
	 * for sending message from a super peer to other super peers.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be removed
	 * @param phySender the IP address and port of the peer who sends the message
	 * @param lgcSender the logical position of the sender in BATON tree
	 * @param lgcReceiver the logical position of the receiver in BATON tree
	 */
	public SPIndexUpdateBody(String pid, InetAddress ipaddr, int port,
			TermDocument doc, TermDocument oldDoc, PhysicalInfo phySender,
			LogicalInfo lgcSender, LogicalInfo lgcReceiver) {
		super(pid, ipaddr, port, doc, phySender, lgcSender, lgcReceiver);
		this.oldDoc = oldDoc;
	}

	/**
	 * Returns the old document that will be replaced by new one.
	 * 
	 * @return returns the old document that will be replaced by new one
	 */
	public TermDocument getOldDocument() {
		return this.oldDoc;
	}

	/**
	 * Set the old document that will be replaced by new one.
	 * 
	 * @param doc the old document that will be replaced by new one
	 */
	public void setOldDocument(TermDocument doc) {
		this.oldDoc = doc;
	}

	public String toString() {
		String result = "SP_UPDATE " + debug();
		return result;
	}

}