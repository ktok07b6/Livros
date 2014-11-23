package livros.db;

public class VarCharValue extends TextValue {

	public VarCharValue(int capacity, String data) {
		super(Type.varChar(capacity), data);
		prepareLength();
	}

	public VarCharValue asVarChar() {
		return this;
	}

	public boolean isVarChar() {
		return true;
	}

	private void prepareLength() {
		int capacity = type().capacity();
		if (mData.length() > capacity) {
			mData = mData.substring(0, capacity);
		}
	}

	public boolean equals(Object other) {
		return (other instanceof VarCharValue) &&
			((VarCharValue)other).mData.equals(mData);
	}

	public int hashCode() {
		return mData.hashCode();
	}
}
