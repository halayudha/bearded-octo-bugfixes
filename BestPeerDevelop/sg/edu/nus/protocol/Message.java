/*
 * @(#) Message.java 1.0 2006-1-4
 * 
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.AbstractPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.body.Body;

/**
 * Implement a message to be communicated between peers.
 * Each message includes two parts: a head and a body.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-4
 */

public class Message implements Cloneable, Serializable {

	/**
	 * The serialVersionUID is used for serializing and de-serializing
	 * this class and be SURE NOT CHANGE THIS VALUE!
	 */
	private static final long serialVersionUID = -1521600004665716668L;

	// private members
	private Head head; // the head of the message
	private Body body; // the body of the message

	/**
	 * Construct a message with specified head and body.
	 * 
	 * @param head The head of the message.
	 * @param body The body of the message.
	 */
	public Message(Head head, Body body) {
		this.head = (Head) head.clone();
		this.body = (Body) body.clone();
	}

	/**
	 * Construct a confirm message with head only.
	 * 
	 * @param head The head of the message.
	 */
	public Message(Head head) {
		this.head = (Head) head.clone();
		this.body = null;
	}

	/**
	 * Clone self.
	 * 
	 * @return The instance of <code>Message</code>.
	 */
	public Object clone() {
		Message instance = null;
		try {
			instance = (Message) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		/* this is a deep clone operation for all necessary components */
		instance.head = (Head) this.head.clone(); // clone head
		instance.body = (Body) this.body.clone(); // clone body

		return instance;
	}

	/**
	 * Get the head of the message.
	 * 
	 * @return The head of the message.
	 */
	public Head getHead() {
		return head;
	}

	/**
	 * Change the head of the message, if necessary.
	 * 
	 * @param head The head of the message.
	 */
	public void setHead(Head head) {
		this.head = (Head) head.clone();
	}

	/**
	 * Get the body of the message.
	 * 
	 * @return The body of the message.
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * Change the body of the message, if necessary.
	 * 
	 * @param body The body of the message.
	 */
	public void setBody(Body body) {
		this.body = (Body) body.clone();
	}

	/**
	 * get size of the message (in byte)
	 * @return the size of the message in byte
	 */
	public int getSize() {
		try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream oStream = new ObjectOutputStream(bStream);
			oStream.writeObject(this);
			byte[] byteVal = bStream.toByteArray();
			return byteVal.length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Serialize the message by <code>ObjectOutputStream</code>.
	 * 
	 * @param oos the output stream 
	 */
	public void serialize(PhysicalInfo dest, ObjectOutputStream oos)
	{
		try
		{
			LogManager.LogOutgoing(dest.getIP() + ":" + dest.getPort(), this);
			
			//Tam
			MessageCrypter messCrypter = new MessageCrypter();
	    	
	    	Key key = AbstractPeer.getEncryptionKey();
	    	
	        Cipher ecipher;
			try {
				ecipher = Cipher.getInstance("DES");
		        ecipher.init(Cipher.ENCRYPT_MODE, key);
				messCrypter.encrypt(this, ecipher);
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
					
			oos.writeObject(messCrypter);
			
			System.out.println("Send message crypter");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Deserialize <code>ObjectInputStream</code> to 
	 * reconstruct an instance of the <code>Message</code>.
	 * 
	 * @return The <code>Message</code> object. 
	 */
	public static Message deserialize(ObjectInputStream ois)
	{
		Message result = null;
		
		try
		{	
			// Tam
			Object obj = ois.readObject();
			// System.out.println("Class: " + obj.getClass().getName());
			
			if (obj.getClass().getName().contains("MessageCrypter")) {
				MessageCrypter messCrypter = (MessageCrypter) obj;

				System.out.println("Receive message crypter");

				Key key = AbstractPeer.getEncryptionKey();
				
				// Prepare the encrypter
				Cipher dcipher;
				try {
					dcipher = Cipher.getInstance("DES");
					dcipher.init(Cipher.DECRYPT_MODE, key);

					result = (Message) messCrypter.decrypt(dcipher);

				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				}
			} 
			else { // if it is not MessageCrypter object, for old code to work...
				result = (Message)obj;
			}
			
		}
		catch (ClassNotFoundException e)
		{
			
			e.printStackTrace();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Override <code>toString()</code> function of <code>java.lang.Object</code>.
	 * 
	 * @return A string that describes the content of the body.
	 */
	public String toString() {
		String delim = "\t";

		String result = "";
		if (head != null)
			result += head.toString() + delim;

		if (body != null)
			result += body.toString();

		return result;
	}

}