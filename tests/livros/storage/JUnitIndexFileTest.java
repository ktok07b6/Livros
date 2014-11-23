package livros.storage;

import livros.Console;
import livros.Debug;
import livros.LogConsole;
import livros.Livros;
import livros.Log;
import livros.db.Constraint;
import livros.db.DataBase;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.FixedCharValue;
import livros.db.VarCharValue;
import livros.db.IntegerValue;
import livros.db.Record;
import livros.db.RecordIndex;
import livros.db.RefConst;
import livros.db.Table;
import livros.db.TextValue;
import livros.db.Type;
import livros.db.Value;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitIndexFileTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TMP = StorageManager.TMP_FILE_PREFIX+"0";

	public JUnitIndexFileTest(String name) {
		super(name);
	}

	public void testWriteRead() {
		try {
			IndexFile ifile = new IndexFile(TMP);

			ifile.writeHeader(100);
			//ifile.dump(0);
			for (int i = 0; i < 100; i++) {
				List indices = new ArrayList();
				for (int j = 0; j < 10; j++, i++) {
					RecordIndex ri = new RecordIndex(i, i*2, i*3);
					indices.add(ri);
				}
				ifile.writeIndices(indices);
			}

			boolean ret = ifile.readHeader();
			assertTrue(ret);

			assertEquals(100, ifile.recordCount());

			for (int i = 0; i < 100; i++) {
				List indices = new ArrayList();
				ifile.readIndices(10, indices);
				for (int j = 0; j < 10; j++, i++) {
					RecordIndex ri = (RecordIndex)indices.get(j);
					assertTrue(ri.recid == i);
					assertTrue(ri.index == i*2);
					assertTrue(ri.chunkid == i*3);
				}
			}
			ifile.close();
			ifile.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

}