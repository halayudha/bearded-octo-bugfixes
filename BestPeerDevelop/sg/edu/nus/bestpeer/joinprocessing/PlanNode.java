package sg.edu.nus.bestpeer.joinprocessing;

import java.util.Vector;

public class PlanNode {

	int level = 0;

	int estimatedSize = 0; // estimated result size

	Vector<PlanNode> children = new Vector<PlanNode>();

	Vector<TablePartition> joinList = new Vector<TablePartition>();

	Vector<Path> paths = new Vector<Path>();

	public Path getOptimalPath() {
		int optimalCost = Integer.MAX_VALUE;
		Path optimalPath = null;

		for (int i = 0; i < paths.size(); i++) {
			Path tempPath = paths.get(i);
			if (optimalCost > tempPath.cost) {
				optimalCost = tempPath.cost;
				optimalPath = new Path(tempPath.path, tempPath.cost);
			}
		}

		return optimalPath;
	}

	public void traverse(String path, int pathCost) {
		if (children.size() == 0) {
			// leaf nodes...
			this.paths.add(new Path(path, pathCost + this.estimatedSize));
		} else {
			for (int i = 0; i < children.size(); i++) {
				PlanNode child = children.get(i);
				child.traverse(new String(path + "_" + child.getNodeName()),
						pathCost + this.estimatedSize);
			}
		}
	}

	public String getNodeName() {
		if (level == 0) {
			return ";";// root virtual node
		} else {
			String result = "";
			for (int i = 0; i < joinList.size(); i++) {
				result += joinList.get(i).node_name;
				result += ";";
			}
			return result;
		}
	}

	public String getJoinTables() {
		if (level == 0) {// root virtual node
			return ";";
		} else {
			String result = "";
			for (int i = 0; i < joinList.size(); i++) {
				result += joinList.get(i).table_name;
				result += ";";
			}
			return result;
		}

	}

	public PlanNode joinPrefixPartition(TablePartition partition) {
		PlanNode tempNode = new PlanNode();
		tempNode.level = this.level + 1;

		tempNode.joinList.add(partition.clone());

		for (int i = 0; i < this.joinList.size(); i++) {
			tempNode.joinList.add(this.joinList.get(i).clone());
		}
		return tempNode;
	}

	public PlanNode joinSuffixPartition(TablePartition partition) {
		PlanNode tempNode = new PlanNode();
		tempNode.level = this.level + 1;

		for (int i = 0; i < this.joinList.size(); i++) {
			tempNode.joinList.add(this.joinList.get(i).clone());
		}

		tempNode.joinList.add(partition.clone());

		return tempNode;
	}

	public boolean equals(PlanNode compareNode) {
		boolean equal = true;
		for (int i = 0; i < joinList.size(); i++) {
			TablePartition p1 = joinList.get(i);
			TablePartition p2 = compareNode.joinList.get(i);
			if (!p1.equals(p2)) {
				equal = false;
			}
		}
		return equal;
	}

}
