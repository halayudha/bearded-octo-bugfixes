package sg.edu.nus.protocol.body;

import sg.edu.nus.peer.info.RangeIndexInfo;

/**
 * insert a range index entry into super peer
 * network
 * @author Wu Sai
 *
 */
public class DBRangeIndexPublication extends Body {

	// private members
	private static final long serialVersionUID = 293056576889955456L;

	/**
	 * index to be inserted
	 */
	private RangeIndexInfo index;

	public DBRangeIndexPublication(RangeIndexInfo index) {
		this.index = index;
	}

	public RangeIndexInfo getRangeIndex() {
		return this.index;
	}
}
