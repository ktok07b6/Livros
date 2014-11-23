package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.DataBase;
import livros.db.Table;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
db header
  4: 'dtbs'
  4: version
  32: db name
  20: reserved
  4: table count
  4*x: table header offsets
*/
class DbHeader
{
	public static final int DBNAME_BYTES = 32;
	public static final int RESERVED_BYTES = 20;
	public static final int HEADER_SIZE = 4 + 4 + DBNAME_BYTES + RESERVED_BYTES + 4;
	public static final byte[] MAGIC = {'d', 't','b','s'};

	int mVersion;
	String mDbName;

	public DbHeader(String dbName) {
		mDbName = dbName;
	}

	public int version() {
		return mVersion;
	}

	public void setVersion(int ver) {
		mVersion = ver;
	}

	public DataBase read(RandomAccessFile input) {
		byte[] buf = new byte[32];

		try {
			//magic "dtbs"
			int read = input.read(buf, 0, 4);
			if (read != 4) {
				return null;
			}
			for (int i = 0; i < 4; i++) {
				if (buf[i] != MAGIC[i]) return null;
			}

			//version
			mVersion = input.readInt();

			//database name
			read = input.read(buf, 0, DBNAME_BYTES);
			if (read != DBNAME_BYTES) {
				return null;
			}

			String name = new String(buf, "UTF-8").trim();
			DataBase db = new DataBase(name);

			//reserved
			read = input.skipBytes(RESERVED_BYTES);
			if (read != RESERVED_BYTES) {
				return null;
			}
			
			//table count
			final int tableCount = input.readInt();
			
			int [] tableHeaderOffsets = new int[tableCount];
			for (int i = 0; i < tableCount; i++) {
				tableHeaderOffsets[i] = input.readInt();
			}
			readTables(input, db, tableHeaderOffsets);

			return db;
		} catch (IOException ex) {
			ex.printStackTrace();			
		}

		return null;
	}

	public void readTables(RandomAccessFile input, DataBase db, int [] tableHeaderOffsets) throws IOException {
		for (int i = 0; i < tableHeaderOffsets.length; i++) {
			input.seek(tableHeaderOffsets[i]);
			TableHeader tabHdr = new TableHeader(mDbName);
			Table t = tabHdr.read(input);
			t.init();
			db.addTable(t);
		}
	}

	public boolean write(RandomAccessFile output, DataBase db) {
		try {
			//magic "dtbs"
			output.write(MAGIC);

			//version
			output.writeInt(StorageManager.DB_VERSION);

			//database name
			output.writeBytes(DbFile.align(db.name(), DBNAME_BYTES));

			//reserved
			for (int i = 0; i < RESERVED_BYTES; i++) {
				output.writeByte(0);
			}

			List tables = db.tables();
			//table count
			output.writeInt(tables.size());

			//dummy table offsets
			final long tableOffsetPos = output.getFilePointer();
			for (int i = 0; i < tables.size(); i++) {
				output.writeInt(-1);
			}

			
			int[] offsets = writeTables(output, tables);
			final long fileEndPos = output.getFilePointer();
			
			//real table offsets
			output.seek(tableOffsetPos);
			for (int i = 0; i < tables.size(); i++) {
				output.writeInt(offsets[i]);
			}

			output.seek(fileEndPos);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;
	}

	int[] writeTables(RandomAccessFile output, List tables) throws IOException {
		int[] offsets = new int[tables.size()];
		for (int i = 0; i < tables.size(); i++) {
			offsets[i] = (int)output.getFilePointer();
			TableHeader tabHdr = new TableHeader(mDbName);
			tabHdr.write(output, (Table)tables.get(i));
		}
		return offsets;
	}

}
