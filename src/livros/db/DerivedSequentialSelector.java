package livros.db;

import livros.Debug;
import livros.Log;
import livros.storage.IndexFile;
import livros.storage.StorageManager;

import java.util.List;
import java.util.ArrayList;

public class DerivedSequentialSelector implements Selector
{
	private static final int BUFFER_SIZE = 50;
	IndexFile mIndexFile;
	Table mBaseTable;
	int mPos;
	int mBufferPos;
	List mIndicesBuffer;

	public DerivedSequentialSelector(DerivedTable dt) {
		Debug.assertTrue(dt != null);
		mIndexFile = StorageManager.instance().indexFile(dt.name());
		mIndexFile.reset();
		DataBase db = DataBase.open(dt.dbName());
		mBaseTable = db.table(dt.baseName());
		Debug.assertTrue(mBaseTable != null);
		mPos = 0;
	    mIndicesBuffer = new ArrayList();
		mBufferPos = 0;
	}

 	public boolean hasNext() {
		if (mBufferPos >= mIndicesBuffer.size()) {
			fillBuffer();
		}
		return mBufferPos < mIndicesBuffer.size();
	}

	public Record next() {
		RecordIndex ri = (RecordIndex)mIndicesBuffer.get(mBufferPos++);
		return mBaseTable.getRecord(ri);
	}

	public void finish() {
		mIndexFile.close();
	}

	private void fillBuffer() {
		mIndicesBuffer.clear();
		int count = BUFFER_SIZE;
		if (mPos + count > mIndexFile.recordCount()) {
			count = mIndexFile.recordCount() - mPos;
		}
		mIndexFile.readIndices(count, mIndicesBuffer);
		Debug.assertTrue(count == mIndicesBuffer.size());
		mPos += count;
		mBufferPos = 0;
	}
}
