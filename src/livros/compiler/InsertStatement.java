package livros.compiler;

import java.util.List;

public class InsertStatement extends Statement
{
	Identifier mTableName;
	List mColumnList; //Identifier
	QueryExpr mQuery;

	public InsertStatement(Identifier name, List columns, QueryExpr query) {
		super();
		mTableName = name;
		mColumnList = columns;
		mQuery = query;
	}

	public String toString() {
		String s = "INSERT INTO " + mTableName + " ";
		
		if (mColumnList.size() > 0) {
			s += "(";
			s += toStringList(mColumnList, ", ");
			s += ") ";
		}

		if (mQuery != null) {
			s += mQuery.toString();
		} else {
			s += "DEFAULT VALUES";
		}
		return s;

	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
