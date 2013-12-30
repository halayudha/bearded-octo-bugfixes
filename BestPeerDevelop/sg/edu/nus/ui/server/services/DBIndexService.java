package sg.edu.nus.ui.server.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONObject;

import sg.edu.nus.bestpeer.indexdata.RangeIndex;
import sg.edu.nus.dbconnection.DBIndex;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * @author Wu Sai
 * 
 */
public class DBIndexService {

	public JSONObject doService(JSONObject params) {

		System.out.println("Receive index data request from web interface");
		JSONObject result = new JSONObject();

		ServerPeer server = ServerGUI.instance.peer();

		List allColumns = (List) params.get("column_name");
		Hashtable<String, Vector<RangeIndex>> rangeIndexInTables = new Hashtable<String, Vector<RangeIndex>>();
		Hashtable<String, Vector<String>> columnsOfTable = new Hashtable<String, Vector<String>>();
		Vector<String> indexedColumn = new Vector<String>();
		Vector<String> listOfTables = new Vector<String>();

		Connection conn = ServerPeer.conn_bestpeerindexdb;
		Statement idxstmt = null;
		try {
			idxstmt = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < allColumns.size(); i++) {
			String cname = (String) allColumns.get(i);
			indexedColumn.add(cname);

			List allTables = (List) params.get(cname);
			Vector<String> owner = new Vector<String>();
			for (int j = 0; j < allTables.size(); j++) {
				String tname = (String) allTables.get(j);
				owner.add(tname);

				if (!listOfTables.contains(tname))
					listOfTables.add(tname);

				Vector<RangeIndex> rangeIndexOfTable = null;
				if (rangeIndexInTables.containsKey(tname)) {
					rangeIndexOfTable = rangeIndexInTables.get(tname);
				} else {
					rangeIndexOfTable = new Vector<RangeIndex>();
					rangeIndexInTables.put(tname, rangeIndexOfTable);
				}

				try {
					conn = ServerPeer.conn_exportDatabase;
					Statement stmt = conn.createStatement();
					String sql = "select max(" + cname + ") from " + tname;
					ResultSet rs = stmt.executeQuery(sql);
					rs.next();
					String maxVal = rs.getString(1);
					rs.close();
					sql = "select min(" + cname + ") from " + tname;
					rs = stmt.executeQuery(sql);
					rs.next();
					String minVal = rs.getString(1);
					rs.close();
					String columnType = MetaDataAccess.metaGetColumnType(
							ServerPeer.conn_metabestpeerdb, tname, cname);
					int type = columnType.contains("varchar") ? RangeIndex.STRING_TYPE
							: RangeIndex.NUMERIC_TYPE;

					RangeIndex rangeIndex = new RangeIndex(tname, cname, type,
							minVal, maxVal, server.getPhysicalInfo());

					rangeIndexOfTable.add(rangeIndex);
					sql = "insert into local_index values('" + cname + "','"
							+ tname + "')";
					if (idxstmt != null)
						idxstmt.addBatch(sql);
				} catch (Exception e) {
					System.out.println("fail to create index for table:"
							+ tname + " column:" + cname);
					System.out.println("caused by:" + e.toString());
				}
			}
			columnsOfTable.put(cname, owner);

			try {
				if (idxstmt != null)
					idxstmt.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			idxstmt.close();

			if (listOfTables.size() == 0) {
				result.put("indexstatus", "ready");
				return null;
			}

			if (server != null) {
				TreeNode[] treeNode = server.getTreeNodes();
				if (treeNode != null) {
					DBIndex dbIndex = new DBIndex(server, server
							.getPhysicalInfo(), treeNode[0].getLogicalInfo(),
							listOfTables, rangeIndexInTables, columnsOfTable);
					dbIndex.indexDatabase();
				}
				result.put("indexstatus", "ready");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result.get("indexstatus") == null) {
			result.put("indexstatus", "failed");
		}
		return result;
	}
}
