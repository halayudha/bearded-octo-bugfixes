/*
 * @(#) FieldConstant.java 1.0 2006-4-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.indexkeyword;

/**
 * Define all fields to be indexed by <code>FileIndexer</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-4-10
 */

public enum FieldConstant {

	/**
	 * Denote the identifier of the file. 
	 * The content of this field should be stored.
	 */
	ID("DocID"),

	/**
	 * Denote "name" without path information.
	 * The content of this field should be stored.
	 */
	FileName("File Name"),

	/**
	 * Denote the content of the file.
	 */
	Content("Content"),

	/**
	 * Denote the summary of the file.
	 * The content of this field should be stored.
	 */
	Summary("Summary"),

	/**
	 * Denote the file size.
	 */
	Size("Size"),

	/**
	 * Denote "file name" with path information.
	 * The content of this field should be stored.
	 */
	PathName("Path Name"),

	/**
	 * Denote "last modified" property of the file.
	 * The content of this field should be stored.
	 */
	LastModified("Last Modified"),

	/**
	 * Denote the login user name who is responsible
	 * for sharing the file.
	 */
	UserID("User ID"),

	/**
	 * Denote the IP address of the login user name.
	 */
	InetAddress("IP Address"),

	/**
	 * Denote the port of the login user.
	 */
	Port("Port"),

	/**
	 * Only used for returned results.
	 */
	Score("Score"),

	/**
	 * New Fields Added by Wu Sai to support Database Indexing
	 */

	/**
	 * Database Name
	 */
	DatabaseName("Database Name"),

	/**
	 * Table Name
	 */
	TableName("Table Name"),

	/**
	 * Column Name (Indexed Column always refer to key attribute)
	 */
	ColumnName("Column Name"),

	/**
	 * Key Value
	 */
	KeyValue("Key Value");

	// private members
	private final String fieldName;

	/**
	 * Constructor.
	 * 
	 * @param fieldName the field name
	 */
	FieldConstant(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Return the field name.
	 * 
	 * @return the field name
	 */
	public String getValue() {
		return this.fieldName;
	}

	/**
	 * Determine if contains the given field name.
	 * 
	 * @param fieldName the given field name
	 * @return if contains such a field, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public boolean checkValue(String fieldName) {
		for (FieldConstant f : FieldConstant.values()) {
			if (f.getValue().equalsIgnoreCase(fieldName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the array of <code>String</code> that lists
	 * the fields to be searched.
	 * 
	 * @return the array of <code>String</code> that lists
	 * 			the fields to be searched
	 */
	public static String[] getFields() { // exclusive "docID", "filename",
		// "size", "userID" and "inet"
		int size = FieldConstant.values().length - 5;
		String[] fields = new String[size];

		fields[0] = FileName.getValue();
		fields[1] = Content.getValue();
		fields[2] = Summary.getValue();
		fields[3] = LastModified.getValue();

		return fields;
	}

}