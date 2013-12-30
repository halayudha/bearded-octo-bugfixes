/*
 * @(#) IndexDeleteBody.java 1.0 2006-7-10
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.io.File;

/**
 * Specify the parameters for removing <code>File</code>s
 * from Lucene index.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-10
 */

public class IndexDeleteBody extends IndexBody {

	private static final long serialVersionUID = 1215354602449543576L;

	/**
	 * Construct the body used for deleting <code>File</code>
	 * from the Lucene index.
	 * 
	 * @param file the <code>File</code> to be removed
	 */
	public IndexDeleteBody(File file) {
		this(new File[] { file });
	}

	/**
	 * Construct the body used for deleting <code>File</code>s
	 * from the Lucene index.
	 * 
	 * @param file the <code>File</code>s to be removed
	 */
	public IndexDeleteBody(File[] file) {
		if (file != null) {
			this.fileToOperate = file.clone();
		}
	}

}