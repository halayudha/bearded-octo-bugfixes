/*
 * @(#) SPResultBody.java 1.0 2007-1-27
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import org.apache.lucene.document.Document;

/**
 * Wrapping all desired Documents.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-27
 */

public class SPResultBody extends Body {

	private static final long serialVersionUID = -5209592623912973878L;

	private int queryID;
	private Document[] docs;

	/**
	 * Construct SPResultBody with specified parameter.
	 * 
	 * @param qid the query identifier
	 */
	public SPResultBody(int qid) {
		this.queryID = qid;
		this.docs = null;
	}

	/**
	 * Construct SPResultBody with specified parameters.
	 * 
	 * @param qid the query identifier
	 * @param docs the documents
	 */
	public SPResultBody(int qid, Document[] docs) {
		this(qid);
		this.docs = docs.clone();
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