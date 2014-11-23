package livros.db;

public class BinExpr extends Expr
{
	int mOp;
	Expr mLeft;
	Expr mRight;
	public BinExpr(int op, Expr l, Expr r) {
		super();
		mOp = op;
		mLeft = l;
		mRight = r;
	}

	public Object accept(ExprVisitor visitor) {
		return visitor.visit(this);
	}

	public String toString() {
		return "("+Operator.opName(mOp) + " " +
			mLeft.toString() + " " +
			mRight.toString()+")";
	}

	public boolean equals(Object other) {
		if (other instanceof BinExpr) {
			BinExpr otherBin = (BinExpr)other;
			return mOp == otherBin.mOp &&
				mLeft.equals(otherBin.mLeft) &&
				mRight.equals(otherBin.mRight);
		}
		return false;
	}
}
