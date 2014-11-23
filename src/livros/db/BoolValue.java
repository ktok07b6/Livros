package livros.db;

public class BoolValue extends Value {
	public static final int UNKNOWN = -1;
	public static final int FALSE = 0;
	public static final int TRUE = 1;
	int mState;
	public BoolValue(int state) {
		super(Type.boolType);
		mState = state;
	}

	public String toString() {
		switch (mState) {
		case UNKNOWN: return "unknown";
		case FALSE: return "false";
		case TRUE: return "true";
		}
		return "";
	}

	public BoolValue asBool() {
		return this;
	}

	public boolean isBool() {
		return true;
	}

	public boolean isTrue() {
		return mState == TRUE;
	}

	public boolean isFalse() {
		return mState == FALSE;
	}

	public boolean isUnknown() {
		return mState == UNKNOWN;
	}
}
