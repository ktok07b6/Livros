package livros.compiler;

import java.util.List;

public class ValueConstructor extends QueryExpr
{
	List mRowValueList; //ASTExpr (row value ctor)

	public ValueConstructor(List vals) {
		super();
		mRowValueList = vals;
	}

	public boolean isValueCtor() {
		return true;
	}

	public String toString() {
		String s = "VALUES (";
		s += toStringList(mRowValueList, ", ");
		s += ")";
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
