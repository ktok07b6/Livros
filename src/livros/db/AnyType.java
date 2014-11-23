package livros.db;

import livros.Debug;

class AnyType extends Type {
	public AnyType() {
	}

	public String toString() {
		return "ANY";
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		return other instanceof Type;
	}

	public int hashCode() {
		return 'A';
	}


	public boolean canCommuteTo(Type t) {
		Debug.assertTrue(false);
		return true;
	}
}
