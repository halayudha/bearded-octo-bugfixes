package sg.edu.nus.accesscontrol;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Vector;

/**
 * Representation class for a list of  role-permissions in access control
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class RolePermissions implements Serializable {

	private static final long serialVersionUID = 5289721305619019276L;

	Vector<RolePermission> role_permissions = new Vector<RolePermission>();

	public RolePermissions() {

	}

	public void addRolePermission(String role_name, String privilege_id,
			String privilege_object, String permission_type) {
		RolePermission rolePerm = new RolePermission(role_name, privilege_id,
				privilege_object, permission_type);
		role_permissions.add(rolePerm);
	}

	public RolePermission getRolePermission(int index) {
		return role_permissions.get(index);
	}

	public int getSize() {
		return role_permissions.size();
	}

	public void readFromDB(Connection conn) {

	}

	public void storeToDB(Connection conn) {

	}

	public void print() {
		System.out.println("Role-permissions data:");
		for (int i = 0; i < role_permissions.size(); i++) {
			role_permissions.get(i).print();
		}
	}

	/**
	 * just for test
	 */
	public static void main(String[] args) {

	}

}
