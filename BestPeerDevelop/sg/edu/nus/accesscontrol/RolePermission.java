package sg.edu.nus.accesscontrol;

import java.io.Serializable;

/**
 * Representation class for a role-permission in access control
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class RolePermission implements Serializable {

	private static final long serialVersionUID = 1720005086135986130L;

	String role_name = null;
	String privilege_id = null;
	String privilege_object = null;
	String permission_type = null;

	RolePermission(String role_name, String privilege_id,
			String privilege_object, String permission_type) {
		this.role_name = role_name;
		this.privilege_id = privilege_id;
		this.privilege_object = privilege_object;
		this.permission_type = permission_type;
	}

	public void print() {
		System.out.println(role_name + "\t" + privilege_id + "\t"
				+ privilege_object + "\t" + permission_type);
	}

}
