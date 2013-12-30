/*
 * @(#) AbstractPeer.java 1.0 2006-1-27
 * 
 * Copyright 2006, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.peer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.sql.Connection;
import java.util.Properties;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.AbstractEventManager;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.peer.request.UDPServer;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.util.EncryptionUtils;
import sg.edu.nus.util.Inet;
import sg.edu.nus.util.Tools;

/**
 * An abstract class that defines all necessary interfaces for communicating
 * between peers.
 * <p>
 * The philosophy is twofolds: (1) wrap all necessary functionalities that has
 * no relationship with GUI in a separated class; (2) provide the interfaces to
 * construct a peer in a simulation environment.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-27
 */

public abstract class AbstractPeer {
	// chensu
	/**
	 * The idea is to have an instance of ServerSocket listening over some port 
	 * for as long as the first running instance of our app runs. 
	 * Consequent launches of the same application will try to set up 
	 * their own ServerSocket over the same port, and will get a java.net.BindException
	 * (because we cannot start two versions of a server on the same port), 
	 * and we'll know that another instance of the app is already running. 
	 * <p>
	 * Now, if, for any unintentional reason, our app dies, the resources used by
	 * the app die away with it, and another instance can start and set the ServerSocket
	 * to prevent more instances from running
	 */
	protected static ServerSocket SERVER_SOCKET;

	public static DB bestpeer_db;

	public static Connection conn_bestpeerdb;

	// public members
	/**
	 * The local port used for monitoring network events, i.e., tcp and udp
	 * services will be initialized on it at the same time.
	 */
	public static int LOCAL_SERVER_PORT;

	/**
	 * The maximal number of threads used for handling network events
	 * simultaneously.
	 */
	public static int CAPACITY;

	/**
	 * The main frame that contains the peer.
	 */
	protected AbstractMainFrame gui;

	/**
	 * The type of a peer.
	 * 
	 * @see PeerType
	 */
	protected String peerType;

	/**
	 * The network event manager who is responsible for monitoring and
	 * processing all incoming network events, while at the same time, replying
	 * feedback if necessary.
	 */
	protected AbstractEventManager eventManager;

	/**
	 * The UDP server is responsible for monitoring and processing all incoming
	 * datagram packets and replying udp packets if necessary.
	 */
	protected UDPServer udpServer;

	/**
	 * Maintain the online peers who has already registered to the system.
	 */
	protected PeerMaintainer peerMaintainer;

	protected Connection dbconn = null;

	/**
	 * How many times a troubleshoot packet is sent out.
	 */
	protected final static boolean debug = true;
	protected final static int TRY_TIME = 2;

	/**
	 * File contain key for message encryption
	 */
	private static final String KEY_FILE = "./conf/key.ini";
	private static Key ENCRYPTION_KEY = null;
	
	/**
	 * Load secret key for message encryption
	 */
	private void loadEncryptionKey(){
		String keyString = "236-117-181-248-76-176-76-84";
		try
    	{
    		FileInputStream in = new FileInputStream(KEY_FILE);
    		Properties keys = null;
    		if (in != null)
    		{
    			keys = new Properties();
    			keys.load(in);
    			
    			String value = keys.getProperty("ENCRYPTIONKEY");
       			if (value != null && !value.equals(""))
       				keyString = value;
       			
       			in.close();
    		}
    	}
    	catch (IOException e)
    	{
    		keyString = "236-117-181-248-76-176-76-84";
    	}
    	
		ENCRYPTION_KEY = EncryptionUtils.getKey(keyString);
	}
	
	/**
	 * Load secret key for message encryption
	 */
	public static Key getEncryptionKey(){
		return ENCRYPTION_KEY;
	}

	
	/**
	 * Construct an empty peer with <code>PeerType</code> only.
	 * 
	 * @param peerType
	 *            the type of the peer
	 */
	public AbstractPeer(AbstractMainFrame gui, String peerType) {
		this.gui = gui;
		this.peerType = peerType;
		this.eventManager = null;
		
		//load secret key for message encryption
		loadEncryptionKey();
		
		initDB();
	}

	/**
	 * Get the handler of the <code>AbstractMainFrame</code>.
	 * 
	 * @return the handler of the <code>AbstractMainFrame</code>
	 */
	public AbstractMainFrame getMainFrame() {
		return this.gui;
	}

	/**
	 * Get the peer type.
	 * 
	 * @return the type of the peer
	 */
	public String getPeerType() {
		return peerType;
	}

	// ------------- for tcp service ----------------

	/**
	 * Determine whether the instance of <code>EventManager</code> is null.
	 * 
	 * @return if null, return <code>false</code>; otherwise, return
	 *         <code>true</code>
	 */
	public boolean isEventManagerAlive() {
		if (eventManager == null)
			return false;
		return eventManager.isAlive();
	}

	/**
	 * Start the network monitoring service.
	 * 
	 * @param port
	 *            the port used for monitoring network events
	 * @param capacity
	 *            the maximum capacity that serves the incoming socket
	 *            connections at the same time
	 * @return if success, return <code>true</code>; otherwise, return
	 *         <code>false</code>
	 */
	public abstract boolean startEventManager(int port, int capacity);

	/**
	 * Stop the network monitoring server.
	 * 
	 * @return if success, return <code>true</code>; otherwise, return
	 *         <code>false</code>
	 */
	public boolean stopEventManager() {
		if (isEventManagerAlive()) {
			try {
				eventManager.stop();
				eventManager = null;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	// --------------- for udp service -----------------

	/**
	 * Returns <code>true</code> if the server is alive.
	 * 
	 * @return <code>true</code> if alive; otherwise, return <code>false</code>
	 */
	public boolean isUDPServerAlive() {
		if (udpServer == null)
			return false;
		return udpServer.isAlive();
	}

	/**
	 * Starts the UDP service by specify the port and the number of threads that
	 * can handle incoming and outgoing UDP packets at the same time.
	 * 
	 * @param port
	 *            the port to be used for starting the UDP service
	 * @param capacity
	 *            the number of threads that can handle incoming and outgoing
	 *            UDP packets at the same time
	 * @return <code>true</code> if the UDP service is started; otherwise,
	 *         return <code>false</code>
	 */
	public abstract boolean startUDPServer(int port, int capacity, long period);

	/**
	 * Re-schedule the UDP Sender with a new time interval.
	 * 
	 * @param period
	 *            the new period for disseminating UDP packets
	 */
	public void scheduleUDPSender(long period) {
		this.udpServer.scheduleUDPSender(period);
	}

	/**
	 * Stops the UDP service.
	 * 
	 * @return <code>true</code> if the UDP service is stoped; otherwise, return
	 *         <code>false</code>
	 */
	public boolean stopUDPServer() {
		if (this.isUDPServerAlive()) {
			try {
				udpServer.stop();
				udpServer = null;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Force a peer out the network with its IP address and port.
	 * 
	 * @param ip
	 *            the IP address of the peer to be forced out the network
	 * @param port
	 *            the port where the network service is running
	 */
	public abstract void forceOut(String ip, int port);

	/**
	 * When the peer exits the system for some reason, it will broadcast a
	 * TROUBLESHOOT message to all relevant peers for its leave.
	 * 
	 * @param toBoot
	 *            if <code>true</code>, send messages to bootstrapper
	 * @param toServer
	 *            if <code>true</code>, send messages to server peers
	 * @param toClient
	 *            if <code>true</code>, send messages to client peers
	 */
	public abstract void troubleshoot(boolean toBoot, boolean toServer,
			boolean toClient);

	/**
	 * Returns a troubleshoot packet to indicate the peer who sends the packet
	 * will leave the system due to hardware or system errors.
	 * 
	 * <p>
	 * The packet format is:
	 * 
	 * <pre>
	 * 0-3: the int value of TROUBLESHOOT
	 * 4-7: the number of bytes used for peer type, denoted as &quot;tsize&quot;
	 * 8-(tsize+7): the bytes array representing peer type 
	 * (tsize+8)-(tsize+11): the number of bytes used for peer identifier, denoted as &quot;psize&quot;
	 * (tsize+12)-(tsize+psize+11): the bytes array representing peer identifier
	 * </pre>
	 * 
	 * @param type
	 *            the peer type
	 * @param pid
	 *            the peer identifier
	 * @return a <code>DatagramPacket</code> with troubleshoot message type
	 */
	protected synchronized DatagramPacket troubleshoot(PeerType type, String pid) {
		byte[] buf = new byte[type.getValue().length() + pid.length() + 12];

		// get message type
		int start = 0;
		int val = MsgType.TROUBLESHOOT.getValue();
		Tools.intToByteArray(val, buf, start);

		// get the size of peer type
		start = 4;
		byte[] buf1 = type.getValue().getBytes();
		int size = buf1.length;
		Tools.intToByteArray(size, buf, start);

		// put peer type into byte array
		start = 8;
		for (int i = 0; i < size; i++) {
			buf[i + start] = buf1[i];
		}

		// get the size of the peer identifier
		start += size;
		byte[] buf2 = pid.getBytes();
		size = buf2.length;
		Tools.intToByteArray(size, buf, start);

		// put peer identifier into byte array
		start += 4;
		for (int i = 0; i < size; i++) {
			buf[i + start] = buf2[i];
		}

		// return packet
		start += size;
		return new DatagramPacket(buf, 0, start);
	}

	// ---------------- end of udp service -----------------

	/**
	 * Check if there exists an IP address in the BOOTSTRAP_SERVER_LIST whose
	 * value is equal to the current IP address of the peer.
	 * 
	 * @param ip
	 *            the IP address list
	 * @return if the current IP address is in the BOOTSTRAP_SERVER_LIST, return
	 *         <code>true</code>; otherwise, return <code>false</code>
	 */
	public static boolean checkInet(String ip) {
		String inet = Inet.getInetAddress();
		String[] ipArray = ip.split(":");
		for (int i = 0; i < ipArray.length; i++) {
			if (ipArray[i].equalsIgnoreCase(inet)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the instance of <code>PhysicalInfo</code>, which includes the
	 * physical information of the IP address and the port used for monitoring
	 * network events.
	 * 
	 * @return an instance of <code>PhysicalInfo</code>
	 * @throws UnknownHostException
	 *             if cannot obtain a valid IP address
	 */
	public PhysicalInfo getPhysicalInfo() throws UnknownHostException {
		String ip = Inet.getInetAddress();
		if (ip == null) {
			throw new UnknownHostException("Cannot obtain IP address");
		}
		return (new PhysicalInfo(ip, LOCAL_SERVER_PORT));
	}

	/**
	 * Send a message to a peer.
	 * 
	 * @param dest
	 *            the peer to which the message will be sent
	 * @param message
	 *            the message to be sent
	 * @throws Exception 
	 */
	public boolean sendMessage(PhysicalInfo dest, Message message) throws Exception{
		Socket socket = new Socket(dest.getIP(), dest.getPort());
		ObjectOutputStream oos = new ObjectOutputStream(socket
				.getOutputStream());
		message.serialize(dest, oos);
		oos.close();
		socket.close();
		return true;
	}

	/**
	* Initialize the user's session.
	*
	* @param user
	* the user to be sign in the system
	* @param pwd
	* the password
	*/
	public void initSession(String user, String pwd) throws RuntimeException {
		if (Session.hasInstance()) {
			throw new RuntimeException("User session should not exist!");
		}

		/* initialize the session */
		Session.getInstance(user, pwd);
	}

	/**
	* Clear the user's session.
	*/
	public void clearSession() {
		Session.clear();
	}

	public abstract void logout(boolean b, boolean c, boolean d);

	/**
	 * chensu, 2009-05-07
	 */
	protected static void initDB() {
		DBProperty dbProperty = new DBProperty();
		ServerPeer.bestpeer_db = new DB(dbProperty.bestpeerdb_type,
				dbProperty.bestpeerdb_server, dbProperty.bestpeerdb_port,
				dbProperty.bestpeerdb_username, dbProperty.bestpeerdb_password,
				dbProperty.bestpeerdb_dbName);
		AbstractPeer.conn_bestpeerdb = AbstractPeer.bestpeer_db
				.createDbConnection();
	}
}