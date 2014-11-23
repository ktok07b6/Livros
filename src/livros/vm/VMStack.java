package livros.vm;

import livros.Debug;
import livros.Log;
import livros.db.Expr;
import livros.db.Field;
import livros.db.Value;
import java.util.Map;
import java.util.Stack;

class VMStack
{
	class VMStackEntry 
	{
		public boolean isExpr() {
			return false;
		}

		public boolean isField() {
			return false;
		}

		public boolean isMap() {
			return false;
		}
	}

	class VMStackEntryExpr extends VMStackEntry
	{
		public Expr expr;
		public VMStackEntryExpr(Expr e) {
			expr = e;
		}
		public boolean isExpr() {
			return true;
		}
	}

	class VMStackEntryField extends VMStackEntry
	{
		public Field field;
		public VMStackEntryField(Field f) {
			field = f;
		}
		public boolean isField() {
			return true;
		}
	}

	class VMStackEntryMap extends VMStackEntry
	{
		public Map map;
		public VMStackEntryMap(Map m) {
			map = m;
		}
		public boolean isMap() {
			return true;
		}
	}

	Stack mStack = new Stack();

	public VMStack() {
	}

	public int size() {
		return mStack.size();
	}

	public VMStackEntry peek() {
		return (VMStackEntry)mStack.peek();
	}

	public VMStackEntry pop() {
		return (VMStackEntry)mStack.pop();
	}

	public void pushExpr(Expr e) {
		mStack.push(new VMStackEntryExpr(e));
	}

 	public Expr popExpr() {
		VMStackEntry e = (VMStackEntry)mStack.pop();
		Debug.assertTrue(e.isExpr());
		return ((VMStackEntryExpr)e).expr;
	}

	public void pushField(Field f) {
		mStack.push(new VMStackEntryField(f));
	}

 	public Field popField() {
		VMStackEntry e = (VMStackEntry)mStack.pop();
		Debug.assertTrue(e.isField());
		return ((VMStackEntryField)e).field;
	}

	public void pushMap(Map m) {
		mStack.push(new VMStackEntryMap(m));
	}

 	public Map popMap() {
		VMStackEntry e = (VMStackEntry)mStack.pop();
		Debug.assertTrue(e.isMap());
		return ((VMStackEntryMap)e).map;
	}
}
