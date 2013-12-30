/*
 * @(#) CAEventDispatcher.java 1.0 2006-2-3
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.management;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.event.AddUser2GroupListener;
import sg.edu.nus.peer.event.CreateGroupListener;
import sg.edu.nus.peer.event.DeleteGroupListener;
import sg.edu.nus.peer.event.GetCertListener;
import sg.edu.nus.peer.event.GetGroupListener;
import sg.edu.nus.peer.event.GetUserListener;
import sg.edu.nus.peer.event.UserVerifyListener;

/**
 * Implement a concrete event manager used for monitoring
 * all incoming socket connections related to the certificate authority.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-3
 */

public class CAEventDispatcher extends AbstractEventDispatcher {

	public CAEventDispatcher(AbstractMainFrame gui) {
		super(gui);
	}

	@Override
	public void registerActionListeners() {
		this.addActionListener(new UserVerifyListener(gui));
		this.addActionListener(new GetCertListener(gui));
		this.addActionListener(new GetUserListener(gui));
		this.addActionListener(new GetGroupListener(gui));
		this.addActionListener(new CreateGroupListener(gui));
		this.addActionListener(new AddUser2GroupListener(gui));
		this.addActionListener(new DeleteGroupListener(gui));
	}

}