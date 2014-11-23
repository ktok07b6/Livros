package livros.compiler;

public class SelectStatement extends Statement
{
	QueryExpr mQuery;
	OrderByClause mOrderBy;
	public SelectStatement(QueryExpr query, OrderByClause orderBy) {
		super();
		mQuery = query;
		mOrderBy = orderBy;
	}

	public String toString() {
		String s = mQuery.toString();
		s += mOrderBy != null ? " " + mOrderBy.toString() : "";
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
