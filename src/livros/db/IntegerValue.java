package livros.db;

public class IntegerValue extends Value {
	int mValue;

	public IntegerValue(int v) {
		super(Type.integerType);
		mValue = v;
	}

	public IntegerValue asInteger() {
		return this;
	}

	public BoolValue asBool() {
		return mValue != 0 ? Value.trueValue : Value.falseValue;
	}

	public boolean isInteger() {
		return true;
	}

	public int intValue() {
		return mValue;
	}

	public String toString() {
		return ""+mValue;
	}

	public boolean equals(Object other) {
		return (other instanceof IntegerValue) &&
			((IntegerValue)other).intValue() == mValue;
	}

	public int hashCode() {
		return mValue;
	}
}
