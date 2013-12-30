/*
 * @(#) ServerIndexManager.java 1.0 2006-7-7
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import org.apache.lucene.analysis.Analyzer;

import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;

/**
 * The index manager is used for listening the incoming <code>IndexEvent</code>s
 * and passing them to the <code>IndexEventDispatcher</code> for processing.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public final class ServerIndexManager extends AbstractIndexEventServer {

	// protected members
	protected ServerGUI servergui;
	protected ServerPeer serverPeer;
	protected Analyzer analyzer;

	/**
	 * Construct a <code>ServerIndexServerManager</code>.
	 *  
	 * @param peer the handler of server peer 
	 * @param analyzer the <code>Analyzer</code> used for parsing <code>File</code>
	 */
	public ServerIndexManager(ServerPeer peer, Analyzer analyzer) {
		this.serverPeer = peer;
		this.servergui = (ServerGUI) peer.getMainFrame();
		this.analyzer = analyzer;
	}

	@Override
	public void setupHandlers() {
		this.eventHandler = new ServerIndexDispatcher(servergui, analyzer);
		this.eventHandler.registerActionListeners();
		this.eventThread = new Thread(eventHandler, "Server Index Handler");
		this.eventThread.start();
	}

	public void stop() {
		stop = true;

		/* stop handler first */
		eventHandler.stop();
		AbstractIndexEventHandler.stopAllHandlers();
	}

}