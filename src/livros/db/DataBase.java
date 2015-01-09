package livros.db;

import livros.Debug;
import livros.Log;
import livros.storage.StorageManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataBase
{
	public static boolean NO_STORAGE = false;

	List mTables = new ArrayList();
	List mTmpTables = new ArrayList();
	String mName;
	
	public static DataBase open(String name) {
		return StorageManager.instance().loadDataBase();
	}
	
	public DataBase(String name) {
		mName = name;
	}

	public String name() {
		return mName;
	}

	public void cleanup() {
		Iterator iter = mTmpTables.iterator();
		while (iter.hasNext()) {
			IReadOnlyTable rot = (IReadOnlyTable)iter.next();
			if (rot.isDerivedTable()) {
				StorageManager.instance().deleteTmpTable(rot);
			} else {
				StorageManager.instance().deleteTable((Table)rot);
			}
		}
		mTmpTables.clear();
		StorageManager.instance().cleanup();
	}

	public void close() {
		StorageManager.instance().close();
	}

	public void commit(Table tab) {
		commitRecords(tab);
		commitSchema();
	}

	public void commitAll() {
		Iterator iter = mTables.iterator();
		List deletes = new ArrayList();
		while (iter.hasNext()) {
			Table t = (Table)iter.next();
			commitRecords(t);
			if (t.isDeleting()) {
				deletes.add(t);
			}
		}
		iter = deletes.iterator();
		while (iter.hasNext()) {
			Table t = (Table)iter.next();
			mTables.remove(t);
		}
		commitSchema();
	}

	private void commitRecords(Table tab) {
		if (tab.isDeleting()) {
			//delete table data file
			StorageManager.instance().deleteTable(tab);
		} else {
			tab.commit();
		}
	}

	private void commitSchema() {
		if (!NO_STORAGE) {
			StorageManager.instance().saveDataBase();
		}
	}

	public void addTable(Table t) {
		if (table(t.name()) != null) {
			Log.e("duplicate table " + t.name());
			new Throwable().printStackTrace();
			return;
		}
		mTables.add(t);
	}

	public void addTmpTable(IReadOnlyTable t) {
		Debug.assertTrue(table(t.name()) == null);
		mTmpTables.add(t);
	}

	public boolean delTable(Table t) {
		if (t.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public Table table(String name){
		//FIXME: use map
		Iterator iter = mTables.iterator();
		while (iter.hasNext()) {
			Table t = (Table)iter.next();
			if (t.name().equals(name) && !t.isDeleting()) {
				return t;
			}
		}
		return null;
	}
	public IReadOnlyTable readOnlyTable(String name) {
		Table t = table(name);
		if (t != null) {
			return t;
		}
		Iterator iter = mTmpTables.iterator();
		while (iter.hasNext()) {
			IReadOnlyTable rot = (IReadOnlyTable)iter.next();
			if (rot.name().equals(name)) {
				return rot;
			}
		}
		Log.e(name + " is not found");
		return null;
	}
	
	public List tables() {
		return mTables;
	}
}
