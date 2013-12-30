/*
 * @(#) SPIndexSearchBody.java 1.0 2007-1-26
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import sg.edu.nus.peer.info.*;
import sg.edu.nus.search.query.*;

/**
 * Implement the message body used for processing user query by
 * using distributed index.
 * <p>
 * The old version <code>{@link SPSearchExactBundleBody}</code>, 
 * <code>{@link SPSearchExactBody}</code>, <code>{@link SPSearchPairBody}</code>,
 * and <code>{@link SPSearchRangeBody}</code> are replaced and deprecated. 
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 * @see sg.edu.nus.peer.event.SPIndexSearchListener
 */

public class SPIndexSearchBody extends Body {

	private static final long serialVersionUID = 3440798446021041893L;

	private int queryID; // query identifier
	private QueryClause[] clauses; // query clauses

	private boolean isTypeFilter; // true if use file extension to filter result
	private String[] typeFilter; // file extensions

	private boolean isQueryFilter; // true if use date range to filter result
	private QueryFilter queryFilter; // date range

	private InetAddress ipaddr; // the IP address of query peer
	private int port; // the port of query peer

	/* variables compatible with old classes SPSearchBody */
	private PhysicalInfo phySender; // IP address and port of sender who sends
	// the message
	private LogicalInfo lgcSender; // logical position of sender who is in BATON
	// tree
	private LogicalInfo lgcReceiver;// logical position of receiver who is in

	// BATON tree

	/**
	 * Construct SPIndexSearchBody with specified query identifier, and the IP address 
	 * and port of the query which are used for sending results back to the query peer.
	 * 
	 * @param qid the query identifier
	 * @param ipaddr the IP address of the query peer
	 * @param port the port of the query peer
	 */
	public SPIndexSearchBody(int qid, InetAddress ipaddr, int port) {
		this.queryID = qid;
		this.ipaddr = ipaddr;
		this.port = port;
		this.isTypeFilter = false;
		this.isQueryFilter = false;
	}

	/**
	 * Construct SPIndexSearchBody with specified query identifier, query clauses
	 * and the IP address and port of the query which are used for sending results
	 * back to the query peer.
	 * 
	 * @param qid the query identifier
	 * @param ipaddr the IP address of the query peer
	 * @param port the port of the query peer
	 * @param clauses the query clauses
	 */
	public SPIndexSearchBody(int qid, InetAddress ipaddr, int port,
			QueryClause[] clauses) {
		this(qid, ipaddr, port);
		this.clauses = clauses.clone();
	}

	/**
	 * Construct SPIndexSearchBody with specified parameters that is used
	 * for sending message from a super peer to other super peers.
	 * 
	 * @param qid the query identifier
	 * @param ipaddr the IP address of the query peer
	 * @param port the port of the query peer
	 * @param clauses the query clauses
	 * @param phySender the IP address and port of the peer who sends the message
	 * @param lgcSender the logical position of the sender in BATON tree
	 * @param lgcReceiver the logical position of the receiver in BATON tree
	 */
	public SPIndexSearchBody(int qid, InetAddress ipaddr, int port,
			QueryClause[] clauses, PhysicalInfo phySender,
			LogicalInfo lgcSender, LogicalInfo lgcReceiver) {
		this(qid, ipaddr, port, clauses);
		this.phySender = phySender;
		this.lgcSender = lgcSender;
		this.lgcReceiver = lgcReceiver;
	}

	/**
	 * Construct SPIndexSearchBody with specified parameters that is used
	 * for sending message from a super peer to other super peers.
	 * 
	 * @param qid the query identifier
	 * @param ipaddr the IP address of the query peer
	 * @param port the port of the query peer
	 * @param clauses the query clauses
	 * @param phySender the IP address and port of the peer who sends the message
	 * @param lgcSender the logical position of the sender in BATON tree
	 * @param lgcReceiver the logical position of the receiver in BATON tree
	 */
	public SPIndexSearchBody(int qid, InetAddress ipaddr, int port,
			QueryClause[] clauses, PhysicalInfo phySender,
			LogicalInfo lgcSender, LogicalInfo lgcReceiver, boolean isQF,
			QueryFilter qf, boolean isTF, String[] tf) {
		this(qid, ipaddr, port, clauses, phySender, lgcSender, lgcReceiver);
		this.isQueryFilter = isQF;
		this.queryFilter = qf;
		this.isTypeFilter = isTF;
		this.typeFilter = tf;
	}

	/**
	 * Returns the unique identifier of the query.
	 * 
	 * @return returns the unique identifier of the query
	 */
	public int getQueryID() {
		return this.queryID;
	}

	/**
	 * Returns the query clauses.
	 * 
	 * @return returns the query clauses
	 */
	public QueryClause[] getQueryClauses() {
		return this.clauses;
	}

	/**
	 * Set the query clauses.
	 * 
	 * @param clauses the query clauses
	 */
	public void setQueryClauses(QueryClause[] clauses) {
		this.clauses = clauses.clone();
	}

	/**
	 * If <code>true</code>, use date range to filter query results.
	 * 
	 * @return <code>true</code> if use date range to filter query results
	 */
	public boolean isQueryFilter() {
		return this.isQueryFilter;
	}

	/**
	 * Put a date filter which includes the query constraints for
	 * filtering the query results.
	 * 
	 * @param filter a date filter that includes the query constraints
	 */
	public void addQueryFilter(QueryFilter filter) {
		this.isQueryFilter = true;
		this.queryFilter = filter;
	}

	/**
	 * Returns the date filter that includes the query constraints.
	 * 
	 * @return the date filter that includes the query constraints
	 */
	public QueryFilter getQueryFilter() {
		return this.queryFilter;
	}

	/**
	 * Returns if use file extension to filter query results.
	 * 
	 * @return <code>true</code> if use file extension to filter query results
	 */
	public boolean isTypeFilter() {
		return this.isTypeFilter;
	}

	/**
	 * Set the file extensions used to filter query results.
	 * 
	 * @param f the file extensions used to filter query results
	 */
	public void setTypeFilter(String[] f) {
		this.isTypeFilter = true;
		this.typeFilter = f.clone();
	}

	/**
	 * Returns the file extensions used to filter query results.
	 * 
	 * @return the file extensions used to filter query results
	 */
	public String[] getTypeFilter() {
		return this.typeFilter;
	}

	/**
	 * Returns the IP address of the query peer.
	 * 
	 * @return returns the IP address of the query peer
	 */
	public InetAddress getInetAddress() {
		return this.ipaddr;
	}

	/**
	 * Returns the port of the query peer.
	 * 
	 * @return returns the port of the query peer
	 */
	public int getPort() {
		return this.port;
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
		String result = "SP_SEARCH ";

		result += "QueryID: " + this.queryID + " Query Peer's InetAddress: "
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

		int size = clauses.length;
		for (int i = 0; i < size; i++)
			result += clauses[i].toString();

		return result;
	}

}