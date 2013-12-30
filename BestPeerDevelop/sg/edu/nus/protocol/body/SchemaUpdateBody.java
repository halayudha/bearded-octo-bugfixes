/**
 * 
 */
package sg.edu.nus.protocol.body;

/**
 * @author mihailupu
 * 
 * A schema update is a message sent from the bootstrap to a superpeer to indicate a new global schema
 */
public class SchemaUpdateBody extends Body {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8584194468524459764L;
	String schema;

	/**
	 * 
	 */
	public SchemaUpdateBody(String schema) {
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}
