package sg.edu.nus.gui.customcomponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import sg.edu.nus.gui.GuiLoader;

/**
 * GradientOvalButton.java
 * @author JDhilsukh
 *
 */
public class GradientOvalButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7205905200022680500L;
	private Color startColor = new Color(255, 255, 255);
	private Color endColor = new Color(82, 82, 82);
	private Color rollOverColor = new Color(255, 91, 13);
	private Color pressedColor = new Color(206, 67, 0);
	private Color textColor = new Color(255, 255, 255);

	private static final int HORIZONTAL = 0;
	private static final int VERTICAL = 1;
	private static final int CENTRAL = 2;
	private static final int NORMAL = 3;
	private int direction = VERTICAL;
	private int gradientType = CENTRAL;
	private int outerRoundRectSize = 30;
	private int innerRoundRectSize = 28;
	private GradientPaint GP;

	/**
	 * 
	 * @param text
	 */
	public GradientOvalButton(String text) {
		super();
		setText(text);
		setContentAreaFilled(false);
		setBorderPainted(false);

		setFont(new Font("button", Font.BOLD, 12));

		setForeground(Color.WHITE);
		setFocusable(false);

		myInit();
	}

	private void myInit() {

		int sideInset = GuiLoader.buttonInset;
		this.setBorder(new EmptyBorder(new Insets(4, sideInset, 4, sideInset)));

		startColor = GuiLoader.buttonStartColor;
		endColor = GuiLoader.buttonEndColor;
		rollOverColor = GuiLoader.buttonRollOverColor;
		pressedColor = GuiLoader.buttonPressedColor;
		textColor = GuiLoader.buttonTextColor;
	}

	public GradientOvalButton(String text, Color startColor, Color endColor,
			Color rollOverColor, Color pressedColor, int adirection,
			int agradientType) {
		super();
		this.startColor = startColor;
		this.endColor = endColor;
		this.rollOverColor = rollOverColor;
		this.pressedColor = pressedColor;
		direction = adirection;
		gradientType = agradientType;
		setForeground(Color.WHITE);
		setFocusable(false);
		setContentAreaFilled(false);
		setBorderPainted(false);

		myInit();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int h = getHeight();
		int w = getWidth();
		ButtonModel model = getModel();
		if (!model.isEnabled()) {
			setForeground(Color.GRAY);
			GP = new GradientPaint(0, 0, new Color(192, 192, 192), 0, h,
					new Color(192, 192, 192), true);
		} else {
			// VHTam
			setForeground(textColor);
			//
			if (model.isRollover()) {
				if (direction == VERTICAL && gradientType == CENTRAL) {
					GP = new GradientPaint(0, 0, startColor, 0, h / 2,
							rollOverColor, true);
				} else if (direction == VERTICAL && gradientType == NORMAL) {
					GP = new GradientPaint(0, 0, startColor, 0, h,
							rollOverColor, true);
				} else if (direction == HORIZONTAL && gradientType == CENTRAL) {
					GP = new GradientPaint(0, 0, startColor, w / 2, 0,
							rollOverColor, true);
				} else if (direction == HORIZONTAL && gradientType == NORMAL) {
					GP = new GradientPaint(0, 0, startColor, w, 0,
							rollOverColor, true);
				}

			} else {
				if (direction == VERTICAL && gradientType == CENTRAL) {
					GP = new GradientPaint(0, 0, startColor, 0, h / 2,
							endColor, true);
				} else if (direction == VERTICAL && gradientType == NORMAL) {
					GP = new GradientPaint(0, 0, startColor, 0, h, endColor,
							true);
				} else if (direction == HORIZONTAL && gradientType == CENTRAL) {
					GP = new GradientPaint(0, 0, startColor, w / 2, 0,
							endColor, true);
				} else if (direction == HORIZONTAL && gradientType == NORMAL) {
					GP = new GradientPaint(0, 0, startColor, w, 0, endColor,
							true);
				}
			}
		}
		g2d.setPaint(GP);
		GradientPaint p1;
		GradientPaint p2;
		if (model.isPressed()) {

			if (direction == VERTICAL && gradientType == CENTRAL) {
				GP = new GradientPaint(0, 0, startColor, 0, h / 2,
						pressedColor, true);
			} else if (direction == VERTICAL && gradientType == NORMAL) {
				GP = new GradientPaint(0, 0, startColor, 0, h, pressedColor,
						true);
			} else if (direction == HORIZONTAL && gradientType == CENTRAL) {
				GP = new GradientPaint(0, 0, startColor, w / 2, 0,
						pressedColor, true);
			} else if (direction == HORIZONTAL && gradientType == NORMAL) {
				GP = new GradientPaint(0, 0, startColor, w, 0, pressedColor,
						true);
			}

			g2d.setPaint(GP);

			p1 = new GradientPaint(0, 0, new Color(0, 0, 0), 0, h - 1,
					new Color(100, 100, 100));
			p2 = new GradientPaint(0, 1, new Color(0, 0, 0, 50), 0, h - 3,
					new Color(255, 255, 255, 100));
		} else {
			p1 = new GradientPaint(0, 0, new Color(100, 100, 100), 0, h - 1,
					new Color(0, 0, 0));
			p2 = new GradientPaint(0, 1, new Color(255, 255, 255, 100), 0,
					h - 3, new Color(0, 0, 0, 50));
			GP = new GradientPaint(0, 0, startColor, 0, h, endColor, true);
		}
		RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, w - 1,
				h - 1, outerRoundRectSize, outerRoundRectSize);
		Shape clip = g2d.getClip();
		g2d.clip(r2d);
		g2d.fillRect(0, 0, w, h);
		g2d.setClip(clip);
		g2d.setPaint(p1);

		// VHTam
		g2d.setColor(endColor);
		//

		g2d.drawRoundRect(0, 0, w - 1, h - 1, outerRoundRectSize,
				outerRoundRectSize);
		g2d.setPaint(p2);
		g2d.drawRoundRect(1, 1, w - 3, h - 3, innerRoundRectSize,
				innerRoundRectSize);
		g2d.dispose();

		super.paintComponent(g);
	}

	/**
	 *  This method sets the Actual Background Color of the Button
	 */
	public void setStartColor(Color color) {
		startColor = color;
	}

	/**
	 *  This method sets the Pressed Color of the Button
	 */
	public void setEndColor(Color pressedColor) {
		endColor = pressedColor;
	}

	/**
	 * @return  Starting Color of the Button
	 */
	public Color getStartColor() {
		return startColor;
	}

	/**
	 * @return  Ending Color of the Button
	 */
	public Color getEndColor() {
		return endColor;
	}

	public static void main(String args[]) {
		JFrame frame = new JFrame("Custom Panels Demo");
		frame.setLayout(new FlowLayout());

		GradientOvalButton standardButton = new GradientOvalButton(
				"Standard Button");

		standardButton.setDirection(GradientOvalButton.VERTICAL);
		standardButton.setGradientType(GradientOvalButton.NORMAL);
		standardButton.setPreferredSize(new Dimension(130, 28));

		frame.add(standardButton.getButtonsPanel());

		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBackground(Color.WHITE);
		frame.setSize(700, 85);
		frame.setVisible(true);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getGradientType() {
		return gradientType;
	}

	public void setGradientType(int gradientType) {
		this.gradientType = gradientType;
	}

	public JPanel getButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		GradientOvalButton standardButton = new GradientOvalButton(
				"Standard Button");
		standardButton.setPreferredSize(new Dimension(130, 28));
		standardButton.setDirection(GradientOvalButton.VERTICAL);
		standardButton.setGradientType(GradientOvalButton.NORMAL);

		GradientOvalButton rollOverButton = new GradientOvalButton(
				"RollOver Button");
		rollOverButton.setPreferredSize(new Dimension(130, 28));
		rollOverButton.setDirection(GradientOvalButton.VERTICAL);
		rollOverButton.setGradientType(GradientOvalButton.NORMAL);

		GradientOvalButton disabledButton = new GradientOvalButton(
				"Disable Button");
		disabledButton.setPreferredSize(new Dimension(130, 28));
		disabledButton.setEnabled(false);

		GradientOvalButton pressedButton = new GradientOvalButton(
				"Pressed Button");
		pressedButton.setPreferredSize(new Dimension(130, 28));
		pressedButton.setDirection(GradientOvalButton.VERTICAL);
		pressedButton.setGradientType(GradientOvalButton.NORMAL);

		panel.add(standardButton);
		panel.add(rollOverButton);
		panel.add(disabledButton);
		panel.add(pressedButton);
		return panel;

	}

}
