/*
 * @(#) SPIndexQueryListener.java 1.0 2007-2-8
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.indexkeyword.FieldConstant;
import sg.edu.nus.indexkeyword.FileHandlerException;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.AdjacentNodeInfo;
import sg.edu.nus.peer.info.BoundaryValue;
import sg.edu.nus.peer.info.ChildNodeInfo;
import sg.edu.nus.peer.info.IndexValue;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.info.RoutingItemInfo;
import sg.edu.nus.peer.info.RoutingTableInfo;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.SPIndexSearchBody;
import sg.edu.nus.protocol.body.SPResultBody;
import sg.edu.nus.search.event.SPIndexQueryBody;
import sg.edu.nus.search.query.QueryClause;
import sg.edu.nus.search.query.QueryFilter;
import sg.edu.nus.search.query.QueryParser;

/**
 * Used for searching desired documents from index maintained by super peer. 
 * The following tasks will be performed by SPIndexSearchListener:
 * <pre>
 * <li>Decompse query expression and use desired query terms to execute local search.</li>
 * <li>Return local resultset to query peer.</li>
 * <li>Deliver query to relevant peers in routing table, child or adjacent peers.</li>
 * </pre>
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-8
 * @see sg.edu.nus.peer.event.SPIndexSearchListener
 */

public class SPIndexQueryListener extends SPIndexOperateAdapter {

	private ServerPeer serverPeer; // handler of server peer
	private IndexReader indexReader; // reading lucene index
	private IndexSearcher indexSearcher; // searching lucene index
	private QueryParser localParser; // parser for generating local query
	private QueryParser remoteParser; // parser for generating remote query

	/* list for sending query to remote peers */
	private Map<PhysicalInfo, QueryParser> queryList;
	private Map<PhysicalInfo, LogicalInfo> peerList;
	private Map<String, Document> docList;

	/**
	 * Construct SPIndexQueryListener with specified parameters.
	 * 
	 * @param gui the handler of the main frame
	 * @param analyzer the analyzer used for parsing document
	 */
	public SPIndexQueryListener(AbstractMainFrame gui, Analyzer analyzer) {
		super(gui, analyzer);
		this.serverPeer = (ServerPeer) gui.peer();
		this.localParser = null;
		this.remoteParser = null;
		this.queryList = new Hashtable<PhysicalInfo, QueryParser>();
		this.peerList = new Hashtable<PhysicalInfo, LogicalInfo>();
		this.docList = new Hashtable<String, Document>();
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(IndexEvent event) throws IOException,
			FileHandlerException, IndexOperationException {
		try {
			/* parse message */
			SPIndexQueryBody body = (SPIndexQueryBody) event.getBody();
			QueryClause[] clauses = body.getQueryClauses();

			/* query peer's IP address and port */
			PhysicalInfo requester = new PhysicalInfo(body.getInetAddress()
					.getHostAddress(), body.getPort());

			/* get self's tree node, min key and max key */
			TreeNode treeNode = this.serverPeer.getTreeNode(body
					.getLogicalDestination());
			if (treeNode == null) {
				System.out
						.println("Tree node is null, do not process the message");
				return;
			}

			BoundaryValue minValue = treeNode.getContent().getMinValue();
			BoundaryValue maxValue = treeNode.getContent().getMaxValue();
			String minKey = minValue.getStringValue();
			String maxKey = maxValue.getStringValue();

			/* decompose query clauses */
			this.decompose(clauses, minKey, maxKey);

			/* perform local search */
			Document[] results = null;
			if ((localParser != null) && (localParser.size() > 0)) {
				results = this.search(localParser, body.isQueryFilter(), body
						.getQueryFilter(), body.isTypeFilter(), body
						.getTypeFilter());
				if ((results != null) && (results.length > 0)) {
					serverPeer.sendMessage(requester, new Message(new Head(
							MsgType.RESULT.getValue()), new SPResultBody(body
							.getQueryID(), results)));
				}
			}

			/* handle remainder query clauses */
			if ((remoteParser != null) && (remoteParser.size() > 0)) {
				/* assign query terms to query parsers */
				this.assignTerms(treeNode, remoteParser, minKey, maxKey);

				/* send query messages to remote peers */
				Head head = new Head(MsgType.SP_SEARCH_PAIR.getValue());

				/* iterator of queryList */
				Entry entry = null;
				PhysicalInfo phyInfo = null;
				LogicalInfo logInfo = null;
				QueryParser parser = null;
				Iterator it = queryList.entrySet().iterator();
				while (it.hasNext()) {
					/* get IP address and logical position of remote peer */
					entry = (Entry) it.next();
					phyInfo = (PhysicalInfo) entry.getKey();
					logInfo = (LogicalInfo) peerList.get(phyInfo);
					parser = (QueryParser) entry.getValue();

					/* for debug */
					if ((phyInfo == null) || (logInfo == null)
							|| (parser == null))
						throw new EventHandleException(
								"Why some parameters are null");

					/* if have query to be handled */
					if (parser.size() > 0) {
						this.serverPeer.sendMessage(phyInfo, new Message(head,
								new SPIndexSearchBody(body.getQueryID(), body
										.getInetAddress(), body.getPort(),
										parser.getClauses(), serverPeer
												.getPhysicalInfo(), treeNode
												.getLogicalInfo(), logInfo,
										body.isQueryFilter(), body
												.getQueryFilter(), body
												.isTypeFilter(), body
												.getTypeFilter())));
					}
				}
			}
		} catch (Exception e) {
			throw new IndexOperationException(
					"Super peer performs search failure", e);
		} finally {
			localParser = null;
			remoteParser = null;
			queryList.clear();
			peerList.clear();
		}
	}

	/**
	 * Decompose query clauses into two sets: one set contains the terms that
	 * fall into the index range specified by minKey and maxKey; and the other
	 * contains all the remainder terms.
	 * 
	 * @param clauses the query clauses
	 * @param minKey the minimum key of the index range
	 * @param maxKey the maximum key of the index range
	 */
	private void decompose(QueryClause[] clauses, String minKey, String maxKey) {
		localParser = new QueryParser();
		remoteParser = new QueryParser();

		QueryClause clause = null;
		int size = clauses.length;
		for (int i = 0; i < size; i++) {
			clause = clauses[i];
			int type = clause.getType();
			switch (type) {
			case QueryParser.TERM:
				TermQuery termQuery = (TermQuery) clause.getQuery();
				if (this.belongTo(termQuery.getTerm().text(), minKey, maxKey))
					localParser.addQueryClause(clause);
				else
					remoteParser.addQueryClause(clause);
				break;

			case QueryParser.FUZZY:
				FuzzyQuery fuzzyQuery = (FuzzyQuery) clause.getQuery();
				if (this.belongTo(fuzzyQuery.getTerm().text(), minKey, maxKey))
					localParser.addQueryClause(clause);
				else
					remoteParser.addQueryClause(clause);
				break;

			case QueryParser.WILDCARD:
				WildcardQuery wildQuery = (WildcardQuery) clause.getQuery();
				if (this.belongTo(wildQuery.getTerm().text(), minKey, maxKey))
					localParser.addQueryClause(clause);
				else
					remoteParser.addQueryClause(clause);
				break;

			case QueryParser.RANGE:
				RangeQuery rangeQuery = (RangeQuery) clause.getQuery();
				Term lower = rangeQuery.getLowerTerm();
				Term upper = rangeQuery.getUpperTerm();
				// case 1: min <= lower <= upper < max
				if (this.belongTo(lower.text(), minKey, maxKey)
						&& this.belongTo(upper.text(), minKey, maxKey))
					localParser.addQueryClause(clause);
				// case 2: lower <= upper < min < max
				else if (upper.text().compareTo(minKey) < 0)
					remoteParser.addQueryClause(clause);
				// case 3: upper = min
				else if (upper.text().compareTo(minKey) == 0)
					localParser.addTermQueryClause(upper.field(), upper.text(),
							clause.getOperate());
				// case 4: min < max <= lower < upper
				else if (lower.text().compareTo(maxKey) >= 0)
					remoteParser.addQueryClause(clause);
				// case 5: min <= lower <= max < upper
				else if ((lower.text().compareTo(minKey) >= 0)
						&& (lower.text().compareTo(maxKey) <= 0)
						&& (upper.text().compareTo(maxKey) > 0)) {
					localParser.addRangeQueryClause(lower.field(),
							lower.text(), maxKey, true,
							BooleanClause.Occur.SHOULD);
					remoteParser.addRangeQueryClause(lower.field(), maxKey,
							upper.text(), false, BooleanClause.Occur.SHOULD);
					remoteParser.addTermQueryClause(lower.field(),
							upper.text(), BooleanClause.Occur.SHOULD);
				}
				// case 6: lower <= min < upper < max
				else if ((lower.text().compareTo(minKey) <= 0)
						&& (upper.text().compareTo(minKey) > 0)
						&& (upper.text().compareTo(maxKey) < 0)) {
					localParser.addRangeQueryClause(lower.field(), minKey,
							upper.text(), true, BooleanClause.Occur.SHOULD);
					remoteParser.addRangeQueryClause(lower.field(), lower
							.text(), minKey, false, BooleanClause.Occur.SHOULD);
					remoteParser.addTermQueryClause(lower.field(),
							lower.text(), BooleanClause.Occur.SHOULD);
				}
				// case 7: do not know, just print it out
				else {
					throw new RuntimeException("how about this case: minKey "
							+ minKey + " maxKey " + maxKey + " lower "
							+ lower.text() + " upper " + upper.text());
				}
				break;

			case QueryParser.PREFIX:
				PrefixQuery prefixQuery = (PrefixQuery) clause.getQuery();
				if (this.belongTo(prefixQuery.getPrefix().text(), minKey,
						maxKey))
					localParser.addQueryClause(clause);
				else
					remoteParser.addQueryClause(clause);
				break;

			default:
				throw new RuntimeException("how about the query clause: "
						+ clause.toString());
			}
		}
	}

	/**
	 * Returns <code>true</code> if the query term belongs to the index range
	 * of minKey and maxKey; otherwise, returns <code>false</code>.
	 * 
	 * @param term the query term
	 * @param minKey the minimum key
	 * @param maxKey the maximum key
	 * @return returns <code>true</code> if the query term belongs to the index range
	 * 		   of minKey and maxKey; otherwise, returns <code>false</code>
	 */
	private boolean belongTo(String term, String minKey, String maxKey) {
		if ((term.compareTo(minKey) >= 0) && (term.compareTo(maxKey) < 0))
			return true;
		return false;
	}

	/**
	 * Perform local search and return results.
	 * 
	 * @param parser the query parser for executing local search
	 * @param isQueryFilter  if <code>true</code>, use query filter to filter results
	 * @param queryFilter the query filter
	 * @param isTypeFilter if <code>true</code>, use file extension to filter results
	 * @param typeFilter the desired file extensions
	 * 
	 * @return an array of <code>Document</code> carrying with rank score
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private Document[] search(QueryParser parser, boolean isQueryFilter,
			QueryFilter queryFilter, boolean isTypeFilter, String[] typeFilter)
			throws IOException {
		/* results to be returned */
		docList.clear();
		try {
			/* perform local search */
			if ((ServerPeer.indexExists())
					&& (!IndexReader.isLocked(ServerPeer.getIndexDir()))) {
				indexReader = IndexReader.open(ServerPeer.getIndexDir());
				indexSearcher = new IndexSearcher(indexReader);

				/* get query */
				Query query = parser.getBooleanQuery();

				/* get results */
				Hits hits = null;
				Document doc = null;
				if (isQueryFilter)
					hits = indexSearcher.search(query, queryFilter, new Sort());
				else
					hits = indexSearcher.search(query, new Sort());

				/* no results are found */
				if (hits.length() == 0)
					return null;

				// for each doc, check constraints
				String docID = null;
				float score = 0.0f;
				for (int i = 0; i < hits.length(); i++) {
					doc = hits.doc(i); // get document
					docID = doc.get(FieldConstant.ID.getValue());
					score = hits.score(i); // get ranking score

					// if doc is null, get next
					if (doc == null)
						continue;

					// if doc is a duplicate item, then ignore it and continue
					// next one
					if (docList.containsKey(docID))
						continue;
					// otherwise, store it for detecting duplication
					else {
						// add rank score to document itself
						doc.add(new Field(FieldConstant.Score.getValue(), ""
								+ score, Field.Store.YES,
								Field.Index.UN_TOKENIZED));

						// check file extension if necessary
						if (isTypeFilter) {
							String extension = null;
							String pathName = doc.get(FieldConstant.PathName
									.getValue());
							if (pathName == null)
								continue;
							int idx = pathName.lastIndexOf(".");
							if (idx != -1)
								extension = pathName.substring(idx + 1,
										pathName.length());

							int size = typeFilter.length;
							for (int j = 0; j < size; j++) {
								if (typeFilter[j].equalsIgnoreCase(extension)) {
									docList.put(docID, doc);
									break;
								}
							}
						} else {
							docList.put(docID, doc);
						}
					}
				}

				/* return results */
				Document[] result = new Document[docList.size()];
				int i = 0;
				Entry entry = null;
				Document value = null;
				Iterator it = docList.entrySet().iterator();
				while (it.hasNext()) {
					entry = (Entry) it.next();
					value = (Document) entry.getValue();
					result[i++] = value;
				}
				return result;
			} else {
				throw new RuntimeException("Index is locked");
			}
		} finally {
			if (indexReader != null)
				indexReader.close();
			if (indexSearcher != null)
				indexSearcher.close();
			docList.clear();
		}
	}

	/**
	 * Assign query terms (or clauses) to parsers that will be sent to remote peers.
	 * 
	 * @param treeNode the TreeNode of the current peer
	 * @param parser the query parser contains the clauses to be assigned
	 * @param minKey the minimum index key
	 * @param maxKey the maximum index key
	 */
	private void assignTerms(TreeNode treeNode, QueryParser parser,
			String minKey, String maxKey) {
		Object[] recvObj = null; // temporarily store IP address and logical
		// position of receiver
		PhysicalInfo phyRecv = null; // IP address of receiver
		LogicalInfo lgcRecv = null; // logical position of receiver

		/* get query clauses */
		QueryClause[] clauses = parser.getClauses();
		QueryClause clause = null;
		QueryParser tempParser = null;

		int type = -1;
		String field = null;
		String term = null;
		int size = clauses.length;
		for (int i = 0; i < size; i++) {
			/* for each query clause, determine where it goes */
			clause = clauses[i];
			type = clause.getType();
			switch (type) {
			case QueryParser.TERM:
				TermQuery termQuery = (TermQuery) clause.getQuery();
				field = termQuery.getTerm().field();
				term = termQuery.getTerm().text();
				break;

			case QueryParser.FUZZY:
				FuzzyQuery fuzzyQuery = (FuzzyQuery) clause.getQuery();
				field = fuzzyQuery.getTerm().field();
				term = fuzzyQuery.getTerm().text();
				break;

			case QueryParser.WILDCARD:
				WildcardQuery wildQuery = (WildcardQuery) clause.getQuery();
				field = wildQuery.getTerm().field();
				term = wildQuery.getTerm().text();
				break;

			case QueryParser.RANGE:
				RangeQuery rangeQuery = (RangeQuery) clause.getQuery();
				Term lower = rangeQuery.getLowerTerm();
				// Term upper = rangeQuery.getUpperTerm();
				field = lower.field();
				term = lower.text();
				break;

			case QueryParser.PREFIX:
				PrefixQuery prefixQuery = (PrefixQuery) clause.getQuery();
				field = prefixQuery.getPrefix().field();
				term = prefixQuery.getPrefix().text();
				break;

			default:
				throw new RuntimeException("how about the query clause: "
						+ clause.toString());
			}

			/* field and term exist */
			if ((field != null) && (term != null)) {
				/* find a desired peer to pass the term */
				recvObj = this.lookup(treeNode, minKey, maxKey, term);
				if (recvObj == null)
					continue;

				// record receiver's logical position in BATON tree
				phyRecv = (PhysicalInfo) recvObj[0];
				lgcRecv = (LogicalInfo) peerList.get(phyRecv);
				if (lgcRecv == null) {
					peerList.put(phyRecv, (LogicalInfo) recvObj[1]);
				} else {
					if (!lgcRecv.equals((LogicalInfo) recvObj[1])) {
						throw new RuntimeException(
								"Why not equal of two logical positions of the same super peer");
					}
				}

				// record query clauses into parser
				tempParser = (QueryParser) queryList.get(phyRecv);
				if (tempParser == null) {
					tempParser = new QueryParser();
					tempParser.addQueryClause(clause);
					queryList.put(phyRecv, tempParser);
				} else {
					tempParser = (QueryParser) queryList.remove(phyRecv);
					tempParser.addQueryClause(clause);
					queryList.put(phyRecv, tempParser);
				}
			}
		}
	}

	/**
	 * Returns an array of Objects that contains exactly two elements:
	 * PhysicalInfo and LogicalInfo.
	 * 
	 * @param treeNode the TreeNode of the current peer
	 * @param minKey the minimum index key
	 * @param maxKey the maximum index key
	 * @param query the query clause to be routed
	 * 
	 * @return returns an array of Objects that contains exactly two elements:
	 * 		   PhysicalInfo and LogicalInfo
	 */
	private Object[] lookup(TreeNode treeNode, String minKey, String maxKey,
			String query) {
		Object[] result = new Object[2];

		RoutingTableInfo routingTable = null;
		RoutingItemInfo routingItem = null;
		RoutingItemInfo desiredNode = null;

		/* if index term is less than min key, find a proper node 
		 * from left child, left adjacent node and left routing table */
		if (minKey.compareTo(query) > 0) {
			routingTable = treeNode.getLeftRoutingTable();
			int index = routingTable.getTableSize() - 1;
			int found = -1;
			while ((index >= 0) && (found == -1)) {
				routingItem = routingTable.getRoutingTableNode(index);
				if (routingItem != null) {
					/* find the node who is the most distant to the current peer
					 * and whose max key is larger than term */
					if (routingItem.getMaxValue().getStringValue().compareTo(
							query) > 0) {
						found = index;
					}
				}
				index--;
			}

			/* if found one */
			if (found != -1) {
				desiredNode = routingTable.getRoutingTableNode(found);
				result[0] = desiredNode.getPhysicalInfo();
				result[1] = desiredNode.getLogicalInfo();
				return result;
			} else {
				/* if no routing item is matched, 
				 * then consider left child and left adjacent node */
				ChildNodeInfo leftChild = treeNode.getLeftChild();
				AdjacentNodeInfo leftAdjacentNode = treeNode
						.getLeftAdjacentNode();
				if (leftChild != null) {
					result[0] = leftChild.getPhysicalInfo();
					result[1] = leftChild.getLogicalInfo();
					return result;
				} else if (leftAdjacentNode != null) {
					result[0] = leftAdjacentNode.getPhysicalInfo();
					result[1] = leftAdjacentNode.getLogicalInfo();
					return result;
				} else {
					/* can not find query results, just ignore it */
					// if this term is less than the MIN_KEY of the super peer
					// network
					// we just ignore it
					if (query.compareTo(IndexValue.MIN_KEY.getKey()) < 0) {
						return null;
					} else {
						throw new RuntimeException(
								"Why not found a desired super peer");
					}
				}
			}
		}

		/* if index term is greater than or equal to max key, find a proper node 
		 * from right child, right adjacent node and right routing table */
		if (maxKey.compareTo(query) <= 0) {
			routingTable = treeNode.getRightRoutingTable();
			int index = routingTable.getTableSize() - 1;
			int found = -1;
			while ((index >= 0) && (found == -1)) {
				routingItem = routingTable.getRoutingTableNode(index);
				if (routingItem != null) {
					/* find the node who is the most distant to the current peer
					 * and whose max key is larger than term */
					if (routingItem.getMinValue().getStringValue().compareTo(
							query) <= 0) {
						found = index;
					}
				}
				index--;
			}

			/* if found one */
			if (found != -1) {
				desiredNode = routingTable.getRoutingTableNode(found);
				result[0] = desiredNode.getPhysicalInfo();
				result[1] = desiredNode.getLogicalInfo();
				return result;
			} else {
				/* if no routing item is matched, 
				 * then consider right child and right adjacent node */
				ChildNodeInfo rightChild = treeNode.getRightChild();
				AdjacentNodeInfo rightAdjacentNode = treeNode
						.getRightAdjacentNode();
				if (rightChild != null) {
					result[0] = rightChild.getPhysicalInfo();
					result[1] = rightChild.getLogicalInfo();
					return result;
				} else if (rightAdjacentNode != null) {
					result[0] = rightAdjacentNode.getPhysicalInfo();
					result[1] = rightAdjacentNode.getLogicalInfo();
					return result;
				} else {
					/* can not find query results, just ignore it */
					// if this term is greater than the MAX_KEY of the super
					// peer network
					// we just ignore it
					if (query.compareTo(IndexValue.MAX_KEY.getKey()) > 0) {
						return null;
					} else {
						throw new RuntimeException(
								"Why not found a desired super peer");
					}
				}
			}
		}

		/* for debug! why min <= query < max */
		return null;
		// throw new RuntimeException("why min key: " + minKey + " <= term: " +
		// query + " < max key: " + maxKey);
	}

	public boolean isConsumed(IndexEvent event)
			throws IndexEventHandleException {
		if (event.getHead().getValue() == IndexEventType.SEARCH.getValue())
			return true;
		return false;
	}

}