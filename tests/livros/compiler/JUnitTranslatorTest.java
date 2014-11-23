package livros.compiler;

import livros.Debug;
import livros.Log;
import livros.db.DataBase;
import livros.storage.StorageManager;
import livros.vm.CLOSE;
import livros.vm.COMMIT;
import livros.vm.InstructionPrinter;
import livros.vm.INST;
import livros.vm.OPEN;
import livros.vm.SHOW;
import livros.vm.TRACE;
import livros.vm.VM;

import java.util.List;
import junit.framework.TestCase;

public class JUnitTranslatorTest extends TestCase
{
	String TMP_PREFIX = StorageManager.TMP_FILE_PREFIX;
	public JUnitTranslatorTest(String name) {
		super(name);
	}

	private AST parse(String sql) {
		Scanner scanner = new Scanner(sql);
		List tokens = scanner.scan();
		Parser parser = new Parser(tokens);
		AST ast = parser.parse();
		try {
			ASTPrinter printer = new ASTPrinter();
			ast.accept(printer);
			Log.d(printer.getResult());
			return ast;
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
			return null;
		}
	}

	private boolean AUTO_COMMIT = false;
	private List translate(String sql) {
		DataBase.NO_STORAGE = true;
		AST ast = parse(sql);
		Debug.assertTrue(ast != null);

		Translator translator = new Translator(AUTO_COMMIT);
		Translator.DEBUG = false;
		try {	
			ast.accept(translator);
			StorageManager.instance().cleanup();
			translator.dump();
			return translator.instructions();
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
			return null;
		}
	}

	public void testCreateTable() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("create table t (id integer primary key, name char(30) unique not null, text varchar(10))");
			assertEquals(4, insts.size());

			INST i0 = (INST)insts.get(0);
			INST i1 = (INST)insts.get(1);
			INST i2 = (INST)insts.get(2);
			INST i3 = (INST)insts.get(3);
			assertEquals("NEWFIELD @id INTEGER PRIMARY KEY", printer.toString(i0));
			assertEquals("NEWFIELD @name CHAR(30) UNIQUE NOT NULL", printer.toString(i1));
			assertEquals("NEWFIELD @text VARCHAR(10)", printer.toString(i2));
			assertEquals("NEWTABLE t", printer.toString(i3));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testDropTable() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("drop table t");
			assertEquals(1, insts.size());

			INST i0 = (INST)insts.get(0);
			assertEquals("DELTABLE t", printer.toString(i0));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testInsert1() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("insert into t (i1, i2) values (123, 456)");

			assertEquals(6, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR @i1", printer.toString(i));
 			i = (INST)insts.get(1);
			assertEquals("PUSHEXPR 123", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PUSHEXPR @i2", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("PUSHEXPR 456", printer.toString(i));
			i = (INST)insts.get(4);
			assertEquals("MAKEREC 2", printer.toString(i));
			i = (INST)insts.get(5);
			assertEquals("INSERT t", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testInsert2() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("insert into t (i1) values (123+456)");

			assertEquals(4, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR @i1", printer.toString(i));
 			i = (INST)insts.get(1);
			assertEquals("PUSHEXPR (ADD 123 456)", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("MAKEREC 1", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("INSERT t", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testDelete1() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("delete from t where x<0");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (LT @x 0)", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SUBTRACT t "+TMP_PREFIX+"0", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testUpdate1() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("update t set x = 1, y = 2 where z = 0");

			assertEquals(7, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (EQ @z 0)", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PUSHEXPR @x", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("PUSHEXPR 1", printer.toString(i));
			i = (INST)insts.get(4);
			assertEquals("PUSHEXPR @y", printer.toString(i));
			i = (INST)insts.get(5);
			assertEquals("PUSHEXPR 2", printer.toString(i));
			i = (INST)insts.get(6);
			assertEquals("UPDATE t "+TMP_PREFIX+"0 2", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect1() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t");

			assertEquals(1, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("SHOW t", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect2() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t where 1=1");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (EQ 1 1)", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect3() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t where name='foo'");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (EQ @name 'foo')", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testSelect4() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t where i=i2+2");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (EQ @i (ADD @i2 2))", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));

		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect_or() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t where i=1 or i=2");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (OR (EQ @i 1) (EQ @i 2))", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));
			/*
			assertEquals(10, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHVAL @i", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("PUSHVAL 1", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PUSHFUN EQ", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));

			i = (INST)insts.get(4);
			assertEquals("PUSHVAL @i", printer.toString(i));
			i = (INST)insts.get(5);
			assertEquals("PUSHVAL 2", printer.toString(i));
			i = (INST)insts.get(6);
			assertEquals("PUSHFUN EQ", printer.toString(i));
			i = (INST)insts.get(7);
			assertEquals("SELECT "+TMP_PREFIX+"1 t", printer.toString(i));

			i = (INST)insts.get(8);
			assertEquals("UNION "+TMP_PREFIX+"2 "+TMP_PREFIX+"1 "+TMP_PREFIX+"0", printer.toString(i));
			i = (INST)insts.get(9);
			assertEquals("SHOW "+TMP_PREFIX+"2", printer.toString(i));
			*/
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect_and() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t where 10<i and i<20");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (AND (LT 10 @i) (LT @i 20))", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect_and_or() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select * from t where 10<i and i<20 or i=100");

			assertEquals(3, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (OR (AND (LT 10 @i) (LT @i 20)) (EQ @i 100))", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect_projection1() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select id from t");

			assertEquals(4, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR 'id'", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("PUSHEXPR @id", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PROJECTION "+TMP_PREFIX+"0 t 1", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testSelect_projection2() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select id as Identifier from t");

			assertEquals(4, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR 'Identifier'", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("PUSHEXPR @id", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PROJECTION "+TMP_PREFIX+"0 t 1", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect_projection3() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select x*2 as x_times_two, x * 3 from t");

			assertEquals(6, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR 'x_times_two'", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("PUSHEXPR (MUL @x 2)", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PUSHEXPR 'x * 3'", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("PUSHEXPR (MUL @x 3)", printer.toString(i));
			i = (INST)insts.get(4);
			assertEquals("PROJECTION "+TMP_PREFIX+"0 t 2", printer.toString(i));
			i = (INST)insts.get(5);
			assertEquals("SHOW "+TMP_PREFIX+"0", printer.toString(i));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testSelect_projection4() {
		try {
			InstructionPrinter printer = new InstructionPrinter();
			List insts = translate("select x*x from t where x<0");

			assertEquals(6, insts.size());

			INST i = (INST)insts.get(0);
			assertEquals("PUSHEXPR (LT @x 0)", printer.toString(i));
			i = (INST)insts.get(1);
			assertEquals("SELECT "+TMP_PREFIX+"0 t", printer.toString(i));
			i = (INST)insts.get(2);
			assertEquals("PUSHEXPR 'x * x'", printer.toString(i));
			i = (INST)insts.get(3);
			assertEquals("PUSHEXPR (MUL @x @x)", printer.toString(i));
			i = (INST)insts.get(4);
			assertEquals("PROJECTION "+TMP_PREFIX+"1 "+TMP_PREFIX+"0 1", printer.toString(i));
			i = (INST)insts.get(5);
			assertEquals("SHOW "+TMP_PREFIX+"1", printer.toString(i));
		} catch (Exception ex) {	
			ex.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void test_0() {
		translate("SELECT name, age FROM student WHERE department = 'Mathematics' OR department = 'Physics'");
	}
}

