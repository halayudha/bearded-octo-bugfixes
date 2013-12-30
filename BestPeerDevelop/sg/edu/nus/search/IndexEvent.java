/*
 * @(#) IndexEvent.java 1.0 2006-7-7
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import sg.edu.nus.search.event.IndexBody;

/**
 * Define the event used for index operations.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public final class IndexEvent {

	// the event type
	private IndexEventType head;

	// the event content
	private IndexBody body;

	/**
	 * Construct an <code>IndexEvent</code>.
	 * 
	 * @param head the <code>IndexEventType</code>
	 */
	public IndexEvent(IndexEventType head) {
		this.head = head;
		this.body = null;
	}

	/**
	 * Construct an <code>IndexEvent</code>.
	 * 
	 * @param head the <code>IndexEventType</code>
	 * @param body the <code>IndexBody</code>
	 */
	public IndexEvent(IndexEventType head, IndexBody body) {
		this.head = head;
		this.body = body;
	}

	/**
	 * Returns the <code>IndexEventType</code>.
	 * 
	 * @return the <code>IndexEventType</code>
	 */
	public IndexEventType getHead() {
		return this.head;
	}

	/**
	 * Set the <code>IndexEventType</code>.
	 * 
	 * @param head the <code>IndexEventType</code>
	 */
	public void setHead(IndexEventType head) {
		this.head = head;
	}

	/**
	 * Returns the <code>IndexBody</code>.
	 * 
	 * @return the <code>IndexBody</code>
	 */
	public IndexBody getBody() {
		return this.body;
	}

	/**
	 * Set the <code>IndexBody</code>.
	 * 
	 * @param body the <code>IndexBody</code>
	 */
	public void setBody(IndexBody body) {
		this.body = body;
	}

}