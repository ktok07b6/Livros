package livros.db;

import livros.storage.StorageManager;
import java.io.File;
import junit.framework.Test;
import junit.framework.TestSuite;

public class DbTest
{
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitTableTest.class));
		suite.addTest(new TestSuite(JUnitExprTest.class));
		suite.addTest(new TestSuite(JUnitSelectorTest.class));
		return suite;
	}

	public static void main(String[] args) {
		File file = new File("/sdcard");
		if (file.exists()) {
			StorageManager.DB_DIR="/sdcard/tmp/";
			StorageManager.TMP_DIR="/sdcard/tmp/";
		}
		StorageManager.DB_FILE = "db_test.db";
		junit.textui.TestRunner.run (suite());
	}
}
