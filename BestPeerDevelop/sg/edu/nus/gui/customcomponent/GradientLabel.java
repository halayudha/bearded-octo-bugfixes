package sg.edu.nus.gui.customcomponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import sg.edu.nus.gui.GuiLoader;

public class GradientLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -974226036895839883L;
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;
	public static final int VERTICAL_RAISED = 2;
	public static final int HORIZONTAL_RAISED = 3;

	private int direction = VERTICAL;

	private Color startColor = new Color(255, 255, 195);

	private Color endColor = new Color(255, 255, 255);

	public GradientLabel(String text) {
		super(text);
		myInit();
	}

	private void myInit() {
		startColor = GuiLoader.labelStartColor;
		endColor = GuiLoader.labelEndColor;
		int inset_vert = 3;
		int inset_horz = 8;
		setBorder(new EmptyBorder(new Insets(inset_vert, inset_horz,
				inset_vert, inset_horz)));
	}

	public void setStartColor(Color color) {
		startColor = color;
	}

	public void setStartColorDarker() {
		startColor = startColor.darker();
	}

	public void setEndColorDarker() {
		endColor = endColor.darker();
	}

	/**
	 * Set the direction
	 *
	 * @param aDirection The direction of the gradient
	 */
	public void setDirection(int aDirection) {
		int oldDirection = direction;
		direction = aDirection;
		super.firePropertyChange("direction", oldDirection, aDirection);
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		int h = getHeight();
		int w = getWidth();

		GradientPaint GP = null;

		if (direction == VERTICAL) {
			GP = new GradientPaint(0, 0, startColor, 0, h, endColor, true);
		} else {// horizontal
			GP = new GradientPaint(0, 0, startColor, w, 0, endColor, true);
		}

		g2d.setPaint(GP);

		g2d.fillRect(0, 0, w, h);

		super.paintComponent(g);
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("GradientLabelDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new GradientLabel("Smiling"), BorderLayout.SOUTH);

		// Display the window.
		frame.setSize(300, 400);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println("Error setting native LAF: " + e);
				}
				createAndShowGUI();
			}
		});
	}

}