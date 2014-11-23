package livros.compiler;

public class NullExpr extends ASTExpr
{
	public NullExpr() {
		super();
	}

	public String toString() {
		return "NULL";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
