/**
 * Created on May 5, 2009
 */
package sg.edu.nus.ui.server.services;

import org.json.simple.JSONObject;

/**
 * @author David Jiang
 *
 */
public class SchemaMappingService {
	public JSONObject doService(JSONObject params) {
		JSONObject result = new JSONObject();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
}
