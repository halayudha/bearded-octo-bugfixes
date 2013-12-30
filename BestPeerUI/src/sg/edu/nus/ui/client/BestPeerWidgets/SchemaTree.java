/**
 * Created on Apr 27, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONArray;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author David Jiang
 *
 */
public class SchemaTree extends TreePanel {
	private class TreePanelListener extends TreePanelListenerAdapter {
		@Override
		public void onClick(TreeNode node, EventObject e) {
			curNode = node;
			super.onClick(node, e);
		}
	}
	private TreeNode curNode = null;
	public TreeNode getSelectedNode() { return curNode; }
	public boolean isRootNode(TreeNode node) {
		TreeNode root = getRootNode();
		if(root.getId().equalsIgnoreCase(node.getId()))
			return true;
		return false;
	}
	
	public void loadSchema(String schemaName, Callback callback) {
		JSONObject params = new JSONObject();
		params.put("schema", new JSONString(schemaName));
		RPCCall.get().invoke("SchemaService", params, callback);
		
	}	
	
	public SchemaTree() {
		addListener(new TreePanelListener());
	}

}
