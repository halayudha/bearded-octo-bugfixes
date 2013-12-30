/**
 * Created on Oct 6, 2008
 */
package sg.edu.nus.bestpeer.queryprocessing;

import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * @author David Jiang
 *
 */
public class RemoteTableInfo {
	private PhysicalInfo remote_table_host;
	private String remote_table_name;

	/**
	 * @param remote_table_host the remote_table_host to set
	 */
	public void setRemoteTableHost(PhysicalInfo remote_table_host) {
		this.remote_table_host = remote_table_host;
	}

	/**
	 * @return the remote_table_host
	 */
	public PhysicalInfo getRemoteTableHost() {
		return remote_table_host;
	}

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

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(remote_table_name);
		buf.append("@");
		buf.append(remote_table_host.toString());
		return buf.toString();
	}

}
