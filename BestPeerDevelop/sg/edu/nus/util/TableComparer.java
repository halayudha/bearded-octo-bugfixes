package sg.edu.nus.util;

import java.util.Arrays;

import sg.edu.nus.gui.dbview.DBTreeNode;

/**
 * This class is written to compare the table for schema mappping.
 * created by Dai Bing Tian on 5th May 2009.
 */

public class TableComparer {
	@SuppressWarnings("unchecked")
	public class cmpPair implements Comparable {
		public int index;
		public double score;

		public cmpPair(int idx, double sco) {
			index = idx;
			score = sco;
		}

		public int compareTo(Object another) {
			double anotherScore = ((cmpPair) another).score;
			if (score > anotherScore)
				return -1;
			else if (score < anotherScore)
				return 1;
			else
				return 0;
		}
	}

	private DBTreeNode localRoot;
	private DBTreeNode selection;
	private int size;
	private cmpPair[] cmpair;

	public TableComparer(DBTreeNode lr, DBTreeNode sl) {
		localRoot = lr;
		selection = sl;
		size = localRoot.getChildCount();
		cmpair = new cmpPair[size];
		for (int i = 0; i < size; i++)
			cmpair[i] = new cmpPair(i, compute_score((DBTreeNode) localRoot
					.getChildAt(i), selection));
		Arrays.sort(cmpair);
	}

	public int get_index(int i) {
		return cmpair[i].index;
	}

	public double get_score(int i) {
		return cmpair[i].score;
	}

	private double compute_score(DBTreeNode a, DBTreeNode b) {
		String p = a.getSourceSchemaName();
		String q = b.getSourceSchemaName();
		int[][] dist = new int[p.length() + 1][q.length() + 1];
		dist[0][0] = 0;
		for (int i = 1; i <= p.length(); i++)
			dist[i][0] = i;
		for (int j = 1; j <= q.length(); j++)
			dist[0][j] = j;
		for (int i = 1; i <= p.length(); i++)
			for (int j = 1; j <= q.length(); j++)
				if (p.charAt(i - 1) == q.charAt(j - 1))
					dist[i][j] = Math.min(dist[i - 1][j - 1], Math.min(
							dist[i - 1][j], dist[i][j - 1]) + 1);
				else
					dist[i][j] = Math.min(dist[i - 1][j - 1] + 1, Math.min(
							dist[i - 1][j], dist[i][j - 1]) + 1);
		return 1.0 - 1.0 * dist[p.length()][q.length()]
				/ (p.length() + q.length());
	}
}
