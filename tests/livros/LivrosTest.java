package livros;

import livros.Log;
import livros.storage.StorageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LivrosTest
{
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitLivrosTest.class));
		return suite;
	}

	public static void main(String[] args) {
		File file = new File("/sdcard");
		if (file.exists()) {
			StorageManager.DB_DIR="/sdcard/tmp/";
			StorageManager.TMP_DIR="/sdcard/tmp/";
		}
		StorageManager.DB_FILE = "livros_test.db";

		junit.textui.TestRunner.run (suite());
	}
}
