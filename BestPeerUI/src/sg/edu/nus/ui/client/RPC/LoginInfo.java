/**
 * Created on Apr 28, 2009
 */
package sg.edu.nus.ui.client.RPC;

import java.io.Serializable;

/**
 * @author David Jiang
 *
 */
@SuppressWarnings("serial")
public class LoginInfo implements Serializable {
	private String username;

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
}
