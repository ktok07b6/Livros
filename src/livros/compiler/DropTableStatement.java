package livros.compiler;

public class DropTableStatement extends Statement
{
	public static final int CASCADE  = 0;
	public static final int RESTRICT = 1;

	Identifier mTableName;
	int mBehaviour;

	public DropTableStatement(Identifier name, int behaviour) {
		super();
		mTableName = name;
		mBehaviour = behaviour;
	}

	public String toString() {
		String s = "DROP TABLE " + mTableName;
		//s += mBehaviour == CASCADE ? " CASCADE" : " RESTRICT";
		return s;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
