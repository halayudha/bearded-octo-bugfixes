package sg.edu.nus.sqlparser.java_cup_use;

import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;

import sg.edu.nus.sqlparser.Attribute;
import sg.edu.nus.sqlparser.Condition;
import sg.edu.nus.sqlparser.java_cup_tool.Symbol;

/** CUP v0.10k generated parser.
 * @version Sat Jan 24 15:55:57 SGT 2009
 */

public class parser extends sg.edu.nus.sqlparser.java_cup_tool.lr_parser {

	/** Default constructor. */
	public parser() {
		super();
	}

	/** Constructor which sets the default scanner. */
	public parser(sg.edu.nus.sqlparser.java_cup_tool.Scanner s) {
		super(s);
	}

	/** Production table. */
	protected static final short _production_table[][] = unpackFromStrings(new String[] { "\000\033\000\002\003\005\000\002\002\004\000\002\003"
			+ "\010\000\002\003\010\000\002\003\006\000\002\003\006"
			+ "\000\002\003\005\000\002\004\005\000\002\004\003\000"
			+ "\002\006\005\000\002\006\003\000\002\007\005\000\002"
			+ "\007\003\000\002\010\005\000\002\010\005\000\002\010"
			+ "\005\000\002\010\004\000\002\010\004\000\002\005\005"
			+ "\000\002\005\003\000\002\011\003\000\002\011\003\000"
			+ "\002\011\003\000\002\011\003\000\002\011\003\000\002"
			+ "\011\003\000\002\011\003" });

	/** Access to production table. */
	public short[][] production_table() {
		return _production_table;
	}

	/** Parse-action table. */
	protected static final short[][] _action_table = unpackFromStrings(new String[] { "\000\055\000\004\020\005\001\002\000\010\002\055\023"
			+ "\054\025\053\001\002\000\006\004\006\010\007\001\002"
			+ "\000\036\002\uffee\003\uffee\005\uffee\007\051\011\uffee\012"
			+ "\uffee\013\uffee\014\uffee\015\uffee\016\uffee\017\uffee\021\uffee"
			+ "\023\uffee\025\uffee\001\002\000\004\021\045\001\002\000"
			+ "\006\005\012\021\013\001\002\000\014\002\ufff9\005\ufff9"
			+ "\021\ufff9\023\ufff9\025\ufff9\001\002\000\004\004\006\001"
			+ "\002\000\004\004\014\001\002\000\014\002\ufff7\005\ufff7"
			+ "\022\ufff7\023\ufff7\025\ufff7\001\002\000\014\002\ufffd\005"
			+ "\016\022\017\023\ufffd\025\ufffd\001\002\000\004\004\043"
			+ "\001\002\000\006\003\021\004\006\001\002\000\012\002"
			+ "\uffff\005\041\023\uffff\025\uffff\001\002\000\004\026\040"
			+ "\001\002\000\022\003\031\011\032\012\030\013\033\014"
			+ "\025\015\026\016\024\017\034\001\002\000\012\002\ufff5"
			+ "\005\ufff5\023\ufff5\025\ufff5\001\002\000\010\004\uffe9\026"
			+ "\uffe9\027\uffe9\001\002\000\010\004\uffea\026\uffea\027\uffea"
			+ "\001\002\000\010\004\uffe8\026\uffe8\027\uffe8\001\002\000"
			+ "\010\004\006\026\037\027\035\001\002\000\010\004\uffec"
			+ "\026\uffec\027\uffec\001\002\000\012\002\ufff1\005\ufff1\023"
			+ "\ufff1\025\ufff1\001\002\000\010\004\uffed\026\uffed\027\uffed"
			+ "\001\002\000\010\004\uffeb\026\uffeb\027\uffeb\001\002\000"
			+ "\010\004\uffe7\026\uffe7\027\uffe7\001\002\000\012\002\ufff3"
			+ "\005\ufff3\023\ufff3\025\ufff3\001\002\000\012\002\ufff2\005"
			+ "\ufff2\023\ufff2\025\ufff2\001\002\000\012\002\ufff4\005\ufff4"
			+ "\023\ufff4\025\ufff4\001\002\000\012\002\ufff0\005\ufff0\023"
			+ "\ufff0\025\ufff0\001\002\000\006\003\021\004\006\001\002"
			+ "\000\012\002\ufff6\005\ufff6\023\ufff6\025\ufff6\001\002\000"
			+ "\014\002\ufff8\005\ufff8\022\ufff8\023\ufff8\025\ufff8\001\002"
			+ "\000\014\002\ufffa\005\ufffa\021\ufffa\023\ufffa\025\ufffa\001"
			+ "\002\000\004\004\014\001\002\000\014\002\ufffc\005\016"
			+ "\022\047\023\ufffc\025\ufffc\001\002\000\006\003\021\004"
			+ "\006\001\002\000\012\002\ufffe\005\041\023\ufffe\025\ufffe"
			+ "\001\002\000\004\004\052\001\002\000\034\002\uffef\003"
			+ "\uffef\005\uffef\011\uffef\012\uffef\013\uffef\014\uffef\015\uffef"
			+ "\016\uffef\017\uffef\021\uffef\023\uffef\025\uffef\001\002\000"
			+ "\004\004\006\001\002\000\004\004\006\001\002\000\004"
			+ "\002\000\001\002\000\012\002\001\005\012\023\001\025"
			+ "\001\001\002\000\012\002\ufffb\005\012\023\ufffb\025\ufffb"
			+ "\001\002" });

	/** Access to parse-action table. */
	public short[][] action_table() {
		return _action_table;
	}

	/** <code>reduce_goto</code> table. */
	protected static final short[][] _reduce_table = unpackFromStrings(new String[] { "\000\055\000\004\003\003\001\001\000\002\001\001\000"
			+ "\006\004\007\005\010\001\001\000\002\001\001\000\002"
			+ "\001\001\000\002\001\001\000\002\001\001\000\004\005"
			+ "\043\001\001\000\004\006\014\001\001\000\002\001\001"
			+ "\000\002\001\001\000\002\001\001\000\010\005\021\007"
			+ "\017\010\022\001\001\000\002\001\001\000\002\001\001"
			+ "\000\004\011\026\001\001\000\002\001\001\000\002\001"
			+ "\001\000\002\001\001\000\002\001\001\000\004\005\035"
			+ "\001\001\000\002\001\001\000\002\001\001\000\002\001"
			+ "\001\000\002\001\001\000\002\001\001\000\002\001\001"
			+ "\000\002\001\001\000\002\001\001\000\002\001\001\000"
			+ "\006\005\021\010\041\001\001\000\002\001\001\000\002"
			+ "\001\001\000\002\001\001\000\004\006\045\001\001\000"
			+ "\002\001\001\000\010\005\021\007\047\010\022\001\001"
			+ "\000\002\001\001\000\002\001\001\000\002\001\001\000"
			+ "\006\004\056\005\010\001\001\000\006\004\055\005\010"
			+ "\001\001\000\002\001\001\000\002\001\001\000\002\001" + "\001" });

	/** Access to <code>reduce_goto</code> table. */
	public short[][] reduce_table() {
		return _reduce_table;
	}

	/** Instance of action encapsulation class. */
	protected CUP$parser$actions action_obj;

	/** Action encapsulation object initializer. */
	protected void init_actions() {
		action_obj = new CUP$parser$actions(this);
	}

	/** Invoke a user supplied parse action. */
	@SuppressWarnings("unchecked")
	public sg.edu.nus.sqlparser.java_cup_tool.Symbol do_action(int act_num,
			sg.edu.nus.sqlparser.java_cup_tool.lr_parser parser, Stack stack,
			int top) throws java.lang.Exception {
		/* call code in generated class */
		return action_obj.CUP$parser$do_action(act_num, parser, stack, top);
	}

	/** Indicates start state. */
	public int start_state() {
		return 0;
	}

	/** Indicates start production. */
	public int start_production() {
		return 1;
	}

	/** <code>EOF</code> Symbol index. */
	public int EOF_sym() {
		return 0;
	}

	/** <code>error</code> Symbol index. */
	public int error_sym() {
		return 1;
	}

	public SQLQuery query;

	public SQLQuery getSQLQuery() {
		return query;
	}

	public void report_fatal_error(String message, Object info)
			throws java.lang.Exception {
		/* stop parsing (not really necessary since we throw an exception, but) */
		done_parsing();

		/* use the normal error message reporting to put out the message */
		// report_error("Fatal error occurred, stop parsing.", info);
		String msg = "Please check SQL syntax.Print from parser!";
		System.out.println(msg);
		throw new Exception("Please check SQL syntax.Print from parser!");

	}

	public void syntax_error(Symbol cur_token) {
	}

}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$parser$actions {

	private final parser parser;

	/** Constructor */
	CUP$parser$actions(parser parser) {
		this.parser = parser;
	}

	/** Method with the actual generated action code. */
	@SuppressWarnings("unchecked")
	public final sg.edu.nus.sqlparser.java_cup_tool.Symbol CUP$parser$do_action(
			int CUP$parser$act_num,
			sg.edu.nus.sqlparser.java_cup_tool.lr_parser CUP$parser$parser,
			Stack CUP$parser$stack, int CUP$parser$top)
			throws java.lang.Exception {
		/* Symbol object for return from actions */
		sg.edu.nus.sqlparser.java_cup_tool.Symbol CUP$parser$result;

		/* select the action based on the action number */
		switch (CUP$parser$act_num) {
		/*. . . . . . . . . . . . . . . . . . . .*/
		case 26: // op ::= LIKE
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.LIKE);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 25: // op ::= EQUAL
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.EQUAL);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 24: // op ::= NOTEQUAL
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.NOTEQUAL);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 23: // op ::= GTOE
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.GTOE);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 22: // op ::= LTOE
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.LTOE);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 21: // op ::= GREATERTHAN
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.GREATERTHAN);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 20: // op ::= LESSTHAN
		{
			Condition RESULT = null;

			RESULT = new Condition(Condition.LESSTHAN);

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					7/*op*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 19: // attribute ::= ID
		{
			Attribute RESULT = null;
			// int ileft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int iright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue i = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			RESULT = new Attribute(i.text());

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					3/*attribute*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 18: // attribute ::= ID DOT ID
		{
			Attribute RESULT = null;
			// int i1left = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int i1right = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			TokenValue i1 = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int i2left = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int i2right = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue i2 = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			RESULT = new Attribute(i1.text(), i2.text());

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					3/*attribute*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 17: // condition ::= error STRINGLIT
		{
			Condition RESULT = null;
			// int pleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).left;
			// int pright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).right;
			// Object p = (Object) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).value;
			// int sleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int sright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue s = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			System.out.println("syntax error 2: incorrect condition:"
					+ s.text());

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					6/*condition*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 1)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 16: // condition ::= attribute error
		{
			Condition RESULT = null;
			// int atleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).left;
			// int atright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).right;
			// Attribute at = (Attribute)
			// ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).value;
			// int pleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int pright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			// Object p = (Object) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).value;

			System.out.println("syntax error 1: incorrect condition");

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					6/*condition*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 1)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 15: // condition ::= attribute op attribute
		{
			Condition RESULT = null;
			// int a1left = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int a1right = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Attribute a1 = (Attribute) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int oleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).left;
			// int oright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).right;
			Condition o = (Condition) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 1)).value;
			// int a2left = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int a2right = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Attribute a2 = (Attribute) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Condition c = new Condition(a1, o.getExprType(), a2);
			c.setOpType(Condition.JOIN);
			RESULT = c;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					6/*condition*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 14: // condition ::= attribute op INTLIT
		{
			Condition RESULT = null;
			// int atleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int atright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Attribute at = (Attribute) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int oleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).left;
			// int oright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).right;
			Condition o = (Condition) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 1)).value;
			// int sleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int sright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue s = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Condition c = new Condition(at, o.getExprType(), s.text());
			c.setOpType(Condition.SELECT);
			c.setRightValueIsString(false);
			RESULT = c;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					6/*condition*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 13: // condition ::= attribute op STRINGLIT
		{
			Condition RESULT = null;
			// int atleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int atright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Attribute at = (Attribute) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int oleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).left;
			// int oright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).right;
			Condition o = (Condition) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 1)).value;
			// int sleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int sright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue s = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Condition c = new Condition(at, o.getExprType(), s.text());
			c.setOpType(Condition.SELECT);
			c.setRightValueIsString(true);
			RESULT = c;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					6/*condition*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 12: // conditionlist ::= condition
		{
			Vector RESULT = null;
			// int cleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int cright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Condition c = (Condition) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Vector clist = new Vector();
			clist.add(c);
			RESULT = clist;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					5/*conditionlist*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 11: // conditionlist ::= conditionlist COMMA condition
		{
			Vector RESULT = null;
			// int clistleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int clistright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Vector clist = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int cleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int cright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Condition c = (Condition) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			clist.add(c);
			RESULT = clist;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					5/*conditionlist*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 10: // tablelist ::= ID
		{
			Vector RESULT = null;
			// int ileft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int iright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue i = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Vector tlist = new Vector();
			tlist.add(i.text());
			RESULT = tlist;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					4/*tablelist*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 9: // tablelist ::= tablelist COMMA ID
		{
			Vector RESULT = null;
			// int tlistleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int tlistright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Vector tlist = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int ileft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int iright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			TokenValue i = (TokenValue) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			tlist.add(i.text());
			RESULT = tlist;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					4/*tablelist*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 8: // attlist ::= attribute
		{
			Vector RESULT = null;
			// int atleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int atright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Attribute at = (Attribute) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Vector v = new Vector();
			v.add(at);
			RESULT = v;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					2/*attlist*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 7: // attlist ::= attlist COMMA attribute
		{
			Vector RESULT = null;
			// int asleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int asright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Vector as = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int aleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int aright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Attribute a = (Attribute) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			as.add(a);
			RESULT = as;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					2/*attlist*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 6: // sqlquery ::= sqlquery ORDERBY attlist
		{
			SQLQuery RESULT = null;
			// int sleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int sright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			SQLQuery s = (SQLQuery) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int bleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int bright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Vector b = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			s.setOrderByList(b);
			parser.query = s;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					1/*sqlquery*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 5: // sqlquery ::= SELECT STAR FROM tablelist
		{
			SQLQuery RESULT = null;
			// int tleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int tright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Vector t = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Vector a = new Vector();
			SQLQuery sq = new SQLQuery(a, t);
			parser.query = sq;
			RESULT = sq;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					1/*sqlquery*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 3)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 4: // sqlquery ::= SELECT attlist FROM tablelist
		{
			SQLQuery RESULT = null;
			// int aleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int aright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Vector a = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int tleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int tright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Vector t = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			// Vector v1 = new Vector();
			SQLQuery sq = new SQLQuery(a, t);
			parser.query = sq;
			RESULT = sq;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					1/*sqlquery*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 3)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 3: // sqlquery ::= SELECT STAR FROM tablelist WHERE conditionlist
		{
			SQLQuery RESULT = null;
			// int tleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int tright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Vector t = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int cleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int cright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Vector c = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			Vector a = new Vector();
			SQLQuery sq = new SQLQuery(a, t, c);
			parser.query = sq;
			RESULT = sq;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					1/*sqlquery*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 5)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 2: // sqlquery ::= SELECT attlist FROM tablelist WHERE
			// conditionlist
		{
			SQLQuery RESULT = null;
			// int aleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 4)).left;
			// int aright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 4)).right;
			Vector a = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 4)).value;
			// int tleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int tright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			Vector t = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int cleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int cright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Vector c = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			// Vector v1 = new Vector();
			SQLQuery sq = new SQLQuery(a, t, c);
			parser.query = sq;
			RESULT = sq;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					1/*sqlquery*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 5)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 1: // $START ::= sqlquery EOF
		{
			Object RESULT = null;
			// int start_valleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).left;
			// int start_valright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 1)).right;
			SQLQuery start_val = (SQLQuery) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 1)).value;
			RESULT = start_val;
			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					0/*$START*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 1)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			/* ACCEPT */
			CUP$parser$parser.done_parsing();
			return CUP$parser$result;

			/*. . . . . . . . . . . . . . . . . . . .*/
		case 0: // sqlquery ::= sqlquery GROUPBY attlist
		{
			SQLQuery RESULT = null;
			// int sleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).left;
			// int sright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 2)).right;
			SQLQuery s = (SQLQuery) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 2)).value;
			// int aleft = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).left;
			// int aright = ((sg.edu.nus.sqlparser.java_cup_tool.Symbol)
			// CUP$parser$stack
			// .elementAt(CUP$parser$top - 0)).right;
			Vector a = (Vector) ((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
					.elementAt(CUP$parser$top - 0)).value;

			s.setGroupByList(a);
			parser.query = s;

			CUP$parser$result = new sg.edu.nus.sqlparser.java_cup_tool.Symbol(
					1/*sqlquery*/,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 2)).left,
					((sg.edu.nus.sqlparser.java_cup_tool.Symbol) CUP$parser$stack
							.elementAt(CUP$parser$top - 0)).right, RESULT);
		}
			return CUP$parser$result;

			/* . . . . . .*/
		default:
			throw new Exception(
					"Invalid action number found in internal parse table");

		}
	}
}