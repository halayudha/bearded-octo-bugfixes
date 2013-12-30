package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sg.edu.nus.gui.server.ServerGUI;

/**
 * Logo_Toolbar_Panel is used for showing Toobar and Logo of bestpeer
 * 
 * @author Han Xixian
 * 
 */

public class Logo_Toolbar_Panel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5472878355655213082L;

	private JLabel logo = null;
	private ImageIcon bestLogo = null;

	private ServerToolbar serverToolbar = null;

	/**
	 * This is the default constructor
	 */
	public Logo_Toolbar_Panel(ServerGUI servergui) {
		super();

		bestLogo = new ImageIcon("./sg/edu/nus/gui/res/logo.gif");
		logo = new JLabel(bestLogo);

		serverToolbar = new ServerToolbar(servergui);

		this.setLayout(new BorderLayout());

		initialize();
	}

	public void setComponentText() {
		serverToolbar.setComponentText();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		this.add(logo, BorderLayout.EAST);
		this.add(serverToolbar, BorderLayout.WEST);
		this.setSize(600, 400);
	}
}
