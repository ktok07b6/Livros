package livros.btree;

public class IntKey extends Key {
	public int value;
	
	public IntKey(int v) {
		value = v;
	}

	public int compareTo(Object other) {
		IntKey rhs = (IntKey)other;
		if (value < rhs.value) return -1;
		if (value > rhs.value) return 1;
		else return 0;
	}

	public boolean equals(Object other) {
		if (other == null) return false;
		IntKey rhs = (IntKey)other;
		return value == rhs.value;
	}

	public String toString() {
		return ""+value;
	}
}
