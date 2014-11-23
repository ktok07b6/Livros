package livros.db;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.List;

public class Field {
	String mFieldName;
	Type mType;
	Value mDefaultValue;
	int mFlags;
	List mConstraints;
	boolean mIsVirtual;

	public static final int FIELD_FLAG_PRIMARY = 1 << 0;
	public static final int FIELD_FLAG_AUTO_INCREMENT = 1 << 1;
	public static final int FIELD_FLAG_NOT_NULL = 1 << 2;
	public static final int FIELD_FLAG_UNIQUE = 1 << 3;
	public static final int FIELD_FLAG_REFERENCE = 1 << 4;
	public static final int FIELD_FLAG_REFERENCED = 1 << 5;
	public static final int FIELD_FLAG_VIRTUAL = 1 << 6;
	public static final int FIELD_FLAG_ENABLE = 1 << 8;

	public Field(String name, Type type) {
		this(name, type, Value.nullValue);
	}

	public Field(String name, Type type, List constraints) {
		this(name, type, Value.nullValue, constraints);
	}

	public Field(String name, Type type, Value defValue) {
		this(name, type, defValue, new ArrayList());
	}

	public Field(String name, Type type, Value defValue, List constraints) {
		mFieldName = name;
		mType = type;
		Debug.assertTrue(defValue != null);
		mDefaultValue = defValue;
		mConstraints = constraints;
		mFlags = FIELD_FLAG_ENABLE;

		for (int i = 0; i < mConstraints.size(); i++) {
			Constraint c = (Constraint)mConstraints.get(i);
			if (c.isPrimary()) {
				mFlags |= FIELD_FLAG_PRIMARY;
			} else if (c.isAutoIncrement()) {
				mFlags |= FIELD_FLAG_AUTO_INCREMENT;
			} else if (c.isNotNull()) {
				mFlags |= FIELD_FLAG_NOT_NULL;
			} else if (c.isUnique()) {
				mFlags |= FIELD_FLAG_UNIQUE;
			} else if (c.isReference()) {
				mFlags |= FIELD_FLAG_REFERENCE;
			}
		}
	}

	public Field(Field f) {
		mFieldName = f.mFieldName;
		mType = f.mType;	
		mDefaultValue = f.mDefaultValue;
		mConstraints = f.mConstraints;
		mFlags = f.mFlags;
	}

	public void setName(String name) {
		mFieldName = name;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		//sb.append(super.toString());
		sb.append("@"+mFieldName + " ");
		sb.append(mType.toString() + " ");
		sb.append(getConstraintString());
		return sb.toString().trim();
	}

	public String getConstraintString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mConstraints.size(); i++) {
			Constraint c = (Constraint)mConstraints.get(i);
			sb.append(c.toString());
			if (i+1 < mConstraints.size()) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public String name() {
		return mFieldName;
	}

	public Type type() {
		return mType;
	}

	public void setDefaultValue(Value v) throws InvalidTypeException {
		if (!verify(v)) {
			throw new InvalidTypeException(mType, v.type());
		}
		mDefaultValue = v;
	}

	public Value defaultValue() {
		return mDefaultValue;
	}

	public List constraints() {
		return mConstraints;
	}

	public boolean isPrimary() {
		return (mFlags & FIELD_FLAG_PRIMARY) != 0;
	}

	public boolean isAutoIncrement() {
		return (mFlags & FIELD_FLAG_AUTO_INCREMENT) != 0;
	}

	public boolean isNotNull() {
		return (mFlags & FIELD_FLAG_NOT_NULL) != 0;
	}

	public boolean isUnique() {
		return (mFlags & FIELD_FLAG_UNIQUE) != 0;
	}

	public boolean isReference() {
		return (mFlags & FIELD_FLAG_REFERENCE) != 0;
	}

	public RefConst refConst() {
		if (!isReference()) {
			return null;
		}
		for (int i = 0; i < mConstraints.size(); i++) {
			Constraint c = (Constraint)mConstraints.get(i);
			if (c.isReference()) {
				return (RefConst)c;
			}
		}
		return null;
	}

	public void setReferencedField() {
		mFlags |= FIELD_FLAG_REFERENCED;
	}

	public boolean isReferenced() {
		return (mFlags & FIELD_FLAG_REFERENCED) != 0;
	}

	public boolean verify(Value v) {
		return mType.equals(v.type());
	}

	public int columnWidth() {
		if (mType.isInteger()) {
			return Math.max(6, mFieldName.length());
		} else if (mType.isText()) {
			return Math.max(mType.capacity(), mFieldName.length());
		} else {
			return mFieldName.length();
		}
	}
	/*
	public boolean isEnabled() {
		return (mFlags & FIELD_FLAG_ENABLE) != 0;
	}

	public void setEnable(boolean b) {
		mFlags = b ? (mFlags|FIELD_FLAG_ENABLE) : (mFlags&~FIELD_FLAG_ENABLE);
	}
	*/
}
