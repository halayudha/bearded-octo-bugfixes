/*
 * @(#) SPIndexInsertBundleBody.java 1.0 2007-1-16
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import org.apache.lucene.document.Document;

/**
 * The message body carrying with the lucene document to be inserted.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-16
 * @see sg.edu.nus.peer.event.SPIndexInsertBundleListener
 */

public class SPIndexInsertBundleBody extends Body {

	private static final long serialVersionUID = -8404989897328173980L;

	protected String peerID; // peer identifier who shares document
	protected InetAddress ipaddr; // ip address of the peer who shares document
	protected int port; // port on which network service is providing
	protected Document doc; // lucene document that contains terms and fields

	/**
	 * Construct SPIndexInsertBundleBody without specifying lucene Document.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 */
	public SPIndexInsertBundleBody(String pid, InetAddress ipaddr, int port) {
		this.peerID = pid;
		this.ipaddr = ipaddr;
		this.port = port;
	}

	/**
	 * Construct SPIndexInsertBody with specified parameters that is used 
	 * for sending message from client peer to owner super peer.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be indexed
	 */
	public SPIndexInsertBundleBody(String pid, InetAddress ipaddr, int port,
			Document doc) {
		this(pid, ipaddr, port);
		this.doc = doc;
	}

	/**
	 * Returns the peer identifier who passes the document to super peer network.
	 * 
	 * @return returns the peer identifier who passes the document to super peer network
	 */
	public String getPeerID() {
		return this.peerID;
	}

	/**
	 * Returns the IP address of the peer who passes the document to super peer network.
	 * 
	 * @return returns the IP address of the peer who passes the document to super peer network
	 */
	public InetAddress getInetAddress() {
		return this.ipaddr;
	}

	/**
	 * Returns the port that provides network service (e.g., FTP) for the access of remote peers. 
	 * 
	 * @return returns the port that provides network service (e.g., FTP) for the access of remote peers
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns the document to be handled, which contains terms and their corresponding field. 
	 * 
	 * @return returns the document to be handled, which contains terms and their corresponding field
	 */
	public Document getDocument() {
		return this.doc;
	}

	/**
	 * Set the document to be handled, which contains terms and their corresponding field.
	 * 
	 * @param doc the document to be handled, which contains terms and their corresponding field
	 */
	public void setDocument(Document doc) {
		this.doc = doc;
	}

	public String toString() {
		String result = "SP_INSERT " + debug();
		return result;
	}

	/**
	 * Returns the string used for debugging.
	 * 
	 * @return returns the string used for debugging
	 */
	protected String debug() {
		String result = new String();

		result += "PeerID: " + this.peerID + " InetAddress: "
				+ this.ipaddr.getHostName() + " Port: " + this.port
				+ " Document: " + doc.toString();

		return result;
	}

}