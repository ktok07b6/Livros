package livros.compiler;

import livros.Log;
import java.util.List;
import junit.framework.TestCase;

public class JUnitParserTest extends TestCase
{
	public JUnitParserTest(String name) {
		super(name);
	}

	public void testTableDefinition() {
		String sql;
		AST ast;
		//data type
		sql = "CREATE TABLE t1 (i integer)";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (i INTEGER)");

		sql = "CREATE TABLE t1 (i int)";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (i INTEGER)");

		sql = "CREATE TABLE t1 (t char(100))";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (t CHAR(100))");

		sql = "CREATE TABLE t1 (t character(100))";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (t CHAR(100))");

		sql = "CREATE TABLE t1 (t varchar(100))";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (t VARCHAR(100))");

		//constraint
		sql = "CREATE TABLE t1 (i int primary key unique not null references base(id))";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (i INTEGER PRIMARY KEY UNIQUE NOT NULL REFERENCES base (id))");

		//Default
		sql = "CREATE TABLE t1 (i int default -1)";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (i INTEGER DEFAULT -1)");

		sql = "CREATE TABLE t1 (t char(10) default 'text')";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (t CHAR(10) DEFAULT 'text')");

		//Multi field
		sql = "CREATE TABLE t1 ( i1 int , i2 int , i3 int )";
		ast = parse(sql);
		assertTrue(ast instanceof TableDefinition);
		assertEquals(ast.toString(), "CREATE TABLE t1 (i1 INTEGER, i2 INTEGER, i3 INTEGER)");
	}

	public void testInsertStatement() {
		String sql;
		AST ast;
		sql = "INSERT INTO t1 (id, name, email) VALUES(0, 'this is name', 'name@domain.com');";
		ast = parse(sql);
		assertTrue(ast instanceof InsertStatement);
		assertEquals(ast.toString(), "INSERT INTO t1 (id, name, email) VALUES (0, 'this is name', 'name@domain.com')");

		sql = "INSERT INTO t1 VALUES(0, 'this is name', 'name@domain.com');";
		ast = parse(sql);
		assertTrue(ast instanceof InsertStatement);
		assertEquals(ast.toString(), "INSERT INTO t1 VALUES (0, 'this is name', 'name@domain.com')");

	}

	public void testDropTableStatement() {
		String sql;
		AST ast;
		sql = "DROP TABLE t1";
		ast = parse(sql);
		assertTrue(ast instanceof DropTableStatement);
		assertEquals(ast.toString(), "DROP TABLE t1");
	}

	public void testDeleteStatement() {
		String sql;
		AST ast;
		sql = "DELETE FROM t1 WHERE i1 = 0";
		ast = parse(sql);
		assertTrue(ast instanceof DeleteStatement);
		assertEquals(ast.toString(), "DELETE FROM t1 WHERE i1 = 0");
	}

	public void testUpdateStatement() {
		String sql;
		AST ast;
		sql = "UPDATE t1 SET i1 = 1 WHERE i1 = 0";
		ast = parse(sql);
		assertTrue(ast instanceof UpdateStatement);
		assertEquals(ast.toString(), "UPDATE t1 SET i1 = 1 WHERE i1 = 0");

		sql = "UPDATE t1 SET i1 = 1, i2 = 2, i3 = 3 WHERE i1 = 0";
		ast = parse(sql);
		assertTrue(ast instanceof UpdateStatement);
		assertEquals(ast.toString(), "UPDATE t1 SET i1 = 1, i2 = 2, i3 = 3 WHERE i1 = 0");

	}

	public void testSelectStatemenet() {
		String sql;
		AST ast;
		sql = "SELECT * FROM t1";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1");

		sql = "SELECT i1,i2,i3 FROM t1";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT i1, i2, i3 FROM t1");

		sql = "SELECT i1 as value FROM t1";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT i1 AS value FROM t1");

		sql = "SELECT i1+i2 as value FROM t1";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT i1 + i2 AS value FROM t1");

		sql = "SELECT i1+i2 as value, i3*i4 as value2 FROM t1";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT i1 + i2 AS value, i3 * i4 AS value2 FROM t1");

		//condition
		sql = "SELECT * FROM t1 WHERE i = -1";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE i = -1");

		sql = "SELECT * FROM t1 WHERE +1 = i";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE 1 = i");

		sql = "SELECT * FROM t1 WHERE 0<i0 AND 1>i1 OR 2>=i2 AND 3<=i3 OR 4<>i4";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE 0 < i0 AND 1 > i1 OR 2 >= i2 AND 3 <= i3 OR 4 <> i4");

		sql = "SELECT * FROM t1 WHERE i0+i1-i2*i3/i4=0";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE i0 + i1 - i2 * i3 / i4 = 0");

		//null check
		sql = "SELECT * FROM t1 WHERE i0 IS NULL";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE i0 IS NULL");

		sql = "SELECT * FROM t1 WHERE i0 IS NOT NULL";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE i0 IS NOT NULL");

		sql = "SELECT * FROM t1 WHERE NOT i0 IS NOT NULL";
		ast = parse(sql);
		assertTrue(ast instanceof SelectStatement);
		assertEquals(ast.toString(), "SELECT * FROM t1 WHERE NOT i0 IS NOT NULL");
	}

	AST parse(String sql) {
		Scanner scanner = new Scanner(sql);
		List tokens = scanner.scan();
		Parser parser = new Parser(tokens);
		return parser.parse();
	}
}
