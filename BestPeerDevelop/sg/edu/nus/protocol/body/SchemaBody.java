package sg.edu.nus.protocol.body;

public class SchemaBody extends Body {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7340158556157078055L;

	String schema = null;//list of Create table query

	String[][] globalSchemas = null;
	String[][] globalSchemaColumns = null;
	
	public SchemaBody(String schema, String[][] globalSchemas, String[][] globalSchemaColumns) {
		this.schema = schema;
		this.globalSchemas = globalSchemas;
		this.globalSchemaColumns = globalSchemaColumns;
	}

	public String getSchema() {
		return schema;
	}
	
	public String[][] getGlobalSchemas (){
		return globalSchemas;
	}
	
	public String[][] getGlobalSchemaColumns (){
		return globalSchemaColumns;
	}
	
}
