package livros.db;

import livros.Debug;
import livros.Log;
import livros.storage.StorageManager;
import livros.storage.TableStorage;

import java.util.List;

public class SequentialSelector implements Selector
{
	TableStorage mStorage;
	Record mPrevRecord;
	Record mCurrentRecord;
	boolean mDone;
	public SequentialSelector(Table t) {
		Debug.assertTrue(t != null);
		mStorage = StorageManager.instance().tableStorage(t);
		mPrevRecord = null;
		mDone = false;
	}

 	public boolean hasNext() {
		if (mDone) return false;
		mCurrentRecord = mStorage.nextRecord(mPrevRecord);
		if (mCurrentRecord == null) {
			mDone = true;
		}
		return mCurrentRecord != null;
	}

	public Record next() {
		if (mDone) return null;
		if (mCurrentRecord == null) {
			mCurrentRecord = mStorage.nextRecord(mPrevRecord);
		}
		//Debug.assertTrue(mCurrentRecord != null);
		mPrevRecord = mCurrentRecord;
		mCurrentRecord = null;
		return mPrevRecord;
	}

	public void finish() {
		
	}
}
