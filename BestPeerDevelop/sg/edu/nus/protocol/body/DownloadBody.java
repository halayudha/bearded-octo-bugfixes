package sg.edu.nus.protocol.body;

/**
 * The message body of download request
 * 
 * @author chris (Wu Sai)
 *
 */
public class DownloadBody extends Body {

	private static final long serialVersionUID = 66746576883435672L;

	private String requestor_ip; // ip address of the requestor
	private String requestor_port; // port of the requestor
	private String request; // absolute path of the downloaded file
	private long download_id; // unique id for this download job

	/**
	 * constructor
	 * @param ip   ip address of the sender
	 * @param port port of the sender
	 * @param file requested file
	 */
	public DownloadBody(long id, String ip, String port, String file) {
		super();
		this.download_id = id;
		this.requestor_ip = ip;
		this.requestor_port = port;
		this.request = file;
	}

	/**
	 * 
	 * @return requestor's ip address
	 */
	public String getIP() {
		return this.requestor_ip;
	}

	/**
	 * 
	 * @return requestor's ip port
	 */
	public String getPort() {
		return this.requestor_port;
	}

	/**
	 * 
	 * @return downloaded file path
	 */
	public String getFile() {
		return this.request;
	}

	/**
	 * get id of this downloading
	 * @return
	 */
	public long getID() {
		return this.download_id;
	}

	public String toString() {
		String delim = ":";
		String result = "DownloadBody format:= ip:port:id:file\r\n";
		result = result + requestor_ip + delim + requestor_port + delim
				+ download_id + delim + request;
		return result;
	}
}
