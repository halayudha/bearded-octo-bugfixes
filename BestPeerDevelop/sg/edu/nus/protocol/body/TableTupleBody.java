package sg.edu.nus.protocol.body;

import java.io.Serializable;
import java.util.Vector;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * message that contains the tuples from a table
 * 
 * @author chris
 * 
 */
public class TableTupleBody extends Body implements Serializable {

	// private members
	private static final long serialVersionUID = -1950356477312385713L;

	private PhysicalInfo sender;

	private Vector<String[]> data;

	private String storedData;

	private boolean isFinished;

	private int totalCount = 0;

	/**
	 * constructor
	 * 
	 * @param sender
	 * @param data
	 * @param storedData
	 */
	public TableTupleBody(PhysicalInfo sender, Vector<String[]> data,
			String storedData, int totalCount) {
		this.sender = sender;
		this.data = data;
		this.storedData = storedData;
		this.isFinished = false;
		this.totalCount = totalCount;
	}

	public PhysicalInfo getSender() {
		return this.sender;
	}

	public Vector<String[]> getData() {
		return this.data;
	}

	public String getTableName() {
		return this.storedData;
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	public void setFinishFlag() {
		this.isFinished = true;
	}

	public int getCount() {
		return this.totalCount;
	}

	public String toString() {
		String delim = ":";
		// String result =
		// "TABLETUPLE MESSAGE format:= sender IP:table name\r\n";
		// result = this.sender.serialize() + delim + this.storedData + "\r\n";
		// return result;
		return this.sender.serialize() + delim + this.storedData;
	}
}
