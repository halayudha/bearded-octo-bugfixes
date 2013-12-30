package sg.edu.nus.bestpeer.joinprocessing;

import java.io.Serializable;
import java.util.Vector;

public class JoinPath implements Serializable {

	private static final long serialVersionUID = 262671491698894164L;

	Vector<TablePartition> path = new Vector<TablePartition>();

	public JoinPath(Vector<String> partitions) {
		for (int i = 0; i < partitions.size(); i++) {
			TablePartition tablePartition = new TablePartition(partitions
					.elementAt(i));
			path.add(tablePartition);
		}
	}

	public int size() {
		return path.size();
	}

	public void print() {
		for (int i = 0; i < path.size(); i++) {
			TablePartition tablePartition = path.elementAt(i);
			System.out.print(tablePartition.node_name + "\t");
		}
		System.out.println();
	}

	public void add(TablePartition partition) {
		path.add(partition);
	}
}
