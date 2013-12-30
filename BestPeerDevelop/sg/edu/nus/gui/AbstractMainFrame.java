/*
 * @(#) AbstractMainFrame.java 1.0 2006-1-26
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import sg.edu.nus.logging.LogEvent;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.PeerType;

/**
 * An abstract frame that simply wrapps <code>JFrame</code> for the convenience
 * of constructing a main GUI.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 */

public abstract class AbstractMainFrame extends JFrame implements
		ComponentListener {

	// public members
	/**
	 * The path of all system resource files.
	 */
	public static final String SRC_PATH = "./sg/edu/nus/res/";

	/**
	 * @MARK
	 * @author chensu Edit here to adjust default width for each column of event
	 *         table in the main gui
	 */
	public static final int[] EVENT_TABLE_WIDTH = { 90, 70, 75, 140, 120 };

	/** define the high frequency of sending UDP messages to remote peers */
	public final static long HIGH_FREQ = 60000; // 60 seconds

	/** define the normal frequency of sending UDP messages to remote peers */
	public final static long NORM_FREQ = 90000; // 90 seconds

	/** define the low frequency of sending UDP messages to remote peers */
	public final static long LOW_FREQ = 120000; // 120 seconds

	// protected members
	// /**
	// * The idea is to have an instance of ServerSocket listening over some
	// port
	// * for as long as the first running instance of our app runs.
	// * Consequent launches of the same application will try to set up
	// * their own ServerSocket over the same port, and will get a
	// java.net.BindException
	// * (because we cannot start two versions of a server on the same port),
	// * and we'll know that another instance of the app is already running.
	// * <p>
	// * Now, if, for any unintentional reason, our app dies, the resources used
	// by
	// * the app die away with it, and another instance can start and set the
	// ServerSocket
	// * to prevent more instances from running
	// */
	// protected static ServerSocket SERVER_SOCKET;

	/**
	 * The minimal height of the window
	 */
	protected int MIN_HEIGHT;

	/**
	 * The minimal width of the window
	 */
	protected int MIN_WIDTH;

	// private members
	private static final long serialVersionUID = 1946143456523911618L;

	static {
		initGUI();
	}

	public AbstractMainFrame(String title, String image) {
		super(title);

		/* add listener for window events */
		this.addComponentListener(this);

		/* set frame icon image */
		try { // image found
			this.setIconImage(new ImageIcon(image).getImage());
		} catch (Exception e) { // no image found
			System.err.println("Resource not found: " + image);
		}

		this.setAlwaysOnTop(false);
	}

	/**
	 * Construct a <code>JFrame</code> with specified parameters.
	 * 
	 * @param title
	 *            the frame title to be displayed
	 * @param image
	 *            the path of the icon image to be displayed
	 * @param height
	 *            the height of the window
	 * @param width
	 *            the width of the window
	 */
	public AbstractMainFrame(String title, String image, int height, int width) {
		this(title, image);
		/* set display position */
		this.MIN_HEIGHT = height;
		this.MIN_WIDTH = width;
		this.setSize(MIN_WIDTH, MIN_HEIGHT);
		// this.centerOnScreen();

		/* set other settings */
		this.setAlwaysOnTop(false);
	}

	/**
	 * Place the window at the center of the screen.
	 */
	protected void centerOnScreen() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screen.width - this.MIN_WIDTH) / 2, 0);
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	// ------------ for login and logout ---------------

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
		int width = getWidth();
		int height = getHeight();

		/*
		 * check if either the width or the height is smaller than the minimum
		 * value
		 */
		boolean resize = false;

		if (width < MIN_WIDTH) {
			resize = true;
			width = MIN_WIDTH;
		}
		if (height < MIN_HEIGHT) {
			resize = true;
			height = MIN_HEIGHT;
		}

		if (resize) {
			this.setSize(MIN_WIDTH, MIN_HEIGHT);
			centerOnScreen();
		}
	}

	// ------------ for customize window shape ------------

	public void componentShown(ComponentEvent arg0) {
	}

	/**
	 * Show event in the gui
	 * 
	 * @author chensu
	 * @date 2009-04-28
	 */
	public abstract void log(LogEvent logEvent);

	/**
	 * Returns the handler of <code>AbstractPeer</code> Corresponding to the
	 * sub-class of <code>AbstractMainFrame</code>.
	 * 
	 * @return the handler of <code>AbstractPeer</code>
	 */
	public abstract AbstractPeer peer();

	/**
	 * Perform some necessary jobs when the window is closing.
	 */
	protected abstract void processWhenWindowClosing();

	/**
	 * When a <code>WindowEvent</code> happens.
	 * 
	 * @param e
	 *            a <code>WindowEvent</code>
	 */
	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.processWhenWindowClosing();
			System.exit(0);
		}
	}

	protected static void initGUI() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"conf/config.ini"));

			String line = null;

			while ((line = reader.readLine()) != null) {
				String[] arrData = line.split("=");
				if (arrData[0].equals("Language")) {
					int language = Integer.parseInt(arrData[1].trim());

					if (language == 0)
						LanguageLoader
								.newLanguageLoader(LanguageLoader.english);
					else if (language == 1)
						LanguageLoader
								.newLanguageLoader(LanguageLoader.chinese);
					else if (language == 2)
						LanguageLoader.newLanguageLoader(LanguageLoader.locale);
				}
			}
		} catch (Exception e) {
			LogManager.LogException("Can't open configuration file", e);
			System.exit(1);
		}
	}

	// end chensu

	public abstract void logout(boolean b, boolean c, boolean d);

	public abstract void restoreTitle();

	public void scheduleUDPSender(long period) {
		// added by wusai, currently we disable the
		// udp sender of the server peer
		if (this.peer().getPeerType().equals(PeerType.BOOTSTRAP.getValue()))
			this.peer().scheduleUDPSender(period);
	}

}