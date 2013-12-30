package sg.edu.nus.accesscontrol.bootstrap;

import sg.edu.nus.accesscontrol.RolePermissions;
import sg.edu.nus.accesscontrol.Roles;
import sg.edu.nus.protocol.body.Body;

/**
 * Body of message will be returned by bootstrap peer 
 * in respond to normal peer's roles-configuration update request.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class MsgBodyRoleReturn extends Body {

	private static final long serialVersionUID = 4367705004836991678L;

	private String result;
	private Roles roles;
	private RolePermissions role_permissions;

	public MsgBodyRoleReturn(String result, Roles roles,
			RolePermissions role_permissions) {
		this.result = result;
		this.roles = roles;
		this.role_permissions = role_permissions;
	}

	public Roles getRoles() {
		return roles;
	}

	public RolePermissions getRolePermissions() {
		return role_permissions;
	}

	/**
	 * Get the result.
	 * 
	 * @return The result.
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Override <code>toString()</code> function of <code>java.lang.Object</code>.
	 * 
	 * @return A string that describes the content of the body.
	 */
	public String toString() {

		String s = "MsgBodyRoleReturn format:= result_string\r\n";
		s += result;

		return s;
	}

}
