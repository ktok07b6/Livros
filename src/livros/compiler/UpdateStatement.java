package livros.compiler;

import java.util.List;

public class UpdateStatement extends Statement
{
	Identifier mTableName;
	List mSetClauseList; //SetClause
	ASTExpr mSearchCond;

	public UpdateStatement(Identifier name, List setClauseList, ASTExpr cond) {
		super();
		mTableName = name;
		mSetClauseList = setClauseList;
		mSearchCond = cond;
	}

	public String toString() {
		String s = "UPDATE " + mTableName + " SET ";
		s += toStringList(mSetClauseList, ", ");
		s += " WHERE " + mSearchCond.toString();
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
