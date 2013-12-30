package sg.edu.nus.bestpeer.joinprocessing;

import java.io.Serializable;
import java.sql.Connection;

import sg.edu.nus.util.MetaDataAccess;

/**
 * 
 * @author VHTam
 *
 */

public class PartitionStatistics implements Serializable {

	private static final long serialVersionUID = -7520963591821279900L;

	String tableName = null;
	int tableSize = 0;
	String[][] columnStats = null;
	String[][] columnHists = null;

	public PartitionStatistics(String tableName) {
		this.tableName = tableName;
	}

	public void getStatFromMetaData(Connection conn) {
		tableSize = MetaDataAccess.metaGetTableStats(conn, tableName);
		columnStats = MetaDataAccess.metaGetColumnStats(conn, tableName);
		columnHists = MetaDataAccess.metaGetColumnHists(conn, tableName);
	}

	public void print() {

		System.out.println("Table size: " + tableSize);

		if (columnStats != null) {
			for (int i = 0; i < columnStats.length; i++) {
				for (int j = 0; j < columnStats[i].length; j++) {
					System.out.print(columnStats[i][j] + "\t");
				}
				System.out.println();
			}
		}

		if (columnHists != null) {
			for (int i = 0; i < columnHists.length; i++) {
				for (int j = 0; j < columnHists[i].length; j++) {
					System.out.print(columnHists[i][j] + "\t");
				}
				System.out.println();
			}
		}

	}

}
