package sg.edu.nus.dbconnection;

public class KeywordTokenizer {

	private String buf;
	private int i, length;

	public KeywordTokenizer(String s) {
		buf = s;
		length = buf.length();
		i = 0;
	}

	public String next() {
		StringBuffer sb = new StringBuffer(15);
		char c;
		boolean purenumber = true;

		if (i >= length)
			return null;

		while (i < length) {
			c = buf.charAt(i);
			if (isalpha(c) || isdigit(c))
				break;
			i++;
		}

		while (i < length) {
			c = buf.charAt(i);
			if (isalpha(c))
				purenumber = false;
			else if (!isdigit(c)) {
				if (purenumber) {
					sb.delete(0, sb.length());
					i++;
					continue;
				} else
					break;
			}
			sb.append(c);
			i++;
		}

		if (sb.length() > 0)
			return sb.toString();

		return null;
	}

	private boolean isalpha(char x) {
		return (x >= 'A' && x <= 'Z') || (x >= 'a' && x <= 'z');
	}

	private boolean isdigit(char x) {
		return x >= '0' && x <= '9';
	}
}
