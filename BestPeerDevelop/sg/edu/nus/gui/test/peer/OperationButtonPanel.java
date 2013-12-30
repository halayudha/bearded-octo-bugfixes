package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sg.edu.nus.peer.LanguageLoader;

public class OperationButtonPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5332075579816142549L;
	private JButton dbmanagerButton = null;
	private JButton querymanagerButton = null;
	private JButton usermanagerButton = null;
	private JButton smmanagerButton = null;

	ImageIcon dbManagerIcon = new ImageIcon(
			"./sg/edu/nus/gui/res/dbmanager.png");
	ImageIcon queryManagerIcon = new ImageIcon(
			"./sg/edu/nus/gui/res/querymanager.png");
	ImageIcon userManagerIcon = new ImageIcon(
			"./sg/edu/nus/gui/res/usermanager.jpg");
	ImageIcon schemamappingManagerIcon = new ImageIcon(
			"./sg/edu/nus/gui/res/schemamanager.gif");

	/**
	 * This is the default constructor
	 */
	public OperationButtonPanel() {
		super();

		LanguageLoader.newLanguageLoader(LanguageLoader.english);

		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		this.setLayout(new BorderLayout());

		Box box = Box.createVerticalBox();
		box.add(getDbmanagerButton());
		box.add(getQuerymanagerButton());
		box.add(getUsermanagerButton());
		box.add(getSmmanagerButton());

		this.add(box, BorderLayout.CENTER);

		this.setSize(200, 400);

	}

	/**
	 * This method initializes dbmanagerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDbmanagerButton() {
		if (dbmanagerButton == null) {
			dbmanagerButton = new JButton(LanguageLoader
					.getProperty("button.dbmanager"), this.dbManagerIcon);
			dbmanagerButton.setHorizontalTextPosition(SwingConstants.RIGHT);
			dbmanagerButton.setVerticalTextPosition(SwingConstants.CENTER);
			dbmanagerButton.setPreferredSize(new Dimension(200, 50));
		}
		return dbmanagerButton;
	}

	/**
	 * This method initializes querymanagerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQuerymanagerButton() {
		if (querymanagerButton == null) {
			querymanagerButton = new JButton(LanguageLoader
					.getProperty("button.querymanager"), this.queryManagerIcon);

			querymanagerButton.setHorizontalTextPosition(SwingConstants.RIGHT);
			querymanagerButton.setVerticalTextPosition(SwingConstants.CENTER);
			querymanagerButton.setPreferredSize(new Dimension(200, 50));
		}
		return querymanagerButton;
	}

	/**
	 * This method initializes usermanagerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getUsermanagerButton() {
		if (usermanagerButton == null) {
			usermanagerButton = new JButton(LanguageLoader
					.getProperty("button.usermanager"), this.userManagerIcon);

			usermanagerButton.setHorizontalTextPosition(SwingConstants.RIGHT);
			usermanagerButton.setVerticalTextPosition(SwingConstants.CENTER);
			usermanagerButton.setPreferredSize(new Dimension(200, 50));
		}
		return usermanagerButton;
	}

	/**
	 * This method initializes smmanagerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSmmanagerButton() {
		if (smmanagerButton == null) {
			smmanagerButton = new JButton(LanguageLoader
					.getProperty("button.schemamanager"),
					this.schemamappingManagerIcon);

			smmanagerButton.setHorizontalTextPosition(SwingConstants.RIGHT);
			smmanagerButton.setVerticalTextPosition(SwingConstants.CENTER);
			smmanagerButton.setPreferredSize(new Dimension(200, 50));
		}
		return smmanagerButton;
	}

}
