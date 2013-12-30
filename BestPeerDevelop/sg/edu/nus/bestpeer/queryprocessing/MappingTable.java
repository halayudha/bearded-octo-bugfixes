/**
 * Created on Aug 19, 2009
 */
package sg.edu.nus.bestpeer.queryprocessing;

import sg.edu.nus.sqlparser.Attribute;

/**
 * @author David Jiang
 *
 */
public class MappingTable {
	private Attribute column;
	public static String MAPPING_TBL = "mapping_table"; 
	
	public MappingTable() { column = null; }
	
	public MappingTable(Attribute column) { this.column = column; }
	
	public void setMappingColumn(Attribute column) {
		this.column = column;
	}
	
	public Attribute getMappingColumn() {
		return column;
	}
	
	public String getAlias() {
		StringBuilder builder = new StringBuilder();
		builder.append("j_").append(column.getTabName()).append("_");
		builder.append(column.getColName());
		return builder.toString();
	}
	
	public String getTableDeclaration() {
		StringBuffer buf = new StringBuffer(MAPPING_TBL); 
		buf.append(" as ").append(getAlias());
		return buf.toString();
	}
}
