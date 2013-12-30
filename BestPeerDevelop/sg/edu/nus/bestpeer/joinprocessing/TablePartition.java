package sg.edu.nus.bestpeer.joinprocessing;

import java.io.Serializable;

import sg.edu.nus.peer.info.PhysicalInfo;

public class TablePartition implements Serializable {

	private static final long serialVersionUID = -9037196137602599516L;

	String node_name = null;
	String table_name = null;
	PhysicalInfo node_physical_info = null;

	public TablePartition(String table_name, PhysicalInfo node_physical_info) {
		this.node_physical_info = node_physical_info;
		this.table_name = table_name;

		this.node_name = node_physical_info.getIP() + ":"
				+ node_physical_info.getPort() + "." + table_name;
	}

	public TablePartition(String partitionStr) {
		int pos1 = partitionStr.indexOf(":");
		int pos2 = partitionStr.indexOf(".", pos1);
		String ip = partitionStr.substring(0, pos1);
		String portStr = partitionStr.substring(pos1 + 1, pos2);
		String tableName = partitionStr.substring(pos2 + 1, partitionStr
				.length() - 1);// since there is ; at the end

		this.node_physical_info = new PhysicalInfo(ip, Integer
				.parseInt(portStr));
		this.table_name = tableName;

		this.node_name = node_physical_info.getIP() + ":"
				+ node_physical_info.getPort() + "." + table_name;
	}

	public TablePartition clone() {
		String new_table_name = new String(this.table_name);
		PhysicalInfo new_physical_info = new PhysicalInfo(
				this.node_physical_info.getIP(), this.node_physical_info
						.getPort());
		return new TablePartition(new_table_name, new_physical_info);
	}

	public boolean equals(TablePartition compare) {
		return (table_name.equals(compare.table_name) && node_physical_info
				.equals(compare.node_physical_info));
	}

	public static String getPartitionStringForHash(String ip, int port,
			String tableName) {
		return ip + ":" + port + "." + tableName;
	}

	public String toString() {
		return node_name;
	}

}
