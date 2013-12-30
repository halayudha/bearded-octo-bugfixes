/*
 * @(#) QueryParser.java 1.0 2006-3-24
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import sg.edu.nus.indexkeyword.FieldConstant;

/**
 * A <code>QueryParser</code> to provide easy-to-use interfaces 
 * for generating different <code>Query</code>.
 * <p>
 * All query types are supported by <code>QueryParser</code> as follows:
 * <ul>
 * <li>TermQuery</li>
 * <li>RangeQuery</li>
 * <li>FuzzyQuery</li>
 * <li>WildcardQuery</li>
 * <li>PrefixQuery</li>
 * <li>PhaseQuery</li>
 * </ul>
 * <p>
 * The query string formalizes as:<br>
 * <i><field name>:keywords,...,keyword<field name>:keyword,...,keyword</i>
 * 
 * @author Xu Linhao
 * @version 1.0 2006-3-24
 */

public final class QueryParser implements Serializable {

	private static final long serialVersionUID = -3014503429307867403L;

	// default parameters
	public static final String defaultField = FieldConstant.Content.getValue();
	public static final BooleanClause.Occur defaultOps = BooleanClause.Occur.SHOULD;
	public static final int maxClauseCount = BooleanQuery.getMaxClauseCount();

	// define query type
	public static final int TERM = 0;
	public static final int FUZZY = 1;
	public static final int WILDCARD = 2;
	public static final int RANGE = 3;
	public static final int PREFIX = 4;

	// the query parser to be wrapped
	private List<QueryClause> queryList;

	/**
	 * Contruct a query parser.
	 */
	public QueryParser() {
		queryList = Collections
				.synchronizedList(new ArrayList<QueryClause>(100));
	}

	/**
	 * Contruct a query parser.
	 */
	public static QueryParser getQueryParser() {
		return new QueryParser();
	}

	/**
	 * Returns the unique identifier of the query.
	 * 
	 * @return returns the unique identifier of the query
	 */
	public int getQueryID() {
		int hashCode = 0;
		QueryClause clause;
		for (int i = 0; i < queryList.size(); i++) {
			clause = queryList.get(i);
			hashCode += clause.hashCode();
		}
		return hashCode;
	}

	/**
	 * Returns the formated query string.
	 * 
	 * @return returns the formated query string
	 */
	public String queryString() {
		// if no query clauses, return null
		if (queryList.size() == 0)
			return "";

		// return boolean query
		QueryClause clause = null;
		BooleanQuery query = new BooleanQuery();
		for (int i = 0; i < queryList.size(); i++) {
			clause = queryList.get(i);
			query.add(clause.getQuery(), clause.getOperate());
		}
		return query.toString();
	}

	/**
	 * Returns a <code>BooleanQuery</code> to be executed.
	 * 
	 * @return a <code>BooleanQuery</code> to be executed
	 */
	public BooleanQuery getBooleanQuery() {
		// if no query clauses, return null
		if (queryList.size() == 0)
			return null;

		// return boolean query
		QueryClause clause = null;
		BooleanQuery query = new BooleanQuery();
		for (int i = 0; i < queryList.size(); i++) {
			clause = queryList.get(i);
			query.add(clause.getQuery(), clause.getOperate());
		}
		return query;
	}

	/**
	 * Inserts a <code>QueryClause</code>.
	 * 
	 * @param clause the query clause
	 */
	public void addQueryClause(QueryClause clause) {
		this.queryList.add(clause);
	}

	/**
	 * Inserts a <code>TermQuery</code>.
	 * 
	 * @param field the field to be searched
	 * @param query the query string
	 * @param ops how query terms may occur in matching documents; 
	 * 				if null or undefined, then <code>defaultOps</code> is used
	 */
	public void addTermQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		Query termQuery = new TermQuery(new Term(field, query));
		this.addQueryClause(TERM, termQuery, ops);
	}

	/**
	 * Inserts a <code>FuzzyQuery</code>.
	 * 
	 * @param field the field to be searched
	 * @param query the query string
	 * @param ops how query terms may occur in matching documents; 
	 * 				if null or undefined, then <code>defaultOps</code> is used
	 * @param sim the similarity of fuzzy query
	 */
	public void addFuzzyQueryClause(String field, String query,
			BooleanClause.Occur ops, float sim) {
		if (field == null)
			field = defaultField;
		Query fuzzyQuery = new FuzzyQuery(new Term(field, query), sim);
		this.addQueryClause(FUZZY, fuzzyQuery, ops);
	}

	/**
	 * Inserts a <code>WildcardQuery</code>.
	 * 
	 * @param field the field to be searched
	 * @param query the query string
	 * @param ops how query terms may occur in matching documents; 
	 * 				if null or undefined, then <code>defaultOps</code> is used
	 */
	public void addWildcardQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		Query wildQuery = new WildcardQuery(new Term(field, query));
		this.addQueryClause(WILDCARD, wildQuery, ops);
	}

	/**
	 * Inserts a <code>RangeQuery</code>.
	 * 
	 * @param field the field to be searched
	 * @param lower the lower bound of the search range
	 * @param upper the upper bound of the search range
	 * @param inclusive <code>true</code> includes the both bound value
	 * @param ops how query terms may occur in matching documents; 
	 * 				if null or undefined, then <code>defaultOps</code> is used
	 */
	public void addRangeQueryClause(String field, String lower, String upper,
			boolean inclusive, BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		Query rangeQuery = new RangeQuery(new Term(field, lower), new Term(
				field, upper), inclusive);
		this.addQueryClause(RANGE, rangeQuery, ops);
	}

	/**
	 * Insert a <code>PrefixQuery</code>.
	 * 
	 * @param field the field to be searched
	 * @param query the prefix string
	 * @param ops how query terms may occur in matching documents; 
	 * 				if null or undefined, then <code>defaultOps</code> is used
	 */
	public void addPrefixQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		Query prefixQuery = new PrefixQuery(new Term(field, query));
		this.addQueryClause(PREFIX, prefixQuery, ops);
	}

	private void addQueryClause(int type, Query query, BooleanClause.Occur ops) {
		// if too many clauses
		if (queryList.size() >= maxClauseCount)
			throw new BooleanQuery.TooManyClauses();

		// insert query into a container
		if ((ops == BooleanClause.Occur.MUST)
				|| (ops == BooleanClause.Occur.MUST_NOT)
				|| (ops == BooleanClause.Occur.SHOULD))
			queryList.add(new QueryClause(type, query, ops));
		else
			queryList.add(new QueryClause(type, query, defaultOps));
	}

	/**
	 * Removes a <code>QueryClause</code>.
	 * 
	 * @param clause the query clause to be removed
	 */
	public Query removeQueryClause(QueryClause clause) {
		int type = clause.getType();
		switch (type) {
		case TERM:
			TermQuery termQuery = (TermQuery) clause.getQuery();
			return this.removeQueryClause(type, termQuery.getTerm().field(),
					termQuery.getTerm().text(), null, clause.getOperate());

		case FUZZY:
			FuzzyQuery fuzzyQuery = (FuzzyQuery) clause.getQuery();
			return this.removeQueryClause(type, fuzzyQuery.getTerm().field(),
					fuzzyQuery.getTerm().text(), null, clause.getOperate());

		case WILDCARD:
			WildcardQuery wildQuery = (WildcardQuery) clause.getQuery();
			return this.removeQueryClause(type, wildQuery.getTerm().field(),
					wildQuery.getTerm().text(), null, clause.getOperate());

		case RANGE:
			RangeQuery rangeQuery = (RangeQuery) clause.getQuery();
			Term lower = rangeQuery.getLowerTerm();
			Term upper = rangeQuery.getUpperTerm();
			return this.removeQueryClause(type, lower.field(), lower.text(),
					upper.text(), clause.getOperate());

		case PREFIX:
			PrefixQuery prefixQuery = (PrefixQuery) clause.getQuery();
			return this.removeQueryClause(type,
					prefixQuery.getPrefix().field(), prefixQuery.getPrefix()
							.text(), null, clause.getOperate());
		}
		return null;
	}

	public TermQuery removeTermQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		return (TermQuery) this
				.removeQueryClause(TERM, field, query, null, ops);
	}

	public FuzzyQuery removeFuzzyQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		return (FuzzyQuery) this.removeQueryClause(FUZZY, field, query, null,
				ops);
	}

	public WildcardQuery removeWildcardQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		return (WildcardQuery) this.removeQueryClause(WILDCARD, field, query,
				null, ops);
	}

	public RangeQuery removeRangeQueryClause(String field, String lower,
			String upper, BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		return (RangeQuery) this.removeQueryClause(RANGE, field, lower, upper,
				ops);
	}

	public PrefixQuery removePrefixQueryClause(String field, String query,
			BooleanClause.Occur ops) {
		if (field == null)
			field = defaultField;
		return (PrefixQuery) this.removeQueryClause(PREFIX, field, query, null,
				ops);
	}

	private Query removeQueryClause(int type, String field, String query1,
			String query2, BooleanClause.Occur ops) {
		QueryClause clause = null;
		for (int i = 0; i < queryList.size(); i++) {
			clause = queryList.get(i);
			switch (type) {
			case TERM:
				TermQuery termQuery = (TermQuery) clause.getQuery();
				if ((termQuery.getTerm().field() == field)
						&& (termQuery.getTerm().text() == query1)
						&& (clause.getOperate() == ops))
					return queryList.remove(i).getQuery();
				break;

			case FUZZY:
				FuzzyQuery fuzzyQuery = (FuzzyQuery) clause.getQuery();
				if ((fuzzyQuery.getTerm().field() == field)
						&& (fuzzyQuery.getTerm().text() == query1)
						&& (clause.getOperate() == ops))
					return queryList.remove(i).getQuery();
				break;

			case WILDCARD:
				WildcardQuery wildQuery = (WildcardQuery) clause.getQuery();
				if ((wildQuery.getTerm().field() == field)
						&& (wildQuery.getTerm().text() == query1)
						&& (clause.getOperate() == ops))
					return queryList.remove(i).getQuery();
				break;

			case RANGE:
				RangeQuery rangeQuery = (RangeQuery) clause.getQuery();
				Term lower = rangeQuery.getLowerTerm();
				Term upper = rangeQuery.getUpperTerm();
				if ((lower.field() == field) && (upper.field() == field)
						&& (lower.text() == query1) && (upper.text() == query2)
						&& (clause.getOperate() == ops))
					return queryList.remove(i).getQuery();
				break;

			case PREFIX:
				PrefixQuery prefixQuery = (PrefixQuery) clause.getQuery();
				if ((prefixQuery.getPrefix().field() == field)
						&& (prefixQuery.getPrefix().text() == query1)
						&& (clause.getOperate() == ops))
					return queryList.remove(i).getQuery();
				break;
			}
		}
		return null;
	}

	/**
	 * Returns an iterator of query clauses.
	 * 
	 * @return an iterator of query clauses
	 */
	@SuppressWarnings("unchecked")
	public Iterator iterator() {
		return this.queryList.iterator();
	}

	/**
	 * Returns an array of query clauses.
	 * 
	 * @return an array of query clauses
	 */
	public QueryClause[] getClauses() {
		QueryClause[] clauses = new QueryClause[queryList.size()];
		queryList.toArray(clauses);
		return clauses;
	}

	/**
	 * Returns the number of query clauses.
	 * 
	 * @return the number of query clauses
	 */
	public int size() {
		return queryList.size();
	}

	/**
	 * Removes all query clauses.
	 */
	public void clear() {
		queryList.clear();
	}

}