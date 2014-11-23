package livros;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UtilTest
{
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitSortedListTest.class));
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run (suite());
	}
}
