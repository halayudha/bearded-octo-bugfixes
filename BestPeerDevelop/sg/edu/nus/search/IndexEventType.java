/**
 * @(#) IndexEventType.java 1.0 2006-7-7
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

/**
 * Define the event type for index operations, including
 * index, deletion, update, renew, optimization, search.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-6-22
 */

public enum IndexEventType {

	// define event type bellow
	INSERT(1), // for indexing files
	DELETE(2), // for removing indexed files
	UPDATE(3), // for re-indexing a particular file
	RENEW(4), // for re-indexing shared files
	OPTIMIZE(5), // for optimizing index
	SEARCH(6), // for searching user queries
	RESULT(7), // for result

	KB_SEARCH(8), // for knowledge bank search
	KB_RESULT(9), // for knowledge bank result

	// added by wusai for support of database
	DBINSERT(10), // for indexing database tuple
	DBDELETE(11), // for removing indexed database tuple
	DBUPDATE(12); // for reindexing a tuple

	/* define message type bellow */
	private final int value;

	IndexEventType(int value) {
		this.value = value;
	}

	/**
	 * Get the value of the event type.
	 * 
	 * @return the value of the event type
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Get the value of the event type.
	 * 
	 * @param eventType the type of the event
	 * @return the value of the event type; if no such event type return -1
	 */
	public static int getValue(int eventType) {
		for (IndexEventType m : IndexEventType.values()) {
			if (m.getValue() == (eventType)) {
				return m.getValue();
			}
		}
		return -1;
	}

	/**
	 * Check if the event type exists.
	 * 
	 * @param eventType the event type
	 * @return <code>true</code> if exists; otherwise, <code>false</code>
	 */
	public static boolean checkValue(int eventType) {
		for (IndexEventType m : IndexEventType.values()) {
			if (m.getValue() == (eventType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the description of the <code>IndexEventType</code>.
	 * 
	 * @param type the index event type
	 * @return the string used for describing the <code>IndexEventType</code> 
	 */
	public static String description(IndexEventType type) {
		int typeVal = type.getValue();
		String result = "";

		// ---------- COMMON EVENT ------------
		if (IndexEventType.INSERT.getValue() == typeVal) {
			result = "Lucene index insertion";
		} else if (IndexEventType.DELETE.getValue() == typeVal) {
			result = "Lucene index deletion";
		} else if (IndexEventType.UPDATE.getValue() == typeVal) {
			result = "Lucene index update";
		} else if (IndexEventType.RENEW.getValue() == typeVal) {
			result = "Lucene index rebuild";
		} else if (IndexEventType.OPTIMIZE.getValue() == typeVal) {
			result = "Lucene index optimization";
		} else if (IndexEventType.SEARCH.getValue() == typeVal) {
			result = "Lucene keyword search";
		} else if (IndexEventType.RESULT.getValue() == typeVal) {
			result = "Search results are returned";
		} else if (IndexEventType.KB_SEARCH.getValue() == typeVal) {
			result = "KnowledgeBank searches query";
		} else if (IndexEventType.KB_RESULT.getValue() == typeVal) {
			result = "KnowledgeBank results are returned";
		} else if (IndexEventType.DBINSERT.getValue() == typeVal) {
			result = "Database tuple index is inserted";
		} else if (IndexEventType.DBDELETE.getValue() == typeVal) {
			result = "Database tuple index is deleted";
		} else if (IndexEventType.DBUPDATE.getValue() == typeVal) {
			result = "Database tuple index is updated";
		} else {
			result = "Unknown operation";
		}

		return result;
	}

}