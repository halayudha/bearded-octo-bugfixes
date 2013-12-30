/**
 * Created on Sep 3, 2008
 */
package sg.edu.nus.util;

import java.net.UnknownHostException;
import java.util.ArrayList;

import sg.edu.nus.bestpeer.indexdata.ExactQuery;
import sg.edu.nus.bestpeer.queryprocessing.Win32;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.TableSearchBody;

/**
 * Utility class for search index
 * 
 * @author David Jiang
 * 
 */
public final class IndexSearch {
	@SuppressWarnings("unchecked")
	public final ArrayList<PhysicalInfo> searchTableIndex(ServerPeer peer,
			String tableName) {
		ArrayList<PhysicalInfo> result = null;
		if (tableName == null || peer == null)
			return result;

		String eventName = tableName + System.nanoTime();

		Win32.Event event = Win32.CreateEvent(eventName);
		Head head = new Head(MsgType.TABLE_INDEX_SEARCH.getValue());
		TableSearchBody body = new TableSearchBody();
		body.setEventName(eventName);
		body.setLogicalDestination(peer.getTreeNodes()[0].getLogicalInfo());
		body.setLogicalSender(peer.getTreeNodes()[0].getLogicalInfo());
		try {
			body.setPhysicalSender(peer.getPhysicalInfo());
			body.setPhysicalRequester(peer.getPhysicalInfo());
		} catch (UnknownHostException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		body.setTableName(tableName);

		try {
			peer.sendMessage(peer.getPhysicalInfo(), new Message(head, body));
			Win32.WaitForSingleObject(event);
			result = (ArrayList<PhysicalInfo>) event.getEventData();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			Win32.CloseEvent(event);
		}
		return result;
	}

	/**
	 * @author dcsvht
	 * @param peer
	 * @param tableName
	 * @param exactQuery
	 * @return list of peer contain this exact data, if no such peers return all
	 *         peers storing this table
	 */
	public final ArrayList<PhysicalInfo> searchDataIndex(ServerPeer peer,
			String tableName, ExactQuery exactQuery) {

		ArrayList<PhysicalInfo> result = null;
		
		//query cache first
		ServerGUI gui = (ServerGUI)peer.getMainFrame();
		result = gui.cacheDbIndex.getTableOwners(tableName, exactQuery);
		
		//query bootstrap
		if (result.size()==0 ){
			result = queryBootstrap(peer, tableName, exactQuery);
		}
		
		//query peer
		if (result.size()==0 ){
			result = queryPeer(peer, tableName, exactQuery);
		}
		
		return result;
	}

	private ArrayList<PhysicalInfo> queryPeer(ServerPeer peer,
			String tableName, ExactQuery exactQuery){
		
		ArrayList<PhysicalInfo> result = null;
		if (tableName == null || peer == null)
			return result;

		String eventName = tableName + System.nanoTime();

		Win32.Event event = Win32.CreateEvent(eventName);
		Head head = new Head(MsgType.TABLE_INDEX_SEARCH.getValue());
		TableSearchBody body = new TableSearchBody();
		body.setLogicalDestination(peer.getTreeNodes()[0].getLogicalInfo());
		body.setLogicalSender(peer.getTreeNodes()[0].getLogicalInfo());

		body.setEventName(eventName);
		try {
			body.setPhysicalSender(peer.getPhysicalInfo());
			body.setPhysicalRequester(peer.getPhysicalInfo());
		} catch (UnknownHostException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		body.setTableName(tableName);
		body.setExactQuery(exactQuery);

		try {
			peer.sendMessage(peer.getPhysicalInfo(), new Message(head, body));
			Win32.WaitForSingleObject(event);
			System.out.println("wake up by notification");
			result = (ArrayList<PhysicalInfo>) event.getEventData();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			Win32.CloseEvent(event);
		}
		return result;		
	}
	
	private ArrayList<PhysicalInfo> queryBootstrap(ServerPeer peer,
			String tableName, ExactQuery exactQuery){
		
		ArrayList<PhysicalInfo> result = null;
		if (tableName == null || peer == null)
			return result;

		String eventName = tableName + System.nanoTime();

		Win32.Event event = Win32.CreateEvent(eventName);
		Head head = new Head(MsgType.TABLE_INDEX_SEARCH.getValue());
		TableSearchBody body = new TableSearchBody();
		body.setLogicalDestination(peer.getTreeNodes()[0].getLogicalInfo());
		body.setLogicalSender(peer.getTreeNodes()[0].getLogicalInfo());

		body.setEventName(eventName);
		try {
			body.setPhysicalSender(peer.getPhysicalInfo());
			body.setPhysicalRequester(peer.getPhysicalInfo());
		} catch (UnknownHostException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		body.setTableName(tableName);
		body.setExactQuery(exactQuery);

		try {
			
			peer.sendMessage(new PhysicalInfo(ServerPeer.BOOTSTRAP_SERVER, ServerPeer.BOOTSTRAP_SERVER_PORT), new Message(head, body));
			
			Win32.WaitForSingleObject(event);
			System.out.println("wake up by notification");
			result = (ArrayList<PhysicalInfo>) event.getEventData();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			Win32.CloseEvent(event);
		}
		return result;		
	}
	
}
