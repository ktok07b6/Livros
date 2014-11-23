package livros.db;

import livros.Debug;
import livros.Log;
import livros.storage.StorageManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitTableTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitTableTest(String name) {
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
	}

	public void testCreateTable() {
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				Field idField = new Field("id", Type.integerType);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
			}
			{
				Field nameField = new Field("name", Type.fixedChar(15));
				nameField.setDefaultValue(new FixedCharValue(15, "foo"));
				t1.addField(nameField);
				assertEquals(nameField, t1.field("name"));
			}
			{
				Field jobField = new Field("job", Type.varChar(20));
				jobField.setDefaultValue(new VarCharValue(20, "bar"));
				t1.addField(jobField);
				assertEquals(jobField, t1.field("job"));
			}
			t1.init();
			assertEquals(0, t1.size());

			DataBase db = DataBase.open(TEST_DB);
			db.addTable(t1);
			db.commit(t1);
			db.close();

			db = DataBase.open(TEST_DB);
			Table tt1 = db.table(TEST_TABLE1);
			assertTrue(tt1 != null);
			assertEquals(t1.name(), tt1.name());
			db.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}		
	}

	public void testDeleteTable() {
		DataBase db = DataBase.open(TEST_DB);
		Table t2 = new Table(TEST_DB, TEST_TABLE2);
		try {
			{
				Field idField = new Field("id", Type.integerType);
				t2.addField(idField);
				assertEquals(idField, t2.field("id"));
			}
			t2.init();
			db = DataBase.open(TEST_DB);
			db.addTable(t2);
			db.commit(t2);
			db.close();

			db = DataBase.open(TEST_DB);
			Table tt2 = db.table(TEST_TABLE2);
			assertTrue(tt2 != null);
			assertEquals(t2.name(), tt2.name());

			boolean ret = db.delTable(tt2);
			assertTrue(ret);
			db.commit(tt2);
			assertTrue(db.tables().size() == 0);
			db.close();

			db = DataBase.open(TEST_DB);
			assertTrue(db.table(TEST_TABLE2) == null);
			db.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testNewId() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			Field idField = new Field("id", Type.integerType);
			t1.addField(idField);
			assertEquals(idField, t1.field("id"));
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

		t1.init();
		db.addTable(t1);

		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			assertEquals(i, r.id());
			r.set("id", new IntegerValue(i));
			t1.insertRecord(r);
		}

		assertEquals(10, t1.size());
		assertEquals(9, t1.getMaxRecordId());
		db.commit(t1);
		db.close();

		db = DataBase.open(TEST_DB);
		t1 = db.table(TEST_TABLE1);

		assertEquals(9, t1.getMaxRecordId());

		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			assertEquals(i+10, r.id());
			r.set("id", new IntegerValue(i+10));
			t1.insertRecord(r);
		}

		assertEquals(20, t1.size());
		db.commit(t1);
		db.close();

		db = DataBase.open(TEST_DB);
		t1 = db.table(TEST_TABLE1);

		List randList = new LinkedList();
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			randList.add(new Integer(i));
		}
		
		for (int i = 0; i < 5; i++) {
			Collections.shuffle(randList, random);
		}
		
		assertEquals(20, t1.size());

		for (int i = 0; i < 5; i++) {
			int rnd = ((Integer)randList.get(i)).intValue();
			Record r = t1.findById(rnd);
			assertTrue(r != null);
			t1.deleteRecord(r);
		}

		assertEquals(15, t1.size());
		db.commit(t1);
		db.close();

		db = DataBase.open(TEST_DB);
		t1 = db.table(TEST_TABLE1);

		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			assertEquals(i+20, r.id());
			r.set("id", new IntegerValue(i+20));
			t1.insertRecord(r);
		}

		assertTrue(db.delTable(t1));
		db.commit(t1);
		db.close();
	}

	
	public void testInsertRecord() {
	}

	public void testDeleteRecord() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			Field idField = new Field("id", Type.integerType);
			t1.addField(idField);
			assertEquals(idField, t1.field("id"));
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

		t1.init();
		db.addTable(t1);

		List recs = new ArrayList();
		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			assertEquals(i, r.id());
			r.set("id", new IntegerValue(i));
			t1.insertRecord(r);
			recs.add(r);
		}
		assertTrue(t1.size() == 10);

		db.commit(t1);
		db.close();
		db = DataBase.open(TEST_DB);
		t1 = db.table(TEST_TABLE1);

		assertTrue(t1.size() == 10);
		for (int i = 0; i < 10; i++) {
			Record r = (Record)recs.get(i);
			assertTrue(t1.deleteRecord(r));
		}
		
		assertTrue(t1.size() == 0);
		
		db.delTable(t1);
		db.commit(t1);
		db.close();
	}

	public void testUpdateRecord() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);

		try {
			Field idField = new Field("id", Type.integerType);
			t1.addField(idField);
			assertEquals(idField, t1.field("id"));
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

		t1.init();
		db.addTable(t1);

		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			r.set("id", new IntegerValue(i));
			t1.insertRecord(r);
		}

		List randList = new LinkedList();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			randList.add(new Integer(i));
		}
		for (int i = 0; i < 10; i++) {
			Collections.shuffle(randList, random);
		}
		final String updateFields[] = {"id"};
		for (int i = 0; i < 10; i++) {
			int rnd = ((Integer)randList.get(i)).intValue();
			Record r = (Record)t1.findById(rnd).clone();
			assertTrue(r != null);
			r.set("id", new IntegerValue(10-r.id()));
			t1.updateRecord(r, updateFields);
		}

		for (int i = 0; i < 10; i++) {
			Record r = t1.findById(i);
			assertTrue(r != null);
			assertEquals(new IntegerValue(10-r.id()), r.get("id"));
		}
		db.delTable(t1);
		db.commit(t1);
		db.close();

	}

	public void testUniqueConst() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				Field idField = new Field("id", Type.integerType, consts);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
			}
			{
				List consts = new ArrayList();
				consts.add(Constraint.uniqueConst);
				Field id2Field = new Field("id2", Type.integerType, consts);
				t1.addField(id2Field);
				assertEquals(id2Field, t1.field("id2"));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

		t1.init();
		db.addTable(t1);
		
		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			r.set("id", new IntegerValue(i));
			r.set("id2", new IntegerValue(i));
			assertTrue(t1.insertRecord(r) == true);
		}
		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			r.set("id", new IntegerValue(i));
			r.set("id2", new IntegerValue(i+1000));
			assertTrue(t1.insertRecord(r) == false);
		}

		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			r.set("id", new IntegerValue(i+1000));
			r.set("id2", new IntegerValue(i));
			assertTrue(t1.insertRecord(r) == false);
		}

		final String updateFields[] = {"id"};

		Record r = (Record)t1.findById(0).clone();
		r.set("id", new IntegerValue(10000));
		assertTrue(t1.updateRecord(r, updateFields) == true);

		r = (Record)t1.findById(1).clone();
		r.set("id", new IntegerValue(0));
		assertTrue(t1.updateRecord(r, updateFields) == true);

		db.delTable(t1);
		db.commit(t1);
		db.close();

	}

	public void testReferencesConst() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		Table t2 = new Table(TEST_DB, TEST_TABLE2);
		try {
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				Field idField = new Field("id", Type.integerType, consts);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
			}
			{
				List consts = new ArrayList();
				consts.add(Constraint.notNullConst);
				consts.add(Constraint.uniqueConst);
				Field nameField = new Field("name", Type.fixedChar(15), consts);
				nameField.setDefaultValue(new FixedCharValue(15, "foo"));
				t1.addField(nameField);
				assertEquals(nameField, t1.field("name"));
			}
			t1.init();
			db.addTable(t1);
			//table 2
			{
				List consts = new ArrayList();
				consts.add(new RefConst(TEST_TABLE1, "id"));
				Field idField = new Field("refid", Type.integerType, consts);
				t2.addField(idField);
			}

			t2.init();
			db.addTable(t2);
			
			assertEquals(0, t1.size());

			Record r0 = t1.createRecord();
			r0.set("id", new IntegerValue(0));
			r0.set("name", new TextValue("zero"));
			assertTrue(t1.insertRecord(r0) == true);

			Record r1 = t1.createRecord();
			r1.set("id", new IntegerValue(1));
			r1.set("name", new TextValue("one"));
			assertTrue(t1.insertRecord(r1) == true);

			Record r2 = t1.createRecord();
			r2.set("id", new IntegerValue(2));
			r2.set("name", new TextValue("two"));
			assertTrue(t1.insertRecord(r2) == true);

			//can insert reference value
			Record ref0 = t2.createRecord();
			ref0.set("refid", new IntegerValue(0));
			assertTrue(t2.insertRecord(ref0) == true);

			//cannot insert invalid reference value
			Record ref3 = t2.createRecord();
			ref3.set("refid", new IntegerValue(3));
			assertTrue(t2.insertRecord(ref3) == false);

			//can insert same reference value
			ref0 = t2.createRecord();
			ref0.set("refid", new IntegerValue(0));
			assertTrue(t2.insertRecord(ref0) == true);

			Record r = t1.findById(0);
			assertTrue(r.refCount("id") == 2);
			r = t1.findById(1);
			assertTrue(r.refCount("id") == 0);
			r = t1.findById(2);
			assertTrue(r.refCount("id") == 0);

			assertTrue(t1.size() == 3);
			assertTrue(t2.size() == 2);
			db.commit(t1);
			db.commit(t2);
			db.close();

			db = DataBase.open(TEST_DB);
			t1 = db.table(TEST_TABLE1);
			t2 = db.table(TEST_TABLE2);

			//cannot insert non reference value
			ref3 = t2.createRecord();
			ref3.set("refid", new IntegerValue(3));
			assertTrue(t2.insertRecord(ref3) == false);

			Record ref1 = t2.createRecord();
			ref1.set("refid", new IntegerValue(1));
			assertTrue(t2.insertRecord(ref1) == true);

			assertTrue(t1.size() == 3);
			assertTrue(t2.size() == 3);

			//check reference counts
			r = t1.findById(0);
			assertTrue(r.refCount("id") == 2);
			r = t1.findById(1);
			assertTrue(r.refCount("id") == 1);
			r = t1.findById(2);
			assertTrue(r.refCount("id") == 0);

			
			final String updateFields[] = {"id"};
			r = (Record)t1.findById(0).clone();
			//cannot delete referenced record
			assertTrue(t1.deleteRecord(r) == false);
			//cannot update referenced field
			r.set("id", new IntegerValue(100));
			assertTrue(t1.updateRecord(r, updateFields) == false);
			//can update non referenced field
			r.set("id", new IntegerValue(0));
			r.set("name", new TextValue("ZERO"));
			assertTrue(t1.updateRecord(r, new String[]{"name"}) == true);

			//dec reference record
			r = t2.findById(0);
			assertTrue(t2.deleteRecord(r) == true);
			r = t1.findById(0);
			assertTrue(r.refCount("id") == 1);
			r = t1.findById(1);
			assertTrue(r.refCount("id") == 1);
			r = t1.findById(2);
			assertTrue(r.refCount("id") == 0);


			r = t2.findById(1);
			assertTrue(t2.deleteRecord(r) == true);
			r = t1.findById(0);
			assertTrue(r.refCount("id") == 0);
			r = t1.findById(1);
			assertTrue(r.refCount("id") == 1);
			r = t1.findById(2);
			assertTrue(r.refCount("id") == 0);

			//can delete non referenced record
			r = (Record)t1.findById(0);
			assertTrue(t1.deleteRecord(r) == true);
			//cannot delete referenced record
			r = (Record)t1.findById(1);
			assertTrue(t1.deleteRecord(r) == false);

			//update the reference record
			r = (Record)t2.findById(2).clone();
			r.set("refid", new IntegerValue(2));
			assertTrue(t2.updateRecord(r, new String[]{"refid"}) == true);

			//can delete non referenced record
			r = (Record)t1.findById(1);
			assertTrue(t1.deleteRecord(r) == true);
			//cannot delete referenced record
			r = (Record)t1.findById(2);
			assertTrue(t1.deleteRecord(r) == false);

			//cannot delete referenced table
			assertTrue(db.delTable(t1) == false);
			//del reference table
			assertTrue(db.delTable(t2) == true);

			//can delete non referenced record
			r = (Record)t1.findById(2);
			assertTrue(t1.deleteRecord(r) == true);

			//can delete non referenced table
			assertTrue(db.delTable(t1) == true);

			db.commit(t1);
			db.commit(t2);
			db.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

	}

	public void testSelect() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				Field idField = new Field("id", Type.integerType, consts);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
			}
			{
				Field id2Field = new Field("id2", Type.integerType);
				t1.addField(id2Field);
				assertEquals(id2Field, t1.field("id2"));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

		t1.init();
		db.addTable(t1);
		
		for (int i = 0; i < 10; i++) {
			Record r = t1.createRecord();
			r.set("id", new IntegerValue(i));
			r.set("id2", new IntegerValue(i%3));
			assertTrue(t1.insertRecord(r) == true);
		}

		Expr expr = new BinExpr(Operator.EQ, new FieldRef("id"), new IntegerValue(3));
		Selector selector = t1.selector(expr);
		assertTrue(selector.hasNext());
		Record r = selector.next();
		assertEquals(r, t1.findById(3));
		assertEquals(r.get("id"), new IntegerValue(3));
		assertEquals(r.get("id2"), new IntegerValue(0));
		assertFalse(selector.hasNext());

		expr = new BinExpr(Operator.EQ, new FieldRef("id2"), new IntegerValue(0));
		selector = t1.selector(expr);
		assertTrue(selector.hasNext());
		assertTrue(selector.hasNext());
		assertTrue(selector.hasNext());
		r = selector.next();
		assertEquals(r, t1.findById(0));
		assertEquals(r.get("id"), new IntegerValue(0));
		assertEquals(r.get("id2"), new IntegerValue(0));
		assertTrue(selector.hasNext());
		r = selector.next();
		assertEquals(r, t1.findById(3));
		assertEquals(r.get("id"), new IntegerValue(3));
		assertEquals(r.get("id2"), new IntegerValue(0));
		assertTrue(selector.hasNext());
		r = selector.next();
		assertEquals(r, t1.findById(6));
		assertEquals(r.get("id"), new IntegerValue(6));
		assertEquals(r.get("id2"), new IntegerValue(0));
		assertTrue(selector.hasNext());
		r = selector.next();
		assertEquals(r, t1.findById(9));
		assertEquals(r.get("id"), new IntegerValue(9));
		assertEquals(r.get("id2"), new IntegerValue(0));
		assertFalse(selector.hasNext());

		db.delTable(t1);
		db.commitAll();
		db.close();
	}
}
