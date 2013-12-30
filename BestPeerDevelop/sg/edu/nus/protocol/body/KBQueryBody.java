package sg.edu.nus.protocol.body;

import java.util.Vector;

/**
 * @author Yu Bei
 */

public class KBQueryBody extends Body {

	private static final long serialVersionUID = -5394032453477392870L;
	private String[] queryKeywords;
	private boolean isFuzzy;
	private float fuzzyValue;

	@SuppressWarnings("unchecked")
	public KBQueryBody(Vector keywords, boolean isFuzzy, float fuzzyValue) {
		queryKeywords = new String[keywords.size()];
		for (int i = 0; i < queryKeywords.length; i++)
			queryKeywords[i] = (String) keywords.get(i);
		this.isFuzzy = isFuzzy;
		this.fuzzyValue = fuzzyValue;
	}

	public String[] getQueryKeywords() {
		return queryKeywords;
	}

	public boolean isFuzzy() {
		return this.isFuzzy;
	}

	public float fuzzyValue() {
		return this.fuzzyValue;
	}

}
