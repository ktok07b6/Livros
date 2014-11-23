package livros.db;

import livros.Debug;

class NullType extends Type {
	public NullType() {
	}

	public String toString() {
		return "NULL";
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		return other instanceof Type;
	}

	public int hashCode() {
		return 'N';
	}

	public boolean isNull() {
		return true;
	}

	public boolean canCommuteTo(Type t) {
		Debug.assertTrue(false);
		return true;
	}
}
