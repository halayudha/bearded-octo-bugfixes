package sg.edu.nus.accesscontrol;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Vector;

public class Privileges implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2882051260283575125L;

	Vector<Privilege> privileges = new Vector<Privilege>();

	public Privileges() {

	}

	public void addPrivilege(String priId, String priName, String priDesc) {
		privileges.add(new Privilege(priId, priName, priDesc));
	}

	public void storeToDB(Connection conn) {

	}

	public void print() {

	}
}
