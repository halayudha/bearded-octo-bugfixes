package sg.edu.nus.sqlparser.java_cup_use;

import java.io.ByteArrayInputStream;

public class SQLParser {

	parser p = new parser();

	public SQLParser(String orgSql) {

		String sql = normalize(orgSql);

		ByteArrayInputStream buffInStream = new ByteArrayInputStream(sql
				.getBytes());

		Scaner sc = new Scaner(buffInStream);
		p.setScanner(sc);

	}

	public void parseQuery() throws Exception {
		p.parse();
	}

	public SQLQuery getSQLQuery() {
		return p.getSQLQuery();
	}

	/**
	 * make some normalization for string if neccessary for underlying parser
	 *
	 */
	private String normalize(String source) {

		String result = new String(source);

		// replace select
		result = result.replaceAll("select ", "SELECT ");
		result = result.replaceAll("Select ", "SELECT ");

		result = result.replaceAll("from ", "FROM ");
		result = result.replaceAll("From ", "FROM ");

		result = result.replaceAll("where ", "WHERE ");
		result = result.replaceAll("Where ", "WHERE ");

		result = result.replaceAll("orderby ", "ORDERBY ");
		result = result.replaceAll("order by ", "ORDERBY ");
		result = result.replaceAll("ORDER BY ", "ORDERBY ");
		result = result.replaceAll("Order By ", "ORDERBY ");
		result = result.replaceAll("Order by ", "ORDERBY ");

		result = result.replaceAll("groupby ", "GROUPBY ");
		result = result.replaceAll("group by ", "GROUPBY ");
		result = result.replaceAll("GROUP BY ", "GROUPBY ");
		result = result.replaceAll("Group By ", "GROUPBY ");
		result = result.replaceAll("Group by ", "GROUPBY ");

		result = result.replaceAll(" and ", " , ");
		result = result.replaceAll(" And ", " , ");
		result = result.replaceAll(" AND ", " , ");

		result = result.replaceAll(" like ", " != ");
		result = result.replaceAll(" LIKE ", " != ");

		return result;
	}

}
