package sg.edu.nus.protocol.body;

import java.io.Serializable;
import java.util.Vector;

import sg.edu.nus.peer.info.LocalDataIndex;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * The message body containing data indices (batch processing)
 * 
 * @author Quang Hieu Vu
 * @version 1.0 2008-05-27
 * @see sg.edu.nus.peer.event.ERPInsertDataIndexListener
 */

public class ERPInsertDataIndexBody extends Body implements Serializable {

	private static final long serialVersionUID = -7971653701601528811L;

	/**
	 * The index data is numeric type.
	 */
	public final static int NUMERIC_TYPE = 0;

	/** 
	 * The index data is string type.
	 */
	public final static int STRING_TYPE = 1;

	// physical sender
	private PhysicalInfo physicalSender;

	// indexed data
	private PhysicalInfo physicalOwner;
	private Vector<LocalDataIndex> listOfTerms;
	private int termFormat;
	private LogicalInfo logicalDestination;

	public ERPInsertDataIndexBody(PhysicalInfo physicalSender,
			PhysicalInfo physicalOwner, Vector<LocalDataIndex> listOfTerms,
			int termFormat, LogicalInfo logicalDestination) {
		this.physicalSender = physicalSender;
		this.physicalOwner = physicalOwner;
		this.termFormat = termFormat;
		this.listOfTerms = listOfTerms;
		this.logicalDestination = logicalDestination;
	}

	public void setPhysicalSender(PhysicalInfo physicalSender) {
		this.physicalSender = physicalSender;
	}

	public PhysicalInfo getPhysicalSender() {
		return this.physicalSender;
	}

	public PhysicalInfo getPhysicalOwner() {
		return this.physicalOwner;
	}

	public Vector<LocalDataIndex> getListOfTerms() {
		return this.listOfTerms;
	}

	public int getTermFormat() {
		return this.termFormat;
	}

	public void setLogicalDestination(LogicalInfo logicalDestination) {
		this.logicalDestination = logicalDestination;
	}

	public LogicalInfo getLogicalDestination() {
		return this.logicalDestination;
	}

	/**
	 * Return a readable string for testing or writing in the log file 
	 * 
	 * @return a readable string
	 */
	public String getString() {
		String outMsg;

		outMsg = "ERPINSERTDATAINDEX";
		outMsg += "\n\t Physical Sender:" + this.physicalSender.toString();
		outMsg += "\n\t Physical Owner:" + this.physicalOwner.toString();
		outMsg += "\n\t Term Format:" + this.termFormat;
		outMsg += "\n\t List of Terms:";
		for (int i = 0; i < this.listOfTerms.size(); i++)
			outMsg += " " + this.listOfTerms.get(i).toString();
		if (this.logicalDestination == null)
			outMsg += "\n\t Logical Destination:null";
		else
			outMsg += "\n\t Logical Destination:"
					+ logicalDestination.toString();

		return outMsg;
	}

	@Override
	public String toString() {
		String outMsg;

		outMsg = "ERPINSERTDATAINDEX";
		outMsg += ":" + this.physicalSender.toString();
		outMsg += ":" + this.physicalOwner.toString();
		outMsg += ":" + this.termFormat;
		for (int i = 0; i < this.listOfTerms.size(); i++)
			outMsg += ":" + this.listOfTerms.get(i).toString();
		if (logicalDestination == null)
			outMsg += ":null";
		else
			outMsg += ":" + logicalDestination.toString();

		return outMsg;
	}
}
