package livros.vm;

import livros.db.Expr;
import livros.db.IntegerValue;
import livros.db.TextValue;

public class PUSHEXPR extends INST
{
	Expr mExpr;
	public PUSHEXPR(Expr e) {
		super();
		mExpr = e;
	}

	public PUSHEXPR(int i) {
		super();
		mExpr = new IntegerValue(i);
	}

	public PUSHEXPR(String s) {
		super();
		mExpr = new TextValue(s);
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
