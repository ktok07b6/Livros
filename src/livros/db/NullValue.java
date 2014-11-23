package livros.db;

public class NullValue extends Value {
	public NullValue() {
		super(Type.nullType);
	}

	public boolean isNull() {
		return true;
	}

	public BoolValue asBool() {
		return Value.falseValue;
	}

	public String toString() {
		return "NULL";
	}
}
