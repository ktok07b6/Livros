package livros.compiler;

public class FuncExpr extends ASTExpr
{
	public static final int AVG = 0;
	public static final int MAX = 1;
	public static final int MIN = 2;
	public static final int SUM = 3;
	public static final int COUNT = 4;

	int mFuncType;
	int mQuantifier;
	ASTExpr mExpr;

	public FuncExpr(int type, int quantifier, ASTExpr expr) {
		super();
		mFuncType = type;
		mQuantifier = quantifier;
		mExpr = expr;
	}

	public FuncExpr(int type, ASTExpr expr) {
		super();
		mFuncType = type;
		mQuantifier = -1;
		mExpr = expr;
	}

	public String toString() {
		String s = funcTypeString() + "(";
		if (mFuncType == COUNT && mExpr == null) {
			s += "*";
		} else {
			s += SetQuantifier.string(mQuantifier);
			s += mExpr.toString();
		}
		s += ")";
		return s;
	}

	String funcTypeString() {
		switch (mFuncType) {
		case AVG: return "AVG";
		case MAX: return "MAX";
		case MIN: return "MIN";
		case SUM: return "SUM";
		case COUNT: return "COUNT";
		default : return "!ERROR!";
		}
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
