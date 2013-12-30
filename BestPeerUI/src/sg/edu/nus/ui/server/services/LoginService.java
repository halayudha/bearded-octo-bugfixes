package sg.edu.nus.ui.server.services;

import org.json.simple.JSONObject;


/**
 * 
 * @author Wu Sai
 *
 */
public class LoginService {
	
	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		System.out.println("Receive Login Request From Web Interface");
		String inputName = (String)params.get("user");
		String password = (String)params.get("password");
		
		JSONObject result = new JSONObject();
		
		result.put("loginack", "error");
		return result;
	}
}
