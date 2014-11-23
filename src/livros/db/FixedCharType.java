package livros.db;

import livros.Debug;

public class FixedCharType extends TextType {
	int mCapacity;
	public FixedCharType(int capacity) {
		super();
		mCapacity = capacity;
		Debug.assertTrue(capacity <= 256);
	}

	public String toString() {
		return "CHAR("+mCapacity+")";
	}

	public int capacity() {
		return mCapacity;
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof AnyType)
			return true;
		if (other instanceof NullType)
			return true;
		return (other instanceof FixedCharType) && 
			((FixedCharType)other).capacity() == mCapacity;
	}

	public int hashCode() {
		return 'F';
	}

	public boolean isFixedChar() {
		return true;
	}

}
