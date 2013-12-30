package sg.edu.nus.gui.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sg.edu.nus.bestpeer.queryprocessing.Win32;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.test.peer.Logo_Toolbar_Panel;
import sg.edu.nus.gui.test.peer.OperatePanel;
import sg.edu.nus.gui.test.peer.ServerMenuBar;
import sg.edu.nus.gui.test.peer.SuccessfulLoginPanel;
import sg.edu.nus.logging.LogEvent;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.CacheDbIndex;
import sg.edu.nus.peer.info.TreeNode;

/**
 * Main function of GUI
 * 
 * @author Han Xixian
 * 
 * @version 2008-8-1
 * 
 */

public class ServerGUI extends AbstractMainFrame implements WindowFocusListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8997906615159674963L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerGUI serverGUI = new ServerGUI();
		ServerGUI.instance = serverGUI;
		serverGUI.setVisible(true);
	}

	// protected members
	/**
	 * Define a super peer manager, who is responsible for providing all
	 * operations related to the super peer. Through this class, the minimal
	 * cohensions are expected between GUI and non-GUI services.
	 */
	protected ServerPeer serverpeer;
	private JPanel jContentPane = null;
	private ServerMenuBar menuBar = null;

	private OperatePanel operatePanel = null;

	private Logo_Toolbar_Panel logo_toolbar = null;

	private Pane pane;

	/**
	 * for static reference
	 * FIXME: needed?
	 */
	public static ServerGUI instance = null;

	/*
	 * Init all static methods or variables for the class object
	 */
	static {
		// ServerPeer.load();
		// David added this
		Win32.InitWin32();
		// end David added this
	}

	// flag for being processing query
	private boolean processingQuery = false;

	public CacheDbIndex cacheDbIndex = null;
	
	/**
	 * This is the default constructor
	 */
	public ServerGUI() {
		super(LanguageLoader.getProperty("system.super"),
				AbstractMainFrame.SRC_PATH + "icon.JPG", 700, 640);

		serverpeer = new ServerPeer(this, PeerType.SUPERPEER.getValue());

		initServerGUI();
		
		cacheDbIndex = new CacheDbIndex();
		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();

			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getOperatePanel(), BorderLayout.CENTER);
			jContentPane.add(getLogo_Toolbar(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	public Logo_Toolbar_Panel getLogo_Toolbar() {
		if (logo_toolbar == null)
			logo_toolbar = new Logo_Toolbar_Panel(this);

		return logo_toolbar;
	}

	public OperatePanel getOperatePanel() {
		if (operatePanel == null)
			operatePanel = new OperatePanel(this);

		return operatePanel;
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
	 * @return the serverpeer
	 */
	public ServerPeer getServerpeer() {
		return serverpeer;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initServerGUI() {
		if (LanguageLoader.locale == -1)
			LanguageLoader.newLanguageLoader(LanguageLoader.english);

		this.menuBar = new ServerMenuBar(this);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				this.getGraphicsConfiguration());
		Rectangle desktopBounds = new Rectangle(screenInsets.left,
				screenInsets.top, screenSize.width - screenInsets.left
						- screenInsets.right, screenSize.height
						- screenInsets.top - screenInsets.bottom);

		this.setSize(desktopBounds.width, desktopBounds.height);

		this.setContentPane(getJContentPane());
		this.setTitle(LanguageLoader.getProperty("servergui.title"));

		this.setJMenuBar(menuBar);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowClosed(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowClosing(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowDeactivated(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowDeiconified(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowIconified(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowOpened(WindowEvent e) {
				ServerGUI.this.repaint();
			}
		});
		this.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				ServerGUI.this.repaint();
			}

			public void windowLostFocus(WindowEvent e) {
				ServerGUI.this.repaint();
			}
		});
		this.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				ServerGUI.this.repaint();
			}
		});

	}

	public boolean isProcessingQuery() {
		return processingQuery;
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

	@Override
	public void logout(boolean b, boolean c, boolean d) {
		this.serverpeer.logout(b, c, d);
		this.restoreTitle();
	}

	/**
	 * adds an entry into the "Matches" table
	 * 
	 * @param sourceColumn
	 *            the source column, given as dbName.tableName.columnName
	 * @param targetColumn
	 *            the target column, given as dbName.tableName.columnName
	 * @return a positive integer representing the number of columns in the
	 *         global db that are still not matched, or a negative number if
	 *         there were exceptions or other problems
	 */
	public synchronized int matchColumns(String sourceColumn,
			String targetColumn) {
		return this.serverpeer.performColumnMatch(sourceColumn, targetColumn);
	}

	/**
	 * Returns the handler of the <code>ServerPeer</code>.
	 * 
	 * @return the handler of the <code>ServerPeer</code>
	 */
	public ServerPeer peer() {
		return this.serverpeer;
	}

	@Override
	protected void processWhenWindowClosing() {
		// we use udp to replace tcp here
		// serverpeer.performLogoutRequest();
		// this.logout(true, true, true);
		serverpeer.logout(true, true, true);
	}

	public void restart() {
		this.dispose();

		new ServerGUI().setVisible(true);
	}

	/**
	 * Restore title for the main frame (logout or login)
	 * @author chensu
	 * @date 2009-4-28
	 */
	public void restoreTitle() {
		this.setTitle(LanguageLoader.getProperty("servergui.title"));
	}

	public void setComponentText() {
		this.setTitle(LanguageLoader.getProperty("servergui.title"));
		menuBar.SetComponentText();
		operatePanel.setComponentText();
		logo_toolbar.setComponentText();
	}

	/**
	 * Set the menus in the menu bar enable or disable.
	 * 
	 * @param flag
	 *            the signal to determine if enable or disable
	 */
	public void setMenuEnable(boolean flag) {
		// menuBar.setEnable(flag);
	}

	public synchronized void setProcessingQuery(boolean processing) {
		processingQuery = processing;
	}

	/**
	 * @param serverpeer
	 *            the serverpeer to set
	 */
	public void setServerpeer(ServerPeer serverpeer) {
		this.serverpeer = serverpeer;
	}

	/**
	 * When be successful to login, modify the interface of Loing manager
	 * 
	 */

	public void showSuccessfulLoginUI() {
		operatePanel.setComponentAt(OperatePanel.TAB_LOGINMANAGER_INDEX,
				new SuccessfulLoginPanel(operatePanel));

		String userType = operatePanel.getUserType();

		if (userType == null){
			userType = LanguageLoader.getProperty("UserType.professional");
		}
		
		if (userType.equals(LanguageLoader.getProperty("UserType.normal"))) {
			operatePanel
					.setEnabledAt(OperatePanel.TAB_QUERYMANAGER_INDEX, true);
		} else {
			operatePanel.setEnabledAt(OperatePanel.TAB_DBMANAGER_INDEX, true);
			operatePanel.setEnabledAt(OperatePanel.TAB_ACCESSMANAGER_INDEX,
					true);
			
		}
	}

	/**
	 * Start the socket server for accepting incoming connections.
	 * 
	 * @return if success, return <code>true</code>; otherwise, return
	 *         <code>false</code>
	 */
	public boolean startService() {
		if (serverpeer.startService()) {
			this.showSuccessfulLoginUI();
			return true;
		}
		return false;
	}

	/**
	 * Removes an entry from the "Matches" table.
	 * 
	 * If one of the parameters is the empty string or null, then it removes all
	 * matches pertaining to the other parameter. <br>
	 * If both parameters are empty, it clears the table<br>
	 * 
	 * @param sourceColumn
	 *            the source column, given as dbName.tableName.columnName
	 * @param targetColumn
	 *            the target column, given as dbName.tableName.columnName
	 * @return a positive integer representing the number of columns in the
	 *         global db that are still not matched, or a negative number if
	 *         there were exceptions or other problems
	 */
	public synchronized int unmatchColumns(String sourceColumn,
			String targetColumn) {
		return this.serverpeer.performColumnUnmatch(sourceColumn, targetColumn);
	}

	/**
	 * call operatePanel update interface due to new data received in database
	 * 
	 */
	public void updateInterface() {
		operatePanel.updateInterface();
	}

	/**
	 * 
	 * Reset the UI of the content pane.
	 */
	public synchronized void updatePane() {
		// pane.updateNodeInfo();
	}

	/**
	 * Update the UI of the content pane.
	 * 
	 * @param treeNode
	 *            the instance of <code>TreeNode</code>
	 */
	public synchronized void updatePane(TreeNode treeNode) {
		// pane.updateNodeInfo(treeNode);
	}

	public synchronized void updateSchema(String newSchema) {
		this.serverpeer.performSchemaUpdate(newSchema);
	}

	public void windowGainedFocus(WindowEvent arg0) {
		this.repaint();
		System.out.println("Window gained focus!");
		
	}

	public void windowLostFocus(WindowEvent arg0) {
		System.out.println("Window lost focus!");
		
	}
}
