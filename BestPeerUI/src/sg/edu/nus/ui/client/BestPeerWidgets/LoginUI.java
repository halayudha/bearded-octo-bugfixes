/**
 * Created on Apr 28, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.TimeField;
import com.gwtext.client.widgets.layout.FitLayout;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.ScreenManager;
import sg.edu.nus.ui.client.UIRepository;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.PropertyGridPanel;
import sg.edu.nus.ui.client.ScreenManager;
/**
 * @author David Jiang
 *
 */
public class LoginUI extends Panel {
	public static interface I18nConstants extends Constants {
		String loginTitle();
		String loginPrompt();
		String login();
		String loginUserName();
		String loginPassword();
		String loginWaitingText();
		String loginFailText();
	}
	private ScreenManager screenManager;
	private Panel panel = null;
	GridPanel grid = new GridPanel();
	private FormPanel form = null;
	private TextField userField = null;
	private TextField passField = null;
	
	private static String userName = null;
	private boolean loggin = false;
	
	public static String getUserName(){
		return userName;
	}
	
	private static String userType = null;
	
	public static String getUserType(){
		return userType;
	}
	
	public LoginUI(ScreenManager screenManager) {
		this.screenManager = screenManager;
		setBorder(false);
		setLayout(new FitLayout());
		setAutoScroll(true);
	}
	
	
	@Override
	protected void afterRender() {
		System.out.println("run afterRender()");
		if(!loggin)
			add(getViewPanel());
		else{
			clear();
			Panel outer = new Panel();
			outer.setPaddings(20);
			outer.setFrame(false);
			outer.setSize(800, 600);
			grid.setSize(400, 400);
			outer.add(grid);
			add(outer);
			doLayout();
		}
	}

	private class LoginAdapter extends ButtonListenerAdapter implements Callback {

		@Override
		public void onClick(Button button, EventObject e) {
			System.err.println("User: " + userField.getText() + " Pass: " + passField.getText());
			JSONObject params = new JSONObject();
			
			userName = userField.getText();
			
			params.put("user", new JSONString(userField.getText()));
			params.put("password", new JSONString(passField.getText()));
			new RPCCall().invoke("LoginService", params, this);
			MessageBox.show(new MessageBoxConfig() {
				{
					setMsg(BestPeerDesktop.constants.loginWaitingText());
					
					setWidth(300);
					setWait(true);
					setWaitConfig(new WaitConfig() {
						{
							setInterval(200);
						}
					});
				}
			});
			super.onClick(button, e);
		}

		@Override
		public void onFailure() {
			// TODO Auto-generated method stub
			MessageBox.hide();
			form.setTitle(BestPeerDesktop.constants.loginFailText());
		}

		@Override
		public void onReady(JSONObject resultSet) {
			// TODO Auto-generated method stub
			String ack = ((JSONString)resultSet.get("loginack")).stringValue();
			
			userType = ((JSONString)resultSet.get("usertype")).stringValue();
			ScreenManager.getDataMappingGrid().setVisible(false);
			if(userType.equals("physician")){
				ScreenManager.getDataMappingGrid().setVisible(true);
			}
			MessageBox.hide();
			BestPeerDesktop.showLeftPanel();			
			
			if(ack.equals("ok")){
				loggin = true;
				grid.setTitle("Login Success", "login-succ-icon");
				grid.setFrame(true);
				grid.setWidth(400);
				grid.setHeight(350);
				grid.stripeRows(true);
				
				ColumnModel columnModel = new ColumnModel(new ColumnConfig[] {
						new ColumnConfig("User Info", "name", 120, true),
						new ColumnConfig("", "value", 180, true)});
				grid.setColumnModel(columnModel);
				
				Store store = 
					new SimpleStore(new String[] {"name", "value"}, 
							new Object[][]{
								new Object[] {"User Name", userName},
								new Object[] {"User Type", userType},
								new Object[] {"Login Time", new Date()},
								new Object[] {"BestPeer Version", "v1.0"}
							});
				store.load();
				grid.setStore(store);
				screenManager.setDisabled(false);
				afterRender();
				if (userType.equals("normal")){
				}
			}
			else {
				form.setTitle(BestPeerDesktop.constants.loginFailText());
				userField.setValue("");
				passField.setValue("");
			}
		}
		
	}
	//@Override
	public Panel getViewPanel() {
		System.out.println("run getViewPanel()");
		if(panel == null){
			if(!loggin){
				panel = new Panel();
				panel.setBorder(false);
				panel.setPaddings(10);
				form = new FormPanel();
				form.setTitle(BestPeerDesktop.constants.loginPrompt());
				form.setFrame(true);
				form.setPaddings(5, 5, 5, 0);
				form.setWidth(300);
				form.setLabelWidth(80);
				
				FieldSet fsSet = new FieldSet();
				fsSet.setFrame(true);
				fsSet.setTitle("");
				userField = new TextField(BestPeerDesktop.constants.loginUserName(), "username", 160);
				userField.setLabelSeparator("");
				passField = new TextField(BestPeerDesktop.constants.loginPassword(), "password", 160);
				passField.setPassword(true);
				passField.setLabelSeparator("");
				fsSet.add(userField);
				fsSet.add(passField);
			
				Button button = new Button(BestPeerDesktop.constants.login());
				button.addListener(new LoginAdapter());
				form.add(fsSet);
				form.addButton(button);
				panel.add(form);
				Panel tempPanel = BestPeerDesktop.getRightPanel();
				panel.setPaddings(130, (tempPanel.getWidth()/3), 0, 0);
			}
			else{
				
			}			
		}		
		return panel;
	}

}
