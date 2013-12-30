package sg.edu.nus.gui.customcomponent;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import sg.edu.nus.gui.GuiLoader;

/**
 * 
 * UserInputsPanel.java
 * <p/>
 * An example of for  custom  JPanel.
 * @author JDhilsukh
 *
 */

public class HeaderFooterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3544354627612800273L;
	private Color topStartColor = new Color(238, 238, 238);
	private Color topEndColor = new Color(255, 255, 255);

	// VHTam
	private Color bottomStartColor = new Color(220, 220, 220);
	private Color bottomEndColor = new Color(236, 236, 236);

	ImageIcon image = null;
	// end

	GeneralPath path;

	Color accentColor = new Color(0x80ffffff);
	Color textColor = new Color(0x0f0f0f);
	String title = "SampleTitle";

	public HeaderFooterPanel() {
		super();

		myInit();
	}

	public HeaderFooterPanel(Color color1, Color color2) {
		super();

		topStartColor = color1;
		topEndColor = color2;

		myInit();
	}

	// VHTam
	private void myInit() {
		int inset = GuiLoader.contentInset;
		this.setBorder(new EmptyBorder(new Insets(inset, inset, inset, inset)));
		topStartColor = GuiLoader.topStartColor;
		topEndColor = GuiLoader.topEndColor;
		bottomStartColor = GuiLoader.bottomStartColor;
		bottomEndColor = GuiLoader.bottomEndColor;

		this.setBackground(GuiLoader.themeBkColor);
	}

	/**
	 * Override the default paintComponent method to paint the gradient in the
	 * panel.
	 * 
	 * @param g
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		int h = getHeight();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		/** 
		 * Top Polygon
		 */
		GeneralPath path = new GeneralPath();
		path.moveTo(70, 0);
		path.lineTo(8, 0);
		path.quadTo(0, 0, 0, 7);
		path.lineTo(0, 55);
		path.lineTo(getWidth() - 1, 55);
		path.lineTo(getWidth() - 1, 7);
		path.quadTo(getWidth() - 1, 0, getWidth() - 8, 0);
		path.lineTo(30, 0);

		Rectangle bounds1 = path.getBounds();
		GradientPaint painter = new GradientPaint(0, path.getBounds().y,
				true ? topEndColor : topStartColor, 0, bounds1.y
						+ bounds1.height - 1, true ? topStartColor
						: topEndColor);
		g2d.setPaint(painter);
		g2d.fill(path);
		Rectangle rectangle = new Rectangle(0, 40, getWidth(), 20);
		g2d.fill(rectangle);

		// VHTam
		g2d.setColor(GuiLoader.contentLineColor);

		g2d.draw(path);

		/**
		 * Middle Rectangle 
		 */
		// VHTam
		g2d.setPaint(GuiLoader.contentBkColor);
		// end VHTam

		g2d.fillRect(0, 31, getWidth() - 1, h - 50);

		// VHTam
		g2d.setColor(GuiLoader.contentLineColor);
		//

		g2d.drawLine(12, 0, getWidth() - 10, 0);
		g2d.drawRect(0, 30, getWidth() - 1, h - 50);

		/**
		 * Bottom Polygon
		 */
		h = h - 30;
		path = new GeneralPath();
		path.moveTo(0, h);
		path.lineTo(0, h + 22);
		path.quadTo(0, h + 29, 8, h + 29);
		path.lineTo(getWidth() - 8, h + 29);
		path.quadTo(getWidth() - 1, h + 29, getWidth() - 1, h + 22);
		path.lineTo(getWidth() - 1, h);
		g2d.setColor(Color.GRAY);

		bounds1 = path.getBounds();
		painter = new GradientPaint(0, path.getBounds().y, bottomEndColor, 0,
				bounds1.y + bounds1.height - 1, bottomStartColor);
		g2d.setPaint(painter);
		g2d.fill(path);

		// VHTam
		g2d.setColor(GuiLoader.contentLineColor);
		//

		g2d.draw(path);

		// VHTam
		g2d.setColor(GuiLoader.contentLineColor);
		//

		g2d.drawLine(0, h - 1, getWidth() - 1, h - 1);

		/**
		 *  Title
		 */
		// VHTam
		// to support unicode
		g2d.setFont(new Font("labelFont", Font.BOLD, 13));
		// end

		g2d.setColor(accentColor);
		g2d.drawString(title, 40, 22);
		g2d.setColor(textColor);
		g2d.drawString(title, 40, 21);

		/**
		 *  image
		 */
		// VHTam
		if (image != null) {
			g2d.drawImage(image.getImage(), 12, 7, null, null);
		}

		// end
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	/**
	 *  This method sets the Actual Background Color of the Button
	 */
	public void setStartColor(Color color) {
		topStartColor = color;
	}

	/**
	 *  This method sets the Pressed Color of the Button
	 */
	public void setEndColor(Color pressedColor) {
		topEndColor = pressedColor;
	}

	/**
	 * @return  Starting Color of the Button
	 */
	public Color getStartColor() {
		return topStartColor;
	}

	/**
	 * @return  Ending Color of the Button
	 */
	public Color getEndColor() {
		return topEndColor;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		// JPanel pane = new JPanel();
		HeaderFooterPanel pane = new HeaderFooterPanel();
		pane.setBorder(new EmptyBorder(new Insets(GuiLoader.contentInset + 40,
				0, 0, 0)));

		String title = "Setting column properties";
		pane.setTitle(title);

		frame.add(pane);

		// Display the window.
		frame.setSize(400, 600);
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