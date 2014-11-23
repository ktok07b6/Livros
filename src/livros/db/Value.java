package livros.db;

import livros.Debug;

public class Value extends Expr {
	public static final NullValue nullValue = new NullValue();
	public static final Undefined undefined = new Undefined();
	public static final BoolValue trueValue = new BoolValue(BoolValue.TRUE);
	public static final BoolValue falseValue = new BoolValue(BoolValue.FALSE);
	public static final BoolValue unknownValue = new BoolValue(BoolValue.UNKNOWN);

	Type mType;

	protected Value(Type type) {
		Debug.assertTrue(type != null);
		mType = type;
	}

	public boolean isValue() {
		return true;
	}

	public Value asValue() {
		return this;
	}

	public Object accept(ExprVisitor visitor) {
		return visitor.visit(this);
	}
	
	public Type type() {
		return mType;
	}
	
	public IntegerValue asInteger() {
		return null;
	}

	public FixedCharValue asFixedChar() {
		return null;
	}
	
	public VarCharValue asVarChar() {
		return null;
	}

	public TextValue asText() {
		return null;
	}

 	public FieldRef asFieldRef() {
		return null;
	}

 	public ValueVector asVector() {
		return null;
	}

 	public BoolValue asBool() {
		return null;
	}

	public boolean isInteger() {
		return false;
	}

	public boolean isFixedChar() {
		return false;
	}

	public boolean isVarChar() {
		return false;
	}

	public boolean isText() {
		return false;
	}

	public boolean isFieldRef() {
		return false;
	}

	public boolean isVector() {
		return false;
	}

	public boolean isBool() {
		return false;
	}

	public boolean isNull() {
		return false;
	}

	public boolean isUndefined() {
		return false;
	}

	public boolean isReference() {
		return false;
	}

}
