package livros.db;

import livros.Log;
import livros.btree.BTree;

public class SelectorFactory
{
	public static class SelectorBuilder  implements ExprVisitor {
		Table mTable;
		public SelectorBuilder(Table t) {
			mTable = t;
		}

		public Object visit(Value v) {
			return null;
		}

		public Object visit(BinExpr expr) {
			switch (expr.mOp) {
			case Operator.OR: {
				Selector lsel = (Selector)expr.mLeft.accept(this);
				Selector rsel = (Selector)expr.mRight.accept(this);
				if (lsel != null && rsel != null) {
					return new UnionSelector(lsel, rsel);
				} else {
					return null;
				}
			}
				//break;
			case Operator.AND: {
				Selector lsel = (Selector)expr.mLeft.accept(this);
				Selector rsel = (Selector)expr.mRight.accept(this);
				if (lsel != null && rsel != null) {
					return new IntersectSelector(lsel, rsel);
				} else {
					return null;
				}
			}
				//break;

			case Operator.EQ:
			case Operator.NE:
			case Operator.LT:
			case Operator.GT:
			case Operator.LE:
			case Operator.GE:
				if (expr.mLeft.isValue() && expr.mRight.isValue()) {
					Value lv = expr.mLeft.asValue();
					Value rv = expr.mRight.asValue();
					if (lv.isFieldRef() && !rv.isFieldRef()) {
						//b-tree selector
 						Field f = mTable.field(lv.asFieldRef().fieldName());
						if (f.isPrimary()) {
							BTree btree = mTable.btree(f.name());
							return new BTreeSelector(btree, mTable, expr.mOp, rv);
						}
					} else if (!lv.isFieldRef() && rv.isFieldRef()) {
						//b-tree selector
 						Field f = mTable.field(rv.asFieldRef().fieldName());
						if (f.isPrimary()) {
							BTree btree = mTable.btree(f.name());
							return new BTreeSelector(btree, mTable, reverseOp(expr.mOp), lv);
						}
					}
					return null;
				}
				break;
			default:
				return null;
			}
			return null;
		}
		
		public Object visit(UnExpr expr) {
			return null;
		}

		private int reverseOp(int op) {
			switch (op) {
			case Operator.LT:
				return Operator.GE;
			case Operator.GT:
				return Operator.LE;
			case Operator.LE:
				return Operator.GT;
			case Operator.GE:
				return Operator.LT;
			}
			return op;
		}
	}

	public static Selector create(IReadOnlyTable rot, Expr expr) {
		if (expr != null) {
			expr = new ConstantFolding().process(expr);
		}

		Selector selector = null;
		if (!rot.isDerivedTable() && expr != null) {
			SelectorBuilder builder = new SelectorBuilder((Table)rot);
			selector = (Selector)expr.accept(builder);
		} else {
			Log.v("no selector builder");
		}

		if (selector == null) {
			if (!rot.isDerivedTable()) {
				selector = new SequentialSelector((Table)rot);
			} else {
				selector = new DerivedSequentialSelector((DerivedTable)rot);
			}
			if (expr != null) {
				selector = new ConditionalSelector(selector, expr);
			}
		}

		Log.v("SelectorFactory#create "+selector.toString());
		Log.v("SelectorFactory expr "+expr);
		return selector;
	}
}
