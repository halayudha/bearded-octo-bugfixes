/**
 * Created on Sep 1, 2008
 */
package sg.edu.nus.protocol.body;

import sg.edu.nus.bestpeer.indexdata.ExactQuery;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * @author David Jiang
 * 
 */
public class TableSearchBody extends Body {

	/**
	 * VersionID for serialization
	 */
	private static final long serialVersionUID = 4501988965543412702L;

	private PhysicalInfo physicalSender;
	private LogicalInfo logicalSender;
	private PhysicalInfo physicalRequester;
	private LogicalInfo logicalRequester;
	private LogicalInfo logicalDestination;
	private String tableName;

	private String eventName;

	// added by VHTam
	private ExactQuery exactQuery;

	public void setPhysicalSender(PhysicalInfo physicalSender) {
		this.physicalSender = physicalSender;
	}

	public PhysicalInfo getPhysicalSender() {
		return physicalSender;
	}

	public void setEventName(String event) {
		this.eventName = event;
	}

	public String getEventName() {
		return this.eventName;
	}

	/**
	 * @param logicalSender
	 *            the logicalSender to set
	 */
	public void setLogicalSender(LogicalInfo logicalSender) {
		this.logicalSender = logicalSender;
	}

	/**
	 * @return the logicalSender
	 */
	public LogicalInfo getLogicalSender() {
		return logicalSender;
	}

	/**
	 * @param physicalRequester
	 *            the physicalRequester to set
	 */
	public void setPhysicalRequester(PhysicalInfo physicalRequester) {
		this.physicalRequester = physicalRequester;
	}

	/**
	 * @return the physicalRequester
	 */
	public PhysicalInfo getPhysicalRequester() {
		return physicalRequester;
	}

	/**
	 * @param logicalRequester
	 *            the logicalRequester to set
	 */
	public void setLogicalRequester(LogicalInfo logicalRequester) {
		this.logicalRequester = logicalRequester;
	}

	/**
	 * @return the logicalRequester
	 */
	public LogicalInfo getLogicalRequester() {
		return logicalRequester;
	}

	/**
	 * @param logicalDestination
	 *            the logicalDestination to set
	 */
	public void setLogicalDestination(LogicalInfo logicalDestination) {
		this.logicalDestination = logicalDestination;
	}

	/**
	 * @return the logicalDestination
	 */
	public LogicalInfo getLogicalDestination() {
		return logicalDestination;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	public void setExactQuery(ExactQuery exactQuery) {
		this.exactQuery = exactQuery;
	}

	public ExactQuery getExactQuery() {
		return exactQuery;
	}

	public String toString() {
		String msg = "TABLESEARCHBODY:" + tableName;
		return msg;
	}

}
