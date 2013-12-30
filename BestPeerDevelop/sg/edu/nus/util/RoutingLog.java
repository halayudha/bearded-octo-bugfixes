package sg.edu.nus.util;

import java.io.File;
import java.net.UnknownHostException;

import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.TreeNode;

/**
 * Record the routing information of each node, if some node
 * crashes, we can restore it by loading the routing information
 * from the files
 * @author Wu Sai
 *
 */
public class RoutingLog {

	/**
	 * file name of the routing log
	 */
	private String routingFile = "routingLog.txt";

	/**
	 * server peer (owner of the log) 
	 */
	private ServerPeer server;

	/**
	 * constructor
	 * @param server
	 */
	public RoutingLog(ServerPeer server) {
		this.server = server;
		try {
			routingFile = server.getPhysicalInfo().serialize() + "_"
					+ routingFile;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void Recovery() {
		File file = new File(routingFile);
		if (file.exists())
			try {

			} catch (Exception e) {
				System.out.println("Fail to recovery the routing table: " + e);
			}
	}

	public void Save() {
		try {
			TreeNode[] treeNodes = server.getTreeNodes();
			for (int i = 0; i < treeNodes.length; i++) {

			}
		} catch (Exception e) {
			System.out.println("Fail to recovery the routing table: " + e);
		}
	}
}
