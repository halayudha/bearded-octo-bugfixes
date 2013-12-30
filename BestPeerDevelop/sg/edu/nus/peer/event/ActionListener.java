/*
 * @(#) ActionListener.java	1.0 2006-1-24
 *
 * Copyright 2006, National University of Singapore. 
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;
import java.util.EventListener;

import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;

/**
 * The listener interface for receiving networking events. 
 * The class that is interested in processing a networking event
 * implements this interface, and the object created with that
 * class is registered by using the object's 
 * <code>addActionListener</code> method. When the networking event occurs, 
 * that object's <code>actionPerformed</code> method is invoked.
 *
 * @author Xu Linhao
 * @version 1.0 2006-1-24
 */
public interface ActionListener extends EventListener {

	/**
	 * Invoked when a networking event occurs.
	 * <p>
	 * Note: the consumed state should be reset before
	 * return a response message.
	 * 
	 * @param oos the output stream used for replying a message
	 * 				to the request peer.
	 * @param msg the message to be processed
	 * @throws IOException 
	 */
	void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException;

	/**
	 * Determine if the listener can consume the network event.
	 * 
	 * @param msg the network event
	 * @return if the listener can consume the event, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	boolean isConsumed(Message msg) throws EventHandleException;

}
