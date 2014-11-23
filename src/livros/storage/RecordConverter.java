package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.Record;
import livros.db.RecordIndex;
import livros.db.Table;
import livros.db.Value;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/*
  record
  4: record size
  4: rec-id
  4*num of field: reference counts
  *: record body

 */

/*
  record index
  4: rec-id
  4: index in chunk
  4: chunkid
 */

class RecordConverter
{
	private static final boolean TEST = false;
	private static final int TEST_PADDING = 1000;

	public static Record readRecord(DataInput din, Table t) throws Exception {
		int size = din.readInt();
		int recid = din.readInt();
		List refCounts = new ArrayList();
		FieldList flist = t.fieldList();
		for (int i = 0; i < flist.size(); i++) {
			Field field = flist.get(i);
			if (field.isReferenced()) {
				refCounts.add(new Integer(din.readInt()));
			}
		}
		Record r = t.createRecord(recid);
		if (TEST) {
			byte[] buf = new byte[TEST_PADDING];
			din.readFully(buf);
		}
		Debug.assertTrue(t.fieldList().size() > 0);
		Value[] values = new Value[t.fieldList().size()];
		for (int i = 0; i < t.fieldList().size(); i++) {
			values[i] = ValueConverter.readValue(din);
		}
		r.load(values);
		r.setRefCounts(refCounts);
		return r;
	}

	public static void writeRecord(DataOutput dout, Record r) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutput tmpout = new DataOutputStream(buf);

		writeRecordBody(tmpout, r);
		byte[] bodyBytes = buf.toByteArray();
		buf.reset();
		//ref count place
		FieldList flist = r.fieldList();
		int numRefField = 0;
		for (int i = 0; i < flist.size(); i++) {
			Field field = flist.get(i);
			if (field.isReferenced()) {
				numRefField++;
			}
		}
		//as offset to a next record
		dout.writeInt(bodyBytes.length + 4 + (4*numRefField));
		//rec id
		dout.writeInt(r.id());
		//ref count
		for (int i = 0; i < flist.size(); i++) {
			Field field = flist.get(i);
			if (field.isReferenced()) {
				dout.writeInt(r.refCount(i));
			}
		}
		//data body
		dout.write(bodyBytes);
	}

	private static void writeRecordBody(DataOutput dout, Record rec) throws Exception {
		if (TEST) {
			byte[] pad = new byte[TEST_PADDING];
			Arrays.fill(pad, (byte)0xcc);
			dout.write(pad);
		}
		Value[] values = rec.values();
		for (int i = 0; i < values.length; i++) {
			ValueConverter.writeValue(dout, values[i]);
		}
	}

	public static int recordSize(Record r) {
		int size = 8;//size & rec id

		//ref count
		FieldList flist = r.fieldList();
		for (int i = 0; i < flist.size(); i++) {
			Field field = flist.get(i);
			if (field.isReferenced()) {
				size += 4;
			}
		}

		Value[] values = r.values();
		for (int i = 0; i < values.length; i++) {
			size += ValueConverter.valueSize(values[i]);
		}
		return size;
	}

	public static RecordIndex readRecordIndex(DataInput din) throws Exception {
		int recid = din.readInt();
		int index = din.readInt();
		int chunkid = din.readInt();
		return new RecordIndex(recid, index, chunkid);
	}

	public static void writeRecordIndex(DataOutput dout, RecordIndex ri) throws Exception {
		dout.writeInt(ri.recid);
		dout.writeInt(ri.index);
		dout.writeInt(ri.chunkid);
	}
}
