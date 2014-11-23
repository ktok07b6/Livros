package livros.db;

import livros.btree.BTree;
import livros.btree.Key;
import java.util.LinkedList;
import java.util.List;

class BTreeSelector implements Selector
{
	BTree mBTree;
	Table mTable;
	int mOp;
	Value mValue;
	List mResults;
	int mPos;

	public BTreeSelector(BTree btree, Table t, int op, Value v) {
		mBTree = btree;
		mTable = t;
		mOp = op;
		mValue = v;
		mResults = new LinkedList();
		findValues();
		mPos = 0;
	}

	private void findValues() {
		switch (mOp) {
		case Operator.EQ: {
			RecordIndex ri = (RecordIndex)mBTree.find(Key.fromValue(mValue));
			if (ri != null) {
				mResults.add(ri);
			}
		}
			break;

		case Operator.NE: {
			//TODO:
			RecordIndex ri = (RecordIndex)mBTree.find(Key.fromValue(mValue));
			List all = mBTree.allValues();
			if (ri != null) {
				all.remove(ri);
			}
			mResults = all;
		}
			break;

		case Operator.LT: {
			//TODO:
			List results = mBTree.findLess(Key.fromValue(mValue));
			if (results != null) {
				mResults = results;
			}
		}
			break;

		case Operator.LE: {
			RecordIndex ri = (RecordIndex)mBTree.find(Key.fromValue(mValue));
			List results = mBTree.findLess(Key.fromValue(mValue));
			if (results != null) {
				mResults.addAll(results);
			}
			mResults.add(ri);
		}
			break;

		case Operator.GT: {
			//TODO:
			List results = mBTree.findGreater(Key.fromValue(mValue));
			if (results != null) {
				mResults = results;
			}
		}
			break;

		case Operator.GE: {
			RecordIndex ri = (RecordIndex)mBTree.find(Key.fromValue(mValue));
			mResults.add(ri);
			List results = mBTree.findGreater(Key.fromValue(mValue));
			if (results != null) {
				mResults.addAll(results);
			}
		}
			break;
		}
	}

 	public boolean hasNext() {
		return (mPos < mResults.size());
	}

	public Record next() {
		if (mPos < mResults.size()) {
			RecordIndex ri = (RecordIndex)mResults.get(mPos++);
			return mTable.getRecord(ri);
		}
		return null;
	}

	public void finish() {
	}
}
