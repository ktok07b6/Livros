package livros.compiler;

public class UniqueConstraint extends Constraint
{
	boolean mPrimaryKey;
	public UniqueConstraint(boolean pkey) {
		super();
		mPrimaryKey = pkey;
	}

	public String toString() {
		return mPrimaryKey ? "PRIMARY KEY" : "UNIQUE";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
