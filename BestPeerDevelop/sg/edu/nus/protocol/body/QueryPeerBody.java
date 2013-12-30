/**
 * Created on Sep 4, 2008
 */
package sg.edu.nus.protocol.body;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * @author David Jiang
 *
 */
public class QueryPeerBody extends Body {

	/**
	 * SerialID for persistence
	 */
	private static final long serialVersionUID = 2110990205852867979L;

	private String sql_cmd;

	private PhysicalInfo sender;

	private String userName; // user who submit query

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;

	}

	/**
	 * @param sql_cmd the sql_cmd to set
	 */
	public void setSqlCommand(String sql_cmd) {
		this.sql_cmd = sql_cmd;
	}

	/**
	 * @return the sql_cmd
	 */
	public String getSqlCommand() {
		return sql_cmd;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(PhysicalInfo sender) {
		this.sender = sender;
	}

	/**
	 * @return the sender
	 */
	public PhysicalInfo getSender() {
		return sender;
	}

}
