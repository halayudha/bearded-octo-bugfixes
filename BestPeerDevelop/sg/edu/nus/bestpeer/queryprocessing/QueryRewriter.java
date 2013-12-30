/**
 * Created on Aug 18, 2009
 */
package sg.edu.nus.bestpeer.queryprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Vector;

import sg.edu.nus.sqlparser.Attribute;
import sg.edu.nus.sqlparser.Condition;
import sg.edu.nus.sqlparser.SelectStatement;
import sg.edu.nus.util.MetaDataAccess;

/**
 * @author David Jiang
 *
 */
public class QueryRewriter {
	
	String userName = null;
	
	LinkedHashSet<String> projSet = new LinkedHashSet<String>();
	HashSet<String> tableList = new HashSet<String>();
	HashSet<Condition> condList = new HashSet<Condition>();
	
	private LinkedHashSet<String> global_term_columns = new LinkedHashSet<String>();
	private LinkedHashSet<String> groupByList = new LinkedHashSet<String>();
	
	//VHTam
	//storing all the mapping columns of this table
	private Vector<MappingColumn> mappingColumns = new Vector<MappingColumn>();
	public Vector<MappingColumn> getMappingColumns(){
		return mappingColumns;
	}
	
	//VHTam
	//add the appended global columns
	//for the case downloading single table for later locally joining on them
	Vector<String> appendedGlobalTermColumns = new Vector<String>();
	
	
	public QueryRewriter(String userName) {
		this.userName = new String(userName);
	}
	
	public String[] getTermColumns() {
		return global_term_columns.toArray(new String[0]);
	}
	
	private String generateSQL(SelectStatement stmt, String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		String proj_list = projSet.toString();
		proj_list = proj_list.substring(1, proj_list.length() - 1);
		sb.append(proj_list);
		
		sb.append("\nfrom ").append(table);
		for(String mappingTable : tableList)
			sb.append(", ").append(mappingTable);
		
		if (condList.isEmpty() == false) {
			sb.append("\nwhere ");
			String whereClause = condList.toString();
			whereClause = whereClause.substring(1, whereClause.length() - 1);
			whereClause = whereClause.replace(",", " and");
			sb.append(whereClause);
		}
					
		if(groupByList.isEmpty() == false){
			sb.append("\ngroup by ");
			String groupBy = groupByList.toString();
			sb.append(groupBy.substring(1, groupBy.length() - 1));
		}
		return sb.toString();

	}
	
	private String getGlobalTerm(String table, String column, Object localTerm) {
		return MetaDataAccess.localTermToGlobalTerm((String)localTerm, table, column, userName);
	}
	
	private String getGlobalTerm(Attribute column, Object localTerm) {
		return getGlobalTerm(column.getTabName(), column.getColName(), localTerm);
	}
	
	private boolean isMappingColumn(String table, String col) {
		return MetaDataAccess.isMappingColumn(col, table);
	}
	
	private boolean isMappingColumn(Attribute column) {
		return isMappingColumn(column.getTabName(), column.getColName());
	}
	
	private Attribute getFullColumnName(Attribute col, String table_name) {
		if(col.getTabName() == null)
			col.setTabName(table_name);
		return col;
	}
	
	private String getGlobalTermAlias(Attribute column) {
		StringBuilder builder = new StringBuilder();
		builder.append(column.getTabName()).append("_").append(column.getColName());
		builder.append("_global_term");
		return builder.toString();
	}
	
	private void rewriteGroupBy(SelectStatement stmt, String table) {
		String[] col_list = projSet.toArray(new String[0]);
		for(Object item : stmt.getGroupByList()) {
			Attribute column = getFullColumnName((Attribute)item, table);
			if(isMappingColumn(column) == false) {
				groupByList.add(column.toString());
				continue;
			}
			MappingTable mtable = new MappingTable(column);
			String alias = getGlobalTermAlias(column);
			Attribute global_term = new Attribute(mtable.getAlias(), "global_term");
			
			for(int i = 0; i < col_list.length; ++i) {
				if(col_list[i].equalsIgnoreCase(column.toString()))
					col_list[i] = global_term + " as " + alias;
			}
			
			//global_term_columns.add(alias);
			Condition joinCond = new Condition(column, 
					Condition.EQUAL, new Attribute(mtable.getAlias(), "local_term"));
			joinCond.setOpType(Condition.JOIN);
			condList.add(joinCond);
			tableList.add(mtable.getTableDeclaration());
			groupByList.add(alias);
			
			Condition tableCond = new Condition(
					new Attribute(mtable.getAlias(), "table_name"), 
					Condition.EQUAL, column.getTabName());
			Condition colCond = 
				new Condition(new Attribute(mtable.getAlias(), "column_name"),
						Condition.EQUAL, column.getColName());
			condList.add(tableCond);
			condList.add(colCond);
			
		}
		projSet = new LinkedHashSet<String>(Arrays.asList(col_list));
	}

	
	private void rewriteProject(SelectStatement stmt, String table) {
		if (stmt.getProjectionList().isEmpty()) {
			projSet.add("*");
			return;
		}
		for (Object item : stmt.getProjectionList()) {
			Attribute proj_item = getFullColumnName((Attribute) item, table);
			if (proj_item.isAggregate()) {
				if (isMappingColumn(proj_item) == false) {
					projSet.add(proj_item.toString());
					continue;
				}
				MappingTable mTable = new MappingTable(proj_item);
				Attribute aggr = (Attribute) proj_item.clone();
				aggr.setTabName(mTable.getAlias());
				aggr.setColName("global_term");
				projSet.add(aggr.toString());
				proj_item.setAggregate(-1);
				projSet.remove(proj_item.toString());
				tableList.add(mTable.getTableDeclaration());
				Condition joinCond = new Condition(proj_item, Condition.EQUAL,
						new Attribute(mTable.getAlias(), "local_term"));
				joinCond.setOpType(Condition.JOIN);
				condList.add(joinCond);
			} 
			else {
				projSet.add(proj_item.toString());
				if (isMappingColumn(proj_item)) {
					boolean existInSelection = columnExistInSelection(proj_item, stmt);
					boolean existInGroupBy = columnExistInGroupBy(proj_item, stmt);
					if (!existInSelection && !existInGroupBy)
						appendedGlobalTermColumns.add(proj_item.getColName());
				}
			}
		}
		
		//VHTam
		//add the appended global columns
		//for the case downloading single table for later locally joining on them
		for (String appendCol: appendedGlobalTermColumns){
			Attribute proj_item = new Attribute(table, appendCol);
			MappingTable mTable = new MappingTable(proj_item);
			MappingColumn mCol = new MappingColumn(mTable, proj_item);

			projSet.add(mCol.getGlobalTermDeclaration());
			global_term_columns.add(mCol.getGlobalTermColumnName());
			
			tableList.add(mTable.getTableDeclaration());
			
			Condition joinCond = new Condition(proj_item,
					Condition.EQUAL, new Attribute(mTable.getAlias(),
							"local_term"));
			joinCond.setOpType(Condition.JOIN);
			condList.add(joinCond);
			
			Condition tableCond = new Condition(
					new Attribute(mTable.getAlias(), "table_name"), 
					Condition.EQUAL, proj_item.getTabName());
			Condition colCond = 
				new Condition(new Attribute(mTable.getAlias(), "column_name"),
						Condition.EQUAL, proj_item.getColName());
			condList.add(tableCond);
			condList.add(colCond);
			
			mappingColumns.add(mCol);
		}
	}
	
	private boolean columnExistInSelection(Attribute proj_item, SelectStatement stmt){
		boolean existInSelection = false;
		for(Object selectItem : stmt.getSelectionList()) {
			Condition cond = (Condition)selectItem;
			if (proj_item.getColName().equals(cond.getLhs().getColName())){
				existInSelection = true;
			}
		}		
		return existInSelection;
	}

	private boolean columnExistInGroupBy(Attribute proj_item, SelectStatement stmt){
		boolean exist = false;
		for(Object item : stmt.getGroupByList()) {
			Attribute att = (Attribute)item;
			if (proj_item.getColName().equals(att.getColName())){
				exist = true;
			}
		}		
		return exist;
	}
	
	private void rewriteSelection(SelectStatement stmt, String table) {
		for(Object item : stmt.getSelectionList()) {
			Condition origCond = (Condition)item;
			Attribute column = getFullColumnName(origCond.getLhs(), table);
			if(isMappingColumn(column) == false) {
				condList.add(origCond);
				continue;
			}
			MappingTable mTable = new MappingTable(column);
			MappingColumn mCol = new MappingColumn(mTable, column);
			projSet.add(mCol.getGlobalTermDeclaration());
			global_term_columns.add(mCol.getGlobalTermColumnName());
			Condition termCond = 
				new Condition(new Attribute(mTable.getAlias(), "global_term"), 
						origCond.getExprType(), 
						getGlobalTerm(column, origCond.getRhs()));
			Condition tableCond = new Condition(
					new Attribute(mTable.getAlias(), "table_name"), 
					Condition.EQUAL, column.getTabName());
			Condition colCond = 
				new Condition(new Attribute(mTable.getAlias(), "column_name"),
						Condition.EQUAL, column.getColName());
			condList.add(termCond);
			condList.add(tableCond);
			condList.add(colCond);
			Condition joinCond = 
				new Condition(column, Condition.EQUAL, 
						new Attribute(mTable.getAlias(), "local_term"));
			joinCond.setOpType(Condition.JOIN);
			condList.add(joinCond);
			tableList.add(mTable.getTableDeclaration());
			//VHTam
			String globalTerm = getGlobalTerm(column, origCond.getRhs());
			mCol.setGlobalTermValue(globalTerm);
			mappingColumns.add(mCol);
		}
	}
	
	public String rewriteSingleTableQuery(SelectStatement stmt) {
		String table = (String) stmt.getTableList().get(0);
		rewriteProject(stmt, table);
		rewriteSelection(stmt, table);
		rewriteGroupBy(stmt, table);
		return generateSQL(stmt, table);
	}
	
	//VHTam
	public String rewriteMultiTableQuery(SelectStatement stmt, Vector oldTables, Vector newTables, Vector<MappingColumn> mappingColumns){

		//retrieve clauses from stmt
		Vector tableList = stmt.getTableList(); // from clause
		Vector projectionList = stmt.getProjectionList(); // select clause
		Vector selectionList = stmt.getSelectionList(); // where clause
		Vector joinList = stmt.getJoinList();
		Vector groupbyList = stmt.getGroupByList();
		Vector orderbyList = stmt.getOrderByList();

		//replace table names by downloaded tables
		for (int t = 0; t < oldTables.size(); t++) {
			String oldTab = (String) oldTables.get(t);
			String newTab = (String) newTables.get(t);

			for (int i = 0; i < projectionList.size(); i++) {
				Attribute att = (Attribute) projectionList.get(i);
				if (att.getTabName() != null) {
					if (att.getTabName().equals(oldTab))
						att.setTabName(newTab);
				}
			}
			
			for (int i = 0; i < groupbyList.size(); i++) {
				Attribute att = (Attribute) groupbyList.get(i);
				if (att.getTabName() != null) {
					if (att.getTabName().equals(oldTab))
						att.setTabName(newTab);
				}
			}			

			for (int i = 0; i < orderbyList.size(); i++) {
				Attribute att = (Attribute) orderbyList.get(i);
				if (att.getTabName() != null) {
					if (att.getTabName().equals(oldTab))
						att.setTabName(newTab);
				}
			}			

			for (int i = 0; i < tableList.size(); i++) {
				String table = (String) tableList.get(i);
				if (table.equals(oldTab)) {
					tableList.remove(i);
					tableList.add(i, newTab);
				}
			}

			for (int i = 0; i < selectionList.size(); i++) {
				Condition cond = (Condition) selectionList.get(i);
				if (cond.getLhs().getTabName() != null)
					if (cond.getLhs().getTabName().equals(oldTab))
						cond.getLhs().setTabName(newTab);
			}

			for (int i = 0; i < joinList.size(); i++) {
				Condition cond = (Condition) joinList.get(i);
				if (cond.getLhs().getTabName().equals(oldTab))
					cond.getLhs().setTabName(newTab);
				if (((Attribute) (cond.getRhs())).getTabName().equals(oldTab))
					((Attribute) (cond.getRhs())).setTabName(newTab);
			}
		}
		
		//regenerate query
		
		String sql = "select ";
		if (projectionList.size() == 0) {// select *
			sql += " * ";
		} else {

			for (int i = 0; i < projectionList.size(); i++) {

				Attribute att = (Attribute) projectionList.get(i);
				//check if mapping column		
				for (MappingColumn mappingColumn : mappingColumns) {
					if (att.getColName().equals(mappingColumn.getColumn().getColName())){
						if (isGroupbyColumn(att.getColName(), groupbyList))
							att.setColName(mappingColumn.getGlobalTermColumnName());
					}
				}				

				String colStr = att.toString();

				if (i < projectionList.size() - 1) {
					colStr += ", ";
				}
				sql += colStr;
			}
			//append global term column
			for (MappingColumn mappingColumn : mappingColumns) {
				if (!columnExistInGroupBy(mappingColumn.getColumn(), stmt)){
					String colStr = ", " + mappingColumn.getGlobalTermColumnName();
					sql += colStr;
				}
			}
		}
		
		sql += " \nfrom ";
		for (int i = 0; i < tableList.size(); i++) {
			String table = (String) tableList.get(i);
			if (i < tableList.size() - 1) {
				sql += table + ", ";
			} else {
				sql += table;
			}
		}

		if (selectionList.size() != 0 || joinList.size() != 0) {
			sql += " \nwhere ";
			
		}

		for (int i = 0; i < selectionList.size(); i++) {
			Condition cond = (Condition) selectionList.get(i);
			//check if mapping column		
			for (MappingColumn mappingColumn : mappingColumns) {
				if (cond.getLhs().getColName().equals(mappingColumn.getColumn().getColName())){
					cond.getLhs().setColName(mappingColumn.getGlobalTermColumnName());
					cond.setRhs(mappingColumn.getGlobalTermValue());
				}
			}
			String colStr = cond.getLhs().toString();
			String value = cond.getRightValueString();			
			String str = colStr + cond.getOperatorString()+ value;
			sql += str;
			if (i < selectionList.size() - 1) {
				sql += " and ";
			}
		}

		if (selectionList.size() > 0 && joinList.size() > 0) {
			sql += " and ";
		}

		for (int i = 0; i < joinList.size(); i++) {
			Condition cond = (Condition) joinList.get(i);
			//check if mapping column		
			for (MappingColumn mappingColumn : mappingColumns) {
				if (cond.getLhs().getColName().equals(mappingColumn.getColumn().getColName())){
					cond.getLhs().setColName(mappingColumn.getGlobalTermColumnName());
				}
				if (((Attribute)cond.getRhs()).getColName().equals(mappingColumn.getColumn().getColName())){
					((Attribute)cond.getRhs()).setColName(mappingColumn.getGlobalTermColumnName());
				}
			}
			String str = cond.getLhs().toString() + cond.getOperatorString()
					+ cond.getRhs().toString();
			sql += str;
			if (i < joinList.size() - 1) {
				sql += " and ";
			}
		}
		
		if (groupbyList.size() > 0) {
			sql += " group by ";
			for (int i = 0; i < groupbyList.size(); i++) {
				Attribute att = (Attribute) groupbyList.get(i);
				//check if mapping column		
				for (MappingColumn mappingColumn : mappingColumns) {
					if (att.getColName().equals(mappingColumn.getColumn().getColName())){
						att.setColName(mappingColumn.getGlobalTermColumnName());
					}
				}				
				sql += att.toString();
				if (i < groupbyList.size() - 1) {
					sql += ", ";
				}
			}
		}
		
		if (orderbyList.size() > 0) {
			sql += " order by ";
			for (int i = 0; i < orderbyList.size(); i++) {
				Attribute att = (Attribute) orderbyList.get(i);
				sql += att.toString();
				if (i < orderbyList.size() - 1) {
					sql += ", ";
				}
			}
		}

		return sql;
	}
	
	private boolean isGroupbyColumn(String colName, Vector groupbyList){
		for (Object obj: groupbyList){
			Attribute att = (Attribute)obj;
			if (att.getColName().equals(colName)){
				return true;
			}
		}
		return false;
	}
}
