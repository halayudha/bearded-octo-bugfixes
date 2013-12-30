/*
 * @(#) Bootstrap.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.BootstrapEventManager;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.peer.request.BootstrapPnPServer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ForceOutBody;
import sg.edu.nus.protocol.body.SchemaUpdateBody;

/**
 * Implement a bootstrap.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class Bootstrap extends AbstractPeer {

	/**
	 * Define an obscure port to test whether an instance has existed.
	 */
	public static final int RUN_PORT = 60000;

	// chensu
	/**
	 * Init all static methods or variables for the class object
	 */
	static {
		DBProperty.setConfigFile("./conf/bootstrap.ini");
		Bootstrap.load();
		if (!isSingleton()) {
			LogManager.LogException("Bootstrap server is already running...",
					new RuntimeException());
			System.exit(1);
		}
	}

	public void logout(boolean toBoot, boolean toServer, boolean toClient) {
		this.troubleshoot(toBoot, toServer, toClient);
		this.stopServer();
	}

	/**
	 * Start the bootstrap service.
	 * 
	 * @return if success, return <code>true</code>; otherwise, return
	 *         <code>false</code>
	 */
	public boolean startServer() {
		if (startEventManager(Bootstrap.LOCAL_SERVER_PORT, Bootstrap.CAPACITY)
				&& startUDPServer(Bootstrap.LOCAL_SERVER_PORT,
						Bootstrap.CAPACITY, BootstrapGUI.NORM_FREQ)) {
			return true;
		}
		return false;
	}

	/**
	 * Stop the bootstrap server.
	 * 
	 * @return if success, return <code>true</code>; otherwise, return
	 *         <code>false</code>
	 */
	public boolean stopServer() {
		if (stopEventManager() && stopUDPServer()) {
			return true;
		}
		return false;
	}

	// end chensu

	// private member
	private static final String CONFIG_FILE = "./conf/bootstrap.ini";

	/**
	 * Load system-defined value.
	 */
	public static void load() {
		try {
			FileInputStream in = new FileInputStream(CONFIG_FILE);
			Properties keys = null;
			if (in != null) {
				keys = new Properties();
				keys.load(in);

				String value = keys.getProperty("LOCALSERVERPORT");
				if (value != null && !value.equals(""))
					LOCAL_SERVER_PORT = Integer.parseInt(value);

				value = keys.getProperty("CAPACITY");
				if (value != null && !value.equals(""))
					CAPACITY = Integer.parseInt(value);

				in.close();
			}
		} catch (IOException e) {
			LogManager.LogException("Can't load config file", e);
			System.exit(1);
		}
	}

	/**
	 * Write user-defined values to file. Notice that this function must be
	 * called after user applies the change.
	 */
	public static void write() {
		try {
			FileOutputStream out = new FileOutputStream(CONFIG_FILE);
			Properties keys = new Properties();

			keys.put("LOCALSERVERPORT", Integer.toString(LOCAL_SERVER_PORT));
			keys.put("CAPACITY", Integer.toString(CAPACITY));
			keys.store(out, "Bootstrap Server Configuration");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bootstrap(AbstractMainFrame gui, String peerType) {
		super(gui, peerType);
		// initialize online peer manager now
		this.peerMaintainer = PeerMaintainer.getInstance();
		LogManager.setLogger(this);
	}

	/**
	 * Use an obscure port to limit the bootstrap server to one instance.
	 * 
	 * @return if the bootstrap server does not exist, return <code>true</code>;
	 *         otherwise, return <code>false</code>.
	 */
	private static boolean isSingleton() {
		try {
			SERVER_SOCKET = new ServerSocket(RUN_PORT);
		} catch (BindException e) {
			LogManager.LogException(LanguageLoader.getProperty("system.msg1"),
					e);
			return false;
		} catch (IOException e) {
			LogManager.LogException(LanguageLoader.getProperty("system.msg2"),
					e);
			return false;
		}
		return true;
	}

	// ----------- for tcp service -----------------

	@Override
	public boolean startEventManager(int port, int capacity) {
		if (!isEventManagerAlive()) {
			try {
				eventManager = new BootstrapEventManager(gui, port, capacity);
				new Thread(eventManager, "TCP Server").start();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	// ------------ for udp service -------------

	@Override
	public boolean startUDPServer(int port, int capacity, long period) {
		if (!this.isUDPServerAlive()) {
			try {
				udpServer = new BootstrapPnPServer(gui, port, capacity, period);
				new Thread(udpServer, "UDP Server").start();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public void troubleshoot(boolean toBoot, boolean toServer, boolean toClient) {
		if (toServer && this.peerMaintainer.hasServer()) {
			PeerInfo info = null;
			PeerInfo[] peerList = null;

			// init datagram packets
			PeerType type = PeerType.BOOTSTRAP;
			String pid = type.getValue();
			DatagramPacket trouble = this.troubleshoot(type, pid);
			DatagramSocket socket = this.udpServer.getDatagramSocket();
			try {
				peerList = this.peerMaintainer.getServers();
				int size = peerList.length;
				for (int i = 0; i < size; i++) {
					info = peerList[i];
					trouble.setAddress(InetAddress.getByName(info
							.getInetAddress()));
					trouble.setPort(info.getPort());
					for (int j = 0; j < TRY_TIME; j++) {
						socket.send(trouble);

						if (debug)
							System.out
									.println("case 7: send troubleshoot out to "
											+ trouble.getAddress()
													.getHostAddress()
											+ " : "
											+ trouble.getPort());
					}
				}
			} catch (UnknownHostException e) { /* ignore it */
			} catch (SocketException e) { /* ignore it */
			} catch (IOException e) { /* ignore it */
			}
		}
	}

	@Override
	public void forceOut(String ip, int port) {
		try {
			Head head = new Head(MsgType.FORCE_OUT.getValue());
			Message message = new Message(head, new ForceOutBody());
			sendMessage(new PhysicalInfo(ip, port), message);
		} catch (Exception e) {
			LogManager
					.LogException("Fail to open connection to super peer.", e);
		}
	}

	/**
	 * The method reads the schema from the 'BestPeer' database and sends it to all the super-peers that the
	 * bootstrap node knows of.
	 * 
	 * @return the number of servers that failed to receive the schema (0 means everything is ok)
	 */
	// VHTam
	public int sendSchema(String schema) {

		PeerMaintainer maintainer = PeerMaintainer.getInstance();

		PeerInfo[] servers = maintainer.getServers();
		int result = 0;

		for (int i = 0; i < servers.length; i++) {
			try {
				// send a message to a peer
				Head head = new Head(MsgType.SCHEMA_UPDATE.getValue());
				Message message = new Message(head,
						new SchemaUpdateBody(schema));
				sendMessage(new PhysicalInfo(servers[i].getInetAddress(),
						servers[i].getPort()), message);
			} catch (Exception e) {
				LogManager.LogException(
						"Fail to open connection to super peer", e);
				result++;
			}

		}

		return result;
	}
}