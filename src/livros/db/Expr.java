package livros.db;

public class Expr
{
	public Object accept(ExprVisitor visitor) {
		return null;
	}

	public boolean isValue() {
		return false;
	}

	public Value asValue() {
		return null;
	}

}
