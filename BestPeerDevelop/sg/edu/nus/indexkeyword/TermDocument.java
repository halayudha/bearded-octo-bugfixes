/*
 * @(#) TermDocument.java 1.0 2007-1-15
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.indexkeyword;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * TermDocument simply simulates TermFreqVector of the Lucene library,
 * that is, it stores all terms of each field for the purpose of easily
 * operating on terms.
 * 
 * @author Xu Linhao
 * @version 1.0 2007-1-15
 */
@SuppressWarnings("unchecked")
public class TermDocument implements Serializable {

	private static final long serialVersionUID = -8363668229720893930L;

	private static final String indexDir = System.getProperty("java.io.tmpdir",
			"tmp")
			+ System.getProperty("file.separator")
			+ "index-dir"
			+ System.getProperty("file.separator");

	private Map<String, ArrayList> table;

	/**
	 * Construct an empty TermDocument.
	 */
	public TermDocument() {
		table = new Hashtable<String, ArrayList>();
	}

	/**
	 * Construct TermDocument with the Lucene IndexReader.
	 * 
	 * @param reader the lucene index reader
	 * @param index the index-th document in the index reader
	 * @throws IOException
	 */
	public TermDocument(IndexReader reader, int index) throws IOException {
		this();

		if (reader == null)
			throw new IllegalArgumentException("Index reader is not open");

		Document doc = reader.document(index);
		TermFreqVector[] termFreqVector = reader.getTermFreqVectors(index);
		if ((termFreqVector != null) && (doc != null)) {
			String field = null;
			String term = null;
			String[] terms = null;

			// FileName and Content
			for (int i = 0; i < termFreqVector.length; i++) {
				terms = termFreqVector[i].getTerms();
				field = termFreqVector[i].getField();
				this.addField(field, terms);
			}

			// for remainder fields
			Enumeration e = doc.fields();
			while (e.hasMoreElements()) {
				field = ((Field) e.nextElement()).name();
				/* ignore it if null */
				if (field == null || field.equals(""))
					continue;
				// docID
				if (field.equalsIgnoreCase(FieldConstant.ID.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// file name
				else if (field.equalsIgnoreCase(FieldConstant.FileName
						.getValue())) {
					/* ignore it */
				}
				// content
				else if (field.equalsIgnoreCase(FieldConstant.Content
						.getValue())) {
					/* ignore it */
				}
				// summary
				else if (field.equalsIgnoreCase(FieldConstant.Summary
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// size
				else if (field.equalsIgnoreCase(FieldConstant.Size.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// path name
				else if (field.equalsIgnoreCase(FieldConstant.PathName
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// last modified
				else if (field.equalsIgnoreCase(FieldConstant.LastModified
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// user id
				else if (field
						.equalsIgnoreCase(FieldConstant.UserID.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// ip address
				else if (field.equalsIgnoreCase(FieldConstant.InetAddress
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// port
				else if (field.equalsIgnoreCase(FieldConstant.Port.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// score
				else if (field.equalsIgnoreCase(FieldConstant.Score.getValue())) {
					/* ignore it */
				}
				// database name
				else if (field.equalsIgnoreCase(FieldConstant.DatabaseName
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// table name
				else if (field.equalsIgnoreCase(FieldConstant.TableName
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// column name
				else if (field.equalsIgnoreCase(FieldConstant.ColumnName
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// key value
				else if (field.equalsIgnoreCase(FieldConstant.KeyValue
						.getValue())) {
					term = doc.get(field);
					this.addField(field, term);
				}
				// unknown field
				else {
					throw new IllegalArgumentException("Unknown field: "
							+ field);
				}
			}
		}
	}

	/**
	 * Transform Lucene document to TermDocument.
	 * 
	 * @param doc the lucene document
	 * @return returns the TermDocument
	 */
	public static TermDocument toTermDocument(final Document doc)
			throws IOException {
		IndexWriter writer = null;
		IndexReader reader = null;
		File tempDir = null;
		String docID = null;
		try {
			/* create a temp directory */
			docID = doc.get(FieldConstant.ID.getValue());
			tempDir = new File(indexDir + docID);
			createTempIndex(tempDir);

			/* write document to directory */
			Directory dir = FSDirectory.getDirectory(tempDir, true);
			writer = new IndexWriter(dir, new StandardAnalyzer(), true);
			writer.setUseCompoundFile(true);
			writer.addDocument(doc);
			writer.optimize();
			writer.close();
			writer = null;

			/* read document to get term freq vector */
			reader = IndexReader.open(dir);
			if (reader.numDocs() != 1)
				throw new IllegalArgumentException(
						"Number of document is not equal to 1");

			TermDocument termDoc = new TermDocument(reader, 0);
			reader.close();
			reader = null;

			return termDoc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (writer != null)
				writer.close();
			if (reader != null)
				reader.close();
			deleteTempIndex(tempDir.getParentFile());
		}
	}

	/**
	 * Create a temp directory that stores the lucene document to be indexed.
	 * 
	 * @param file the directory name to be indexed
	 */
	private static void createTempIndex(File file) {
		if (!file.exists())
			file.mkdirs();
		deleteTempIndex(file);
	}

	/**
	 * Delete a temp directory that stores the lucene document to be removed.
	 * 
	 * @param file the directory name to be deleted
	 */
	private static void deleteTempIndex(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteTempIndex(files[i]);
						files[i].delete();
					} else {
						files[i].delete();
					}
				}
			} else {
				file.delete();
			}
		}
	}

	/**
	 * Put a term to its field.
	 * 
	 * @param field the field
	 * @param term  the term belonging to the field
	 */
	public void addField(String field, String term) {
		ArrayList<String> arrayList = null;
		if (!table.containsKey(field)) {
			arrayList = new ArrayList<String>();
			arrayList.add(term);
			table.put(field, arrayList);
		} else {
			arrayList = (ArrayList<String>) table.get(field);
			arrayList.add(term);
		}
	}

	/**
	 * Put a set of terms to its field.
	 * 
	 * @param field the field
	 * @param terms a set of terms belonging to the field
	 */
	public void addField(String field, String[] terms) {
		ArrayList<String> arrayList = null;
		if (!table.containsKey(field)) {
			arrayList = new ArrayList<String>();
			int size = terms.length;
			for (int i = 0; i < size; i++)
				arrayList.add(terms[i]);
			table.put(field, arrayList);
		} else {
			arrayList = (ArrayList<String>) table.get(field);
			int size = terms.length;
			for (int i = 0; i < size; i++)
				arrayList.add(terms[i]);
		}
	}

	/**
	 * Returns all fields stored in the TermDocument. If no fields
	 * in TermDocument, just return null.
	 * 
	 * @return returns all fields stored in the TermDocument
	 */
	public String[] getFields() {
		Set<String> keySet = table.keySet();
		if (keySet.isEmpty())
			return null;
		String[] keys = new String[keySet.size()];
		keySet.toArray(keys);
		return keys;
	}

	/**
	 * Returns all terms that belong to a specified field. If no such
	 * field exists, just return null.
	 * 
	 * @param field the field
	 * @return returns all terms that belong to a specified field
	 */
	public String[] getTerms(String field) {
		if (!table.containsKey(field))
			return null;
		ArrayList<String> arrayList = (ArrayList<String>) table.get(field);
		String[] terms = new String[arrayList.size()];
		arrayList.toArray(terms);
		return terms;
	}

	/**
	 * Returns true if no terms to be indexed; otherwise, returns false.
	 * 
	 * @return returns true if no terms to be indexed; otherwise, returns false
	 */
	public boolean isEmpty() {
		if (getTerms(FieldConstant.DatabaseName.getValue()) != null
				&& getTerms(FieldConstant.DatabaseName.getValue()).length != 0
				&& getTerms(FieldConstant.Content.getValue()) != null
				&& getTerms(FieldConstant.Content.getValue()).length != 0)
			return false;
		else
			return ((getTerms(FieldConstant.FileName.getValue()) == null || getTerms(FieldConstant.FileName
					.getValue()).length == 0) && (getTerms(FieldConstant.Content
					.getValue()) == null || getTerms(FieldConstant.Content
					.getValue()).length == 0));
	}

	public String toString() {
		if (table.isEmpty())
			return "NULL";

		String result = new String();
		String newline = "\n";
		String[] field = getFields();
		String[] terms = null;
		for (int i = 0; i < field.length; i++) {
			result += field[i] + ": ";
			terms = getTerms(field[i]);
			if (terms == null)
				result += "NULL" + newline;
			else {
				for (int j = 0; j < terms.length; j++)
					result += terms[j] + " ";
				result += newline;
			}
		}

		return result;
	}

}