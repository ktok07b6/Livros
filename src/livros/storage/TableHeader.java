package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.Table;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
  4: 'tabl'
  4: table header size
  32: tab name (is table body file name)
  28: reserved
  4: field def count
  4*x: field def offsets
*/
class TableHeader
{
	public static final int TABNAME_BYTES = 32;
	public static final int RESERVED_BYTES = 20;
	public static final int HEADER_FIX_SIZE = 4 + 4 + TABNAME_BYTES + RESERVED_BYTES + 4;
	public static final byte[] MAGIC = {'t', 'a', 'b', 'l'};

	String mDbName;

	public TableHeader(String dbName) {
		mDbName = dbName;
	}

	public Table read(RandomAccessFile input) {
		byte[] buf = new byte[32];

		try {
			//magic "tabl"
			int read = input.read(buf, 0, 4);
			if (read != 4) {
				return null;
			}
			for (int i = 0; i < 4; i++) {
				if (buf[i] != MAGIC[i]) return null;
			}

			//header size
			final int totalHeaderSize = input.readInt();

			//database name
			read = input.read(buf, 0, TABNAME_BYTES);
			if (read != TABNAME_BYTES) {
				return null;
			}
			final String name = new String(buf, "UTF-8").trim();
			Table t = new Table(mDbName, name);

			//reserved
			read = input.skipBytes(RESERVED_BYTES);
			if (read != RESERVED_BYTES) {
				return null;
			}
			
			//field count
			final int fieldCount = input.readInt();
			
			int [] fieldHeaderOffsets = new int[fieldCount];
			for (int i = 0; i < fieldCount; i++) {
				fieldHeaderOffsets[i] = input.readInt();
			}
			
			readFields(input, t, fieldHeaderOffsets);
			
			return t;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public void readFields(RandomAccessFile input, Table t, int [] tableHeaderOffsets) throws IOException {
		FieldList fields = new FieldList();
		for (int i = 0; i < tableHeaderOffsets.length; i++) {
			input.seek(tableHeaderOffsets[i]);
			FieldHeader fieldHdr = new FieldHeader();
			Field f = fieldHdr.read(input);
			t.addField(f);
		}
	}

	public boolean write(RandomAccessFile output, Table t) {
		int pos = 0;
		byte[] buf = new byte[32];

		FieldList fields = t.fieldList();
		try {
			//magic "tabl"
			output.write(MAGIC);

			//header size 
			output.writeInt(HEADER_FIX_SIZE + fields.size()*4);

			//table name
			output.writeBytes(DbFile.align(t.name(), TABNAME_BYTES));

			//reserved
			for (int i = 0; i < RESERVED_BYTES/4; i++) {
				output.writeInt(0);
			}

			//field count
			output.writeInt(fields.size());

			final long fieldOffsetPos = output.getFilePointer();
			for (int i = 0; i < fields.size(); i++) {
				output.writeInt(-1);//dummy
			}

			int[] offsets = writeFields(output, fields);
			final long headerEndPos = output.getFilePointer();

			Debug.assertTrue(offsets.length == fields.size());
			//real field offsets
			output.seek(fieldOffsetPos);
			for (int i = 0; i < fields.size(); i++) {
				output.writeInt(offsets[i]);
			}

			output.seek(headerEndPos);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;
	}

	int[] writeFields(RandomAccessFile output, FieldList fields) throws IOException {
		int[] offsets = new int[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			offsets[i] = (int)output.getFilePointer();
		    FieldHeader fieldHdr = new FieldHeader();
			fieldHdr.write(output, fields.get(i));
		}

		return offsets;
	}
}
