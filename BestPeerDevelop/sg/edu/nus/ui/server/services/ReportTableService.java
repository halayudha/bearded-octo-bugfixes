package sg.edu.nus.ui.server.services;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * @author Wang Jinbao
 *
 */
public class ReportTableService {

	@SuppressWarnings("unchecked")
	public JSONObject doService(JSONObject params) {
		System.out.println("Receive a request for report tables.");
		if(params.containsKey("username")){
			JSONArray names = new JSONArray();
			JSONArray sql = new JSONArray();
			JSONArray param = new JSONArray();
			JSONArray discription = new JSONArray();
			JSONArray type = new JSONArray();
			JSONArray creator = new JSONArray();
			String userName = (String)params.get("username");
			
			String[][] report = sg.edu.nus.util.MetaDataAccess.metaGetReportQuery(ServerPeer.conn_metabestpeerdb, userName);
			
			System.out.println("retrieve report query ok.");
			
			for(int i=0; i<report.length; i++){
				names.add(report[i][0]);
				sql.add(report[i][1]);
				param.add(report[i][2]);
				discription.add(report[i][3]);
				type.add(report[i][4]);
				creator.add(report[i][5]);
			}
			JSONObject result = new JSONObject();
			result.put("queryName", names);
			result.put("reportName", sql);
			result.put("param", param);
			result.put("discription", discription);
			result.put("type", type);
			result.put("creator", creator);
			
			String tableName = "medical_test";
			String colName = "test_name";
			
			String[] localTerm = MetaDataAccess.dataMappingGetLocalTerm(tableName, colName);
			int length = localTerm == null ? 0 : localTerm.length;
			JSONArray lTerm = new JSONArray();
			if (localTerm != null) {
				for (int i = 0; i < localTerm.length; i++) {
					lTerm.add(localTerm[i]);
				}
			}
			result.put("lsize", length);
			result.put("localTerm", lTerm);
			
			String[] globalTerm = MetaDataAccess.dataMappingGetGlobalTerm(tableName, colName);
			length = globalTerm == null ?  0 : globalTerm.length;
			JSONArray gTerm = new JSONArray();
			if (globalTerm != null) {
				for (int i = 0; i < globalTerm.length; i++) {
					gTerm.add(globalTerm[i]);
				}
			}
			result.put("gsize", length);
			result.put("globalTerm", gTerm);
			
			return result;
		}
		else{
			
			String user = (String)params.get("userName");
			String sql = (String)params.get("sql");
			String tname = (String)params.get("reportName");
			String parameters = (String)params.get("parameters");
			String discriptionString = (String)params.get("discription");
			String typeString = (String)params.get("queryType");
			sg.edu.nus.util.MetaDataAccess.metaInsertReportQuery(ServerPeer.conn_metabestpeerdb, tname, user, sql, parameters, discriptionString, typeString);
			JSONObject result = new JSONObject();
			result.put("done", "ok");
			return result;
		}
	}

}
