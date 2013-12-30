package sg.edu.nus.ui.server.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sg.edu.nus.util.MetaDataAccess;

import com.wcohen.ss.*;

/**
 * 
 * @author Wang Jinbao
 * 
 */
public class DataMappingService {
	public JSONObject doService(JSONObject params) {
		JSONObject result = new JSONObject();
		if(params.containsKey("loadMapping")){
			String tableName = (String)params.get("table");
			String colName = (String)params.get("column");
			String userName = (String)params.get("username");
			
			String[] globalTerm = MetaDataAccess.dataMappingGetGlobalTerm(tableName, colName);
			JSONArray gTerm = new JSONArray();
			for(int i=0; i<globalTerm.length; i++){
				gTerm.add(globalTerm[i]);
			}
			result.put("gsize", globalTerm.length);
			result.put("globalTerm", gTerm);
			
			String[] localTerm = MetaDataAccess.dataMappingGetLocalTerm(tableName, colName);
			JSONArray lTerm = new JSONArray();
			for(int i=0; i<localTerm.length; i++){
				lTerm.add(localTerm[i]);
			}
			result.put("lsize", localTerm.length);
			result.put("localTerm", lTerm);
			
			//add confidence value array here.
			Jaro jaro = new Jaro();
			Jaccard jacard = new Jaccard();
			System.out.println("H1N1: "+jacard.score("H1N1", "H5N1"));
			System.out.println("H3N2: "+jacard.score("H1N1", "H3N2"));
			for(int i=0; i<localTerm.length; i++){
				JSONArray conf = new JSONArray();
				for(int j=0; j<globalTerm.length; j++){
					double sco = 0;
					String[] semantic = MetaDataAccess.dataMappingGetSemanticMappings(tableName, colName, globalTerm[j]);
					for(int k=0; k<semantic.length; k++){
						sco = Math.max(sco, jacard.score(localTerm[i], semantic[k]));
					}
					sco = Math.max(sco, jacard.score(localTerm[i], globalTerm[j]));
					conf.add(sco);
				}
				String key = localTerm[i]+"Confidence";
				result.put(key, conf);
			}			
			
			String[][] mapping;
			try{
				mapping = MetaDataAccess.dataMappingGetExistingMapping(
						tableName, colName, userName);	
				result.put("msize", mapping.length);
				JSONArray mappingContent = new JSONArray();
				for(int i=0; i<mapping.length; i++){
					JSONObject row = new JSONObject();
					row.put("localTerm", mapping[i][0]);
					row.put("globalTerm", mapping[i][1]);
					mappingContent.add(row);
				}
				result.put("mapping", mappingContent);
			}catch(Exception e){
				result.put("msize", 0);
				result.put("mapping", null);
			}							
		}else if(params.containsKey("insertMapping")){
			String globalTerm;
			String localTerm;
			String tableName = (String)params.get("table");
			String colName = (String)params.get("column");
			String userName = (String)params.get("username");
			JSONArray insertJ = (JSONArray) params.get("toInsert");
			JSONArray deleteJ = (JSONArray) params.get("toDelete");
			JSONArray updateJ = (JSONArray) params.get("toUpdate");			
			//insert
			for(int i=0; i<insertJ.size(); i++){
				JSONObject jo = (JSONObject)(insertJ.get(i));
				globalTerm = jo.get("global").toString();
				localTerm = jo.get("local").toString();
				MetaDataAccess.dataMappingInsertMapping(localTerm, globalTerm, tableName, colName, userName);
			}
			//delete
			for(int i=0; i<deleteJ.size(); i++){
				JSONObject jo = (JSONObject)(deleteJ.get(i));
				localTerm = jo.get("local").toString();
				MetaDataAccess.dataMappingDeleteMapping(localTerm, tableName, colName, userName);
			}
			//update
			for(int i=0; i<updateJ.size(); i++){
				JSONObject jo = (JSONObject)(updateJ.get(i));
				globalTerm = jo.get("global").toString();
				localTerm = jo.get("local").toString();
				MetaDataAccess.dataMappingDeleteMapping(localTerm, tableName, colName, userName);
				MetaDataAccess.dataMappingInsertMapping(localTerm, globalTerm, tableName, colName, userName);
			}
			result.put("insert", null);
		}
		return result;		
	}
}