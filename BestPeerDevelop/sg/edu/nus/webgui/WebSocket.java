package sg.edu.nus.webgui;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.WebLoginBody;
import sg.edu.nus.protocol.body.WebSearchBody;

/**
 * use as a servlet to forward
 * messages between web interface
 * and bestpeer
 * @author wusai
 *
 */
public class WebSocket {

	// public String configureFile = "port.txt";

	/**
	 * port of the local bestpeer instance
	 */
	public int port;

	/**
	 * constructor
	 * @param port
	 */
	public WebSocket(int port) {
		this.port = port;
	}

	/**
	 * send a sql message to the bestpeer instance from the web 
	 * interface
	 * @param sql
	 */
	public void sendSQLMessage(String sql, long id) {
		WebSearchBody searchBody = new WebSearchBody(sql, id);
		Head head = new Head();
		head.setMsgType(MsgType.WEB_SEARCH.getValue());
		Message message = new Message(head, searchBody);
		try {
			Socket socket = new Socket("127.0.0.1", port);
			ObjectOutputStream oos = new ObjectOutputStream(socket
					.getOutputStream());
			oos.writeObject(message);
			oos.close();
			socket.close();
		} catch (UnknownHostException e) {
			 
			e.printStackTrace();
		} catch (IOException e) {
			 
			e.printStackTrace();
		}

	}

	public void sendLoginMessage() {
		Head head = new Head();
		head.setMsgType(MsgType.WEB_LOGIN.getValue());
		Message message = new Message(head, new WebLoginBody());
		try {
			Socket socket = new Socket("127.0.0.1", port);
			ObjectOutputStream oos = new ObjectOutputStream(socket
					.getOutputStream());
			oos.writeObject(message);
			oos.close();
			socket.close();
		} catch (UnknownHostException e) {
			 
			e.printStackTrace();
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	}
}
