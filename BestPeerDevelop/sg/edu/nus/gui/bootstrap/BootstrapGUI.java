/*
 * @(#) BootstrapGUI.java 1.0 2005-12-29
 * 
 * Copyright 2005, National University of Singapore. All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.awt.BorderLayout;

import javax.swing.UIManager;

import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.customcomponent.JStatusBar;
import sg.edu.nus.logging.LogEvent;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.info.CacheDbIndex;

/**
 * Implement a bootstrap server that provides the entrance point of register,
 * sign in and depart from the PeerDB system. In other words, only through the
 * bootstrap server, each user or peer can know where is the system and how to
 * join the network.
 * <p>
 * The basic functionality of the bootstrap server is to monitor all online
 * peers. modified by Han Xixian 2008-6-3
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-29
 */

public class BootstrapGUI extends AbstractMainFrame {

	// private members
	private static final long serialVersionUID = -3865284058256280506L;

	/**
	 * Run the bootstrap server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// set look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LogManager.LogException("Can't initialize style for server gui", e);
		}
		
		//check connection and prompt user to input if neccessary
		DBProperty.setConfigFile("./conf/bootstrap.ini");
		DBProperty dbProperty = new DBProperty();
		if (! dbProperty.getBestpeerDB().testDbConnection()){
			DlgBootstrapConfig.showDialogAlone();
		}
		
		//run bootstrap
		new BootstrapGUI();
	}

	/**
	 * The idea is to have an instance of ServerSocket listening over some port
	 * for as long as the first running instance of our app runs. Consequent
	 * launches of the same application will try to set up their own
	 * ServerSocket over the same port, and will get a java.net.BindException
	 * (because we cannot start two versions of a server on the same port), and
	 * we'll know that another instance of the app is already running.
	 * 
	 * Now, if, for any unintentional reason, our app dies, the resources used
	 * by the app die away with it, and another instance can start and set the
	 * ServerSocket to prevent more instances from running.
	 * 
	 * We use an obscure port to test whether an instance has existed.
	 */

	// /**
	// * Define an obscure port to test whether an instance has existed.
	// */
	// define components
	private MenuBar menuBar;
	private ToolBar toolBar;
	private Pane pane;

	private JStatusBar statusBar;

	// define bootstrap peer
	private Bootstrap bootstrap;

	public CacheDbIndex cacheDbIndex = new CacheDbIndex();
	
	/**
	 * Construct a bootstrap server.
	 */
	public BootstrapGUI() {
		super(LanguageLoader.getProperty("system.boot"),
				AbstractMainFrame.SRC_PATH + "icon.JPG", 700, 500);

		// create bootstrap server
		bootstrap = new Bootstrap(this, PeerType.BOOTSTRAP.getValue());

		initBootstrapGUI();
		
		startServer();
		
		cacheDbIndex.loadFromDB(bootstrap.conn_bestpeerdb);
	}

	/**
	 * Add the message to the status bar.
	 * 
	 * @param str
	 *            the message
	 */
	public void addMsg2StatusBar(String str) {
		statusBar.setText(str);
	}

	/**
	 * Get the instance of the tabbed pane.
	 * 
	 * @return the instance of the tabbed pane
	 */
	public Pane getPane() {
		return pane;
	}

	/**
	 * Initialize the main frame
	 * @author chensu
	 * @date 2009-05-07
	 */
	private void initBootstrapGUI() {
		// set layout
		this.setLayout(new BorderLayout());

		// make menu bar
		menuBar = new MenuBar(this);
		this.setJMenuBar(menuBar);
		menuBar.setEnable(false);

		// init tool bar
		toolBar = new ToolBar(this);
		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
		toolBar.setEnable(false);

		// init content pane with tabbed panel
		pane = new Pane(this);
		this.getContentPane().add(pane, BorderLayout.CENTER);

		// init status bar
		statusBar = new JStatusBar();
		statusBar.setText(LanguageLoader.getProperty("system.msg3"));
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);

		// locate the main frame
		this.centerOnScreen();

		// show frame now
		this.setVisible(true);
	}

	/**
	 * Show event in the gui
	 * @author chensu
	 * @date 2009-04-28
	 */
	public void log(LogEvent event) {
		if (this.pane != null)
			pane.log(event);
	}

	public void logout(boolean toBoot, boolean toServer, boolean toClient) {
		this.bootstrap.troubleshoot(toBoot, toServer, toClient);
		this.stopServer();
	}

	/**
	 * Returns the handler of <code>Bootstrap</code>.
	 * 
	 * @return the handler of <code>Bootstrap</code>
	 */
	public AbstractPeer peer() {
		return bootstrap;
	}

	public void processWhenWindowClosing() {
		// do clear job here
		bootstrap.logout(false, true, false);
	}

	// VHTam
	public void sendSchema(String schema) {
		this.bootstrap.sendSchema(schema);
	}

	/**
	 * Start the bootstrap service.
	 * 
	 * @return if success, return <code>true</code>; otherwise, return
	 *         <code>false</code>
	 */
	public boolean startServer() {
		if (bootstrap.startServer()) {
			statusBar.setText(LanguageLoader.getProperty("system.msg4")
					+ Bootstrap.LOCAL_SERVER_PORT);
			menuBar.setEnable(true);
			toolBar.setEnable(true);

			// Added by Han Xixian, used for test network status to distribute
			// global schema
			// Modified in 2008-6-3

			pane.setEnableTestNetworkStatus(true);

			// This is just s demo which is used to show the functionality.
			// The code here should be deleted for formal edition.
			boolean webStatus = true;
			boolean MySQLStatus = true;
			boolean superpeerStatus = true;
			pane.setNetworkStatus(webStatus, MySQLStatus, superpeerStatus);

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
		// if (bootstrap.stopEventManager() && bootstrap.stopUDPServer()) {
		if (bootstrap.stopServer()) {
			statusBar.setText(LanguageLoader.getProperty("system.msg5"));
			menuBar.setEnable(false);
			toolBar.setEnable(false);
			pane.removeOnlinePeers();
			pane.removeLogEvents();

			pane.clearGlobalSchemaTable();
			pane.setEnableDistributeGlobalSchema(false);
			pane.setEnableTestNetworkStatus(false);

			return true;
		}
		return false;
	}

	@Override
	public void restoreTitle() {
		this.setTitle(LanguageLoader.getProperty("system.boot"));
	}
}