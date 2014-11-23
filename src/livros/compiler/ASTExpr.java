package livros.compiler;

public class ASTExpr extends AST
{
	public ASTExpr() {
		super();
	}

	private static NullExpr sNullExpr;
	private static DefaultExpr sDefaultExpr;
	private static TrueExpr sTrueExpr;
	private static FalseExpr sFalseExpr;
	private static UnknownExpr sUnknownExpr;

	public static NullExpr nullExpr() {
		if (sNullExpr == null) {
			sNullExpr = new NullExpr();
		}
		return sNullExpr;
	}

	public static DefaultExpr defaultExpr() {
		if (sDefaultExpr == null) {
			sDefaultExpr = new DefaultExpr();
		}
		return sDefaultExpr;
	}

	public static TrueExpr trueExpr() {
		if (sTrueExpr == null) {
			sTrueExpr = new TrueExpr();
		}
		return sTrueExpr;
	}

	public static FalseExpr falseExpr() {
		if (sFalseExpr == null) {
			sFalseExpr = new FalseExpr();
		}
		return sFalseExpr;
	}

	public static UnknownExpr unknownExpr() {
		if (sUnknownExpr == null) {
			sUnknownExpr = new UnknownExpr();
		}
		return sUnknownExpr;
	}

}
