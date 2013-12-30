/*
 * @(#) IndexBody.java 1.0 2006-7-10
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.event;

import java.io.File;
import java.io.Serializable;

/**
 * Specify the parameters that will be used for a particular 
 * <code>IndexActionListener</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-10
 */

public abstract class IndexBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2670555545273597348L;
	/**
	 * The <code>File</code>s to be operated by <code>IndexActionListener</code>.
	 */
	protected File[] fileToOperate;

	/**
	 * Returns the <code>File</code>s to be processed.
	 * 
	 * @return the <code>File</code>s to be processed
	 */
	public File[] fileToOperate() {
		return this.fileToOperate;
	}

	public String toString() {
		if (fileToOperate == null) // some body no need to use this method
			return "";

		String result = new String();
		for (int i = 0; i < fileToOperate.length; i++) {
			result += "\tat" + fileToOperate[i].getAbsolutePath() + "\r\n";
		}
		return result;
	}

}