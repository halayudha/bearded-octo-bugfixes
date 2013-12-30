/*
 * @(#) SPIndexPublishBody.java 1.0 2007-2-8
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.net.InetAddress;

import sg.edu.nus.indexkeyword.TermDocument;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.body.SPIndexInsertBody;

/**
 * Implement the message body used for inserting a set of terms and 
 * their corresponding field into the super peer network, for the purpose
 * of constructing distributed index.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-8
 * @see sg.edu.nus.search.SPIndexPublishListener
 */

public class SPIndexPublishBody extends IndexBody {

	private static final long serialVersionUID = -642018984058784614L;

	private String peerID; // peer identifier who shares document
	private InetAddress ipaddr; // ip address of the peer who shares document
	private int port; // port on which network service is providing
	private TermDocument doc; // term document that contains terms and fields

	/* variables compatible with old classes SPInsertBody */
	private PhysicalInfo phySender; // IP address and port of sender who sends
	// the message
	private LogicalInfo lgcSender; // logical position of sender who is in BATON
	// tree
	private LogicalInfo lgcReceiver;// logical position of receiver who is in

	// BATON tree

	/**
	 * Construct SPIndexPublishBody with specified parameter.
	 * 
	 * @param body the SPIndexInsertBody instance
	 */
	public SPIndexPublishBody(SPIndexInsertBody body) {
		this.peerID = body.getPeerID();
		this.ipaddr = body.getInetAddress();
		this.port = body.getPort();
		this.doc = body.getDocument();

		this.phySender = body.getPhysicalSender();
		this.lgcSender = body.getLogicalSender();
		this.lgcReceiver = body.getLogicalDestination();
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
	 * Returns physical address of the sender
	 * 
	 * @return returns physical address of the sender
	 */
	public PhysicalInfo getPhysicalSender() {
		return this.phySender;
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
	 * Returns logical address of the receiver
	 * 
	 * @return returns logical address of the receiver
	 */
	public LogicalInfo getLogicalDestination() {
		return this.lgcReceiver;
	}

}