/**
 * Created on May 6, 2009
 */
package sg.edu.nus.ui.server.services;

import java.util.Vector;

import org.json.simple.JSONObject;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.util.DB_TYPE;

/**
 * @author David Jiang
 * 
 */
public class DBConfigService {
	public JSONObject doService(JSONObject params) {
		System.out.println("Receive Database Configuration Message from Web");

		String errorMsg = "";

		JSONObject erpdbInfo = (JSONObject) params.get("erp-config");
		String erpDriver = (String) erpdbInfo.get("erp-driver");
		String erpURL = (String) erpdbInfo.get("erp-dburl");
		String erpPORT = (String) erpdbInfo.get("erp-dbport");
		String erpName = (String) erpdbInfo.get("erp-dbname");
		String erpUser = (String) erpdbInfo.get("erp-dbuser");
		String erpPass = (String) erpdbInfo.get("erp-dbpass");

		JSONObject bestpeerdbInfo = (JSONObject) params.get("bestpeer-config");
		String bestpeerDriver = (String) bestpeerdbInfo.get("bestpeer-driver");
		String bestpeerURL = (String) bestpeerdbInfo.get("bestpeer-dburl");
		String bestpeerPORT = (String) bestpeerdbInfo.get("bestpeer-dbport");
		String bestpeerName = (String) bestpeerdbInfo.get("bestpeer-dbname");
		String bestpeerUser = (String) bestpeerdbInfo.get("bestpeer-dbuser");
		String bestpeerPass = (String) bestpeerdbInfo.get("bestpeer-dbpass");

		// test erp dataset
		DB_TYPE dbtype = DB_TYPE.getByType(erpDriver);

		DB testdb = new DB(dbtype.getName(), erpURL, erpPORT, erpUser, erpPass,
				erpName);

		if (!testdb.testDbConnection()) {
			errorMsg = "Fail to connect ERP database!\n";
		}

		DB_TYPE bestpeerdbtype = DB_TYPE.getByType(bestpeerDriver);

		testdb = new DB(bestpeerdbtype.getName(), bestpeerURL, bestpeerPORT,
				bestpeerUser, bestpeerPass, bestpeerName);

		if (!testdb.testDbConnection()) {
			errorMsg += "Fail to connect bestpeer database!";
		}

		if (errorMsg.length() == 0) {
			Vector<String> vec = new Vector<String>();
			vec.add(dbtype.getName());
			vec.add(erpURL);
			vec.add(erpPORT);
			vec.add(erpUser);
			vec.add(erpPass);
			vec.add(erpName);
			DBProperty dbProperty = new DBProperty();
			dbProperty.put_erpdb_configure(vec);

			vec = new Vector<String>();
			vec.add(bestpeerdbtype.getName());
			vec.add(bestpeerURL);
			vec.add(bestpeerPORT);
			vec.add(bestpeerUser);
			vec.add(bestpeerPass);
			vec.add(bestpeerName);
			dbProperty.put_bestpeer_configure(vec);

		}

		JSONObject result = new JSONObject();
		result.put("error", errorMsg);
		return result;
	}
}
