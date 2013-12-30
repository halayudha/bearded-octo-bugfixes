package sg.edu.nus.accesscontrol;

import java.io.Serializable;

public class RoleHierarchy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9002002931883407154L;

	String super_role_name;
	String sub_role_name;

	public RoleHierarchy(String superRole, String subRole) {
		super_role_name = superRole;
		sub_role_name = subRole;
	}

}
