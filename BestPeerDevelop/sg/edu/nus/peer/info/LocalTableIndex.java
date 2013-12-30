package sg.edu.nus.peer.info;

import java.io.Serializable;
import java.util.Vector;

import sg.edu.nus.bestpeer.indexdata.RangeIndex;

/**
 * This class contains a unit of a table index at a peer node
 * 
 * @author Quang Hieu Vu
 * @version 1.0 2008-05-29
 */

public class LocalTableIndex implements Serializable {

	private static final long serialVersionUID = -979149836974096059L;

	private String tableName;
	
	private Vector<RangeIndex> rangeIndexOfTable;
	
	public LocalTableIndex(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return this.tableName;
	}
	
	public void setRangeIndexOfTable(Vector<RangeIndex> rangeIndexOfTable){
		this.rangeIndexOfTable = rangeIndexOfTable;
	}
	
	public Vector<RangeIndex> getRangeIndexOfTable(){
		return rangeIndexOfTable;
	}
	@Override
	public String toString() {
		String outMsg;

		outMsg = this.tableName;

		return outMsg;
	}
}
