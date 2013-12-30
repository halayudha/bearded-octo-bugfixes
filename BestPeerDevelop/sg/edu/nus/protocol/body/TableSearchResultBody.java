/**
 * Created on Sep 2, 2008
 */
package sg.edu.nus.protocol.body;

import java.util.ArrayList;
import java.util.StringTokenizer;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * @author David Jiang
 * 
 */
public class TableSearchResultBody extends Body {

	/**
	 * SerialID for persistence
	 */
	private static final long serialVersionUID = 2469055855561680532L;

	private String tableName;
	private ArrayList<PhysicalInfo> tableOwners;
	private String eventName;
	private boolean isFound;

	public TableSearchResultBody(String owners, boolean found) {
		setFound(found);
		tableOwners = new ArrayList<PhysicalInfo>();
		if (owners != null)
			setTableOwners(owners);
	}

	/**
	 * @param isFound
	 *            the isFound to set
	 */
	public void setFound(boolean isFound) {
		this.isFound = isFound;
	}

	public void setEventName(String event) {
		this.eventName = event;
	}

	public String getEventName() {
		return this.eventName;
	}

	/**
	 * @return the isFound
	 */
	public boolean isFound() {
		return isFound;
	}

	/**
	 * @param tableOwners
	 *            the tableOwners to set
	 */
	public void setTableOwners(ArrayList<PhysicalInfo> tableOwners) {
		this.tableOwners = tableOwners;
	}

	/**
	 * @param tableOwnersString
	 *            the tableOwners to set
	 */
	public void setTableOwners(String tableOwnersString) {
		if (tableOwnersString == null)
			return;

		StringTokenizer tokenizer = new StringTokenizer(tableOwnersString, ":");
		while (tokenizer.hasMoreTokens()) {
			tableOwners.add(new PhysicalInfo(tokenizer.nextToken()));
		}
	}

	/**
	 * @return the tableOwners
	 */
	public ArrayList<PhysicalInfo> getTableOwners() {
		return tableOwners;
	}

	public static void main(String args[]) {
		TableSearchResultBody body = new TableSearchResultBody(
				"127.0.0.1_3001:172.12.34.17_3002", true);
		System.out.println(body.getTableOwners());
		body = new TableSearchResultBody("", true);
		System.out.println(body.getTableOwners());
		body.setTableOwners("176.12.13.10_3003:123.12.13.12_3004");
		System.out.println(body.getTableOwners());
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
}
