package livros.db;

import livros.Log;
import java.util.List;

public class ValueVector extends Value
{
	List mValues;
	public ValueVector(Type t, List values) {
		super(t);
		mValues = values;
	}

	public List values() {
		return mValues;
	}

	public ValueVector asVector() {
		return this;
	}

	public boolean isVector() {
		return true;
	}
}
