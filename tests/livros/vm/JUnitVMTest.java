package livros.vm;

import livros.Console;
import livros.LogConsole;
import livros.Livros;
import livros.Log;
import livros.db.BinExpr;
import livros.db.Constraint;
import livros.db.DataBase;
import livros.db.Field;
import livros.db.FieldRef;
import livros.db.FixedCharValue;
import livros.db.IntegerValue;
import livros.db.Operator;
import livros.db.TextValue;
import livros.db.Type;
import livros.db.Value;
import livros.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import java.io.File;

public class JUnitVMTest extends TestCase
{
	//LogConsole mConsole;
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE = "tab";

	private String tmp(int i) {
		return StorageManager.TMP_FILE_PREFIX+i;
	}

	public JUnitVMTest(String name) {
		super(name);
		/*
		File file = new File(StorageManager.DB_DIR+TEST_TABLE);
		if (file.exists()) {
			file.delete();
		}
		file = new File(StorageManager.DB_DIR+TEST_DB);
		if (file.exists()) {
			file.delete();
		}
		*/
	}

	public void setUp() {
		File file = new File(StorageManager.DB_DIR+TEST_TABLE);
		if (file.exists()) {
			file.delete();
		}
		file = new File(StorageManager.DB_DIR+TEST_DB);
		if (file.exists()) {
			file.delete();
		}
		reset();
	}

	private List insts = new ArrayList();
	private void emit(INST inst) {
		insts.add(inst);
	}

	private void exec() {
		VM vm = new VM();
		vm.exec(insts);
		vm.destroy();
	}

	private void reset() {
		//insts.clear();
		//mConsole.reset();
	}

	void addFields() {
		try {
			List consts1 = new ArrayList();
			consts1.add(Constraint.uniqueConst);
			consts1.add(Constraint.notNullConst);

			Field idField = new Field("id", Type.integerType, consts1);
			emit(new NEWFIELD(idField));

			Field id2Field = new Field("id2", Type.integerType);
			emit(new NEWFIELD(id2Field));

			Field textField = new Field("text", Type.fixedChar(8));
			textField.setDefaultValue(new FixedCharValue(8, "--------"));
			emit(new NEWFIELD(textField));

		} catch (Exception ex) {
			Log.d(ex.toString());
		}
	}

	void addRecord(int id, int id2, String text) {
		emit(new PUSHEXPR(new FieldRef("id")));
		emit(new PUSHEXPR(id));
		emit(new PUSHEXPR(new FieldRef("id2")));
		emit(new PUSHEXPR(id2));
		emit(new PUSHEXPR(new FieldRef("text")));
		emit(new PUSHEXPR(text));
		emit(new MAKEREC(3));
		emit(new INSERT(TEST_TABLE));
	}

	void addRecords() {
		addRecord(0, 10, "A");
		addRecord(1, 11, "B");
		addRecord(2, 2, "C");
		addRecord(3, 6, "D");
		addRecord(4, 14, "E");
	}

	public void testTable() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		emit(new SHOW(TEST_TABLE));
		emit(new COMMIT());

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();

		assertEquals("id|id2|text\n", console.getLog());
		reset();
	}
	
	public void testInsert() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		emit(new SHOW(TEST_TABLE));
		emit(new COMMIT());

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id|id2|text\n"+
			"0|10|'A       '\n"+
			"1|11|'B       '\n"+
			"2|2|'C       '\n"+
			"3|6|'D       '\n"+
			"4|14|'E       '\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}

	public void testSelect1() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		BinExpr expr = new BinExpr(Operator.EQ, new FieldRef("id"), new IntegerValue(1));
		emit(new PUSHEXPR(expr));
		emit(new SELECT(tmp(1), TEST_TABLE));
		emit(new SHOW(tmp(1)));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id|id2|text\n"+
			"1|11|'B       '\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}


	public void testSelect2() {
		Log.d("test_select2");
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		BinExpr expr = new BinExpr(Operator.EQ, new FieldRef("id"), new FieldRef("id2"));
		emit(new PUSHEXPR(expr));
		emit(new SELECT(tmp(1), TEST_TABLE));
		emit(new SHOW(tmp(1)));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id|id2|text\n"+
			"2|2|'C       '\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}

	public void testSelect3() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		BinExpr expr = new BinExpr(Operator.EQ, 
								new FieldRef("id2"),
								new BinExpr(Operator.ADD,
											new FieldRef("id"), 
											new IntegerValue(10)));
								
		emit(new PUSHEXPR(expr));
		emit(new SELECT(tmp(1), TEST_TABLE));
		BinExpr expr2 = new BinExpr(Operator.NE, 
									new FieldRef("id"),
									new IntegerValue(0));
		emit(new PUSHEXPR(expr2));
		emit(new SELECT(tmp(2), tmp(1)));
		emit(new SHOW(tmp(2)));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id|id2|text\n"+
			"1|11|'B       '\n"+
			"4|14|'E       '\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}

	
	public void testDelete() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		BinExpr expr = new BinExpr(Operator.OR, 
								new BinExpr(Operator.EQ,
											new FieldRef("text"), 
											new TextValue("B")),
								new BinExpr(Operator.EQ,
											new FieldRef("id"), 
											new IntegerValue(4)));

		emit(new PUSHEXPR(expr));
		emit(new SELECT(tmp(1), TEST_TABLE));
		emit(new SUBTRACT(TEST_TABLE, tmp(1)));
		emit(new SHOW(tmp(1)));
		emit(new SHOW(TEST_TABLE));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id|id2|text\n"+
			"1|11|'B       '\n"+
			"4|14|'E       '\n"+
 			"id|id2|text\n"+
			"0|10|'A       '\n"+
			"2|2|'C       '\n"+
			"3|6|'D       '\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}

	public void testUpdate() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		BinExpr expr = new BinExpr(Operator.OR, 
								new BinExpr(Operator.EQ,
											new FieldRef("id"), 
											new IntegerValue(3)),
								new BinExpr(Operator.EQ,
											new FieldRef("id"), 
											new IntegerValue(2)));
		emit(new PUSHEXPR(expr));
		emit(new SELECT(tmp(1), TEST_TABLE));
		emit(new PUSHEXPR(new FieldRef("id2")));
		BinExpr expr2 = new BinExpr(Operator.MUL, 
									new FieldRef("id"), 
									new IntegerValue(3));
		emit(new PUSHEXPR(expr2));
		emit(new UPDATE(TEST_TABLE, tmp(1), 1));
		//emit(new SHOW(tmp(1)));
		emit(new SHOW(TEST_TABLE));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		
		String expected = 
 			"id|id2|text\n"+
			"0|10|'A       '\n"+
			"1|11|'B       '\n"+
			"2|6|'C       '\n"+
			"3|9|'D       '\n" +
			"4|14|'E       '\n"
			;

		assertEquals(expected, console.getLog());
		reset();
	}

	public void testProjection1() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		emit(new PUSHEXPR("id+10"));
		BinExpr expr = new BinExpr(Operator.ADD, 
									new FieldRef("id"), 
									new IntegerValue(10));
		emit(new PUSHEXPR(expr));
		emit(new PUSHEXPR("id2+20"));
		BinExpr expr2 = new BinExpr(Operator.ADD, 
									new FieldRef("id2"), 
									new IntegerValue(20));
		emit(new PUSHEXPR(expr2));
		emit(new PROJECTION(tmp(1), TEST_TABLE, 2));
		emit(new SHOW(tmp(1)));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id+10|id2+20\n"+
			"10|30\n"+
			"11|31\n"+
			"12|22\n"+
			"13|26\n"+
			"14|34\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}

	public void testProjection2() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		addFields();
		emit(new NEWTABLE(TEST_TABLE));
		addRecords();
		emit(new COMMIT());

		BinExpr expr = new BinExpr(Operator.NE, 
									new FieldRef("id"), 
									new IntegerValue(0));
		emit(new PUSHEXPR(expr));
		emit(new SELECT(tmp(1), TEST_TABLE));
		emit(new PUSHEXPR("id+10"));
		BinExpr expr2 = new BinExpr(Operator.ADD, 
									new FieldRef("id"), 
									new IntegerValue(10));
		emit(new PUSHEXPR(expr2));
		emit(new PUSHEXPR("id2+20"));
		BinExpr expr3 = new BinExpr(Operator.ADD, 
									new FieldRef("id2"), 
									new IntegerValue(20));
		emit(new PUSHEXPR(expr3));
		emit(new PROJECTION(tmp(2), tmp(1), 2));
		emit(new SHOW(tmp(2)));

		emit(new DELTABLE(TEST_TABLE));
		emit(new COMMIT());
		exec();
		String expected = 
			"id+10|id2+20\n"+
			"11|31\n"+
			"12|22\n"+
			"13|26\n"+
			"14|34\n"
			;
		assertEquals(expected, console.getLog());
		reset();
	}
}

