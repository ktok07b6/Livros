package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.Constraint;
import livros.db.PrimaryConst;
import livros.db.NotNullConst;
import livros.db.UniqueConst;
import livros.db.Field;
import livros.db.RefConst;
import livros.db.Table;
import livros.db.Type;
import livros.db.Value;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
  4: 'fild'
  4: field def size
  32: field name
  2: type
  1: is ref field
  *: default int/str value
  1: constraint count
  *: constraints
*/
class FieldHeader
{
	public static final int FIELDNAME_BYTES = 32;
	public static final int HEADER_FIX_SIZE = 4 + 4 + FIELDNAME_BYTES + 4;
	public static final byte[] MAGIC = {'f', 'i', 'l', 'd'};

	int mTotalHeaderSize;

	public FieldHeader() {
	}

	public int totalHeaderSize() {
		return mTotalHeaderSize;
	}

	public void setTotalHeaderSize(int sz) {
		mTotalHeaderSize = sz;
	}

	public String toString() {
		return "";
	}

	public Field read(RandomAccessFile input) {
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

			//field size
			final int totalHeaderSize = input.readInt();

			//field name
			read = input.read(buf, 0, FIELDNAME_BYTES);
			if (read != FIELDNAME_BYTES) {
				return null;
			}
			final String name = new String(buf, "UTF-8").trim();

			//type
			final Type type = readType(input);

			//is reference field
			final int isRef = input.readByte();

			//default value
			final Value defValue = ValueConverter.readValue(input);

			//constraints
			final List constraints = readConstraints(input);

			Field f = new Field(name, type, defValue, constraints);
			if (isRef != 0) {
				f.setReferencedField();
			}
			return f;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	Type readType(RandomAccessFile input) throws IOException{
		final int hashCode = input.readUnsignedByte();
		final int capacity = input.readUnsignedByte();
		switch (hashCode) {
		case 'I': return Type.integerType;
		case 'F': return Type.fixedChar(capacity);
		case 'V': return Type.varChar(capacity);
		default: Debug.assertTrue(false); return null;
		}
	}

	List readConstraints(RandomAccessFile input) throws IOException {
		int constsSize = input.readUnsignedByte();
		ArrayList consts = new ArrayList();
		for (int i = 0; i < constsSize; i++) {
			int c = input.readByte();
			switch (c) {
			case 'P':
				consts.add(Constraint.primaryConst);
				break;
			case 'N':
				consts.add(Constraint.notNullConst);
				break;
			case 'U':
				consts.add(Constraint.uniqueConst);
				break;
			case 'R':
				byte[] buf = new byte[32];
				int read = input.read(buf, 0, TableHeader.TABNAME_BYTES);
				if (read != TableHeader.TABNAME_BYTES) {
					throw new IOException("cannot read table name of reference constraint");
				}
				final String table = new String(buf, "UTF-8").trim();

				buf = new byte[32];
				read = input.read(buf, 0, FIELDNAME_BYTES);
				if (read != FIELDNAME_BYTES) {
					throw new IOException("cannot read field name of reference constraint");
				}
				final String field = new String(buf, "UTF-8").trim();
				consts.add(new RefConst(table, field));
				break;
			default:
				Debug.assertTrue(false);
			}
		}
		return consts;
	}

	public boolean write(RandomAccessFile output, Field f) {
		int pos = 0;
		byte[] buf = new byte[32];

		try {
			final long headerStartPos = output.getFilePointer();
			//magic "fild"
			output.write(MAGIC);

			//dummy header size
			final long headerSizePos = output.getFilePointer();
			output.writeInt(-1);//dummy

			//filed name
			output.writeBytes(DbFile.align(f.name(), FIELDNAME_BYTES));
			
			//type
			writeType(output, f.type());
		
			//is ref field
			output.writeByte(f.isReferenced() ? 1:0);

			ValueConverter.writeValue(output, f.defaultValue());
			
			writeConstraints(output, f.constraints());
			final long headerEndPos = output.getFilePointer();

			//real header size
			output.seek(headerSizePos);
			output.writeInt((int)(headerEndPos - headerStartPos));

			output.seek(headerEndPos);
			
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	void writeType(RandomAccessFile output, Type t) throws IOException{
		output.writeByte(t.hashCode());
		Debug.assertTrue(t.capacity() <= 256);
		output.writeByte(t.capacity());
	}

	void writeConstraints(RandomAccessFile output, List consts) throws IOException {
		output.writeByte(consts.size());
		for (int i = 0; i < consts.size(); i++) {
			Constraint c = (Constraint)consts.get(i);
			if (c.isPrimary()) {
				output.writeByte('P');
			} else if (c.isNotNull()) {
				output.writeByte('N');
			} else if (c.isUnique()) {
				output.writeByte('U');
			} else if (c.isReference()) {
				RefConst refc = (RefConst)c;
				output.writeByte('R');
				output.writeBytes(DbFile.align(refc.table(), TableHeader.TABNAME_BYTES));
				output.writeBytes(DbFile.align(refc.field(), FIELDNAME_BYTES));
			} else {
				Debug.assertTrue(false);
			}
		}
	}

}
