package sg.edu.nus.gui.bootstrap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import sg.edu.nus.accesscontrol.AccCtrlLanguageLoader;
import sg.edu.nus.gui.AbstractDialog;
import sg.edu.nus.gui.GuiHelper;
import sg.edu.nus.gui.GuiLoader;
import sg.edu.nus.gui.customcomponent.GradientLabel;
import sg.edu.nus.gui.customcomponent.GridBagPanel;
import sg.edu.nus.gui.customcomponent.HeaderFooterPanel;
import sg.edu.nus.gui.customcomponent.SimpleGradientPanel;
import sg.edu.nus.peer.LanguageLoader;

/**
 * Dialog for editing local admin accounts
 * @author VHTam
 *
 */
public class DlgAddLocalAdmin extends AbstractDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	String strCmdOk = "ok";
	String strCmdCancel = "cancel";

	String strUserName = null;
	String strUserDesc = null;
	String strUserPwd = null;

	boolean ok = false;

	JTextField txtUserName = null;
	JTextArea txtUserDesc = null;
	JPasswordField passwd = null;
	JPasswordField confirmPasswd = null;

	String title = null;

	/**
	 * Constructor
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public DlgAddLocalAdmin(Frame owner, String title, boolean modal) {

		super(owner, title, modal, 500, 530);

		this.setTitle(LanguageLoader.getProperty("label.addNewLocalAdmin"));
		this.title = LanguageLoader.getProperty("label.addNewLocalAdmin");

		initGUI();
	}

	public String getUserName() {
		return strUserName;
	}

	public String getUserDesc() {
		return strUserDesc;
	}

	public String getUserPwd() {
		return strUserPwd;
	}

	public void setModifyUserName(String modifiedUserName) {
		txtUserName.setText(modifiedUserName);
		txtUserName.setEditable(false);
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

		String header_role = LanguageLoader.getProperty("label.localAdmin");
		String header_action = title;

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
		pane.setBorder(new EmptyBorder(new Insets(GuiLoader.contentInset + 20,
				0, 0, 0)));

		String title = LanguageLoader.getProperty("label.setProperty");

		pane.setTitle(title);
		pane.setImage(GuiHelper.getIcon("image_user"));

		JPanel contentPane = createPropertyPane();

		pane.add(contentPane);

		return pane;
	}

	private JPanel createPropertyPane() {

		GridBagPanel panel = new GridBagPanel(new Insets(0, 0, 0, 0));

		int widthLong = 252;

		int widthMedium = widthLong / 2;

		int heightLabel = 20;
		int heightTextArea = 80;

		String lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_user_general.user_name");

		GradientLabel lbl1 = GuiHelper.createGradientLabel(lblCaption);
		lbl1.setPreferredSize(new Dimension(widthMedium, heightLabel));

		panel.addComponent(lbl1, 1, 2);

		txtUserName = GuiHelper.createLnFTextField();
		txtUserName
				.setPreferredSize(new Dimension(widthMedium, heightLabel + 4));

		panel.addComponent(txtUserName, 2, 2);

		// insert image label on the left

		lblCaption = "";
		JLabel lblImage = GuiHelper.createLnFLabel(lblCaption);
		ImageIcon icon = GuiHelper.getIcon("image_information");

		lblImage.setIcon(icon);

		lblImage.setPreferredSize(new Dimension(widthMedium / 2, heightLabel));
		panel.addFilledComponent(lblImage, 1, 1, 1, 2, GridBagConstraints.BOTH);

		// insert hidden label for seperation...
		lblCaption = " ";
		JLabel lblHidden = GuiHelper.createLnFLabel(lblCaption);
		lblHidden.setPreferredSize(new Dimension(widthLong, heightLabel));
		panel.addComponent(lblHidden, 3, 2);


		lblCaption = AccCtrlLanguageLoader
				.getProperty("panel_user_general.user_desc");
		GradientLabel lbl2 = GuiHelper.createGradientLabel(lblCaption);
		lbl2.setPreferredSize(new Dimension(widthLong, heightLabel));

		panel.addComponent(lbl2, 4, 2);

		txtUserDesc = GuiHelper.createLnFTextArea();
		JScrollPane scroll = GuiHelper.createLnFScrollPane(txtUserDesc);

		scroll.setPreferredSize(new Dimension(widthLong, heightTextArea));

		panel.addComponent(scroll, 5, 2);

		// insert hidden label for seperation...
		lblCaption = " ";
		JLabel lblHidden2 = GuiHelper.createLnFLabel(lblCaption);
		lblHidden2.setPreferredSize(new Dimension(widthLong, heightLabel));
		panel.addComponent(lblHidden2, 6, 2);

		GridBagPanel authenticatePanel = new GridBagPanel(new Insets(8, 10, 12,
				10));

		Border border = BorderFactory.createLineBorder(GuiLoader.titleColor
				.brighter());

		TitledBorder titledBorder = new TitledBorder(border);
		Font font = new Font("titledBorderFont", Font.ITALIC, 12);
		titledBorder.setTitleColor(GuiLoader.titleColor);
		titledBorder.setTitleFont(font);

		String strAuthenticate = AccCtrlLanguageLoader
				.getProperty("panel_user_general.authentication");
		titledBorder.setTitle(strAuthenticate);

		authenticatePanel.setBorder(titledBorder);

		String strCaptionPasswd = AccCtrlLanguageLoader
				.getProperty("panel_user_general.passwd");
		JLabel lblPasswd = GuiHelper.createLnFLabel(strCaptionPasswd);
		authenticatePanel.addComponent(lblPasswd, 1, 1);

		passwd = GuiHelper.createLnFPasswordField();

		Dimension passwdSize = new Dimension(118, 20);
		passwd.setPreferredSize(passwdSize);
		authenticatePanel.addComponent(passwd, 1, 2);

		String strCaptionConfPasswd = AccCtrlLanguageLoader
				.getProperty("panel_user_general.confirm_passwd");
		JLabel lblConfirmPasswd = GuiHelper
				.createLnFLabel(strCaptionConfPasswd);
		authenticatePanel.addComponent(lblConfirmPasswd, 2, 1);

		confirmPasswd = GuiHelper.createLnFPasswordField();

		confirmPasswd.setPreferredSize(passwdSize);
		authenticatePanel.addComponent(confirmPasswd, 2, 2);

		panel.addComponent(authenticatePanel, 7, 2);

		return panel;

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

	public boolean isOkPressed() {
		return ok;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(strCmdOk)) {
			ok = true;
			strUserName = txtUserName.getText();

			strUserDesc = txtUserDesc.getText();

			strUserPwd = new String(passwd.getPassword());
			String conf_password = new String(confirmPasswd.getPassword());

			if (!strUserPwd.equals(conf_password)) {
				JOptionPane
						.showMessageDialog(null,
								"Please ensure password is the same as confirm passoword");
				return;
			}

			this.dispose();

		} else if (e.getActionCommand().equals(strCmdCancel)) {
			ok = false;
			this.dispose();
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