package livros.compiler;

public class DataType extends AST
{
	public static final int INTEGER_TYPE = 0;
	public static final int CHAR_TYPE = 1;
	public static final int VARCHAR_TYPE = 2;

	public static final int DEFAULT_CHAR_LENGTH = 32;

	int mType;
	int mLength;
	public DataType(int type, int length) {
		super();
		mType = type;
		mLength = length;
	}

	public String toString() {
		switch (mType) {
		case INTEGER_TYPE:
			return "INTEGER";
		case CHAR_TYPE:
			return "CHAR("+mLength+")";
		case VARCHAR_TYPE:
			return "VARCHAR("+mLength+")";
		default:
			return "";
		}
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
