/*
 * @(#) IndexSearchBody.java 1.0 2006-7-10
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.util.Date;

import sg.edu.nus.search.query.QueryFilter;
import sg.edu.nus.search.query.QueryParser;

/**
 * Specify the parameters used for performing a search operation
 * on both Lucene index and super peer network.
 * 
 * @author Xu linhao
 * @version 1.0 2006-7-10
 */

public final class IndexSearchBody extends IndexBody {

	private static final long serialVersionUID = -5165417910732095445L;

	// private members
	private boolean isGlobal; // global or local?
	private boolean isDateRange; // use date range?
	private boolean isTypeFilter; // use file type filter?

	private Date fromDate;
	private Date toDate;

	private String[] typeFilter;

	private QueryParser parser;
	private QueryFilter filter;

	/**
	 * Put a query parser which includes the query details
	 * into the <code>IndexSearchBody</code> processed by
	 * <code>IndexSearchListener</code>.
	 * 
	 * @param parser a query parser that includes the query details
	 */
	public void addQueryParser(QueryParser parser) {
		this.parser = parser;
	}

	/**
	 * Returns the query parser that includes the query details.
	 * 
	 * @return the query parser that includes the query details
	 */
	public QueryParser getQueryParser() {
		return this.parser;
	}

	/**
	 * Put a query filter whichi includes the query constraints for
	 * filtering the query results, into the <code>IndexSearchBody</code>
	 * processed by <code>IndexSearchListener</code>.
	 * 
	 * @param filter a query filter that includes the query constraints
	 */
	public void addQueryFilter(QueryFilter filter) {
		this.filter = filter;
	}

	/**
	 * Returns the query filter that includes the query constraints.
	 * 
	 * @return the query filter that includes the query constraints
	 */
	public QueryFilter getQueryFilter() {
		return this.filter;
	}

	/**
	 * Determine which type of search will be performed.
	 * 
	 * @param b if <code>true</code>, global search is supported;
	 * 			otherwise, only local search
	 */
	public void enableGlobalSearch(boolean b) {
		isGlobal = true;
	}

	/**
	 * If <code>true</code>, use global search.
	 * 
	 * @return <code>true</code> if use global search; otherwise, only local search
	 */
	public boolean isGlobalSearch() {
		return this.isGlobal;
	}

	/**
	 * If <code>true</code>, use date range to filter query results.
	 * 
	 * @return <code>true</code> if use date range to filter query results
	 */
	public boolean isDateRange() {
		return this.isDateRange;
	}

	/**
	 * Set the date range used for filtering query results.
	 * 
	 * @param from the start of the date range 
	 * @param to the end of the date range
	 */
	public void setDateRange(Date from, Date to) {
		fromDate = from;
		toDate = to;
		this.isDateRange = true;
	}

	/**
	 * Returns the start of the date range.
	 * 
	 * @return the start of the date range
	 */
	public Date getFromDate() {
		return this.fromDate;
	}

	/**
	 * Returns the end of the date range.
	 * 
	 * @return the end of the date range
	 */
	public Date getToDate() {
		return this.toDate;
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
	public void setTypeFilter(String f) {
		if (f.equals("All files (*.*)")) {
			this.isTypeFilter = false;
		} else {
			String delim = ";";
			String[] str = f.split(delim);
			typeFilter = new String[str.length];
			for (int i = 0; i < str.length; i++) {
				int idx = str[i].lastIndexOf(".");
				if ((idx != -1) || (idx != (str[i].length() - 1))) {
					typeFilter[i] = str[i].substring(idx + 1, str[i].length());
				}
			}
			this.isTypeFilter = true;
		}
	}

	/**
	 * Returns the file extensions used to filter query results.
	 * 
	 * @return the file extensions used to filter query results
	 */
	public String[] getTypeFilter() {
		return typeFilter;
	}

}