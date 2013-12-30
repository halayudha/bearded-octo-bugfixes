package sg.edu.nus.ui.server.services;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;

/**
 * 
 * @author Wu Sai
 * @modified by Wang Jinbao
 */
public class SQLService {

	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		System.out.println("Receive SQL Query From Web Interface");
		String query = (String) params.get("sql");

		ServerPeer server = ServerGUI.instance.peer();
		JSONObject result = new JSONObject();

		SelectExecutor selectExecutor = new SelectExecutor(query);
		
		String userName = (String) params.get("username");
		
		selectExecutor.setUserName(userName);
		try{
		ArrayList<String[]> tuples = selectExecutor.executeWebQuery();
		
		if (tuples == null) {
			result.put("status", "false");
		} else
			result.put("status", "true");	
		
		String[] name = tuples.get(0);
		String[] types = tuples.get(1);
		
		//added by Jinbao
		int[] map = new int[name.length];
		for(int i=0; i<map.length; i++){
			map[i] = 1;
		}
		//end
		
		JSONArray namelist = new JSONArray();
		JSONArray typelist = new JSONArray();
		
		//added by Jinbao
		for (int i = 0; i < name.length; i++){			
			if(name[i].indexOf("_global_term") != -1){
				for(int j=0; j<i; j++){
					if(name[i].indexOf(name[j]+"_global_term")!=-1){
						for(int k=2; k<tuples.size(); k++){
							tuples.get(k)[j] = tuples.get(k)[j] + "  ["
								+ tuples.get(k)[i] + "]";							
						}
						map[i] = 0;//will not list this column anymore
					}
				}
				for(int j=i+1; j<name.length; j++){
					if(name[i].indexOf(name[j]+"_global_term")!=-1){
						for(int k=2; k<tuples.size(); k++){
							tuples.get(k)[j] = tuples.get(k)[j] + "  ["
								+ tuples.get(k)[i] + "]";							
						}
						map[i] = 0;//will not list this column anymore
					}
				}
			}
			if (map[i] == 1){
				namelist.add(name[i]);
				typelist.add(new Integer(types[i]));
			}			
		}
		//end
		result.put("column_name", namelist);
		result.put("column_type", typelist);

		result.put("column_number", new Integer(tuples.size() - 2));
		// retrieve the data
		for (int i = 2; i < tuples.size(); i++) {
			String[] record = tuples.get(i);
			JSONArray list = new JSONArray();
			//added by Jinbao
			for (int j = 0; j < record.length; j++) {
				if(map[j] == 1)
					list.add(record[j]);
			}
			//end
			int listno = i - 2;
			result.put("list" + listno, list);
		}
		return result;
		}
		catch(Exception e){
			result.put("error", null);
			return result;
		}
	}

}
