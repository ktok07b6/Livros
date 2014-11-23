package livros.compiler;

public class BinaryExpr extends ASTExpr
{
	public static final int LOGICAL_AND = 21;
	public static final int LOGICAL_OR = 22;

	public static final int ADD = 30;
	public static final int SUB = 31;
	public static final int MUL = 32;
	public static final int DIV = 33;
	public static final int NEGA = 34;

	public static final int LT = 0;
	public static final int GT = 1;
	public static final int LE = 2;
	public static final int GE = 3;
	public static final int EQ = 4;
	public static final int NE = 5;
	public static final int IS = 9;
	public static final int IS_NOT = 10;
	public static final int CONCAT = 40;
	public static final int ASSIGN = 50;

	public boolean isLogOp() {
		return LOGICAL_AND <= mOp && mOp <= LOGICAL_OR;
	}

	public boolean isArithOp() {
		return ADD <= mOp && mOp <= DIV;
	}

	public boolean isCondOp() {
		return LT <= mOp && mOp <= IS_NOT;
	}

	int mOp;
	ASTExpr mLeft;
	ASTExpr mRight;

	public BinaryExpr(int op, ASTExpr l, ASTExpr r) {
		super();
		mOp = op;
		mLeft = l;
		mRight = r;
	}

	public String toString() {
		return mLeft.toString() + " " +
			opString() + " " +
			mRight.toString();
	}

	String opString() {
		switch (mOp) {
		case LOGICAL_OR: return "OR";
		case LOGICAL_AND: return "AND";
		case ADD: return "+";
		case SUB: return "-";
		case MUL: return "*";
		case DIV: return "/";
		case EQ: return "=";
		case NE: return "<>";
		case LT: return "<";
		case LE: return "<=";
		case GT: return ">";
		case GE: return ">=";
		case IS: return "IS";
		case IS_NOT: return "IS NOT";
		case ASSIGN: return "=";
		case CONCAT: return "||";
		default: return "!ERROR!";
		}
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
