package sg.edu.nus.protocol.body;

import java.io.Serializable;
import java.util.Vector;

import sg.edu.nus.peer.info.LocalColumnIndex;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * The message body containing column indices (batch processing)
 * 
 * @author Quang Hieu Vu
 * @version 1.0 2008-05-27
 * @see sg.edu.nus.peer.event.ERPInsertColumnIndexListener
 */

public class ERPInsertColumnIndexBody extends Body implements Serializable {

	private static final long serialVersionUID = -6056008954332470893L;

	// physical sender
	private PhysicalInfo physicalSender;

	// indexed data
	private PhysicalInfo physicalOwner;
	private Vector<LocalColumnIndex> listOfColumns;
	private LogicalInfo logicalDestination;

	public ERPInsertColumnIndexBody(PhysicalInfo physicalSender,
			PhysicalInfo physicalOwner, Vector<LocalColumnIndex> listOfColumns,
			LogicalInfo logicalDestination) {
		this.physicalSender = physicalSender;
		this.physicalOwner = physicalOwner;
		this.listOfColumns = listOfColumns;
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

	public Vector<LocalColumnIndex> getListOfColumns() {
		return this.listOfColumns;
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

		outMsg = "ERPINSERTCOLUMNINDEX";
		outMsg += "\n\t Physical Sender:" + this.physicalSender.toString();
		outMsg += "\n\t Physical Owner:" + this.physicalOwner.toString();
		outMsg += "\n\t List of Columns:";
		for (int i = 0; i < this.listOfColumns.size(); i++)
			outMsg += " " + this.listOfColumns.get(i).toString();
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

		outMsg = "ERPINSERTCOLUMNINDEX";
		outMsg += ":" + this.physicalSender.toString();
		outMsg += ":" + this.physicalOwner.toString();
		for (int i = 0; i < this.listOfColumns.size(); i++)
			outMsg += ":" + this.listOfColumns.get(i).toString();
		if (logicalDestination == null)
			outMsg += ":null";
		else
			outMsg += ":" + logicalDestination.toString();

		return outMsg;
	}
}
