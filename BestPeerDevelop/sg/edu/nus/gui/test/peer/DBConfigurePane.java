package sg.edu.nus.gui.test.peer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class DBConfigurePane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5281988413574589684L;

	private OperatePanel operatePanel = null;

	private DBConfigureView dbconfigView = null;

	/**
	 * This is the default constructor
	 */
	public DBConfigurePane(OperatePanel operatePanel) {
		super();
		this.operatePanel = operatePanel;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridBagLayout());

		this.dbconfigView = new DBConfigureView(operatePanel);
		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridwidth = 1;
		constraints1.weightx = 100;
		constraints1.weighty = 100;
		constraints1.fill = GridBagConstraints.HORIZONTAL;

		JPanel fillPanel = new JPanel();
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridwidth = 1;
		constraints2.weightx = 100;
		constraints2.weighty = 100;
		constraints2.fill = GridBagConstraints.NONE;
		fillPanel.setPreferredSize(new Dimension(280, 300));
		fillPanel.setBackground(OperatePanel.panel_color);

		panel1.add(this.dbconfigView, constraints1);
		panel1.add(fillPanel, constraints2);
		panel1.setBackground(OperatePanel.panel_color);

		this.setLayout(new BorderLayout());

		this.add(panel1, BorderLayout.NORTH);

		this.setBackground(OperatePanel.panel_color);
	}

}
