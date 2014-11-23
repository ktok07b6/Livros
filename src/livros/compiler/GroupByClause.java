package livros.compiler;

import java.util.List;

public class GroupByClause extends AST
{
	List mColumns; //ColumnReference

	public GroupByClause(List cols) {
		super();
		mColumns = cols;
	}

	public String toString() {
		String s = "GROUP BY ";
		s += toStringList(mColumns, ", ");
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
