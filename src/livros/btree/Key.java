package livros.btree;

import livros.db.Value;

public abstract class Key implements Comparable {
	public static Key fromValue(Value v) {
		if (v.isInteger()) {
			return new IntKey(v.asInteger().intValue());
		} else if (v.isText()) {
			return new TextKey(v.asText().textValue().trim());
		}
		return null;
	}
	public Key() {
	}
}
