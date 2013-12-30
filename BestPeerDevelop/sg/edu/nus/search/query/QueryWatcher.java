/*
 * @(#) QueryWatcher.java 1.0 2007-2-14
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.query;

/**
 * When a new query is issued, it can call queryChanged in a
 * listener to update the current query identifier. 
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-14
 */

public interface QueryWatcher {

	/**
	 * Changes the query identifier.
	 * 
	 * @param qid the new query identifier
	 * @param query the formated query string
	 */
	public void queryChanged(int qid, String query);

}