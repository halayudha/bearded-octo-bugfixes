/*
 * @(#) IndexInsertBody.java 1.0 2006-7-10
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.io.File;

/**
 * Specify the parameters for adding <code>File</code>s
 * into Lucene index.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-10
 */

public class IndexInsertBody extends IndexBody {

	private static final long serialVersionUID = 544369319323306683L;

	/**
	 * if <code>true</code>, then rewrite the Lucene index if it exists
	 * or create a new Lucene index if it does not exists; otherwise, 
	 * append <code>Document</code>s into the Lucene index.
	 */
	private boolean overwrite;

	/**
	 * Construct the body used for inserting <code>File</code>s
	 * into the Lucene index.
	 * 
	 * @param file the <code>File</code> to be inserted
	 * @param b if <code>true</code>, then rewrite the Lucene index if it exists
	 * 			or create a new Lucene index if it does not exists; otherwise, 
	 * 			append <code>Document</code>s into the Lucene index
	 */
	public IndexInsertBody(File file, boolean b) {
		this(new File[] { file }, b);
	}

	/**
	 * Construct the body used for inserting <code>File</code>s
	 * into the Lucene index.
	 * 
	 * @param file the <code>File</code>s to be inserted
	 * @param b if <code>true</code>, then rewrite the Lucene index if it exists
	 * 			or create a new Lucene index if it does not exists; otherwise, 
	 * 			append <code>Document</code>s into the Lucene index
	 */
	public IndexInsertBody(File[] file, boolean b) {
		if (file != null) {
			this.fileToOperate = file.clone();
		}
		this.overwrite = b;
	}

	/**
	 * Returns the status whether overwrites the existing Lucene index.
	 * 
	 * @return the status whether overwrites the existing Lucene index
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

}