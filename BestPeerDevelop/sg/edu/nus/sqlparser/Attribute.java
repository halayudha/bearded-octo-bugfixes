/** Attibute or column meta data **/

package sg.edu.nus.sqlparser;

import java.io.Serializable;

public class Attribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7086499381008305936L;

	/** enumerating type of attribute **/
	String tblname = null; // tabel to which this attribute belongs

	String colname = null; // name of the attribute **/

	public Attribute(String tbl, String col) {
		tblname = tbl;
		colname = col;
	}

	public Attribute(String col) {
		colname = col;
	}

	public void setTabName(String tab) {
		tblname = tab;
	}

	public String getTabName() {
		return tblname;
	}

	public void setColName(String col) {
		colname = col;
	}

	public String getColName() {
		return colname;
	}

	public boolean equals(Attribute attr) {
		if (this.tblname.equals(attr.getTabName())
				&& this.colname.equals(attr.getColName()))
			return true;
		else
			return false;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Attribute) 
			return this.toString().equalsIgnoreCase(obj.toString());
		return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public Object clone() {
		String newtbl = tblname;
		String newcol = colname;
		Attribute newattr = new Attribute(newtbl, newcol);
		newattr.aggregateType = this.aggregateType;
		return newattr;
	}

	public String toString() {
		String fullName;
		fullName = tblname == null ? colname : tblname + "." + colname;
		if(isAggregate() == false)
			return fullName;
		return getAggregateFunction() + "(" + fullName + ")";
	}

	String[] aggregateTypes = { "sum", "max", "min", "avg", "count" };
	int aggregateType = -1;

	public void setAggregate(int aggregateType){
		this.aggregateType = aggregateType;
	}
	
	public void checkAggregate() {
		if (tblname == null) {
			for (int i = 0; i < aggregateTypes.length; i++) {
				if (colname.contains(aggregateTypes[i] + "(")
						|| colname.contains(aggregateTypes[i] + " (")) {
					aggregateType = i;
					// recompute
					int pos1 = colname.indexOf("(");
					int pos2 = colname.indexOf(")");
					String newColName = colname.substring(pos1 + 1, pos2);
					newColName = newColName.trim();
					
					colname = newColName;
					
					break;
				}
			}
		}
		else { //tableName = "count(product" and colName = "product_id)"
			for (int i = 0; i < aggregateTypes.length; i++) {
				if (tblname.contains(aggregateTypes[i] + "(")
						|| tblname.contains(aggregateTypes[i] + " (")) {
					aggregateType = i;
					// recompute
					int pos1 = tblname.indexOf("(");
					tblname = tblname.substring(pos1+1);
					tblname = tblname.trim();
					
					int pos2 = colname.indexOf(")");
					String newColName = colname.substring(0, pos2);
					newColName = newColName.trim();
					colname = newColName;
					
					break;
				}
			}
			
		}
	}

	public boolean isAggregate() {
		return (aggregateType >= 0);
	}

	public String getAggregateFunction() {
		if (aggregateType >= 0) {
			return aggregateTypes[aggregateType];
		}
		return null;
	}

	public static void main(String[] args) {
		Attribute att = new Attribute(null, "max ( t.a )");
		System.out.println(att.tblname);
		System.out.println(att.colname);
		System.out.println(att.getAggregateFunction());

		att.checkAggregate();

		System.out.println(att.tblname);
		System.out.println(att.colname);
		System.out.println(att.getAggregateFunction());
	}
}
