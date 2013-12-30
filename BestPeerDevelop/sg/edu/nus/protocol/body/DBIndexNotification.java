package sg.edu.nus.protocol.body;

/**
 * the message is used to notify other peers
 * to create tuple index.
 * @author Wu Sai
 *
 */

public class DBIndexNotification extends Body {

	// private members
	private static final long serialVersionUID = 982016576889209837L;

	/**
	 * relation name
	 */
	private String relation;

	/**
	 * attribute name
	 */
	private String attribute;

	/**
	 * min value in the range to be indexed
	 */
	private String min;

	/**
	 * max value in the range to be indexed
	 */
	private String max;

	public DBIndexNotification(String relation, String attribute, String min,
			String max) {
		this.relation = relation;
		this.attribute = attribute;
		this.min = min;
		this.max = max;
	}

	public String getRelation() {
		return this.relation;
	}

	public String getAttribute() {
		return this.attribute;
	}

	public String getMin() {
		return this.min;
	}

	public String getMax() {
		return this.max;
	}

}
