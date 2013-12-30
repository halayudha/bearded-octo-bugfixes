/** represents join/select condition **/

package sg.edu.nus.sqlparser;

import java.util.Enumeration;
import java.util.Hashtable;

public class Condition {

	/** enumeration of the op code in the condition **/

	public static final int LESSTHAN = 1;
	public static final int GREATERTHAN = 2;
	public static final int LTOE = 3;
	public static final int GTOE = 4;
	public static final int EQUAL = 5;
	public static final int NOTEQUAL = 6;
	public static final int LIKE = 7;

	public static final int SELECT = 1;
	public static final int JOIN = 2;

	Attribute lhs; // left hand side of the condition
	int optype; // Whether select condition or join condition
	int exprtype; // Comparison type, equal to/less than/greater than etc.,
	Object rhs; // This is Attribute for Join condition and String for Select
	// Condition

	boolean rightValueIsString = true;

	public void setRightValueIsString(boolean isString) {
		rightValueIsString = isString;
	}

	public boolean isRightValueString() {
		return rightValueIsString;
	}
	

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Condition){
			return this.toString().equalsIgnoreCase(obj.toString());
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(lhs).append(' ');
		builder.append(getOperatorString()).append(' ');
		if(optype == Condition.SELECT)
			builder.append(getRightValueString());
		else if(optype == Condition.JOIN)
			builder.append(getRhs());
		else 
			throw new RuntimeException("Unknown optype");
		return builder.toString();
	}

	public Condition(Attribute attr, int type, Object value) {
		lhs = attr;
		exprtype = type;
		this.rhs = value;
		this.optype = Condition.SELECT;
	}

	public Condition(int type) {
		exprtype = type;
		this.optype = Condition.SELECT;
	}

	public Attribute getLhs() {
		return lhs;
	}

	public void setLhs(Attribute attr) {
		lhs = attr;
	}

	public void setOpType(int num) {
		optype = num;
	}

	public int getOpType() {
		return optype;
	}

	public void setExprType(int num) {
		exprtype = num;
	}

	public int getExprType() {
		return exprtype;
	}

	public Object getRhs() {
		return rhs;
	}

	public void setRhs(Object value) {
		rhs = value;
	}

	public void flip() {
		if (optype == JOIN) {
			Object temp = lhs;
			lhs = (Attribute) rhs;
			rhs = temp;
		}
	}

	public Object clone() {
		Attribute newlhs = (Attribute) lhs.clone();
		Object newrhs;

		if (optype == SELECT)
			newrhs = (String) rhs;
		else
			newrhs = (Attribute) ((Attribute) rhs).clone();
		Condition newcn = new Condition(newlhs, exprtype, newrhs);
		newcn.setOpType(optype);
		newcn.setRightValueIsString(rightValueIsString);
		return newcn;
	}

	public String getRightValueString() {
		String rightValue = null;
		if (this.optype == JOIN) {
			rightValue = rhs.toString();
		} else {// selection operator
			rightValue = rhs.toString();
			if (rightValueIsString) {
				rightValue = "'" + rightValue + "'";
			}
		}
		return rightValue;
	}

	public String getRightValueString(Hashtable<String, String[][]> metaColumns) {
		String rightValue = null;
		if (this.optype == JOIN) {
			rightValue = rhs.toString();
		} else {// selection operator
			if (metaColumns == null) {
				// assume right value as String
				// rightValue = "'"+rhs.toString()+"'";

				// assume as number
				rightValue = rhs.toString();

			} else {// check db context
				if (lhs.tblname == null) {
					Enumeration<String[][]> iter = metaColumns.elements();
					boolean isStringValue = false;
					while (iter.hasMoreElements()) {
						String[][] columns = iter.nextElement();

						for (int i = 0; i < columns.length; i++) {
							if (lhs.colname.equals(columns[i][0])) {
								if (columns[i][1].contains("varchar") || columns[i][1].contains("text"))
									isStringValue = true;
							}
						}
					}
					rightValue = isStringValue ? ("'" + rhs.toString() + "'")
							: rhs.toString();

				} else {
					String[][] columns = metaColumns.get(lhs.tblname);
					boolean isStringValue = false;
					for (int i = 0; i < columns.length; i++) {
						if (lhs.colname.equals(columns[i][0])) {
							if (columns[i][1].contains("varchar")  || columns[i][1].contains("text"))
								isStringValue = true;
						}
					}
					rightValue = isStringValue ? ("'" + rhs.toString() + "'")
							: rhs.toString();

				}
			}
		}
		return rightValue;
	}

	public String getOperatorString() {
		return getOperatorString(exprtype);
	}

	private String getOperatorString(int type) {
		if (type == GREATERTHAN) {
			return ">";
		} else if (type == GTOE) {
			return ">=";
		} else if (type == LESSTHAN) {
			return "<";
		} else if (type == LTOE) {
			return "<=";
		} else if (type == EQUAL) {
			return "=";
		} else if (type == NOTEQUAL) {
			// return "!=";
			return " like ";// currently use != replace for "like" in syntax
		} else {
			return null;
		}
	}
}
