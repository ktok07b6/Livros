package livros.db;

import java.util.Map;
import java.util.HashMap;

public class Type {
	public static final IntegerType integerType = new IntegerType();
	public static final TextType textType = new TextType();
	public static final AnyType anyType = new AnyType();
	public static final NullType nullType = new NullType();
	public static final BoolType boolType = new BoolType();

	static HashMap mFixedCharMap = new HashMap();
	static HashMap mVarCharMap = new HashMap();

	public static FixedCharType fixedChar(int capacity) {
		FixedCharType t = (FixedCharType)mFixedCharMap.get(new Integer(capacity));
		if (t == null) {
			t = new FixedCharType(capacity);
			mFixedCharMap.put(new Integer(capacity), t);
		}
		return t;
	}

	public static VarCharType varChar(int capacity) {
		VarCharType t = (VarCharType)mVarCharMap.get(new Integer(capacity));
		if (t == null) {
			t = new VarCharType(capacity);
			mVarCharMap.put(new Integer(capacity), t);
		}
		return t;
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

	public boolean isNull() {
		return false;
	}

	public int capacity() {
		return 0;
	}

	public boolean canCommuteTo(Type t) {
		return false;
	}
}
