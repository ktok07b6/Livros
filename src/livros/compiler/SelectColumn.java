package livros.compiler;

public class SelectColumn extends AST
{
	Identifier mTableName;
	ASTExpr mColumn;
	Identifier mAsName;
	public SelectColumn(Identifier table) {
		super();
		mTableName = table;
	}

	public SelectColumn(ASTExpr column, Identifier asName) {
 		super();
		mColumn = column;
		mAsName = asName;
	}

	public String toString() {
		if (mTableName != null) {
			return mTableName.toString() + ".*";
		} else {
			return mColumn.toString() + (mAsName != null ? " AS " + mAsName.toString() : "");
		}
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
