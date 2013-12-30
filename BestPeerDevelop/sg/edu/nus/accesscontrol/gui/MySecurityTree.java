package sg.edu.nus.accesscontrol.gui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import sg.edu.nus.gui.GuiHelper;
import sg.edu.nus.gui.GuiLoader;

public class MySecurityTree extends JTree implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8804751543043209721L;
	public static final String USER = "User";
	public static final String ROLE = "Role";

	PanelAccessControlManagement parentPane = null;

	public MySecurityTree() {
		// add customized TreeCellRenderer
		setCellRenderer(new TreeCellRenderer());

		addTreeSelectionListener(this);
	}

	public void setParent(PanelAccessControlManagement parentPane) {
		this.parentPane = parentPane;
	}

	DefaultMutableTreeNode root = null;
	String[][] data = null;
	int nodeIndex = 0;

	public void setRootNode(DefaultMutableTreeNode root) {
		this.root = root;
	}

	public void setNumberFirstLevelChild(int n) {
		data = new String[n][];
		nodeIndex = 0;
	}

	public void buildTree() {
		setupTree(data, root);
	}

	public void setFirstLevelChildData(String nodeName, String[] nodeData) {

		data[nodeIndex] = new String[nodeData.length + 1];

		data[nodeIndex][0] = nodeName;

		for (int i = 1; i <= nodeData.length; i++) {
			data[nodeIndex][i] = nodeData[i - 1];
		}

		nodeIndex++;
	}

	public void setupTree(Object[][] data, DefaultMutableTreeNode root) {

		ObjectNode objNode;// object in considering: for e.g Role or User

		TableNode tableNode;// specific name of role or user

		for (int i = 0; i < data.length; i++) {
			objNode = new ObjectNode((String) data[i][0]);

			String type = null;

			if (i == 0) {
				type = ROLE;
			} else {
				type = USER;
			}

			objNode.type = type;

			root.add(objNode);

			for (int j = 1; j < data[i].length; j++) {
				tableNode = new TableNode((String) data[i][j]);
				tableNode.type = type;

				objNode.add(tableNode);

			}

		}

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);

	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = this.getSelectionPath();
		if (path == null)
			return;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();

		if (node.toString().contains("TableNode")) {
			
			if (parentPane != null) {
				parentPane.updatePanel(((TableNode) node).type,
						((TableNode) node).getName());
			}
		}
	}

	class ObjectNode extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = -617801020103856373L;

		String name;

		String type = null;

		public ObjectNode(String nodeName) {
			super(nodeName);
			this.name = nodeName;

		}

		public String getName() {
			return name;
		}

		public void setName(String nodeName) {
			this.name = nodeName;
		}

		public String toString() {
			return "ObjectNode";
		}

	}

	class TableNode extends DefaultMutableTreeNode {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7632897467777494483L;

		// the name of the table
		String tableName;

		String type = null;

		/**
		 * Constructor
		 * 
		 * @param tableName -  the name of the table
		 */
		public TableNode(String tableName) {
			super(tableName);
			this.tableName = tableName;
		}

		/**
		 * Return the name of the table
		 * 
		 * @return name of table
		 */
		public String getName() {
			return tableName;
		}

		/**
		 * Set the string value as the name of table
		 * 
		 * @param tableName - the name of table
		 */
		public void setName(String tableName) {
			this.tableName = tableName;
		}

		/**
		 * Return a string: "TableNode".
		 * 
		 * @return "TableNode"
		 */
		public String toString() {
			return "TableNode";
		}
	}

	class TreeCellRenderer extends DefaultTreeCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1661007867289825114L;

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

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			String imgId = null;

			this.setTextNonSelectionColor(GuiLoader.contentLineColor);

			this.setBackgroundSelectionColor(GuiLoader.selectionBkColor);
			this.setTextSelectionColor(GuiLoader.contentTextColor);
			this.setBorderSelectionColor(GuiLoader.contentTextColor);
			this.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

			if (node.toString().equals("TableNode")) {
				imgId = ("image_leaf_node");
				this.setText(((TableNode) node).getName());
			} else if (value.toString().equals("ObjectNode")) {
				String name = ((ObjectNode) node).type;
				if (name.indexOf(ROLE) != -1) {
					imgId = "image_role";
				} else if (name.indexOf(USER) != -1) {
					imgId = "image_user";
				} else {
					imgId = ("image_object_node");
				}
				this.setText(((ObjectNode) node).getName());
			} else {
				imgId = ("image_security");
			}

			ImageIcon icon = GuiHelper.getIcon(imgId);

			// set the image icon to render the node

			this.setIcon(icon);

			return this;
		}
	}

}
