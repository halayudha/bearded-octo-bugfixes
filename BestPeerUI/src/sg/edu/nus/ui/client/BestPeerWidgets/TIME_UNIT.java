package sg.edu.nus.ui.client.BestPeerWidgets;

public enum TIME_UNIT {
	MINUTE("MINUTE"), HOUR("HOUR"), DAY("DAY"), WEEK("WEEK"), MONTH("MONTH"), YEAR(
			"YEAR");

	private final String name;

	TIME_UNIT(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
