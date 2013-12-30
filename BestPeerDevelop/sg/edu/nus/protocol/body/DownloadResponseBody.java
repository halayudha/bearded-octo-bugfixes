package sg.edu.nus.protocol.body;

/**
 * After performing the download, whether completing or
 * failing, this message will be sent back to the requestor
 * @author chris
 *
 */
public class DownloadResponseBody extends Body {

	private static final long serialVersionUID = 5293692746181126937L;

	private String sender_ip; // ip address of the sender
	private String sender_port; // port of the sender
	private String request; // absolute path of the downloaded file
	private String error_msg; // if fail to perform the request, fill in the
	// error
	// information
	private long download_id; // id of this downloading

	// private boolean isComplete; //whether the request has been completed

	/**
	 * constructor
	 * @param ip
	 * @param port
	 * @param request
	 */
	public DownloadResponseBody(long id, String ip, String port, String request) {
		this.download_id = id;
		sender_ip = ip;
		sender_port = port;
		this.request = request;
		error_msg = null;
	}

	/**
	 * fill in the error message
	 * @param msg
	 */
	public void setErrorMsg(String msg) {
		error_msg = msg;
	}

	/**
	 * 
	 * @return the sender's ip
	 */
	public String getIP() {
		return sender_ip;
	}

	/**
	 * 
	 * @return the sender's port
	 */
	public String getPort() {
		return sender_port;
	}

	/**
	 * 
	 * @return the request (downloaded file)
	 */
	public String getFile() {
		return this.request;
	}

	/**
	 * 
	 * @return id of this downloading
	 */
	public long getID() {
		return this.download_id;
	}

	/**
	 * 
	 * @return the error message
	 */
	public String getErrorMsg() {
		return this.error_msg;
	}

	public String toString() {
		String delim = ":";
		String result = "DownloadResponseBody format:= ip:port:id:file:error_message\r\n";
		result = result + sender_ip + delim + sender_port + delim + download_id
				+ delim + request + delim + error_msg;
		return result;
	}
}
