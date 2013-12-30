package sg.edu.nus.gui.test.peer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.attribute.standard.Severity;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.dbconnection.DBConnector;
import sg.edu.nus.gui.server.ConfigDialog;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

public class ServerToolbar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3172643524433586459L;

	private ServerGUI servergui = null;

	private JComboBox languageCombo = null;

	private JButton settingButton = null;
	private JButton helpButton = null;
	private JButton registerButton = null;
	private JButton updateButton = null;

	private ImageIcon settingIcon = null;
	private ImageIcon helpIcon = null;
	private ImageIcon registerIcon = null;
	private ImageIcon updateIcon = null;

	/**
	 * This is the default constructor
	 */
	public ServerToolbar(ServerGUI servergui) {
		super();

		this.servergui = servergui;

		initialize();
	}

	public void setComponentText() {
		languageCombo.removeAllItems();

		this.comboBoxAddItem();
	}

	public void comboBoxAddItem() {
		languageCombo.addItem(LanguageLoader
				.getProperty("languageselection.txt"));
		languageCombo.addItem(LanguageLoader
				.getProperty("languageselection.english"));
		languageCombo.addItem(LanguageLoader
				.getProperty("languageselection.chinese"));
		languageCombo.addItem(LanguageLoader
				.getProperty("languageselection.other"));

		languageCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selection = (String) languageCombo.getSelectedItem();

				if (selection.equals(LanguageLoader
						.getProperty("languageselection.english"))) {
					if (LanguageLoader.locale == LanguageLoader.chinese) {
						LanguageLoader
								.newLanguageLoader(LanguageLoader.english);

						servergui.setComponentText();

					} else if (LanguageLoader.locale == LanguageLoader.english) {
						return;
					}
				} else if (selection.equals(LanguageLoader
						.getProperty("languageselection.chinese"))) {
					if (LanguageLoader.locale == LanguageLoader.chinese) {
						return;
					} else if (LanguageLoader.locale == LanguageLoader.english) {
						LanguageLoader
								.newLanguageLoader(LanguageLoader.chinese);
						servergui.setComponentText();
					}
				}
			}
		});
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		languageCombo = new JComboBox();

		this.comboBoxAddItem();

		this.settingIcon = new ImageIcon("./sg/edu/nus/gui/res/tools.png");
		this.settingButton = new JButton(this.settingIcon);
		int iconHW = 50;
		this.settingButton.setPreferredSize(new Dimension(iconHW, iconHW));
		this.settingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new ConfigDialog(servergui);
			}
		});

		this.helpIcon = new ImageIcon("./sg/edu/nus/gui/res/question.png");
		this.helpButton = new JButton(this.helpIcon);
		this.helpButton.setPreferredSize(new Dimension(iconHW, iconHW));
		this.helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Press help");
				
				SelectExecutor.executeFile("./sqlscript/testquery.sql", servergui.peer().userName);
				
			}
		});

		this.registerIcon = new ImageIcon(
				"./sg/edu/nus/gui/res/register-icon.jpg");
		this.registerButton = new JButton(this.registerIcon);
		this.registerButton.setPreferredSize(new Dimension(50, 50));

		this.updateIcon = new ImageIcon("./sg/edu/nus/gui/res/update.gif");
		this.updateButton = new JButton(this.updateIcon);
		this.updateButton.setPreferredSize(new Dimension(50, 50));

		this.add(this.settingButton);
		this.add(this.helpButton);

		this.setSize(400, 200);
	}

}
