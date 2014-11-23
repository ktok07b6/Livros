package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.DataBase;
import livros.db.IReadOnlyTable;
import livros.db.Table;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

public class StorageManager
{
	public static String DB_DIR = "./";
	public static String DB_FILE = "default.db";
	public static String TMP_DIR = "./";
	public static String TMP_FILE_PREFIX = "#livros_tmp#";

	public static final int DB_VERSION = 1;

	private static StorageManager sStorageManager;

	public static StorageManager instance() {
		if (sStorageManager == null) {
			sStorageManager = new StorageManager();
		}
		return sStorageManager;
	}

	private DataBase mDataBase;
	private DbFile mDbFile;
	private Map mTableStorageMap = new HashMap();
	private Map mIndexFileMap = new HashMap();

	protected StorageManager() {
		mDbFile = new DbFile(DB_DIR, DB_FILE);
		clearAllTmpTable(true);
	}
	
	public TableStorage tableStorage(Table t) {
		if (mTableStorageMap.containsKey(t)) {
			return (TableStorage)mTableStorageMap.get(t);
		} else {
			TableStorage ts = new TableStorage(t);
			mTableStorageMap.put(t, ts);
			return ts;
		}
	}

	public IndexFile indexFile(String name) {
		if (mIndexFileMap.containsKey(name)) {
			IndexFile ifile = (IndexFile)mIndexFileMap.get(name);
			ifile.reopen();
			return ifile;
		} else {
			IndexFile ifile = new IndexFile(name);
			mIndexFileMap.put(name, ifile);
			return ifile;
		}
	}

	public void deleteTable(Table t) {
		mTableStorageMap.remove(t);
		File file = new File(DB_DIR+t.name());
		if (file.exists()) {
			file.delete();
		}
	}

	public void deleteTmpTable(IReadOnlyTable t) {
		Log.v("StorageManager#deleteTmpTable ??? " +t.name());
		IndexFile ifile = (IndexFile)mIndexFileMap.remove(t.name());
		if (ifile != null) {
			ifile.close();
			ifile.delete();
			Log.v("StorageManager#deleteTmpTable " +t.name());
		}
		/*
		File file = new File(TMP_DIR+t.name());
		if (file.exists()) {
			file.delete();
		}
		*/
	}

	public boolean existTable(Table t) {
		File file = new File(DB_DIR+t.name());
		return file.exists();
	}

	public DataBase loadDataBase() {
		if (mDataBase == null) {
			if (new File(DB_DIR+DB_FILE).exists() == false) {
				mDbFile.saveHeader(new DataBase(DB_FILE));
			}
			mDataBase = mDbFile.loadHeader();
		}
		return mDataBase;
	}

	public void cleanup() {
		clearAllTmpTable(false);
		mIndexFileMap.clear();
		mTmpFileCount = 0;
	}

	public void close() {
		cleanup();
		mDataBase = null;
	}

	public boolean isLoaded() {
		return mDataBase != null;
	}

	public void saveDataBase() {
		mDbFile.saveHeader(mDataBase);
	}

	private void clearAllTmpTable(boolean bootTime) {
		if (bootTime) {
			File dbDir = new File(TMP_DIR);
			File files[] = dbDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (!f.isFile()) {
					continue;
				}
				String name = f.getName();
				if (name.startsWith(TMP_FILE_PREFIX)) {
					Log.v("StorageManager#clearAllTmpTable delete tmp file " +name);
					f.delete();
				}
			}
		} else {
			Iterator iter = mIndexFileMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry)iter.next();
				IndexFile ifile = (IndexFile)entry.getValue();
				ifile.close();
				ifile.delete();
				Log.v("StorageManager#clearAllTmpTable delete tmp file " + entry.getKey());
			}
		}
	}
	
	private int mTmpFileCount;
	public String newIndexFileName() {
		return TMP_FILE_PREFIX + mTmpFileCount++;
	}

	int mDiagReadRecordBytes;
	public void diagResetReadRecordBytes() {
		mDiagReadRecordBytes = 0;
	}
	public void diagAddReadRecordBytes(int bytes) {
		mDiagReadRecordBytes += bytes;
	}
	public int diagReadRecordBytes() {
		return mDiagReadRecordBytes;
	}
}
