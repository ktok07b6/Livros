package livros.db;

import livros.Debug;
import java.util.Arrays;

public class TextValue extends Value {
	protected String mData;
 	public TextValue(String data) {
		super(Type.textType);
		mData = data;
		Debug.assertTrue(!mData.substring(0,1).equals("'"));
	}

 	public TextValue(Type t, String data) {
		super(t);
		mData = data;
		Debug.assertTrue(!mData.substring(0,1).equals("'"));
	}

	public TextValue asText() {
		return this;
	}

	public BoolValue asBool() {
		return Value.trueValue;
	}

	public boolean isText() {
		return true;
	}

	public String toString() {
		return "'"+mData+"'";
	}

	public String textValue() {
		return mData;
	}

	public boolean equals(Object other) {
		return (other instanceof TextValue) &&
			((TextValue)other).mData.equals(mData);
	}

	public int hashCode() {
		return mData.hashCode();
	}
}
