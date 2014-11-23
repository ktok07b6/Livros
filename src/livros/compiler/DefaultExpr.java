package livros.compiler;

public class DefaultExpr extends ASTExpr
{
	public DefaultExpr() {
		super();
	}

	public String toString() {
		return "DEFAULT";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
