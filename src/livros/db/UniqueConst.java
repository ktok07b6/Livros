package livros.db;

public class UniqueConst extends Constraint
{
	protected UniqueConst() {
	}

	public String toString() {
		return "UNIQUE";
	}

	public boolean isUnique() {
		return true;
	}

	public boolean verify(Table t, Field f, Value v) {
		//TODO
		return false;//!t.uniqueSet(f).contains(v);
	}
}
