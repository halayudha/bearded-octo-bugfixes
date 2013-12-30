package sg.edu.nus.bestpeer.joinprocessing;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * 
 * @author VHTam
 *
 */
public class JoinQueryPlan {
	Vector<String> table_names = null;
	Vector<ArrayList<PhysicalInfo>> table_owners_array = null;

	Vector<Vector<PlanNode>> plan_node_all_levels = new Vector<Vector<PlanNode>>();

	String fullJoinTableString = "";

	public Vector<TablePartition> table_partitions = new Vector<TablePartition>();

	Hashtable<String, PartitionStatistics> partitionStats = null;

	public JoinQueryPlan(Vector<String> table_names,
			Vector<ArrayList<PhysicalInfo>> table_owners_array,
			Hashtable<String, PartitionStatistics> partitionStats) {

		this.table_names = table_names;
		this.table_owners_array = table_owners_array;
		this.partitionStats = partitionStats;

		// create string of all join table names
		for (int i = 0; i < table_names.size(); i++) {
			fullJoinTableString += table_names.get(i) + ";";
		}

		// create plan node at each level of join search tree
		for (int i = 0; i <= table_names.size(); i++) {

			Vector<PlanNode> plan_nodes = new Vector<PlanNode>();

			if (i == 0) {// level 0
				PlanNode rootNode = new PlanNode();
				rootNode.level = 0;
				plan_nodes.add(rootNode);
			}

			else if (i == 1) {
				PlanNode tempNode;
				int count = 1;
				for (int t = 0; t < table_names.size(); t++) {
					ArrayList<PhysicalInfo> table_owners = table_owners_array
							.get(t);
					for (int owner = 0; owner < table_owners.size(); owner++) {
						tempNode = new PlanNode();
						tempNode.level = i;
						TablePartition partition = new TablePartition(
								table_names.get(t), table_owners.get(owner));
						tempNode.joinList.add(partition);
						plan_nodes.add(tempNode);
						// store table partition for use later
						table_partitions.add(partition);

						// partition size
						if (partitionStats != null) {
							String key = TablePartition
									.getPartitionStringForHash(
											partition.node_physical_info
													.getIP(),
											partition.node_physical_info
													.getPort(),
											partition.table_name);
							PartitionStatistics stat = partitionStats.get(key);
							tempNode.estimatedSize = stat.tableSize;
						} else {
							// sample cost
							tempNode.estimatedSize = 100 * count++;
						}

					}
				}

			}

			else {// other level
				Vector<PlanNode> upper_plan_nodes = plan_node_all_levels
						.get(i - 1);
				for (int k = 0; k < upper_plan_nodes.size(); k++) {
					PlanNode upperNode = upper_plan_nodes.get(k);
					for (int p = 0; p < table_partitions.size(); p++) {
						String joinTableStr = upperNode.getJoinTables();
						String prefix = table_partitions.get(p).table_name
								+ ";" + joinTableStr;
						PlanNode tempNode = null;

						if (fullJoinTableString.contains(prefix)) {
							tempNode = upperNode
									.joinPrefixPartition(table_partitions
											.get(p));
							addPlanNodeToVector(tempNode, plan_nodes);
						}
						String suffix = joinTableStr
								+ table_partitions.get(p).table_name + ";";
						if (fullJoinTableString.contains(suffix)) {
							tempNode = upperNode
									.joinSuffixPartition(table_partitions
											.get(p));
							addPlanNodeToVector(tempNode, plan_nodes);
						}

					}

				}
			}

			plan_node_all_levels.add(plan_nodes);
		}

		// create links for node at each level of join search tree
		for (int i = 0; i < table_names.size(); i++) {
			Vector<PlanNode> plan_nodes = plan_node_all_levels.get(i);
			Vector<PlanNode> lower_plan_nodes = plan_node_all_levels.get(i + 1);

			for (int j = 0; j < plan_nodes.size(); j++) {
				PlanNode node = plan_nodes.get(j);
				String upperJoinTableStr = node.getNodeName();

				for (int k = 0; k < lower_plan_nodes.size(); k++) {
					PlanNode lowerNode = lower_plan_nodes.get(k);
					// String lowerJoinTableStr = lowerNode.getJoinTables();
					String lowerJoinTableStr = lowerNode.getNodeName();
					if (lowerJoinTableStr.contains(upperJoinTableStr)) {
						node.children.add(lowerNode);// add children
					}
				}// end for k
			}// end for j
		}// end for i

		// traverese to compute path and cost...
		traverseJoinSearchTree();
	}

	private void addPlanNodeToVector(PlanNode addNode,
			Vector<PlanNode> plan_nodes) {

		for (int i = 0; i < plan_nodes.size(); i++) {
			if (addNode.equals(plan_nodes.get(i))) {
				return;// no add
			}
		}

		plan_nodes.add(addNode);
	}

	private void print() {

		for (int i = 0; i < plan_node_all_levels.size(); i++) {
			System.out.println("LEVEL: " + i);
			Vector<PlanNode> plan_nodes = plan_node_all_levels.get(i);

			for (int j = 0; j < plan_nodes.size(); j++) {
				PlanNode node = plan_nodes.get(j);

				System.out.println("node " + (j + 1) + "\t"
						+ node.getNodeName() + "\t num_childs\t"
						+ node.children.size());
			}
		}

	}

	private PlanNode getRootNode() {
		Vector<PlanNode> rootLevel = plan_node_all_levels.get(0);
		return rootLevel.get(0);
	}

	public void traverseJoinSearchTree() {
		PlanNode rootNode = getRootNode();
		rootNode.traverse("", 0);

	}

	public void printPathToLeafNodes() {
		Vector<PlanNode> leafNodes = plan_node_all_levels
				.get(plan_node_all_levels.size() - 1);
		for (int i = 0; i < leafNodes.size(); i++) {
			PlanNode leaf = leafNodes.get(i);
			System.out.println("Node " + leaf.getNodeName());
			for (int j = 0; j < leaf.paths.size(); j++) {
				Path joinPath = leaf.paths.get(j);
				System.out.println("path: " + joinPath.path + " COST: "
						+ joinPath.cost);
			}

		}

	}

	public void printOptimalPaths() {
		Vector<PlanNode> leafNodes = plan_node_all_levels
				.get(plan_node_all_levels.size() - 1);
		for (int i = 0; i < leafNodes.size(); i++) {
			PlanNode leaf = leafNodes.get(i);
			System.out.println("Node " + leaf.getNodeName());
			for (int j = 0; j < leaf.paths.size(); j++) {
				Path joinPath = leaf.paths.get(j);
				System.out.println("path: " + joinPath.path + " COST: "
						+ joinPath.cost);
				splitJoinPath(joinPath.path);
			}
			Path optimalPath = leaf.getOptimalPath();
			System.out.println("optimalPath: " + optimalPath.path + " COST: "
					+ optimalPath.cost);
			splitJoinPath(optimalPath.path);
		}

	}

	private Vector<String> splitJoinPath(String pathStr) {
		Vector<String> parts = new Vector<String>();
		pathStr = pathStr.substring(1);// not use first mark

		String[] arrNodes = pathStr.split("_");
		parts.add(arrNodes[0]);

		for (int j = 0; j < arrNodes.length - 1; j++) {
			String node1 = arrNodes[j];
			String node2 = arrNodes[j + 1];
			String[] arrStr = node2.split(node1);
			if (arrStr.length == 2) {
				parts.add(new String(arrStr[0] + arrStr[1]));
			} else {
				parts.add(new String(arrStr[0]));
			}
		}

		return parts;
	}

	public Vector<JoinPath> getOptimalPaths() {
		Vector<JoinPath> joinPaths = new Vector<JoinPath>();
		Vector<PlanNode> leafNodes = plan_node_all_levels
				.get(plan_node_all_levels.size() - 1);
		for (int i = 0; i < leafNodes.size(); i++) {
			PlanNode leaf = leafNodes.get(i);
			Path optimalPath = leaf.getOptimalPath();
			Vector<String> parts = splitJoinPath(optimalPath.path);
			JoinPath joinPath = new JoinPath(parts);
			joinPaths.add(joinPath);
		}

		return joinPaths;
	}

	public void printJoinSearchTree() {
		print();
	}

	public static void main(String[] args) {
		System.out.println("test join");

		Vector<String> table_names = new Vector<String>();
		table_names.add("R1");
		table_names.add("R2");
		table_names.add("R3");

		ArrayList<PhysicalInfo> table_owners_R1 = new ArrayList<PhysicalInfo>();
		table_owners_R1.add(new PhysicalInfo("172.18.179.31", 8080));

		ArrayList<PhysicalInfo> table_owners_R2 = new ArrayList<PhysicalInfo>();
		table_owners_R2.add(new PhysicalInfo("172.18.179.32", 8080));
		table_owners_R2.add(new PhysicalInfo("172.18.179.33", 8080));

		ArrayList<PhysicalInfo> table_owners_R3 = new ArrayList<PhysicalInfo>();
		table_owners_R3.add(new PhysicalInfo("172.18.179.34", 8080));
		table_owners_R3.add(new PhysicalInfo("172.18.179.35", 8080));
		table_owners_R3.add(new PhysicalInfo("172.18.179.36", 8080));

		Vector<ArrayList<PhysicalInfo>> table_owners_array = new Vector<ArrayList<PhysicalInfo>>();
		table_owners_array.add(table_owners_R1);
		table_owners_array.add(table_owners_R2);
		table_owners_array.add(table_owners_R3);

		JoinQueryPlan joinQueryPlan = new JoinQueryPlan(table_names,
				table_owners_array, null);

		Vector<JoinPath> joinPath = joinQueryPlan.getOptimalPaths();
		System.out.println("OPTIMAL JOIN PATH");
		for (int i = 0; i < joinPath.size(); i++) {
			joinPath.elementAt(i).print();
		}
		
		System.out.println("test join");
	}
}
