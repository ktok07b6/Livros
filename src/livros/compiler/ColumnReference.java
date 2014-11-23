package livros.compiler;

public class ColumnReference extends ASTExpr
{
	Identifier mTableName;
	Identifier mColumnName;
	public ColumnReference(Identifier table, Identifier column) {
		super();
		mTableName = table;
		mColumnName = column;
	}

	public String toString() {
		return (mTableName != null ? mTableName+"." : "") + mColumnName;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
