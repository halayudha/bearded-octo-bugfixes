package sg.edu.nus.protocol.body;

/**
 * Message used for sending from knowledge bank to super peer.
 * 
 * @author Xu Linhao
 */

public class KBIndexBody extends Body {

	private static final long serialVersionUID = 4483380552543868395L;
	private String fileName;

	/**
	 * Constructor.
	 * 
	 * @param fn the file to be indexed
	 */
	public KBIndexBody(String fn) {
		this.fileName = fn;
	}

	/**
	 * Returns the file to be indexed.
	 * 
	 * @return returns the file to be indexed
	 */
	public String getFileName() {
		return this.fileName;
	}

}