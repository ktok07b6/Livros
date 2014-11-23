package livros.compiler;

public class LikePredicate extends ASTExpr
{
	ASTExpr mMatch;
	ASTExpr mPattern;
	boolean mIsNot;

	public LikePredicate(ASTExpr match, ASTExpr pattern, boolean isNot) {
		super();
		mMatch = match;
		mPattern = pattern;
		mIsNot = isNot;
	}

	public String toString() {
		String s = mMatch.toString();
		s += mIsNot ? " NOT" : "";
		s += " LIKE ";
		s += mPattern.toString();
		return s;

	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
