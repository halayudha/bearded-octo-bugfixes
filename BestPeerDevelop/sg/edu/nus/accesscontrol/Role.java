package sg.edu.nus.accesscontrol;

import java.io.Serializable;

/**
 * Representation class for a role in access control
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class Role implements Serializable {

	private static final long serialVersionUID = 90377564625939982L;

	String role_name;

	String role_desc;

	Role(String role_name, String role_desc) {
		this.role_name = role_name;
		this.role_desc = role_desc;
	}

	public void print() {

		System.out.println(role_name + "\t" + role_desc);

	}

}
