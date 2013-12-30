package sg.edu.nus.ui.server.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sg.edu.nus.logging.LogManager;
import sg.edu.nus.logging.REPORT_ITEM;
import sg.edu.nus.logging.TIME_UNIT;

/**
 * 
 * @author Wu Sai
 * 
 */
public class StatisticsService {

	public JSONObject doService(JSONObject params) {
		JSONObject result = new JSONObject();

		TIME_UNIT unit = TIME_UNIT.getByName((String) params.get("TIME_UNIT"));
		int numUnits = Integer.parseInt((String) params.get("NUM_UNITS"));

		for (int itemno = 0; itemno < REPORT_ITEM.itemset.length; itemno++) {
			REPORT_ITEM item = REPORT_ITEM.itemset[itemno];
			this.addStatItem(result, item, unit, numUnits);
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	private void addStatItem(JSONObject jsonObj, REPORT_ITEM item,
			TIME_UNIT unit, int numUnits) {
		JSONArray rsList = new JSONArray();
		int[] rsArray = LogManager.getReport(item, unit, numUnits);
		for (int i = 0; i < rsArray.length; i++) {
			rsList.add(new Integer(rsArray[i]));
		}
		System.out.println(item.toString());
		jsonObj.put(item.toString(), rsList);
	}
}
