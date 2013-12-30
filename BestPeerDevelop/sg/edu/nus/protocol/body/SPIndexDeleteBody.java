/*
 * @(#) SPIndexDeleteBody.java 1.0 2007-1-26
 * 
 * Copyright 2007, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.net.InetAddress;

import sg.edu.nus.indexkeyword.TermDocument;
import sg.edu.nus.peer.info.LogicalInfo;
import sg.edu.nus.peer.info.PhysicalInfo;

/**
 * Implement the message body used for removing index terms and their
 * corresponding fields from super peer network, for the purpose of
 * updating distributed index.
 * <p>
 * The old version <code>{@link SPDeleteBundleBody}</code> and 
 * <code>{@link SPDeleteBody}</code> are deprecated
 * for the sake of unefficiency of building index. 
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 * @see sg.edu.nus.peer.event.SPIndexDeleteListener
 */

public class SPIndexDeleteBody extends SPIndexInsertBody {

	private static final long serialVersionUID = 3343195240115179272L;

	/**
	 * Construct SPIndexDeleteBody without specifying TermDocument.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 */
	public SPIndexDeleteBody(String pid, InetAddress ipaddr, int port) {
		super(pid, ipaddr, port);
	}

	/**
	 * Construct SPIndexDeleteBody with specified parameters that is used 
	 * for sending message from client peer to owner super peer.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be removed
	 */
	public SPIndexDeleteBody(String pid, InetAddress ipaddr, int port,
			TermDocument doc) {
		super(pid, ipaddr, port, doc);
	}

	/**
	 * Construct SPIndexDeleteBody with specified parameters that is used
	 * for sending message from a super peer to other super peers.
	 * 
	 * @param pid the peer identifier
	 * @param ipaddr the IP address of the peer
	 * @param port the port of the peer
	 * @param doc the document contains terms and fields to be removed
	 * @param phySender the IP address and port of the peer who sends the message
	 * @param lgcSender the logical position of the sender in BATON tree
	 * @param lgcReceiver the logical position of the receiver in BATON tree
	 */
	public SPIndexDeleteBody(String pid, InetAddress ipaddr, int port,
			TermDocument doc, PhysicalInfo phySender, LogicalInfo lgcSender,
			LogicalInfo lgcReceiver) {
		super(pid, ipaddr, port, doc, phySender, lgcSender, lgcReceiver);
	}

	public String toString() {
		String result = "SP_DELETE " + debug();
		return result;
	}

}