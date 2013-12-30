package sg.edu.nus.gui.dbview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class ExportDBGlobalTreeCellRenderer implements TreeCellRenderer {

	private JCheckBox leafRenderer = new JCheckBox();

	private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

	Color selectionBorderColor, selectionForeground, selectionBackground,
			textForeground, textBackground;

	protected JCheckBox getLeafRenderer() {
		return leafRenderer;
	}

	public ExportDBGlobalTreeCellRenderer() {
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");
		if (fontValue != null) {
			leafRenderer.setFont(fontValue);
		}
		Boolean booleanValue = (Boolean) UIManager
				.get("Tree.drawsFocusBorderAroundIcon");
		leafRenderer.setFocusPainted((booleanValue != null)
				&& (booleanValue.booleanValue()));

		selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component returnValue;

		DBTreeNode node = (DBTreeNode) value;

		if (!node.getNodeType().equals("DATABASE")) {
			leafRenderer.setEnabled(tree.isEnabled());
			leafRenderer.setText(node.getSourceSchemaName());
			leafRenderer.setSelected(node.isSelected());

			if (node.isHasInsertIndex()) {
				leafRenderer.setForeground(Color.GREEN);

				if (selected) {
					leafRenderer.setBackground(selectionBackground);
				} else {
					leafRenderer.setBackground(textBackground);
				}
			} else {
				leafRenderer.setForeground(Color.BLACK);

				if (selected) {
					leafRenderer.setBackground(selectionBackground);
				} else {
					leafRenderer.setBackground(textBackground);
				}
			}

			returnValue = leafRenderer;
		} else {
			returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
					value, selected, expanded, leaf, row, hasFocus);
		}

		tree.repaint();

		return returnValue;
	}
}