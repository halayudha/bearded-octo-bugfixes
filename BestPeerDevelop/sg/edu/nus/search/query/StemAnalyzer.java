/*
 * @(#) StemAnalyzer.java 1.0 2006-3-30
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.search.query;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Implement an <code>Analyzer</code> with both stemming function
 * and all functions of <code>StandardAnalyzer</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-3-30
 */

public class StemAnalyzer extends Analyzer {

	// public members
	/**
	 * The stop word list for <code>StemAnalyzer</code>.
	 */
	public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;

	// private members
	@SuppressWarnings("unchecked")
	private Set stopSet;

	/**
	 * Construct a <code>StemAnalyzer</code>.
	 */
	public StemAnalyzer() {
		this(STOP_WORDS);
	}

	/**
	 * Construct a <code>StemAnalyzer</code> with stop words.
	 * 
	 * @param stopWords the stop word list
	 */
	@SuppressWarnings("unchecked")
	public StemAnalyzer(Set stopWords) {
		stopSet = stopWords;
	}

	/**
	 * Construct a <code>StemAnalyzer</code> with an array of stop words.
	 * 
	 * @param stopWords an array of stop words
	 */
	public StemAnalyzer(String[] stopWords) {
		stopSet = StopFilter.makeStopSet(stopWords);
	}

	@Override
	public final TokenStream tokenStream(String fieldName, Reader reader) {
		return new PorterStemFilter(new LowerCaseFilter(new StopFilter(
				new StandardTokenizer(reader), stopSet)));
	}

}
