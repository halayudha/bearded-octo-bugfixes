package sg.edu.nus.ui.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;

/**
 * 
 * @author Wu Sai
 * 
 */
public class TestQueryServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4582870548943182177L;

	public String testResult = "queryresult.txt";

	public int timeout = 0;

	public static int completed = 0;

	public ArrayList<String> tname;

	public Hashtable<String, ArrayList<String>> attribute;

	public Hashtable<String, ArrayList<Integer>> type;

	public Timer timer = null;

	public TestQueryServlet() {
		super();

		Connection conn = ServerPeer.conn_globalSchema;

		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			tname = new ArrayList<String>();

			attribute = new Hashtable<String, ArrayList<String>>();
			type = new Hashtable<String, ArrayList<Integer>>();

			ResultSet tables, columns;
			String[] tableTypes = { "TABLE" };

			tables = dbmd.getTables(null, null, null, tableTypes);

			while (tables.next()) {
				String tableName = tables.getString("TABLE_NAME");
				tname.add(tableName);

				ArrayList<String> col = new ArrayList<String>();
				ArrayList<Integer> types = new ArrayList<Integer>();
				columns = dbmd.getColumns(null, null, tableName, null);
				while (columns.next()) {
					String cName = columns.getString("COLUMN_NAME");
					int t = columns.getInt("DATA_TYPE");
					col.add(cName);
					types.add(t);
				}
				columns.close();

				attribute.put(tableName, col);
				type.put(tableName, types);
			}
			tables.close();

			//

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class MyWork extends TimerTask {
		public Hashtable<String, ArrayList<String>> attribute;

		public ArrayList<String> tname;

		public Hashtable<String, ArrayList<Integer>> type;

		public Random rand;

		public MyWork(Hashtable<String, ArrayList<String>> attribute,
				ArrayList<String> tname,
				Hashtable<String, ArrayList<Integer>> type) {
			super();
			this.attribute = attribute;
			this.tname = tname;
			this.type = type;
			rand = new Random();
			rand.setSeed(System.currentTimeMillis());
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			// generate queries
			int idx = rand.nextInt(tname.size());
			String name = tname.get(idx);

			ArrayList<String> att = attribute.get(name);

			idx = rand.nextInt(att.size());

			String cname = att.get(idx);
			int t = type.get(name).get(idx);
			int year = 1999 + rand.nextInt(10);
			char[] alpha = { 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D',
					'E', 'F' };

			String sql = "select * from " + name + " where ";
			switch (t) {
			case 4: // java.sql.Types.INTEGER
			case 5: // java.sql.Types.BIGINT
			case 3: // java.sql.Types.DECIMAL
			case 2: // java.sql.Types.NUMERIC:
			case 8: // java.sql.Types.DOUBLE
			case 6: // java.sql.Types.FLOAT
			case 7: // java.sql.Types.REAL
				sql = sql + cname + ">" + rand.nextInt(100000);
				break;
			case 91: // java.sql.Types.DATE
			{

				sql = sql + cname + ">'" + year + "-01-01'";
				break;
			}
			case 16: // java.sql.Types.BOOLEAN
				sql = sql + cname + "=" + rand.nextBoolean();
				break;
			default:
				sql = sql + cname + ">'" + alpha[rand.nextInt(alpha.length)]
						+ "'";
			}

			System.out.println("send query " + sql);
			ServerPeer server = ServerGUI.instance.peer();
			SelectExecutor selectExecutor = new SelectExecutor(sql);
			selectExecutor.setUserName(server.getServerPeerAdminName());
			// ArrayList<String[]> tuples =
			selectExecutor.executeWebQuery();
			TestQueryServlet.completed++;
		}

	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new MyWork(attribute, tname, type), 10000, 10000);
			out.println("<HTML>");
			out.println("<HEAD><TITLE>Simulation Starts</TITLE></HEAD>");
			out.println("<BODY>");
			out.println("<BIG>Simulation Starts, current qid:"
					+ TestQueryServlet.completed + "</BIG>");
			out.println("</BODY></HTML>");
		} else {
			out.println("<HTML>");
			out.println("<HEAD><TITLE>Simulation continues</TITLE></HEAD>");
			out.println("<BODY>");
			out.println("<BIG>Simulation Continues, current qid:"
					+ TestQueryServlet.completed + "</BIG>");
			out.println("</BODY></HTML>");
		}
	}
}
