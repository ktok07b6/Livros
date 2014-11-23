package livros.db;

import livros.Debug;

public class VarCharType extends TextType {
	int mCapacity;
	public VarCharType(int capacity) {
		mCapacity = capacity;
		Debug.assertTrue(capacity <= 256);
	}

	public String toString() {
		return "VARCHAR("+mCapacity+")";
	}

	public int capacity() {
		return mCapacity;
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof AnyType)
			return true;
		return (other instanceof VarCharType) && 
			((VarCharType)other).capacity() == mCapacity;
	}

	public int hashCode() {
		return 'V';
	}

	public boolean isVarChar() {
		return true;
	}
}
