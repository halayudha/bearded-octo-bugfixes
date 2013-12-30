package sg.edu.nus.bestpeer.queryprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import sg.edu.nus.bestpeer.indexdata.ExactQuery;
import sg.edu.nus.bestpeer.joinprocessing.MsgBodyTableStatResult;
import sg.edu.nus.bestpeer.joinprocessing.MsgBodyTableStatSearch;
import sg.edu.nus.bestpeer.joinprocessing.PartitionStatistics;
import sg.edu.nus.gui.customcomponent.ProgressDialog;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.gui.test.peer.DatabaseQueryTableModel;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.QueryPeerBody;
import sg.edu.nus.protocol.body.TableRetrieval;
import sg.edu.nus.sqlparser.Attribute;
import sg.edu.nus.sqlparser.Condition;
import sg.edu.nus.sqlparser.SelectStatement;
import sg.edu.nus.util.IndexSearch;
import sg.edu.nus.util.MetaDataAccess;
import sg.edu.nus.util.NameUtil;
import sg.edu.nus.util.ReportWriter;

/**
 * 
 * @author VHTam
 * 
 */
@SuppressWarnings("unchecked")
public class SelectExecutor extends Thread {

	public static ServerPeer peer;

	SelectStatement selectStatement = null;

	Vector tableList = null;

	Vector projectionList = null;

	Vector selectionList = null;
	
	Vector groupbyList = null;

	String orgSql = null;

	boolean isSingleTableQuery = true;
	boolean isSelectStar = true;
	boolean legalQuery = false;

	long qid = 0;

	boolean isFromWeb = false;

	String[] metaTableName = null;
	Hashtable<String, String[][]> metaColumns = null;

	//private String tmpTable;

	DatabaseQueryTableModel model = null; // jtable showing result

	ProgressDialog progressDialog = new ProgressDialog(LanguageLoader
			.getProperty("title.queryProcessing"));

	int colCnt = 0;
	int rowCnt = 0;

	//for newly appended column when query query have data mapping
	Vector<MappingColumn> mappingColumns = new Vector<MappingColumn>();
	
	public Vector<MappingColumn> getMappingColumns(){
		return mappingColumns;
	}
	
	public void setTableModelForViewingResult(DatabaseQueryTableModel model) {
		this.model = model;
	}

	String userName = null; // user who submit query

	public void setUserName(String userName) {
		this.userName = new String(userName);
	}

	public void setWebSearch(boolean webSearch) {
		this.isFromWeb = webSearch;
	}

	public void setQueryID(long id) {
		this.qid = id;
	}

	public boolean isLegalQuery() {
		return this.legalQuery;
	}

	public boolean isSingleTableQuery() {
		return this.isSingleTableQuery;
	}

	public SelectExecutor(String sql) {
		orgSql = new String(sql);

		// check query syntax
		if (!checkQuerySyntax(orgSql)) {
			legalQuery = false;
			return;
		}

		selectStatement = new SelectStatement(sql);

		if (selectStatement.isAttribNotInGroupBy()) {
			legalQuery = false;
			String message = "Projected attribute is not in group by clause!";
			 System.out.println(message);
			ServerGUI servergui = (ServerGUI) (peer.getMainFrame());
			servergui.setProcessingQuery(false);
			return;
		}

		tableList = selectStatement.getTableList();
		projectionList = selectStatement.getProjectionList();
		groupbyList = selectStatement.getGroupByList();

		selectionList = selectStatement.getSelectionList();

		isSingleTableQuery = (tableList.size() == 1) ? true : false;
		isSelectStar = (projectionList.size() == 0) ? true : false;

		// get db context
		metaTableName = MetaDataAccess
				.metaGetTables(ServerPeer.conn_metabestpeerdb);
		metaColumns = new Hashtable<String, String[][]>();
		for (int i = 0; i < metaTableName.length; i++) {
			String[][] columns = MetaDataAccess.metaGetColumnsWithType(
					ServerPeer.conn_metabestpeerdb, metaTableName[i]);
			metaColumns.put(metaTableName[i], columns);
		}

		// check query semantic
		legalQuery = checkTableNameAndColumnName();
	}

	public void run() {
		long start = System.currentTimeMillis();

		LogManager.LogQuery(qid, this.orgSql, -1);

		execute();

		long end = System.currentTimeMillis();
		LogManager.LogQuery(qid, this.orgSql, (end - start) / 1000.0);
	}

	/**
	 * execute query
	 * 
	 */
	public void execute() {

		ServerGUI servergui = (ServerGUI) (peer.getMainFrame());

		if (!legalQuery) {
			// set flag finishing query
			servergui.setProcessingQuery(false);
			return;// do nothing because illegal query
		}

		progressDialog.setVisible(true);
		progressDialog.setAlwaysOnTop(true);

		// execute query
		if (isSingleTableQuery) {
			singleTableSelectAndViewResult();
		} else {
			multiTableSelect_Naive();
		}

		progressDialog.setVisible(false);

		// set flag finishing query
		servergui.setProcessingQuery(false);
	}

	/**
	 * process query from the webpage
	 * 
	 */
	public ArrayList<String[]> executeWebQuery() {
		// execute query
		if (!legalQuery)
			return null;

		if (isSingleTableQuery) {
			return singleTableWebSearch();
		} else {
			// to do
			return multiTableNaiveWebSearch();
		}
	}

	public static String generateReport(String userName, String sql, String title, String type){
		SelectExecutor executor = new SelectExecutor(sql);
		executor.setUserName(userName);
		ArrayList<String[]> results = executor.executeWebQuery();
		results.remove(1);//only keep colName, and data, remove type
		if (type.equalsIgnoreCase("pdf")){
			return ReportWriter.writeDataArrayToPdf(userName, results, title);
		} 
		else if (type.equalsIgnoreCase("rtf")) {
			return ReportWriter.writeDataArrayToRtf(userName, results, title);
		}
		return "wrong report type";
	}
	
	public ArrayList<String[]> executeQuery() {
		return executeWebQuery();
	}
	
	private boolean checkQuerySyntax(String sql) {

		boolean check = SelectStatement.checkQuerySyntax(sql);
		if (check == false) {
			String message = "Please check SQL syntax...";
			System.out.println(message);

			ServerGUI servergui = (ServerGUI) (peer.getMainFrame());
			servergui.setProcessingQuery(false);

		}
		return check;
	}

	/**
	 * check if query retrieve the right table and column names
	 * 
	 */
	private boolean checkTableNameAndColumnName() {
		// check semantics
		return selectStatement.checkTableNameAndColumnName(metaTableName,
				metaColumns);
	}

	private void singleTableSelectAndViewResult() {

		// query the network...
		String resultTableName = singleTableSelect();
	
		String sql = getQueryFromTempResult(resultTableName);
		
		viewResult(resultTableName, sql);

		dropTable(resultTableName);
	}

	private String getQueryFromTempResult(String resultTableName){
		String sql = "select * from " + resultTableName;

		if (selectStatement.isAggregateQuery()) {
			String tempSql = "select ";
			for (int i = 0; i < projectionList.size(); i++) {
				Attribute att = (Attribute) projectionList.get(i);
				if (att.isAggregate()) {
					String aggregateFunc = att.getAggregateFunction()
							.toLowerCase();
					if (aggregateFunc.equals("count")) {
						aggregateFunc = "sum";
					}
					tempSql += aggregateFunc + "(" + att.getColName() + ")";
				} else {
					tempSql += att.getColName();
				}
				if (i < projectionList.size() - 1) {
					tempSql += ", ";
				}
			}
			// from
			tempSql += " from " + resultTableName; // query on temp result

			// group by
			if (groupbyList.size() > 0) {
				tempSql += " group by ";
				for (int i = 0; i < groupbyList.size(); i++) {
					Attribute att = (Attribute) groupbyList.get(i);
					tempSql += att.getColName();
					if (i < groupbyList.size() - 1) {
						tempSql += ", ";
					}
				}
			}
			// will support having clause?
			System.out.println("DEBUG: " + tempSql);

			sql = tempSql;
		}

		if (selectStatement.hasOrderByClause()){
			Vector orderByList = selectStatement.getOrderByList();
			String orderByClause = " order by ";
			for (int i=0; i<orderByList.size(); i++){
				Attribute att = (Attribute)orderByList.get(i);
				orderByClause += att.getColName();
				if (i<orderByList.size()-1){
					orderByClause += ", ";
				}
			}
			sql += orderByClause;
		}		
		return sql;
	}
	
	/**
	 * process query of single relation
	 * 
	 * @return sql query to retrieve the result
	 */
	public ArrayList<String[]> singleTableWebSearch() {
		// query the network...

		String resultTableName = singleTableSelect();

		String sql = getQueryFromTempResult(resultTableName);
		
		ArrayList<String[]> result = null;
		Connection conn = ServerPeer.conn_exportDatabase;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			stmt.setFetchSize(1000);
			result = new ArrayList<String[]>();
			int count = rs.getMetaData().getColumnCount();
			String[] name = new String[count];
			String[] type = new String[count];
			for (int i = 1; i <= count; i++) {
				name[i - 1] = rs.getMetaData().getColumnName(i);
				type[i - 1] = Integer.toString(rs.getMetaData()
						.getColumnType(i));
			}
			result.add(name);
			result.add(type);

			while (rs.next()) {
				String[] tuple = new String[count];
				for (int i = 1; i <= count; i++) {
					tuple[i - 1] = rs.getString(i);
				}
				if (!isTupleInResultSet(tuple, result)){
					result.add(tuple);
				}
			}
			rs.close();

			sql = "drop table " + resultTableName;
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private boolean isTupleInResultSet(String[] tuple, ArrayList<String[]> result){
		return false;
	}
	
	private RemoteTableInfo[] executeSimpleSelect(
			ArrayList<PhysicalInfo> table_owners) {

		int numOfPeers = table_owners.size();
		Head head = new Head();
		head.setMsgType(MsgType.QUERY_PEER.getValue());
		QueryPeerBody body = new QueryPeerBody();

		body.setUserName(userName);

		try {
			body.setSender(SelectExecutor.peer.getPhysicalInfo());
		} catch (UnknownHostException e) {
			LogManager.LogException(
					"Fail to get peer address while processing basic queries",
					e);
		}

		body.setSqlCommand(orgSql);

		ServerPeer serverpeer = SelectExecutor.peer;

		Win32.Event event = Win32.CreateEvent(NameUtil.queryEventName(body
				.getSqlCommand()));
		event.setWaitCounts(numOfPeers);

		
			for (int i = 0; i < numOfPeers; ++i) {
				try {
				serverpeer
						.sendMessage(table_owners.get(i), new Message(head, body));
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					event.decWaitCounts();
				}
				
			}
		

		Win32.WaitForMultipleObjects(event, ServerPeer.queryWaitingTime);

		Object[] tables = event.getEventDataArray();
		RemoteTableInfo[] remote_tables = null;
		remote_tables = new RemoteTableInfo[tables.length];
		for (int i = 0; i < tables.length; ++i)
			remote_tables[i] = (RemoteTableInfo) tables[i];

		Win32.CloseEvent(event);

		return remote_tables;
	}

	/**
	 * search table owner based on both data index and table index
	 * @param table_name
	 * 
	 */
	private ArrayList<PhysicalInfo> findTableOwnersUsingDataIndex(String table_name) {

		ExactQuery exactQuery = null;
		if (tableList.size()==1){//exact query on single table
			if (selectionList.size()>0){
				Condition cond = (Condition) selectionList.get(0);
				if (cond.getExprType()==Condition.EQUAL){//exact query
					Attribute att = cond.getLhs();
					String value = cond.getRightValueString();
					exactQuery = new ExactQuery(table_name, att.getColName(),value);
				}
			}
		}
		
		IndexSearch search = new IndexSearch();
		ArrayList<PhysicalInfo> table_owners = search.searchDataIndex(
				SelectExecutor.peer, table_name, exactQuery);

		//remove the duplicate
		for (int i = 0; i < table_owners.size() - 1; i++) {
			for (int j = i + 1; j < table_owners.size(); j++) {
				if (table_owners.get(i).equals(table_owners.get(j))) {
					table_owners.remove(j);
					j--;
				}
			}
		}
		System.out.println("FindTableOwner: " + table_owners);

		return table_owners;
	}

	private void fetchTableOp(String table, RemoteTableInfo[] remote_tables) {

		// send out the messages for retrieving the data
		ServerPeer serverpeer = SelectExecutor.peer;

		PhysicalInfo localIP = null;
		int expectedResponse = 0;
		try {
			localIP = serverpeer.getPhysicalInfo();
		} catch (Exception e) {
			localIP = new PhysicalInfo("127.0.0.1", 30001);
		}

		// wait for the event
		Win32.Event event = Win32.CreateEvent(NameUtil.queryEventName(table));
		event.setWaitCounts(remote_tables.length);
	
		for (int i = 0; i < remote_tables.length; i++) {
			PhysicalInfo receiver = remote_tables[i].getRemoteTableHost();
			String tableName = remote_tables[i].getRemoteTableName();

			// send out the message for retrieving
			TableRetrieval tableRetrieval = new TableRetrieval(localIP,
					tableName, table);
			Head head = new Head(MsgType.TABLE_RETRIEVAL.getValue());
			Message msg = new Message(head, tableRetrieval);
			try {
				serverpeer.sendMessage(receiver, msg);
				expectedResponse++;
			} catch (Exception e) {
				event.decWaitCounts();
			}
		}

		Win32.WaitForMultipleObjects(event, ServerPeer.queryWaitingTime);

		Win32.CloseEvent(event);
		// results have been received


	}

	public synchronized void viewResult(String resultTableName, String sql) {

		if (model != null) {
			model.setDatabaseName(ServerPeer.EXPORTED_DB);
			model.initDB();
			model.setQuery(sql);
		}

	}

	private void dropTable(String tableName) {
		// remove the table
		// DBServer server = new DBServer(ServerPeer.EXPORTED_DB);
		Connection conn = ServerPeer.conn_exportDatabase;
		String sql = "drop table " + tableName;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String createTempTableForResult(String orginaTable,
			String[] column_names, String[] column_types) {

		String table = generateTempTableName(orginaTable);

		String sql = "create table " + table + " (";
		for (int i = 0; i < column_names.length; i++) {
			sql += column_names[i];
			sql += " " + column_types[i];
			if (i < column_names.length - 1) {
				sql += ", ";
			}
		}
		sql += ")";

		Connection conn = ServerPeer.conn_exportDatabase;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String drop = "drop table " + table;
			stmt.executeUpdate(drop);
		} catch (Exception e) {

		}

		try {
			stmt.execute(sql);
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return table;
	}

	/**
	 * return the temporary table name of result
	 */
	private String singleTableSelect() {

		String table_name = (String) tableList.get(0);

		String[] column_names = null;
		String[] column_types = null;

		String[][] columns = MetaDataAccess.metaGetColumnsWithType(
				ServerPeer.conn_metabestpeerdb, table_name);

		QueryRewriter qr = new QueryRewriter(userName);
		if (isSelectStar) { // for select *
			column_names = new String[columns.length];
			column_types = new String[columns.length];
			String selectColumns = "";

			for (int i = 0; i < columns.length; i++) {
				column_names[i] = new String(columns[i][0]);
				column_types[i] = new String(columns[i][1]);

				selectColumns += column_names[i];
				if (i < columns.length - 1) {
					selectColumns += ", ";
				}
			}
			// replace star by exact column names to guarantee remote table will
			orgSql = qr.rewriteSingleTableQuery(selectStatement);
			orgSql = orgSql.replace("*", selectColumns);
		} else {// select some specific column
			column_names = new String[projectionList.size()];
			column_types = new String[projectionList.size()];
			for (int i = 0; i < projectionList.size(); i++) {
				Attribute att = (Attribute) projectionList.get(i);
				column_names[i] = new String(att.getColName());
				if (att.isAggregate()) {
					column_types[i] = "float";
				} else {
					// check with meta data and assign column type
					for (int j = 0; j < columns.length; j++) {
						if (column_names[i].equals(columns[j][0])) {
							column_types[i] = columns[j][1];
						}
					}
				}
			}
			orgSql = qr.rewriteSingleTableQuery(selectStatement);

		}
		
		System.out.println("Rewritten: " + orgSql);

		String[] term_columns = qr.getTermColumns();
		if(term_columns.length > 0) {
			String[] all_columns = new String[column_names.length + term_columns.length];
			String[] all_types = new String[column_types.length + term_columns.length];
			System.arraycopy(column_names, 0, all_columns, 0, column_names.length);
			System.arraycopy(term_columns, 0, all_columns,
					column_names.length, term_columns.length);
			System.arraycopy(column_types, 0, all_types, 0, column_types.length);
			String text_type = "text";
			for(int i = term_columns.length; i < all_types.length; ++i)
				all_types[i] = text_type;
			column_names = all_columns;
			column_types = all_types;		
		}
		
		mappingColumns.addAll(qr.getMappingColumns());

		ArrayList<PhysicalInfo> table_owners = findTableOwnersUsingDataIndex(table_name);
		
		RemoteTableInfo[] remote_tables = executeSimpleSelect(table_owners);
		String resultTableName = createTempTableForResult(table_name,
				column_names, column_types);
		fetchTableOp(resultTableName, remote_tables);

		return resultTableName;

	}

	 private void multiTableSelect_Naive() {

		Vector downloadedTableList = downloadTables();
		
		String newSql = getLocalJoinSQL(downloadedTableList);

		// execute the new query and view result table
		try {
			Connection conn = ServerPeer.conn_exportDatabase;
			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(newSql);
			model.setResultSetAndDisplay(rs);

			// delete all temp table
			for (int i = 0; i < downloadedTableList.size(); i++) {

				String sql = "drop table "
						+ (String) downloadedTableList.get(i);

				stmt.executeUpdate(sql);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// download Multiple Tables in join query
	private Vector downloadTables(){
		
		Vector downloadedTableList = new Vector();
		
		for (int i = 0; i < tableList.size(); i++) {
			
			String tableToRetrieve = (String) tableList.get(i);
			
			String[][] columns = MetaDataAccess.metaGetColumns(ServerPeer.conn_metabestpeerdb, tableToRetrieve);
			String strProjectList = "";
			for (String[] row: columns){
				String colName = row[0];
				strProjectList += colName + ", ";
			}
			strProjectList = strProjectList.substring(0,strProjectList.length()-2);

			String sql = "select " + strProjectList +" from " + tableToRetrieve;			
			if (selectionList.size()>0){
				Condition cond = (Condition) selectionList.get(0);
				if (cond.getExprType()==Condition.EQUAL){//exact query
					Attribute att = cond.getLhs();
					String colName = att.getColName();
					String value = cond.getRightValueString();
					for (int j=0; j<columns.length; j++){
						if (colName.equals(columns[j][0])){
							sql += " where " + colName + " = "+ value;
							break;
						}	
					}
				}
			}			
			System.out.println("DEBUG: download seperate table: "+ sql);
			
			SelectExecutor partialExecutor = new SelectExecutor(sql);
			partialExecutor.setUserName(userName);
			String downloadedTable = partialExecutor.singleTableSelect();

			downloadedTableList.add(downloadedTable);
			mappingColumns.addAll(partialExecutor.getMappingColumns());
		}

		return downloadedTableList;
	}
	
	private String getLocalJoinSQL(Vector downloadedTableList){
		// convert original query to use the downloaded table.
		String newSql = new String(orgSql);

		SelectStatement newSelectStm = new SelectStatement(newSql);
		QueryRewriter qr = new QueryRewriter(userName);
		newSql = qr.rewriteMultiTableQuery(newSelectStm, tableList,
				downloadedTableList, mappingColumns);

		System.out.println("DEBUG: new query: " + newSql);
		System.out.println("DEBUG: APPENDED column ");
		for (MappingColumn mappingColumn : mappingColumns) {
			String table = mappingColumn.getColumn().getTabName();
			String colName = mappingColumn.getColumn().getColName();
			String globalColName = mappingColumn.getGlobalTermColumnName();
			String globalTerm = mappingColumn.getGlobalTermValue();
			System.out.println(table + " : " + colName + " : " + globalColName
					+ " : " + globalTerm);
		}
		
		return newSql;
	}
	
	private synchronized String generateTempTableName(String originalTable) {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		long idx = rand.nextLong();
		if (idx < 0)
			idx = -idx;
		idx = idx % 1000000;

		String table = originalTable + Long.toString(idx);

		return table;
	}

	// join_query processing
	public PartitionStatistics getPartitionStatistics(String tableName,
			String ip, int port) {
		PartitionStatistics stat = null;
		try {
			// init socket connection with bootstrap server
			Socket socket = new Socket(ip, port);

			// create message to be sent out
			Head head = new Head(MsgType.TABLE_STAT_SEARCH.getValue());

			PhysicalInfo info = peer.getPhysicalInfo();
			MsgBodyTableStatSearch body = new MsgBodyTableStatSearch(tableName,
					info.getIP(), info.getPort());

			Message message = new Message(head, body);

			/**
			 * FIXME: use oos/ois temporarily, need to use sendMessage/processEvent
			 * @author chensu 
			 */
			ObjectOutputStream oos = new ObjectOutputStream(socket
					.getOutputStream());
			message.serialize(new PhysicalInfo(ip, port), oos);

			// get reply from bootstrap server
			ObjectInputStream ois = new ObjectInputStream(socket
					.getInputStream());
			message = Message.deserialize(ois);

			if (message.getHead().getMsgType() == MsgType.TABLE_STAT_RESULT
					.getValue()) {
				MsgBodyTableStatResult msgBody = (MsgBodyTableStatResult) message
						.getBody();
				stat = msgBody.getPartitionStatistics();
			}
			oos.close();
			ois.close();
			socket.close();
		} catch (Exception e) {
			LogManager.LogException(
					"Exception caught while asking partition statistics from "
							+ ip + ":" + port, e);
		}

		return stat;
	}
	
	private ArrayList<String[]> multiTableNaiveWebSearch() {
		
		Vector downloadedTableList  = downloadTables();
		
		String newSql = getLocalJoinSQL(downloadedTableList);
		
		ArrayList<String[]> result = null;
		// execute the new query and view result table
		try {
			Connection conn = ServerPeer.conn_exportDatabase;
			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(newSql);
			
			result = new ArrayList<String[]>();
			int columnCount = rs.getMetaData().getColumnCount();
			
			String[] name = new String[columnCount];
			String[] type = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				String colName = rs.getMetaData().getColumnName(i);
				if (colName.contains("(") && colName.contains(".")){ //like count(patient123.mrn)
					int pos1 = colName.indexOf("(");
					int pos2 = colName.indexOf(".");
					colName = colName.substring(0, pos1) + "("+ colName.substring(pos2+1, colName.length() - 1) + ")"; 
				}
				name[i - 1] = colName;
				type[i - 1] = Integer.toString(rs.getMetaData()
						.getColumnType(i));
			}
			result.add(name);
			result.add(type);
			
			while(rs.next()){
				String[] tuple = new String[columnCount];
				for(int i=1; i<=columnCount; i++)
				{
					tuple[i-1] = rs.getString(i);
				}
				if (!isTupleInResultSet(tuple, result)){
					result.add(tuple);
				}
			}
			
			// delete all temp table
			for (int i = 0; i < downloadedTableList.size(); i++) {
				String sql = "drop table "
						+ (String) downloadedTableList.get(i);
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * execute a query file
	 * @param fileName
	 * @return
	 */
	public static boolean executeFile(String fileName, String userName){
		boolean test = true;
		String str = "";
		try {
			BufferedReader input = new BufferedReader (new FileReader(fileName));
			String s = null;
			while ((s = input.readLine())!=null){
				if (s.startsWith("//")){
					continue;
				}
				str += s+"\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		String[] queries = str.split(";");
		for (int i=0; i<queries.length-1; i++){
			System.out.println("///////////////////");
			System.out.println(queries[i]);
			
			SelectExecutor executor = new SelectExecutor(queries[i]);
			executor.setUserName(userName);
			ArrayList<String[]> result = executor.executeQuery();
			if (result == null || result.size()<=2){
				test = false;
				System.out.println("Failed query: \n"+queries[i]);
				if (result!=null){
					for (int j=0; j<result.size(); j++){
						String[] arr = result.get(j);
						for (int k=0; k<arr.length; k++){
							System.out.print(arr[k]+" ");
						}
						System.out.println();
					}
				}
				break;
			}
			System.out.println("---------------");
		}
		System.out.println("Finished! Success all queries:  " + test);
		return test;
	}
}
