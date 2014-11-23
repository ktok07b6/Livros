package livros.compiler;

public class StringExpr extends ASTExpr
{
	String mValue;
	public StringExpr(String s) {
		super();
		mValue = s;
	}

	public String toString() {
		return "'"+mValue+"'";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
