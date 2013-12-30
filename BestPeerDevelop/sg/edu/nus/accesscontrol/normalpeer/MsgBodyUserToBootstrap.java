package sg.edu.nus.accesscontrol.normalpeer;

import sg.edu.nus.protocol.body.Body;

public class MsgBodyUserToBootstrap extends Body {

	private static final long serialVersionUID = -6869462918641863826L;

	private String userName;
	private String userDesc;
	private String pwd;
	private String sender;

	public MsgBodyUserToBootstrap(String name, String desc, String passwd,
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

		String s = "MsgBodyUserToBoostrap format:= user:desc:pwd:serverip\r\n";

		return s;
	}
}
