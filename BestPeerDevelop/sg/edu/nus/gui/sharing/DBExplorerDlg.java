package sg.edu.nus.gui.sharing;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.UIManager;

import sg.edu.nus.peer.LanguageLoader;

public class DBExplorerDlg extends JDialog {
	private static final long serialVersionUID = 7357219946142955449L;

	DBExplorerPanel panel;

	public DBExplorerDlg() {
		this.setLayout(new BorderLayout());
		panel = new DBExplorerPanel(this);
		this.add(panel, BorderLayout.CENTER);

		this.setLocation(300, 100);
		this.setSize(400, 600);

		try // set look and feel
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		LanguageLoader.newLanguageLoader(LanguageLoader.english);

		this.setVisible(true);

		this.setTitle("Database Explorer Dialog");
	}

	public static void main(String[] args) {
		new DBExplorerDlg();
	}
}
