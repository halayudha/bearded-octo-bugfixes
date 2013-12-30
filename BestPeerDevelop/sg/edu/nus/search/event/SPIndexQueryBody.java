/*
 * @(#) SPIndexQueryBody.java 1.0 2007-2-8
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.net.InetAddress;

import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.body.SPIndexSearchBody;
import sg.edu.nus.search.query.QueryClause;
import sg.edu.nus.search.query.QueryFilter;

/**
 * Implement the message body used for processing user query by
 * using distributed index.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-8
 * @see sg.edu.nus.search.SPIndexQueryListener
 */

public class SPIndexQueryBody extends IndexBody {

	private static final long serialVersionUID = -449466919354815611L;

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

	public SPIndexQueryBody(SPIndexSearchBody body) {
		this.queryID = body.getQueryID();
		this.clauses = body.getQueryClauses();

		this.isTypeFilter = body.isTypeFilter();
		this.typeFilter = body.getTypeFilter();

		this.isQueryFilter = body.isQueryFilter();
		this.queryFilter = body.getQueryFilter();

		this.ipaddr = body.getInetAddress();
		this.port = body.getPort();

		this.phySender = body.getPhysicalSender();
		this.lgcSender = body.getLogicalSender();
		this.lgcReceiver = body.getLogicalDestination();
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
	 * If <code>true</code>, use date range to filter query results.
	 * 
	 * @return <code>true</code> if use date range to filter query results
	 */
	public boolean isQueryFilter() {
		return this.isQueryFilter;
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