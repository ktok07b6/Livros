package livros.compiler;

import java.util.List;

public class TableDefinition extends Statement
{
	Identifier mTableName;
	List mTableElementList;

	public TableDefinition(Identifier name, List elems) {
		super();
		mTableName = name;
		mTableElementList = elems;
	}

	public String toString() {
		String s = "CREATE TABLE " + mTableName.toString();
		s += " (";
		s += toStringList(mTableElementList, ", ");
		s += ")";
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
