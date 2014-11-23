package livros.compiler;

import java.util.List;

public class QuerySpecification extends QueryExpr
{
	int mQuantifier = -1;
	boolean mSelectAll;
	List mColumns; //SelectColumn
	FromClause mFrom;
	WhereClause mWhere;
	GroupByClause mGroupBy;

	public QuerySpecification(int quantifier, FromClause from, WhereClause where, GroupByClause group) {
		super();
		mQuantifier = quantifier;
		mSelectAll = true;
		mFrom = from;
		mWhere = where;
		mGroupBy = group;
	}

	public QuerySpecification(int quantifier, List columns, FromClause from, WhereClause where, GroupByClause group) {
		super();
		mQuantifier = quantifier;
		mSelectAll = false;
		mColumns = columns;
		mFrom = from;
		mWhere = where;
		mGroupBy = group;
	}

	public boolean isQuerySpec() {
		return true;
	}

	public String toString() {
		String s = "SELECT ";

		s += SetQuantifier.string(mQuantifier);

		if (mSelectAll) {
			s += "*";
		} else {
			s += toStringList(mColumns, ", ");
		}
		s += " " + mFrom.toString();
		if (mWhere != null) {
			s += " " + mWhere.toString();
		}
		if (mGroupBy != null) {
			s += " " + mGroupBy.toString();
		}
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
