/**
 * 
 */
package sg.edu.nus.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author mihailupu
 *
 */
public class SchemaDumper {

	String driverClassName = "com.mysql.jdbc.Driver";
	String driverURL = "jdbc:mysql://localhost:3306/BestPeer";
	String uname = "root";
	String pword = "toor";

	public SchemaDumper(String driverClassName, String driverURL, String uname,
			String pword) {
		if (driverClassName != null) {
			this.driverClassName = driverClassName;
			this.driverURL = driverURL;
			this.uname = uname;
			this.pword = pword;
		}
	}

	public String dumpDB() {
		// Default to not having a quote character
		String columnNameQuote = "";
		DatabaseMetaData dbMetaData = null;
		Connection dbConn = null;
		try {
			Class.forName(driverClassName);
			dbConn = DriverManager.getConnection(driverURL, uname, pword);
			dbMetaData = dbConn.getMetaData();
		} catch (Exception e) {
			System.err.println("Unable to connect to database: " + e);
			return null;
		}

		try {
			StringBuffer result = new StringBuffer();
			String catalog = "";
			String schema = "";
			String tables = "";
			ResultSet rs = dbMetaData.getTables(catalog, schema, tables, null);
			if (!rs.next()) {
				System.err
						.println("Unable to find any tables matching: catalog="
								+ catalog + " schema=" + schema + " tables="
								+ tables);
				rs.close();
			} else {
				// Right, we have some tables, so we can go to work.
				// the details we have are
				// TABLE_CAT String => table catalog (may be null)
				// TABLE_SCHEM String => table schema (may be null)
				// TABLE_NAME String => table name
				// TABLE_TYPE String => table type. Typical types are "TABLE",
				// "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
				// "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
				// REMARKS String => explanatory comment on the table
				// TYPE_CAT String => the types catalog (may be null)
				// TYPE_SCHEM String => the types schema (may be null)
				// TYPE_NAME String => type name (may be null)
				// SELF_REFERENCING_COL_NAME String => name of the designated
				// "identifier" column of a typed table (may be null)
				// REF_GENERATION String => specifies how values in
				// SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM",
				// "USER", "DERIVED". (may be null)
				// We will ignore the schema and stuff, because people might
				// want to import it somewhere else
				// We will also ignore any tables that aren't of type TABLE for
				// now.
				// We use a do-while because we've already caled rs.next to see
				// if there are any rows
				do {
					String tableName = rs.getString("TABLE_NAME");
					String tableType = rs.getString("TABLE_TYPE");
					if ("TABLE".equalsIgnoreCase(tableType)) {
						// result.append("\n\n-- "+tableName);
						result.append("CREATE TABLE " + tableName + " (\n");
						ResultSet tableMetaData = dbMetaData.getColumns(null,
								null, tableName, "%");
						boolean firstLine = true;
						while (tableMetaData.next()) {
							if (firstLine) {
								firstLine = false;
							} else {
								// If we're not the first line, then finish the
								// previous line with a comma
								result.append(",\n");
							}
							String columnName = tableMetaData
									.getString("COLUMN_NAME");
							String columnType = tableMetaData
									.getString("TYPE_NAME");
							// WARNING: this may give daft answers for some
							// types on some databases (eg JDBC-ODBC link)
							int columnSize = tableMetaData
									.getInt("COLUMN_SIZE");
							int columnDecimals = tableMetaData
									.getInt("DECIMAL_DIGITS");
							String nullable = tableMetaData
									.getString("IS_NULLABLE");
							String nullString = "NULL";
							if ("NO".equalsIgnoreCase(nullable)) {
								nullString = "NOT NULL";
							}
							String columnSizeString = columnSize
									+ (columnDecimals > 0 ? ","
											+ columnDecimals : "");
							result
									.append("    " + columnNameQuote
											+ columnName + columnNameQuote
											+ " " + columnType + " ("
											+ columnSizeString + ")" + " "
											+ nullString);
						}
						tableMetaData.close();

						// Now we need to put the primary key constraint
						try {
							ResultSet primaryKeys = dbMetaData.getPrimaryKeys(
									catalog, schema, tableName);
							// What we might get:
							// TABLE_CAT String => table catalog (may be null)
							// TABLE_SCHEM String => table schema (may be null)
							// TABLE_NAME String => table name
							// COLUMN_NAME String => column name
							// KEY_SEQ short => sequence number within primary
							// key
							// PK_NAME String => primary key name (may be null)
							String primaryKeyName = null;
							StringBuffer primaryKeyColumns = new StringBuffer();
							while (primaryKeys.next()) {
								String thisKeyName = primaryKeys
										.getString("PK_NAME");
								if ((thisKeyName != null && primaryKeyName == null)
										|| (thisKeyName == null && primaryKeyName != null)
										|| (thisKeyName != null && !thisKeyName
												.equals(primaryKeyName))
										|| (primaryKeyName != null && !primaryKeyName
												.equals(thisKeyName))) {
									// the keynames aren't the same, so output
									// all that we have so far (if anything)
									// and start a new primary key entry
									if (primaryKeyColumns.length() > 0) {
										// There's something to output
										result.append(", \n ");
										if (primaryKeyName != null) {
											result.append(primaryKeyName);
										}
										result.append(" KEY ");
										result.append("("
												+ primaryKeyColumns.toString()
												+ ")");
									}
									// Start again with the new name
									primaryKeyColumns = new StringBuffer();
									primaryKeyName = thisKeyName;
								}
								// Now append the column
								if (primaryKeyColumns.length() > 0) {
									primaryKeyColumns.append(", ");
								}
								primaryKeyColumns.append(primaryKeys
										.getString("COLUMN_NAME"));
							}
							if (primaryKeyColumns.length() > 0) {
								// There's something to output
								result.append(",\n    ");
								if (primaryKeyName != null) {
									result.append(primaryKeyName);
								}
								result.append(" KEY ");
								result.append(" ("
										+ primaryKeyColumns.toString() + ")");
							}
						} catch (SQLException e) {
							// NB you will get this exception with the JDBC-ODBC
							// link because it says
							// [Microsoft][ODBC Driver Manager] Driver does not
							// support this function
							System.err
									.println("Unable to get primary keys for table "
											+ tableName + " because " + e);
						}

						result.append("\n);\n||");

						// Right, we have a table, so we can go and dump it
						// dumpTable(dbConn, result, tableName);
					}
				} while (rs.next());
				rs.close();
			}
			dbConn.close();
			return result.toString();
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use
			// Options | File Templates.
		}
		return null;
	}

}
