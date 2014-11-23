package livros.compiler;

public class UnaryExpr extends ASTExpr
{
	public static final int LOGICAL_NOT = 0;
	public static final int PLUS = 1;
	public static final int MINUS = 2;

	int mOp;
	ASTExpr mExpr;

	public UnaryExpr(int op, ASTExpr expr) {
		super();
		mOp = op;
		mExpr = expr;
	}

	public String toString() {
		return opString() + mExpr.toString();
	}

	String opString() {
		switch (mOp) {
		case LOGICAL_NOT: return "NOT ";
		case PLUS: return "";
		case MINUS: return "-";
		default: return "";
		}
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
