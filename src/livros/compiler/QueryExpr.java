package livros.compiler;

public class QueryExpr extends ASTExpr
{
	public QueryExpr() {
		super();
	}

	public boolean isValueCtor() {
		return false;
	}

	public boolean isQuerySpec() {
		return false;
	}
}
