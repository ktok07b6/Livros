package livros.compiler;

public class SetClause extends AST
{
	Identifier mColumnName;
	ASTExpr mSource;
	public SetClause(Identifier column, ASTExpr src) {
		super();
		mColumnName = column;
		mSource = src;
	}

	public String toString() {
		return mColumnName.toString() + " = " + mSource.toString();
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
