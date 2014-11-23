package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.DataBase;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

class DbFile
{
	public static DbFile dbFile(String fileName) {
		return new DbFile(fileName);
	}

	String mDir;
	String mFileName;
	
	public DbFile(String fileName) {
		this(StorageManager.DB_DIR, fileName);
	}

	public DbFile(String dir, String fileName) {
		mDir = dir;
		mFileName = fileName;
	}

	public DataBase loadHeader() {
		try {
			if (new File(StorageManager.DB_DIR+mFileName).exists() == false) {
				return null;
			}
			RandomAccessFile input = new RandomAccessFile(StorageManager.DB_DIR+mFileName, "r");
			DbHeader dbHdr = new DbHeader(mFileName);
			DataBase db = dbHdr.read(input);
			input.close();
			return db;
		} catch (Exception ex) {
			Log.e(ex.toString());
			ex.printStackTrace();
			return null;
		}
	}

	public boolean saveHeader(DataBase db) {
		try {
			File file = new File(StorageManager.DB_DIR+mFileName);
			if (file.exists()) {
				file.delete();
			}

			RandomAccessFile output = new RandomAccessFile(StorageManager.DB_DIR+mFileName, "rw");
			DbHeader dbHdr = new DbHeader(mFileName);
			dbHdr.write(output, db);
			output.close();

			return true;
		} catch (Exception ex) {
			Log.e(ex.toString());
			ex.printStackTrace();
			return false;
		}
	}

	static String align(String s, int sz) {
		if (sz < s.length()) {
			return s.substring(0, sz);
		} else if (s.length() < sz) {
			char spc[] = new char[sz - s.length()];
			Arrays.fill(spc, ' ');
			return s + new String(spc);
		} else {
			return s;
		}
	}
}
