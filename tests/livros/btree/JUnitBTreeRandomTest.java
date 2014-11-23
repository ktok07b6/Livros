package livros.btree;

import livros.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitBTreeRandomTest extends TestCase
{
	public JUnitBTreeRandomTest(String name) {
		super(name);
	}

	public void testRandomInsertRemove1() {
		randomTest(4);
	}

	public void testRandomInsertRemove2() {
		randomTest(16);
	}

	public void testRandomInsertRemove3() {
		randomTest(64);
	}

	private void randomTest(int order) {
		Leaf.ORDER = order;
		Branch.ORDER = order;

		BTree btree = new BTree();

		final int SIZE = 1024;
		List keys = new ArrayList();
		for (int i = 0; i < SIZE; i++) {
			keys.add(new IntKey(i));
		}

		Random r = new Random();
		for (int i = 0; i < SIZE; i ++) {
			int a = r.nextInt(SIZE-1);
			int b = r.nextInt(SIZE-1);
			IntKey ak = (IntKey)keys.get(a);
			IntKey bk = (IntKey)keys.get(b);
			int tmp = ak.value;
			ak.value = bk.value;
			bk.value = tmp;
		}

		for (int i = 0; i < keys.size(); i ++) {
			IntKey key = (IntKey)keys.get(i);
			System.out.print(key + ", ");
		}
		System.out.print("\n");
		for (int i = 0; i < keys.size(); i ++) {
			IntKey key = (IntKey)keys.get(i);
			btree.insert(key, new Integer(key.value));
		}

		System.out.println(btree.toString());
		for (int i = 0; i < keys.size(); i ++) {
			IntKey key = (IntKey)keys.get(i);
			Object entry = btree.find(key);
			assertTrue(entry != null);
			assertEquals(((Integer)entry).intValue(), key.value);
		}

		for (int i = 0; i < keys.size(); i ++) {
			IntKey key = (IntKey)keys.get(i);
			btree.remove(key);
		}
	}

}


