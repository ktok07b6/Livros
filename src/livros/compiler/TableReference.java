package livros.compiler;

import livros.Debug;

public class TableReference extends AST
{
	Identifier mTableName;
	QueryExpr mQuery;
	CorrelationSpecification mCorrelation;

	public TableReference(Identifier name, CorrelationSpecification correlation) {
		super();
		mTableName = name;
		mCorrelation = correlation;
	}

	public TableReference(QueryExpr query, CorrelationSpecification correlation) {
		super();
		mQuery = query;
		mCorrelation = correlation;
	}

	public String toString() {
		if (mTableName != null) {
			String s = mTableName.toString();
			if (mCorrelation != null) {
				s += " " + mCorrelation.toString();
			}
			return s;
		} else {
			Debug.assertTrue(mCorrelation != null);
			String s = mQuery.toString();
			s += " " + mCorrelation.toString();
			return s;
		}
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}

