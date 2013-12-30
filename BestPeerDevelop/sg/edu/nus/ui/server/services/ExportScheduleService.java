package sg.edu.nus.ui.server.services;

import org.json.simple.JSONObject;

import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;

/**
 * 
 * @author Wu Sai
 * 
 */
public class ExportScheduleService {

	public JSONObject doService(JSONObject params) {
		System.out
				.println("Receive Setting Scheduled Exporting Request from Web");
		int hour = Integer.parseInt((String) params.get("hour"));
		int min = Integer.parseInt((String) params.get("min"));
		int sec = Integer.parseInt((String) params.get("sec"));

		ServerPeer serverpeer = ServerGUI.instance.peer();

		serverpeer.dataExport.setSchedule(hour, min, sec);

		return null;
	}

}
