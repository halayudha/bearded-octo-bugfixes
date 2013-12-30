package sg.edu.nus.protocol.body;

import sg.edu.nus.accesscontrol.RoleManagement;

public class RolemanBody extends Body {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7359608217886207039L;

	RoleManagement roleMan = null;

	public RolemanBody(RoleManagement roleManagement) {
		roleMan = roleManagement;
	}

	public RoleManagement getRoleman() {
		return roleMan;
	}
}
