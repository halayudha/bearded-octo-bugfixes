package sg.edu.nus.peer.info;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.StringTokenizer;

import sg.edu.nus.bestpeer.indexdata.ExactQuery;
import sg.edu.nus.util.MetaDataAccess;

public class CacheDbIndex implements Serializable{

	private static final long serialVersionUID = -226361772687929980L;

	String[][] tableIndex = null;
	String[][] rangeIndexString = null;
	String[][] rangeIndexNumber = null;
	
	public CacheDbIndex(){
		
	}
	
	public void loadFromDB(Connection conn){		
		tableIndex = MetaDataAccess.metaGetTableIndex(conn);
		rangeIndexString = MetaDataAccess.metaGetRangeIndexString(conn);
		rangeIndexNumber = MetaDataAccess.metaGetRangeIndexNumber(conn);
	}
	
	public ArrayList<PhysicalInfo> getTableOwners(String tableName, ExactQuery exactQuery) {
		
		String tableOwnersString = findTableOwners(tableName, exactQuery); 
			
		ArrayList<PhysicalInfo> tableOwners = new ArrayList<PhysicalInfo>();
		
		if (tableOwnersString != null) {
			StringTokenizer tokenizer = new StringTokenizer(tableOwnersString,
					":");
			while (tokenizer.hasMoreTokens()) {
				tableOwners.add(new PhysicalInfo(tokenizer.nextToken()));
			}
		}
		
		if (tableOwners.size()>0){
			System.out.println("Search Table Index using CacheDbIndex Successfully!");
		}
		
		return tableOwners;
	}
	
	public String findTableOwners(String tableName, ExactQuery exactQuery) {

		String owners = null;

		if (exactQuery != null) {

			boolean numberPredicate = true; //query with predicate on number
			if (exactQuery.getValue().contains("'")) {
				numberPredicate = false;
			}
			
			String colName = exactQuery.getColumnName();
			String predicateValue = exactQuery.getValue();
			
			String nodes = "";
			
			if (numberPredicate){
				if (rangeIndexNumber != null) {
					for (int i = 0; i < rangeIndexNumber.length; i++) {
						if (tableName.equals(rangeIndexNumber[i][0])
								&& colName.equals(rangeIndexNumber[i][1])) {
							double queryValue = Double
									.parseDouble(predicateValue);
							double lowerBound = Double
									.parseDouble(rangeIndexNumber[i][3]);
							double upperBound = Double
									.parseDouble(rangeIndexNumber[i][4]);
							if (queryValue <= upperBound
									&& queryValue >= lowerBound) {
								nodes += rangeIndexNumber[i][2] + ":";
							}
						}
					}
				}
			} 
			else {//string predicate
				if (rangeIndexString != null) {
					for (int i = 0; i < rangeIndexString.length; i++) {
						if (tableName.equals(rangeIndexString[i][0])
								&& colName.equals(rangeIndexString[i][1])) {
							String queryValue = predicateValue;
							String lowerBound = rangeIndexString[i][3];
							String upperBound = rangeIndexString[i][4];
							if (queryValue.compareTo(upperBound) <= 0
									&& queryValue.compareTo(lowerBound) >= 0) {
								nodes += rangeIndexString[i][2] + ":";
							}
						}
					}
				}
			}

			if (!nodes.equals("")) {
				owners = nodes.substring(0, nodes.length() - 1);//omit the last ":"
				System.out.println("DEBUG: find owners with data index "
						+ owners);
			}

		}

		// if no data index, so search for table index
		if (owners == null) {
			if (tableIndex != null) {
				for (int i = 0; i < tableIndex.length; i++) {
					if (tableName.equals(tableIndex[i][0])) {
						owners = new String(tableIndex[i][1]);
						break;
					}
				}
			}
			
			System.out.println("DEBUG: find owners " + owners);
		}

		return owners;
	}
	
}
