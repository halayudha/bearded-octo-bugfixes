package sg.edu.nus.logging;

public enum TIME_UNIT {
	MINUTE("MINUTE"), HOUR("HOUR"), DAY("DAY"), WEEK("WEEK"), MONTH("MONTH"), YEAR(
			"YEAR");

	private final String name;

	private static final TIME_UNIT[] unit_set = { TIME_UNIT.MINUTE,
			TIME_UNIT.HOUR, TIME_UNIT.DAY, TIME_UNIT.WEEK, TIME_UNIT.MONTH,
			TIME_UNIT.YEAR };

	TIME_UNIT(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static TIME_UNIT getByName(String string) {
		for (int i = 0; i < unit_set.length; i++) {
			if (string.equals(unit_set[i]))
				return unit_set[i];
		}
		return TIME_UNIT.MINUTE;
	}
}
