/**
 * Created on Sep 3, 2008
 */
package sg.edu.nus.bestpeer.queryprocessing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class implement Win32 Event mechanism
 * 
 * @author David Jiang
 * 
 */
public final class Win32 {

	public final static class Event {
		private String eventName;
		private int usedCount;
		private int waitCounts;
		private Object eventData;
		private ArrayList<Object> eventDataList;
		private boolean isCompleted;

		public Event(String name) {
			eventName = name;
			usedCount = 1;
			eventData = null;
			waitCounts = 0;
			eventDataList = new ArrayList<Object>();
			isCompleted = false;
		}

		public void addEventData(Object data) {
			eventDataList.add(data);
		}

		public Object[] getEventDataArray() {
			return eventDataList.toArray();
		}

		public int incUsedCount() {
			return ++usedCount;
		}

		public int decUsedCount() {
			return --usedCount;
		}

		public int getWaitCounts() {
			return waitCounts;
		}

		public int decWaitCounts() {
			return --waitCounts;
		}

		public void setWaitCounts(int nCount) {
			waitCounts = nCount;
		}

		/**
		 * @param eventData
		 *          the eventData to set
		 */
		public void setEventData(Object eventData) {
			this.eventData = eventData;
		}

		/**
		 * @return the eventData
		 */
		public Object getEventData() {
			return eventData;
		}

		/**
		 * @param usedCount
		 *          the usedCount to set
		 */

		public void setUsedCount(int usedCount) {
			this.usedCount = usedCount;
		}

		/**
		 * @return the usedCount
		 */
		public int getUsedCount() {
			return usedCount;
		}

		/**
		 * @param eventName
		 *          the eventName to set
		 */
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}

		/**
		 * @return the eventName
		 */
		public String getEventName() {
			return eventName;
		}

	}

	public static final synchronized Win32.Event CreateEvent(String name) {
		Win32.Event e = events.get(name);
		if (e == null) {
			e = new Win32.Event(name);
			events.put(name, e);
		} else
			e.incUsedCount();
		return e;
	}

	public static final synchronized Win32.Event OpenEvent(String name) {
		return events.get(name);
	}

	public static final synchronized void CloseEvent(Win32.Event e) {
		if (e.decUsedCount() == 0){
			System.out.println("remove event:..." + e.getEventName());
			events.remove(e.getEventName());
		}
	}

	public static final void WaitForMultipleObjects(Win32.Event e, int nCount,
			long timeOut) {
		e.setWaitCounts(nCount);
		long delay = 0;
		synchronized (e) {
			while (e.getWaitCounts() > 0 && delay < timeOut) { // Not all tasks
				// are finished
				try {
					e.wait(1000);
					delay += 1000;
				} catch (Exception ex) {
					System.err.println(ex);
					ex.printStackTrace();
				}
			}
		}
	}

	public static final void WaitForMultipleObjects(Win32.Event e, long timeOut) {
		long delay = 0;
		synchronized (e) {
			while (e.getWaitCounts() > 0 && delay < timeOut) { // Not all tasks
				// are finished
				try {
					e.wait(1000);
					delay += 1000;
				} catch (Exception ex) {
					System.err.println(ex);
					ex.printStackTrace();
				}
			}
		}
	}

	public static final void WaitForSingleObject(Win32.Event e) {
		synchronized (e) {
			if(e.isCompleted)
				return;
			try {
				e.wait();
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	public static final synchronized void SetEvent(Win32.Event e) {
		synchronized (e) {
			e.isCompleted = true;
			e.notifyAll();
		}
	}

	public static final synchronized void InitWin32() {
		if (events == null)
			events = new HashMap<String, Win32.Event>();
	}

	private static HashMap<String, Win32.Event> events;

}
