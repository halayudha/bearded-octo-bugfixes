package sg.edu.nus.accesscontrol.bootstrap;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.event.ActionAdapter;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;

/**
 * Bootstrap peer's listener for Role-Setting update request from normal peer.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class AccessControlRoleUpdateListener extends ActionAdapter {

	public AccessControlRoleUpdateListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ACCESS_CONTROL_ROLE_REQUEST
				.getValue())
			return true;
		return false;
	}
}
