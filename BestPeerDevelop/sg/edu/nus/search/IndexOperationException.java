/*
 * @(#) IndexOperationException.java 1.0 2006-3-9
 * 
 * Copyright 2006, National University of Singapore.
 * All right reserved.
 */

package sg.edu.nus.search;

/**
 * Exception thrown by the <code>FileHandler</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-3-9
 */

public class IndexOperationException extends Exception {

	// private members
	private static final long serialVersionUID = 1L;

	public IndexOperationException() {
		super();
	}

	public IndexOperationException(String arg0) {
		super(arg0);
	}

	public IndexOperationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IndexOperationException(Throwable arg0) {
		super(arg0);
	}

}
