package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.Record;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class NewerChunk extends VirtualChunk
{
	public static int SIZE_MAX = (ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE);
	private boolean mIsRecordDirty;
	private List mRecords;
	private int mRemainSize;

	public NewerChunk(int id) {
		this(id, new ArrayList(), 0);
	}

	public NewerChunk(int id, List records, int recordBytes) {
		super(id, ChunkHeader.NEWER, records.size());
		mRecords = records;
		mRemainSize = SIZE_MAX - recordBytes;
	}

	public String toString() {
		String s = super.toString() +"\n";
		Iterator iter = mRecords.iterator();
		while (iter.hasNext()) {
			Record r = (Record)iter.next();
			s += r.toString();
			s += "\n";
		}
		return s;
	}

	public int remainSize() {
		return mRemainSize;
	}

	public void insertRecord(Record r, int size) {
		Debug.assertTrue(-1 == r.index());
		Debug.assertTrue(-1 == r.chunkid());
		mIsRecordDirty = true;

		mRemainSize -= size;
		Debug.assertTrue(0 <= mRemainSize);

		mRecords.add(r);
		r.setPosition(mRecords.size()-1, id());
	}

	public void deleteRecord(Record r, int size) {
		int i = r.index();
		Debug.assertTrue(0 <= i && i < mRecords.size());
		Debug.assertTrue(id() == r.chunkid());
		mIsRecordDirty = true;

		mRemainSize += size;
		Debug.assertTrue(mRemainSize <= SIZE_MAX);

		//Debug.assertTrue(mRecords.get(i).toString().equals(r.toString()));
		//Record del = (Record)mRecords.get(i);
		//Debug.assertTrue(del.toString().equals(r.toString()));
		int oldSize = mRecords.size();
		Debug.assertTrue(((Record)mRecords.get(i)).id() == r.id());
		mRecords.remove(i);
		Debug.assertTrue(oldSize - 1 == mRecords.size());
		for (int j = i; j < mRecords.size(); j++) {
			Record rr = (Record)mRecords.get(j);
			rr.setPosition(j, id());
		}
		r.setPosition(-1, -1);
	}

	public void updateRecord(Record r, int diffSize) {
		int i = r.index();
		Debug.assertTrue(0 <= i && i < mRecords.size());
		Debug.assertTrue(id() == r.chunkid());

		mIsRecordDirty = true;
		mRemainSize += diffSize;
		Debug.assertTrue(0 <= mRemainSize && mRemainSize <= SIZE_MAX);

  		mRecords.set(i, r);
	}

	public List records() {
		return mRecords;
	}

	public Record get(int i) {
		return (Record)mRecords.get(i);
	}

	public Record findById(int id) {
		for (int i = 0; i < mRecords.size(); i++) {
			Record r = (Record)mRecords.get(i);
			if (r.id() == id) {
				return r;
			}
		}
		return null;
	}

	public int recordCount() {
		return mRecords.size();
	}

	public boolean needSyncRecord() {
		return mIsRecordDirty;
	}

	public void forceNeedSyncRecord() {
		mIsRecordDirty = true;
	}
}
