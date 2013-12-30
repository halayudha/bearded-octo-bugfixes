/**
 * Created on May 6, 2009
 */
package sg.edu.nus.ui.server.services;

import org.json.simple.JSONObject;

/**
 * @author David Jiang
 *
 */
public class DBConfigService {
	public JSONObject doService(JSONObject params) {
		System.out.println(params.get("erp-config"));
		System.out.println(params.get("bestpeer-config"));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
