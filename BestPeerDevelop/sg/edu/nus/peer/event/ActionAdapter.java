/*
 * @(#) ActionAdapter.java 1.0 2006-1-25
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.IOException;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;

/**
 * An abstract adapter class for receiving network events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-25
 */

public class ActionAdapter implements ActionListener {

	// protected member
	protected static final boolean debug = true;
	protected AbstractMainFrame gui;

	public ActionAdapter(AbstractMainFrame gui) {
		this.gui = gui;
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException, IOException {
		// do nothing now
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		return false;
	}

}