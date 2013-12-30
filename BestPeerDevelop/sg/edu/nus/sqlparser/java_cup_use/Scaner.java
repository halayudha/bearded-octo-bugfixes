package sg.edu.nus.sqlparser.java_cup_use;

public class Scaner extends Yylex {
	public Scaner(java.io.Reader reader) {
		super(reader);
	}

	public Scaner(java.io.InputStream instream) {
		super(instream);
	}

}
