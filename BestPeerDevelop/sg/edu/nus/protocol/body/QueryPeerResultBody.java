/**
 * Created on Sep 4, 2008
 */
package sg.edu.nus.protocol.body;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * @author David Jiang
 *
 */
public class QueryPeerResultBody extends Body {

	/**
	 * SerialID for persistence
	 */
	private static final long serialVersionUID = 6925142908881837635L;

	private String remote_table_name;

	private PhysicalInfo table_host;

	private String queryString;

	/**
	 * @param remote_table_name the remote_table_name to set
	 */
	public void setRemoteTableName(String remote_table_name) {
		this.remote_table_name = remote_table_name;
	}

	/**
	 * @return the remote_table_name
	 */
	public String getRemoteTableName() {
		return remote_table_name;
	}

	/**
	 * @param table_host the table_host to set
	 */
	public void setRemoteTableHost(PhysicalInfo table_host) {
		this.table_host = table_host;
	}

	/**
	 * @return the table_host
	 */
	public PhysicalInfo getRemoteTableHost() {
		return table_host;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

}
