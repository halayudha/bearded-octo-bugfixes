package sg.edu.nus.peer.event;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.DownloadBody;
import sg.edu.nus.protocol.body.DownloadResponseBody;

/**
 * a listener to accept the download
 * request and handle the file transfer
 * @author chris
 * @deprecated
 */

public class DownloadListener extends ActionAdapter {

	public DownloadListener(AbstractMainFrame gui) {
		super(gui);
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		Message result = null;
		try {
			DownloadBody down = (DownloadBody) msg.getBody();
			String IP = down.getIP();
			String Port = down.getPort();
			String Path = down.getFile();
			long id = down.getID();

			// test whether the file exists
			File file = new File(Path);
			if (!file.exists()) {
				Head msghead = new Head();
				msghead.setMsgType(MsgType.DOWNLOAD_NACK.getValue());
				DownloadResponseBody msgbody = new DownloadResponseBody(id,
						null, null, Path);
				msgbody.setErrorMsg("file doesnot exist!");
				result = new Message(msghead, msgbody);
				gui.peer().sendMessage(
						new PhysicalInfo(IP, Integer.parseInt(Port)), result);
			} else {
				Head msghead = new Head(MsgType.DOWNLOAD_ACK.getValue());
				DownloadResponseBody msgbody = new DownloadResponseBody(id,
						null, null, Path);
				result = new Message(msghead, msgbody);
				gui.peer().sendMessage(
						new PhysicalInfo(IP, Integer.parseInt(Port)), result);

				Socket socket = new Socket(IP, Integer.parseInt(Port));
				ObjectOutputStream newoos = new ObjectOutputStream(socket
						.getOutputStream());
				NetworkTransfer transfer = new NetworkTransfer(socket, newoos,
						IP, Port, file);
				new Thread(transfer).start();
			}
		} catch (Exception e) {

		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.DOWNLOAD.getValue())
			return true;
		return false;
	}

	/**
	 * an inner class to create a new thread
	 * for transferring the requested file
	 */
	public class NetworkTransfer implements Runnable {
		// private String ip;
		// private String port;
		private File file;
		private ObjectOutputStream newoos;
		private Socket socket;

		/**
		 * constructor
		 * @param ip
		 * @param port
		 * @param file
		 */
		public NetworkTransfer(Socket socket, ObjectOutputStream oos,
				String ip, String port, File file) {
			this.socket = socket;
			this.newoos = oos;
			// this.ip = ip;
			// this.port = port;
			this.file = file;
		}

		/**
		 * sending the corresponding file
		 */
		public void run() {
			try {

				FileInputStream input = new FileInputStream(file);
				OutputStream output = (OutputStream) newoos;
				OutputStream dataOutput = new DataOutputStream(
						new BufferedOutputStream(output));
				int bufsize = 1024 * 4;
				byte[] buffer = new byte[bufsize];
				int size = 0;
				while ((size = input.read(buffer)) != -1) {
					dataOutput.write(buffer, 0, size);
					dataOutput.flush();
				}
				input.close();
				dataOutput.close();
				newoos.close();
				// acknowledge.close();
				socket.close();

			} catch (Exception e) {
				// do nothing
				System.out.println("sender error");
				e.printStackTrace();
			}
		}
	}
}
