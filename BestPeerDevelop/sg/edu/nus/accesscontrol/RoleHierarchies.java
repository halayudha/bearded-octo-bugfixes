package sg.edu.nus.accesscontrol;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Vector;

public class RoleHierarchies implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2971429393699750583L;

	Vector<RoleHierarchy> roleHierchies = new Vector<RoleHierarchy>();

	public RoleHierarchies() {

	}

	public void addRoleHierarchy(String superRole, String subRole) {
		roleHierchies.add(new RoleHierarchy(superRole, subRole));
	}

	public void readFromDB(Connection conn) {

	}

	public void storeToDB(Connection conn) {

	}

}
