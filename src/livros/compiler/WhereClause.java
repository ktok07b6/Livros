package livros.compiler;

public class WhereClause extends AST
{
	ASTExpr mSearchCond;
	public WhereClause(ASTExpr cond) {
		super();
		mSearchCond = cond;
	}

	public String toString() {
		return "WHERE " + mSearchCond.toString();
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
