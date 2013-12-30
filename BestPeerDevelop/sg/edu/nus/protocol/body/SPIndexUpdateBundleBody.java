/*
 * @(#) SPIndexUpdateBundleBody.java 1.0 2007-1-26
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import org.apache.lucene.document.Document;

/**
 * The message body carrying with the lucene document to be updated.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 * @see sg.edu.nus.peer.event.SPIndexUpdateBundleListener
 */

public class SPIndexUpdateBundleBody extends SPIndexInsertBundleBody {

	private static final long serialVersionUID = -4677795848943800562L;

	/* old document to be updated */
	private Document oldDoc;

	/**
	 * Construct SPIndexUpdateBody without specifying TermDocument.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 */
	public SPIndexUpdateBundleBody(String pid, InetAddress ipaddr, int port) {
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
	public SPIndexUpdateBundleBody(String pid, InetAddress ipaddr, int port,
			Document doc, Document oldDoc) {
		super(pid, ipaddr, port, doc);
		this.oldDoc = oldDoc;
	}

	/**
	 * Returns the old document that will be replaced by new one.
	 * 
	 * @return returns the old document that will be replaced by new one
	 */
	public Document getOldDocument() {
		return this.oldDoc;
	}

	/**
	 * Set the old document that will be replaced by new one.
	 * 
	 * @param doc the old document that will be replaced by new one
	 */
	public void setOldDocument(Document doc) {
		this.oldDoc = doc;
	}

	public String toString() {
		String result = "SP_UPDATE " + debug();
		return result;
	}

}