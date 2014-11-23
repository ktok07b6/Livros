package livros;

import junit.framework.TestCase;

public class JUnitSortedListTest extends TestCase
{
	public JUnitSortedListTest(String name) {
		super(name);
	}

	class C implements Comparable {
		int id;
		public C(int i) {
			id = i;
		}

		public int compareTo(Object o) {
			C other = (C)o;
			return id - other.id;
		}
	}

	public void testAdd() {
		C c0 = new C(0);
		C c1 = new C(1);
		C c2 = new C(2);
		C c3 = new C(3);
		C c4 = new C(4);

		{
			SortedList list = new SortedList();
			list.add(c0);
			list.add(c1);
			list.add(c2);
			list.add(c3);
			list.add(c4);
			assertEquals(c0, list.get(0));
			assertEquals(c1, list.get(1));
			assertEquals(c2, list.get(2));
			assertEquals(c3, list.get(3));
			assertEquals(c4, list.get(4));
		}

		{
			SortedList list = new SortedList();
			list.add(c4);
			list.add(c3);
			list.add(c2);
			list.add(c1);
			list.add(c0);
			assertEquals(c0, list.get(0));
			assertEquals(c1, list.get(1));
			assertEquals(c2, list.get(2));
			assertEquals(c3, list.get(3));
			assertEquals(c4, list.get(4));
		}

		{
			SortedList list = new SortedList();
			list.add(c2);
			list.add(c4);
			list.add(c0);
			list.add(c3);
			list.add(c1);
			assertEquals(c0, list.get(0));
			assertEquals(c1, list.get(1));
			assertEquals(c2, list.get(2));
			assertEquals(c3, list.get(3));
			assertEquals(c4, list.get(4));
		}

	}

	public void testRemove() {
		C c0 = new C(0);
		C c1 = new C(1);
		C c2 = new C(2);
		C c3 = new C(3);
		C c4 = new C(4);

		{
			SortedList list = new SortedList();
			assertEquals(null, list.remove(c0));

			list.add(c0);
			list.add(c1);
			list.add(c2);
			list.add(c3);
			list.add(c4);
			
			assertEquals(c0, list.remove(c0));
			assertEquals(c1, list.get(0));
			assertEquals(c2, list.get(1));
			assertEquals(c3, list.get(2));
			assertEquals(c4, list.get(3));

			assertEquals(c1, list.remove(c1));
			assertEquals(c2, list.get(0));
			assertEquals(c3, list.get(1));
			assertEquals(c4, list.get(2));

			assertEquals(c2, list.remove(c2));
			assertEquals(c3, list.get(0));
			assertEquals(c4, list.get(1));

			assertEquals(c3, list.remove(c3));
			assertEquals(c4, list.get(0));

			assertEquals(c4, list.remove(c4));
			assertEquals(0, list.size());
		}

		{
			SortedList list = new SortedList();
			assertEquals(null, list.remove(c0));

			list.add(c0);
			list.add(c1);
			list.add(c2);
			list.add(c3);
			list.add(c4);
			
			assertEquals(c4, list.remove(c4));
			assertEquals(c0, list.get(0));
			assertEquals(c1, list.get(1));
			assertEquals(c2, list.get(2));
			assertEquals(c3, list.get(3));

			assertEquals(c3, list.remove(c3));
			assertEquals(c0, list.get(0));
			assertEquals(c1, list.get(1));
			assertEquals(c2, list.get(2));

			assertEquals(c2, list.remove(c2));
			assertEquals(c0, list.get(0));
			assertEquals(c1, list.get(1));

			assertEquals(c1, list.remove(c1));
			assertEquals(c0, list.get(0));

			assertEquals(c0, list.remove(c0));
			assertEquals(0, list.size());
		}

	}
}
