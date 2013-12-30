/**
 * Created on Apr 30, 2009
 */
package sg.edu.nus.ui.server.services;

import org.json.simple.JSONObject;

/**
 * @author David Jiang
 *
 */
public class EchoService {
	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		System.out.println("In EchoService");
		if(params != null) {
			System.out.println("input params: " + params.toString());
		}
		JSONObject result = new JSONObject();
		result.put("output", "Hi " + params.get("input") + " Welcome to server!");
		return result;
	}
}
