package livros.db;

public class Undefined extends Value {
	public Undefined() {
		super(Type.anyType);
	}

	public boolean isUndefined() {
		return true;
	}
}
