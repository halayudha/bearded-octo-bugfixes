package sg.edu.nus.protocol.body;

/**
 * login message from the web interface
 * currently, it is an empty message
 * @author wusai
 *
 */
public class WebLoginBody extends Body {
	// private members
	/**
	 * The serialVersionUID is used for serializing and de-serializing
	 * this class and be SURE NOT CHANGE THIS VALUE!
	 */
	private static final long serialVersionUID = 6131083208102833065L;

	public WebLoginBody() {

	}

	/**
	 * Override <code>toString()</code> function of <code>java.lang.Object</code>.
	 * 
	 * @return A string that describes the content of the body.
	 */
	public String toString() {
		// String delim = ":";

		String result = "WebLoginBody format:= \r\n";
		result += "";

		return result;
	}
}
