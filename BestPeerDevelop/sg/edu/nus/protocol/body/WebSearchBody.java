package sg.edu.nus.protocol.body;

/**
 * This class represents a query request from the
 * web interface
 * @author wusai
 *
 */
public class WebSearchBody extends Body {
	// private members
	/**
	 * The serialVersionUID is used for serializing and de-serializing
	 * this class and be SURE NOT CHANGE THIS VALUE!
	 */
	private static final long serialVersionUID = 6232083108201833065L;

	/**
	 * sql query to be processed
	 */
	private String sql = "";

	/**
	 * query id
	 */
	private long id = 0;

	/**
	 * constructor
	 * @param sql
	 * @param id
	 */
	public WebSearchBody(String sql, long id) {
		this.sql = sql;
		this.id = id;
	}

	/**
	 * 
	 * @return the sql query to be processed
	 */
	public String getSQLQuery() {
		return sql;
	}

	/**
	 * each query has a unique id 
	 * @return id of current query
	 */
	public long getQID() {
		return id;
	}

	/**
	 * Override <code>toString()</code> function of <code>java.lang.Object</code>.
	 * 
	 * @return A string that describes the content of the body.
	 */
	public String toString() {
		String delim = ":";

		String result = "WebSearchBody format:= queryid:sql\r\n";
		result += id + delim + sql;

		return result;
	}

}
