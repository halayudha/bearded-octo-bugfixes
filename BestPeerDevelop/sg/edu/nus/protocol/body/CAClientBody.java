/*
 * @(#) CAClientBody.java 1.0 2006-1-6
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.security.cert.Certificate;

/**
 * Implement the message body used for communicating with peers which 
 * contains the user identifier, certificate, signature and other information.
 * 
 * @author Wu Sai
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-1-6
 */

public class CAClientBody extends Body {

	// private members
	private static final long serialVersionUID = 344546576889955456L;

	private String userID;
	private String groupName; // group name
	private String groupInfo; // group information
	private String invitedUser; // invite this user to join our group
	private String invitedGroup; // add users to this group
	private String rawData; // raw data without encrption
	private byte[] signaure; // signature
	private Certificate certificate; // certificate

	/**
	 * Construct a message body used for commnicating with peers.
	 * 
	 * @param uid the user identifier
	 * @param groupName the group name
	 * @param data the raw data used for evaluating user's validness
	 * @param sign the signature
	 */
	public CAClientBody(String uid, String groupName, String data, byte[] sign) {
		this.userID = uid;
		this.rawData = data;
		this.signaure = sign;
		this.groupName = groupName;
		this.groupInfo = new String();
		this.invitedUser = new String();
		this.invitedGroup = new String();
	}

	/**
	 * Get the user identifier.
	 * 
	 * @return the user identifier
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Get the group name.
	 * 
	 * @return the group name
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Get the group information.
	 * 
	 * @return the group information
	 */
	public String getGroupInfo() {
		return groupInfo;
	}

	/**
	 * Set the group information.
	 * 
	 * @param s the group information
	 */
	public void setGroupInfo(String s) {
		groupInfo = s;
	}

	/**
	 * Get the invited user name.
	 * 
	 * @return the invited user name
	 */
	public String getInvitedUser() {
		return invitedUser;
	}

	/**
	 * Set the invited user name.
	 * 
	 * @param u the invited user name
	 */
	public void setInvitedUser(String u) {
		invitedUser = u;
	}

	/**
	 * Get the invited group name.
	 * 
	 * @return the invited group name
	 */
	public String getInvitedGroup() {
		return invitedGroup;
	}

	/**
	 * Set the invited group name.
	 * 
	 * @param g the invited group name
	 */
	public void setInvitedGroup(String g) {
		invitedGroup = g;
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
	 * Get the raw data used for evaluating user's validness.
	 * 
	 * @return the raw data used for evaluating user's validness
	 */
	public String getData() {
		return rawData;
	}

	/**
	 * Get the signature.
	 * 
	 * @return the signature
	 */
	public byte[] getSignature() {
		return signaure;
	}

	/**
	 * Set the user's certificate
	 * 
	 * @param cert the user's certificate
	 */
	public void setCertificate(Certificate cert) {
		this.certificate = cert;
	}

	@Override
	public String toString() {
		String delim = ":";
		String result = "CAClientBody format:= userID:Certificate:data\r\n";
		result = result + userID + delim + certificate.toString() + delim
				+ rawData;
		return result;
	}

}
