/**
 * Created on Sep 2, 2008
 */
package sg.edu.nus.bestpeer.queryprocessing;

import java.util.HashMap;

import sg.edu.nus.logging.LogManager;

/**
 * Manage different signals 
 * 
 * @author David Jiang
 */
public class SignalManager {
	private HashMap<String, Signal> sigMap = new HashMap<String, Signal>();

	private static SignalManager manager = null;

	public static SignalManager getSignalManager() {
		if (manager == null)
			manager = new SignalManager();
		return manager;
	}

	private SignalManager() {
	}

	public synchronized Signal createSignal(String name) {
		Signal result = null;
		result = sigMap.get(name);
		if (result == null) {
			result = new Signal(name);
			sigMap.put(name, result);
		}
		return result;
	}

	public synchronized Signal getSignal(String name) {
		return sigMap.get(name);
	}

	public synchronized void removeSignal(String name) {
		sigMap.remove(name);
	}
}

class AsyncWait implements Runnable {
	private Win32.Event e;

	public AsyncWait(Win32.Event event) {
		e = event;
	}

	public void run() {

		Win32.WaitForSingleObject(e);
		System.out.println("signal " + e.getEventName() + " is set");
		Win32.CloseEvent(e);
	}
}

class WorkerThread1 implements Runnable {
	private String sigName;

	public WorkerThread1(String sigName) {
		this.sigName = sigName;
	}

	public void run() {
		try {
			Thread.sleep(1000);
			Win32.Event finishSig = Win32.OpenEvent(sigName);
			if (finishSig == null) {
				System.err.println("Fetal error!");
				return;
			}
			System.out.println("Finish search emp table");
			Win32.SetEvent(finishSig);
		} catch (Exception e) {
			LogManager.LogException("Exception caught for worker thread 1", e);
		}
	}
}

class WorkerThread2 implements Runnable {
	private String sigName;

	public WorkerThread2(String sigName) {
		this.sigName = sigName;
	}

	public void run() {

		try {
			Thread.sleep(5000);
			System.out.println("sleeping over");
			Win32.Event finishSig = Win32.OpenEvent(sigName);
			if (finishSig == null) {
				System.err.println("Fetal error!");
				return;
			}
			System.out.println("Finish search part table");
			Win32.SetEvent(finishSig);
		} catch (Exception e) {
			LogManager.LogException("Exception caught for worker thread 2", e);
		}
	}
}
