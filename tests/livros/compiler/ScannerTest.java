package livros.compiler;

import livros.Log;
import java.util.List;

public class ScannerTest
{
	static void test1() {
		//String sql = "% () ' + * , . .. - ; : <> < = > = <= >= / ? _ || |";
		String sql = "'I\\'m not in Love - 10cc'";
		//String sql = "1 100 + 5";
		//String sql = "select name from tab where \"name\"='adam' and id <> -100";
		Scanner scanner = new Scanner(sql);
		List tokens = scanner.scan();
		
		for (int i = 0; i < tokens.size(); i++) {
			Token t = (Token)tokens.get(i);
			Log.d("token " +t.toString());
		}
		
	}

	public static void main(String[] args) {
		test1();
	}

}
