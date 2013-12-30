package sg.edu.nus.bestpeer.indexdata;

import java.io.Serializable;

/**
 * Exact query to query range data index 
 * @author dcsvht
 *
 */
public class ExactQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8502142495058795186L;
	private String table_name;
	private String column_name;
	private String value;

	public ExactQuery(String table_name, String column_name, String value) {
		this.table_name = table_name;
		this.column_name = column_name;
		this.value = value;
	}

	public String getTableName() {
		return table_name;
	}

	public String getColumnName() {
		return column_name;
	}

	public String getValue() {
		return value;
	}
}
