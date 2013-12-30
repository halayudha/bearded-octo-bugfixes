/*
 * @(#) QueryClause.java 1.0 2006-7-21
 * 
 * Copyright 2006 National University of Singapore.
 * All right reserved.
 */

package sg.edu.nus.search.query;

import java.io.Serializable;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

/**
 * A wrapper for <code>Lucene Query</code>. The difference is 
 * that the <code>QueryClause</code> is composed of two elements:
 * <code>Lucene Query</code> and <code>BooleanClause.Occur</code>.
 * <p>
 * The philosophy of the <code>QueryClause</code> is that all user 
 * queries MUST be a <code>BooleanQuery</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-21
 */

public final class QueryClause implements Serializable {

	private static final long serialVersionUID = 4181445717584106415L;

	private int type;
	private Query query;
	private BooleanClause.Occur operate;

	/**
	 * Construct a query clause.
	 * 
	 * @param type the query type
	 * @param query the <code>Query</code>
	 * @param operate how query terms may occur in matching documents
	 */
	public QueryClause(int type, Query query, BooleanClause.Occur operate) {
		this.type = type;
		this.query = (Query) query.clone();
		this.operate = operate;
	}

	/**
	 * Returns the query type.
	 * 
	 * @return the query type
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns the <code>Lucene Query</code>.
	 * 
	 * @return the <code>Lucene Query</code>
	 */
	public Query getQuery() {
		return this.query;
	}

	/**
	 * Returns the specification how query terms may occur in matching documents.
	 * 
	 * @return the specification how query terms may occur in matching documents
	 */
	public BooleanClause.Occur getOperate() {
		return this.operate;
	}

	public String toString() {
		return (" Query Type: " + type + " Query: " + query.toString()
				+ " Operate Type: " + operate);
	}

}