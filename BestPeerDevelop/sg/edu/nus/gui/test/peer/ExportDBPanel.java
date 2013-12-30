package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import sg.edu.nus.gui.customcomponent.JStatusBar;
import sg.edu.nus.gui.dbview.ExportGlobalDBTreeView;
import sg.edu.nus.gui.dbview.ExportLocalDBTreeView;
import sg.edu.nus.peer.LanguageLoader;

/**
 * 
 * @author Han Xixian
 * 
 * This class is used to display interface for exporting data 
 * to database outside firewall
 *
 */

public class ExportDBPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6962055425154955408L;

	private OperatePanel operatePanel = null;

	private ExportDBToolBar toolbar = null;
	private ExportLocalDBTreeView exportLocalTreeView = null;
	private ExportGlobalDBTreeView exportGlobalTreeView = null;
	private ExportDBButtonPanel buttonPanel = null;

	public static JStatusBar exportdb_statusBar;

	public ExportDBPanel(OperatePanel operatePanel) {
		super();

		this.operatePanel = operatePanel;

		initPane();
	}

	public void initPane() {
		this.toolbar = new ExportDBToolBar(this);

		JPanel EXPORT_LT_B_GT = new JPanel();
		EXPORT_LT_B_GT.setLayout(new GridBagLayout());

		this.exportLocalTreeView = new ExportLocalDBTreeView(this, this);
		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 400;
		constraints1.weighty = 600;
		constraints1.fill = GridBagConstraints.BOTH;

		this.buttonPanel = new ExportDBButtonPanel(this);
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 0;
		constraints2.weighty = 600;
		constraints2.fill = GridBagConstraints.VERTICAL;

		this.exportGlobalTreeView = new ExportGlobalDBTreeView(this, this);
		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 2;
		constraints3.gridy = 0;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 400;
		constraints3.weighty = 600;
		constraints3.fill = GridBagConstraints.BOTH;

		EXPORT_LT_B_GT.add(this.exportLocalTreeView, constraints1);
		EXPORT_LT_B_GT.add(this.buttonPanel, constraints2);
		EXPORT_LT_B_GT.add(this.exportGlobalTreeView, constraints3);

		this.setLayout(new BorderLayout());

		this.add(this.toolbar, BorderLayout.PAGE_START);
		this.add(EXPORT_LT_B_GT, BorderLayout.CENTER);

		exportdb_statusBar = new JStatusBar();
		this.add(exportdb_statusBar, BorderLayout.SOUTH);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.exportDB")));

	}

	public OperatePanel getOperatePanel() {
		return operatePanel;
	}

	/**
	 * @return the toolbar
	 */
	public ExportDBToolBar getToolbar() {
		return toolbar;
	}

	/**
	 * @param toolbar the toolbar to set
	 */
	public void setToolbar(ExportDBToolBar toolbar) {
		this.toolbar = toolbar;
	}

	/**
	 * @return the exportLocalTreeView
	 */
	public ExportLocalDBTreeView getExportLocalTreeView() {
		return exportLocalTreeView;
	}

	/**
	 * @param exportLocalTreeView the exportLocalTreeView to set
	 */
	public void setExportLocalTreeView(ExportLocalDBTreeView exportLocalTreeView) {
		this.exportLocalTreeView = exportLocalTreeView;
	}

	/**
	 * @return the exportGlobalTreeView
	 */
	public ExportGlobalDBTreeView getExportGlobalTreeView() {
		return exportGlobalTreeView;
	}

	/**
	 * @param exportGlobalTreeView the exportGlobalTreeView to set
	 */
	public void setExportGlobalTreeView(
			ExportGlobalDBTreeView exportGlobalTreeView) {
		this.exportGlobalTreeView = exportGlobalTreeView;
	}

	/**
	 * @return the buttonPanel
	 */
	public ExportDBButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	/**
	 * @param buttonPanel the buttonPanel to set
	 */
	public void setButtonPanel(ExportDBButtonPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	/**
	 * @param operatePanel the operatePanel to set
	 */
	public void setOperatePanel(OperatePanel operatePanel) {
		this.operatePanel = operatePanel;
	}
}
