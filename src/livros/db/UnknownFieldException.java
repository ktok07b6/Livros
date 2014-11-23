package livros.db;

public class UnknownFieldException extends Exception {
	String mField;
	public UnknownFieldException(String field) {
		mField = field;
	}
}