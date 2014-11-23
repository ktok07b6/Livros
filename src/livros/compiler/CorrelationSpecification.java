package livros.compiler;

import java.util.List;

public class CorrelationSpecification extends AST
{
	Identifier mCorrelationName;
	List mColumnNameList; //Identifier
	public CorrelationSpecification(Identifier name, List columns) {
		super();
		mCorrelationName = name;
		mColumnNameList = columns;
	}

	public String toString() {
		String s = "AS " + mCorrelationName;
		if (mColumnNameList != null) {
			s += "(";
			s += toStringList(mColumnNameList, ", ");
			s += ")";
		}
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}

