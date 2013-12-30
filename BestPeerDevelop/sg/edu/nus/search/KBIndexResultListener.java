/*
 * @(#) KBIndexResultListener.java 1.0 2006-7-7
 * 
 * Copyright 2006 National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search;

import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.indexkeyword.FieldConstant;
import sg.edu.nus.indexkeyword.FileHandlerException;
import sg.edu.nus.search.event.SPIndexResultBody;

/**
 * Process results.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-7-7
 */

public final class KBIndexResultListener extends SPIndexOperateAdapter {

	/* indicate if pass results to knowledgebank application */
	private static final String dir = "C:/tomcat/bp-results/";

	public KBIndexResultListener(AbstractMainFrame gui, Analyzer analyzer) {
		super(gui, analyzer);
	}

	/**
	 * Optimize the Lucene index.
	 * 
	 * @throws IOException, FileHandlerException
	 */
	public void actionPerformed(IndexEvent event) throws IOException,
			FileHandlerException {
		try {
			SPIndexResultBody body = (SPIndexResultBody) event.getBody();
			Document[] docs = body.getResults();

			// dump results to disk
			File file = new File(dir);
			if (!file.exists())
				file.mkdirs();

			String path = null;
			String ipaddr = null;
			int port = 0;
			Document doc = null;
			int size = docs.length;
			for (int i = 0; i < size; i++) {
				file = new File(dir + System.nanoTime() + ".dat");
				if (file.createNewFile()) {
					PrintWriter writer = new PrintWriter(file);
					doc = docs[i];

					// check if duplicate
					ipaddr = doc.get(FieldConstant.InetAddress.getValue());
					port = Integer.parseInt(doc.get(FieldConstant.Port
							.getValue()));
					// if (ipaddr.equals(Inet.getInetAddress()) && port ==
					// ServerPeer.LOCAL_SERVER_PORT)
					// continue;

					// write path name
					path = doc.get(FieldConstant.PathName.getValue());
					writer.println(path);
					// write file name
					int idx = 0;
					idx = path
							.lastIndexOf(System.getProperty("file.separator"));
					if (idx != -1)
						writer.println(path.substring(idx + 1, path.length()));
					else
						writer.println(path);
					// write size
					writer.println(doc.get(FieldConstant.Size.getValue()));
					// write last modified
					writer.println(doc.get(FieldConstant.LastModified
							.getValue()));
					// write ip address
					writer.println(ipaddr);
					// write port
					writer.println(port);
					// write score
					writer.println(doc.get(FieldConstant.Score.getValue()));
					// write summary
					writer.println(doc.get(FieldConstant.Summary.getValue()));

					// close file
					writer.close();
				}
			}
		} catch (Exception e) {
			throw new FileHandlerException("Errors when processing results", e);
		}
	}

	public boolean isConsumed(IndexEvent event)
			throws IndexEventHandleException {
		if (event.getHead().getValue() == IndexEventType.KB_RESULT.getValue()) {
			return true;
		}
		return false;
	}

}