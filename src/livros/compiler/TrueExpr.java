package livros.compiler;

public class TrueExpr extends ASTExpr
{
	public TrueExpr() {
		super();
	}

	public String toString() {
		return "TRUE";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
