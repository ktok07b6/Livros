package livros.db;

import livros.Debug;
import livros.Log;

public class ExprEvaluator implements ExprVisitor
{
	Record mRecord;

	public Value eval(Expr expr, Record r) {
		Log.v("ExprEvaluator#eval "+expr.toString());
		mRecord = r;
		return (Value)expr.accept(this);
	}

	public Object visit(Value val) {
		Log.v("Value "+val.toString());
		if (val.isFieldRef()) {
			FieldRef ref = val.asFieldRef();
			Value ret = mRecord.get(ref.fieldName());
			Debug.assertTrue(ret != null);
			return ret;
		} else {
			return val;
		}
	}

	public Object visit(BinExpr exp) {
		Log.v("BinExpr "+exp.toString());
		Value lv = (Value)exp.mLeft.accept(this);
		Value rv = (Value)exp.mRight.accept(this);

		//check operand type
		if (lv.isText() && rv.isText()) {
		} else if (!lv.type().equals(rv.type()) && 
				   !lv.type().isNull() && !rv.type().isNull()) {
			Log.e("operands type is invalid!!!");
			return Value.nullValue;
		}

		if (Operator.isLogicalOp(exp.mOp)) {
			return evalBoolExpr(exp.mOp, lv.asBool(), rv.asBool());
		}
		if (lv.isNull() || rv.isNull()) {
			return evalNullExpr(exp.mOp, lv, rv);
		}
		if (lv.isInteger() && rv.isInteger()) {
			return evalIntExpr(exp.mOp, lv.asInteger(), rv.asInteger());
		}
		if (lv.isText() && rv.isText()) {
			return evalTextExpr(exp.mOp, lv.asText(), rv.asText());
		}
		return null;
	}

	public static Value evalIntExpr(int op, IntegerValue li, IntegerValue ri) {
		Log.v("evalInt "+ Operator.opName(op) + " "+li + " " + ri);
		int l = li.intValue();
		int r = ri.intValue();
		switch (op) {
		case Operator.LT:
			return l < r ? Value.trueValue : Value.falseValue;
		case Operator.GT:
			return l > r ? Value.trueValue : Value.falseValue;
		case Operator.LE:
			return l <= r ? Value.trueValue : Value.falseValue;
		case Operator.GE:
			return l >= r ? Value.trueValue : Value.falseValue;
		case Operator.EQ:
		case Operator.IS:
			return l == r ? Value.trueValue : Value.falseValue;
		case Operator.NE:
		case Operator.IS_NOT:
			return l != r ? Value.trueValue : Value.falseValue;
		case Operator.ADD:
			return new IntegerValue(l + r);
		case Operator.SUB:
			return new IntegerValue(l - r);
		case Operator.MUL:
			return new IntegerValue(l * r);
		case Operator.DIV:
			return new IntegerValue(l / r);
		default:
			Debug.assertTrue(false);
			return null;
		}
	}

	public static Value evalTextExpr(int op, TextValue lt, TextValue rt) {
		Log.v("evalText "+ Operator.opName(op) + " " + lt + " " + rt);
		String l = lt.textValue().trim();
		String r = rt.textValue().trim();
		switch (op) {
		case Operator.LT:
			return l.compareTo(r) < 0 ? Value.trueValue : Value.falseValue;
		case Operator.GT:
			return l.compareTo(r) > 0 ? Value.trueValue : Value.falseValue;
		case Operator.LE:
			return (l.compareTo(r) < 0 || l.equals(r)) ? Value.trueValue : Value.falseValue;
		case Operator.GE:
			return (l.compareTo(r) > 0 || l.equals(r)) ? Value.trueValue : Value.falseValue;
		case Operator.EQ:
		case Operator.IS:
			return l.equals(r) ? Value.trueValue :  Value.falseValue;
		case Operator.NE:
		case Operator.IS_NOT:
			return l.equals(r) ? Value.falseValue : Value.trueValue;
		case Operator.CONTAINS:
			return l.contains(r) ? Value.trueValue : Value.falseValue;
		case Operator.STARTSWITH:
			return l.startsWith(r) ? Value.trueValue : Value.falseValue;
		case Operator.ENDSWITH:
			return l.endsWith(r) ? Value.trueValue : Value.falseValue;
		default:
			Debug.assertTrue(false);
			return null;
		}
	}

	public static Value evalBoolExpr(int op, BoolValue lb, BoolValue rb) {
		Log.v("evalBool "+ Operator.opName(op) + " "+lb + " " + rb);
		switch (op) {
		case Operator.OR:
			if (lb.isTrue()) {
				return Value.trueValue;
			} else if (lb.isFalse()) {
				return rb;
			} else if (lb.isUnknown()) {
				if (rb.isTrue()) {
					return Value.trueValue;
				} else {
					return Value.unknownValue;
				}
			}
			break;
		case Operator.AND:
			if (lb.isTrue()) {
				return rb;
			} else if (lb.isFalse()) {
				return Value.falseValue;
			} else if (lb.isUnknown()) {
				if (rb.isFalse()) {
					return Value.falseValue;
				} else {
					return Value.unknownValue;
				}
			}
			break;
		}

		Debug.assertTrue(false);
		return null;
	}

	public static Value evalNullExpr(int op, Value lv, Value rv) {
		Log.v("evalNull "+ Operator.opName(op) + " "+lv + " " + rv);
		if (lv.isNull() && rv.isNull()) {
			switch (op) {
			case Operator.IS:
				return Value.trueValue;
			case Operator.IS_NOT:
				return Value.falseValue;
			default:
				return Value.unknownValue;
			}
		} else {
			switch (op) {
			case Operator.IS:
				return Value.falseValue;
			case Operator.IS_NOT:
				return Value.trueValue;
			default:
				return Value.unknownValue;
			}
		}
	}

	public Object visit(UnExpr exp) {
		Log.v("UnExpr "+exp);
		Value v = (Value)exp.mExpr.accept(this);
		if (exp.mOp == Operator.NEGA) {
			if (v.isInteger()) {
				int i = v.asInteger().intValue();
				return new IntegerValue(-i);
			} else if (v.isText()) {
				return new IntegerValue(0);
			} else {
				return Value.nullValue;
			}
		} else if (exp.mOp == Operator.NOT) {
			BoolValue b = v.asBool();
			if (b.isTrue()) {
				return Value.falseValue;
			} else if (b.isFalse()) {
				return Value.trueValue;
			} else {
				return Value.unknownValue;
			}
		}
		return Value.nullValue;
	}
}
