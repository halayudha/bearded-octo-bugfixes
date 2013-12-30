/*
 * @(#) CAServerBody.java 1.0 2006-2-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.security.PrivateKey;
import java.security.cert.*;

/**
 * Implement the message body used for communicating with 
 * the certificate authority which contains the user identifier,
 * the list of groups, the list of users, private keys and certificates.
 * 
 * @author Wu Sai
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-1-6
 */

public class CAServerBody extends Body {

	// private members
	private static final long serialVersionUID = 193735485923423423L;

	private String userID;
	private String[] groupList;
	private String[] userList;
	private PrivateKey privateKey;
	private Certificate certificate;

	/**
	 * Get the user identifier.
	 * 
	 * @return the user identifier
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Construct the body with the user identifier.
	 * 
	 * @param uid the user identifier
	 */
	public CAServerBody(String uid) {
		userID = uid;
	}

	/**
	 * Get the private key of the user.
	 * 
	 * @return the user's private key
	 */
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * Set the private key of the user.
	 * 
	 * @param key the user's private key 
	 */
	public void setPrivateKey(PrivateKey key) {
		this.privateKey = key;
	}

	/**
	 * Get the user's certificate.
	 * 
	 * @return the user's certificate
	 */
	public Certificate getCertificate() {
		return certificate;
	}

	/**
	 * Set the user's certificate.
	 * 
	 * @param cert the user's certificate
	 */
	public void setCertificate(Certificate cert) {
		this.certificate = cert;
	}

	/**
	 * Get the group list.
	 * 
	 * @return a list of groups
	 */
	public String[] getGroupList() {
		return groupList;
	}

	/**
	 * Set the group list.
	 * 
	 * @param gl a list of groups
	 */
	public void setGroupList(String[] gl) {
		groupList = (String[]) gl.clone();
	}

	/**
	 * Get the user list.
	 * 
	 * @return a list of users
	 */
	public String[] getUserList() {
		return userList;
	}

	/**
	 * Set the user list.
	 * 
	 * @param ul a list of users
	 */
	public void setUserList(String[] ul) {
		userList = (String[]) ul.clone();
	}

	@Override
	public String toString() {
		String delim = ":";

		String result = "CAServerBody format : userID:groupList:"
				+ "userList:private key:certifacte\r\n";

		String gl = new String();
		if (this.groupList != null) {
			gl = groupList.toString();
		}

		String ul = new String();
		if (userList != null) {
			ul = userList.toString();
		}
		result += userID + delim + gl + delim + ul + delim
				+ privateKey.toString() + delim + certificate.toString();
		return result;
	}

}
