package livros.db;

public class ConstantFolding implements ExprVisitor
{
	public ConstantFolding() {
	}

	public Expr process(Expr expr) {
		return (Expr)expr.accept(this);
	}

	public Object visit(Value v) {
		return v;
	}

	public Object visit(BinExpr expr) {
		Expr le = (Expr)expr.mLeft.accept(this);
		Expr re = (Expr)expr.mRight.accept(this);
		if (le.isValue() && re.isValue()) {
			Value lv = le.asValue();
			Value rv = re.asValue();
			if (Operator.isLogicalOp(expr.mOp)) {
				return ExprEvaluator.evalBoolExpr(expr.mOp, lv.asBool(), rv.asBool());
			}
			if (lv.isInteger() && rv.isInteger()) {
				return ExprEvaluator.evalIntExpr(expr.mOp, lv.asInteger(), rv.asInteger());
			} else if (lv.isText() && rv.isText()) {
				return ExprEvaluator.evalTextExpr(expr.mOp, lv.asText(), rv.asText());
			}
		}
		return new BinExpr(expr.mOp, le, re);
	}

	public Object visit(UnExpr expr) {
		Expr e = (Expr)expr.mExpr.accept(this);
		if (e.isValue()) {
			Value v = e.asValue();
			if (expr.mOp == Operator.NEGA) {
				if (v.isInteger()) {
					int i = v.asInteger().intValue();
					return new IntegerValue(-i);
				} else if (v.isText()) {
					return new IntegerValue(0);
				}
			} else if (expr.mOp == Operator.NOT
					   && (v.isInteger() || v.isText() || v.isBool())) {
				BoolValue b = v.asBool();
				if (b.isTrue()) {
					return Value.falseValue;
				} else if (b.isFalse()) {
					return Value.trueValue;
				} else {
					return Value.unknownValue;
				}
			}
		}
		return new UnExpr(expr.mOp, e);
	}
}
