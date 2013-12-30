package sg.edu.nus.gui.test.peer;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class DBTabbedPanel extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2126864207541606729L;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;

	/**
	 * This is the default constructor
	 */
	public DBTabbedPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.addTab(null, null, getJPanel(), null);
		this.addTab(null, null, getJPanel1(), null);
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
		}
		return jPanel1;
	}

}
