/*
 * @(#) SPIndexInsertBody.java 1.0 2007-1-16
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import sg.edu.nus.indexkeyword.TermDocument;
import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for inserting a bundle of terms and 
 * their corresponding field into the super peer network, for the purpose
 * of constructing distributed index.
 * <p>
 * The old version <code>{@link SPInsertBundleBody}</code> and 
 * <code>{@link SPInsertBody}</code> are deprecated
 * for the sake of unefficiency of building index. 
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-16
 * @see sg.edu.nus.peer.event.SPIndexInsertListener
 */

public class SPIndexInsertBody extends Body {

	private static final long serialVersionUID = 3390910101773614108L;

	protected String peerID; // peer identifier who shares document
	protected InetAddress ipaddr; // ip address of the peer who shares document
	protected int port; // port on which network service is providing
	protected TermDocument doc; // term document that contains terms and fields

	/* variables compatible with old classes SPInsertBody */
	protected PhysicalInfo phySender; // IP address and port of sender who sends
	// the message
	protected LogicalInfo lgcSender; // logical position of sender who is in
	// BATON tree
	protected LogicalInfo lgcReceiver;// logical position of receiver who is in

	// BATON tree

	/**
	 * Construct SPIndexInsertBody without specifying TermDocument.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 */
	public SPIndexInsertBody(String pid, InetAddress ipaddr, int port) {
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
	public SPIndexInsertBody(String pid, InetAddress ipaddr, int port,
			TermDocument doc) {
		this(pid, ipaddr, port);
		this.doc = doc;
	}

	/**
	 * Construct SPIndexInsertBody with specified parameters that is used
	 * for sending message from a super peer to other super peers.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be indexed
	 * @param phySender the IP address and port of the peer who sends the message
	 * @param lgcSender the logical position of the sender in BATON tree
	 * @param lgcReceiver the logical position of the receiver in BATON tree
	 */
	public SPIndexInsertBody(String pid, InetAddress ipaddr, int port,
			TermDocument doc, PhysicalInfo phySender, LogicalInfo lgcSender,
			LogicalInfo lgcReceiver) {
		this(pid, ipaddr, port, doc);
		this.phySender = phySender;
		this.lgcSender = lgcSender;
		this.lgcReceiver = lgcReceiver;
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
	public TermDocument getDocument() {
		return this.doc;
	}

	/**
	 * Set the document to be handled, which contains terms and their corresponding field.
	 * 
	 * @param doc the document to be handled, which contains terms and their corresponding field
	 */
	public void setDocument(TermDocument doc) {
		this.doc = doc;
	}

	/**
	 * Update physical address of the sender
	 * 
	 * @param phySender physical address of the sender
	 */
	public void setPhysicalSender(PhysicalInfo phySender) {
		this.phySender = phySender;
	}

	/**
	 * Returns physical address of the sender
	 * 
	 * @return returns physical address of the sender
	 */
	public PhysicalInfo getPhysicalSender() {
		return this.phySender;
	}

	/**
	 * Update logical address of the sender
	 * 
	 * @param lgcSender logical address of the sender
	 */
	public void setLogicalSender(LogicalInfo lgcSender) {
		this.lgcSender = lgcSender;
	}

	/**
	 * Returns logical address of the sender
	 * 
	 * @return returns logical address of the sender
	 */
	public LogicalInfo getLogicalSender() {
		return this.lgcSender;
	}

	/**
	 * Update logical address of the receiver
	 * 
	 * @param lgcReceiver logical address of the receiver
	 */
	public void setLogicalDestination(LogicalInfo lgcReceiver) {
		this.lgcReceiver = lgcReceiver;
	}

	/**
	 * Returns logical address of the receiver
	 * 
	 * @return returns logical address of the receiver
	 */
	public LogicalInfo getLogicalDestination() {
		return this.lgcReceiver;
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
				+ this.ipaddr.getHostName() + " Port: " + this.port;

		if (this.phySender == null)
			result += " Physical Sender: null";
		else
			result += " Physical Sender: " + this.phySender.toString();

		if (this.lgcSender == null)
			result += " Logical Sender: null";
		else
			result += " Logical Sender: " + this.lgcSender.toString();

		if (this.lgcReceiver == null)
			result += " Logical Receiver: null";
		else
			result += " Logical Receiver: " + this.lgcReceiver.toString();

		result += " TermDocument: " + doc.toString();

		return result;
	}

}