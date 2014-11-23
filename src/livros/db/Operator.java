package livros.db;

public class Operator {
	public static final int LT = 0;
	public static final int GT = 1;
	public static final int LE = 2;
	public static final int GE = 3;
	public static final int EQ = 4;
	public static final int NE = 5;
	public static final int CONTAINS = 6;
	public static final int STARTSWITH = 7;
	public static final int ENDSWITH = 8;
	public static final int IS = 9;
	public static final int IS_NOT = 10;

	public static final int NOT = 20;
	public static final int AND = 21;
	public static final int OR = 22;

	public static final int ADD = 30;
	public static final int SUB = 31;
	public static final int MUL = 32;
	public static final int DIV = 33;
	public static final int NEGA = 34;

	public static final int CONCAT = 40;

	public static final int ASSIGN = 50;

	public static boolean isConditionOp(int op) {
		return (LT <= op && op <= IS_NOT);
	}

	public static boolean isLogicalOp(int op) {
		return (NOT <= op && op <= OR);
	}

	public static boolean isArithmeticlOp(int op) {
		return (ADD <= op && op <= NEGA);
	}

	public static boolean isUnaryOp(int op) {
		return (op == NEGA || op == NOT);
	}
	
	public static String opString(int op) {
		switch (op) {
		case LT: return "<";
		case GT: return ">";
		case LE: return "<=";
		case GE: return ">=";
		case EQ: return "=";
		case NE: return "<>";
		case ADD: return "+";
		case SUB: return "-";
		case MUL: return "*";
		case DIV: return "/";
		case NEGA: return "-";
		case NOT: return "not";
		case AND: return "and";
		case OR: return "or";
		case IS: return "is";
		case IS_NOT: return "is not";
		default: return ""+op;
		}
	}

	public static String opName(int op) {
		switch (op) {
		case LT: return "LT";
		case GT: return "GT";
		case LE: return "LE";
		case GE: return "GE";
		case EQ: return "EQ";
		case NE: return "NE";
		case CONTAINS: return "CONTAINS";
		case STARTSWITH: return "STARTSWITH";
		case ENDSWITH: return "ENDSWITH";
		case IS: return "IS";
		case IS_NOT: return "IS_NOT";
		case NOT: return "NOT";
		case AND: return "AND";
		case OR: return "OR";
		case ADD: return "ADD";
		case SUB: return "SUB";
		case MUL: return "MUL";
		case DIV: return "DIV";
		case NEGA: return "NEGA";
		case CONCAT: return "CONCAT";
		default: return ""+op;
		}
	}

}
