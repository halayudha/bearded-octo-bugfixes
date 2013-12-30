package sg.edu.nus.bestpeer.joinprocessing;

public class Path {
	String path = null;
	int cost = 0;

	public Path(String path, int cost) {
		this.path = new String(path);
		this.cost = cost;
	}
}