package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import sg.edu.nus.gui.dbview.GlobalSchemaDBTreeView;
import sg.edu.nus.gui.dbview.LocalSchemaDBTreeView;
import sg.edu.nus.peer.LanguageLoader;

public class DBExplorerPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2772116178787624038L;
	private DBExplorerToolbar toolbar = null;
	private LocalSchemaDBTreeView localDBTreeView = null;
	private GlobalSchemaDBTreeView globalDBTreeView = null;
	private DBExplorerMappingPanel mappingPanel = null;

	private OperatePanel operatePanel = null;

	public DBExplorerPanel(OperatePanel operatePanel) {
		super();

		this.operatePanel = operatePanel;

		initPane();
	}

	public void initPane() {
		toolbar = new DBExplorerToolbar(this);
		localDBTreeView = new LocalSchemaDBTreeView(this, this);
		globalDBTreeView = new GlobalSchemaDBTreeView(this, this);
		mappingPanel = new DBExplorerMappingPanel();

		this.setLayout(new BorderLayout());

		JPanel LT_MP_GP_PANEL = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		LT_MP_GP_PANEL.setLayout(layout);

		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 250;
		constraints1.weighty = 250;
		constraints1.fill = GridBagConstraints.BOTH;
		constraints1.insets.bottom = 0;
		constraints1.insets.left = 0;
		constraints1.insets.right = 0;
		constraints1.insets.top = 0;

		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 150;
		constraints2.weighty = 250;
		constraints2.fill = GridBagConstraints.BOTH;
		constraints2.insets.bottom = 0;
		constraints2.insets.left = 0;
		constraints2.insets.right = 0;
		constraints2.insets.top = 0;

		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 2;
		constraints3.gridy = 0;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 250;
		constraints3.weighty = 250;
		constraints3.fill = GridBagConstraints.BOTH;
		constraints3.insets.bottom = 0;
		constraints3.insets.left = 0;
		constraints3.insets.right = 0;
		constraints3.insets.top = 0;

		LT_MP_GP_PANEL.add(this.localDBTreeView, constraints1);
		LT_MP_GP_PANEL.add(this.mappingPanel, constraints2);
		LT_MP_GP_PANEL.add(this.globalDBTreeView, constraints3);

		this.add(this.toolbar, BorderLayout.PAGE_START);
		this.add(LT_MP_GP_PANEL, BorderLayout.CENTER);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(4), LanguageLoader
				.getProperty("PanelTitle.dbexplorer")));

	}

	/**
	 * @return the operatePanel
	 */
	public OperatePanel getOperatePanel() {
		return operatePanel;
	}

	/**
	 * @param operatePanel the operatePanel to set
	 */
	public void setOperatePanel(OperatePanel operatePanel) {
		this.operatePanel = operatePanel;
	}

	/**
	 * @return the localDBTreeView
	 */
	public LocalSchemaDBTreeView getLocalDBTreeView() {
		return localDBTreeView;
	}

	/**
	 * @param localDBTreeView the localDBTreeView to set
	 */
	public void setLocalDBTreeView(LocalSchemaDBTreeView localDBTreeView) {
		this.localDBTreeView = localDBTreeView;
	}

	/**
	 * @return the globalDBTreeView
	 */
	public GlobalSchemaDBTreeView getGlobalDBTreeView() {
		return globalDBTreeView;
	}

	/**
	 * @param globalDBTreeView the globalDBTreeView to set
	 */
	public void setGlobalDBTreeView(GlobalSchemaDBTreeView globalDBTreeView) {
		this.globalDBTreeView = globalDBTreeView;
	}

	/**
	 * @return the mappingPanel
	 */
	public DBExplorerMappingPanel getMappingPanel() {
		return mappingPanel;
	}

	/**
	 * @param mappingPanel the mappingPanel to set
	 */
	public void setMappingPanel(DBExplorerMappingPanel mappingPanel) {
		this.mappingPanel = mappingPanel;
	}
}
