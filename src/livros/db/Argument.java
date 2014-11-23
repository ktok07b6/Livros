package livros.db;

public class Argument extends Value {

	UserFunction mFunc;
	int mArgn;
	
	public Argument(UserFunction func, int argn) {
		super(Type.anyType);
		mFunc = func;
		mArgn = argn;
	}

	public IntegerValue asInteger() {
		return mFunc.arg(mArgn).asInteger();
	}

	public FixedCharValue asFixedChar() {
		return mFunc.arg(mArgn).asFixedChar();
	}

	public VarCharValue asVarChar() {
		return mFunc.arg(mArgn).asVarChar();
	}

	public TextValue asText() {
		return mFunc.arg(mArgn).asText();
	}

 	public FieldRef asFieldRef() {
		return mFunc.arg(mArgn).asFieldRef();
	}

 	public ValueVector asVector() {
		return mFunc.arg(mArgn).asVector();
	}

	public boolean isInteger() {
		return mFunc.arg(mArgn).isInteger();
	}

	public boolean isFixedChar() {
		return mFunc.arg(mArgn).isFixedChar();
	}

	public boolean isVarChar() {
		return mFunc.arg(mArgn).isVarChar();
	}

	public boolean isText() {
		return mFunc.arg(mArgn).isText();
	}

	public boolean isFieldRef() {
		return mFunc.arg(mArgn).isFieldRef();
	}

	public boolean isVector() {
		return mFunc.arg(mArgn).isVector();
	}

	public boolean isBool() {
		return mFunc.arg(mArgn).isBool();
	}

	public boolean isNull() {
		return mFunc.arg(mArgn).isNull();
	}

	public boolean isReference() {
		return mFunc.arg(mArgn).isReference();
	}
}
