package sg.edu.nus.bestpeer.joinprocessing;

import java.io.Serializable;

import sg.edu.nus.peer.info.PhysicalInfo;

public class AgentJoinObject implements Serializable {

	private static final long serialVersionUID = 5309721879193227562L;

	JoinPath joinPath = null;
	int processingIndex = 0;// the index position in JoinPath that is currently
	// being processing
	PhysicalInfo queryPeerAddress = null;
	String remoteTempTable = null; // remote table name of temporary result

	public AgentJoinObject(JoinPath joinPath, PhysicalInfo queryPeerAddress) {
		this.joinPath = joinPath;
		this.queryPeerAddress = queryPeerAddress;
	}

	public TablePartition getTablePartition(int index) {
		return joinPath.path.elementAt(index);
	}

	public PhysicalInfo getAdressOfJoinNode(int index) {
		TablePartition partition = getTablePartition(index);
		return partition.node_physical_info;
	}
}
