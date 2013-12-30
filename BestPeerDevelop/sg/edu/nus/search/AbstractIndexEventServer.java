/*
 * @(#) AbstractIndexEventServer.java 1.0 2006-7-7
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

/**
 * Define an abstract class used for listening the event related to index operations,
 * and then pass the event to the corresponding event handler.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public abstract class AbstractIndexEventServer implements Runnable {

	/**
	 * A set of <code>AbstractIndexEventDispatcher</code> that will be
	 * responsible for processing the incoming <code>IndexEvent</code>.
	 */
	protected AbstractIndexEventDispatcher eventHandler;

	/**
	 * The reference of the eventHandler.
	 */
	protected Thread eventThread;

	/**
	 * The signal used to determine whether stops 
	 * the service of accepting the incoming index requests.
	 */
	protected volatile boolean stop;

	/**
	 * Constructor.
	 */
	public AbstractIndexEventServer() {
		this.stop = false;
	}

	/**
	 * Initiate the <code>AbstractIndexEventHandler</code>
	 * for processing the <code>IndexEvent</code>s. 
	 */
	public abstract void setupHandlers();

	/**
	 * Accept the incoming <code>IndexEvent</code>.
	 * 
	 * @param event the incoming <code>IndexEvent</code>
	 * @throws IndexEventServerBindException
	 */
	public void accept(IndexEvent event) throws IndexEventServerBindException {
		if (!stop) {
			AbstractIndexEventHandler.processRequest(event);
		} else {
			throw new IndexEventServerBindException(
					"Index event server is stopped");
		}
	}

	/**
	 * Judge if the <code>AbstractIndexEventServer</code>
	 * is running.
	 * 
	 * @return if <code>true</code>, then the server is running;
	 * 			otherwise, <code>false</code>
	 */
	public boolean isAlive() {
		return !stop;
	}

	/**
	 * Returns the <code>Thread</code> of the event handler.
	 * 
	 * @return returns the <code>Thread</code> of the event handler
	 */
	public Thread getEventHandler() {
		return this.eventThread;
	}

	public void run() {
		this.setupHandlers();
	}

}
