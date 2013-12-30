/**
 * Created on Aug 14, 2009
 * Author Wang Jinbao
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.RPCCall;



public class SchemaWindow{
	
	public static interface I18nConstants extends Constants {
		String termFilter();
	}	
	
	public Window window;
	
	public SchemaWindow(){
		window = new Window();
		window.setTitle(BestPeerDesktop.constants.termFilter());
		window.setTopToolbar(new Toolbar());
		window.setWidth(550);
		window.setHeight(400);
		TreePanel treePanel = new TreePanel();
		
		treePanel.setWidth(550);
		treePanel.setHeight(350);
		TreeNode root = new TreeNode("database");
		treePanel.setRootNode(root);
		
		ToolbarButton selectBtn = new ToolbarButton("confirm");
		selectBtn.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				System.out.println("confirm");
			}
		});
			
		ToolbarButton cancelBtn = new ToolbarButton("cancel");
		cancelBtn.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(com.gwtext.client.widgets.Button button, EventObject e){
				window.close();
			}
		});
		
		window.getTopToolbar().addButton(selectBtn);
		window.getTopToolbar().addSpacer();
		window.getTopToolbar().addSeparator();
		window.getTopToolbar().addButton(cancelBtn);
		window.add(treePanel);
		window.show();
	}

	
	
}