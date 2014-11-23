package livros;

import livros.Log;
import livros.compiler.Scanner;
import java.util.regex.*;

public class RegexTest
{
	//private static final String SPECIAL_CHAR = "<>|<=|>=|\\|\\||\\.\\.|[%'\\(\\)\\+\\*,\\.-;:<>/=\\?_\\|]";
	private static final String SPECIAL_CHAR = "<>|<=|>=|\\|\\||\\.\\.|[%'\\(\\)\\+\\*,\\.\\?_\\|<>/.:;-]";
	private static final String IDENTIFIER = "[a-zA-Z][a-zA-Z0-9]*";
	private static final String CHARACTER_LITERAL = "\"[^\"]*\"";
	static void test() {
		//String regex = IDENTIFIER + "|" + SPECIAL_CHAR;
		String regex =  CHARACTER_LITERAL + "|" + IDENTIFIER/*SPECIAL_CHAR*/;
		Pattern p = Pattern.compile(regex);
		//String s = "select\tname, 0a from t1 where a = 1;";
		//String s = "% & ( ) ' + * , . .. - ; : <> < = > = <= >= / ? _ || |";
		String s = "\"0123 hello\" + \" world \"";
		Matcher m = p.matcher(s);

		//Log.d("matches " +m.matches());
		//m.reset();
		while (m.find()) {
			Log.d(s.substring(m.start(), m.end()) + " " + m.start() + " " + m.end());
			
			for (int i = 0;i<m.groupCount();i++) {
				Log.d(i + " " +m.group(i) + " " +
					  m.start(i) + " " +
					  m.end(i));
			}
			
		}
	}
	public static void main(String[] args) {
		test();
	}
}
