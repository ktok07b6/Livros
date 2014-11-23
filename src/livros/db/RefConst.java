package livros.db;

public class RefConst extends Constraint
{
	String mTableName;
	String mFieldName;
	public RefConst(String table, String field) {
		mTableName = table;
		mFieldName = field;
	}

	public String table() {
		return mTableName;
	}

	public String field() {
		return mFieldName;
	}

	public String toString() {
		return "REFERENCES " + mTableName + "("+mFieldName+")";
	}

	public boolean isReference() {
		return true;
	}
}
