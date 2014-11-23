package livros.db;

import livros.Log;
import livros.storage.StorageManager;
import java.util.List;

public class SelectorTest
{
	public static void test1() {
		DataBase loaded = StorageManager.instance().loadDataBase();
		List tables = loaded.tables();
		Table t = (Table)tables.get(0);
		Log.d(""+t.toString());
		Field f = t.field("id");
		Selector sel = SelectorFactory.create(t, null);
		while (sel.hasNext()) {
			Record r = sel.next();
			Log.d("select : "+r.toString());
		}
	}

	public static void main(String[] args) {
		test1();
	}
}
