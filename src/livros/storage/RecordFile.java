package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.Record;
import livros.db.Table;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.RandomAccessFile;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
  record file header
  4: 'rech'
  4: total record count
  4: record chunk count
  4: head id
  4: free head id
*/
class RecordFile
{
	private static final byte[] MAGIC = {'r', 'e','c','h'};
	private static final int HEADER_SIZE = ChunkHeader.CHUNK_SIZE;//4+4+4+4+4;

	Table mTable;
	RandomAccessFile mFile;
	int mTotalRecordCount;
	int mChunkCount;
	int mHeadChunkId;
	int mFreeHeadChunkId;

	private static byte[] chunkBuf = new byte[ChunkHeader.CHUNK_SIZE];

	public RecordFile(Table t) {
		mTable = t;
		try {
			mFile = new RandomAccessFile(StorageManager.DB_DIR + mTable.name(), "rw");
			if (mFile.length() == 0) {
				writeHeader(0, 0, 0, 0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			mFile.close();
		} catch (IOException ex) {
			Log.e("failed to close index file");
		}
	}

	public Table table() {
		return mTable;
	}

	public int recordCount() {
		return mTotalRecordCount;
	}

	public int chunkCount() {
		return mChunkCount;
	}

	public int headChunkId() {
		return mHeadChunkId;
	}

	public int freeHeadChunkId() {
		return mFreeHeadChunkId;
	}

	public void reset()  {
		try {
			mFile.length();//workaround for avian bug
			mFile.seek(0);
		} catch(Exception ex) {
			ex.printStackTrace();//Log.e("failed to seek record file");
		}
	}

	public boolean readHeader() throws Exception {
		reset();
		byte[] buf = new byte[4];
		mFile.read(buf, 0, 4);
		if (!Arrays.equals(buf, MAGIC)) {
			Log.e("record data file is corrupted");
			return false;
		}
		mTotalRecordCount = mFile.readInt();
		mChunkCount = mFile.readInt();
		mHeadChunkId = mFile.readInt();
		mFreeHeadChunkId = mFile.readInt();
		return true;
	}

	public ChunkHeader readChunkHeader(int chunkid) {
		if (mChunkCount < chunkid || chunkid < 1) {
			return null;
		}

		try {
			long offset = HEADER_SIZE + ChunkHeader.CHUNK_SIZE * (chunkid-1); 
			mFile.seek(offset);
			return ChunkHeader.readFromStream(mFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}			
	}

	public int readRecords(int chunkid, int count, List list) {
		//Log.d("readRecords id " + chunkid + "  count " +  count);
		//new Throwable().printStackTrace();
		Debug.assertTrue(mTable.fieldList().size() > 0);
		try {
			long offset = HEADER_SIZE + ChunkHeader.CHUNK_SIZE * (chunkid-1) + ChunkHeader.HEADER_SIZE;
			Debug.assertTrue(offset < mFile.length());
			//dump(offset);
			mFile.seek(offset);

			long startPos = mFile.getFilePointer();

			mFile.readFully(chunkBuf, 0, ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE);
			InputStream is = new ByteArrayInputStream(chunkBuf, 0, ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE);
			DataInputStream din = new DataInputStream(is);
			for (int i = 0; i < count; i++) {
				Record r = RecordConverter.readRecord(din, mTable);
				r.setPosition(i, chunkid);
				list.add(r);
			}
			int read = ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE;//(int)(mFile.getFilePointer() - startPos);
			Debug.assertTrue(read <= (ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE));
			StorageManager.instance().diagAddReadRecordBytes(read);
			return read;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}			
	}

	public Record readRecord(int chunkid, int index) {
		Log.v("readRecord " + chunkid + "  "+  index);
		Debug.assertTrue(mTable.fieldList().size() > 0);
		try {
			long offset = HEADER_SIZE + ChunkHeader.CHUNK_SIZE * (chunkid-1) + ChunkHeader.HEADER_SIZE;
			Debug.assertTrue(offset < mFile.length());
			//dump(offset);
			mFile.seek(offset);
			//seek to target record
			int size = 0;
			for (int i = 0; i < index; i++) {
				size += mFile.readInt() + 4;//record size + size field(4)
				mFile.seek(offset + size);
			}
			Record r = RecordConverter.readRecord(mFile, mTable);
			r.setPosition(index, chunkid);

			StorageManager.instance().diagAddReadRecordBytes(RecordConverter.recordSize(r));
			return r;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}			
	}

	public boolean writeHeader(int recordCount, int chunkCount, int headChunkId, int freeChunkId) {
		reset();
		mTotalRecordCount = recordCount;
		mChunkCount = chunkCount;
		mHeadChunkId = headChunkId;
		mFreeHeadChunkId = freeChunkId;

		try {
			mFile.write(MAGIC);
			mFile.writeInt(recordCount);
			mFile.writeInt(chunkCount);
			mFile.writeInt(headChunkId);
			mFile.writeInt(freeChunkId);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void writeChunkHeader(ChunkHeader header) {
		if (mChunkCount < header.id()) {
			mChunkCount = header.id();
		}

		try {
			long offset = HEADER_SIZE + ChunkHeader.CHUNK_SIZE * (header.id()-1);
			if (mFile.length() < offset+ChunkHeader.CHUNK_SIZE) {
				//avian has not setLength()
				//mFile.setLength(offset+ChunkHeader.CHUNK_SIZE);
				mFile.seek(offset);
				mFile.write(new byte[ChunkHeader.CHUNK_SIZE]);
			}
			mFile.seek(offset);
			ChunkHeader.writeToStream(mFile, header);
		} catch (Exception ex) {
			ex.printStackTrace();
		}			
	}

	public int writeRecords(int chunkid, List records) {
		Debug.assertTrue(mTable.fieldList().size() > 0);
		try {
			long offset = HEADER_SIZE + ChunkHeader.CHUNK_SIZE * (chunkid-1) + ChunkHeader.HEADER_SIZE;
			Debug.assertTrue(offset <= mFile.length());
			mFile.seek(offset);
			long startPos = mFile.getFilePointer();
			for (int i = 0; i < records.size(); i++) {
				Record r = (Record)records.get(i);
				r.setPosition(i, chunkid);
				RecordConverter.writeRecord(mFile, r);
			}
			int wrote = (int)(mFile.getFilePointer() - startPos);
			Debug.assertTrue(wrote <= (ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE));
			return wrote;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}			
	}

	public void clearRecords(int chunkid) {
		try {
			long offset = HEADER_SIZE + ChunkHeader.CHUNK_SIZE * (chunkid-1) + ChunkHeader.HEADER_SIZE;
			Debug.assertTrue(offset < mFile.length());
			mFile.seek(offset);
			mFile.write(new byte[ChunkHeader.CHUNK_SIZE - ChunkHeader.HEADER_SIZE]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}			
	}

	void dump(long offset) {
		try {
			mFile.seek(offset);
			byte[] dumpbuf = new byte[(int)(mFile.length()-offset)];
			mFile.read(dumpbuf);
			Debug.hexDump(dumpbuf, (int)offset);
		} catch (Exception e) {
		}
	}
}
