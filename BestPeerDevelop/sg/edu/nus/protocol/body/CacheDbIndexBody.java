/**
 * Created on Sep 1, 2008
 */
package sg.edu.nus.protocol.body;

import sg.edu.nus.peer.info.CacheDbIndex;

/**
 * @author VHTam
 * 
 */
public class CacheDbIndexBody extends Body {


	private static final long serialVersionUID = 8698469163856335668L;

	private CacheDbIndex cacheDbIndex= null;
	
	public CacheDbIndexBody(CacheDbIndex cacheDbIndex){
		this.cacheDbIndex = cacheDbIndex;
	}

	public CacheDbIndex getCacheDbIndex(){
		return cacheDbIndex;
	}
	
	public String toString() {
		String msg = "CACHE_DB_INDEX_BODY";
		return msg;
	}

}
