/*
 * @(#) DBTreeCellRenderer.java 1.0 2007-1-3
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Implement customize <code>SchemaMappingDBTreeCellRenderer</code> that is to render each cell
 * in the <code>DBTree</code>.
 * 
 * @author Han Xixian
 * @version 1.0 2008-07-12
 * @see javax.swing.tree.DefaultTreeCellRenderer
 */

public class GlobalSchemaTreeCellRenderer extends DefaultTreeCellRenderer {
	// private members
	private static final long serialVersionUID = -4977292375250536149L;

	/**
	 * Rewrite the <code>getTreeCellRendererComponent</code> function in the DefaultTreeCellRenderer class
	 * to set the custemized image for different kinds of tree node in the <code>DBTree</code>.
	 * 
	 * @return the Component that the renderer uses to draw the value 
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded,
				leaf, row, hasFocus);

		return this;
	}
}
