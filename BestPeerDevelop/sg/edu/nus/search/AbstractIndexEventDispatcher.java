/*
 * @(#) AbstractIndexEventDispatcher.java 1.0 2006-7-7
 * 
 * Copyright 2006, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.search;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.indexkeyword.FileHandlerException;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.management.EventDispatcher;

/**
 * This class extends <code>AbstractIndexEventHandler</code> and is responsible
 * for handling the incoming <code>IndexEvent</code>s.
 * <p>
 * The incoming event will be dispatched to the corresponding event processor
 * and the response will be returned by the event processor.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public abstract class AbstractIndexEventDispatcher extends
		AbstractIndexEventHandler implements EventDispatcher {

	// protected members
	protected AbstractMainFrame gui;

	/**
	 * A container for keeping all registered index event listeners. Each index
	 * event listener is responsible for processing a type of
	 * <code>IndexEvent</code>.
	 */
	protected List<IndexActionListener> listeners;

	/**
	 * A mutex lock used for making sure all events be processed in a concurrent
	 * manner.
	 */
	private Lock mutex;

	/**
	 * Construct an <code>AbstractEventDispatcher</code>.
	 * 
	 * @param gui
	 *            the main frame that contains the event manager
	 */
	public AbstractIndexEventDispatcher(AbstractMainFrame gui) {
		this.gui = gui;
		mutex = new ReentrantLock();
		listeners = Collections.checkedList(new Vector<IndexActionListener>(),
				IndexActionListener.class);
	}

	/**
	 * Process the incoming <code>IndexEvent</code>.
	 */
	public void handleConnection() {
		try {
			if (IndexEventType.checkValue(request.getHead().getValue())) {

				LogManager.LogPreEvent("IndexEvent", IndexEventType
						.description(request.getHead()), null);

				processEvent(request);

				LogManager.LogPostEvent("IndexEvent", IndexEventType
						.description(request.getHead()), null);
			}
		} catch (Exception e) {
			LogManager
					.LogException(
							"Exception ocurrs in handleConnection of AbstractIndexEventDispatcher",
							e);
		}
	}

	/**
	 * Pass the index event to each registered listener, which will process the
	 * obtained event and then return the confirm message, if necessary.
	 * <p>
	 * The response message should be processed in the listener's
	 * <code>actionPerformed</code> method.
	 * 
	 * @param indexEvent
	 *            the <code>IndexEvent</code>
	 */
	protected void processEvent(IndexEvent indexEvent) throws IOException,
			FileHandlerException, IndexOperationException,
			IndexEventHandleException {
		try {
			/*
			 * this clone operation here is VERY IMPORTANT, after clone, the
			 * current listeners will be not affected by the new joined ones
			 */
			Object[] list;
			synchronized (this) {
				list = listeners.toArray().clone();
			}

			/* make sure concurrent processing */
			mutex.lock();

			/* process network events */
			IndexActionListener listener;
			for (int i = 0; i < list.length; i++) {
				listener = (IndexActionListener) list[i];
				if (listener.isConsumed(indexEvent)) {
					// perform action here
					listener.actionPerformed(indexEvent);
				}
			}
		}
		/* unlock all listeners */
		finally {
			mutex.unlock();
		}
	}

	/**
	 * Register a set of <code>ActionListener</code>s to the
	 * <code>PooledSocketHandler</code> for the purpose of processing any
	 * incoming network events.
	 */
	public abstract void registerActionListeners();

	/**
	 * Add an instance of the <code>ActionListener</code> into the
	 * <code>EventDispatcher</code>.
	 * 
	 * @param l
	 *            an instance of the <code>ActionListener</code>
	 */
	public synchronized void addActionListener(IndexActionListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}

	/**
	 * Remove an instance of the <code>ActionListener</code> from the
	 * <code>EventDispatcher</code>.
	 * 
	 * @param l
	 *            an instance of the <code>ActionListener</code>
	 */
	public synchronized void removeActionListener(IndexActionListener l) {
		if (!listeners.isEmpty())
			listeners.remove(l);
	}

	/**
	 * Remove all <code>ActionListener</code>.
	 */
	public synchronized void removeAll() {
		listeners.clear();
	}

}