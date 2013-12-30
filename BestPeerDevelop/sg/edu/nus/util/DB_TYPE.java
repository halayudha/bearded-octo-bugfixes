package sg.edu.nus.util;

public enum DB_TYPE {
	MYSQL("MySQL", "com.mysql.jdbc.Driver", "mysql://"), //
	POSTGRE("Postgre SQL", "org.postgresql.Driver", "postgresql://"), //
	SQL_SERVER("SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver",
			"microsoft:sqlserver://"), //
	DB2("DB2", "com.ibm.db2.jdbc.app.DB2Driver", "db2://"), // 
	ORACLE("Oracle", "oracle.jdbc.driver.OracleDriver", "oracle:thin:@"), //
	SYBASE("SyBase", "com.sybase.jdbc2.jdbc.SybDriver", "sybase:Tds://");

	public static DB_TYPE[] support_dbs = { DB_TYPE.MYSQL, DB_TYPE.POSTGRE,
			DB_TYPE.SQL_SERVER, DB_TYPE.DB2, DB_TYPE.ORACLE, DB_TYPE.SYBASE };

	private String name;
	private String driver;
	private String connStr;

	DB_TYPE(String name, String driver, String connStr) {
		this.name = name;
		this.driver = driver;
		this.connStr = connStr;
	}

	public String getName() {
		return name;
	}

	public String getDriver() {
		return driver;
	}

	public String getConnString() {
		return connStr;
	}

	public static String getDriverByType(String type) {
		for (int i = 0; i < support_dbs.length; i++) {
			if (support_dbs[i].getName().equals(type)) {
				return support_dbs[i].getDriver();
			}
		}
		return DB_TYPE.MYSQL.getDriver();
	}

	public static DB_TYPE getByType(String type) {
		for (int i = 0; i < support_dbs.length; i++) {
			if (support_dbs[i].getName().equals(type)) {
				return support_dbs[i];
			}
		}
		return DB_TYPE.MYSQL;
	}

	public static String getURL(String dbtype, String server, String port) {
		String url = "jdbc:" + DB_TYPE.getByType(dbtype).getConnString()
				+ server + ":" + port + "/";
		return url;
	}

	public static String getURL(DB_TYPE dbtype, String server, String port) {
		String url = "jdbc:" + dbtype.getConnString() + server + ":" + port
				+ "/";
		return url;
	}

	public static String getDNS(DB_TYPE dbtype, String server, String port,
			String dbName) {
		String url = "jdbc:" + dbtype.getConnString() + server + ":" + port;
		String dns = url;

		if (dbtype == DB_TYPE.ORACLE)
			dns = url + ":";
		else if (dbtype == DB_TYPE.SQL_SERVER)
			dns += ";DatabaseName=";
		else
			dns = url + "/";
		dns += dbName;
		return dns;
	}
}
