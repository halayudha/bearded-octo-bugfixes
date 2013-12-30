package sg.edu.nus.protocol.body;

import sg.edu.nus.peer.info.TupleIndexInfo;

/**
 * message for publishing tuple index
 * @author Wu Sai
 *
 */
public class DBTupleIndexPublication extends Body {

	// private members
	private static final long serialVersionUID = 293056576889209837L;

	/**
	 * the tuple index entry
	 */
	private TupleIndexInfo index;

	public DBTupleIndexPublication(TupleIndexInfo index) {
		this.index = index;
	}

	public TupleIndexInfo getIndex() {
		return this.index;
	}

}
