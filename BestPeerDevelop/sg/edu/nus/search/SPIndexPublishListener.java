/*
 * @(#) SPIndexPublishListener.java 1.0 2007-2-8
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.indexkeyword.FieldConstant;
import sg.edu.nus.indexkeyword.FileHandlerException;
import sg.edu.nus.indexkeyword.TermDocument;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.event.SPGeneralAction;
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
import sg.edu.nus.protocol.body.SPIndexInsertBody;
import sg.edu.nus.search.event.SPIndexPublishBody;

/**
 * Used for inserting terms into index maintained by super peer. The following 
 * tasks are performed by SPIndexInsertListener:
 * <pre>
 * <li>Split received <code>TermDocument</code> into two subsets, say D1 and D2.
 *  All terms in D1 belong to the index range of the current server peer, and
 *  the remainder terms are put into D2.</li>
 * <li>Write D1 to disk with Lucene library.</li>
 * <li>For <code>TermDocument</code> D2, it will be further splitted into two parts.
 *  One contains terms whose values are smaller than the minimum index key of
 *  the current server peer (named D21), and the other for all terms whose values 
 *  are greater than the maximum index key of the current server peer (named D22).</li>
 * <li>Travelling the left routing table, for all terms in D21, send them as a group
 *  to left routing table nodes, left child, or left adjacent in a greedy manner.</li>
 * <li>Travelling the right routing table, for all terms in D22, send them as a group
 *  to right routing table nodes, right child, or right adjacent in a greedy manner.</li>
 * <li>Invoke load balancing operation if necessary.</li>
 * </pre>
 * 
 * @author Xu Linhao
 * @version 1.0 2007-2-8
 * @see sg.edu.nus.peer.event.SPIndexInsertListener
 */

public class SPIndexPublishListener extends SPIndexOperateAdapter {

	/* private variables */
	private ServerPeer serverPeer; // handler of server peer
	private IndexWriter indexWriter; // writer used for writing index to disk
	private Document localDoc; // document handled by local peer
	private TermDocument remoteDoc; // document handled by remote peer

	/* list for sending document to remote peers */
	private Map<PhysicalInfo, TermDocument> docList;
	private Map<PhysicalInfo, LogicalInfo> peerList;

	public SPIndexPublishListener(AbstractMainFrame gui, Analyzer analyzer) {
		super(gui, analyzer);
		this.serverPeer = (ServerPeer) gui.peer();
		this.localDoc = null;
		this.remoteDoc = null;
		this.docList = new Hashtable<PhysicalInfo, TermDocument>();
		this.peerList = new Hashtable<PhysicalInfo, LogicalInfo>();
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(IndexEvent event) throws IOException,
			FileHandlerException, IndexOperationException {
		/* handle received document */
		try {
			if (debug) {
				System.out.println("begin to index document...");
			}

			/* get message body */
			SPIndexPublishBody body = (SPIndexPublishBody) event.getBody();
			TermDocument doc = body.getDocument();

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

			// for debug
			if (debug)
				System.out.println("minKey = " + minKey + ":" + " maxKey = "
						+ maxKey);

			/* split document */
			this.split(doc, minKey, maxKey);

			// for debug
			if (debug) {
				if (localDoc != null) {
					String[] ts = localDoc.getValues(FieldConstant.Content
							.getValue());
					if (ts != null) {
						System.out.println("local index terms [" + ts[0]
								+ " - " + ts[ts.length - 1] + "]");
						ts = null;
					}
				}
			}

			/* handle remainder terms that should be indexed by remote peers */
			if (remoteDoc != null) {
				/* if no terms to be indexed, just return */
				if (!remoteDoc.isEmpty()) {
					/* assign terms from FileName field to documents */
					String field = FieldConstant.FileName.getValue();
					String[] terms = remoteDoc.getTerms(field);
					if (terms != null) {
						this
								.assignTerms(treeNode, minKey, maxKey, field,
										terms);
					}

					/* assign terms from Content field to documents */
					field = FieldConstant.Content.getValue();
					terms = remoteDoc.getTerms(field);
					if (terms != null) {
						this
								.assignTerms(treeNode, minKey, maxKey, field,
										terms);
					}

					/* send messages to remote peers */
					Head head = new Head(MsgType.SP_INSERT.getValue());

					/* iterater of docList */
					Entry entry = null;
					PhysicalInfo phyInfo = null;
					LogicalInfo logInfo = null;
					TermDocument termDoc = null;
					Iterator it = docList.entrySet().iterator();
					while (it.hasNext()) {
						/* get IP address and logical position of remote peer */
						entry = (Entry) it.next();
						phyInfo = (PhysicalInfo) entry.getKey();
						logInfo = (LogicalInfo) peerList.get(phyInfo);
						termDoc = (TermDocument) entry.getValue();

						/* for debug */
						if ((phyInfo == null) || (logInfo == null)
								|| (termDoc == null))
							throw new EventHandleException(
									"Why some parameters are null");

						/* if have terms to be indexed */
						if (!termDoc.isEmpty()) {
							this.serverPeer
									.sendMessage(phyInfo, new Message(head,
											new SPIndexInsertBody(body
													.getPeerID(), body
													.getInetAddress(), body
													.getPort(), termDoc,
													serverPeer
															.getPhysicalInfo(),
													treeNode.getLogicalInfo(),
													logInfo)));

							// for debug
							if (debug)
								System.out
										.println("send document to remote peers ["
												+ phyInfo.getIP()
												+ ":"
												+ phyInfo.getPort() + "]");
						}
					}
				}
			}

			/* write document to local index */
			if (localDoc != null) {
				this.insert(localDoc, body.getPeerID(), body.getInetAddress(),
						body.getPort());
			}
		} catch (Exception e) {
			throw new IndexOperationException(
					"Super peer inserts index failure", e);
		} finally {
			/* clear documents and hashtables */
			localDoc = null;
			remoteDoc = null;
			docList.clear();
			peerList.clear();
		}
	}

	/**
	 * Split received <code>TermDocument</code> into two subsets, 
	 * say localDoc and remoteDoc. All terms in localDoc belong to 
	 * [minKey, maxKey), and the remainder terms are put into remoteDoc.
	 * 
	 * @param doc the document to be splitted
	 * @param minKey the minimum key of the current peer
	 * @param maxKey the maximum key of the current peer
	 */
	private void split(TermDocument doc, String minKey, String maxKey) {
		localDoc = new Document();
		remoteDoc = new TermDocument();

		/* get fields and corresponding terms */
		String field = null;
		String[] fields = doc.getFields();
		String[] terms = null;
		int size = fields.length;
		for (int i = 0; i < size; i++) {
			field = fields[i];
			terms = doc.getTerms(field);
			// DocID
			if (field.equals(FieldConstant.ID.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"ID error in TermDocument");
				localDoc.add(new Field(FieldConstant.ID.getValue(), terms[0],
						Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.ID.getValue(), terms[0]);
			}
			// FileName
			else if (field.equals(FieldConstant.FileName.getValue())) {
				String term = null;
				for (int j = 0; j < terms.length; j++) {
					term = terms[j];
					/* term belongs to [minKey, maxKey) */
					if ((term.compareTo(minKey) >= 0)
							&& (term.compareTo(maxKey) < 0)) {
						localDoc.add(new Field(FieldConstant.FileName
								.getValue(), term, Field.Store.YES,
								Field.Index.TOKENIZED, Field.TermVector.YES));
					} else {
						remoteDoc.addField(FieldConstant.FileName.getValue(),
								term);
					}
				}
			}
			// Content
			else if (field.equals(FieldConstant.Content.getValue())) {
				if (terms != null && terms.length > 0) {
					Arrays.sort(terms);
					int start = Arrays.binarySearch(terms, minKey);
					if (start < 0)
						start = -start - 1;

					int end = Arrays.binarySearch(terms, maxKey);
					if (end < 0)
						end = -end - 1;

					if (start > end)
						throw new IllegalArgumentException(
								"Content error in TermDocument");

					// construct local document
					for (int j = start; j < end; j++) {
						localDoc.add(new Field(
								FieldConstant.Content.getValue(), terms[j],
								Field.Store.YES, Field.Index.TOKENIZED,
								Field.TermVector.YES));
					}
					for (int j = 0; j < start; j++) {
						remoteDoc.addField(FieldConstant.Content.getValue(),
								terms[j]);
					}

					// construct remote document
					for (int j = end; j < terms.length; j++) {
						remoteDoc.addField(FieldConstant.Content.getValue(),
								terms[j]);
					}
				}
			}
			// Summary
			else if (field.equals(FieldConstant.Summary.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.Summary.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.Summary.getValue(), terms[0]);
			}
			// PathName
			else if (field.equals(FieldConstant.PathName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.PathName.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.PathName.getValue(), terms[0]);
			}
			// LastModified
			else if (field.equals(FieldConstant.LastModified.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.LastModified.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.LastModified.getValue(),
						terms[0]);
			}
			// Size
			else if (field.equals(FieldConstant.Size.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.Size.getValue(), terms[0],
						Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.Size.getValue(), terms[0]);
			}
			// database name
			else if (field.equals(FieldConstant.DatabaseName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.DatabaseName.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.DatabaseName.getValue(),
						terms[0]);
			}
			// table name
			else if (field.equals(FieldConstant.TableName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.TableName.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc
						.addField(FieldConstant.TableName.getValue(), terms[0]);
			}
			// column name
			else if (field.equals(FieldConstant.ColumnName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.ColumnName.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.ColumnName.getValue(),
						terms[0]);
			}
			// key value
			else if (field.equals(FieldConstant.KeyValue.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				localDoc.add(new Field(FieldConstant.KeyValue.getValue(),
						terms[0], Field.Store.YES, Field.Index.UN_TOKENIZED));
				remoteDoc.addField(FieldConstant.KeyValue.getValue(), terms[0]);
			}
		}
	}

	/**
	 * Insert document into the Lucene index.
	 * 
	 * @param doc the document to be indexed
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer who shares the document
	 * @param port the port of the peer
	 * @throws IOException
	 */
	private void insert(Document doc, String pid, InetAddress ipaddr, int port)
			throws IOException {
		try {
			boolean overwrite = !ServerPeer.indexExists();
			if (overwrite) {
				Directory dir = FSDirectory.getDirectory(ServerPeer
						.getIndexDir(), overwrite);
				indexWriter = new IndexWriter(dir, analyzer, overwrite);
			} else {
				if (!IndexReader.isLocked(ServerPeer.getIndexDir())) {
					Directory dir = FSDirectory.getDirectory(ServerPeer
							.getIndexDir(), overwrite);
					indexWriter = new IndexWriter(dir, analyzer, overwrite);
				} else {
					throw new RuntimeException("Index is locked");
				}
			}
			indexWriter.setUseCompoundFile(true);

			// add user name, ip address and port to doc
			localDoc.add(new Field(FieldConstant.UserID.getValue(), pid,
					Field.Store.YES, Field.Index.UN_TOKENIZED));
			localDoc.add(new Field(FieldConstant.InetAddress.getValue(), ipaddr
					.getHostAddress(), Field.Store.YES,
					Field.Index.UN_TOKENIZED));
			localDoc.add(new Field(FieldConstant.Port.getValue(), "" + port,
					Field.Store.YES, Field.Index.UN_TOKENIZED));

			// write document to local index
			indexWriter.addDocument(localDoc);
			indexWriter.optimize();
			indexWriter.close();
		} finally {
			if (indexWriter != null)
				indexWriter.close();
		}
	}

	/**
	 * Assign terms to documents that will be sent to remote peers.
	 * 
	 * @param treeNode the TreeNode of the current peer
	 * @param minKey the minimum index key
	 * @param maxKey the maximum index key
	 * @param field the field that the terms belong to
	 * @param terms the terms to be processed
	 */
	private void assignTerms(TreeNode treeNode, String minKey, String maxKey,
			String field, String[] terms) {
		Object[] recvObj = null; // temporarily store IP address and logical
		// position of receiver
		PhysicalInfo phyRecv = null; // IP address of receiver
		LogicalInfo lgcRecv = null; // logical position of receiver
		TermDocument termDoc = null;
		String term = null;

		int size = terms.length;
		for (int i = 0; i < size; i++) {
			term = terms[i];

			/* find a desired peer to pass the term */
			recvObj = this.lookup(treeNode, minKey, maxKey, field, term);
			/* 
			 * if recvObj equals to null, it means the term is either
			 * less than MIN_KEY or greater than MAX_KEY of the super
			 * peer network. we simply ignore such term.
			 */
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

			// record document to be sent to receiver
			termDoc = (TermDocument) docList.get(phyRecv);
			if (termDoc == null) {
				termDoc = new TermDocument();
				termDoc.addField(field, term);
				this.appendFields(remoteDoc, termDoc);
				docList.put(phyRecv, termDoc);
			} else {
				termDoc = docList.remove(phyRecv);
				termDoc.addField(field, term);
				docList.put(phyRecv, termDoc);
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
	 * @param field the field that the terms belong to
	 * @param term the search key used for determining which peer it sends to
	 * 
	 * @return returns an array of Objects that contains exactly two elements:
	 * 		   PhysicalInfo and LogicalInfo
	 */
	private Object[] lookup(TreeNode treeNode, String minKey, String maxKey,
			String field, String term) {
		Object[] result = new Object[2];

		RoutingTableInfo routingTable = null;
		RoutingItemInfo routingItem = null;
		RoutingItemInfo desiredNode = null;

		/* if index term is less than min key, find a proper node 
		 * from left child, left adjacent node and left routing table */
		if (minKey.compareTo(term) > 0) {
			routingTable = treeNode.getLeftRoutingTable();
			int index = routingTable.getTableSize() - 1;
			int found = -1;
			while ((index >= 0) && (found == -1)) {
				routingItem = routingTable.getRoutingTableNode(index);
				if (routingItem != null) {
					/* find the node who is the most distant to the current peer
					 * and whose max key is larger than term */
					if (routingItem.getMaxValue().getStringValue().compareTo(
							term) > 0) {
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
					// if this term is less than the MIN_KEY of the super peer
					// network
					// we just ignore it
					if (term.compareTo(IndexValue.MIN_KEY.getKey()) < 0) {
						return null;
					} else {
						IndexValue newMinKey = new IndexValue(
								IndexValue.STRING_TYPE, term);
						treeNode.getContent().insertData(newMinKey, 1);
						SPGeneralAction.updateRangeValues(this.serverPeer,
								treeNode);
						this.localDoc.add(new Field(field, term,
								Field.Store.YES, Field.Index.TOKENIZED,
								Field.TermVector.YES));
					}
				}
			}
		}

		/* if index term is greater than or equal to max key, find a proper node 
		 * from right child, right adjacent node and right routing table */
		if (maxKey.compareTo(term) <= 0) {
			routingTable = treeNode.getRightRoutingTable();
			int index = routingTable.getTableSize() - 1;
			int found = -1;
			while ((index >= 0) && (found == -1)) {
				routingItem = routingTable.getRoutingTableNode(index);
				if (routingItem != null) {
					/* find the node who is the most distant to the current peer
					 * and whose max key is larger than term */
					if (routingItem.getMinValue().getStringValue().compareTo(
							term) <= 0) {
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
					// if this term is greater than the MAX_KEY of the super
					// peer network
					// we just ignore it
					if (term.compareTo(IndexValue.MAX_KEY.getKey()) > 0) {
						return null;
					} else {
						IndexValue newMaxKey = new IndexValue(
								IndexValue.STRING_TYPE, term);
						treeNode.getContent().insertData(newMaxKey, 2);
						SPGeneralAction.updateRangeValues(this.serverPeer,
								treeNode);
						this.localDoc.add(new Field(field, term,
								Field.Store.YES, Field.Index.TOKENIZED,
								Field.TermVector.YES));
					}
				}
			}
		}

		/* for debug! why min <= term < max */
		return null;
	}

	/**
	 * Append a document's fields to another one.
	 * 
	 * @param doc the document whose fields will be appended to newDoc
	 * @param newDoc the document to be appended fields of doc
	 */
	private void appendFields(TermDocument doc, TermDocument newDoc) {
		// get fields and corresponding terms
		String field = null;
		String[] fields = doc.getFields();
		String[] terms = null;
		int size = fields.length;
		for (int i = 0; i < size; i++) {
			field = fields[i];
			terms = doc.getTerms(field);
			// DocID
			if (field.equals(FieldConstant.ID.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"ID error in TermDocument");
				newDoc.addField(FieldConstant.ID.getValue(), terms[0]);
			}
			// Summary
			else if (field.equals(FieldConstant.Summary.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc.addField(FieldConstant.Summary.getValue(), terms[0]);
			}
			// PathName
			else if (field.equals(FieldConstant.PathName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc.addField(FieldConstant.PathName.getValue(), terms[0]);
			}
			// LastModified
			else if (field.equals(FieldConstant.LastModified.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc
						.addField(FieldConstant.LastModified.getValue(),
								terms[0]);
			}
			// Size
			else if (field.equals(FieldConstant.Size.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc.addField(FieldConstant.Size.getValue(), terms[0]);
			}
			// Database Name
			else if (field.equals(FieldConstant.DatabaseName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc
						.addField(FieldConstant.DatabaseName.getValue(),
								terms[0]);
			}
			// Table Name
			else if (field.equals(FieldConstant.TableName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc.addField(FieldConstant.TableName.getValue(), terms[0]);
			}
			// Column Name
			else if (field.equals(FieldConstant.ColumnName.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc.addField(FieldConstant.ColumnName.getValue(), terms[0]);
			}
			// Key Value
			else if (field.equals(FieldConstant.KeyValue.getValue())) {
				if (terms.length != 1)
					throw new IllegalArgumentException(
							"Summary error in TermDocument");
				newDoc.addField(FieldConstant.KeyValue.getValue(), terms[0]);
			}
		}
	}

	public boolean isConsumed(IndexEvent event)
			throws IndexEventHandleException {
		if (event.getHead().getValue() == IndexEventType.INSERT.getValue())
			return true;
		return false;
	}

}