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

public class JUnitRecordChunkTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitRecordChunkTest(String name) {
		super(name);
	}

	public void setUp() {
		File file = new File(StorageManager.DB_DIR+TEST_TABLE1);
		if (file.exists()) {
			file.delete();
		}
		file = new File(StorageManager.DB_DIR+TEST_TABLE2);
		if (file.exists()) {
			file.delete();
		}
		file = new File(StorageManager.DB_DIR+TEST_DB);
		if (file.exists()) {
			file.delete();
		}

		make();
	}

	DataBase mDataBase;
	Table mTable1;
	Table mTable2;

	private void make() {
		mTable1 = new Table(TEST_DB, TEST_TABLE1);
		mTable2 = new Table(TEST_DB, TEST_TABLE2);
		mDataBase = new DataBase(TEST_DB);
		mDataBase.addTable(mTable1);
		mDataBase.addTable(mTable2);

		try {
			//table 1
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				Field idField = new Field("id", Type.integerType, consts);
				mTable1.addField(idField);
			}
			{
				List consts = new ArrayList();
				consts.add(Constraint.notNullConst);
				consts.add(Constraint.uniqueConst);
				Field nameField = new Field("name", Type.fixedChar(15), consts);
				nameField.setDefaultValue(new FixedCharValue(15, "foo"));
				mTable1.addField(nameField);
			}
			{
				Field jobField = new Field("job", Type.varChar(20));
				jobField.setDefaultValue(new VarCharValue(20, "bar"));
				mTable1.addField(jobField);
			}

			//table 2
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				consts.add(new RefConst(TEST_TABLE1, "id"));
				Field idField = new Field("id", Type.integerType, consts);
				mTable2.addField(idField);
			}

		} catch (Exception ex) {
		}
	}

	public void testWriteReadRecord() {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutput dout = new DataOutputStream(buf);

		Record r = mTable1.createRecord(1);
		r.set("id", new IntegerValue(0));
		r.set("name", new FixedCharValue(15, "Caetano Veloso"));
		r.set("job", new VarCharValue(20, "Singer"));
		mTable1.field("id").setReferencedField();
		r.incReference("id");
		r.incReference("id");

		try {
			/*
			  record
			  4: record size
			  4: rec-id
			  4*num of field: reference counts
			  *: record body
			  
			  */
			RecordConverter.writeRecord(dout, r);
			byte[] bytes = buf.toByteArray();
			assertEquals(4+4+4+(1+4)+(1+1+15)+(1+1+1+6), bytes.length);

			Debug.hexDump(bytes);
			ByteArrayInputStream inbuf = new ByteArrayInputStream(bytes);
			DataInput din = new DataInputStream(inbuf);
			
			Record rr = RecordConverter.readRecord(din, mTable1);
			assertEquals(new IntegerValue(0), rr.get("id"));
			assertEquals(new FixedCharValue(15, "Caetano Veloso"), rr.get("name"));
			assertEquals(new VarCharValue(20, "Singer"), rr.get("job"));
			assertEquals(2, rr.refCount("id"), 2);
		} catch (Exception ex) {
			assertTrue(false);
		}
	}

	public void testWriteReadChunk() {
	}
}
