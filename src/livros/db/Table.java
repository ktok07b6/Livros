package livros.db;

import livros.Debug;
import livros.Log;
import livros.Livros;
import livros.btree.BTree;
import livros.storage.StorageManager;
import livros.storage.TableStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Table implements IReadOnlyTable
{
	String mName;
	String mDbName;
	FieldList mFields;
	boolean mWillDelete = false;
	int mMaxRecordId = -1;
	TableStorage mStorage;

	public Table(String dbName, String name) {
		mName = name;
		mDbName = dbName;
		mFields = new FieldList();
	}

	public void init() {
		mStorage = StorageManager.instance().tableStorage(this);
		mStorage.init();
		Record r = mStorage.lastRecord();
		if (r != null) {
			mMaxRecordId = r.id();
		} else {
			mMaxRecordId = -1;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(mName + " (" + mFields.toString() + ")");
		sb.append("\n");
		sb.append("number of record " + size());
		sb.append("\n");
		sb.append(mStorage.toString());
		return sb.toString();
	}

	public boolean equals(Table t) {
		return mName.equals(t.mName);
	}

	public void addField(Field f) {
		mFields.add(f);

		if (StorageManager.instance().isLoaded()) {
			RefConst refc = f.refConst();
			if (refc == null) {
				return;
			}
			DataBase db = DataBase.open(mDbName);
			Table refTable = db.table(refc.table());
			refTable.field(refc.field()).setReferencedField();
		}
	}

	public Record createRecord() {
		return new Record(this, mMaxRecordId+1, -1, -1);
	}

	public Record createRecord(int recid) {
		return new Record(this, recid, -1, -1);
	}

	public Record createRecord(int recid, int index, int chunkid) {
		Record r = new Record(this, recid, index, chunkid);
		return r;
	}

	public void commit() {
		mStorage.commit();
	}

	public Record getRecord(RecordIndex ri) {
		Record r = mStorage.getRecord(ri.index, ri.chunkid);
		Debug.assertTrue(r.id() == ri.recid);
		return r;
	}

	public boolean insertRecord(Record r) {
		if (!typeCheckAndConvert(r)) {
			return false;
		}
		if (!checkConstraint(r, null)) {
			return false;
		}
		mStorage.insert(r);
		mMaxRecordId = Math.max(mMaxRecordId, r.id());

		return true;
	}

	public boolean insertRecord(Map values) {
		Record r = createRecord();

		Iterator iter = values.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			String key = (String)entry.getKey();
			Value value = (Value)entry.getValue();
			r.set(key, value);
		}
		return insertRecord(r);
	}

	public boolean deleteRecord(Record r) {
		if (r.refCount() > 0) {
			Log.e("record |" + r.toString() + "| cannot delete the record because it is referenced by an another record");
			return false;
		}
		decReference(r, null);
		mStorage.delete(r);

		return true;
	}

	public boolean updateRecord(Record r, String fields[]) {
		Record older = mStorage.getRecord(r.index(), r.chunkid());
		//must use a clone
		Debug.assertTrue(r != older);
		for (int i = 0; i < fields.length; i++) {
			if (older.refCount(fields[i]) > 0) {
				Log.e("record |" + r.toString() + "| cannot update the record because its \"" + fields[i] + "\" field is referenced by an another record");
				return false;
			}
		}

		if (!typeCheckAndConvert(r)) {
			return false;
		}
		if (!checkConstraint(r, older)) {
			return false;
		}
		decReference(older, fields);
		mStorage.update(r);

		return true;
	}

	public boolean updateRecord(RecordIndex ri, Map newValues) {
		Record modified = (Record)getRecord(ri).clone();

		String fieldNames[] = new String[newValues.size()];
		int i = 0;
		Iterator iter = newValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			String key = (String)entry.getKey();
			Value value = (Value)entry.getValue();
			modified.set(key, value);
			fieldNames[i++] = key;
		}
		return updateRecord(modified, fieldNames);
	}

	private void decReference(Record r, String updateFields[]) {
		if (updateFields == null) {
			for (int i = 0; i < mFields.size(); i++) {
				Field f = mFields.get(i);
				decReferenceField(r, f);
			}
		} else {
			for (int i = 0; i < updateFields.length; i++) {
				Field f = mFields.get(updateFields[i]);
				decReferenceField(r, f);
			}
		}
	}
	
	private void decReferenceField(Record r, Field f) {
		RefConst refc = f.refConst();
		if (refc == null) {
			return;
		}
		DataBase db = DataBase.open(mDbName);
		Table reft = db.table(refc.table());
		RefValue refv = (RefValue)r.get(f.name());
		Record refr = reft.findById(refv.refId());
		refr.decReference(refc.field());
		reft.mStorage.update(refr);
	}
	
	private boolean typeCheckAndConvert(Record r) {
		for (int i = 0; i < mFields.size(); i++) {
			Value v = r.get(i);
			Field f = mFields.get(i);

			Type ftype = f.type();
			if ((ftype.isFixedChar() || ftype.isVarChar()) &&
				v.isText()) {
				if (ftype.isFixedChar()) {
					v = new FixedCharValue(ftype.capacity(), v.asText().textValue());
				} else {
					v = new VarCharValue(ftype.capacity(), v.asText().textValue());
				}
				r.set(i, v);
			} else if (v.isUndefined()) {
				if (f.defaultValue() != null) {
					v = f.defaultValue();
				} else {
					v = Value.nullValue;
				}
				r.set(i, v);
			}

			if (!v.isNull() && !ftype.equals(v.type())) {
				Log.e("value type is not valid " + ftype + " " + v.type());
				return false;
			}
		}
		return true;
	}

	private boolean checkConstraint(Record r, Record older) {
		for (int i = 0; i < mFields.size(); i++) {
			Value v = r.get(i);
			Field f = mFields.get(i);

			Value newV = acceptValue(f, v, older);
			if (newV == null) {
				return false;
			} else {
				r.set(i, newV);
			}
		}
		return true;
	}

	private Value acceptValue(Field f, Value v, Record old) {
		if (f.isNotNull() || f.isPrimary()) {
			if (v.isNull()) {
				Log.e("NOT_NULL constraint is not valid");
				return null;
			}
		}

		if (f.isUnique() || f.isPrimary()) {
			Record r = mStorage.findFirstRecord(f.name(), v);
			if (r != null && !r.equals(old)) {
				Log.e("UNIQUE constraint is not valid");
				return null;
			}
		}

		RefConst refc;
		if ((refc = f.refConst()) != null) {
			DataBase db = DataBase.open(mDbName);
			Table reft = db.table(refc.table());
			RefValue refv = reft.makeRefValueIfFound(refc.field(), v);
			if (refv == null) {
				Log.e("REFERENCES constraint is not valid");
				return null;
			}
			v = refv;
		}
		return v;
	}
 
	boolean delete() {
		Record r = mStorage.nextRecord(null);
		while (r != null) {
			if (r.refCount() > 0) {
				Log.e("record |" + r.toString() + "| cannot delete the table because its records is referenced by an another record");
				return false;
			}
			r = mStorage.nextRecord(r);
		}

		mWillDelete = true;
		decAllReferences();
		return mWillDelete;
	}

	private void decAllReferences() {
		Debug.assertTrue(mWillDelete);
		for (int i = 0; i < mFields.size(); i++) {
			Field f = mFields.get(i);
			RefConst refc = f.refConst();
			if (refc == null) {
				continue;
			}

			Record r = mStorage.nextRecord(null);
			while (r != null) {
				DataBase db = DataBase.open(mDbName);
				Table reft = db.table(refc.table());
				RefValue refv = (RefValue)r.get(f.name());
				Record refr = reft.findById(refv.refId());
				refr.decReference(refc.field());
				reft.mStorage.update(refr);
				r = mStorage.nextRecord(r);
			}
		}
	}

	public boolean isDeleting() {
		return mWillDelete;
	}

	private RefValue makeRefValueIfFound(String fieldName, Value v) {
		Record r = mStorage.findFirstRecord(fieldName, v);
		if (r == null) {
			return null;
		}

		r.incReference(fieldName);
		mStorage.update(r);
		RefValue refv = new RefValue(r.get(fieldName), r.id());
		return refv;
	}

	Record findById(int id) {
		return mStorage.findById(id);
	}

	int getMaxRecordId() {
		return mMaxRecordId;
	}

	public BTree btree(String fieldName) {
		return mStorage.btree(fieldName);
	}
 
	/* IReadOnlyTable interface */
	public String name() {
		return mName;
	}

	/* IReadOnlyTable interface */
	public String dbName() {
		return mDbName;
	}

	/* IReadOnlyTable interface */
	public String baseName() {
		return mName;
	}

	/* IReadOnlyTable interface */
	public Field field(String fieldName) {
		return mFields.get(fieldName);
	}

	/* IReadOnlyTable interface */
	public FieldList fieldList() {
		return mFields;
	}

	/* IReadOnlyTable interface */
	public int size() {
		return mStorage.recordCount();
	}

	/* IReadOnlyTable interface */
	public Selector selector(Expr expr) {
		return SelectorFactory.create(this, expr);
	}

	/* IReadOnlyTable interface */
	public boolean isDerivedTable() {
		return false;
	}
}
