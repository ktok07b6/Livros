package livros.compiler;

public class DeleteStatement extends Statement
{
	Identifier mTableName;
	ASTExpr mSearchCond;
	public DeleteStatement(Identifier name, ASTExpr cond) {
		super();
		mTableName = name;
		mSearchCond = cond;
	}

	public String toString() {
		String s = "DELETE FROM " + mTableName + " WHERE ";
		s += mSearchCond.toString();
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
