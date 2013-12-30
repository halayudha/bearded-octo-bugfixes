package sg.edu.nus.ui.server.services;

import org.json.simple.JSONObject;

import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * @author Wu Sai
 * 
 */
public class LoginService {

	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		System.out.println("Receive Login Request From Web Interface");
		String inputName = (String) params.get("user");
		String password = (String) params.get("password");

		ServerPeer server = ServerGUI.instance.peer();
		JSONObject result = new JSONObject();

		String userType = null;

		System.out.println("check user " + inputName + " pass "+ password);
		
		if (inputName.equals(server.getServerPeerAdminName())
				&& password.equals(server.getPassword()))

		{
			result.put("loginack", "ok");
			userType = "admin";
			result.put("usertype", userType);
		} else {
			boolean valid = MetaDataAccess.checkValidUser(ServerPeer.conn_metabestpeerdb, inputName, password);
			if (valid){
				result.put("loginack", "ok");
				String[][] role = MetaDataAccess.metaGetUserGrantedRole(ServerPeer.conn_metabestpeerdb, inputName);
				if (role!=null){
					userType = role[0][0];
				} else { 
					userType = "non-role-assigned";
				}
				result.put("usertype", userType);
			} else {
				result.put("loginack", "error");
				userType = "wrong user name";
				result.put("usertype", userType);				
			}
			
		}
		return result;
	}
}
