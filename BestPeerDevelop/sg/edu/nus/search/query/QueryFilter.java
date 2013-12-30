/*
 * @(#) QueryFilter.java 1.0 2006-7-24
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.query;

import org.apache.lucene.search.RangeFilter;

;

/**
 * A wrapper of <code>RangeFilter</code> for the purpose of
 * filtering returned <code>Document</code>s with special contraints.
 * 
 * @author Xu Linhao
 * @version 1.0
 */

public class QueryFilter extends RangeFilter {

	private static final long serialVersionUID = -4471365868412754698L;

	public QueryFilter(String field, String lower, String upper,
			boolean includeLower, boolean includeUpper) {
		super(field, lower, upper, includeLower, includeUpper);
	}

}