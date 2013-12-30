/*
 * @(#) SPIndexResultBody.java 1.0 2007-1-27
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import org.apache.lucene.document.Document;

import sg.edu.nus.protocol.body.SPResultBody;

/**
 * Wrapping all desired Documents.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-27
 */

public class SPIndexResultBody extends IndexBody {

	private static final long serialVersionUID = -2880580123959902804L;

	private int queryID;
	private Document[] docs;

	/**
	 * Construct SPResultBody with specified parameter.
	 * 
	 * @param body the result body
	 */
	public SPIndexResultBody(SPResultBody body) {
		this.queryID = body.getQueryID();
		this.docs = body.getResults();
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
	 * Returns the number of results.
	 * 
	 * @return returns the number of results
	 */
	public int size() {
		return docs.length;
	}

	/**
	 * Returns all results.
	 * 
	 * @return return all results
	 */
	public Document[] getResults() {
		return this.docs;
	}

}