package sg.edu.nus.sqlparser;

import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.sqlparser.java_cup_use.SQLParser;
import sg.edu.nus.sqlparser.java_cup_use.SQLQuery;

@SuppressWarnings("unchecked")
public class SelectStatement {

	String orgSql = null;

	Vector tableList = null; // from clause

	Vector projectionList = null; // select clause

	Vector selectionList = null; // where clause
	Vector joinList = null;

	Vector groupbyList = null;
	Vector orderbyList = null;

	boolean isAggregate = false;
	boolean projectAttribNotInGroupBy = false;

	public SelectStatement(String sql) {
		orgSql = new String(sql);
		init();
	}

	public boolean isAggregateQuery() {
		return isAggregate;
	}

	public static boolean checkQuerySyntax(String sql) {
		SQLParser sqlparser = new SQLParser(sql);

		try {
			sqlparser.parseQuery();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void init() {
		// use java_cup_tool to parse
		// can use other tools as well
		SQLParser sqlparser = new SQLParser(orgSql);

		try {
			sqlparser.parseQuery();
		} catch (Exception e) {
			String message = "Please check SQL syntax. Print from SelectStement.";
			System.out.println(message);
			//JOptionPane.showMessageDialog(null, message);

		}

		SQLQuery sqlquery = sqlparser.getSQLQuery();

		tableList = sqlquery.getFromList();
		projectionList = sqlquery.getProjectList();
		selectionList = sqlquery.getSelectionList();
		joinList = sqlquery.getJoinList();

		groupbyList = sqlquery.getGroupByList();
		orderbyList = sqlquery.getOrderByList();

		// for not null object
		if (groupbyList == null) {
			groupbyList = new Vector();
		}

		// detecting aggregate projection
		if (projectionList.size() > 0) {
			for (int i = 0; i < projectionList.size(); i++) {
				Attribute att = (Attribute) projectionList.get(i);
				att.checkAggregate();
				if (att.isAggregate()) {
					isAggregate = true;
				} else {// not agrregate
					if (groupbyList != null) {
						if (groupbyList.size() > 0) {
							boolean exist = false;
							for (int j = 0; j < groupbyList.size(); j++) {
								Attribute groupbyAtt = (Attribute) groupbyList
										.get(j);
								if (att.colname.equals(groupbyAtt.colname)) {
									exist = true;
								}
							}
							if (!exist) {
								projectAttribNotInGroupBy = true;
							}
						}
					}
				}
			}
		}
	}

	public boolean isAttribNotInGroupBy() {
		return projectAttribNotInGroupBy;
	}

	public Vector getTableList() {
		return tableList;
	}

	public Vector getProjectionList() {
		return projectionList;
	}

	public Vector getSelectionList() {
		return selectionList;
	}

	public Vector getJoinList() {
		return joinList;
	}

	/**
	 * check if query retrieve the right table and column names
	 * 
	 */
	public boolean checkTableNameAndColumnName(String[] metaTableName,
			Hashtable<String, String[][]> metaColumns) {

		// check table name

		for (int test = 0; test < tableList.size(); test++) {
			String testTable = (String) tableList.get(test);
			boolean exist = checkStringInArray(testTable, metaTableName);
			if (!exist) {
				return false;
			}
		}

		// check column name in projection list
		for (int test = 0; test < projectionList.size(); test++) {
			Attribute att = (Attribute) projectionList.get(test);
			if (!checkAttribute(att, metaTableName, metaColumns)) {
				return false;
			}
		}

		// check column name in selection list
		for (int test = 0; test < selectionList.size(); test++) {
			Condition cond = (Condition) selectionList.get(test);
			Attribute att = cond.getLhs();
			if (!checkAttribute(att, metaTableName, metaColumns)) {
				return false;
			}
		}

		// check column name in join list
		for (int test = 0; test < joinList.size(); test++) {
			Condition cond = (Condition) joinList.get(test);
			Attribute att = cond.getLhs();
			if (!checkAttribute(att, metaTableName, metaColumns)) {
				return false;
			}
			att = (Attribute) cond.getRhs();
			if (!checkAttribute(att, metaTableName, metaColumns)) {
				return false;
			}
		}

		return true;
	}

	private boolean checkStringInArray(String testTable, String[] metaTableName) {

		boolean exist = false;
		for (int i = 0; i < metaTableName.length && !exist; i++) {
			if (testTable.equals(metaTableName[i])) {
				exist = true;
			}
		}

		if (!exist) {
			System.out.println("Illegal table name: '" + testTable + "'");
		}

		return exist;
	}

	private boolean checkColumnInArray(String testColumn, String[][] columns) {

		boolean exist = false;
		for (int i = 0; i < columns.length && !exist; i++) {
			if (testColumn.equals(columns[i][0])) {
				exist = true;
			}
		}

		return exist;
	}

	private boolean checkAttribute(Attribute att, String[] metaTableName,
			Hashtable<String, String[][]> metaColumns) {
		boolean exist = false;

		String testTable = att.getTabName();
		String testColumn = att.getColName();

		if (testTable != null) {// attribute has explicit table name
			exist = checkStringInArray(testTable, metaTableName);
			if (!exist) {
				return false;
			}
			String[][] columns = metaColumns.get(testTable);
			exist = checkColumnInArray(testColumn, columns);
			if (!exist) {
				//JOptionPane.showMessageDialog(null, "Illegal column name: '"
					//	+ testColumn + "'");
				System.out.println("Illegal column name: '"	+ testColumn + "'");
				return false;
			}
		} else {// attribute has implicit table name
			exist = false;
			for (int test = 0; test < metaTableName.length && !exist; test++) {
				testTable = metaTableName[test];
				String[][] columns = metaColumns.get(testTable);
				exist = checkColumnInArray(testColumn, columns);
			}
			if (!exist) {
				System.out.println("Illegal column name: '"
						+ testColumn + "'");
				return false;
			}
		}

		return true;
	}


	public Vector getGroupByList() {
		return groupbyList;
	}

	public Vector getOrderByList() {
		return orderbyList;
	}

	public boolean hasOrderByClause(){
		return orderbyList.size() > 0;
	}
	
	public static String pushTableNameToWhereClause(String tableName,
			String initialWhere) {
		String returnWhere = "";
		String sql = "select * from " + tableName + " where " + initialWhere;
		SelectStatement stat = new SelectStatement(sql);
		for (int i = 0; i < stat.selectionList.size(); i++) {
			Condition cond = (Condition) stat.selectionList.get(i);
			returnWhere += tableName + "." + cond.getLhs().getColName();
			returnWhere += " " + cond.getOperatorString() + " ";
			returnWhere += " " + cond.getRightValueString() + " ";
			if (i < stat.selectionList.size() - 1) {
				returnWhere += " and ";
			}
		}

		return returnWhere;
	}

	public static void main(String[] args) {
		System.out.println("Test");

		String sql = "select  products.product_name, sum(quantity_sale) from products, sales where products.product_id = sales.product_id and date_sale > '2009-01-01' group by products.product_name";
		 
		System.out.println(sql);
		
		SelectStatement stm = new SelectStatement(sql);
		Vector orderbyList = stm.getOrderByList();
		
		System.out.println("oderby list: ");
		for (int i=0; i<orderbyList.size(); i++){
			Attribute att = (Attribute)orderbyList.get(i);
			System.out.println(att.toString());
		}
		System.out.println("selection list: ");		
		for(Object s : stm.getSelectionList()) {
			System.out.println(((Condition)s).getLhs());
		}
		

	}
}
