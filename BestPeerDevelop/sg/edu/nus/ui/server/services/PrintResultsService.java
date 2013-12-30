package sg.edu.nus.ui.server.services;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;

/**
 * 
 * @author Wang Jinbao
 *
 */
public class PrintResultsService {

	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		JSONObject result = new JSONObject();
		String username = (String) params.get("username");
		String sql = (String) params.get("sql");
		String filetype = (String) params.get("filetype");
		String title = (String) params.get("title");
		String url = sg.edu.nus.bestpeer.queryprocessing.SelectExecutor.generateReport(username, sql, title, filetype);
		result.put("url", url);		
		return result;
	}

}
