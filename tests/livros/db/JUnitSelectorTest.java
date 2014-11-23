package livros.db;

import livros.Log;
import livros.storage.StorageManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitSelectorTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitSelectorTest(String name) {
		super(name);

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

	public void testSequentialSelector() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				List consts = new ArrayList();
				Field idField = new Field("id", Type.integerType);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record recs[] = new Record[100];
		for (int i = 0; i < recs.length; i++) {
			recs[i] = t1.createRecord();
			recs[i].set("id", new IntegerValue(i));
			t1.insertRecord(recs[i]);
		}		

		Selector sel = t1.selector(null);
		assertTrue(sel != null);
		assertTrue(sel instanceof SequentialSelector);

		assertTrue(sel.hasNext());

		for (int i = 0; i < recs.length; i++) {
			assertTrue(sel.hasNext());
			assertTrue(sel.hasNext());
			assertEquals(recs[i], sel.next());
		}

		db.delTable(t1);
	}

	public void testConditionalSelector() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				Field idField = new Field("id", Type.integerType);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
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

		Record recs[] = new Record[100];
		for (int i = 0; i < recs.length; i++) {
			recs[i] = t1.createRecord();
			recs[i].set("id", new IntegerValue(i));
			if ((i % 2) == 0) {
				recs[i].set("id2", new IntegerValue(i));
			} else {
				recs[i].set("id2", new IntegerValue(-1));
			}
			t1.insertRecord(recs[i]);
		}		

		Expr expr = new BinExpr(Operator.EQ, 
								new FieldRef("id"),
								new FieldRef("id2"));
		Selector sel = t1.selector(expr);
		assertTrue(sel != null);
		assertTrue(sel instanceof ConditionalSelector);

		assertTrue(sel.hasNext());

		for (int i = 0; i < recs.length/2; i++) {
			assertTrue(sel.hasNext());
			assertTrue(sel.hasNext());
			assertEquals(recs[i*2], sel.next());
		}

		db.delTable(t1);
	}

	public void testDerivedSelector() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				Field idField = new Field("id", Type.integerType);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
				Field id2Field = new Field("id2", Type.integerType);
				t1.addField(id2Field);
				assertEquals(id2Field, t1.field("id2"));
				Field id3Field = new Field("id3", Type.integerType);
				t1.addField(id3Field);
				assertEquals(id3Field, t1.field("id3"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record recs[] = new Record[100];
		for (int i = 0; i < recs.length; i++) {
			recs[i] = t1.createRecord();
			recs[i].set("id", new IntegerValue(i));
			if ((i % 2) == 0) {
				recs[i].set("id2", new IntegerValue(i));
			} else {
				recs[i].set("id2", new IntegerValue(-1));
			}
			if (i >= 50) {
				recs[i].set("id3", new IntegerValue(-i));
			} else {
				recs[i].set("id3", new IntegerValue(i));
			}
			t1.insertRecord(recs[i]);
		}

		DerivedTable t2 = new DerivedTable(db.name(), 
										   TEST_TABLE2, 
										   t1.baseName(), 
										   t1.fieldList());
		t2.init();
		db.addTmpTable(t2);
 		Expr expr = new BinExpr(Operator.EQ, 
								new FieldRef("id"),
								new FieldRef("id2"));
		Selector sel = t1.selector(expr);
		assertTrue(sel != null);
		assertTrue(sel instanceof ConditionalSelector);
		while (sel.hasNext()) {
			Record r = (Record)sel.next();
			t2.insertIndex(new RecordIndex(r));
		}
		sel.finish();
		t2.flush();

		sel = t2.selector(null);
		assertTrue(sel != null);
		assertTrue(sel instanceof DerivedSequentialSelector);
		for (int i = 0; i < recs.length/2; i++) {
			assertTrue(sel.hasNext());
			assertTrue(sel.hasNext());
			assertEquals(recs[i*2], sel.next());
		}

 		expr = new BinExpr(Operator.EQ, 
						   new FieldRef("id"),
						   new FieldRef("id3"));
		sel = t2.selector(expr);
		assertTrue(sel != null);
		assertTrue(sel instanceof ConditionalSelector);
		for (int i = 0; i < recs.length/4; i++) {
			assertTrue(sel.hasNext());
			assertTrue(sel.hasNext());
			assertEquals(recs[i*2], sel.next());
		}

		db.delTable(t1);
		db.close();
	}

	public void testBTreeSelector() {
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
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record recs[] = new Record[100];
		for (int i = 0; i < recs.length; i++) {
			recs[i] = t1.createRecord();
			recs[i].set("id", new IntegerValue(i));
			t1.insertRecord(recs[i]);
		}		

		for (int i = 0; i < 100; i++) {
			Expr expr = new BinExpr(Operator.GT, 
									new FieldRef("id"),
									new IntegerValue(i));
			Selector sel = t1.selector(expr);
			assertTrue(sel != null);
			assertTrue(sel instanceof BTreeSelector);

			for (int j = i+1; j < 100; j++) {
				assertTrue(sel.hasNext());
				assertTrue(sel.hasNext());
				assertEquals(recs[j], sel.next());
			}
			assertTrue(!sel.hasNext());
		}

		for (int i = 0; i < 100; i++) {
			Expr expr = new BinExpr(Operator.LT, 
									new FieldRef("id"),
									new IntegerValue(i));
			Selector sel = t1.selector(expr);
			assertTrue(sel != null);
			assertTrue(sel instanceof BTreeSelector);

			for (int j = 0; j < i; j++) {
				assertTrue(sel.hasNext());
				assertTrue(sel.hasNext());
				assertEquals(recs[j], sel.next());
			}
			assertTrue(!sel.hasNext());
		}

		for (int i = 0; i < 100; i++) {
			Expr expr = new BinExpr(Operator.GE, 
									new FieldRef("id"),
									new IntegerValue(i));
			Selector sel = t1.selector(expr);
			assertTrue(sel != null);
			assertTrue(sel instanceof BTreeSelector);

			for (int j = i; j < 100; j++) {
				assertTrue(sel.hasNext());
				assertTrue(sel.hasNext());
				assertEquals(recs[j], sel.next());
			}
			assertTrue(!sel.hasNext());
		}

		for (int i = 0; i < 100; i++) {
			Expr expr = new BinExpr(Operator.LE, 
									new FieldRef("id"),
									new IntegerValue(i));
			Selector sel = t1.selector(expr);
			assertTrue(sel != null);
			assertTrue(sel instanceof BTreeSelector);

			for (int j = 0; j < i+1; j++) {
				assertTrue(sel.hasNext());
				assertTrue(sel.hasNext());
				assertEquals(recs[j], sel.next());
			}
			assertTrue(!sel.hasNext());
		}

		for (int i = 0; i < 100; i++) {
			Expr expr = new BinExpr(Operator.EQ, 
							   new FieldRef("id"),
							   new IntegerValue(i));
			Selector sel = t1.selector(expr);
			assertTrue(sel != null);
			assertTrue(sel instanceof BTreeSelector);
		
			assertTrue(sel.hasNext());
			assertEquals(recs[i], sel.next());
			assertTrue(!sel.hasNext());
		}

		for (int i = 0; i < 100; i++) {
			Expr expr = new BinExpr(Operator.NE, 
							   new FieldRef("id"),
							   new IntegerValue(i));
			Selector sel = t1.selector(expr);
			assertTrue(sel != null);
			assertTrue(sel instanceof BTreeSelector);
		
			int offs = 0;
			for (int j = 0; j < 99; j++) {
				assertTrue(sel.hasNext());
				if (j >= i) offs = 1;
				assertEquals(recs[j+offs], sel.next());
			}
			assertTrue(!sel.hasNext());
		}

		db.delTable(t1);
	}
	
	public void testUninSelector() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				List consts = new ArrayList();
				consts.add(Constraint.uniqueConst);
				Field idField = new Field("id", Type.integerType, consts);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
				Field textField = new Field("text", new FixedCharType(10), consts);
				t1.addField(textField);
				assertEquals(textField, t1.field("text"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record recs[] = new Record[100];
		for (int i = 0; i < recs.length; i++) {
			recs[i] = t1.createRecord();
			recs[i].set("id", new IntegerValue(i));
			if ((i % 2) == 0) {
				recs[i].set("text", new TextValue(String.valueOf(i)));
			} else {
				recs[i].set("text", new TextValue(String.valueOf(-i)));
			}
			t1.insertRecord(recs[i]);
		}		

		Expr expr = new BinExpr(Operator.OR, 
								new BinExpr(Operator.GE, 
											new FieldRef("id"),
											new IntegerValue(90)),
								new BinExpr(Operator.EQ, 
											new FieldRef("text"),
											new TextValue("10")));
		Selector sel = t1.selector(expr);
		assertTrue(sel != null);
		assertTrue(sel instanceof UnionSelector);
		/*
		while (sel.hasNext()) {
			Log.d(""+sel.next());
		}
		*/
		assertTrue(sel.hasNext());
		assertEquals(recs[10], sel.next());

		for (int i = 0; i < 10; i++) {
			assertTrue(sel.hasNext());
			assertTrue(sel.hasNext());
			assertEquals(recs[90+i], sel.next());
		}
		assertTrue(!sel.hasNext());

		db.delTable(t1);
	}

}
