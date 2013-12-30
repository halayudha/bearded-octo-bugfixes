package sg.edu.nus.gui.sharing;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.UIManager;

import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.gui.dbview.*;
import sg.edu.nus.gui.server.ServerGUI;

public class DBConfigureDlg extends JDialog {
	private static final long serialVersionUID = 7357219946142955449L;

	private DBConfigureView panel;
	private ServerGUI servergui = null;

	public DBConfigureDlg(ServerGUI servergui) {
		this.servergui = servergui;

		this.setLayout(new BorderLayout());
		panel = new DBConfigureView(this);
		this.add(panel, BorderLayout.CENTER);

		this.setLocation(300, 100);
		this.setSize(400, 300);

		try // set look and feel
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		LanguageLoader.newLanguageLoader(LanguageLoader.english);

		this.setVisible(true);

		this.setTitle("Database Configure Dialog");
	}

	/**
	 * @return the panel
	 */
	public DBConfigureView getPanel() {
		return panel;
	}

	/**
	 * @param panel the panel to set
	 */
	public void setPanel(DBConfigureView panel) {
		this.panel = panel;
	}

	/**
	 * @return the servergui
	 */
	public ServerGUI getServergui() {
		return servergui;
	}

	/**
	 * @param servergui the servergui to set
	 */
	public void setServergui(ServerGUI servergui) {
		this.servergui = servergui;
	}
}
