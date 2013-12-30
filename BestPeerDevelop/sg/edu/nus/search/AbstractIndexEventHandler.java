/*
 * @(#) AbstractIndexEventHandler.java 1.0 2006-7-7
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

/**
 * Define an abstract event handler by implementing
 * <code>Runnable</code> to process the incoming <code>IndexEvent</code>s.
 * <p>
 * An incoming <code>IndexEvent</code>s will be passed from 
 * the <code>AbstractIndexEventServer</code> and then be put
 * into a event pool for further processing.
 * <p>
 * If the pool is not empty, then a <code>AbstractIndexEventHandler</code>
 * will be invoked alive to process it.
 * <p>
 * If the stop signal is set as <code>true</code>, then the handler will
 * be stopped.
 *  
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 * 
 * @see sg.edu.nus.net.AbstractPooledSocketServer 
 */

public abstract class AbstractIndexEventHandler implements Runnable {

	// protected members

	/**
	 * A pool for keeping a set of incoming <code>IndexEvent</code>s,
	 * which is waiting for being processed by <code>AbstractIndexEventHandler</code>.
	 */
	protected static List<IndexEvent> pool = Collections.checkedList(
			new LinkedList<IndexEvent>(), IndexEvent.class);

	/**
	 * A signal to determine whether stop the service of 
	 * the <code>AbstractIndexEventHandler</code>. The thread will 
	 * check this singal continuously and if equals to <code>true</code>,
	 * otherwise the handler will be released.
	 */
	protected volatile boolean stop;

	/**
	 * The incoming event to be processed.
	 */
	protected IndexEvent request;

	/**
	 * Initiate the stop singal as false.
	 */
	public AbstractIndexEventHandler() {
		this.stop = false;
	}

	/**
	 * Accepting the socket connection to be handled from 
	 * the <code>AbstractPooledSocketServer</code> and then
	 * put the socket connection into a pool for further processing.
	 * 
	 * @param requestToHandle the incoming socket connetion to be processed
	 */
	public static void processRequest(IndexEvent requestToHandle) {
		synchronized (pool) {
			pool.add(pool.size(), requestToHandle);
			pool.notifyAll();
		}
	}

	/**
	 * Dispatch the incoming requests to the corresponding message processor,
	 * and reply with an outgoing response if necessary.
	 */
	public abstract void handleConnection();

	/**
	 * If the stop signal is <code>false</code> and the socket pool is not empty,
	 * then the first socket will be removed from the pool and passed to a 
	 * <code>AbstractPooledSocketHanlder</code> to process it.
	 */
	public void run() {
		while (!stop) {
			synchronized (pool) {
				while (pool.isEmpty()) {
					try {
						pool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (stop) {
						return;
					}
				}

				/* get a incoming request */
				request = pool.remove(0);
			}

			/* handle request now */
			this.handleConnection();
		}
	}

	/**
	 * Stop all <code>AbstractIndexEventHandler</code>s.
	 */
	public static void stopAllHandlers() {
		synchronized (pool) {
			pool.clear();
			pool.notifyAll();
		}
	}

	/**
	 * Set stop signal and terminate all handlers.
	 */
	public void stop() {
		this.stop = true;
	}

}