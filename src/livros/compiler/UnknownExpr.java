package livros.compiler;

public class UnknownExpr extends ASTExpr
{
	public UnknownExpr() {
		super();
	}

	public String toString() {
		return "UNKNOWN";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
