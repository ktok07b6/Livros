package livros.db;

public class Constraint {

	public static final PrimaryConst primaryConst = new PrimaryConst();
	public static final NotNullConst notNullConst = new NotNullConst();
	public static final UniqueConst uniqueConst = new UniqueConst();

	public boolean isPrimary() {
		return false;
	}

	public boolean isAutoIncrement() {
		return false;
	}

	public boolean isNotNull() {
		return false;
	}

	public boolean isUnique() {
		return false;
	}

	public boolean isReference() {
		return false;
	}

	public boolean verify(Table t, Field f, Value v) {
		return false;
	}
}
