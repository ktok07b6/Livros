package livros.db;

import livros.Debug;
import livros.Log;
import livros.Livros;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.List;

public final class Record implements Cloneable, Comparable {
	public class Position {
		public int index;
		public int chunkid;
		public Position(int i, int c) {
			index = i; chunkid = c;
		}
	}

	int mId;
	FieldList mFields;
	Position mPosition;
	Value mValues[];
	int mRefCounts[];

	public Record(Table t, int recid, int index, int chunkid) {
		mId = recid;
		mFields = t.fieldList();
		mPosition = new Position(index, chunkid);
		mValues = new Value[mFields.size()];
		mRefCounts = new int[mFields.size()];
		for (int i = 0; i < mValues.length; i++) {
			mValues[i] = Value.undefined;
		}
	}

	public int id() {
		return mId;
	}

	public FieldList fieldList() {
		return mFields;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		return toString().equals(o.toString());
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		//sb.append(super.toString() + " ");
		if (Livros.DEBUG) {
			sb.append("[" + mId + "] |");
		}

		if (mFields.hasOrder()) {
			for (int i = 0; i < mFields.size(); i++) {
				int ii = mFields.order(i);
 				Field f = mFields.get(ii);
				int width = f.columnWidth();
				String valStr = mValues[ii].toString();
				/* FIXME
				if (width > valStr.length()) {
					char spc[] = new char[width - valStr.length()];
					Arrays.fill(spc, ' ');
					sb.append(new String(spc));
				}
				*/
				sb.append(valStr);

				if (Livros.DEBUG) {
					if (mRefCounts[ii] > 0) {
						sb.append(":Ref("+mRefCounts[ii]+")");
					}
				}
				if (i+1 < mValues.length) {
					sb.append("|");
				}
			}
		} else {
			for (int i = 0; i < mValues.length; i++) {
				Field f = mFields.get(i);
				int width = f.columnWidth();
				String valStr = mValues[i].toString();
				/* FIXME
				if (width > valStr.length()) {
					char spc[] = new char[width - valStr.length()];
					Arrays.fill(spc, ' ');
					sb.append(new String(spc));
				}
				*/
				sb.append(valStr);

				if (Livros.DEBUG) {
					if (mRefCounts[i] > 0) {
						sb.append(":Ref("+mRefCounts[i]+")");
					}
				}
				if (i+1 < mValues.length) {
					sb.append("|");
				}
			}
		}

		if (Livros.DEBUG) {
			if (mPosition != null) {
				sb.append(" ("+mPosition.index+","+mPosition.chunkid+")");
			}
		}
		return sb.toString();
	}

	public void unload() {
		mValues = null;
	}

	public boolean isLoaded() {
		return mValues != null;
	}

	public void load(Value[] values) {
		mValues = new Value[values.length];
		mRefCounts = new int[values.length];
		for (int i = 0 ; i < values.length; i++) {
			mValues[i] = values[i];
			mRefCounts[i] = 0;
		}
	}

	public void set(String fieldName, Value v) {
		int i = mFields.getIndex(fieldName);
		//Debug.assertTrue(mRefCounts[i] == 0);
		mValues[i] = v;
	}

	public void set(int index, Value v) {
		//Debug.assertTrue(mRefCounts[index] == 0);
		mValues[index] = v;
 	}

	public Value get(String fieldName) {
		int i = mFields.getIndex(fieldName);
		return mValues[i];
	}

	public Value get(int index) {
		return mValues[index];
	}

	public Value[] values() {
		return mValues;

	}

	public void setPosition(int index, int chunkid) {
		//Log.v("setPosition " + this + " index " + index + " chunkid " + chunkid);
		mPosition = new Position(index, chunkid);
	}

	public int index() {
		return mPosition.index;
	}

	public int chunkid() {
		return mPosition.chunkid;
	}

	public void incReference(String fieldName) {
		int i = mFields.getIndex(fieldName);
		mRefCounts[i]++;
	}

	public void decReference(String fieldName) {
		int i = mFields.getIndex(fieldName);
		Debug.assertTrue(mRefCounts[i] > 0);
		mRefCounts[i]--;
	}

	public void setRefCounts(List refCounts) {
		Iterator iter = refCounts.iterator();
		for (int i = 0; i < mFields.size(); i++) {
			Field f = mFields.get(i);
			if (f.isReferenced()) {
				Debug.assertTrue(iter.hasNext());
				Integer refcount = (Integer)iter.next();
				mRefCounts[i] = refcount.intValue();
			}
		}
	}

	public int refCount() {
		int refCount = 0;
		for (int i = 0; i < mFields.size(); i++) {
			Field f = mFields.get(i);
			int idx = mFields.getIndex(f.name());
			refCount += mRefCounts[idx];
		}
		return refCount;
	}

	public int refCount(String fieldName) {
		int idx = mFields.getIndex(fieldName);
		return mRefCounts[idx];
	}

	public int refCount(int index) {
		return mRefCounts[index];
	}

	public List collectRefTarget() {
		List li = new ArrayList();
		for (int i = 0; i < mValues.length; i++) {
			Value v = mValues[i];
			if (v.isReference()) {
				RefValue refv = (RefValue)v;
				li.add(new Integer(refv.refId()));
			}
		}
		return li;
	}

	public Object clone() {
		Record cln = null;
		try {
			cln = (Record)super.clone();
		} catch (CloneNotSupportedException e) {
			Debug.assertTrue(false);
		}
		int sz = this.mFields.size();
		cln.mValues = new Value[sz];
		for (int i = 0; i < sz; i++) {
			cln.mValues[i] = this.mValues[i];
		}
		
		return cln;
	}

	public int compareTo(Object o) {
		Record other = (Record)o;
		return id() - other.id();
	}

}
