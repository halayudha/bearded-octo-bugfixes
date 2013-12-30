package sg.edu.nus.protocol.body;

import java.io.Serializable;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * message body for retrieving data from a table
 * @author chris
 *
 */
public class TableRetrieval extends Body implements Serializable {

	// private members
	private static final long serialVersionUID = -1303561954772385713L;

	private PhysicalInfo physicalSender;

	// original name of the table at the peer
	private String tableName;

	// name of the table for storing the tuples
	private String storedTable;

	/**
	 * constructor
	 * @param physicalSender  sender of the message
	 * @param tname table for retrieving
	 * @param storedTable table for storing the data
	 */
	public TableRetrieval(PhysicalInfo physicalSender, String tname,
			String storedTable) {
		this.physicalSender = physicalSender;
		this.tableName = tname;
		this.storedTable = storedTable;
	}

	/**
	 * 
	 * @return the name of the table for retrieving
	 */
	public String getTable() {
		return this.tableName;
	}

	/**
	 * 
	 * @return the name of the table for storing the retrieved data
	 */
	public String getStoredTable() {
		return this.storedTable;
	}

	/**
	 * 
	 * @return the requestor's IP
	 */
	public PhysicalInfo getSender() {
		return this.physicalSender;
	}

	@Override
	public String toString() {
		String delim = ":";
		// String result =
		// "TABLERETRIEVAL MESSAGE format:= requestor IP:table name\r\n";
		// result = this.physicalSender.serialize() + delim + this.tableName;
		// return result;
		return this.physicalSender.serialize() + delim + this.tableName;
	}
}
