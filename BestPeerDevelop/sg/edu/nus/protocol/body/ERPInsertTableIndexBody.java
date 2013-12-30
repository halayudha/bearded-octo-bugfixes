package sg.edu.nus.protocol.body;

import java.io.Serializable;
import java.util.Vector;

import sg.edu.nus.peer.info.LocalTableIndex;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * The message body containing table indices (batch processing)
 * 
 * @author Quang Hieu Vu
 * @version 1.0 2008-05-27
 * @see sg.edu.nus.peer.event.ERPInsertTableIndexListener
 */

public class ERPInsertTableIndexBody extends Body implements Serializable {

	private static final long serialVersionUID = 6182583151001671878L;

	// physical sender
	private PhysicalInfo physicalSender;

	// indexed data
	private PhysicalInfo physicalOwner;
	private Vector<LocalTableIndex> listOfTables;

	// logical destination
	private LogicalInfo logicalDestination;

	public ERPInsertTableIndexBody(PhysicalInfo physicalSender,
			PhysicalInfo physicalOwner, Vector<LocalTableIndex> listOfTables,
			LogicalInfo logicalDestination) {
		this.physicalSender = physicalSender;
		this.physicalOwner = physicalOwner;
		this.listOfTables = listOfTables;
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

	public Vector<LocalTableIndex> getListOfTables() {
		return this.listOfTables;
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

		outMsg = "ERPINSERTTABLEINDEX";
		outMsg += "\n\t Physical Sender:" + this.physicalSender.toString();
		outMsg += "\n\t Physical Owner:" + this.physicalOwner.toString();
		outMsg += "\n\t List of Tables:";
		for (int i = 0; i < this.listOfTables.size(); i++)
			outMsg += " " + this.listOfTables.get(i).toString();
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

		outMsg = "ERPINSERTTABLEINDEX";
		outMsg += ":" + this.physicalSender.toString();
		outMsg += ":" + this.physicalOwner.toString();
		for (int i = 0; i < this.listOfTables.size(); i++)
			outMsg += ":" + this.listOfTables.get(i).toString();
		if (logicalDestination == null)
			outMsg += ":null";
		else
			outMsg += ":" + logicalDestination.toString();

		return outMsg;
	}
}
