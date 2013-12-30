/*
 * @(#) AbstractEventDispatcher.java 1.0 2005-12-30
 * 
 * Copyright 2005, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.peer.management;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sg.edu.nus.bestpeer.joinprocessing.MsgBodyTableStatSearch;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.event.ActionListener;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;

/**
 * This class extends <code>AbstractPooledSocketHandler</code> and is
 * responsible for handling the incoming socket connection.
 * <p>
 * The incoming network event will be dispatched to the corresponding event
 * processor and the response will be returned by the event processor.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 */

public abstract class AbstractEventDispatcher extends EventDispatcherAdapter {

	// protected members
	protected AbstractMainFrame gui;

	/**
	 * A container for keeping all registered network event listeners. Each
	 * network event listener is responsible for processing a type of networking
	 * message.
	 */
	protected List<ActionListener> listeners;

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
	public AbstractEventDispatcher(AbstractMainFrame gui) {
		this.gui = gui;
		mutex = new ReentrantLock();
		listeners = Collections.checkedList(new Vector<ActionListener>(),
				ActionListener.class);
	}

	/**
	 * Process the incoming socket connection.
	 */
	public void handleConnection() {
		try {
			// read stream and process request
			ObjectInputStream ois = new ObjectInputStream(connection
					.getInputStream());
			Message message = Message.deserialize(ois);

			// determine whether the message type is valid first
			if (MsgType.checkValue(message.getHead().getMsgType())) {
				/**
				 * FIXME need to remove this after changing select executor
				 * @author chensu
				 */
				if (message.getHead().getMsgType() == MsgType.TABLE_STAT_SEARCH
						.getValue())
					((MsgBodyTableStatSearch) message.getBody())
							.setOutputStream(new ObjectOutputStream(connection
									.getOutputStream()));

				processEvent(message);
			} else
				LogManager.LogException("Unknown incoming message type.", null);

			ois.close();
		} catch (Exception e) {
			LogManager
					.LogException(
							"Exception ocurrs in handleConnection of AbstractEventDispatcher",
							e);
		}
	}

	/**
	 * Pass the network event to each registered listener, which will process
	 * the obtained event and then return the confirm message, if necessary.
	 * <p>
	 * The response message should be processed in the listener's
	 * <code>actionPerformed</code> method.
	 * <p>
	 * Note: the current design is not good enough, but I have no any good idea.
	 * 
	 * @param msg
	 *            the network event
	 */
	protected void processEvent(Message msg) {
		try {
			PhysicalInfo dest = new PhysicalInfo(msg.getHead().getPid(), ":");

			// this clone operation here is VERY IMPORTANT, after clone, the
			// current listeners will be not affected by the new joined ones

			Object[] list;
			synchronized (this) {
				list = listeners.toArray().clone();
			}

			/* make sure concurrent processing */
			mutex.lock();

			/* process network events */
			ActionListener listener;
			for (int i = 0; i < list.length; i++) {
				listener = (ActionListener) list[i];

				if (listener.isConsumed(msg)) {
					LogManager.LogPreEvent("Triggered by incoming message",
							MsgType.description(msg.getHead().getMsgType()),
							dest.getIP() + ":" + dest.getPort());

					// perform action here
					listener.actionPerformed(dest, (Message) msg);

					LogManager.LogPostEvent("Triggered by incoming message",
							MsgType.description(msg.getHead().getMsgType()),
							dest.getIP() + ":" + dest.getPort());
				}
			}
		} // release lock in finally clause
		catch (Exception e) {
			mutex.unlock();
			LogManager
					.LogException(
							"Exception ocurrs in processEvent of AbstractEventDispatcher",
							e);
		} finally {
			// unlock all listeners
			mutex.unlock();
		}
	}

	/**
	 * Register a set of <code>ActionListener</code>s to the
	 * <code>PooledSocketHandler</code> for the purpose of processing any
	 * incoming network events.
	 * 
	 * 
	 */
	public abstract void registerActionListeners();

	/**
	 * Add an instance of the <code>ActionListener</code> into the
	 * <code>EventDispatcher</code>.
	 * 
	 * @param l
	 *            an instance of the <code>ActionListener</code>
	 */
	public synchronized void addActionListener(ActionListener l) {
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
	public synchronized void removeActionListener(ActionListener l) {
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