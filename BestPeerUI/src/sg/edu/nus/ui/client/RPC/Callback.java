/**
 * Created on Apr 29, 2009
 */
package sg.edu.nus.ui.client.RPC;

import com.google.gwt.json.client.JSONObject;


/**
 * @author David Jiang
 *
 */
public interface Callback {
	void onReady(JSONObject resultSet);
	void onFailure();
}
