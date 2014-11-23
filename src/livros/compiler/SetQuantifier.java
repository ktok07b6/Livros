package livros.compiler;

public class SetQuantifier {
	public static final int DISTINCT = 0;
	public static final int ALL = 1;

	public static String string(int q) {
		return q == 0 ?	"DISTINCT " : (q == 1 ? "ALL " : "");
	}
}
