package livros.db;

import livros.Debug;
import livros.Log;
import java.util.Arrays;

public class FixedCharValue extends TextValue {

	public FixedCharValue(int capacity, String data) {
		super(Type.fixedChar(capacity), data);
		prepareLength();
	}

	public FixedCharValue asFixedChar() {
		return this;
	}

	public boolean isFixedChar() {
		return true;
	}

	private void prepareLength() {
		int capacity = type().capacity();
		if (mData.length() > capacity) {
			mData = mData.substring(0, capacity);
		} else if (mData.length() < capacity) {
			char spc[] = new char[capacity - mData.length()];
			Arrays.fill(spc, ' ');
			mData += new String(spc);
		}
		Debug.assertTrue(mData.length() == capacity);
	}

	public boolean equals(Object other) {
		return (other instanceof FixedCharValue) &&
			((FixedCharValue)other).mData.equals(mData);
	}

	public int hashCode() {
		return mData.hashCode();
	}
}
