package livros;

import livros.Log;
import livros.compiler.AST;
import livros.storage.StorageManager;
import java.io.File;
import java.util.List;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JUnitLivrosTest extends TestCase
{
	public JUnitLivrosTest(String name) {
		super(name);
	}

	public void setUp() {
		delete(StorageManager.DB_FILE);
		delete("t");
		delete("u");
	}

	void delete(String name) {
		File file = new File(StorageManager.DB_DIR+name);
		if (file.exists()) {
			file.delete();
		}
	}

	Livros livros = new Livros();	
	public void exec(String sql) {
		List tokens = livros.scan(sql);
		AST ast = livros.parse(tokens);
		List insts = livros.translate(ast);
		livros.exec(insts);
	}

	private void setUpTable() {
		exec("create table t (num int primary key, text varchar(16))");
		exec("insert into t (num, text) values (0, 'zero')");
		exec("insert into t (num, text) values (1, 'one')");
		exec("insert into t (num, text) values (2, 'two')");
		exec("insert into t (num, text) values (3, 'three')");
		exec("insert into t (num, text) values (4, 'four')");
		exec("insert into t (num, text) values (5, 'five')");
		exec("insert into t (num, text) values (6, 'six')");
		exec("insert into t (num, text) values (7, 'seven')");
		exec("insert into t (num, text) values (8, 'eight')");
		exec("insert into t (num, text) values (9, 'nine')");
		exec("insert into t (num) values (10)");
	}

	private void deleteTable() {
		exec("drop table t");
		exec("commit");
	}

	public void testCreateTable() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		exec("create table t (i1 int, i2 int, i3 int)");
		exec("select * from t");
		String expected = 
			"i1|i2|i3\n"
			;
		assertEquals(expected, console.getLog());
		exec("drop table t");
		exec("commit");
	}

	public void testInsert1() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		exec("create table t (i1 int, t2 char(3), t3 varchar(4))");
		exec("insert into t values (1, 'aaaa', 'AAAAA')");
		exec("insert into t values (' ', 'bbb', 'bbb')");//error
		exec("insert into t values (1, 2, 3)");//error
		exec("insert into t values (2, 'bb', 'BB')");
		exec("select * from t");
		String expected = 
			"i1|t2|t3\n"+
			"1|'aaa'|'AAAA'\n"+
			"2|'bb '|'BB'\n"
			;
		assertEquals(expected, console.getLog());
		exec("drop table t");
		exec("commit");
	}

	public void testInsert2() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		exec("create table t (i1 int, i2 int default -1)");
		exec("insert into t (i2) values (0)");
		exec("insert into t (i1) values (0)");
		exec("select * from t");
		String expected = 
			"i1|i2\n"+
			"NULL|0\n"+
			"0|-1\n"
			;
		assertEquals(expected, console.getLog());
		exec("drop table t");
		exec("commit");
	}

	public void testConstraint1() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		exec("create table t (i1 int primary key, i2 int unique , i3 int not null)");
		exec("insert into t (i2, i3) values (0, 0)");//error
		exec("insert into t (i1, i2) values (0, 0)");//error
		exec("insert into t values (0, 0, 0)");
		exec("insert into t values (0, 1, 1)");//error
		exec("insert into t values (1, 0, 1)");//error
		exec("insert into t values (1, 1, 1)");

		exec("select * from t");
		String expected = 
			"i1|i2|i3\n"+
			"0|0|0\n"+
			"1|1|1\n"
			;
		assertEquals(expected, console.getLog());
		exec("drop table t");
		exec("commit");
	}

	public void testConstraint2() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		exec("create table t (i1 int primary key, i2 int)");
		exec("create table u (i1 int references t(i1))");
		exec("insert into t values (0, 0)");
		exec("insert into t values (1, 1)");
		exec("insert into u values (0)");
		exec("insert into u values (0)");
		exec("insert into u values (1)");
		exec("insert into u values (2)");//error
		exec("update u set i1 = 3 where i1 = 0");//error

		exec("delete from t where i1 = 0");//error
		exec("delete from t where i1 = 1");//error
		exec("update t set i1 = 2 where i1 = 0");//error
		exec("update t set i1 = 2 where i1 = 1");//error

		//del refrence
		exec("delete from u where i1 = 0");
		exec("delete from u where i1 = 1");
		
		//can update & delete 
		exec("update t set i1 = 2 where i1 = 0");
		exec("delete from t where i1 = 1");

		exec("insert into u values (2)");
		
		exec("select * from t");
		exec("select * from u");

		String expected = 
			"i1|i2\n"+
			"2|0\n"+
			"i1\n"+
			"2\n"
			;
		assertEquals(expected, console.getLog());

		exec("drop table u");
		exec("drop table t");
		exec("commit");
	}
	
	public void testSelect1() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		
		setUpTable();
		exec("select * from t)");
		exec("select text, num from t)");
		
		String expected = 
			"num|text\n"+
			"0|'zero'\n" + 
			"1|'one'\n" + 
			"2|'two'\n" + 
			"3|'three'\n" + 
			"4|'four'\n" + 
			"5|'five'\n" + 
			"6|'six'\n" + 
			"7|'seven'\n" + 
			"8|'eight'\n" + 
			"9|'nine'\n" +
			"10|NULL\n"+
			"text|num\n" + 
			"'zero'|0\n" + 
			"'one'|1\n" + 
			"'two'|2\n" + 
			"'three'|3\n" + 
			"'four'|4\n" + 
			"'five'|5\n" + 
			"'six'|6\n" + 
			"'seven'|7\n" + 
			"'eight'|8\n" + 
			"'nine'|9\n" + 
			"NULL|10\n"
			;
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testSelect2() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		
		setUpTable();
		exec("select * from t where num = 1 OR num = 3 OR num = 5 OR text = 'seven')");
		
		String expected = 
			"num|text\n"+
			"1|'one'\n" + 
			"3|'three'\n" + 
			"5|'five'\n" + 
			"7|'seven'\n"
			;
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testSelect3() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		
		setUpTable();
		exec("select * from t where num <> 1 AND num <> 3 AND num <> 5 AND text <> 'seven')");

		String expected = 
			"num|text\n"+
			"0|'zero'\n" + 
			"2|'two'\n" + 
			"4|'four'\n" + 
			"6|'six'\n" + 
			"8|'eight'\n" + 
			"9|'nine'\n"
			;
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testSelect4() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		
		setUpTable();
		exec("select * from t where num < 3 OR num > 7)");

		String expected = 
			"num|text\n"+
			"0|'zero'\n" + 
			"1|'one'\n" + 
			"2|'two'\n" + 
			"8|'eight'\n" + 
			"9|'nine'\n" +
			"10|NULL\n"
			;
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testSelect5() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		
		setUpTable();
		exec("select * from t where num <= 3 OR num >= 7)");

		String expected = 
			"num|text\n"+
			"0|'zero'\n" + 
			"1|'one'\n" + 
			"2|'two'\n" + 
			"3|'three'\n" + 
			"7|'seven'\n" +
			"8|'eight'\n" + 
			"9|'nine'\n" +
			"10|NULL\n"
			;
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testSelect6() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		
		setUpTable();
		exec("select * from t where text is NULL)");
		exec("select * from t where text is NOT NULL)");
		
		String expected = 
			"num|text\n"+
			"10|NULL\n"+
			"num|text\n"+
			"0|'zero'\n" + 
			"1|'one'\n" + 
			"2|'two'\n" + 
			"3|'three'\n" + 
			"4|'four'\n" + 
			"5|'five'\n" + 
			"6|'six'\n" + 
			"7|'seven'\n" + 
			"8|'eight'\n" + 
			"9|'nine'\n"
			;
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testUpdate() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);
		setUpTable();

		exec("update t set text='eleven', num = 11 where num=10");
		exec("select * from t where num = 11");

		String expected = 
			"num|text\n"+
			"11|'eleven'\n";
		assertEquals(expected, console.getLog());
		deleteTable();
	}
	
	public void testDelete() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		setUpTable();
		exec("delete from t where text is null or text = 'six'");
		exec("select * from t");

		String expected = 
			"num|text\n"+
			"0|'zero'\n" + 
			"1|'one'\n" + 
			"2|'two'\n" + 
			"3|'three'\n" + 
			"4|'four'\n" + 
			"5|'five'\n" + 
			"7|'seven'\n" + 
			"8|'eight'\n" + 
			"9|'nine'\n";

		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testProjection() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		setUpTable();
		exec("select num, num*num as power from t");
		String expected = 
			"num|power\n"+
			"0|0\n" + 
			"1|1\n" + 
			"2|4\n" + 
			"3|9\n" + 
			"4|16\n" + 
			"5|25\n" + 
			"6|36\n" + 
			"7|49\n" + 
			"8|64\n" + 
			"9|81\n" +
			"10|100\n";
		assertEquals(expected, console.getLog());
		deleteTable();
	}

	public void testSelect_text() {
		LogConsole console = new LogConsole();
		Livros.setConsole(console);

		setUpTable();
		exec("select * from t where text > 's'");

		String expected = 
			"num|text\n"+
			"0|'zero'\n" + 
			"2|'two'\n" + 
			"3|'three'\n"+
			"6|'six'\n" + 
			"7|'seven'\n";

		assertEquals(expected, console.getLog());
		deleteTable();
	}
}
