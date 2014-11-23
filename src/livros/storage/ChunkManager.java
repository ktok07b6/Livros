package livros.storage;

import livros.Debug;
import livros.Log;
import livros.SortedList;
import livros.db.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

class ChunkManager
{
	VirtualChunk mChunkListHead;
	VirtualChunk mChunkListTail;
	VirtualChunk mFreeListHead;
	VirtualChunk mFreeListTail;
	TableStorage mTableStorage;
	RecordFile mRecordFile;
	SortedList mSortedChunks;

	public ChunkManager(TableStorage storage, RecordFile recordFile) {
		mTableStorage = storage;
		mRecordFile = recordFile;
	}

	/*use test only */
	public ChunkManager(RecordFile recordFile) {
		this(null, recordFile);
	}

	public boolean init() {
		mSortedChunks = new SortedList();
		mChunkListHead = null;
		mChunkListTail = null;
		mFreeListHead = null;
		mFreeListTail = null;
		boolean fail = false;
		fail |= !buildChunkList();
		fail |= !buildFreeList();
		return !fail;
	}

	public String toString() {
		String s = "<<<ChunkList>>>\n";
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			s += vc.toString() + "\n";
			vc = vc.next();
		}
		return s;
	}

	public VirtualChunk firstChunk() {
		return mChunkListHead;
	}

	public VirtualChunk lastChunk() {
		return mChunkListTail;
	}

	public VirtualChunk firstFreeChunk() {
		return mFreeListHead;
	}

	public int totalRecordCount() {
		int count = 0;
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			count += vc.recordCount();
			vc = vc.next();
		}
		return count;
	}

	public VirtualChunk findChunk(int id) {
		/* sanity check
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			if (vc.id() == id) {
				return vc;
			}
			vc = vc.next();
		}
		Debug.assertTrue(false);
		return null;
		*/
		Debug.assertTrue(0 < id && id <= mSortedChunks.size());
		return (VirtualChunk)mSortedChunks.get(id-1);
	}

	public NewerChunk allocateChunk() {
		if (mChunkListTail == null) {
			NewerChunk newer = new NewerChunk(newerId());
			mSortedChunks.add(newer);
			mChunkListHead = newer;
			mChunkListTail = newer;
			return newer;
		} else {
			return allocateChunk(mChunkListTail);
		}
	}

	public NewerChunk allocateChunk(VirtualChunk prev) {
		NewerChunk newer = new NewerChunk(newerId());
		mSortedChunks.add(newer);
		VirtualChunk prevNext = prev.next();
		if (prevNext != null) {
			prevNext.setPrev(newer);
			newer.setNext(prevNext);
		}
		prev.setNext(newer);
		newer.setPrev(prev);
		if (prev == mChunkListTail) {
			mChunkListTail = newer;
		}
		return newer;
	}

	public NewerChunk modifyChunk(VirtualChunk older) {
		Debug.startProfile();
		List records = new ArrayList();
		int size = mRecordFile.readRecords(older.id(), older.recordCount(), records);
		Debug.assertTrue(older.recordCount() == records.size());

		int newid = newerId();
		NewerChunk newer = new NewerChunk(newid, records, size);
		if (mTableStorage != null) {
			mTableStorage.updateRecordChunk(newid, records);
		} else {
			//use test only 
			Iterator iter = records.iterator();
			while (iter.hasNext()) {
				Record r = (Record)iter.next();
				r.setPosition(r.index(), newid);
			}
		}
		/*
		for (int i = 0; i < records.size(); i++) {
			Log.d("records " + records.get(i));
		}
		*/

		mSortedChunks.add(newer);
		VirtualChunk prev = older.prev();
		VirtualChunk next = older.next();
		newer.setPrev(prev);
		newer.setNext(next);
		newer.setAncestor(older);
		older.setDescendant(newer);
		if (older == mChunkListHead) {
			Debug.assertTrue(prev == null);
			mChunkListHead = newer;
		} else {
			Debug.assertTrue(prev != null);
			prev.setNext(newer);
		}
		if (older == mChunkListTail) {
			Debug.assertTrue(next == null);
			mChunkListTail = newer;
		} else {
			Debug.assertTrue(next != null);
			next.setPrev(newer);
		}

		older.setPrev(null);
		older.setNext(null);
		older.setState(ChunkHeader.OLDER);

		Debug.endProfile();
		return newer;
	}

	public int chunkCount() {
		int count = 0;
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			count++;
			vc = vc.next();
		}
		return count;
	}

	public int freeChunkCount() {
		int count = 0;
		VirtualChunk vc = mFreeListHead;
		while (vc != null) {
			count++;
			vc = vc.next();
		}
		return count;
	}

	private int newerId() {
		int newerId;
		if (mFreeListTail != null) {
			newerId = mFreeListTail.id();
			mSortedChunks.remove(mFreeListTail);
			mFreeListTail = mFreeListTail.prev();
			if (mFreeListTail != null) {
				mFreeListTail.setNext(null);
			}
		} else {
			newerId = maxId()+1;
		}
		return newerId;
	}
	
	private int maxId() {
		//FIXME: use sorted list or set
		/*
		int maxId = 0;
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			maxId = Math.max(maxId, vc.id());
			if (vc.ancestor() != null) {
				maxId = Math.max(maxId, vc.ancestor().id());
			}
			//Log.d("maxId "+maxId);
			vc = vc.next();
		}
		vc = mFreeListHead;
		while (vc != null) {
			maxId = Math.max(maxId, vc.id());
			Debug.assertTrue(vc.ancestor() == null);
			vc = vc.next();
		}
		return maxId;
		*/
		if (mSortedChunks.size() > 0) {
			VirtualChunk last = (VirtualChunk)mSortedChunks.get(mSortedChunks.size()-1);
			Debug.assertTrue(last.id() == mSortedChunks.size());
			return last.id();
		} else {
			return 0;
		}
	}

	public boolean commit() {
		stabilizeChunkList();
		sync();
		return true;
	}

	public boolean rollback() {
		//TODO:
		//unlink 'newer' and clean it, then back to free-list
		//if 'newer' link to 'older', the 'older' back to the chunk-list and change to 'stable'
		return false;
	}

	private void stabilizeChunkList() {
		List newFreeChunks = new ArrayList();
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			//find 'newer' and unlink 'newer' and 'older'
			if (vc.state() == ChunkHeader.NEWER) {
				VirtualChunk ancestor = vc.ancestor();
				if (ancestor != null) {
					vc.setAncestor(null);
					ancestor.setDescendant(null);
					Debug.assertTrue(ancestor.state() == ChunkHeader.OLDER);
					//add the 'older' chunk to free-list
					newFreeChunks.add(ancestor);
				}
				//if 'newer' is empty, unlink itself and add it to free-list 
				//(becaus this chunk's all data is deleted)
				if (vc.recordCount() < 1) {
					VirtualChunk prev = vc.prev();
					VirtualChunk next = vc.next();
					if (vc == mChunkListHead) {
						Debug.assertTrue(prev == null);
						mChunkListHead = next;
					} else {
						prev.setNext(next);
					}
					if (vc == mChunkListTail) {
						Debug.assertTrue(next == null);
						mChunkListTail = prev;
					} else {
						next.setPrev(prev);
					}
					newFreeChunks.add(vc);
				} else {
					//change 'newer' chunk to 'stable'
					vc.setState(ChunkHeader.STABLE);
				}
			}
			vc = vc.next();
		}
		
		for (int i = 0; i < newFreeChunks.size(); i++) {
			VirtualChunk free = (VirtualChunk)newFreeChunks.get(i);
			free.setPrev(null);
			free.setNext(null);
			addToFreeList(free);
		}
	}

	private void sync() {
		mRecordFile.reset();
		mRecordFile.writeHeader(totalRecordCount(), chunkCount()+freeChunkCount(), 
								mChunkListHead != null ? mChunkListHead.id() : 0,
								mFreeListHead != null ? mFreeListHead.id() : 0);
		VirtualChunk vc = mChunkListHead;
		while (vc != null) {
			if (vc.needSyncHeader()) {
				ChunkHeader header = ChunkHeader.create(vc);
				mRecordFile.writeChunkHeader(header);
				if (vc.needSyncRecord()) {
					//Log.d("sync "+vc);
					NewerChunk newer = (NewerChunk)vc;
					int chunkBytes = mRecordFile.writeRecords(header.id(), newer.records());
					Debug.assertTrue(header.recordCount() == newer.records().size());
				}
			}
			vc = vc.next();
		}

		VirtualChunk free = mFreeListHead;
		while (free != null) {
			if (free.needSyncHeader()) {
				ChunkHeader header = ChunkHeader.create(free);
				mRecordFile.writeChunkHeader(header);
				mRecordFile.clearRecords(header.id());
			}
			free = free.next();
		}
	}

	private void addToFreeList(VirtualChunk newFree) {
		newFree.setState(ChunkHeader.FREE);
		if (mFreeListTail != null) {
			mFreeListTail.setNext(newFree);
			newFree.setPrev(mFreeListTail);
			mFreeListTail = newFree;
		} else {
			mFreeListHead = newFree;
			mFreeListTail = newFree;
		}
	}

	private boolean buildChunkList() {
		int id = mRecordFile.headChunkId();
		if (id <= 0) {
			return true;
		}

		ChunkHeader header = mRecordFile.readChunkHeader(id);
		if (header == null) {
			return false;
		}
		Debug.assertTrue(header.state() == ChunkHeader.STABLE);
		VirtualChunk vc = new VirtualChunk(header.id(), ChunkHeader.STABLE, header.recordCount());
		mSortedChunks.add(vc);
		mChunkListHead = vc;
		mChunkListHead.setPrev(null);
		VirtualChunk prev = vc;
		while ((id = header.nextId()) > 0) {
			header = mRecordFile.readChunkHeader(id);
			if (header == null) {
				return false;
			}
			Debug.assertTrue(header.state() == ChunkHeader.STABLE);
			vc = new VirtualChunk(header.id(), ChunkHeader.STABLE, header.recordCount());
			mSortedChunks.add(vc);
			vc.setPrev(prev);
			prev.setNext(vc);
			prev = vc;
		}
		mChunkListTail = vc;
		mChunkListTail.setNext(null);
		return true;
	}

	private boolean buildFreeList() {
		int id = mRecordFile.freeHeadChunkId();
		if (id <= 0) {
			return true;
		}

		ChunkHeader header = mRecordFile.readChunkHeader(id);
		if (header == null) {
			return false;
		}
		Debug.assertTrue(header.state() == ChunkHeader.FREE);
		NewerChunk nc = new NewerChunk(header.id());
		mSortedChunks.add(nc);
		nc.setState(ChunkHeader.FREE);
		mFreeListHead = nc;
		mFreeListHead.setPrev(null);
		NewerChunk prev = nc;
		while ((id = header.nextId()) > 0) {
			header = mRecordFile.readChunkHeader(id);
			if (header == null) {
				return false;
			}
			Debug.assertTrue(header.state() == ChunkHeader.FREE);
			nc = new NewerChunk(header.id());
			mSortedChunks.add(nc);
			nc.setState(ChunkHeader.FREE);
			nc.setPrev(prev);
			prev.setNext(nc);
			prev = nc;
		}
		mFreeListTail = nc;
		mFreeListTail.setNext(null);
		return true;
	}
	
}
