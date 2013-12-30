package sg.edu.nus.bestpeer.joinprocessing;

import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.body.Body;

public class MsgBodyJoinProcessing extends Body {

	private static final long serialVersionUID = 2432601402830492014L;

	private String queryString;

	private AgentJoinObject agentJoinObject = null;

	private PhysicalInfo sender = null;

	private String[][] columns = null;

	private String userName; // user who submit query

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public MsgBodyJoinProcessing(AgentJoinObject agentJoinObject) {
		this.agentJoinObject = agentJoinObject;
	}

	public AgentJoinObject getAgentJoinObject() {
		return agentJoinObject;
	}

	public void setSender(PhysicalInfo sender) {
		this.sender = sender;
	}

	public PhysicalInfo getSender() {
		return sender;
	}

	public void setColumns(String[][] columns) {
		this.columns = columns;
	}

	public String[][] getColumns() {
		return columns;
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
