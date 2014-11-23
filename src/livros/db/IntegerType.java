package livros.db;

class IntegerType extends Type {
	public IntegerType() {
	}

	public String toString() {
		return "INTEGER";
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof AnyType)
			return true;
		return other instanceof IntegerType;
	}

	public int hashCode() {
		return 'I';
	}

	public boolean isInteger() {
		return true;
	}

	public boolean canCommuteTo(Type other) {
		if (other == null)
			return false;
		if (other instanceof AnyType)
			return true;
		if (other instanceof NullType)
			return true;
		return (other instanceof IntegerType);
	}
}
