/*
 * @(#) IndexEventHandleException.java 1.0 2006-7-7
 * 
 * Copyright 2006, National University of Singapore.
 * All right reserved.
 */

package sg.edu.nus.search;

/**
 * Exception thrown by the <code>IndexActionListener</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public class IndexEventHandleException extends Exception {

	private static final long serialVersionUID = 8403794873707201353L;

	public IndexEventHandleException() {
		super();
	}

	public IndexEventHandleException(String arg0) {
		super(arg0);
	}

	public IndexEventHandleException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IndexEventHandleException(Throwable arg0) {
		super(arg0);
	}

}
