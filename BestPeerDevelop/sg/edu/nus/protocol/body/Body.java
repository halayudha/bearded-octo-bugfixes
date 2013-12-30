/*
 * @(#) Body.java 1.0 2006-1-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;

/**
 * Define the abstract body of the message.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-4
 */

public abstract class Body implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7553141504873070971L;

	/**
	 * Clone self.
	 * 
	 * @return The instance of <code>Body</code>.
	 */
	public Object clone() {
		Body instance = null;
		try {
			instance = (Body) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return instance;
	}

	/**
	 * @author chensu
	 * @date 2009-4-28
	 */
	public String toString() {
		return "Empty";
	}
}