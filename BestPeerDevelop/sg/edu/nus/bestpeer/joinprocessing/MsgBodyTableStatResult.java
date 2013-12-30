package sg.edu.nus.bestpeer.joinprocessing;

import sg.edu.nus.protocol.body.Body;

/**
 * 
 * @author VHTam
 *
 */

public class MsgBodyTableStatResult extends Body {

	private static final long serialVersionUID = -7231085229504234460L;

	private PartitionStatistics partitionStat = null;

	public MsgBodyTableStatResult(PartitionStatistics stat) {
		partitionStat = stat;
	}

	public PartitionStatistics getPartitionStatistics() {
		return partitionStat;
	}

	public String toString() {

		String result = "MsgBodyTableStatResult format:= PartionStatistics\r\n";
		result += partitionStat.toString();

		return result;
	}
}
