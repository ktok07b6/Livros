package livros.db;

public class PrimaryConst extends Constraint
{
	protected PrimaryConst() {
	}

	public String toString() {
		return "PRIMARY KEY";
	}

	public boolean isPrimary() {
		return true;
	}

	public boolean verify(Table t, Field f, Value v) {
		//TODO
		return false;//!t.uniqueSet(f).contains(v) && !v.isNull();
	}
}
