/*
 * @(#) SPIndexDeleteBundleBody.java 1.0 2007-1-26
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import org.apache.lucene.document.Document;

/**
 * The message body carrying with the lucene document to be removed.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 * @see sg.edu.nus.peer.event.SPIndexDeleteBundleListener
 */

public class SPIndexDeleteBundleBody extends SPIndexInsertBundleBody {

	private static final long serialVersionUID = -6073068356385367446L;

	/**
	 * Construct SPIndexDeleteBundleBody without specifying lucene Document.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 */
	public SPIndexDeleteBundleBody(String pid, InetAddress ipaddr, int port) {
		super(pid, ipaddr, port);
	}

	/**
	 * Construct SPIndexDeleteBundleBody with specified parameters that is used 
	 * for sending message from client peer to owner super peer.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be removed
	 */
	public SPIndexDeleteBundleBody(String pid, InetAddress ipaddr, int port,
			Document doc) {
		super(pid, ipaddr, port, doc);
	}

	public String toString() {
		String result = "SP_DELETE_BUNDLE " + debug();
		return result;
	}

}