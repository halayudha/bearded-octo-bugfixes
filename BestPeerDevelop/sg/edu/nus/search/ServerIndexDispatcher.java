/*
 * @(#) ServerIndexDispatcher.java 1.0 2006-7-7
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import org.apache.lucene.analysis.Analyzer;

import sg.edu.nus.gui.AbstractMainFrame;

/**
 * Handle the incoming <code>IndexEvent</code> with the corresponding
 * <code>IndexActionListener</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public final class ServerIndexDispatcher extends AbstractIndexEventDispatcher {

	protected Analyzer analyzer;

	public ServerIndexDispatcher(AbstractMainFrame gui, Analyzer analyzer) {
		super(gui);
		this.analyzer = analyzer;
	}

	@Override
	public void registerActionListeners() {
		this.addActionListener(new SPIndexPublishListener(gui, analyzer));
		this.addActionListener(new SPIndexRemoveListener(gui, analyzer));
		this.addActionListener(new SPIndexQueryListener(gui, analyzer));

		/* handlers for processing message related to KnowledgeBank */
		this.addActionListener(new KBIndexQueryListener(gui, analyzer));
		this.addActionListener(new KBIndexResultListener(gui, analyzer));
	}

}