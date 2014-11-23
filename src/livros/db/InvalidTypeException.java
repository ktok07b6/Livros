package livros.db;

class InvalidTypeException extends Exception
{
	public InvalidTypeException(Type a, Type b) {
		super(a + " is not " + b);
	}
}
