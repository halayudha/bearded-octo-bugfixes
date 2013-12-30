package sg.edu.nus.gui.test.peer;

import javax.swing.JButton;
import javax.swing.JPanel;

public class text extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6421771625419730553L;
	private JButton jButton = null;
	private JButton jButton1 = null;

	/**
	 * This is the default constructor
	 */
	public text() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setSize(420, 200);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new java.awt.Rectangle(31, 32, 34, 10));
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setBounds(new java.awt.Rectangle(106, 32, 34, 10));
		}
		return jButton1;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
