package livros.vm;

import livros.Livros;
import livros.Debug;
import livros.Log;
import livros.db.BinExpr;
import livros.db.DataBase;
import livros.db.DerivedTable;
import livros.db.DerivedSequentialSelector;
import livros.db.Expr;
import livros.db.ExprEvaluator;
import livros.db.Field;
import livros.db.FieldRef;
import livros.db.Function;
import livros.db.IReadOnlyTable;
import livros.db.Operator;
import livros.db.Record;
import livros.db.RecordIndex;
import livros.db.Selector;
import livros.db.Table;
import livros.db.TextValue;
import livros.db.Type;
import livros.db.UnExpr;
import livros.db.UserFunction;
import livros.db.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class VM implements InstructionVisitor
{
	public static boolean DEBUG = false;

	DataBase mDataBase;
	List mVmLog = new ArrayList();
	VMStack mVMStack;

	public VM() {
		mDataBase = DataBase.open("");
	}

	public void destroy() {
		mDataBase.cleanup();
	}

	public void exec(List instructions) {
		mVMStack = new VMStack();

		for (int i = 0; i < instructions.size(); i++) {
			INST inst = (INST)instructions.get(i);
			try {
				if (DEBUG) {
					Log.d(new InstructionPrinter().toString(inst));
				}
				mVmLog.add(inst);
				inst.accept(this);
			} catch (Exception ex) {
				Log.e("=========================================");
				for (int j = 0; j < mVmLog.size(); j++) {
					INST called = (INST)mVmLog.get(j);
					Log.e("#"+ j +" : " + called.toString());
				}
				Log.e("=========================================");
				ex.printStackTrace();
				break;
			} 
		}
	}

	public void visit(CLOSE close) throws Exception  {
	}

	public void visit(COMMIT commit) throws Exception  {
		if (commit.mTable != null) {
			Table t = mDataBase.table(commit.mTable);
			//Log.d("===========\n"+t.toString());
			mDataBase.commit(t);
		} else {
			mDataBase.commitAll();
		}
	}

	public void visit(DELTABLE del) throws Exception {
		Table t = mDataBase.table(del.mTable);
		if (t == null) {
			throw new VMExecException(t.name() + " is not exist in db");
		}
		mDataBase.delTable(t);
	}

	public void visit(INSERT insert) throws Exception  {
		Map valueMap = mVMStack.popMap();
		Table t = mDataBase.table(insert.mTable);
		if (t == null) {
			throw new VMExecException(t.name() + " is not exist in db");
		}
		t.insertRecord(valueMap);
	}

	public void visit(INTERSECT intersect) throws Exception {
	}

	public void visit(MAKEREC makerec) throws Exception  {
		Map valueMap = new HashMap();
		ExprEvaluator evaluator = new ExprEvaluator();
		for (int i = 0; i < makerec.mFieldCount; i++) {
			Expr expr = mVMStack.popExpr();
			Value v = evaluator.eval(expr, null);

			Value fv = (Value)mVMStack.popExpr();
			Debug.assertTrue(fv.isFieldRef());
			FieldRef field = fv.asFieldRef();
			valueMap.put(field.fieldName(), v);
		}
		mVMStack.pushMap(valueMap);
	}

	public void visit(NEWFIELD newfield) throws Exception {
		mVMStack.pushField(newfield.mField);
	}

	public void visit(NEWTABLE tab) throws Exception {
		Debug.assertTrue(mDataBase != null);
		if (mDataBase.table(tab.mName) != null) {
			throw new VMExecException("table " + tab.mName + " is already defined");
		}
		Table t = new Table(mDataBase.name(), tab.mName);
		mDataBase.addTable(t);

		//for reverse order
		Stack fields = new Stack();
		while (mVMStack.size() != 0 && mVMStack.peek().isField()) {
			fields.push(mVMStack.popField());
		}
		while (fields.size() > 0) {
			t.addField((Field)fields.pop());
		}
		t.init();
	}

	public void visit(OPEN open) throws Exception  {
		//NIY
	}

	public void visit(PRODUCT product) throws Exception  {
	}

	public void visit(PROJECTION proj) throws Exception  {
		IReadOnlyTable src = mDataBase.readOnlyTable(proj.mSrc);
		Table dst = new Table(mDataBase.name(), proj.mDst);
		Map projectionMap = new HashMap();
		int order[] = new int[proj.mArgc];
		for (int i = 0; i < proj.mArgc; i++) {
			Expr expr = mVMStack.popExpr();
			Value v = (Value)mVMStack.popExpr();
			Debug.assertTrue(v.isText());
			String fieldName = v.asText().textValue();
			projectionMap.put(fieldName, expr);
			dst.addField(new Field(fieldName, Type.anyType));
			order[i] = (proj.mArgc-1) - i;
		}
		dst.init();
		mDataBase.addTmpTable(dst);
		dst.fieldList().setOrder(order);

		ExprEvaluator evaluator = new ExprEvaluator();
		Selector selector = src.selector(null);
		while (selector.hasNext()) {
			Record srcRec = (Record)selector.next();
			Record dstRec = dst.createRecord();
			Iterator iter = projectionMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry)iter.next();
				String fieldName = (String)entry.getKey();
				Expr expr = (Expr)entry.getValue();
				Value v = evaluator.eval(expr, srcRec);
				dstRec.set(fieldName, v);
			}
			dst.insertRecord(dstRec);
		}
		selector.finish();
	}

	public void visit(PUSHEXPR pushe) throws Exception  {
		mVMStack.pushExpr(pushe.mExpr);
	}

	public void visit(SELECT select) throws Exception {
		Debug.assertTrue(mDataBase != null);
		IReadOnlyTable src = mDataBase.readOnlyTable(select.mSrc);
		Debug.assertTrue(src != null);
		DerivedTable dst = new DerivedTable(mDataBase.name(), 
											select.mDst, 
											src.baseName(), 
											src.fieldList());
		dst.init();
		mDataBase.addTmpTable(dst);
		Expr expr = mVMStack.popExpr();
		Selector selector = src.selector(expr);
		while (selector.hasNext() == true) {
			Record r = (Record)selector.next();
			Log.v("selected " + r);
			dst.insertIndex(new RecordIndex(r));
		}
		selector.finish();
		dst.flush();
	}

	public void visit(SHOW show) throws Exception {
		IReadOnlyTable t = mDataBase.readOnlyTable(show.mTable);
		Debug.assertTrue(t != null);
		if (Livros.NO_SHOW) return;

		Livros.console().println(t.fieldList().headerString());
		Selector selector = t.selector(null);
		while (selector.hasNext()) {
			Record r = selector.next();
			Livros.console().println(r.toString());
		}
		selector.finish();
	}

	public void visit(SUBTRACT sub) throws Exception {
		IReadOnlyTable src = mDataBase.readOnlyTable(sub.mSrc);
		Table dst = mDataBase.table(sub.mDst);

		Selector selector = src.selector(null);
		Debug.assertTrue(selector instanceof DerivedSequentialSelector);
		while (selector.hasNext()) {
			Record r = selector.next();
			dst.deleteRecord(r);
		}
	}

	public void visit(TRACE trace) throws Exception {
	}

	public void visit(UNION union) throws Exception {
	}

	public void visit(UPDATE update) throws Exception {
		IReadOnlyTable src = mDataBase.readOnlyTable(update.mSrc);
		Table dst = mDataBase.table(update.mDst);
		Debug.assertTrue(src != null);
		Debug.assertTrue(dst != null);
		Expr setValues[] = new Expr[update.mArgc];
		String fieldNames[] = new String[update.mArgc];
		for (int i = 0; i < update.mArgc; i++) {
			Expr expr = mVMStack.popExpr();
			Value v = (Value)mVMStack.popExpr();
			Debug.assertTrue(v.isFieldRef());
			FieldRef ref = v.asFieldRef();
			setValues[i] = expr;
			fieldNames[i] = ref.fieldName();
		}

		ExprEvaluator evaluator = new ExprEvaluator();
		Selector selector = src.selector(null);
		Debug.assertTrue(selector instanceof DerivedSequentialSelector);
		while (selector.hasNext()) {
			Record r = (Record)selector.next().clone();
			for (int i = 0; i < setValues.length; i++) {
				Expr expr = setValues[i];
				Value v = evaluator.eval(expr, r);
				Debug.assertTrue(v.isInteger() || v.isText());
				r.set(fieldNames[i], v);
			}
			dst.updateRecord(r, fieldNames);
		}
		selector.finish();
	}
	/*
	Expr buildExprTree() {
		if (mVMStack.peek().isValue()) {
			return mVMStack.popValue();
		} else if (mVMStack.peek().isOp()) {
			int op = mVMStack.popOp();
			if (Operator.isUnaryOp(op)) {
				Expr e = buildExprTree();
				return new UnExpr(op, e);
			} else {
				Expr r = buildExprTree();
				Expr l = buildExprTree();
				return new BinExpr(op, l, r);
			}
		} else {
			Debug.assertTrue(false);
			return null;
		}
	}
	*/
}
