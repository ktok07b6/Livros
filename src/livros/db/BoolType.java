package livros.db;

class BoolType extends Type {
	public BoolType() {
	}

	public String toString() {
		return "BOOL";
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof AnyType)
			return true;
		return other instanceof BoolType;
	}

	public int hashCode() {
		return 'B';
	}

	public boolean canCommuteTo(Type t) {
		return equals(t);
	}
}
