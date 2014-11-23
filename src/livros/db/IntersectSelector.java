package livros.db;

import livros.Debug;
import livros.Log;
import livros.SortedList;
import java.util.Iterator;

class IntersectSelector implements Selector 
{
	Selector mLeft;
	Selector mRight;
	SortedList mLefts;
	SortedList mRights;
	Iterator mLeftIter;
	Iterator mRightIter;
	Record mNextRecord;

	public IntersectSelector(Selector l, Selector r) {
		mLeft = l;
		mRight = r;
		mLefts = new SortedList();
		mRights = new SortedList();
		
		while (l.hasNext()) {
			mLefts.add(l.next());
		}
		mLeftIter = mLefts.iterator();
		
		while (r.hasNext()) {
			mRights.add(r.next());
		}
		mRightIter = mRights.iterator();
	}

 	public boolean hasNext() {
		if (mNextRecord != null) {
			return true;
		}

		Record l = null;
		Record r = null;
		while (mLeftIter.hasNext() && mRightIter.hasNext()) {
			if (l == null) {
				l = (Record)mLeftIter.next();
			}
			if (r == null) {
				r = (Record)mRightIter.next();
			}
			if (l.id() == r.id()) {
				mNextRecord = l;
				break;
			} else if (l.id() < r.id()) {
				l = null;
			} else {
				r = null;
			}
		}
		return mNextRecord != null;
	}

	public Record next() {
		Record ret = mNextRecord;
		mNextRecord = null;
		return ret;
	}

	public void finish() {
		mLeft.finish();
		mRight.finish();
	}

	public String toString() {
		return super.toString() + "(" + mLeft.toString() + ", " + mRight.toString() + ")";
	}
}
