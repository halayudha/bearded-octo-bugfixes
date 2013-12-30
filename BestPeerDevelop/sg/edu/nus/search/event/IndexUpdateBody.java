/*
 * @(#) IndexUpdateBody.java 1.0 2006-7-10
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.io.File;

/**
 * Specify the parameters for updating <code>File</code>s
 * in Lucene index.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-10
 */

public class IndexUpdateBody extends IndexBody {

	private static final long serialVersionUID = 8461731102062099694L;

	/**
	 * Construct the body used for updating Lucene index.
	 * 
	 * @param file the <code>File</code> to be updated
	 */
	public IndexUpdateBody(File file) {
		this(new File[] { file });
	}

	/**
	 * Construct the body used for updating Lucene index.
	 * 
	 * @param file the <code>File</code>s to be updated
	 */
	public IndexUpdateBody(File[] file) {
		if (file != null) {
			this.fileToOperate = file.clone();
		}
	}

}