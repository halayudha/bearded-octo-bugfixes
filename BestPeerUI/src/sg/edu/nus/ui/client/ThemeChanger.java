/**
 * Created on Jul 8, 2009
 */
package sg.edu.nus.ui.client;

import com.gwtext.client.data.Record;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.util.CSS;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;

/**
 * @author David Jiang
 *
 */
public class ThemeChanger extends ComboBox {
	public ThemeChanger() {

		final Store store = new SimpleStore(new String[]{"theme", "label"}, new Object[][]{
				new Object[]{"themes/slate/css/xtheme-slate.css", "Slate"},
				new Object[]{"js/ext/resources/css/xtheme-gray.css", "Gray"},
				new Object[]{"themes/green/css/xtheme-green.css", "Green"},
				new Object[]{"themes/gtheme/gtheme.css", "GTheme"},
		});
		store.load();

		setFieldLabel("Select Theme");
		setEditable(false);
		setStore(store);
		setDisplayField("label");
		setForceSelection(true);
		setTriggerAction(ComboBox.ALL);
		setValue("Slate");
		setFieldLabel("Switch theme");
		addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {
				String theme = record.getAsString("theme");
				CSS.swapStyleSheet("theme", theme);
			}
		});
		setWidth(100);
	}

}
