package sg.edu.nus.gui.bootstrap;

/**
 * Setting column properties when edit global schema
 * @author VHTam
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import sg.edu.nus.gui.AbstractDialog;
import sg.edu.nus.gui.GuiHelper;
import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.gui.customcomponent.GradientLabel;
import sg.edu.nus.gui.customcomponent.GridBagPanel;
import sg.edu.nus.gui.customcomponent.HeaderFooterPanel;
import sg.edu.nus.gui.customcomponent.SimpleGradientPanel;
import sg.edu.nus.peer.LanguageLoader;

/**
 * Dialog for adding a column into a table in global schema
 * @author VHTam
 *
 */
public class DlgSchemaAddColumn extends AbstractDialog implements
		ActionListener {

	private static final long serialVersionUID = 1L;

	String strCmdOk = "ok";
	String strCmdCancel = "cancel";

	JComboBox comboColumnType = null;
	JTextField txtColumnName = null;

	String strColumnName = null;
	String strColumnType = null;

	public DlgSchemaAddColumn(Frame owner, String title, boolean modal) {

		super(owner, title, modal, 395, 389);

		this.setTitle(LanguageLoader.getProperty("label.addNewColumn"));

		initGUI();
	}

	public String getColumnName() {
		return strColumnName;
	}

	public String getColumnType() {
		return strColumnType;
	}

	private void initGUI() {

		this.setResizable(true);
		this.addComponentListener(new ResizeListener(this));

		this.getContentPane().add(createHeaderPanel(), BorderLayout.NORTH);
		this.getContentPane().add(createCommandPanel(), BorderLayout.SOUTH);

		SimpleGradientPanel sideLeftPanel = new SimpleGradientPanel();
		sideLeftPanel.setDirection(SimpleGradientPanel.VERTICAL);
		sideLeftPanel.setLayout(new BorderLayout());
		JPanel insideleft = GuiHelper.createBlankColorPanel(20,
				GuiLoader.themeBkColor);
		sideLeftPanel.add(insideleft, BorderLayout.SOUTH);

		JPanel sideRightPanel = GuiHelper.createBlankThemePanel(20);

		this.getContentPane().add(sideLeftPanel, BorderLayout.WEST);
		this.getContentPane().add(sideRightPanel, BorderLayout.EAST);

		// add main Panel to dialog
		this.getContentPane().add(createContentPanel(), BorderLayout.CENTER);

	}

	private JPanel createHeaderPanel() {

		SimpleGradientPanel pane = new SimpleGradientPanel();
		pane.setLayout(new BorderLayout());

		String header_role = LanguageLoader.getProperty("label.globalSchema");
		String header_action = LanguageLoader.getProperty("label.addNewColumn");

		String htmlLabel = "<html><font size = 5 >" + header_role
				+ "</font><br>" + "<font color=\"#0000ff\">" + header_action
				+ "<br>";

		JLabel lblComment = new JLabel(htmlLabel);
		lblComment.setBorder(new EmptyBorder(new Insets(5, 9, 5, 5)));

		pane.add(lblComment, BorderLayout.WEST);

		return pane;
	}

	private HeaderFooterPanel createContentPanel() {

		HeaderFooterPanel pane = new HeaderFooterPanel();
		pane.setBorder(new EmptyBorder(new Insets(GuiLoader.contentInset + 40,
				0, 0, 0)));

		String title = LanguageLoader.getProperty("label.setColumnProperty");
		pane.setTitle(title);

		GridBagPanel panel = new GridBagPanel(new Insets(0, 0, 0, 0));

		int widthLong = 200;

		int widthMedium = widthLong / 2;

		int heightLabel = 20;

		String lblCaption = LanguageLoader.getProperty("label.columnName");

		GradientLabel lbl1 = GuiHelper.createGradientLabel(lblCaption);
		lbl1.setPreferredSize(new Dimension(widthMedium, heightLabel));
		lbl1.setForeground(Color.BLUE);

		panel.addComponent(lbl1, 1, 2);

		txtColumnName = GuiHelper.createLnFTextField();
		txtColumnName.setPreferredSize(new Dimension(widthMedium,
				heightLabel + 4));
		txtColumnName.setForeground(Color.BLACK);

		// data
		txtColumnName.setText("");

		panel.addComponent(txtColumnName, 2, 2);

		// insert image label on the left

		lblCaption = "";
		JLabel lblImage = GuiHelper.createLnFLabel(lblCaption);

		ImageIcon icon = new ImageIcon("./sg/edu/nus/res/InformationIcon.png");
		// ImageIcon icon = null;

		lblImage.setIcon(icon);

		lblImage.setPreferredSize(new Dimension(widthMedium / 2, heightLabel));
		panel.addFilledComponent(lblImage, 1, 1, 1, 2, GridBagConstraints.BOTH);

		// /

		// insert hidden label for seperation...
		lblCaption = " ";
		JLabel lblHidden = GuiHelper.createLnFLabel(lblCaption);
		lblHidden.setPreferredSize(new Dimension(widthLong, heightLabel));
		panel.addComponent(lblHidden, 3, 2);

		// /

		// lblCaption = "Column type";
		lblCaption = LanguageLoader.getProperty("label.columnType");

		GradientLabel lbl2 = GuiHelper.createGradientLabel(lblCaption);
		lbl2.setPreferredSize(new Dimension(widthLong, heightLabel));
		lbl2.setForeground(Color.BLUE);

		panel.addComponent(lbl2, 4, 2);

		comboColumnType = GuiHelper.createLnFComboBox();
		comboColumnType.addActionListener(this);

		comboColumnType.setEditable(true);
		comboColumnType.addItem("varchar(30)");
		comboColumnType.addItem("int");
		comboColumnType.addItem("float");
		comboColumnType.addItem("text");
		comboColumnType.addItem("datetime");
		

		JScrollPane scroll = GuiHelper.createLnFScrollPane(comboColumnType);

		scroll.setPreferredSize(new Dimension(widthLong, heightLabel + 4));

		panel.addComponent(scroll, 5, 2);

		pane.add(panel);

		return pane;

	}

	private JPanel createCommandPanel() {

		JPanel pane = GuiHelper.createThemePanel();

		pane.setBorder(new EmptyBorder(new Insets(6, 6, 6, 6)));

		// one way of layout

		JButton btnOk = createButtonOk();
		pane.add(btnOk);

		int buttonDistance = 20;
		pane.add(GuiHelper.createBlankThemePanel(3, buttonDistance, 3,
				buttonDistance));

		JButton btnCancel = createButtonCancel();
		pane.add(btnCancel);

		return pane;
	}

	private JButton createButtonOk() {

		String caption = LanguageLoader.getProperty("button.ok");

		JButton btn = null;
		btn = new JButton(caption);

		btn.setActionCommand(strCmdOk);
		btn.addActionListener(this);

		return btn;
	}

	private JButton createButtonCancel() {

		String caption = LanguageLoader.getProperty("button.cancel");

		JButton btn = null;
		btn = new JButton(caption);

		btn.setActionCommand(strCmdCancel);
		btn.addActionListener(this);
		return btn;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(strCmdOk)) {
			strColumnName = txtColumnName.getText();

			strColumnType = (String) comboColumnType.getSelectedItem();

			this.dispose();

		} else if (e.getActionCommand().equals(strCmdCancel)) {
			strColumnName = null;
			strColumnType = null;
			this.dispose();
		}
		if (e.getSource().getClass().equals(JComboBox.class)) {
			JComboBox combo = (JComboBox) e.getSource();
			strColumnType = (String) combo.getSelectedItem();
		}

	}

	// //////////////////////////////

	protected void processWhenWindowClosing() {
		// do something to if needed
	}

	// //////////////////////////////

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				DlgSchemaAddColumn dlg = new DlgSchemaAddColumn(null,
						"Adding column", true);
				dlg.setVisible(true);

				System.exit(1);
			}
		});

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