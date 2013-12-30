/** the format of the parse SQL quer, also see readme file **/

package sg.edu.nus.sqlparser.java_cup_use;

import java.util.Vector;

import sg.edu.nus.sqlparser.Attribute;
import sg.edu.nus.sqlparser.Condition;

@SuppressWarnings("unchecked")
public class SQLQuery {
	Vector projectList; // List of project attributes from select clause

	Vector fromList; // List of tables in from clause

	Vector conditionList; // List of conditions appeared in where clause

	/** represent again the the selection and join conditions
	 in separate lists
	 **/

	Vector selectionList; // List of select predicates

	Vector joinList; // List of join predicates

	Vector groupbyList = new Vector(); // List of attibutes in groupby clause

	Vector orderbyList = new Vector(); // List of attibutes in orderby clause

	boolean isDistinct = false; // Whether distinct key word appeared in select

	// clause

	public SQLQuery(Vector list1, Vector list2, Vector list3, Vector list4) {
		projectList = list1;
		fromList = list2;
		conditionList = list3;
		groupbyList = list4;
		splitConditionList(conditionList);

	}

	public SQLQuery(Vector list1, Vector list2, Vector list3) {
		projectList = list1;
		fromList = list2;
		conditionList = list3;
		groupbyList = null;
		splitConditionList(conditionList);
	}

	// 12 july 2003 (whtok)
	// Constructor to handle no WHERE clause case
	public SQLQuery(Vector list1, Vector list2) {
		projectList = list1;
		fromList = list2;
		conditionList = null;
		groupbyList = null;
		joinList = new Vector();
		selectionList = new Vector();
	}

	/** split the condition list into selection, and join list **/

	protected void splitConditionList(Vector tempVector) {
		selectionList = new Vector();
		joinList = new Vector();
		for (int i = 0; i < tempVector.size(); i++) {
			Condition cn = (Condition) tempVector.elementAt(i);
			if (cn.getOpType() == Condition.SELECT)
				selectionList.add(cn);
			else
				joinList.add(cn);
		}
	}

	public void setIsDistinct(boolean flag) {
		isDistinct = flag;
	}

	public boolean isDistinct() {
		return isDistinct;
	}

	public Vector getProjectList() {
		return projectList;
	}

	public Vector getFromList() {
		return fromList;
	}

	public Vector getConditionList() {
		return conditionList;
	}

	public Vector getSelectionList() {
		return selectionList;
	}

	public Vector getJoinList() {
		return joinList;
	}

	public void setGroupByList(Vector list) {
		groupbyList = list;
	}

	public Vector getGroupByList() {
		return groupbyList;
	}

	public void setOrderByList(Vector list) {
		orderbyList = list;
	}

	public Vector getOrderByList() {
		return orderbyList;
	}

	public int getNumJoin() {
		if (joinList == null)
			return 0;

		return joinList.size();
	}

	public void printSQLQueryInfo() {

		System.out.println("--------Tables in query: ");
		Vector tableList = this.getFromList();
		for (int i = 0; i < tableList.size(); i++) {
			System.out.println(tableList.get(i));
		}

		System.out.println("--------Column projected in query: ");
		Vector projectionList = this.getProjectList();
		for (int i = 0; i < projectionList.size(); i++) {
			Attribute att = (Attribute) projectionList.get(i);
			System.out.println(att.getTabName() + "." + att.getColName());
		}

		System.out.println("--------Selection in query: ");
		Vector selectionList = this.getSelectionList();
		for (int i = 0; i < selectionList.size(); i++) {
			Condition cond = (Condition) selectionList.get(i);
			Attribute leftAtt = cond.getLhs();
			String rightValue = (String) cond.getRhs();
			System.out.println(leftAtt.getTabName() + "."
					+ leftAtt.getColName() + " vs. " + rightValue);
		}

		System.out.println("--------Join in query: ");
		Vector joinList = this.getJoinList();
		for (int i = 0; i < joinList.size(); i++) {
			Condition cond = (Condition) joinList.get(i);
			Attribute leftAtt = cond.getLhs();
			Attribute rightAtt = (Attribute) cond.getRhs();
			System.out.println(leftAtt.getTabName() + "."
					+ leftAtt.getColName() + " vs. " + rightAtt.getTabName()
					+ "." + rightAtt.getColName());
		}

	}

}
