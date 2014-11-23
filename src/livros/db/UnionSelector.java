package livros.db;

import livros.Debug;
import livros.Log;
import livros.SortedList;
import java.util.Iterator;

class UnionSelector implements Selector 
{
	Selector mLs;
	Selector mRs;
	SortedList mLefts;
	SortedList mRights;
	Iterator mLeftIter;
	Iterator mRightIter;
	Record mNextLeftRecord;
	Record mNextRightRecord;
	boolean mHasNextLeft;
	boolean mHasNextRight;

	public UnionSelector(Selector l, Selector r) {
		mLs = l;
		mRs = r;
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

		l.finish();
		r.finish();
	}

 	public boolean hasNext() {
		if (mNextLeftRecord == null) {
			mHasNextLeft = mLeftIter.hasNext();
		} else {
			mHasNextLeft = true;
		}
		if (mNextRightRecord == null) {
			mHasNextRight = mRightIter.hasNext();
		} else {
			mHasNextRight = true;
		}

		return mHasNextLeft || mHasNextRight;
	}

	public Record next() {
		if (mNextLeftRecord == null && mHasNextLeft) {
			mNextLeftRecord = (Record)mLeftIter.next();
		}
		if (mNextRightRecord == null && mHasNextRight) {
			mNextRightRecord = (Record)mRightIter.next();
		}

		Debug.assertTrue(mNextLeftRecord != null || mNextRightRecord != null);
		Record ret = null;
		if (mNextLeftRecord != null && mNextRightRecord != null) {
			if (mNextLeftRecord.id() < mNextRightRecord.id()) {
				ret = mNextLeftRecord;
				mNextLeftRecord = null;
			} else if (mNextLeftRecord.id() > mNextRightRecord.id()) {
				ret = mNextRightRecord;
				mNextRightRecord = null;
			} else {
				ret = mNextLeftRecord;
				mNextLeftRecord = null;
				mNextRightRecord = null;
			}
		} else if (mNextRightRecord == null) {
			ret = mNextLeftRecord;
			mNextLeftRecord = null;
		} else {
			ret = mNextRightRecord;
			mNextRightRecord = null;
		}
		return ret;
	}
	
	public void finish() {
	}

	public String toString() {
		return super.toString() + "(" + mLs.toString() + ", " + mRs.toString() + ")";
	}
}
