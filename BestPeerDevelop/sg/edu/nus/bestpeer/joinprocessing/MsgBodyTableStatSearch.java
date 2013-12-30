package sg.edu.nus.bestpeer.joinprocessing;

import java.io.ObjectOutputStream;

import sg.edu.nus.protocol.body.Body;

/**
 * 
 * @author VHTam
 *
 */

public class MsgBodyTableStatSearch extends Body {

	private static final long serialVersionUID = 5395072298778801282L;

	private String tableName = null;
	private String ip = null;
	int port = 0;

	/**
	 * FIXME need to remove this
	 * @author chensu 
	 */
	public ObjectOutputStream oos = null;

	public void setOutputStream(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public MsgBodyTableStatSearch(String tableName, String ip, int port) {
		this.tableName = tableName;
		this.ip = ip;
		this.port = port;
	}

	public String getTableName() {
		return tableName;
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

	public String toString() {

		String result = "MsgBodyTableStatSearch format:= tableName\r\n";
		result += tableName;

		return result;
	}
}
