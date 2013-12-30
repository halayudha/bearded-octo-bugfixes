/*
 * @(#) SPIndexOperateAdapter.java	1.0 2006-7-7
 *
 * Copyright 2006, National University of Singapore. 
 * All rights reserved.
 */

package sg.edu.nus.search;

import org.apache.lucene.analysis.Analyzer;

import sg.edu.nus.gui.*;

/**
 * An abstract adapter class for receiving index events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public abstract class SPIndexOperateAdapter implements IndexActionListener {

	// protected members
	protected static final boolean debug = true;
	protected AbstractMainFrame gui;
	/**
	 * The Lucene <code>Analyzer</code> used for parsing <code>Document</code>.
	 */
	protected Analyzer analyzer;

	/**
	 * Construct an <code>IndexOperator</code> with specified parameters.
	 * 
	 * @param gui the handler of main frame
	 * @param analyzer a Lucene <code>Analyzer</code> used for analysing <code>File</code>
	 */
	public SPIndexOperateAdapter(AbstractMainFrame gui, Analyzer analyzer) {
		this.gui = gui;
		this.analyzer = analyzer;
	}

}