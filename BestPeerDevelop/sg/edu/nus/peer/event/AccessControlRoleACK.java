package sg.edu.nus.peer.event;

import java.io.IOException;

import javax.swing.JOptionPane;

import sg.edu.nus.accesscontrol.bootstrap.MsgBodyRoleReturn;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;

public class AccessControlRoleACK extends ActionAdapter {

	public AccessControlRoleACK(AbstractMainFrame gui) {
		super(gui);
	}

	@Override
	public void actionPerformed(PhysicalInfo dest, Message message)
			throws EventHandleException, IOException {
		MsgBodyRoleReturn msgBody = (MsgBodyRoleReturn) message.getBody();

		JOptionPane.showMessageDialog(null, "Result from bootstrap: "
				+ msgBody.getResult() + " PLEASE see more info at console!");

		msgBody.getRoles().print();
		msgBody.getRolePermissions().print();
	}

	@Override
	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.ACCESS_CONTROL_ROLE_RESULT
				.getValue())
			return true;

		return false;
	}

}
