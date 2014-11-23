package livros.btree;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BTreeTest {
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitBTreeTest.class));
		suite.addTest(new TestSuite(JUnitLeafBucketTest.class));
		suite.addTest(new TestSuite(JUnitBranchBucketTest.class));
		suite.addTest(new TestSuite(JUnitBTreeRandomTest.class));

		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run (suite());
	}

}
