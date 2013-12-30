/**
 * Created on Apr 26, 2009
 */
package sg.edu.nus.ui.client;

import com.gwtext.client.data.Node;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;
import com.gwtext.client.core.EventObject;

/**
 * @author David Jiang
 * 
 */
public class ScreenManager {
	private TabPanel appTabPanel;
	private static Panel DataMappingTab;

	private Panel stackView = new Panel();
	public ScreenManager(TabPanel tabPanel) {
		appTabPanel = tabPanel;
	}

	public void setDisabled(boolean bDisabled) {
		if(stackView != null)
			stackView.setDisabled(bDisabled);
	}
	
	public Panel getStackView() {
		stackView.setLayout(new AccordionLayout(false));

		Store store = UIRepository.getStore();

		Record[] records = store.getRecords();
		for (int i = 0; i < records.length; ++i) {
			Record record = records[i];
			String id = record.getAsString("id");
			final String category = record.getAsString("category");
			String title = record.getAsString("title");
			String iconCls = record.getAsString("iconCls");
			final BestPeerPanel panel = (BestPeerPanel) record.getAsObject("uiobject");

			if (category == null) { // Create a new category
				Panel categoryPanel = new Panel();
				categoryPanel.setAutoScroll(true);
				categoryPanel.setLayout(new FitLayout());
				categoryPanel.setId(id + "-cat");
				categoryPanel.setTitle(title);
				if(title.equals("Data Mapping")){
					DataMappingTab = categoryPanel;
				}
				categoryPanel.setIconCls("query-panel-icon");
				stackView.add(categoryPanel);
			} else { // Add to existing category
				Panel categoryPanel = (Panel) stackView.findByID(category
						+ "-cat");
				TreePanel treePanel = (TreePanel) categoryPanel
						.findByID(category + "-cat-tree");
				TreeNode root = null;
				if (treePanel == null) {
					treePanel = new TreePanel();
					treePanel.setAutoScroll(true);
					treePanel.setId(category + "-cat-tree");
					treePanel.setRootVisible(false);
					root = new TreeNode();
					treePanel.setRootNode(root);
					categoryPanel.add(treePanel);
				} else {
					root = treePanel.getRootNode();
				}

				TreeNode node = new TreeNode();
				node.setText(title);
				node.setId(id);
				if(iconCls != null)
					node.setIconCls(iconCls);
				root.appendChild(node);
				addNodeClickListener(node, panel);
			}
		}
		stackView.setDisabled(true);
		return stackView;
	}

	private void addNodeClickListener(TreeNode node, final Panel panel) {
		if (panel != null) {
			panel.setIconCls(node.getIconCls());
			node.addListener(new TreeNodeListenerAdapter() {
				public void onClick(Node node, EventObject e) {
					String panelID = panel.getId();
					if (appTabPanel.hasItem(panelID)) {
						showScreen(panel, null, node.getId());
					} else {
						TreeNode treeNode = (TreeNode) node;
						panel.setTitle(treeNode.getText());
						showScreen(panel, treeNode.getText(), node.getId());
					}
				}
			});
		}
	}

	public void showScreen(Panel panel, String title, String screenName) {
		String panelID = panel.getId();
		if (appTabPanel.hasItem(panelID)) {
			appTabPanel.scrollToTab(panel, true);
			appTabPanel.activate(panelID);
		} else {
			if (!panel.isRendered()) {
				panel.setTitle(title);
			}
			appTabPanel.add(panel);
			appTabPanel.activate(panel.getId());
		}
	}
	
	public static void disableDataMappingGrid(){
		DataMappingTab.disable();
	}
	
	public static Panel getDataMappingGrid(){
		return DataMappingTab;
	}
	
}
