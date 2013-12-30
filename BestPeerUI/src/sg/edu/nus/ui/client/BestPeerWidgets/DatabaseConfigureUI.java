/**
 * Created on Apr 24, 2009
 */
package sg.edu.nus.ui.client.BestPeerWidgets;

import sg.edu.nus.ui.client.BestPeerDesktop;
import sg.edu.nus.ui.client.BestPeerPanel;
import sg.edu.nus.ui.client.RPC.Callback;
import sg.edu.nus.ui.client.RPC.RPCCall;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

/**
 * @author David Jiang
 * 
 */
public class DatabaseConfigureUI extends BestPeerPanel implements Callback {

	public static interface I18nConstants extends Constants {
		String dbConfigureERPDatabase();

		String dbConfigureBestPeerDatabase();

		String dbConfigureDriver();

		String dbConfigureURL();

		String dbConfigurePort();

		String dbConfigureDBName();

		String dbConfigureUserName();

		String dbConfigurePassword();

		String dbConfigureSave();

		String dbConfigureSaveText();

		String dbConfigConnectionText();

		String dbConfigureCancelText();

		String dbConfigureMissDriverText();

		String dbConfigureMissURLText();

		String dbConfigureMissPortText();

		String dbConfigureMissDBNameText();

		String dbConfigureMissUserText();

		String dbConfigureMissPassText();

		String dbConfigureSavingText();
	}

	public DatabaseConfigureUI() {
	}

	private final FieldSet createFieldSet(String title, String prefix) {
		FieldSet fieldSet = new FieldSet();
		fieldSet.setTitle(title);
		fieldSet.setCollapsible(true);
		fieldSet.setAutoHeight(true);
		Store store = new SimpleStore(new String[] { "name" }, new String[][] {
				new String[] { "MySQL" }, new String[] { "Postgre SQL" },
				new String[] { "SQL Server" }, new String[] { "DB2" },
				new String[] { "Oracle" }, new String[] { "SyBase" } });
		store.load();
		ComboBox driver = new ComboBox();
		driver.setForceSelection(true);
		driver.setMinChars(1);
		driver.setFieldLabel(prefix + "driver");
		driver.setId(prefix + "driver");
		driver.setStore(store);
		driver.setDisplayField("name");
		driver.setMode(ComboBox.LOCAL);
		driver.setEmptyText("Select Database Type");
		driver.setWidth(210);
		fieldSet.add(driver);

		TextField dbUrl = new TextField(BestPeerDesktop.constants
				.dbConfigureURL(), prefix + "dburl", 210);
		dbUrl.setAllowBlank(false);
		dbUrl.setBlankText(BestPeerDesktop.constants.dbConfigureMissURLText());
		dbUrl.setLabelSeparator("");
		dbUrl.setValue("localhost");
		fieldSet.add(dbUrl);

		TextField dbPort = new TextField(BestPeerDesktop.constants
				.dbConfigurePort(), prefix + "dbport", 210);
		dbPort.setLabelSeparator("");
		dbPort.setAllowBlank(false);
		dbPort
				.setBlankText(BestPeerDesktop.constants
						.dbConfigureMissPortText());
		fieldSet.add(dbPort);

		TextField dbName = new TextField(BestPeerDesktop.constants
				.dbConfigureDBName(), prefix + "dbname", 210);
		dbName.setAllowBlank(false);
		dbName.setBlankText(BestPeerDesktop.constants
				.dbConfigureMissDBNameText());
		dbName.setLabelSeparator("");
		fieldSet.add(dbName);

		TextField dbUserName = new TextField(BestPeerDesktop.constants
				.dbConfigureUserName(), prefix + "dbuser", 210);
		dbUserName.setAllowBlank(false);
		dbUserName.setBlankText(BestPeerDesktop.constants
				.dbConfigureMissUserText());
		dbUserName.setLabelSeparator("");
		fieldSet.add(dbUserName);

		TextField dbPass = new TextField(BestPeerDesktop.constants
				.dbConfigurePassword(), prefix + "dbpass", 210);
		dbPass.setAllowBlank(false);
		dbPass
				.setBlankText(BestPeerDesktop.constants
						.dbConfigureMissPassText());
		dbPass.setPassword(true);
		dbPass.setLabelSeparator("");
		fieldSet.add(dbPass);
		return fieldSet;
	}

	Panel panel = null;
	FormPanel formPanel = null;

	@Override
	public Panel getViewPanel() {
		if (panel == null) {
			panel = new Panel();
			panel.setBorder(false);
			formPanel = new FormPanel(Position.LEFT);
			formPanel.setTitle(BestPeerDesktop.constants
					.dbConfigConnectionText());
			formPanel.setFrame(true);
			formPanel.setPaddings(5, 5, 5, 0);
			formPanel.setWidth(370);
			formPanel.setLabelWidth(95);
			formPanel.add(createFieldSet(BestPeerDesktop.constants
					.dbConfigureERPDatabase(), "erp-"));
			formPanel.add(createFieldSet(BestPeerDesktop.constants
					.dbConfigureBestPeerDatabase(), "bestpeer-"));

			Button saveButton = new Button(BestPeerDesktop.constants
					.dbConfigureSaveText());
			saveButton.addListener(new ButtonListenerAdapter() {
				private JSONObject createDatabaseInfo(Form form, String prefix) {
					JSONObject dbInfo = new JSONObject();
					dbInfo.put(prefix + "driver", new JSONString(form
							.findField(prefix + "driver").getValueAsString()));
					dbInfo.put(prefix + "dburl", new JSONString(form.findField(
							prefix + "dburl").getValueAsString()));
					dbInfo.put(prefix + "dbport", new JSONString(form
							.findField(prefix + "dbport").getValueAsString()));
					dbInfo.put(prefix + "dbname", new JSONString(form
							.findField(prefix + "dbname").getValueAsString()));
					dbInfo.put(prefix + "dbuser", new JSONString(form
							.findField(prefix + "dbuser").getValueAsString()));
					dbInfo.put(prefix + "dbpass", new JSONString(form
							.findField(prefix + "dbpass").getValueAsString()));
					return dbInfo;
				}

				@Override
				public void onClick(Button button, EventObject e) {
					Form form = formPanel.getForm();
					if (!form.isValid())
						return;
					JSONObject params = new JSONObject();
					params.put("erp-config", createDatabaseInfo(form, "erp-"));
					params.put("bestpeer-config", createDatabaseInfo(form,
							"bestpeer-"));
					RPCCall.get().invoke("DBConfigService", params,
							DatabaseConfigureUI.this);
					WaitingMessageBox.show(BestPeerDesktop.constants
							.dbConfigureSavingText());
				}

			});
			formPanel.addButton(saveButton);
			Button cancelButton = new Button(BestPeerDesktop.constants
					.dbConfigureCancelText());
			cancelButton.addListener(new ButtonListenerAdapter() {

				private void clearDatabaseInfo(Form form, String prefix) {
					form.findField(prefix + "driver").reset();
					form.findField(prefix + "dburl").reset();
					form.findField(prefix + "dbport").reset();
					form.findField(prefix + "dbname").reset();
					form.findField(prefix + "dbuser").reset();
					form.findField(prefix + "dbpass").reset();
				}

				@Override
				public void onClick(Button button, EventObject e) {
					Form form = formPanel.getForm();
					clearDatabaseInfo(form, "erp-");
					clearDatabaseInfo(form, "bestpeer-");
				}
			});
			formPanel.addButton(cancelButton);
			panel.add(formPanel);
		}
		return panel;
	}

	@Override
	public void onFailure() {
	}

	@Override
	public void onReady(JSONObject resultSet) {
		MessageBox.hide();

		String errorMsg = ((JSONString) resultSet.get("error")).stringValue();
		if (errorMsg.length() != 0) {
			MessageBox.alert(errorMsg);
		}
	}

}
