package livros.compiler;

import java.util.List;

public class TableElement extends AST
{
	Identifier mColumnName;
	DataType mDataType;
	DefaultClause mDefault;
	List mConstraints;

	public TableElement(Identifier name, DataType dtype, DefaultClause def, List cons) {
		super();
		mColumnName = name;
		mDataType = dtype;
		mDefault = def;
		mConstraints = cons;
	}

	public String toString() {
		String s = mColumnName.toString() + " " +
			mDataType.toString() + 
			(mDefault != null ? " " + mDefault.toString() : "");
		if (mConstraints.size() != 0) {
			s += " ";
			s += toStringList(mConstraints, " ");
		}
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
