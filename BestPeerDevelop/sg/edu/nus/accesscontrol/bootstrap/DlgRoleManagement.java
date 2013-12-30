package sg.edu.nus.accesscontrol.bootstrap;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Connection;

import javax.swing.JDialog;

import sg.edu.nus.accesscontrol.gui.PanelAccessControlManagement;
import sg.edu.nus.gui.AbstractDialog;

public class DlgRoleManagement extends AbstractDialog {

	private static final long serialVersionUID = 5145822527239909744L;

	Connection conn = null;

	public DlgRoleManagement(Frame owner, String title, Connection conn) {

		super(owner, title, true, 730, 635);

		this.conn = conn;

		initGUI();
	}

	public void setDbConnection(Connection conn) {
		this.conn = conn;
	}

	private void initGUI() {

		this.setResizable(true);
		this.addComponentListener(new ResizeListener(this));

		// create panel
		PanelAccessControlManagement panelRoleMan = new PanelAccessControlManagement(
				conn);

		// add main Panel to dialog
		this.getContentPane().add(panelRoleMan, BorderLayout.CENTER);
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
		}

		public void componentHidden(ComponentEvent e) {

		}

		public void componentMoved(ComponentEvent e) {

		}

		public void componentShown(ComponentEvent e) {

		}
	}

}
