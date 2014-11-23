package livros.db;

public class FieldRef extends Value {
	String mTableName;
	String mFieldName;

	public FieldRef(String name) {
		super(Type.anyType);
		mFieldName = name;
	}

	public FieldRef(String table, String name) {
		super(Type.anyType);
		mTableName = table;
		mFieldName = name;
	}

	public boolean equals(Object other) {
		if (other instanceof FieldRef) {
			FieldRef ref = (FieldRef)other;
			if (mTableName != null) {
				return mTableName.equals(ref.tableName()) &&
					mFieldName.equals(ref.fieldName());
			} else {
				return ref.tableName() == null && 
					mFieldName.equals(ref.fieldName());
			}
		}
		return false;
	}

	public String toString() {
		return "@"+mFieldName;
	}

	public String fieldName() {
		return mFieldName;
	}

	public String tableName() {
		return mTableName;
	}

	public FieldRef asFieldRef() {
		return this;
	}

	public boolean isFieldRef() {
		return true;
	}
}
