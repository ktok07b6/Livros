package livros.vm;

import livros.db.Operator;

public class InstructionPrinter implements InstructionVisitor
{
	StringBuilder mBuilder;
	public InstructionPrinter() {
		mBuilder = new StringBuilder();
	}

	public String toString(INST i) {
		reset();
		try {i.accept(this);} catch (Exception e) {}
		return getResult();
	}

	public String getResult() {
		return mBuilder.toString();
	}

	public void reset() {
		mBuilder = new StringBuilder();
	}

	public void visit(CLOSE close) throws Exception {
		mBuilder.append("CLOSE "+close.mDbName);
	}

	public void visit(COMMIT commit) throws Exception {
		mBuilder.append("COMMIT");
	}

	public void visit(DELTABLE del) throws Exception {
		mBuilder.append("DELTABLE " + del.mTable);
	}

	public void visit(INSERT insert) throws Exception {
		mBuilder.append("INSERT " + insert.mTable);
	}

	public void visit(INTERSECT intersect) throws Exception {
		mBuilder.append("INTERSECT " + intersect.mDst + " " + 
						intersect.mSrc1 + " " + intersect.mSrc2);
	}

	public void visit(MAKEREC makerec) throws Exception {
		mBuilder.append("MAKEREC "+makerec.mFieldCount);
	} 

	public void visit(NEWFIELD newfield) throws Exception {
		mBuilder.append("NEWFIELD "+newfield.mField.toString());
	}

	public void visit(NEWTABLE newtable) throws Exception {
		mBuilder.append("NEWTABLE "+newtable.mName);
	}

	public void visit(OPEN open) throws Exception {
		mBuilder.append("OPEN "+open.mDbName);
	}

	public void visit(PRODUCT product) throws Exception {
		mBuilder.append("PRODUCT "+product.mArgc);
	}

	public void visit(PROJECTION projection) throws Exception {
		mBuilder.append("PROJECTION " + 
						projection.mDst + " " + projection.mSrc + " " + projection.mArgc);
	}

	public void visit(PUSHEXPR pushe) throws Exception {
		mBuilder.append("PUSHEXPR "+pushe.mExpr.toString());
	}

	public void visit(SELECT select) throws Exception {
		mBuilder.append("SELECT " + select.mDst + " " + select.mSrc);
	}

	public void visit(SHOW show) throws Exception {
		mBuilder.append("SHOW " + show.mTable);
	}

	public void visit(SUBTRACT sub) throws Exception {
		mBuilder.append("SUBTRACT " + sub.mDst + " " + sub.mSrc);
	}

	public void visit(TRACE trace) throws Exception {
		mBuilder.append("TRACE");
	}

	public void visit(UNION union) throws Exception {
		mBuilder.append("UNION " + union.mDst + " " + 
						union.mSrc1 + " " + union.mSrc2);
	}

	public void visit(UPDATE update) throws Exception {
		mBuilder.append("UPDATE " + update.mDst + " " + update.mSrc + " " + update.mArgc);
	}
}
