package livros.btree;

public class TextKey extends Key {
	public String value;
	
	public TextKey(String v) {
		value = v;
	}

	public int compareTo(Object other) {
		TextKey rhs = (TextKey)other;
		return value.compareTo(rhs.value);
	}

	public boolean equals(Object other) {
		if (other == null) return false;
		TextKey rhs = (TextKey)other;
		return value.equals(rhs.value);
	}

	public String toString() {
		return value;
	}
}
