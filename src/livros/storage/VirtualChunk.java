package livros.storage;

import livros.Debug;
import livros.Log;

class VirtualChunk implements Comparable 
{
	private int mFlags;
	private int mChunkId;
	private VirtualChunk mPrevChunk;
	private VirtualChunk mNextChunk;
	private VirtualChunk mAncestor;
	private NewerChunk mDescendant;
	private int mRecordCount;
	private boolean mIsDirty;

	public VirtualChunk(int id, int state, int count) {
		Debug.assertTrue(id >= 1);
		mChunkId = id;
		setState(state);
		mRecordCount = count;
		mIsDirty = false;
	}

	public String toString() {
		return "Chunk:" + id() + " state:"+stateString() + " count:"+recordCount(); 
	}
	public int recordCount() {
		return mRecordCount;
	}

	public int id() {
		return mChunkId;
	}

	public VirtualChunk prev() {
		return mPrevChunk;
	}

	public void setPrev(VirtualChunk c) {
		mPrevChunk = c;
		mIsDirty = true;
	}

	public VirtualChunk next() {
		return mNextChunk;
	}

	public void setNext(VirtualChunk c) {
		mNextChunk = c;
		mIsDirty = true;
	}

	public VirtualChunk ancestor() {
		return mAncestor;
	}

	public void setAncestor(VirtualChunk c) {
		mAncestor = c;
		mIsDirty = true;
	}

	public NewerChunk descendant() {
		return mDescendant;
	}

	public void setDescendant(NewerChunk c) {
		mDescendant = c;
		mIsDirty = true;
	}

	public boolean isStable() {
		return mFlags == ChunkHeader.STABLE;
	}

	public int state() {
		return mFlags & 7;
	}

	public void setState(int state) {
		mFlags = (mFlags & ~7) | state;
		mIsDirty = true;
	}

	public boolean isFree() {
		return state() == ChunkHeader.FREE;
	}

	public int flags() {
		return mFlags;
	}

	public boolean hasContinuousData() {
		return ((mFlags >> 3) & 1) == 1;
	}

	public void setContinuousData(boolean continuous) {
		mFlags = continuous ? (mFlags|(1<<3)) : (mFlags&(~(1<<3)));
		mIsDirty = true;
	}

	public boolean needSyncHeader() {
		return mIsDirty;
	}

	public boolean needSyncRecord() {
		return false;
	}

	public int compareTo(Object o) {
		VirtualChunk other = (VirtualChunk)o;
		return id() - other.id();
	}

	private String stateString() {
		switch (state()) {
		case ChunkHeader.STABLE: return "STABLE";
		case ChunkHeader.NEWER: return "NEWER";
		case ChunkHeader.OLDER: return "OLDER";
		case ChunkHeader.FREE: return "FREE";
		default: Debug.assertTrue(false);
			return "";
		}
	}
}
