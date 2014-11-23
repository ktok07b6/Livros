package livros.db;

class TextType extends Type {
	public TextType() {
	}

	public String toString() {
		return "TEXT";
	}

	public boolean equals(Object other) {
		return other instanceof TextType;
	}

	public int hashCode() {
		return 'T';
	}

	public boolean isText() {
		return true;
	}

	public boolean canCommuteTo(Type t) {
		return t.isNull() || t.isText();
	}
}
