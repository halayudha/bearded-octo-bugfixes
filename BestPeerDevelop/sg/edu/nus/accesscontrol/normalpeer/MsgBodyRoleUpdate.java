package sg.edu.nus.accesscontrol.normalpeer;

import sg.edu.nus.protocol.body.Body;

/**
 * Body of message sent by normal peer to bootstrap peer 
 * for roles-configuration update request.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class MsgBodyRoleUpdate extends Body {

	private static final long serialVersionUID = -3080032047518705907L;

	private String userID; // the user identifier
	private String password; // the password
	private String ip; // the IP address
	private int port; // the port of the server socket
	private String type; // the peer type: superpeer or peer

	public MsgBodyRoleUpdate(String userID, String password, String ip,
			int port, String type) {
		this.userID = userID;
		this.password = password;
		this.ip = ip;
		this.port = port;
		this.type = type;
	}

	/**
	 * Get the user identifier.
	 * 
	 * @return The user identifier.
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Get the password.
	 * 
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get the request peer's IP address,
	 * for another peer to reply message.
	 * 
	 * @return the request peer's IP address
	 */
	public String getIP() {
		return ip;
	}

	/**
	 * Get the port of the server socket that 
	 * is used for providing service of monitoring
	 * incoming and outgoing socket connections.
	 * 
	 * @return the port of the server socket
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Get the peer type.
	 * 
	 * @return the peer type
	 */
	public String getPeerType() {
		return this.type;
	}

	/**
	 * Override <code>toString()</code> function of <code>java.lang.Object</code>.
	 * 
	 * @return A string that describes the content of the body.
	 */
	public String toString() {
		String delim = ":";
		return userID + delim + password + delim + ip + delim + port + delim
				+ type;
	}
}
