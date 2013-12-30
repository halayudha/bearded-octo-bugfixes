package sg.edu.nus.accesscontrol;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Vector;

/**
 * Representation class for a list of roles in access control
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
*/

public class Roles implements Serializable {

	private static final long serialVersionUID = 8048256926947205900L;

	Vector<Role> roles = new Vector<Role>();

	public Roles() {

	}

	public void addRole(String role_name, String role_desc) {
		roles.add(new Role(role_name, role_desc));
	}

	public Role getRole(int index) {
		return roles.get(index);
	}

	public int getSize() {
		return roles.size();
	}

	public void readFromDB(Connection conn) {

	}

	public void storeToDB(Connection conn) {

	}

	public void print() {
		System.out.println("-------Roles data-------");
		for (int i = 0; i < roles.size(); i++) {
			roles.get(i).print();
		}
	}
}
