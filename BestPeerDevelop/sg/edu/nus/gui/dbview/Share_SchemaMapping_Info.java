package sg.edu.nus.gui.dbview;

public class Share_SchemaMapping_Info {
	private String nodeType = null;

	private String sourceSchema = null;
	private String newTargetSchema = null;
	private String oldTargetSchema = null;

	private String operationType = null;
	private int originalStatus = -1;

	private String columnName = null;
	private String tableName = null;

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * @return the sourceSchema
	 */
	public String getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @param sourceSchema the sourceSchema to set
	 */
	public void setSourceSchema(String sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * @return the operationType
	 */
	public String getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Share_SchemaMapping_Info() {
		super();
	}

	/**
	 * @return the newTargetSchema
	 */
	public String getNewTargetSchema() {
		return newTargetSchema;
	}

	/**
	 * @param newTargetSchema the newTargetSchema to set
	 */
	public void setNewTargetSchema(String newTargetSchema) {
		this.newTargetSchema = newTargetSchema;
	}

	/**
	 * @return the oldTargetSchema
	 */
	public String getOldTargetSchema() {
		return oldTargetSchema;
	}

	/**
	 * @param oldTargetSchema the oldTargetSchema to set
	 */
	public void setOldTargetSchema(String oldTargetSchema) {
		this.oldTargetSchema = oldTargetSchema;
	}

	public String toString() {
		return "Operation Type = " + this.operationType + ", NodeType = "
				+ this.nodeType + ", tableName = " + this.tableName
				+ ", columnName = " + this.columnName + ", sourceSchema = "
				+ this.sourceSchema + ", oldTargetSchema = "
				+ this.oldTargetSchema + ", newTargetSchema = "
				+ this.newTargetSchema + ", Original Status = "
				+ this.originalStatus;

	}

	/**
	 * @return the originalStatus
	 */
	public int getOriginalStatus() {
		return originalStatus;
	}

	/**
	 * @param originalStatus the originalStatus to set
	 */
	public void setOriginalStatus(int originalStatus) {
		this.originalStatus = originalStatus;
	}
}
