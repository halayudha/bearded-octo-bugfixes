package sg.edu.nus.gui.customcomponent;

import javax.swing.*;

import sg.edu.nus.gui.GuiLoader;

import java.awt.*;

/**
 * Helper class for GridBagLayout JPanel.
 * 
 * @author VHTam
 * @version 1.0 2008-07-08
 */

public class GridBagPanel extends JPanel

{
	private static final long serialVersionUID = 7357219946142955418L;

	private GridBagConstraints constraints;

	// default constraints value definitions

	private static final int C_HORZ = GridBagConstraints.HORIZONTAL;

	private static final int C_NONE = GridBagConstraints.NONE;

	private static final int C_WEST = GridBagConstraints.WEST;

	private static final int C_WIDTH = 1;

	private static final int C_HEIGHT = 1;

	// Create a GridBagLayout panel using a default insets constraint.

	public GridBagPanel() {

		super(new GridBagLayout());

		myInit();

	}

	// Create a GridBagLayout panel using the specified insets

	// constraint.

	public GridBagPanel(Insets insets) {

		super(new GridBagLayout());

		myInit(insets);

	}

	private void myInit() {

		int inset = GuiLoader.contentGridBagInset;

		Insets insets = new Insets(inset, inset, inset, inset);

		constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;

		constraints.insets = insets;

		setBackground(GuiLoader.contentBkColor);

	}

	private void myInit(Insets insets) {

		constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;

		constraints.insets = insets;

		setBackground(GuiLoader.contentBkColor);

	}

	// Add a component to specified row and col.

	public void addComponent(JComponent component, int row, int col) {

		addComponent(component, row, col, C_WIDTH,

		C_HEIGHT, C_WEST, C_NONE);

	}

	// Add component to specified row and col, spanning across

	// a specified number of columns and rows.

	public void addComponent(JComponent component, int row, int col,

	int width, int height) {

		addComponent(component, row, col, width,

		height, C_WEST, C_NONE);

	}

	// Add component to specified row and col, using a specified

	// anchor constraint

	public void addAnchoredComponent(JComponent component, int row,

	int col, int anchor) {

		addComponent(component, row, col, C_WIDTH,

		C_HEIGHT, anchor, C_NONE);

	}

	// Add component to specified row and col, spanning across

	// a specified number of columns and rows, using a specified

	// anchor constraint

	public void addAnchoredComponent(JComponent component,

	int row, int col, int width, int height, int anchor) {

		addComponent(component, row, col, width,

		height, anchor, C_NONE);

	}

	// Add component to specified row and col

	// filling the column horizontally.

	public void addFilledComponent(JComponent component,

	int row, int col) {

		addComponent(component, row, col, C_WIDTH,

		C_HEIGHT, C_WEST, C_HORZ);

	}

	// Add component to the specified row and col

	// with the specified fill constraint.

	public void addFilledComponent(JComponent component,

	int row, int col, int fill) {

		addComponent(component, row, col, C_WIDTH,

		C_HEIGHT, C_WEST, fill);

	}

	// Add component to the specified row and col,

	// spanning a specified number of columns and rows,

	// with specified fill constraint

	public void addFilledComponent(JComponent component,

	int row, int col, int width, int height, int fill) {

		addComponent(component, row, col, width, height, C_WEST, fill);

	}

	// Add component to the specified row and col,

	// spanning specified number of columns and rows, with

	// specified fill and anchor constraints

	public void addComponent(JComponent component,

	int row, int col, int width, int height, int anchor, int fill) {

		constraints.gridx = col;

		constraints.gridy = row;

		constraints.gridwidth = width;

		constraints.gridheight = height;

		constraints.anchor = anchor;

		double weightx = 0.0;

		double weighty = 0.0;

		// only use extra horizontal or vertical space if component

		// spans more than one column and/or row.

		if (width > 1)

			weightx = 1.0;

		if (height > 1)

			weighty = 1.0;

		switch (fill)

		{

		case GridBagConstraints.HORIZONTAL:

			constraints.weightx = weightx;

			constraints.weighty = 0.0;

			break;

		case GridBagConstraints.VERTICAL:

			constraints.weighty = weighty;

			constraints.weightx = 0.0;

			break;

		case GridBagConstraints.BOTH:

			constraints.weightx = weightx;

			constraints.weighty = weighty;

			break;

		case GridBagConstraints.NONE:

			constraints.weightx = 0.0;

			constraints.weighty = 0.0;

			break;

		default:

			break;

		}

		constraints.fill = fill;

		add(component, constraints);

	}

}
