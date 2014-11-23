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

public class JUnitExprTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitExprTest(String name) {
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

	public void testValue() {
		Value v = new IntegerValue(10);
		assertTrue(v.isInteger());
		assertTrue(!v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(!v.isNull());
		assertTrue(!v.isReference());
		assertTrue(v.asInteger().intValue() == 10);
		assertTrue(v.hashCode() == 10);
		assertTrue(v.equals(new IntegerValue(10)));
		assertTrue(!v.equals(new IntegerValue(1)));
		assertTrue(new IntegerValue(1).asBool().isTrue());
		assertTrue(new IntegerValue(0).asBool().isFalse());
		assertTrue(new IntegerValue(-1).asBool().isTrue());
		
		v = new TextValue("text");
		assertTrue(!v.isInteger());
		assertTrue(v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(!v.isNull());
		assertTrue(!v.isReference());
		assertTrue(v.asText().textValue().equals("text"));
		assertTrue(v.type().hashCode() == 'T');
		assertTrue(v.asBool().isTrue());

		v = new FixedCharValue(5, "text");
		assertTrue(!v.isInteger());
		assertTrue(v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(!v.isNull());
		assertTrue(!v.isReference());
		assertTrue(v.asText().textValue().equals("text "));
		assertTrue(v.asFixedChar().textValue().equals("text "));
		assertTrue(v.type().hashCode() == 'F');
		assertTrue(v.asBool().isTrue());

		v = new VarCharValue(3, "text");
		assertTrue(!v.isInteger());
		assertTrue(v.isText());
		assertTrue(v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(!v.isNull());
		assertTrue(!v.isReference());
		assertTrue(v.asText().textValue().equals("tex"));
		assertTrue(v.asVarChar().textValue().equals("tex"));
		assertTrue(v.type().hashCode() == 'V');
		assertTrue(v.asBool().isTrue());

		v = Value.trueValue;
		assertTrue(!v.isInteger());
		assertTrue(!v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(v.isBool());
		assertTrue(!v.isNull());
		assertTrue(!v.isReference());
		assertTrue(v.asBool().isTrue());
		assertTrue(v.equals(new BoolValue(BoolValue.TRUE)) == false);
		assertTrue(v.equals(Value.falseValue) == false);

		v = Value.nullValue;
		assertTrue(!v.isInteger());
		assertTrue(!v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(v.isNull());
		assertTrue(!v.isReference());

		v = new RefValue(new IntegerValue(10), 5);
		assertTrue(v.isInteger());
		assertTrue(!v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(!v.isNull());
		assertTrue(v.isReference());
		assertTrue(v.asInteger().intValue() == 10);
		assertTrue(v.equals(new IntegerValue(10)) == true);
		assertTrue(v.equals(new RefValue(new IntegerValue(10), 5)));

		v = new FieldRef("id");
		assertTrue(v.isFieldRef());
		assertTrue(!v.isInteger());
		assertTrue(!v.isText());
		assertTrue(!v.isVarChar());
		assertTrue(!v.isFixedChar());
		assertTrue(!v.isBool());
		assertTrue(!v.isNull());
		assertTrue(!v.isReference());
		assertTrue(v.asFieldRef().fieldName().equals("id"));
		assertTrue(v.equals(new FieldRef("id")));
		assertTrue(!v.equals(new FieldRef("tab", "id")));
		assertTrue(new FieldRef("tab", "id").equals(new FieldRef("tab", "id")));
	}

	public void testIntegerEval() {
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

		Record r = t1.createRecord();
		r.set("id", new IntegerValue(10));
		
		ExprEvaluator eval = new ExprEvaluator();
		Expr expr = new IntegerValue(5);
		Value b = eval.eval(expr, r);
		assertTrue(b.equals(expr));

		expr = new FieldRef("id");
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(10)));

		expr = new BinExpr(Operator.ADD, new IntegerValue(100), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(110)));

		expr = new BinExpr(Operator.SUB, new IntegerValue(100), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(90)));

		expr = new BinExpr(Operator.MUL, new IntegerValue(100), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(1000)));

		expr = new BinExpr(Operator.DIV, new IntegerValue(100), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(10)));

		expr = new UnExpr(Operator.NEGA, new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(-10)));

		expr = new UnExpr(Operator.NEGA, new IntegerValue(-10));
		b = eval.eval(expr, r);
		assertTrue(b.equals(new IntegerValue(10)));


		expr = new BinExpr(Operator.EQ, new IntegerValue(100), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));
		expr = new BinExpr(Operator.EQ, new IntegerValue(10), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.NE, new IntegerValue(100), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.NE, new IntegerValue(10), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));

		expr = new BinExpr(Operator.LT, new IntegerValue(10), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));
		expr = new BinExpr(Operator.LT, new IntegerValue(9), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.GT, new IntegerValue(10), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));
		expr = new BinExpr(Operator.GT, new IntegerValue(11), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.LE, new IntegerValue(9), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.LE, new IntegerValue(10), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.GE, new IntegerValue(11), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.GE, new IntegerValue(10), new FieldRef("id"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.EQ, 
						   new BinExpr(Operator.SUB, new IntegerValue(25), new FieldRef("id")),
						   new BinExpr(Operator.ADD, new IntegerValue(5), new FieldRef("id")));

		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		db.delTable(t1);
	}

	public void testTextEval() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				Field nameField = new Field("name", new VarCharType(10));
				t1.addField(nameField);
				assertEquals(nameField, t1.field("name"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record r = t1.createRecord();
		r.set("name", new VarCharValue(10, "abc"));
		
		ExprEvaluator eval = new ExprEvaluator();
		Expr expr = new VarCharValue(10, "a");
		Value b = eval.eval(expr, r);
		assertTrue(b.equals(expr));

		expr = new FieldRef("name");
		b = eval.eval(expr, r);
		assertTrue(b.equals(new VarCharValue(10, "abc")));

		expr = new BinExpr(Operator.EQ, new VarCharValue(10, "ab"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));
		expr = new BinExpr(Operator.EQ, new VarCharValue(10, "abc"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.NE, new VarCharValue(10, "ab"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.NE, new VarCharValue(10, "abc"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));

		expr = new BinExpr(Operator.LT, new VarCharValue(10, "abc"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));
		expr = new BinExpr(Operator.LT, new VarCharValue(10, "ab"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.GT, new VarCharValue(10, "abc"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));
		expr = new BinExpr(Operator.GT, new VarCharValue(10, "abcd"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.LE, new VarCharValue(10, "abc"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.LE, new VarCharValue(10, "ab"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.GE, new VarCharValue(10, "abc"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.GE, new VarCharValue(10, "abcd"), new FieldRef("name"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));

		expr = new BinExpr(Operator.CONTAINS, new FieldRef("name"), new VarCharValue(10, "abc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.CONTAINS, new FieldRef("name"), new VarCharValue(10, "c"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.CONTAINS, new FieldRef("name"), new VarCharValue(10, "bc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.CONTAINS, new FieldRef("name"), new VarCharValue(10, "Abc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));

		expr = new BinExpr(Operator.STARTSWITH, new FieldRef("name"), new VarCharValue(10, "abc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.STARTSWITH, new FieldRef("name"), new VarCharValue(10, "a"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.STARTSWITH, new FieldRef("name"), new VarCharValue(10, "bc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));

		expr = new BinExpr(Operator.ENDSWITH, new FieldRef("name"), new VarCharValue(10, "abc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.ENDSWITH, new FieldRef("name"), new VarCharValue(10, "bc"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.trueValue));
		expr = new BinExpr(Operator.ENDSWITH, new FieldRef("name"), new VarCharValue(10, "ab"));
		b = eval.eval(expr, r);
		assertTrue(b.equals(Value.falseValue));

		db.delTable(t1);
	}

	public void testLogicalOp() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				Field idField = new Field("id", Type.integerType);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));
				Field textField = new Field("text", new VarCharType(10));
				t1.addField(textField);
				assertEquals(textField, t1.field("text"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record r = t1.createRecord();
		r.set("id", new IntegerValue(10));
		r.set("text", new VarCharValue(10, "foo"));
		
		ExprEvaluator eval = new ExprEvaluator();
		//boolean OR
		Expr expr = new BinExpr(Operator.OR, Value.trueValue, Value.trueValue);
		Value b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, Value.trueValue, Value.falseValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, Value.trueValue, Value.unknownValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.OR, Value.falseValue, Value.trueValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, Value.falseValue, Value.falseValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.OR, Value.falseValue, Value.unknownValue);
		b = eval.eval(expr, r);
		assertEquals(Value.unknownValue, b);

		expr = new BinExpr(Operator.OR, Value.unknownValue, Value.falseValue);
		b = eval.eval(expr, r);
		assertEquals(Value.unknownValue, b);
		expr = new BinExpr(Operator.OR, Value.unknownValue, Value.trueValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, Value.unknownValue, Value.unknownValue);
		b = eval.eval(expr, r);
		assertEquals(Value.unknownValue, b);

		//boolean AND
		expr = new BinExpr(Operator.AND, Value.trueValue, Value.trueValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.AND, Value.trueValue, Value.falseValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.AND, Value.trueValue, Value.unknownValue);
		b = eval.eval(expr, r);
		assertEquals(Value.unknownValue, b);

		expr = new BinExpr(Operator.AND, Value.falseValue, Value.trueValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.AND, Value.falseValue, Value.falseValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.AND, Value.falseValue, Value.unknownValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		expr = new BinExpr(Operator.AND, Value.unknownValue, Value.trueValue);
		b = eval.eval(expr, r);
		assertEquals(Value.unknownValue, b);
		expr = new BinExpr(Operator.AND, Value.unknownValue, Value.falseValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.AND, Value.unknownValue, Value.unknownValue);
		b = eval.eval(expr, r);
		assertEquals(Value.unknownValue, b);

		//integer OR
		expr = new BinExpr(Operator.OR, new IntegerValue(1), new IntegerValue(1));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, new IntegerValue(1), new IntegerValue(0));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, new IntegerValue(0), new IntegerValue(1));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.OR, new IntegerValue(0), new IntegerValue(0));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		//integer AND
		expr = new BinExpr(Operator.AND, new IntegerValue(1), new IntegerValue(1));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);
		expr = new BinExpr(Operator.AND, new IntegerValue(1), new IntegerValue(0));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.AND, new IntegerValue(0), new IntegerValue(1));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
		expr = new BinExpr(Operator.AND, new IntegerValue(0), new IntegerValue(0));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);
	
		db.delTable(t1);
	}

	public void testNULL() {
		DataBase db = DataBase.open(TEST_DB);
		Table t1 = new Table(TEST_DB, TEST_TABLE1);
		try {
			{
				Field idField = new Field("id", Type.integerType);
				t1.addField(idField);
				assertEquals(idField, t1.field("id"));

				Field fcharField = new Field("fchar", new FixedCharType(10));
				t1.addField(fcharField);
				assertEquals(fcharField, t1.field("fchar"));

				Field vcharField = new Field("vchar", new VarCharType(10));
				t1.addField(vcharField);
				assertEquals(vcharField, t1.field("vchar"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
		t1.init();
		db.addTable(t1);

		Record r = t1.createRecord();
		r.set("id", Value.nullValue);
		r.set("fchar", Value.nullValue);
		r.set("vchar", Value.nullValue);
		
		ExprEvaluator eval = new ExprEvaluator();

		//Integer vs NULL
		Expr expr = null;
		Value b = null;
		int op[] = {Operator.EQ, Operator.NE, 
					Operator.LT, Operator.GT, 
					Operator.LE, Operator.GE,
					Operator.ADD, Operator.SUB,
					Operator.MUL, Operator.DIV};
		for (int i = 0; i < op.length; i++) {
			expr = new BinExpr(op[i], new FieldRef("id"), new IntegerValue(0));
			b = eval.eval(expr, r);
			assertEquals(Value.unknownValue, b);
		}

		for (int i = 0; i < op.length; i++) {
			expr = new BinExpr(op[i], new FieldRef("id"), Value.nullValue);
			b = eval.eval(expr, r);
			assertEquals(Value.unknownValue, b);
		}

		expr = new BinExpr(Operator.IS, new FieldRef("id"), new IntegerValue(0));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		expr = new BinExpr(Operator.IS, new FieldRef("id"), Value.nullValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.IS_NOT, new FieldRef("id"), new IntegerValue(0));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.IS_NOT, new FieldRef("id"), Value.nullValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		//FixedChar vs NULL
		for (int i = 0; i < op.length; i++) {
			expr = new BinExpr(op[i], new FieldRef("fchar"), new FixedCharValue(10, "bar"));
			b = eval.eval(expr, r);
			assertEquals(Value.unknownValue, b);
		}

		for (int i = 0; i < op.length; i++) {
			expr = new BinExpr(op[i], new FieldRef("fchar"), Value.nullValue);
			b = eval.eval(expr, r);
			assertEquals(Value.unknownValue, b);
		}

		expr = new BinExpr(Operator.IS, new FieldRef("fchar"), new FixedCharValue(10, "bar"));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		expr = new BinExpr(Operator.IS, new FieldRef("fchar"), Value.nullValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.IS_NOT, new FieldRef("fchar"), new FixedCharValue(10, "bar"));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.IS_NOT, new FieldRef("fchar"), Value.nullValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		//VarChar vs NULL
		for (int i = 0; i < op.length; i++) {
			expr = new BinExpr(op[i], new FieldRef("vchar"), new VarCharValue(10, "bar"));
			b = eval.eval(expr, r);
			assertEquals(Value.unknownValue, b);
		}

		for (int i = 0; i < op.length; i++) {
			expr = new BinExpr(op[i], new FieldRef("vchar"), Value.nullValue);
			b = eval.eval(expr, r);
			assertEquals(Value.unknownValue, b);
		}

		expr = new BinExpr(Operator.IS, new FieldRef("vchar"), new VarCharValue(10, "bar"));
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		expr = new BinExpr(Operator.IS, new FieldRef("vchar"), Value.nullValue);
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.IS_NOT, new FieldRef("vchar"), new VarCharValue(10, "bar"));
		b = eval.eval(expr, r);
		assertEquals(Value.trueValue, b);

		expr = new BinExpr(Operator.IS_NOT, new FieldRef("vchar"), Value.nullValue);
		b = eval.eval(expr, r);
		assertEquals(Value.falseValue, b);

		db.delTable(t1);
	}

	public void testConstantFolding1() {
		ConstantFolding cf = new ConstantFolding();

		Expr expr = new IntegerValue(5);
		Expr b = cf.process(expr);
		assertTrue(b.equals(expr));

		expr = new FieldRef("id");
		b = cf.process(expr);
		assertTrue(b.equals(expr));

		expr = new BinExpr(Operator.ADD, new IntegerValue(100), new FieldRef("id"));
		b = cf.process(expr);
		assertTrue(b.equals(expr));

		expr = new BinExpr(Operator.ADD, new IntegerValue(100), new IntegerValue(100));
		b = cf.process(expr);
		assertTrue(b.equals(new IntegerValue(200)));

		expr = new UnExpr(Operator.NEGA, new FieldRef("id"));
		b = cf.process(expr);
		assertTrue(b.equals(expr));

		expr = new UnExpr(Operator.NEGA, new IntegerValue(-10));
		b = cf.process(expr);
		assertTrue(b.equals(new IntegerValue(10)));

		expr = new BinExpr(Operator.EQ, 
						   new BinExpr(Operator.SUB, new IntegerValue(25), new FieldRef("id")),
						   new BinExpr(Operator.ADD, new IntegerValue(5), new FieldRef("id")));

		b = cf.process(expr);

		expr = new BinExpr(Operator.MUL, 
						   new BinExpr(Operator.ADD, 
									   new IntegerValue(1), 
									   new IntegerValue(2)),
						   new BinExpr(Operator.ADD, 
									   new IntegerValue(3), 
									   new IntegerValue(4)));
		b = cf.process(expr);
		assertTrue(b.equals(new IntegerValue((1+2) * (3+4))));
	}
}
