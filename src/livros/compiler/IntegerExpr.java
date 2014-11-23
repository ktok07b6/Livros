package livros.compiler;

public class IntegerExpr extends ASTExpr
{
	int mValue;
	public IntegerExpr(int i) {
		super();
		mValue = i;
	}

	public String toString() {
		return String.valueOf(mValue);
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
