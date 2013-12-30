package sg.edu.nus.gui.sharing;

import javax.swing.JDialog;
import javax.swing.UIManager;

import sg.edu.nus.peer.LanguageLoader;

public class SDBExplorerDlg extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1727361854417021868L;
	SDBExplorerPanel panel;

	public SDBExplorerDlg() {

		panel = new SDBExplorerPanel(this);
		this.add(panel);

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

		this.setTitle("Shared Database Explorer");
	}

	public static void main(String[] args) {
		new SDBExplorerDlg();
	}
}
