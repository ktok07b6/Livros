package livros.compiler;

public class FalseExpr extends ASTExpr
{
	public FalseExpr() {
		super();
	}

	public String toString() {
		return "FALSE";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
