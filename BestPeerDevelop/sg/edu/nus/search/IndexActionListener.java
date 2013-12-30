/*
 * @(#) IndexActionListener.java	1.0 2006-7-7
 *
 * Copyright 2006, National University of Singapore. 
 * All rights reserved.
 */

package sg.edu.nus.search;

import java.io.IOException;
import java.util.EventListener;

import sg.edu.nus.indexkeyword.FileHandlerException;

/**
 * The listener interface for receiving index events. 
 * The class that is interested in processing a index event
 * implements this interface, and the object created with that
 * class is registered by using the object's 
 * <code>addActionListener</code> method. When the index event occurs, 
 * that object's <code>actionPerformed</code> method is invoked.
 *
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */
public interface IndexActionListener extends EventListener {

	/**
	 * Invoked when a index event occurs.
	 * 
	 * @param event the message to be processed
	 * @throws IOException, FileHandlerException, IndexOperationException
	 */
	void actionPerformed(IndexEvent event) throws IOException,
			FileHandlerException, IndexOperationException;

	/**
	 * Determine if the listener can consume the index event.
	 * 
	 * @param event the index event
	 * @return if the listener can consume the event, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	boolean isConsumed(IndexEvent event) throws IndexEventHandleException;

}
