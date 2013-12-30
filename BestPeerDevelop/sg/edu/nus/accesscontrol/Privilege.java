package sg.edu.nus.accesscontrol;

import java.io.Serializable;

public class Privilege implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 7438813734520327327L;

	String privilege_id;
	String privilege_name;
	String privilege_desc;

	public Privilege(String pri_id, String pri_name, String pri_desc) {
		privilege_id = pri_id;
		privilege_name = pri_name;
		privilege_desc = pri_desc;
	}

	public void print() {
		System.out.println(privilege_id + "\t" + privilege_name + "\t"
				+ privilege_desc);
	}
}
