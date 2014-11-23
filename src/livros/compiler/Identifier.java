package livros.compiler;

public class Identifier extends AST
{
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_NAME_REF = 1;
	public static final int TABLE_NAME = 2;
	public static final int COLUMN_ALIAS_DEF = 3;
	public static final int TABLE_ALIAS_DEF = 4;

	int mKind;
	String mIdent;
	public Identifier(int kind, String ident) {
		super();
		mKind = kind;
		mIdent = ident;
	}

	public String toString() {
		return mIdent;
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
