package sg.edu.nus.accesscontrol.bootstrap;

import sg.edu.nus.protocol.body.Body;

/**
 * Body of message sent from bootstrap peer 
 * to normal peer. This message contain info of
 * new user account.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class MsgBodyUserToPeer extends Body {

	private static final long serialVersionUID = -5507522753176432451L;

	private String userName;
	private String userDesc;
	private String pwd;
	private String sender;

	public MsgBodyUserToPeer(String name, String desc, String passwd,
			String sender) {
		userName = name;
		userDesc = desc;
		pwd = passwd;
		this.sender = sender;

	}

	public String getUserName() {
		return userName;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public String getPasswd() {
		return pwd;
	}

	public String getSender() {
		return sender;
	}

	/**
	 * Override <code>toString()</code> function of <code>java.lang.Object</code>.
	 * 
	 * @return A string that describes the content of the body.
	 */
	public String toString() {

		String s = "MsgBodyUserToPeer format:= user:desc:pwd\r\n";

		return s;
	}

}
