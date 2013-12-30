package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import sg.edu.nus.accesscontrol.AccessControlHelper;
import sg.edu.nus.gui.HtmlDialog;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

//import sun.security.krb5.internal.ccache.ar;

public class ServerMenuBar extends JMenuBar {

	private static final long serialVersionUID = 960104605040874422L;

	ServerGUI servergui = null;

	JMenu fileMenu = null;
	JMenu settingMenu = null;

	String file = "menu.file";
	String setting = "menu.setting";
	String settingWaitTime = "menu.setWaitTime";
	String queryBrowsing = "menu.queryBrowing";
	String settingDataExportTime = "menu.setDataExportTime";

	JMenuItem mnuItemSetWaitTime = null;
	JMenuItem mnuItemSetDataExportTime = null;
	JMenuItem mnuItemQueryBrowsing = null;

	JMenu reportMenu = null;
	String report = "menu.report";
	String reportUserAccess = "menu.reportUserAccess";
	JMenuItem mnuItemReportUserAccess = null;

	public ServerMenuBar(ServerGUI servergui) {

		this.servergui = servergui;

		fileMenu = new JMenu(LanguageLoader.getProperty(file));
		settingMenu = new JMenu(LanguageLoader.getProperty(setting));

		// setting query time
		mnuItemSetWaitTime = new JMenuItem(LanguageLoader
				.getProperty(settingWaitTime));
		settingMenu.add(mnuItemSetWaitTime);

		// setting data export time
		mnuItemSetDataExportTime = new JMenuItem(LanguageLoader
				.getProperty(settingDataExportTime));
		settingMenu.add(mnuItemSetDataExportTime);

		// new query browser
		mnuItemQueryBrowsing = new JMenuItem(LanguageLoader
				.getProperty(queryBrowsing));
		fileMenu.add(mnuItemQueryBrowsing);

		// report menu
		reportMenu = new JMenu(LanguageLoader.getProperty(report));
		mnuItemReportUserAccess = new JMenuItem(LanguageLoader
				.getProperty(reportUserAccess));
		reportMenu.add(mnuItemReportUserAccess);

		// add menu
		this.add(fileMenu);
		this.add(settingMenu);
		this.add(reportMenu);

		initAction();
	}

	private void initAction() {

		mnuItemSetWaitTime.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String title = LanguageLoader.getProperty("title.setWaitTime");
				String message = LanguageLoader
						.getProperty("message.inputWaitTime");

				String str = (String) JOptionPane.showInputDialog(null,
						message, title, JOptionPane.INFORMATION_MESSAGE, null,
						null, "60");

				int value = 60;
				try {
					value = Integer.parseInt(str);
				} catch (Exception ex) {
					value = 60;
				}

				if (value > 0) {
					ServerPeer.queryWaitingTime = value * 1000;// convert to
					// milliseconds
				}

			}

		});

		mnuItemSetDataExportTime.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String title = LanguageLoader
						.getProperty("title.setDataExportTime");
				String message = LanguageLoader
						.getProperty("message.inputDataExportTime");

				String str = (String) JOptionPane.showInputDialog(null,
						message, title, JOptionPane.INFORMATION_MESSAGE, null,
						null, "23:01:01");

				int h = 23, m = 0, s = 0;
				String[] arrStr = str.split(":");
				try {
					h = Integer.parseInt(arrStr[0]);
					m = Integer.parseInt(arrStr[1]);
					s = Integer.parseInt(arrStr[2]);
					JOptionPane.showMessageDialog(null, "period " + h + ":" + m
							+ ":" + s);
					ServerPeer serverpeer = servergui.peer();

					serverpeer.dataExport.setSchedule(h, m, s);

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane
							.showMessageDialog(null,
									"Illegal time format! Please input time period again.");
				}
			}

		});

		mnuItemReportUserAccess.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String title = LanguageLoader.getProperty("title.dlgSetUser");
				String message = LanguageLoader
						.getProperty("message.inputUserName");

				String[] users = MetaDataAccess
						.metaGetUsers(ServerPeer.conn_metabestpeerdb);
				String user = (String) JOptionPane.showInputDialog(null,
						message, title, JOptionPane.INFORMATION_MESSAGE, null,
						users, "u4");

				if (user != null) {
					Vector<String> privilegeTalbes = new Vector<String>();
					Vector<String> privilegeColumns = new Vector<String>();
					Vector<String> privilegeRows = new Vector<String>();

					AccessControlHelper.getAllPrivelegeOfUser(user,
							privilegeTalbes, privilegeColumns, privilegeRows);

					String whole_table = "whole table";
					String column_level = "column level";
					String row_level = "row level";

					Vector<String[]> allPrivileges = new Vector<String[]>();
					String[] privilege = null;

					for (int i = 0; i < privilegeTalbes.size(); i++) {
						privilege = new String[3];
						privilege[0] = "select";
						privilege[1] = privilegeTalbes.get(i);
						privilege[2] = whole_table;
						allPrivileges.add(privilege);
					}

					for (int i = 0; i < privilegeColumns.size(); i++) {
						privilege = new String[3];
						privilege[0] = "select";
						privilege[1] = privilegeColumns.get(i);
						privilege[2] = column_level;
						allPrivileges.add(privilege);
					}

					for (int i = 0; i < privilegeRows.size(); i++) {
						privilege = new String[3];
						privilege[0] = "select";
						privilege[1] = privilegeRows.get(i);
						privilege[2] = row_level;
						allPrivileges.add(privilege);
					}

					String dlgTitle = LanguageLoader
							.getProperty("title.dlgUserAccess")
							+ " " + user;
					new HtmlDialog(dlgTitle, allPrivileges);
					// dlg will show itself
				}

			}

		});

		mnuItemQueryBrowsing.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String user = "u1";
				user = JOptionPane.showInputDialog("Please input user name: ");

				JDialog dlg = new JDialog();
				dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				dlg.setTitle("Query Browser of user "+user);
				dlg.setModal(true);
				dlg.setSize(800,750);
				
				QueryBrowerPanel queryPanel = new QueryBrowerPanel(user);
				dlg.getContentPane().setLayout(new BorderLayout());
				dlg.getContentPane().add(queryPanel, BorderLayout.CENTER);
				
				dlg.setVisible(true);

			}

		});

	}

	public void SetComponentText() {
		if (fileMenu != null)
			fileMenu.setText(LanguageLoader.getProperty(file));

		if (settingMenu != null)
			settingMenu.setText(LanguageLoader.getProperty(setting));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
