package livros.storage;

import livros.Debug;
import livros.Log;
import livros.btree.BTree;
import livros.btree.IntKey;
import livros.btree.Key;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.Record;
import livros.db.RecordIndex;
import livros.db.Table;
import livros.db.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableStorage
{
	private Table mTable;
	private ChunkManager mChunkManager;
	private RecordFile mRecordFile;
	private NewerChunk mInsertChunk;
	private VirtualChunk mCurrentChunk;
	private List mCurrentRecordList;
	private Map mBTreeMap;
	private BTree mRecIdBTree;

	public TableStorage(Table t) {
		mTable = t;
		mBTreeMap = new HashMap();
		mRecIdBTree = new BTree();
	}

	public boolean init() {
		try {
			Debug.startProfile();

			mRecordFile = new RecordFile(mTable);
			mChunkManager = new ChunkManager(this, mRecordFile);
			mInsertChunk = null;

			boolean fail = false;
			fail |= !mRecordFile.readHeader();
			fail |= !mChunkManager.init();
			if (!fail) {
				rebuildBTrees();
			}
			return !fail;
		} catch (Exception ex) {
			return false;
		} finally {
			Debug.endProfile();
		}
	}

	public Record getRecord(int index, int chunkid) {
		VirtualChunk vc = mChunkManager.findChunk(chunkid);
		if (vc.state() == ChunkHeader.NEWER) {
			NewerChunk nc = (NewerChunk)vc;
			return nc.get(index);
		} else {
			//FIXME: use cache
			
			//List list = new ArrayList();
			//mRecordFile.readRecords(vc.id(), vc.recordCount(), list);
			//return (Record)list.get(index);
			
			return mRecordFile.readRecord(vc.id(), index);
		}
	}

	public Record nextRecord(Record r) {
		if (r == null) {
			return firstRecord();
		}

		if (mCurrentChunk == null) {
			return null;
		}
		Debug.assertTrue(0 <= r.index());

		//rewind current chunk
		if (mCurrentChunk.id() != r.chunkid()) {
		    mCurrentChunk= mChunkManager.findChunk(r.chunkid());
			if (mCurrentChunk.state() == ChunkHeader.NEWER) {
				//Log.d("user Newer ");
				mCurrentRecordList = ((NewerChunk)mCurrentChunk).records();
			} else {
 				mCurrentRecordList = new ArrayList();
				mRecordFile.readRecords(mCurrentChunk.id(), mCurrentChunk.recordCount(), mCurrentRecordList);
			}
		}

		int nextIndex = r.index()+1;
		if (nextIndex >= mCurrentRecordList.size()) {
			nextIndex = 0;
			//Log.d("to nextChunk");
			mCurrentChunk = mCurrentChunk.next();
			while (mCurrentChunk != null && mCurrentChunk.recordCount() == 0) {
				mCurrentChunk = mCurrentChunk.next();
			}
			if (mCurrentChunk == null) {
				return null;
			}
			if (mCurrentChunk.state() == ChunkHeader.NEWER) {
				//Log.d("use Newer");
				mCurrentRecordList = ((NewerChunk)mCurrentChunk).records();
			} else {
 				mCurrentRecordList = new ArrayList();
				mRecordFile.readRecords(mCurrentChunk.id(), mCurrentChunk.recordCount(), mCurrentRecordList);
			}
		}
		Record result = (Record)mCurrentRecordList.get(nextIndex);
		/*
		if (0 > result.index()) {
			Log.d("nextIndex " + nextIndex);
			Log.d("mCurrentRecordList.size " + mCurrentRecordList.size());
			Log.d("mCurrentChunk.state() " + mCurrentChunk.state());
			for (int i = 0; i < mCurrentRecordList.size(); i++) {
				Log.d("!!! "+mCurrentRecordList.get(i));
			}
		}
		*/
		Debug.assertTrue(0 <= result.index());
		Debug.assertTrue(mCurrentChunk.recordCount() > result.index());
		/*
		if (mCurrentChunk.id() != result.chunkid()) {
			Log.d("$$$ " + mCurrentChunk);
			Log.d("$$$ " + result);
		}
		*/
		Debug.assertTrue(mCurrentChunk.id() == result.chunkid());
		return result;
	}

	Record firstRecord() {
		if (recordCount() == 0) {
			return null;
		}
		mCurrentChunk = mChunkManager.firstChunk();
		while (mCurrentChunk != null && mCurrentChunk.recordCount() == 0) {
			mCurrentChunk = mCurrentChunk.next();
		}
		if (mCurrentChunk == null) {
			return null;
		}

		if (mCurrentChunk.state() == ChunkHeader.NEWER) {
			//Log.d("use Newer");
			mCurrentRecordList = ((NewerChunk)mCurrentChunk).records();
		} else {
			//Log.d("read ");
			mCurrentRecordList = new ArrayList();
			mRecordFile.readRecords(mCurrentChunk.id(), mCurrentChunk.recordCount(), mCurrentRecordList);
		}
		if (mCurrentRecordList.size() > 0) {
			Record first = (Record)mCurrentRecordList.get(0);
			Debug.assertTrue(first.index() == 0);
			Debug.assertTrue(mCurrentChunk.id() == first.chunkid());
			return first;
		} else {
			Debug.assertTrue(false);
			return null;
		}
	}

	public Record lastRecord() {
		if (recordCount() == 0) {
			return null;
		}
		VirtualChunk last = mChunkManager.lastChunk();
		while (last != null && last.recordCount() == 0) {
			last = last.prev();
		}
		if (last == null) {
			return null;
		}
		return getRecord(last.recordCount() - 1, last.id());
	}

	public Record findById(int id) {
		RecordIndex ri = (RecordIndex)mRecIdBTree.find(new IntKey(id));
		if (ri != null) {
			Record r = getRecord(ri.index, ri.chunkid);
			return r;
		} else {
			return null;
		}
	}

	public Record findFirstRecord(String fieldName, Value v) {
		BTree btree = (BTree)mBTreeMap.get(fieldName);
		if (btree != null) {
			RecordIndex ri = (RecordIndex)btree.find(Key.fromValue(v));
			if (ri != null) {
				return getRecord(ri.index, ri.chunkid);
			} else {
				return null;
			}
		} else {
			Record r = firstRecord();
			while (r != null) {
				Value rv = r.get(fieldName);
				if (rv.equals(v)) {
					return r;
				}
				r = nextRecord(r);
			}
		}
		return null;
	}

	public int insert(Record r) {
		Debug.startProfile();

		int bytes = RecordConverter.recordSize(r);
		if (mInsertChunk == null) {
			VirtualChunk last = mChunkManager.lastChunk();
			if (last != null && last.isStable()) {
				mInsertChunk = mChunkManager.modifyChunk(last);
				mInsertChunk.forceNeedSyncRecord();
			}
			if (mInsertChunk == null || bytes > mInsertChunk.remainSize()) {
				mInsertChunk = mChunkManager.allocateChunk();
			}
		} else if(bytes > mInsertChunk.remainSize()) {
			//TODO:flush the old insert chunk
			mInsertChunk = mChunkManager.allocateChunk();
		}
		//FIXME: support large record
		Debug.assertTrue(bytes <= mInsertChunk.remainSize());
		Debug.assertTrue(mInsertChunk.state() == ChunkHeader.NEWER);
		mInsertChunk.insertRecord(r, bytes);
		Debug.assertTrue(0 <= r.index());

		//-------------------
		Set btreeFields = mBTreeMap.keySet();
		insertBTree(r, btreeFields);
		//-------------------

		Debug.endProfile();
		return bytes;
	}

	public void delete(Record r) {
		Log.v("delete " + r);
		Debug.startProfile();

		VirtualChunk vc = mChunkManager.findChunk(r.chunkid());
		NewerChunk nc;
		if (vc.state() == ChunkHeader.STABLE) {
			//Log.d("del in stable id:"+vc.id());
 			nc = mChunkManager.modifyChunk(vc);
			r.setPosition(r.index(), nc.id());
			if (mCurrentChunk == vc) {
				mCurrentChunk = nc;
				mCurrentRecordList = nc.records();
			}
		} else if (vc.state() == ChunkHeader.OLDER) {
			Debug.assertTrue(vc.descendant() != null);
			nc = vc.descendant();
			r = nc.findById(r.id());
		} else {
			//Log.d("del in editable id:"+vc.id());
			Debug.assertTrue(vc instanceof NewerChunk);
			nc = (NewerChunk)vc;
		}
		int deleteIndex = r.index();
		int bytes = RecordConverter.recordSize(r);
		int oldSize1 = recordCount();
		int oldSize2 = nc.recordCount();
		nc.deleteRecord(r, bytes);
		Debug.assertTrue(-1 == r.index());
		Debug.assertTrue(oldSize2-1 == nc.recordCount());
		if (oldSize1 - 1 != recordCount()) {
			Log.e("oldSize " +oldSize1);
			Log.e("recordCount "+recordCount());
			Debug.assertTrue(false);
		}

		//-------------------
		Set btreeFields = mBTreeMap.keySet();
		deleteBTree(r, btreeFields);
		//records after deleted one is shifted
		for (int i = deleteIndex; i < nc.recordCount(); i++) {
			Record shifted = nc.get(i);
			replaceBTree(shifted, btreeFields);
		}
		//-------------------

		Debug.endProfile();
	}

	public void update(Record r) {
		//Log.d("update " + r);
		//Log.d(toString());
		Debug.startProfile();

		VirtualChunk vc = mChunkManager.findChunk(r.chunkid());
		NewerChunk nc;
		if (vc.state() == ChunkHeader.STABLE) {
			nc = mChunkManager.modifyChunk(vc);
			r.setPosition(r.index(), nc.id());
			if (mCurrentChunk == vc) {
				mCurrentChunk = nc;
				mCurrentRecordList = nc.records();
			}
		} else if (vc.state() == ChunkHeader.OLDER) {
			Debug.assertTrue(vc.descendant() != null);
			nc = vc.descendant();
			r.setPosition(r.index(), nc.id());
		} else {
			Debug.assertTrue(vc instanceof NewerChunk);
			nc = (NewerChunk)vc;
		}
		Record old = nc.get(r.index());
		int oldbytes = RecordConverter.recordSize(old);
		int newbytes = RecordConverter.recordSize(r);
		//overflow check
		if ((newbytes - oldbytes) < nc.remainSize()) {
			nc.updateRecord(r, (newbytes - oldbytes));
		} else {
			nc.deleteRecord(old, oldbytes);
			nc = mChunkManager.allocateChunk(nc);
			r.setPosition(-1, -1);
			nc.insertRecord(r, newbytes);
		}

		//-------------------
		Set btreeFields = mBTreeMap.keySet();
		deleteBTree(old, btreeFields);
		insertBTree(r, btreeFields);
		//-------------------

		Debug.endProfile();
	}

	public void commit() {
		Debug.startProfile();
		mChunkManager.commit();
		mInsertChunk = null;
		Debug.endProfile();
	}

	public int recordCount() {
		return mChunkManager.totalRecordCount();
	}

	public String toString() {
		return mChunkManager.toString();
	}

	/* called from ChunkManager */
	public void updateRecordChunk(int chunkid, List records) {
		//update record chunkid
		Set btreeFields = mBTreeMap.keySet();
		Iterator iter = records.iterator();
		while (iter.hasNext()) {
			Record r = (Record)iter.next();
			r.setPosition(r.index(), chunkid);
			//-------------------
			replaceBTree(r, btreeFields);
			//-------------------
		}
	}

	void rebuildBTrees() {
		FieldList fields = mTable.fieldList();
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			if (f.isPrimary() || f.isUnique()) {
				mBTreeMap.put(f.name(), new BTree());
			}
		}

		Set btreeFields = mBTreeMap.keySet();
		Record r = firstRecord();
		while (r != null) {
			insertBTree(r, btreeFields);
			r = nextRecord(r);
		}
	}

	void insertBTree(Record r, Set btreeFields) {
		RecordIndex ri = new RecordIndex(r);
 		Iterator iter = btreeFields.iterator();
		while (iter.hasNext()) {
			String fieldName = (String)iter.next();
			Value v = r.get(fieldName);
			BTree btree = (BTree)mBTreeMap.get(fieldName);
			btree.insert(Key.fromValue(v), ri);
		}

		mRecIdBTree.insert(new IntKey(r.id()), ri);
	}

	void deleteBTree(Record r, Set btreeFields) {
 		Iterator iter = btreeFields.iterator();
		while (iter.hasNext()) {
			String fieldName = (String)iter.next();
			Value v = r.get(fieldName);
			BTree btree = (BTree)mBTreeMap.get(fieldName);
			btree.remove(Key.fromValue(v));
		}

		mRecIdBTree.remove(new IntKey(r.id()));
	}

	void replaceBTree(Record r, Set btreeFields) {
 		Iterator iter = btreeFields.iterator();
		while (iter.hasNext()) {
			String fieldName = (String)iter.next();
			Value v = r.get(fieldName);
			BTree btree = (BTree)mBTreeMap.get(fieldName);
			RecordIndex ri = (RecordIndex)btree.find(Key.fromValue(v));
			ri.index = r.index();
			ri.chunkid = r.chunkid();
			Debug.assertTrue(ri.recid == r.id());
		}

		RecordIndex ri = (RecordIndex)mRecIdBTree.find(new IntKey(r.id()));
		ri.index = r.index();
		ri.chunkid = r.chunkid();
		Debug.assertTrue(ri.recid == r.id());
	}

	public BTree btree(String fieldName) {
		BTree btree = (BTree)mBTreeMap.get(fieldName);
		return btree;
	}
}
