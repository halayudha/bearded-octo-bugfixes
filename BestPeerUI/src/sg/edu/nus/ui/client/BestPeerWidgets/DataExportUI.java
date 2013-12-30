package sg.edu.nus.ui.client.BestPeerWidgets;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * 
 * @author Wu Sai
 * @author David Jiang (Modified)
 */
public class DataExportUI extends BestPeerPanel implements ClickHandler,
		Callback {

	private Tree leftTree;

	private Tree rightTree;

	private com.google.gwt.user.client.ui.Button export;

	private ArrayList<String> localTables = new ArrayList<String>();

	private ArrayList<String> globalTables = new ArrayList<String>();

	private HashMap<String, String[]> localAttribute = new HashMap<String, String[]>();

	private HashMap<String, String[]> globalAttribute = new HashMap<String, String[]>();

	private HashMap<String, ArrayList<String>> indexedAttribute = new HashMap<String, ArrayList<String>>();

	public static interface I18nConstants extends Constants {
		String localDatabase();

		String bestPeerDatabase();

		String exportData();

		String databaseInformation();

		String indexButton();

		String removeIndex();

		String cancelExport();

		String exportWait();

		String indexWait();

		String scheduleInfo();

		String scheduleExport();

		String scheduleWarning();
	}

	public DataExportUI() {
		setTopToolbar(new Toolbar());
	}

	public void addToolbar() {
		Toolbar toolbar = getTopToolbar();

		ToolbarButton index = new ToolbarButton(BestPeerDesktop.constants
				.indexButton());
		index.setEnableToggle(true);
		index.addListener(new MyButtonAdapter(this));
		toolbar.addButton(index);
		toolbar.addSeparator();

		ToolbarButton cancelindex = new ToolbarButton(BestPeerDesktop.constants
				.removeIndex());
		cancelindex.setEnableToggle(true);
		toolbar.addButton(cancelindex);
		toolbar.addSeparator();

		ToolbarButton cancelexport = new ToolbarButton(
				BestPeerDesktop.constants.cancelExport());
		cancelexport.setEnableToggle(true);
		toolbar.addButton(cancelexport);

		ToolbarButton scheduleexport = new ToolbarButton(
				BestPeerDesktop.constants.scheduleExport());
		scheduleexport.setEnableToggle(true);
		scheduleexport.addListener(new MyButtonAdapter(this));
		toolbar.addButton(scheduleexport);
	}

	
	@Override
	protected void afterRender() {
		Toolbar tool = getTopToolbar();
		super.afterRender();
		addToolbar();
	}


	public class MyButtonAdapter extends ButtonListenerAdapter {

		public DataExportUI parent;

		public MyButtonAdapter(DataExportUI parent) {
			super();
			this.parent = parent;
		}

		public void onClick(Button button, EventObject e) {

			if (button.getText()
					.equals(BestPeerDesktop.constants.indexButton())) {

				// get the indexed data
				TreeItem root = parent.rightTree.getItem(0);
				HashMap<String, ArrayList<String>> columnInTables = new HashMap<String, ArrayList<String>>();
				ArrayList<String> columns = new ArrayList<String>();
				int tablecount = root.getChildCount();

				for (int i = 0; i < tablecount; i++) {
					TreeItem table = root.getChild(i);
					int columncount = table.getChildCount();
					ArrayList<String> indexed = parent.indexedAttribute
							.get(table.getText());
					for (int j = 0; j < columncount; j++) {
						TreeItem column = table.getChild(j);
						String cname = column.getText();
						if (indexed != null && indexed.contains(cname)) {
							continue;
						}
						CheckBox check = (CheckBox) column.getWidget();
						if (check.getValue()) {
							if (columnInTables.containsKey(cname)) {
								ArrayList<String> tables = columnInTables
										.get(cname);
								tables.add(table.getText());
							} else {
								ArrayList<String> tables = new ArrayList<String>();
								tables.add(table.getText());
								columnInTables.put(cname, tables);
							}
							columns.add(cname);

							if (parent.indexedAttribute.containsKey(table
									.getText())) {
								ArrayList<String> columnlist = parent.indexedAttribute
										.get(table.getText());
								columnlist.add(cname);
							} else {
								ArrayList<String> columnlist = new ArrayList<String>();
								columnlist.add(cname);
								parent.indexedAttribute.put(table.getText(),
										columnlist);
							}
						}

					}
				}

				// send out the message
				JSONObject param = new JSONObject();
				JSONArray columnlist = new JSONArray();
				for (int i = 0; i < columns.size(); i++) {
					columnlist.set(i, new JSONString(columns.get(i)));

					JSONArray indexedcolumn = new JSONArray();
					ArrayList<String> tablelist = columnInTables.get(columns
							.get(i));
					for (int j = 0; j < tablelist.size(); j++) {
						indexedcolumn.set(j, new JSONString(tablelist.get(j)));
					}
					param.put(columns.get(i), indexedcolumn);
				}
				param.put("column_name", columnlist);
				new RPCCall().invoke("DBIndexService", param, parent);

				// wait...
				MessageBox.show(new MessageBoxConfig() {
					{
						setMsg(BestPeerDesktop.constants.indexWait());

						setWidth(300);
						setWait(true);
						setWaitConfig(new WaitConfig() {
							{
								setInterval(200);
							}
						});
					}
				});
			} else if (button.getText().equals(
					BestPeerDesktop.constants.scheduleExport())) {
				MessageBox.prompt(BestPeerDesktop.constants.scheduleExport(),
						BestPeerDesktop.constants.scheduleInfo(),
						new MessageBox.PromptCallback() {
							public void execute(String btnID, String text) {
								if (text == null) {
									MessageBox.alert(BestPeerDesktop.constants
											.scheduleWarning());
									return;
								}
								String[] split = text.split(":");
								if (split.length != 3) {
									MessageBox.alert(BestPeerDesktop.constants
											.scheduleWarning());
								}
								try {
									int hour = Integer.parseInt(split[0]);
									int min = Integer.parseInt(split[1]);
									int sec = Integer.parseInt(split[2]);
									if (hour >= 0 && hour < 24 && min >= 0
											&& min < 60 && sec >= 0 && sec < 60) {
										JSONObject param = new JSONObject();
										param.put("hour", new JSONString(
												split[0]));
										param.put("min", new JSONString(
												split[1]));
										param.put("sec", new JSONString(
												split[2]));
										new RPCCall().invoke(
												"ExportScheduleService", param,
												parent);
									} else {
										MessageBox
												.alert(BestPeerDesktop.constants
														.scheduleWarning());
									}
								} catch (Exception e) {
									MessageBox.alert(BestPeerDesktop.constants
											.scheduleWarning());
								}
							}
						});

			}
		}
	}

	public void createLeftTree() {
		leftTree.clear();

		TreeItem root = new TreeItem(BestPeerDesktop.constants.localDatabase());
		HashMap<String, String[]> att = this.localAttribute;
		for (int i = 0; i < localTables.size(); i++) {
			TreeItem nextTable = new TreeItem(localTables.get(i));
			String[] nextAtt = att.get(localTables.get(i));
			for (int j = 0; j < nextAtt.length; j++) {
				nextTable.addItem(nextAtt[j]);
			}
			root.addItem(nextTable);
		}
		root.setState(true);
		leftTree.addItem(root);
		leftTree.setAnimationEnabled(true);
	}

	public void createRightTree() {
		rightTree.clear();

		TreeItem root = new TreeItem(BestPeerDesktop.constants
				.bestPeerDatabase());
		HashMap<String, String[]> att = this.globalAttribute;
		for (int i = 0; i < globalTables.size(); i++) {
			TreeItem nextTable = new TreeItem(globalTables.get(i));
			String[] nextAtt = att.get(globalTables.get(i));
			for (int j = 0; j < nextAtt.length; j++) {
				nextTable.addItem(nextAtt[j]);
			}
			root.addItem(nextTable);
		}
		root.setState(true);
		rightTree.addItem(root);
		rightTree.setAnimationEnabled(true);
	}

	Panel panel = null;

	@Override
	public Panel getViewPanel() {
		// TODO Auto-generated method stub
		if (panel == null) {
			panel = new Panel();
			panel.setBorder(false);
			panel.setPaddings(15);
			panel.setLayout(new VerticalLayout(10));

			leftTree = new Tree();
			rightTree = new Tree();
			export = new com.google.gwt.user.client.ui.Button("->");

			Panel bottom = new Panel();
			bottom.setBorder(true);
			bottom.setLayout(new HorizontalLayout(30));
			createLeftTree();
			createRightTree();
			FormPanel leftform = new FormPanel(Position.LEFT);
			leftform.setTitle(BestPeerDesktop.constants.databaseInformation());
			leftform.setFrame(true);
			leftform.setHeight(500);
			leftform.setWidth(300);
			leftform.setAutoScroll(true);
			leftform.add(leftTree);
			bottom.add(leftform);

			bottom.add(export);
			export.addClickHandler(this);

			FormPanel rightform = new FormPanel(Position.RIGHT);
			rightform.setTitle(BestPeerDesktop.constants.databaseInformation());
			rightform.setFrame(true);
			rightform.setHeight(500);
			rightform.setWidth(300);
			rightform.setAutoScroll(true);
			rightform.add(rightTree);
			bottom.add(rightform);
			panel.add(bottom);
			getSchemaInfo();
		}

		return panel;
	}

	private void getSchemaInfo() {
		this.localAttribute.clear();
		this.globalAttribute.clear();
		this.globalTables.clear();
		this.localTables.clear();

		JSONObject params = new JSONObject();
		params.put("schema", new JSONString("exported"));
		new RPCCall().invoke("SchemaService", params, this);
		params = new JSONObject();
		params.put("schema", new JSONString("localmatched"));
		new RPCCall().invoke("SchemaService", params, this);
	}


	// @Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Object source = event.getSource();
		if (source instanceof com.google.gwt.user.client.ui.Button) {
			// retrieve the ui information
			TreeItem root = leftTree.getItem(0);
			int count = root.getChildCount();
			// get table info
			JSONObject params = new JSONObject();
			JSONArray table = new JSONArray();
			for (int i = 0; i < count; i++) {
				TreeItem child = root.getChild(i);

				CheckBox check = (CheckBox) child.getWidget();
				if (!check.getValue())
					continue;
				String tname = child.getText();
				table.set(table.size(), new JSONString(tname));
				int childnumber = child.getChildCount();

				JSONArray column = new JSONArray();
				for (int j = 0; j < childnumber; j++) {
					TreeItem grandChild = child.getChild(j);
					CheckBox subcheck = (CheckBox) child.getWidget();
					if (!subcheck.getValue())
						continue;
					String cname = grandChild.getText();
					column.set(column.size(), new JSONString(cname));
				}
				params.put(tname, column);
			}
			params.put("table_name", table);

			// invoke the service
			new RPCCall().invoke("DBExportService", params, this);

			// wait...
			MessageBox.show(new MessageBoxConfig() {
				{
					setMsg(BestPeerDesktop.constants.exportWait());

					setWidth(300);
					setWait(true);
					setWaitConfig(new WaitConfig() {
						{
							setInterval(200);
						}
					});
				}
			});
		} 
		else if (source instanceof CheckBox) {
			TreeItem root = leftTree.getItem(0);
			int count = root.getChildCount();
			for (int i = 0; i < count; i++) {
				TreeItem table = root.getChild(i);
				CheckBox selected = (CheckBox) table.getWidget();
				if (selected.getText().equals(((CheckBox) source).getText())) {
					if (selected.getValue()) {

						int subcount = table.getChildCount();
						for (int j = 0; j < subcount; j++) {
							TreeItem column = table.getChild(j);
							CheckBox subselected = (CheckBox) column
									.getWidget();
							subselected.setValue(true);
						}
						break;
					} else {
						int subcount = table.getChildCount();
						for (int j = 0; j < subcount; j++) {
							TreeItem column = table.getChild(j);
							CheckBox subselected = (CheckBox) column
									.getWidget();
							subselected.setValue(false);
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReady(JSONObject resultSet) {
		// TODO Auto-generated method stub
		if (resultSet.get("type") != null
				|| resultSet.get("indexstatus") != null) {
			MessageBox.hide();
		}

		if (resultSet.containsKey("schema")) {
			String type = ((JSONString) resultSet.get("schema")).stringValue();
			if (type.equals("exported")) {
				this.indexedAttribute.clear();
				JSONArray namelist = (JSONArray) resultSet.get("table_name");
				for (int i = 0; i < namelist.size(); i++) {
					String name = ((JSONString) namelist.get(i)).stringValue();

					JSONArray columnlist = (JSONArray) resultSet.get("n_"
							+ name);
					JSONArray indexed = (JSONArray) resultSet.get("i_" + name);
					String[] cname = new String[columnlist.size()];
					for (int j = 0; j < cname.length; j++) {
						cname[j] = ((JSONString) columnlist.get(j))
								.stringValue();
					}

					if (indexed != null && indexed.size() > 0) {
						ArrayList<String> column = new ArrayList<String>();
						for (int j = 0; j < indexed.size(); j++) {
							column.add(((JSONString) indexed.get(j))
									.stringValue());
						}
						this.indexedAttribute.put(name, column);
					}
					this.globalAttribute.put(name, cname);
					this.globalTables.add(name);
				}

				// update the right tree
				rightTree.clear();

				TreeItem root = new TreeItem(BestPeerDesktop.constants
						.bestPeerDatabase());
				HashMap<String, String[]> att = this.globalAttribute;
				for (int i = 0; i < globalTables.size(); i++) {
					TreeItem nextTable = new TreeItem(globalTables.get(i));

					String[] nextAtt = att.get(globalTables.get(i));
					ArrayList<String> indexed = this.indexedAttribute
							.get(globalTables.get(i));
					for (int j = 0; j < nextAtt.length; j++) {

						CheckBox subcheck = new CheckBox(nextAtt[j]);
						if (indexed != null && indexed.contains(nextAtt[j])) {
							subcheck.setEnabled(false);
							subcheck.setValue(true);
							nextTable.addItem(subcheck);
						} else {
							nextTable.addItem(subcheck);
						}
					}
					root.addItem(nextTable);
				}
				root.setState(true);
				rightTree.addItem(root);
				rightTree.setAnimationEnabled(true);
			} else {
				JSONArray namelist = (JSONArray) resultSet.get("table_name");
				for (int i = 0; i < namelist.size(); i++) {
					String name = ((JSONString) namelist.get(i)).stringValue();

					JSONArray columnlist = (JSONArray) resultSet.get("n_"
							+ name);
					String[] cname = new String[columnlist.size()];
					for (int j = 0; j < cname.length; j++) {
						cname[j] = ((JSONString) columnlist.get(j))
								.stringValue();
					}
					this.localAttribute.put(name, cname);
					this.localTables.add(name);
				}

				// update the right tree
				leftTree.clear();

				TreeItem root = new TreeItem(BestPeerDesktop.constants
						.bestPeerDatabase());
				HashMap<String, String[]> att = this.localAttribute;
				for (int i = 0; i < localTables.size(); i++) {
					CheckBox check = new CheckBox(localTables.get(i));
					check.addClickHandler(this);
					TreeItem nextTable = new TreeItem(check);
					String[] nextAtt = att.get(localTables.get(i));
					for (int j = 0; j < nextAtt.length; j++) {
						CheckBox subcheck = new CheckBox(nextAtt[j]);
						nextTable.addItem(subcheck);
					}
					root.addItem(nextTable);
				}
				root.setState(true);
				leftTree.addItem(root);
				leftTree.setAnimationEnabled(true);
			}
		} else {
			// for indexing UI
			TreeItem root = rightTree.getItem(0);
			int tablecount = root.getChildCount();
			for (int i = 0; i < tablecount; i++) {
				TreeItem table = root.getChild(i);
				int columncount = table.getChildCount();
				for (int j = 0; j < columncount; j++) {
					TreeItem column = table.getChild(j);
					CheckBox check = (CheckBox) column.getWidget();
					if (check.getValue()) {
						check.setEnabled(false);
					}
				}
			}
		}

		panel.doLayout();
	}
}
