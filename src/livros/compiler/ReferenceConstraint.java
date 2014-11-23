package livros.compiler;

import java.util.List;

public class ReferenceConstraint extends Constraint
{
	Identifier mTableName;
	Identifier mColumnName;
	public ReferenceConstraint(Identifier table, Identifier column) {
		super();
		mTableName = table;
		mColumnName = column;
	}

	public String toString() {
		String s = "REFERENCES ";
		s += mTableName.toString();
		s += " (";
		s += mColumnName.toString();
		s += ")";
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
