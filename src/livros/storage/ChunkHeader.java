package livros.storage;

import livros.Debug;
import livros.Log;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/*
chunk (fixed 4096 byte)
--header--
  4: 'chnk'
  4: chunkid
  4: prev chunkid
  4: next chunkid
  4: ancestor chunkid (0 is no ancestor)
  4: descendant chunkid (0 is no descendant)
  2: flags
     bit 0-2: chunk state (0:stable 1:newer 2:older 3:volatile 7:free chunk)
     bit 3  : continuous chunk (true: having continuous data from previous chunk, 
                               false: having completed data)
     bit 4-15: reserved
  2: record count in chunk
--data--
  *: records
*/

class ChunkHeader
{
	public static int CHUNK_SIZE = 4096;
	public static final int HEADER_SIZE = 4+4+4+4+4+4+2+2;
	/* flags */
	public static final int STABLE = 0;
	public static final int NEWER = 1;
	public static final int OLDER = 2;
	public static final int VOLATILE = 3;
	public static final int FREE = 7;

	private static final byte[] mPadding = new byte[4096];
	private static final byte[] MAGIC = {'c','h','n','k'};

	int mChunkId;
	int mPrevId;
	int mNextId;
	int mAncestorId;
	int mDescendantId;
	int mFlags;
	int mRecordCount;

	protected ChunkHeader(int chunkId, 
						  int prevId, 
						  int nextId, 
						  int ancestorId, 
						  int descendantId, 
						  int flags, 
						  int recordCount) {
		mChunkId = chunkId;
		mPrevId = prevId;
		mNextId = nextId;
		mAncestorId = ancestorId;
		mDescendantId = descendantId;
		mFlags = flags;
		mRecordCount = recordCount;
	}

	public int id() {
		return mChunkId;
	}

	public int prevId() {
		return mPrevId;
	}

	public int nextId() {
		return mNextId;
	}

	public int ancestorId() {
		return mAncestorId;
	}

	public int descendantId() {
		return mDescendantId;
	}

	public int flags() {
		return mFlags;
	}

	public int state() {
		return mFlags & 7;
	}

	public int recordCount() {
		return mRecordCount;
	}

	public static ChunkHeader create(VirtualChunk vc) {
		int chunkId = vc.id();
		int prevId = vc.prev() != null ? vc.prev().id() : 0;
		int nextId = vc.next() != null ? vc.next().id() : 0;
		int ancestorId = vc.ancestor() != null ? vc.ancestor().id() : 0;
		int descendantId = vc.descendant() != null ? vc.descendant().id() : 0;
		int flags = vc.flags();
		int recordCount = vc.recordCount();
		return new ChunkHeader(chunkId, prevId, nextId, ancestorId, descendantId, flags, recordCount);
	}

	public static ChunkHeader readFromStream(DataInput din) throws Exception {
		//magic 'chnk'
		byte[] buf = new byte[4];
		din.readFully(buf);
		if (!Arrays.equals(buf, MAGIC)) {
			Log.e("chunk data is corrupted");
			return null;
		}

		int chunkId = din.readInt();
		int prevId = din.readInt();
		int nextId = din.readInt();
		int ancestorId = din.readInt();
		int descendantId = din.readInt();
		int flags = din.readUnsignedShort();
		int recordCount = din.readUnsignedShort();
		return new ChunkHeader(chunkId, prevId, nextId, ancestorId, descendantId, flags, recordCount);
	}

	public static void writeToStream(DataOutput dout, ChunkHeader header) throws Exception {
		dout.write(MAGIC);
		dout.writeInt(header.id());
		dout.writeInt(header.prevId());
		dout.writeInt(header.nextId());
		dout.writeInt(header.ancestorId());
		dout.writeInt(header.descendantId());
		dout.writeShort(header.flags());
		dout.writeShort(header.recordCount());
	}

}
