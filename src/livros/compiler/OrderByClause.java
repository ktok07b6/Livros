package livros.compiler;

import java.util.List;

public class OrderByClause extends AST
{
	List mSortSpec; //SortSpecification

	public OrderByClause(List sortSpec) {
		super();
		mSortSpec = sortSpec;
	}

	public String toString() {
		String s = "ORDER BY ";
		//TODO:
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
