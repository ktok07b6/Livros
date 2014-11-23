package livros.compiler;

import livros.Log;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ParserTest
{
	public static void main(String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	
	public static Test suite ( ) {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitParserTest.class));
		suite.addTest(new TestSuite(JUnitTranslatorTest.class));
		return suite;
	}
}
