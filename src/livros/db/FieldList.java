package livros.db;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FieldList {
	HashMap mFieldMap;
	ArrayList mFieldArray;
	Set mDisableFields;
	int[] mOrder;

	class FieldListItem {
		int index;
		Field field;
		FieldListItem(int i, Field f) {
			index = i;
			field = f;
		}
	}
	
 	public FieldList() {
		mFieldMap = new HashMap();
		mFieldArray = new ArrayList();
	}

 	public FieldList(FieldList li) {
		mFieldMap = new HashMap(li.mFieldMap);
		mFieldArray = new ArrayList(li.mFieldArray);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator it = mFieldMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			FieldListItem item = (FieldListItem)entry.getValue();
			sb.append(item.field.toString());
			if (it.hasNext()) {
				sb.append(", ");
			} else {
				break;
			}
		}
		return sb.toString();
	}

	public String headerString() {
		StringBuffer sb = new StringBuffer();
		if (mOrder != null) {
			for (int i = 0; i < mOrder.length; i++) {
				int ii = mOrder[i];
				Field f = (Field)mFieldArray.get(ii);
				int width = f.columnWidth();
				String name = f.name();
				/* FIXME
				if (width > name.length()) {
					char spc[] = new char[width - name.length()];
					Arrays.fill(spc, ' ');
					sb.append(new String(spc));
				}
				*/
				sb.append(name);
				sb.append("|");
			}
		} else {
			for (int i = 0; i < mFieldArray.size(); i++) {
				Field f = (Field)mFieldArray.get(i);
				int width = f.columnWidth();
				String name = f.name();
				/* FIXME
				if (width > name.length()) {
					char spc[] = new char[width - name.length()];
					Arrays.fill(spc, ' ');
					sb.append(new String(spc));
				}
				*/
				sb.append(name);
				sb.append("|");
			}
		}
		String header = sb.toString();
		if (header.length() != 0) {
			return header.substring(0, header.length()-1);
		} else {
			return "";
		}
	}

	public void add(Field f) {
		if (mFieldMap.containsKey(f.name())) {
			Log.e("FieldList#add this field is already added : " + f);
			return;
		}
		mFieldMap.put(f.name(), new FieldListItem(mFieldMap.size(), f));
		mFieldArray.add(f);
	}

	public Field get(String name) {
		FieldListItem item = (FieldListItem)mFieldMap.get(name);
		if (item == null) {
			return null;
		}
		return item.field;
	}

	public Field get(int index) {
		return (Field)mFieldArray.get(index);
	}

	public int getIndex(String name) {
		FieldListItem item = (FieldListItem)mFieldMap.get(name);
		Debug.assertTrue(item != null);
		int index = item.index;
		return index;
	}

	public int size() {
		return mFieldMap.size();
	}

	public void setOrder(int[] order) {
		Debug.assertTrue(mFieldArray.size() >= order.length);
		mOrder = order;
	}

	public boolean hasOrder() {
		return mOrder != null;
	}

	public int order(int i) {
		if (mOrder != null) {
			return mOrder[i];
		} else {
			return i;
		}
	}

	public void updateField(int i, Field f) {
		Field old = get(i);
		mFieldArray.set(i, f);
		mFieldMap.remove(old.name());
		mFieldMap.put(f.name(), new FieldListItem(i, f));
	}
}
