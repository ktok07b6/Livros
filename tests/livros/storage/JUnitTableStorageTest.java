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

public class JUnitTableStorageTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitTableStorageTest(String name) {
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

	DbFile mDbFile;
	DataBase mDataBase;
	Table mTable1;
	Table mTable2;

	private void make() {
		mDbFile = new DbFile(StorageManager.DB_DIR, TEST_DB);
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

	public void testInsert() {
		TableStorage ts = new TableStorage(mTable1);
		boolean ret = ts.init();
		assertTrue(ret);

 		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "a"));
		r1.set("job", new VarCharValue(20, "a"));

 		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "b"));
		r2.set("job", new VarCharValue(20, "b"));

 		Record r3 = mTable1.createRecord(3);
		r3.set("id", new IntegerValue(0x3));
		r3.set("name", new FixedCharValue(15, "c"));
		r3.set("job", new VarCharValue(20, "c"));

 		Record r4 = mTable1.createRecord(4);
		r4.set("id", new IntegerValue(0x4));
		r4.set("name", new FixedCharValue(15, "d"));
		r4.set("job", new VarCharValue(20, "d"));

 		Record r5 = mTable1.createRecord(5);
		r5.set("id", new IntegerValue(0x5));
		r5.set("name", new FixedCharValue(15, "e"));
		r5.set("job", new VarCharValue(20, "e"));
		
		ts.insert(r1);
		ts.insert(r2);
		ts.insert(r3);
		ts.insert(r4);
		ts.insert(r5);

		assertEquals(r1, ts.nextRecord(null));
		assertEquals(r2, ts.nextRecord(r1));
		assertEquals(r3, ts.nextRecord(r2));
		assertEquals(r4, ts.nextRecord(r3));
		assertEquals(r5, ts.nextRecord(r4));
		assertEquals(null, ts.nextRecord(r5));

		assertEquals(r1, ts.nextRecord(null));
		assertEquals(r2, ts.nextRecord(r1));
		assertEquals(r3, ts.nextRecord(r2));
		assertEquals(r4, ts.nextRecord(r3));
		assertEquals(r5, ts.nextRecord(r4));
		assertEquals(null, ts.nextRecord(r5));

	}

	public void testGetRecord() {
		TableStorage ts = new TableStorage(mTable1);
		boolean ret = ts.init();
		assertTrue(ret);

 		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "a"));
		r1.set("job", new VarCharValue(20, "a"));

 		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "b"));
		r2.set("job", new VarCharValue(20, "b"));

 		Record r3 = mTable1.createRecord(3);
		r3.set("id", new IntegerValue(0x3));
		r3.set("name", new FixedCharValue(15, "c"));
		r3.set("job", new VarCharValue(20, "c"));

 		Record r4 = mTable1.createRecord(4);
		r4.set("id", new IntegerValue(0x4));
		r4.set("name", new FixedCharValue(15, "d"));
		r4.set("job", new VarCharValue(20, "d"));

 		Record r5 = mTable1.createRecord(5);
		r5.set("id", new IntegerValue(0x5));
		r5.set("name", new FixedCharValue(15, "e"));
		r5.set("job", new VarCharValue(20, "e"));
		
		ts.insert(r1);
		ts.insert(r2);
		ts.insert(r3);
		ts.insert(r4);
		ts.insert(r5);

		ts.commit();
		ret = ts.init();
		assertTrue(ret);

		Record r = ts.getRecord(0, 1);
		assertEquals(r1, r);

		r = ts.getRecord(1, 1);
		assertEquals(r2, r);

		r = ts.getRecord(0, 2);
		assertEquals(r3, r);

		r = ts.getRecord(1, 2);
		assertEquals(r4, r);

		r = ts.getRecord(0, 3);
		assertEquals(r5, r);

		r = ts.firstRecord();
		assertEquals(r1, r);

		r = ts.lastRecord();
		assertEquals(r5, r);
	}

	public void testDelete() {
		TableStorage ts = new TableStorage(mTable1);
		boolean ret = ts.init();
		assertTrue(ret);

 		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "a"));
		r1.set("job", new VarCharValue(20, "a"));

 		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "b"));
		r2.set("job", new VarCharValue(20, "b"));

 		Record r3 = mTable1.createRecord(3);
		r3.set("id", new IntegerValue(0x3));
		r3.set("name", new FixedCharValue(15, "c"));
		r3.set("job", new VarCharValue(20, "c"));

 		Record r4 = mTable1.createRecord(4);
		r4.set("id", new IntegerValue(0x4));
		r4.set("name", new FixedCharValue(15, "d"));
		r4.set("job", new VarCharValue(20, "d"));

 		Record r5 = mTable1.createRecord(5);
		r5.set("id", new IntegerValue(0x5));
		r5.set("name", new FixedCharValue(15, "e"));
		r5.set("job", new VarCharValue(20, "e"));
		
		ts.insert(r1);
		ts.insert(r2);
		ts.insert(r3);
		ts.insert(r4);
		ts.insert(r5);

		assertEquals(0, r1.index());
		assertEquals(1, r2.index());
		assertEquals(0, r3.index());
		assertEquals(1, r4.index());
		assertEquals(0, r5.index());
		assertEquals(r1, ts.nextRecord(null));

		ts.delete(r1);

		assertEquals(-1, r1.index());
		assertEquals(0, r2.index());
		assertEquals(0, r3.index());
		assertEquals(1, r4.index());
		assertEquals(0, r5.index());
		assertEquals(r2, ts.nextRecord(null));

		ts.delete(r2);

		assertEquals(-1, r1.index());
		assertEquals(-1, r2.index());
		assertEquals(0, r3.index());
		assertEquals(1, r4.index());
		assertEquals(0, r5.index());

		assertEquals(r3, ts.nextRecord(null));

		ts.delete(r4);

		assertEquals(-1, r1.index());
		assertEquals(-1, r2.index());
		assertEquals(0, r3.index());
		assertEquals(-1, r4.index());
		assertEquals(0, r5.index());

		assertEquals(r3, ts.nextRecord(null));
		assertEquals(r5, ts.nextRecord(r3));

	}


	public void testDelete_afterCommit() {
		TableStorage ts = new TableStorage(mTable1);
		boolean ret = ts.init();
		assertTrue(ret);

 		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "a"));
		r1.set("job", new VarCharValue(20, "a"));

 		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "b"));
		r2.set("job", new VarCharValue(20, "b"));

 		Record r3 = mTable1.createRecord(3);
		r3.set("id", new IntegerValue(0x3));
		r3.set("name", new FixedCharValue(15, "c"));
		r3.set("job", new VarCharValue(20, "c"));

 		Record r4 = mTable1.createRecord(4);
		r4.set("id", new IntegerValue(0x4));
		r4.set("name", new FixedCharValue(15, "d"));
		r4.set("job", new VarCharValue(20, "d"));

 		Record r5 = mTable1.createRecord(5);
		r5.set("id", new IntegerValue(0x5));
		r5.set("name", new FixedCharValue(15, "e"));
		r5.set("job", new VarCharValue(20, "e"));
		
		ts.insert(r1);
		ts.insert(r2);
		ts.insert(r3);
		ts.insert(r4);
		ts.insert(r5);

		ts.commit();
		ret = ts.init();
		assertTrue(ret);

		assertEquals(5, ts.recordCount());
		assertEquals(1, ts.firstRecord().id());
		assertEquals(5, ts.lastRecord().id());

		ts.delete(r1);

		assertEquals(4, ts.recordCount());
		assertEquals(2, ts.firstRecord().id());
		assertEquals(5, ts.lastRecord().id());

		ts.delete(r2);

		assertEquals(3, ts.recordCount());
		assertEquals(3, ts.firstRecord().id());
		assertEquals(5, ts.lastRecord().id());

		ts.delete(r5);

		assertEquals(2, ts.recordCount());
		assertEquals(3, ts.firstRecord().id());
		assertEquals(4, ts.lastRecord().id());

		ts.delete(r3);

		assertEquals(1, ts.recordCount());
		assertEquals(4, ts.firstRecord().id());
		assertEquals(4, ts.lastRecord().id());

		ts.delete(r4);

		assertEquals(0, ts.recordCount());
	}

	public void testUpdate() {
		TableStorage ts = new TableStorage(mTable1);
		boolean ret = ts.init();
		assertTrue(ret);

 		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "a"));
		r1.set("job", new VarCharValue(20, "a"));

 		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "b"));
		r2.set("job", new VarCharValue(20, "b"));

 		Record r3 = mTable1.createRecord(3);
		r3.set("id", new IntegerValue(0x3));
		r3.set("name", new FixedCharValue(15, "c"));
		r3.set("job", new VarCharValue(20, "c"));
		
		ts.insert(r1);
		ts.insert(r2);
		ts.insert(r3);

		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());
		assertEquals(1, r2.index());
		assertEquals(1, r2.chunkid());
		assertEquals(0, r3.index());
		assertEquals(2, r3.chunkid());


 		Record r2_1 = mTable1.createRecord(2);
		r2_1.set("id", new IntegerValue(0x2));
		r2_1.set("name", new FixedCharValue(15, "b"));
		r2_1.set("job", new VarCharValue(20, "B"));
		r2_1.setPosition(1, 1);
		ts.update(r2_1);

		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());
		assertEquals(1, r2_1.index());
		assertEquals(1, r2_1.chunkid());
		assertEquals(0, r3.index());
		assertEquals(2, r3.chunkid());

		ts.commit();

 		Record r2_2 = mTable1.createRecord(2);
		r2_2.set("id", new IntegerValue(0x2));
		r2_2.set("name", new FixedCharValue(15, "b"));
		r2_2.set("job", new VarCharValue(20, "12345678901234567890"));
		r2_2.setPosition(1, 1);
		ts.update(r2_2);

		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());
		assertEquals(1, r2_2.index());
		assertEquals(3, r2_2.chunkid());
		assertEquals(0, r3.index());
		assertEquals(2, r3.chunkid());
	}

}
