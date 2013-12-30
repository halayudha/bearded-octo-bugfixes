package sg.edu.nus.peer.event;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import sg.edu.nus.bestpeer.queryprocessing.Win32;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.PhysicalInfo;
import sg.edu.nus.peer.management.EventHandleException;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.TableTupleBody;
import sg.edu.nus.util.NameUtil;

/**
 * process the tuple data
 * 
 * @author chris
 * 
 */
public class TableTupleListener extends ActionAdapter {

	private static Hashtable<String, int[]> resultCount;

	public TableTupleListener(AbstractMainFrame gui) {
		super(gui);
		resultCount = new Hashtable<String, int[]>();
	}

	public void actionPerformed(PhysicalInfo dest, Message msg)
			throws EventHandleException {
		synchronized(resultCount){
		try {
			TableTupleBody dataMessage = (TableTupleBody) msg.getBody();
			String tableName = dataMessage.getTableName();
			String sender = dataMessage.getSender().getIP();
			Vector<String[]> data = dataMessage.getData();
			Connection conn = ServerPeer.conn_exportDatabase;
			Statement stmt = conn.createStatement();

			if (data.size() >= 0) {
				for (int i = 0; i < data.size(); i++) {
					String[] tuple = data.get(i);

					String sql = "insert into " + tableName + " values(";
					for (int j = 0; j < tuple.length; j++) {
						String svalue = tuple[j];
						sql += svalue + ",";
					}
					sql = sql.substring(0, sql.length() - 1) + ")";
					
					stmt.executeUpdate(sql);
				}
				stmt.close();

				//synchronized(resultReceived)
				//{
					if (resultCount.containsKey(sender+tableName)) {
						int[] received = resultCount.get(sender+tableName);
						received[1] += data.size();
						resultCount.put(sender+tableName, received );
					} else
					{
						resultCount.put(sender+tableName, new int[]{Integer.MAX_VALUE,data.size()});
					}
				//}				
			}
			if (dataMessage.isFinished()) {
				int count = dataMessage.getCount();
				int[] received = resultCount.get(sender+tableName);
				received[0] = count;
				resultCount.put(sender+tableName, received);
			}

			if (resultCount.containsKey(sender+tableName)) {
				int[] count = TableTupleListener.resultCount.get(sender+tableName);

				if (count[1] >= count[0]) {
					// done, we should notify the even
					Win32.Event event = Win32.OpenEvent(NameUtil
							.queryEventName(tableName));
					if (event != null) {
							event.decWaitCounts();
							System.out.println("wake up the query sender");
							System.out.println(NameUtil.queryEventName(tableName) + " current count: " + event.getWaitCounts());
						Win32.SetEvent(event);
					}
					else{
					}
					resultCount.remove(sender+tableName);
				}
			}
		} catch (Exception e) {
			System.out.println("Fail to process the TableData message");
			e.printStackTrace();
		}
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException {
		if (msg.getHead().getMsgType() == MsgType.TABLE_DATA.getValue())
			return true;
		return false;
	}
}
