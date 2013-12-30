package sg.edu.nus.accesscontrol;

import java.util.ArrayList;

import com.sun.jmx.remote.util.OrderClassLoaders;

public class SqlHandler {

	String orgSql = null;
	ArrayList<String> columnList = null;
	ArrayList<String> tableList = null; // in most the case, there will be only
	// 1 table in querying
	String whereClause = null;
	String groupbyClause = null;

	String SELECT = "select ";
	String FROM = " from "; // add " " so that keyword can not be recognized
	// wrongly
	String WHERE = " where ";
	String GROUPBY = " group by ";

	public SqlHandler() {

	}

	public void setColumnList(ArrayList<String> arrList) {
		columnList = arrList;
	}

	public void setTableList(ArrayList<String> arrList) {
		tableList = arrList;
	}

	public ArrayList<String> getColumnList() {
		return columnList;
	}

	public ArrayList<String> getTableList() {
		return tableList;
	}

	public void setWhereClause(String str) {
		whereClause = str;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public String regenerateSqlString() {

		String sql = SELECT;

		int size = columnList.size();
		for (int i = 0; i < size; i++) {
			sql += columnList.get(i);
			if (i < size - 1) {
				sql += ", ";
			}
		}

		sql += FROM;

		size = tableList.size();
		for (int i = 0; i < size; i++) {
			sql += tableList.get(i);
			if (i < size - 1) {
				sql += ", ";
			}
		}

		if (whereClause != null) {
			sql += WHERE + whereClause;
		}

		if (groupbyClause != null) {
			sql += GROUPBY + groupbyClause;
		}

		return sql;
	}

	public void parseSql(String sql) {

		orgSql = preProcessSqlString(sql);

		int selectIndex = orgSql.indexOf(SELECT);
		int fromIndex = orgSql.indexOf(FROM);
		int whereIndex = orgSql.indexOf(WHERE);
		int groupbyIndex = orgSql.indexOf(GROUPBY);

		String columnString = orgSql.substring(selectIndex + SELECT.length(),
				fromIndex);

		int beforeGroupby = groupbyIndex >= 0 ? groupbyIndex : orgSql.length();
		int sentinel = whereIndex >= 0 ? whereIndex : beforeGroupby;
		String tableString = orgSql.substring(fromIndex + FROM.length(),
				sentinel);

		sentinel = groupbyIndex >= 0 ? groupbyIndex : orgSql.length();
		if (whereIndex >= 0) {
			whereClause = orgSql.substring(whereIndex + WHERE.length(),
					sentinel);
		}

		if (groupbyIndex >= 0) {
			groupbyClause = orgSql.substring(groupbyIndex + GROUPBY.length(),
					orgSql.length());
		}

		columnList = splitIntoAtomStrings(columnString, ",");
		tableList = splitIntoAtomStrings(tableString, ",");
	}

	private String preProcessSqlString(String sql) {
		
		sql = sql.toLowerCase(); 
		
		//do not use order by, the receiver will process order clause later
		if (sql.contains("order by")){
			int pos = sql.indexOf("order by");
			sql = sql.substring(0, pos-1);
		}
		
		String result = null;
		sql = sql.replace('\n', ' ');
		result = sql.replace('\r', ' ');
		result = result.replaceAll("groupby ", GROUPBY);
		return result;
	}

	private ArrayList<String> splitIntoAtomStrings(String orgStr,
			String seperator) {
		ArrayList<String> arrList = new ArrayList<String>();
		String[] strArr = orgStr.split(seperator);
		for (int i = 0; i < strArr.length; i++) {
			strArr[i] = strArr[i].trim();
			arrList.add(strArr[i]);
		}
		return arrList;
	}

	private String removeSpaceInString(String orgStr) {
		String strOut = "";
		for (int i = 0; i < orgStr.length(); i++) {
			char c = orgStr.charAt(i);
			if (c != ' ') {
				strOut += c;
			}
		}
		return strOut;
	}

	public static void main(String[] args) {
		SqlHandler sqlHandler = new SqlHandler();
		String sql = "Select sum(a), b.x, c \nfrom t1, t2 \rwhere d = 1 group by c";

		System.out.println(sql);

		sqlHandler.parseSql(sql);

		SqlHandler newSqlHandler = new SqlHandler();
		newSqlHandler.setColumnList(sqlHandler.getColumnList());
		newSqlHandler.setTableList(sqlHandler.getTableList());
		newSqlHandler.setWhereClause(sqlHandler.getWhereClause());

		System.out.println(newSqlHandler.regenerateSqlString());

	}
}
