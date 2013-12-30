/**
 * Created on Aug 19, 2009
 */
package sg.edu.nus.bestpeer.queryprocessing;

import sg.edu.nus.sqlparser.Attribute;

/**
 * @author David Jiang
 *
 */
public class MappingColumn {
	private MappingTable table;
	private Attribute column;
	
	//VHTam
	//value of the mapped global term 
	private String globalTermValue = null;
	
	public MappingColumn(MappingTable table, Attribute column) {
		this.table = table;
		this.column = column;
	}
	
	public String getGlobalTermColumnName() {
		return column.getTabName() + "_" + column.getColName() + "_global_term"; 
	}
	public String getGlobalTermDeclaration() {
		return table.getAlias() + ".global_term as " + 
			column.getTabName() + "_" + column.getColName() + "_global_term";  
	}
	
	public void setGlobalTermValue(String globalTerm){
		globalTermValue = globalTerm;
	}
	
	public String getGlobalTermValue(){
		return globalTermValue;
	}
	
	public Attribute getColumn(){
		return column;
	}
}
