package sg.edu.nus.gui.customcomponent;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 * 
 * @author dcsvht
 *
 */

public class ProgressDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3352379158342982163L;

	public ProgressDialog(String title) {

		this.setTitle(title);

		Container content = this.getContentPane();

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		content.add(progressBar, BorderLayout.CENTER);

		Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		JLabel westLabel = new JLabel();
		JLabel eastLabel = new JLabel();
		JLabel southLabel = new JLabel();
		JLabel northLabel = new JLabel();

		westLabel.setBorder(emptyBorder);
		eastLabel.setBorder(emptyBorder);
		southLabel.setBorder(emptyBorder);
		northLabel.setBorder(emptyBorder);

		content.add(westLabel, BorderLayout.WEST);
		content.add(eastLabel, BorderLayout.EAST);
		content.add(northLabel, BorderLayout.NORTH);
		content.add(southLabel, BorderLayout.SOUTH);

		setSize(300, 80);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		this.setLocation(x, y);

		// should not do this, because it will get focus of parent window, can
		// not show result...
		// this.setModal(true);

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

	}

	public static void main(String args[]) {
		final ProgressDialog dlg = new ProgressDialog("Processing query...");

		JFrame f = new JFrame("JProgressBar Sample");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();

		JButton start = new JButton("Start");
		JButton stop = new JButton("Stop");

		start.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(true);

			}
		});

		stop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(false);

			}
		});

		content.add(start, BorderLayout.NORTH);
		content.add(stop, BorderLayout.SOUTH);

		f.setSize(300, 100);
		f.setVisible(true);

	}
}
