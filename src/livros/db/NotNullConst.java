package livros.db;

public class NotNullConst extends Constraint
{
	protected NotNullConst() {
	}

	public String toString() {
		return "NOT NULL";
	}

	public boolean isNotNull() {
		return true;
	}

	public boolean verify(Table t, Field f, Value v) {
		return !v.isNull();
	}
}
