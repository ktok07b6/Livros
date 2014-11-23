package livros.vm;

import livros.Log;
import livros.storage.StorageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestSuite;

public class VMTest
{
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitVMTest.class));
		return suite;
	}

	public static void main(String[] args) {
		//Properties props = System.getProperties();
		//props.list(System.out);
		File file = new File("/sdcard");
		if (file.exists()) {
			StorageManager.DB_DIR="/sdcard/tmp/";
			StorageManager.TMP_DIR="/sdcard/tmp/";
		}
		StorageManager.DB_FILE = "vm_test.db";

		junit.textui.TestRunner.run (suite());
	}
}
