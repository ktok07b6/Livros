package livros.storage;

import livros.Log;
import livros.db.Constraint;
import livros.db.DataBase;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.FixedCharValue;
import livros.db.VarCharValue;
import livros.db.IntegerValue;
import livros.db.Record;
import livros.db.Table;
import livros.db.Type;
import livros.db.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;

public class StorageTest
{
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(JUnitRecordChunkTest.class));
		suite.addTest(new TestSuite(JUnitRecordFileTest.class));
		suite.addTest(new TestSuite(JUnitIndexFileTest.class));
		suite.addTest(new TestSuite(JUnitVirtualChunkTest.class));
		suite.addTest(new TestSuite(JUnitChunkManagerTest.class));
		suite.addTest(new TestSuite(JUnitTableStorageTest.class));
		return suite;
	}

	public static void main(String[] args) {
		File file = new File("/sdcard");
		if (file.exists()) {
			StorageManager.DB_DIR="/sdcard/tmp/";
			StorageManager.TMP_DIR="/sdcard/tmp/";
		}
		StorageManager.DB_FILE = "storage_test.db";

		ChunkHeader.CHUNK_SIZE = 128;
		Log.d("SIZE_MAX " + NewerChunk.SIZE_MAX);
		junit.textui.TestRunner.run (suite());
		//test();
	}

	/*
	static void test() {
		try {
			Table table = new Table(DataBase.DB_FILENAME, "table1");
			DataBase db = new DataBase(DataBase.DB_FILENAME);
			db.addTable(table);

			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				Field idField = new Field("id", Type.integerType, consts);
				table.addField(idField);
			}

			{
				List consts = new ArrayList();
				consts.add(Constraint.notNullConst);
				consts.add(Constraint.uniqueConst);
				Field nameField = new Field("name", Type.fixedChar(15), consts);
				nameField.setDefaultValue(new FixedCharValue(15, "foo"));
				table.addField(nameField);
			}
			{
				Field jobField = new Field("job", Type.varChar(20));
				jobField.setDefaultValue(new VarCharValue(20, "bar"));
				table.addField(jobField);
			}

			Record r = table.createRecord();
			r.set("id", new IntegerValue(0));
			r.set("name", new FixedCharValue(15, "Dave Stewart"));
			r.set("job", new VarCharValue(20, "Composer"));
			table.insertRecord(r);
			
			r = table.createRecord();
			r.set("id", new IntegerValue(1));
			r.set("name", new FixedCharValue(15, "John Lennon"));
			r.set("job", new VarCharValue(20, "Artist"));
			table.insertRecord(r);
			
			r = table.createRecord();
			r.set("id", new IntegerValue(2));
			r.set("name", new FixedCharValue(15, "Lou Reed"));
			r.set("job", new VarCharValue(20, "Rock Singer"));
			table.insertRecord(r);
			
			r = table.createRecord();
			r.set("id", new IntegerValue(3));
			r.set("name", new FixedCharValue(15, "David Bowie"));
			r.set("job", new VarCharValue(20, "Artist"));
			table.insertRecord(r);
			
			r = table.createRecord();
			r.set("id", new IntegerValue(4));
			r.set("name", new FixedCharValue(30, "Mark Bolan"));
			r.set("job", new VarCharValue(20, "Gram Rocker"));
			table.insertRecord(r);
			
			assert table.size() == 5;

			DbFile file = new DbFile("./default.db");
			file.saveHeader(db);
			//file.saveRecord(db);
			
			DataBase loaded = file.loadHeader();
			checkLoaded(loaded);
			
			file.loadAllRecords(loaded);
			
			List tables = loaded.tables();
			Table t = (Table)tables.get(0);
			checkLoaded2(t);
			
			Log.d("checkLoaded OK");
		} catch (Exception ex) {
			Log.d(ex.toString());
			ex.printStackTrace();
		}
	}

	static void checkLoaded2(Table t) {
		assert t.size() == 5;

		IRecordSet rset = t.records();
		Record r = rset.get(0);
		Value v = r.get("id");
		assert v.isInteger();
		assert v.asInteger().intValue() == 0;
		v = r.get("name");
		assert v.isFixedChar();
		assert v.toString().equals("Dave Stewart   ");
		assert v.type().capacity() == 15;

		v = r.get("job");
		assert v.isVarChar();
		assert v.toString().equals("Composer");
		assert v.type().capacity() == 20;

	}

	static void checkLoaded(DataBase db) {
		assert db != null;
		List tables = db.tables();
		assert tables != null && tables.size() == 1;
		Table t = db.table("table1");
		assert t != null;
		
		FieldList fields = t.fieldList();
		assert fields.size() == 3;
		
		Field id = t.field("id");
		assert id != null;
		Field name = t.field("name");
		assert name != null;
		Field job = t.field("job");
		assert job != null;

		assert id.type().isInteger();
		assert id.type().capacity() == 0;
		assert name.type().isFixedChar();
		assert name.type().capacity() == 15;
		assert job.type().isVarChar();
		assert job.type().capacity() == 20;
		
		assert id.defaultValue().isNull();
		assert name.defaultValue().isFixedChar();
		assert name.defaultValue().asFixedChar().toString().equals("foo            ");
		assert job.defaultValue().isVarChar();
		assert job.defaultValue().asVarChar().toString().equals("bar");

		assert id.isPrimary();
		assert !id.isUnique();
		assert !id.isNotNull();
		assert !name.isPrimary();
		assert name.isUnique();
		assert name.isNotNull();
		assert !job.isPrimary();
		assert !job.isUnique();
		assert !job.isNotNull();

	}
	*/
}
