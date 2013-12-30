package sg.edu.nus.bestpeer.indexdata;

import java.io.Serializable;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * range data index: min-max value of a specific column of a table
 * @author VHTam
 *
 */

public class RangeIndex implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * relation name
	 */
	private String relation;

	/**
	 * attribute name
	 */
	private String attribute;

	/**
	 * attribute type
	 */
	private int type;
	public static final int NUMERIC_TYPE = 1;
	public static final int STRING_TYPE = 2;

	/**
	 * the range value, although we
	 * represent them as string values,
	 * they can be casted to integer
	 * if necessary
	 */
	private String minValue;

	private String maxValue;

	/**
	 * peer having data in this range
	 */
	private PhysicalInfo owner;

	public RangeIndex(String relation, String attribute, int type, String min,
			String max, PhysicalInfo owner) {
		this.relation = relation;
		this.attribute = attribute;
		this.type = type;
		this.minValue = min;
		this.maxValue = max;
		this.owner = owner;
	}

	public int getType() {
		return this.type;
	}

	public String getMinValue(){
		String result = this.minValue;
		if (type==STRING_TYPE){
			result="'"+result+"'";
		}		
		return result;
	}

	public String getMaxValue(){
		String result = this.maxValue;
		if (type==STRING_TYPE){
			result="'"+result+"'";
		}
		return result;
	}
	
	public boolean isStringType(){
		return type==STRING_TYPE;
	}

	public String getTableName() {
		return this.relation;
	}

	public String getColumnName() {
		return this.attribute;
	}

	public PhysicalInfo getOwner() {
		return this.owner;
	}

	public boolean isSatisfied(String relation, String attribute) {
		if (relation.equals(this.relation) && attribute.equals(this.attribute))
			return true;
		else
			return false;
	}

	public String serialize() {
		String msg = "";
		msg += this.relation + "$" + this.attribute + "$" + this.type + "$"
				+ this.minValue + "$" + this.maxValue + "$";
		PhysicalInfo ip = owner;
		msg += ip.serialize();
		return msg;
	}
	
	public String toString(){
		return serialize();
	}
}
