/**
 * Created on Sep 2, 2008
 */
package sg.edu.nus.bestpeer.queryprocessing;

/**
 * @author David Jiang
 * This is a simplified windows Event mechanism
 */
public class Signal {
	private String name;

	public Signal(String sigName) {
		name = sigName;
	}

	public void waitSignal() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	public void setSignal() {
		synchronized (this) {
			notify();
		}
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
