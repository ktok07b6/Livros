package livros.db;

public class UnExpr extends Expr
{
	int mOp;
	Expr mExpr;
	public UnExpr(int op, Expr e) {
		super();
		mOp = op;
		mExpr = e;
	}

	public Object accept(ExprVisitor visitor) {
		return visitor.visit(this);
	}

	public String toString() {
		return "("+Operator.opName(mOp) + " " +
			mExpr.toString() + ")";
	}

	public boolean equals(Object other) {
		if (other instanceof UnExpr) {
			UnExpr otherUn = (UnExpr)other;
			return mOp == otherUn.mOp &&
				mExpr.equals(otherUn.mExpr);
		}
		return false;
	}
}
