package livros.db;

import livros.Debug;
import livros.Log;
import livros.storage.StorageManager;
import livros.storage.TableStorage;

import java.util.List;

public class ConditionalSelector implements Selector
{
	Selector mBaseSelector;
	Expr mSearchCond;
	Record mNextRecord;
	ExprEvaluator mEval;

	public ConditionalSelector(Selector selector, Expr expr) {
		mBaseSelector = selector;
		mSearchCond = expr;
		mEval = new ExprEvaluator();
		mNextRecord = null;
	}

	private void searchNext() {
		while (mBaseSelector.hasNext()) {
			Record r = mBaseSelector.next();
			Value resultV = mEval.eval(mSearchCond, r);
			BoolValue result = resultV.asBool();
			if (result == null) {
				continue;
			}
			if (result.isTrue()) {
				mNextRecord = r;
				return;
			}
		}
		mNextRecord = null;
	}
 	public boolean hasNext() {
		if (mNextRecord != null) {
			return true;
		}
		searchNext();
		boolean ret = mNextRecord != null;
		return ret;
	}

	public Record next() {
		//Debug.assertTrue(mNextRecord != null);
		if (mNextRecord == null) {
			searchNext();
		}
		Record nextRec = mNextRecord;
		mNextRecord = null;
		return nextRec;
	}

	public void finish() {
		mBaseSelector.finish();
	}

	public String toString() {
		return super.toString() + "(" + mBaseSelector.toString() + ")";
	}
}
