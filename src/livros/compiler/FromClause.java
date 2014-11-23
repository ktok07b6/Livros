package livros.compiler;

import java.util.List;

public class FromClause extends AST
{
	List mTables; //TableReference

	public FromClause(List tables) {
		super();
		mTables = tables;
	}

	public String toString() {
		String s = "FROM ";
		s += toStringList(mTables, ", ");
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
