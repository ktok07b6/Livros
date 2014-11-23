package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.FixedCharValue;
import livros.db.IntegerValue;
import livros.db.RefValue;
import livros.db.TextValue;
import livros.db.Value;
import livros.db.VarCharValue;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
  value
  1: val id

  int value
  4: signed integer
  
  fixchar value
  1: capacity
  *: characters
  
  varchar value
  1: capacity
  1: length
  *: characters
  
  null value
  0: ---
  
  ref value
  4: reference target recid
  *: reference value
  */

class ValueConverter 
{
	public static Value readValue(DataInput input) throws IOException {
		int hashCode = input.readByte();
		int capacity;
		byte[] buf;
		switch (hashCode) {
		case 'I':
			return new IntegerValue(input.readInt());
		case 'F': 
			capacity = input.readUnsignedByte();
			buf = new byte[capacity];
			input.readFully(buf);
			return new FixedCharValue(capacity, new String(buf, "UTF-8"));
		case 'V':
			capacity = input.readUnsignedByte();
			int length = input.readUnsignedByte();
			buf = new byte[length];
			input.readFully(buf);
			return new VarCharValue(capacity, new String(buf, "UTF-8"));
		case 'N': 
			return Value.nullValue;
		case 'R': 
			int refid = input.readInt();
			return new RefValue(readValue(input), refid);
		default:
			Debug.assertTrue(false); return null;
		}
	}

	public static int writeValue(DataOutput dout, Value v) throws IOException {
		int bytes = 0;
		if (v.isReference()) {
			dout.writeByte('R');
			dout.writeInt(((RefValue)v).refId());
			bytes+=5;
		}
		dout.writeByte(v.type().hashCode());
		bytes++;
		if (v.isInteger()) {
			IntegerValue iv = v.asInteger();
			dout.writeInt(iv.intValue());
			bytes += 4;
		} else if (v.isText()) {
			TextValue tv = v.asText();
			dout.writeByte(tv.type().capacity());
			bytes += 1;
			if (v.isVarChar()) {
				dout.writeByte(tv.textValue().length());
				bytes += 1;
			}
			dout.writeBytes(tv.textValue());
			bytes += tv.textValue().length();
		} else if (v.isNull()) {
			
		} else {
			Debug.assertTrue(false);
		}
		return bytes;
	}


	public static int valueSize(Value v) {
		int size = 0;
		if (v.isReference()) {
			size += 5;
		}
		size++;
		if (v.isInteger()) {
			size += 4;
		} else if (v.isText()) {
			size += 1;
			if (v.isVarChar()) {
				size += 1;
			}
			size += ((TextValue)v).textValue().length();
		} else if (v.isNull()) {
			
		} else {
			Debug.assertTrue(false);
		}
		return size;
	}

}

