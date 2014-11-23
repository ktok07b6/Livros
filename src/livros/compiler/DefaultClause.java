package livros.compiler;

public class DefaultClause extends AST
{
	ASTExpr mLiteral;
	public DefaultClause(ASTExpr lit) {
		super();
		mLiteral = lit;
	}

	public String toString() {
		return "DEFAULT " + (mLiteral != null ? mLiteral.toString() : "NULL");
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
