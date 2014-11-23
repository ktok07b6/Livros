package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.RecordIndex;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
  record file header
  4: 'ridx'
  4: total record count
*/
public class IndexFile
{
	private static final byte[] MAGIC = {'r', 'i','d','x'};
	private static final int HEADER_SIZE = 4+4;

	String mName;
	RandomAccessFile mFile;
	int mTotalRecordCount;
	int mWritePos;
	int mReadPos;

	public IndexFile(String name) {
		mName = name;
		try {
			File f = new File(StorageManager.TMP_DIR + name);
			Debug.assertTrue(!f.exists());
			mFile = new RandomAccessFile(StorageManager.TMP_DIR + name, "rw");
			//Log.d("OPEN " + name);
			writeHeader(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("failed to create index file");
		}
	}

	public void reopen() {
		try {
			mFile = new RandomAccessFile(StorageManager.TMP_DIR + mName, "rw");
			//Log.d("RE-OPEN " + mName);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("failed to re-open index file");
		}
	}

	public void close() {
		try {
			mFile.close();
			//Log.d("CLOSE " + mName);
		} catch (IOException ex) {
			Log.e("failed to close index file");
		}
	}

	public void delete() {
		try {
			File f = new File(StorageManager.TMP_DIR + mName);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception ex) {
			Log.e("failed to close index file");
		}
	}

	public int recordCount() {
		return mTotalRecordCount;
	}

	public void reset()  {
		try {
			mFile.length();//workaround for avian bug
			mFile.seek(0);
			mWritePos = HEADER_SIZE;
			mReadPos = HEADER_SIZE;
		} catch(Exception ex) {
			ex.printStackTrace();
			Log.e("failed to seek index file");
		}
	}

	public boolean readHeader() throws Exception {
		reset();
		byte[] buf = new byte[4];
		mFile.read(buf, 0, 4);
		if (!Arrays.equals(buf, MAGIC)) {
			Log.e("index file is corrupted");
			return false;
		}
		mTotalRecordCount = mFile.readInt();
		return true;
	}

	public void readIndices(int count, List indices) {
		try {
			if (mReadPos >= mFile.length()) {
				return;
			}
			mFile.seek(mReadPos);

			for (int i = 0; i < count; i++) {
				RecordIndex ri = RecordConverter.readRecordIndex(mFile);
				indices.add(ri);
			}
			mReadPos = (int)(mFile.getFilePointer());
		} catch (Exception ex) {
			ex.printStackTrace();
		}			
	}

	public boolean writeHeader(int recordCount) {
		reset();
		mTotalRecordCount = recordCount;
		try {
			mFile.write(MAGIC);
			mFile.writeInt(recordCount);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void writeIndices(List indices) {
		try {
			Debug.assertTrue(mWritePos <= mFile.length());
			mFile.seek(mWritePos);

			for (int i = 0; i < indices.size(); i++) {
				RecordIndex ri = (RecordIndex)indices.get(i);
				RecordConverter.writeRecordIndex(mFile, ri);
			}
			mWritePos = (int)(mFile.getFilePointer());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dump(long offset) {
		try {
			mFile.seek(offset);
			byte[] dumpbuf = new byte[(int)(mFile.length()-offset)];
			mFile.read(dumpbuf);
			Debug.hexDump(dumpbuf);
		} catch (Exception e) {
		}
	}
}
