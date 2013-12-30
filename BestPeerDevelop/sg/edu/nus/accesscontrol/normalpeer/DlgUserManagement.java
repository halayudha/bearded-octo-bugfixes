package sg.edu.nus.accesscontrol.normalpeer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Connection;

import javax.swing.JDialog;

import sg.edu.nus.gui.AbstractDialog;

public class DlgUserManagement extends AbstractDialog {

	private static final long serialVersionUID = 5145822527239909744L;

	Connection conn = null;

	public DlgUserManagement(Frame owner, String title, boolean modal) {

		super(owner, title, modal, 700, 635);

		initGUI();
	}

	public void setDbConnection(Connection conn) {
		this.conn = conn;
	}

	private void initGUI() {

		this.setResizable(true);
		this.addComponentListener(new ResizeListener(this));

		// create panel
		PanelUserManagement panelUserMan = new PanelUserManagement(conn);

		// add main Panel to dialog
		this.getContentPane().add(panelUserMan, BorderLayout.CENTER);
	}


	protected void processWhenWindowClosing() {
		// do something to if needed
	}

	class ResizeListener implements ComponentListener {
		JDialog dlg = null;

		ResizeListener(JDialog owner) {
			dlg = owner;
		}

		public void componentResized(ComponentEvent e) {
			// System.out.println(e.getComponent().getClass().getName()
			// + " --- Resized ");
			// System.out.println("dlg with: " + dlg.getWidth());
			// System.out.println("dlg height: " + dlg.getHeight());
		}

		public void componentHidden(ComponentEvent e) {

		}

		public void componentMoved(ComponentEvent e) {

		}

		public void componentShown(ComponentEvent e) {

		}
	}

}
