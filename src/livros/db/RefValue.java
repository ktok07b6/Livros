package livros.db;

import livros.Livros;

public class RefValue extends Value {
	Value mRef;
	int mRefId;

	public RefValue(Value v, int refid) {
		super(v.type());
		mRef = v;
		mRefId = refid;
	}

	public boolean equals(Object other) {
		if (other instanceof RefValue) {
			RefValue ref = (RefValue)other;
			return mRef.equals(ref.mRef);
		} else {
			return mRef.equals(other);
		}
	}
	public int refId() {
		return mRefId;
	}

	public String toString() {
		if (Livros.DEBUG) {
			return "(R)"+mRef.toString();
		} else {
			return mRef.toString();
		}
	}

	public Type type() {
		return mRef.type();
	}
	
	public IntegerValue asInteger() {
		return mRef.asInteger();
	}

	public FixedCharValue asFixedChar() {
		return mRef.asFixedChar();
	}

	public VarCharValue asVarChar() {
		return mRef.asVarChar();
	}

	public TextValue asText() {
		return mRef.asText();
	}

 	public FieldRef asFieldRef() {
		return mRef.asFieldRef();
	}

 	public ValueVector asVector() {
		return mRef.asVector();
	}

 	public BoolValue asBool() {
		return mRef.asBool();
	}

	public boolean isInteger() {
		return mRef.isInteger();
	}

	public boolean isFixedChar() {
		return mRef.isFixedChar();
	}

	public boolean isVarChar() {
		return mRef.isVarChar();
	}

	public boolean isText() {
		return mRef.isText();
	}

	public boolean isFieldRef() {
		return mRef.isFieldRef();
	}

	public boolean isVector() {
		return mRef.isVector();
	}

	public boolean isBool() {
		return mRef.isBool();
	}

	public boolean isNull() {
		return mRef.isNull();
	}

	public boolean isUndefined() {
		return mRef.isUndefined();
	}

	public boolean isReference() {
		return true;
	}
}
