package livros.btree;

import livros.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitBTreeTest extends TestCase
{
	public JUnitBTreeTest(String name) {
		super(name);
	}

	public void testInsert1() {
		BTree btree = new BTree();
		assertTrue(btree.root() != null);
		assertTrue(btree.size() == 0);

		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(20), new Integer(20));
 		btree.insert(new IntKey(40), new Integer(40));

		Node root = btree.root();
		assertTrue(root != null);
		assertTrue(btree.size() == 4);

		assertEquals(new IntKey(20), root.keyAt(0));
		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(2, l1.keyCount());
		assertEquals(new IntKey(0), l1.keyAt(0));
		assertEquals(new IntKey(10), l1.keyAt(1));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(2, l2.keyCount());
		assertEquals(new IntKey(20), l2.keyAt(0));
		assertEquals(new IntKey(40), l2.keyAt(1));
				   		
 		btree.insert(new IntKey(30), new Integer(30));
  		btree.insert(new IntKey(50), new Integer(50));

		root = btree.root();
		assertTrue(root != null);
		assertTrue(btree.size() == 6);

		assertEquals(2, root.keyCount());
		assertEquals(new IntKey(20), root.keyAt(0));
		assertEquals(new IntKey(40), root.keyAt(1));
		l1 = (Leaf)root.childAt(0);
		assertEquals(2, l1.keyCount());
		assertEquals(new IntKey(0), l1.keyAt(0));
		assertEquals(new IntKey(10), l1.keyAt(1));
		l2 = (Leaf)root.childAt(1);
		assertEquals(2, l2.keyCount());
		assertEquals(new IntKey(20), l2.keyAt(0));
		assertEquals(new IntKey(30), l2.keyAt(1));
		Leaf l3 = (Leaf)root.childAt(2);
		assertEquals(2, l3.keyCount());
		assertEquals(new IntKey(40), l3.keyAt(0));
		assertEquals(new IntKey(50), l3.keyAt(1));

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);
		l3 = (Leaf)root.childAt(2);

		assertEquals(root, l1.parent());
		assertEquals(root, l2.parent());
		assertEquals(root, l3.parent());
	}

	
	public void testInsert2() {
		BTree btree = new BTree();
		assertTrue(btree.root() != null);
		assertTrue(btree.size() == 0);

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
 		btree.insert(new IntKey(40), new Integer(40));
 		btree.insert(new IntKey(50), new Integer(50));
 		btree.insert(new IntKey(60), new Integer(60));
 		btree.insert(new IntKey(70), new Integer(70));
 		btree.insert(new IntKey(80), new Integer(80));
 		btree.insert(new IntKey(90), new Integer(90));

		//System.out.println(btree.toString());
		Node root = btree.root();
		assertTrue(root != null);
		assertTrue(btree.size() == 10);
		assertEquals(1, root.keyCount());
		assertEquals(new IntKey(40), root.keyAt(0));

		Branch b1 = (Branch)root.childAt(0);
		assertEquals(1, b1.keyCount());
		assertEquals(root, b1.parent());
		assertEquals(new IntKey(20), b1.keyAt(0));

		Branch b2 = (Branch)root.childAt(1);
		assertEquals(2, b2.keyCount());
		assertEquals(root, b2.parent());
		assertEquals(new IntKey(60), b2.keyAt(0));
		assertEquals(new IntKey(80), b2.keyAt(1));

		assertEquals(null, root.nextSibling());
		assertEquals(null, root.prevSibling());
		assertEquals(b2, b1.nextSibling());
		assertEquals(null, b1.prevSibling());
		assertEquals(null, b2.nextSibling());
		assertEquals(b1, b2.prevSibling());

		Leaf l1 = (Leaf)b1.childAt(0);
		assertEquals(2, l1.keyCount());
		assertEquals(b1, l1.parent());
		assertEquals(new IntKey(0), l1.keyAt(0));
		assertEquals(new IntKey(10), l1.keyAt(1));

		Leaf l2 = (Leaf)b1.childAt(1);
		assertEquals(2, l2.keyCount());
		assertEquals(new IntKey(20), l2.keyAt(0));
		assertEquals(new IntKey(30), l2.keyAt(1));

		assertEquals(l2, l1.nextSibling());
		assertEquals(null, l1.prevSibling());
		assertEquals(null, l2.nextSibling());
		assertEquals(l1, l2.prevSibling());

		Leaf l3 = (Leaf)b2.childAt(0);
		assertEquals(2, l3.keyCount());
		assertEquals(new IntKey(40), l3.keyAt(0));
		assertEquals(new IntKey(50), l3.keyAt(1));

		Leaf l4 = (Leaf)b2.childAt(1);
		assertEquals(2, l4.keyCount());
		assertEquals(new IntKey(60), l4.keyAt(0));
		assertEquals(new IntKey(70), l4.keyAt(1));

		Leaf l5 = (Leaf)b2.childAt(2);
		assertEquals(2, l5.keyCount());
		assertEquals(new IntKey(80), l5.keyAt(0));
		assertEquals(new IntKey(90), l5.keyAt(1));

		assertEquals(l4, l3.nextSibling());
		assertEquals(l5, l4.nextSibling());
		assertEquals(null, l5.nextSibling());
		assertEquals(null, l3.prevSibling());
		assertEquals(l3, l4.prevSibling());
		assertEquals(l4, l5.prevSibling());

		//
		root = btree.root();
		b1 = (Branch)root.childAt(0);
		b2 = (Branch)root.childAt(1);
		l1 = (Leaf)b1.childAt(0);
		l2 = (Leaf)b1.childAt(1);
		l3 = (Leaf)b2.childAt(0);
		l4 = (Leaf)b2.childAt(1);
		l5 = (Leaf)b2.childAt(2);

		assertEquals(root, b1.parent());
		assertEquals(root, b2.parent());
		assertEquals(b1, l1.parent());
		assertEquals(b1, l2.parent());
		assertEquals(b2, l3.parent());
		assertEquals(b2, l4.parent());
		assertEquals(b2, l5.parent());
	}
	

	public void testFind() {
		BTree btree = new BTree();
		int keys[] = {5,8,14,10,12,0,7,9,1,6,3,15,4,13,2,11};
		for (int i = 0; i < keys.length; i ++) {
			btree.insert(new IntKey(keys[i]), new Integer(keys[i]));
		}

		Random r = new Random();
		for (int i = 0; i < 50; i ++) {
			int key = r.nextInt(50);
			Object entry = btree.find(new IntKey(key));
			if (key < 16){
				assertEquals(((Integer)entry).intValue(), key);
			} else {
				assertEquals(null, entry);
			}
		}
	}

	public void testFind2() {
		BTree btree = new BTree();
		String keys[] = {"5","8","14","10","12","0","7","9","1","6","3","15","4","13","2","11"};
		for (int i = 0; i < keys.length; i ++) {
			btree.insert(new TextKey(keys[i]), keys[i]);
		}

		Random r = new Random();
		for (int i = 0; i < 50; i ++) {
			int key = r.nextInt(50);
			Object entry = btree.find(new TextKey(String.valueOf(key)));
			if (key < 16){
				assertEquals(entry, String.valueOf(key));
			} else {
				assertEquals(null, entry);
			}
		}
	}

	public void testFindLess() {
		BTree btree = new BTree();
		int keys[] = {5,8,14,10,12,0,7,9,1,6,3,15,4,13,2,11};
		for (int i = 0; i < keys.length; i ++) {
			btree.insert(new IntKey(keys[i]), new Integer(keys[i]));
		}

		//System.out.println(btree.toString());
		List list = btree.findLess(new IntKey(0));
		assertTrue(list == null);

		list = btree.findLess(new IntKey(1));
		assertTrue(list != null);
		assertEquals(1, list.size());
		for (int i = 0; i < 1; i++) {
			assertEquals(new Integer(i), list.get(i));
		}
		//assertTrue(list == null);
		
		list = btree.findLess(new IntKey(3));
		assertTrue(list != null);
		assertEquals(3, list.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(new Integer(i), list.get(i));
		}

		list = btree.findLess(new IntKey(20));
		assertTrue(list != null);
		System.out.println("list size " + list.size());
		assertEquals(16, list.size());
		for (int i = 0; i < 16; i++) {
			assertEquals(new Integer(i), list.get(i));
		}

		list = btree.findLess(new IntKey(-1));
		assertTrue(list == null);
	}

	public void testFindLess2() {
		BTree btree = new BTree();
		String keys[] = {"a","b","c","d","e","f"};
		for (int i = 0; i < keys.length; i ++) {
			btree.insert(new TextKey(keys[i]), keys[i]);
		}

		//System.out.println(btree.toString());
		List list = btree.findLess(new TextKey("a"));
		assertTrue(list == null);

		list = btree.findLess(new TextKey("b"));
		assertTrue(list != null);
		assertEquals(1, list.size());
		for (int i = 0; i < 1; i++) {
			assertEquals("a", list.get(i));
		}
		//assertTrue(list == null);
		
		list = btree.findLess(new TextKey("d"));
		assertTrue(list != null);
		assertEquals(3, list.size());
		assertEquals("a", list.get(0));
		assertEquals("b", list.get(1));
		assertEquals("c", list.get(2));

		list = btree.findLess(new TextKey("x"));
		assertTrue(list != null);
		System.out.println("list size " + list.size());
		assertEquals(6, list.size());
		for (int i = 0; i < 6; i++) {
			assertEquals(keys[i], list.get(i));
		}

		list = btree.findLess(new TextKey(""));
		assertTrue(list == null);
	}


	public void testFindGreater() {
		BTree btree = new BTree();
		int keys[] = {5,8,14,10,12,0,7,9,1,6,3,15,4,13,2,11};
		for (int i = 0; i < keys.length; i ++) {
			btree.insert(new IntKey(keys[i]), new Integer(keys[i]));
		}

		//System.out.println(btree.toString());
		List list = btree.findGreater(new IntKey(15));
		assertTrue(list == null);

		list = btree.findGreater(new IntKey(12));
		assertTrue(list != null);
		assertEquals(3, list.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(new Integer(13+i), list.get(i));
		}

		list = btree.findGreater(new IntKey(0));
		assertTrue(list != null);
		System.out.println("list size " + list.size());
		assertEquals(15, list.size());
		for (int i = 0; i < 15; i++) {
			assertEquals(new Integer(1+i), list.get(i));
		}

		list = btree.findGreater(new IntKey(20));
		assertTrue(list == null);
	}

	public void testAllValues() {
		BTree btree = new BTree();
		int keys[] = {5,8,14,10,12,0,7,9,1,6,3,15,4,13,2,11};
		for (int i = 0; i < keys.length; i ++) {
			btree.insert(new IntKey(keys[i]), new Integer(keys[i]));
		}

		//System.out.println(btree.toString());
		List list = btree.allValues();
		assertTrue(list != null);
		assertEquals(16, list.size());
		for (int i = 0; i < 16; i++) {
			assertEquals(new Integer(i), list.get(i));
		}
	}

	public void testRotateLeftLeaf1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(35), new Integer(35));

		//System.out.println(btree.toString());
		btree.remove(new IntKey(0));
		btree.remove(new IntKey(10));
		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 6);
		assertEquals(2, root.keyCount());
		assertEquals(new IntKey(30), root.keyAt(0));
		assertEquals(new IntKey(40), root.keyAt(1));

		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(1, l1.keyCount());
		assertEquals(new IntKey(20), l1.keyAt(0));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(2, l2.keyCount());
		assertEquals(new IntKey(30), l2.keyAt(0));
		assertEquals(new IntKey(35), l2.keyAt(1));

		Leaf l3 = (Leaf)root.childAt(2);
		assertEquals(3, l3.keyCount());
		assertEquals(new IntKey(40), l3.keyAt(0));
		assertEquals(new IntKey(50), l3.keyAt(1));
		assertEquals(new IntKey(60), l3.keyAt(2));

		assertEquals(l2, l1.nextSibling());
		assertEquals(l3, l2.nextSibling());
		assertEquals(null, l3.nextSibling());
		assertEquals(null, l1.prevSibling());
		assertEquals(l1, l2.prevSibling());
		assertEquals(l2, l3.prevSibling());

		btree.remove(new IntKey(30));
		btree.remove(new IntKey(35));
		//System.out.println(btree.toString());

		assertTrue(btree.size() == 4);
		assertEquals(2, root.keyCount());
		assertEquals(new IntKey(30), root.keyAt(0));
		assertEquals(new IntKey(50), root.keyAt(1));

		l1 = (Leaf)root.childAt(0);
		assertEquals(1, l1.keyCount());
		assertEquals(new IntKey(20), l1.keyAt(0));

		l2 = (Leaf)root.childAt(1);
		assertEquals(1, l2.keyCount());
		assertEquals(new IntKey(40), l2.keyAt(0));

		l3 = (Leaf)root.childAt(2);
		assertEquals(2, l3.keyCount());
		assertEquals(new IntKey(50), l3.keyAt(0));
		assertEquals(new IntKey(60), l3.keyAt(1));

		assertEquals(l2, l1.nextSibling());
		assertEquals(l3, l2.nextSibling());
		assertEquals(null, l3.nextSibling());
		assertEquals(null, l1.prevSibling());
		assertEquals(l1, l2.prevSibling());
		assertEquals(l2, l3.prevSibling());

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);
		l3 = (Leaf)root.childAt(2);

		assertEquals(root, l1.parent());
		assertEquals(root, l2.parent());
		assertEquals(root, l3.parent());
	}


	public void testRotateRightLeaf1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(35), new Integer(35));

		btree.remove(new IntKey(40));
		btree.remove(new IntKey(50));

		//System.out.println(btree.toString());
		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 5);
		assertEquals(root.keyCount(), 2);
		assertEquals(root.keyAt(0), new IntKey(20));
		assertEquals(root.keyAt(1), new IntKey(30));

		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(l1.keyCount(), 2);
		assertEquals(l1.keyAt(0), new IntKey(0));
		assertEquals(l1.keyAt(1), new IntKey(10));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(l2.keyCount(), 1);
		assertEquals(l2.keyAt(0), new IntKey(20));

		Leaf l3 = (Leaf)root.childAt(2);
		assertEquals(l3.keyCount(), 2);
		assertEquals(l3.keyAt(0), new IntKey(30));
		assertEquals(l3.keyAt(1), new IntKey(35));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l2.nextSibling(), l3);
		assertEquals(l3.nextSibling(), null);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.prevSibling(), l1);
		assertEquals(l3.prevSibling(), l2);

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);
		l3 = (Leaf)root.childAt(2);

		assertEquals(l1.parent(), root);
		assertEquals(l2.parent(), root);
		assertEquals(l3.parent(), root);
	}	
	
	public void testFusionLeaf1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(50));

		btree.remove(new IntKey(10));

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 2);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(40));

		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(l1.keyCount(), 1);
		assertEquals(l1.keyAt(0), new IntKey(30));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(l2.keyCount(), 1);
		assertEquals(l2.keyAt(0), new IntKey(40));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.nextSibling(), null);
		assertEquals(l2.prevSibling(), l1);

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);

		assertEquals(l1.parent(), root);
		assertEquals(l2.parent(), root);
	}

	public void testFusionLeaf2() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(50));

		btree.remove(new IntKey(30));
		//System.out.println(btree.toString());

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 2);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(20));

		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(l1.keyCount(), 1);
		assertEquals(l1.keyAt(0), new IntKey(10));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(l2.keyCount(), 1);
		assertEquals(l2.keyAt(0), new IntKey(40));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.nextSibling(), null);
		assertEquals(l2.prevSibling(), l1);

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);

		assertEquals(l1.parent(), root);
		assertEquals(l2.parent(), root);
	}

	public void testFusionLeaf3() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(50));
		//System.out.println(btree.toString());

		btree.remove(new IntKey(40));
		//System.out.println(btree.toString());

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 2);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(20));

		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(l1.keyCount(), 1);
		assertEquals(l1.keyAt(0), new IntKey(10));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(l2.keyCount(), 1);
		assertEquals(l2.keyAt(0), new IntKey(30));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.nextSibling(), null);
		assertEquals(l2.prevSibling(), l1);

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);

		assertEquals(l1.parent(), root);
		assertEquals(l2.parent(), root);
	}

	public void testDecreaseLeaf1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(20));
		//System.out.println(btree.toString());

		btree.remove(new IntKey(10));
		//System.out.println(btree.toString());

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(root.isLeaf());
		assertTrue(btree.size() == 1);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(30));
		assertEquals(root.nextSibling(), null);
		assertEquals(root.prevSibling(), null);
	}

	
	public void testDecreaseLeaf2() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(20));
		//System.out.println(btree.toString());

		btree.remove(new IntKey(30));
		//System.out.println(btree.toString());

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(root.isLeaf());
		assertTrue(btree.size() == 1);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(10));
		assertEquals(root.nextSibling(), null);
		assertEquals(root.prevSibling(), null);
	}
	
	public void testRotateLeftBranch1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));

		//System.out.println(btree.toString());
		btree.remove(new IntKey(0));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(20));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 7);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(60));

		Branch b1 = (Branch)root.childAt(0);
		assertEquals(b1.keyCount(), 1);
		assertEquals(b1.keyAt(0), new IntKey(40));

		Branch b2 = (Branch)root.childAt(1);
		assertEquals(b2.keyCount(), 1);
		assertEquals(b2.keyAt(0), new IntKey(80));

		Leaf l1 = (Leaf)b1.childAt(0);
		assertEquals(l1.keyCount(), 1);
		assertEquals(l1.keyAt(0), new IntKey(30));

		Leaf l2 = (Leaf)b1.childAt(1);
		assertEquals(l2.keyCount(), 2);
		assertEquals(l2.keyAt(0), new IntKey(40));
		assertEquals(l2.keyAt(1), new IntKey(50));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.nextSibling(), null);
		assertEquals(l2.prevSibling(), l1);

		Leaf l3 = (Leaf)b2.childAt(0);
		assertEquals(l3.keyCount(), 2);
		assertEquals(l3.keyAt(0), new IntKey(60));
		assertEquals(l3.keyAt(1), new IntKey(70));

		Leaf l4 = (Leaf)b2.childAt(1);
		assertEquals(l4.keyCount(), 2);
		assertEquals(l4.keyAt(0), new IntKey(80));
		assertEquals(l4.keyAt(1), new IntKey(90));

		assertEquals(l3.nextSibling(), l4);
		assertEquals(l3.prevSibling(), null);
		assertEquals(l4.nextSibling(), null);
		assertEquals(l4.prevSibling(), l3);

		//
		root = btree.root();
		b1 = (Branch)root.childAt(0);
		b2 = (Branch)root.childAt(1);
		l1 = (Leaf)b1.childAt(0);
		l2 = (Leaf)b1.childAt(1);
		l3 = (Leaf)b2.childAt(0);
		l4 = (Leaf)b2.childAt(1);

		assertEquals(b1.parent(), root);
		assertEquals(b2.parent(), root);
		assertEquals(l1.parent(), b1);
		assertEquals(l2.parent(), b1);
		assertEquals(l3.parent(), b2);
		assertEquals(l4.parent(), b2);
	}

	public void testRotateRightBranch1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(110), new Integer(110));
		btree.insert(new IntKey(100), new Integer(100));
		btree.insert(new IntKey(90), new Integer(90));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(0), new Integer(0));

		//System.out.println(btree.toString());
		btree.remove(new IntKey(110));
		btree.remove(new IntKey(100));
		btree.remove(new IntKey(90));
		btree.remove(new IntKey(80));
		btree.remove(new IntKey(70));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 7);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(40));

		Branch b1 = (Branch)root.childAt(0);
		assertEquals(b1.keyCount(), 1);
		assertEquals(b1.keyAt(0), new IntKey(20));

		Branch b2 = (Branch)root.childAt(1);
		assertEquals(b2.keyCount(), 1);
		assertEquals(b2.keyAt(0), new IntKey(60));

		Leaf l1 = (Leaf)b1.childAt(0);
		assertEquals(l1.keyCount(), 2);
		assertEquals(l1.keyAt(0), new IntKey(0));
		assertEquals(l1.keyAt(1), new IntKey(10));

		Leaf l2 = (Leaf)b1.childAt(1);
		assertEquals(l2.keyCount(), 2);
		assertEquals(l2.keyAt(0), new IntKey(20));
		assertEquals(l2.keyAt(1), new IntKey(30));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.nextSibling(), null);
		assertEquals(l2.prevSibling(), l1);

		Leaf l3 = (Leaf)b2.childAt(0);
		assertEquals(l3.keyCount(), 2);
		assertEquals(l3.keyAt(0), new IntKey(40));
		assertEquals(l3.keyAt(1), new IntKey(50));

		Leaf l4 = (Leaf)b2.childAt(1);
		assertEquals(l4.keyCount(), 1);
		assertEquals(l4.keyAt(0), new IntKey(60));

		assertEquals(l3.nextSibling(), l4);
		assertEquals(l3.prevSibling(), null);
		assertEquals(l4.nextSibling(), null);
		assertEquals(l4.prevSibling(), l3);

		//
		root = btree.root();
		b1 = (Branch)root.childAt(0);
		b2 = (Branch)root.childAt(1);
		l1 = (Leaf)b1.childAt(0);
		l2 = (Leaf)b1.childAt(1);
		l3 = (Leaf)b2.childAt(0);
		l4 = (Leaf)b2.childAt(1);

		assertEquals(b1.parent(), root);
		assertEquals(b2.parent(), root);
		assertEquals(l1.parent(), b1);
		assertEquals(l2.parent(), b1);
		assertEquals(l3.parent(), b2);
		assertEquals(l4.parent(), b2);
	}

	public void testFusionBranch1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));
		btree.insert(new IntKey(100), new Integer(100));
		btree.insert(new IntKey(110), new Integer(110));
		btree.insert(new IntKey(120), new Integer(120));
		btree.insert(new IntKey(130), new Integer(130));
		btree.remove(new IntKey(120));
		btree.remove(new IntKey(130));

		/*
		  40:80
         /  |  \
       20   60  100:120
      
		  40:80
         /  |  \
        x   60  100:120
       /   /  \ 
      a   b    c  

		    80
         /      \
        40:60   100:120
       /  |  \
      a   b   c
		*/

		//System.out.println(btree.toString());
		btree.remove(new IntKey(0));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(20));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 9);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(80));

		Branch b1 = (Branch)root.childAt(0);
		assertEquals(b1.keyCount(), 2);
		assertEquals(b1.keyAt(0), new IntKey(40));
		assertEquals(b1.keyAt(1), new IntKey(60));

		Branch b2 = (Branch)root.childAt(1);
		assertEquals(b2.keyCount(), 1);
		assertEquals(b2.keyAt(0), new IntKey(100));

		Leaf l1 = (Leaf)b1.childAt(0);
		assertEquals(l1.keyCount(), 1);
		assertEquals(l1.keyAt(0), new IntKey(30));

		Leaf l2 = (Leaf)b1.childAt(1);
		assertEquals(l2.keyCount(), 2);
		assertEquals(l2.keyAt(0), new IntKey(40));
		assertEquals(l2.keyAt(1), new IntKey(50));

		Leaf l3 = (Leaf)b1.childAt(2);
		assertEquals(l3.keyCount(), 2);
		assertEquals(l3.keyAt(0), new IntKey(60));
		assertEquals(l3.keyAt(1), new IntKey(70));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l2.nextSibling(), l3);
		assertEquals(l3.nextSibling(), null);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.prevSibling(), l1);
		assertEquals(l3.prevSibling(), l2);

		Leaf l4 = (Leaf)b2.childAt(0);
		assertEquals(l4.keyCount(), 2);
		assertEquals(l4.keyAt(0), new IntKey(80));
		assertEquals(l4.keyAt(1), new IntKey(90));

		Leaf l5 = (Leaf)b2.childAt(1);
		assertEquals(l5.keyCount(), 2);
		assertEquals(l5.keyAt(0), new IntKey(100));
		assertEquals(l5.keyAt(1), new IntKey(110));

		assertEquals(l4.nextSibling(), l5);
		assertEquals(l4.prevSibling(), null);
		assertEquals(l5.nextSibling(), null);
		assertEquals(l5.prevSibling(), l4);

		//
		root = btree.root();
		b1 = (Branch)root.childAt(0);
		b2 = (Branch)root.childAt(1);
		l1 = (Leaf)b1.childAt(0);
		l2 = (Leaf)b1.childAt(1);
		l3 = (Leaf)b1.childAt(2);
		l4 = (Leaf)b2.childAt(0);
		l5 = (Leaf)b2.childAt(1);

		assertEquals(b1.parent(), root);
		assertEquals(b2.parent(), root);
		assertEquals(l1.parent(), b1);
		assertEquals(l2.parent(), b1);
		assertEquals(l3.parent(), b1);
		assertEquals(l4.parent(), b2);
		assertEquals(l5.parent(), b2);
	}

	public void testFusionBranch2() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));
		btree.insert(new IntKey(100), new Integer(100));
		btree.insert(new IntKey(110), new Integer(110));
		btree.insert(new IntKey(120), new Integer(120));
		btree.insert(new IntKey(130), new Integer(130));
		btree.remove(new IntKey(120));
		btree.remove(new IntKey(130));

		/*
		  40:80
         /  |  \
       20   60  100:120
      
		  40:80
         /  |  \
        20  60  x
           /  \   \
          a    b   c

		    40
         /      \
        20     60:80
              /  |  \
             a   b   c
		*/

		//System.out.println(btree.toString());
		btree.remove(new IntKey(110));
		btree.remove(new IntKey(100));
		btree.remove(new IntKey(90));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 9);
		assertEquals(root.keyCount(), 1);
		assertEquals(root.keyAt(0), new IntKey(40));

		Branch b1 = (Branch)root.childAt(0);
		assertEquals(b1.keyCount(), 1);
		assertEquals(b1.keyAt(0), new IntKey(20));

		Branch b2 = (Branch)root.childAt(1);
		assertEquals(b2.keyCount(), 2);
		assertEquals(b2.keyAt(0), new IntKey(60));
		assertEquals(b2.keyAt(1), new IntKey(80));

		Leaf l1 = (Leaf)b1.childAt(0);
		assertEquals(l1.keyCount(), 2);
		assertEquals(l1.keyAt(0), new IntKey(0));
		assertEquals(l1.keyAt(1), new IntKey(10));

		Leaf l2 = (Leaf)b1.childAt(1);
		assertEquals(l2.keyCount(), 2);
		assertEquals(l2.keyAt(0), new IntKey(20));
		assertEquals(l2.keyAt(1), new IntKey(30));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l2.nextSibling(), null);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.prevSibling(), l1);

		Leaf l3 = (Leaf)b2.childAt(0);
		assertEquals(l3.keyCount(), 2);
		assertEquals(l3.keyAt(0), new IntKey(40));
		assertEquals(l3.keyAt(1), new IntKey(50));

		Leaf l4 = (Leaf)b2.childAt(1);
		assertEquals(l4.keyCount(), 2);
		assertEquals(l4.keyAt(0), new IntKey(60));
		assertEquals(l4.keyAt(1), new IntKey(70));

		Leaf l5 = (Leaf)b2.childAt(2);
		assertEquals(l5.keyCount(), 1);
		assertEquals(l5.keyAt(0), new IntKey(80));

		assertEquals(l3.nextSibling(), l4);
		assertEquals(l4.nextSibling(), l5);
		assertEquals(l5.nextSibling(), null);
		assertEquals(l3.prevSibling(), null);
		assertEquals(l4.prevSibling(), l3);
		assertEquals(l5.prevSibling(), l4);

		//
		root = btree.root();
		b1 = (Branch)root.childAt(0);
		b2 = (Branch)root.childAt(1);
		l1 = (Leaf)b1.childAt(0);
		l2 = (Leaf)b1.childAt(1);
		l3 = (Leaf)b2.childAt(0);
		l4 = (Leaf)b2.childAt(1);
		l5 = (Leaf)b2.childAt(2);

		assertEquals(b1.parent(), root);
		assertEquals(b2.parent(), root);
		assertEquals(l1.parent(), b1);
		assertEquals(l2.parent(), b1);
		assertEquals(l3.parent(), b2);
		assertEquals(l4.parent(), b2);
		assertEquals(l5.parent(), b2);
	}

	public void testDecreaseBranch1() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));
		btree.remove(new IntKey(90));
		btree.remove(new IntKey(80));

		//System.out.println(btree.toString());

		btree.remove(new IntKey(30));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(10));

		//System.out.println(btree.toString());

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 5);
		assertEquals(root.keyCount(), 2);
		assertEquals(root.keyAt(0), new IntKey(40));
		assertEquals(root.keyAt(1), new IntKey(60));

		Leaf l1 = (Leaf)root.childAt(0);
		assertEquals(l1.keyCount(), 1);
		assertEquals(l1.keyAt(0), new IntKey(0));

		Leaf l2 = (Leaf)root.childAt(1);
		assertEquals(l2.keyCount(), 2);
		assertEquals(l2.keyAt(0), new IntKey(40));
		assertEquals(l2.keyAt(1), new IntKey(50));

		Leaf l3 = (Leaf)root.childAt(2);
		assertEquals(l3.keyCount(), 2);
		assertEquals(l3.keyAt(0), new IntKey(60));
		assertEquals(l3.keyAt(1), new IntKey(70));

		assertEquals(l1.nextSibling(), l2);
		assertEquals(l2.nextSibling(), l3);
		assertEquals(l3.nextSibling(), null);
		assertEquals(l1.prevSibling(), null);
		assertEquals(l2.prevSibling(), l1);
		assertEquals(l3.prevSibling(), l2);

		//
		root = btree.root();
		l1 = (Leaf)root.childAt(0);
		l2 = (Leaf)root.childAt(1);
		l3 = (Leaf)root.childAt(2);

		assertEquals(l1.parent(), root);
		assertEquals(l2.parent(), root);
		assertEquals(l3.parent(), root);
	}

	public void testRemoveLeaf() {
		BTree btree = new BTree();
		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 3);
		assertEquals(root.keyCount(), 3);

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(20));
		assertTrue(btree.size() == 0);
		assertEquals(root.keyCount(), 0);

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(20));
		assertTrue(btree.size() == 0);
		assertEquals(root.keyCount(), 0);
	}

	public void testRemoveLeaf2() {
		BTree btree = new BTree();
		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));

		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 3);
		assertEquals(root.keyCount(), 3);

		btree.remove(new IntKey(20));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(0));
		assertTrue(btree.size() == 0);
		assertEquals(root.keyCount(), 0);
	}

	public void testRemoveBranch() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));
		btree.insert(new IntKey(100), new Integer(100));
		btree.insert(new IntKey(110), new Integer(110));
		btree.insert(new IntKey(120), new Integer(120));
		btree.insert(new IntKey(130), new Integer(130));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 14);

		btree.remove(new IntKey(0));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(30));
		btree.remove(new IntKey(40));
		btree.remove(new IntKey(50));
		btree.remove(new IntKey(60));
		btree.remove(new IntKey(70));
		btree.remove(new IntKey(80));
		btree.remove(new IntKey(90));
		btree.remove(new IntKey(100));
		btree.remove(new IntKey(110));
		btree.remove(new IntKey(120));
		btree.remove(new IntKey(130));
		assertTrue(btree.size() == 0);

	}

	public void testRemoveBranch2() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));
		btree.insert(new IntKey(100), new Integer(100));
		btree.insert(new IntKey(110), new Integer(110));
		btree.insert(new IntKey(120), new Integer(120));
		btree.insert(new IntKey(130), new Integer(130));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 14);

		btree.remove(new IntKey(130));
		btree.remove(new IntKey(120));
		btree.remove(new IntKey(110));
		btree.remove(new IntKey(100));
		btree.remove(new IntKey(90));
		btree.remove(new IntKey(80));
		btree.remove(new IntKey(70));
		btree.remove(new IntKey(60));
		btree.remove(new IntKey(50));
		btree.remove(new IntKey(40));
		btree.remove(new IntKey(30));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(0));
		assertTrue(btree.size() == 0);

	}

	public void testRemoveBranch3() {
		BTree btree = new BTree();

		btree.insert(new IntKey(0), new Integer(0));
		btree.insert(new IntKey(10), new Integer(10));
		btree.insert(new IntKey(20), new Integer(20));
		btree.insert(new IntKey(30), new Integer(30));
		btree.insert(new IntKey(40), new Integer(40));
		btree.insert(new IntKey(50), new Integer(50));
		btree.insert(new IntKey(60), new Integer(60));
		btree.insert(new IntKey(70), new Integer(70));
		btree.insert(new IntKey(80), new Integer(80));
		btree.insert(new IntKey(90), new Integer(90));
		btree.insert(new IntKey(100), new Integer(100));
		btree.insert(new IntKey(110), new Integer(110));
		btree.insert(new IntKey(120), new Integer(120));
		btree.insert(new IntKey(130), new Integer(130));

		//System.out.println(btree.toString());
		Node root = btree.root();
 		assertTrue(root != null);
		assertTrue(btree.size() == 14);

		btree.remove(new IntKey(60));
		btree.remove(new IntKey(20));
		btree.remove(new IntKey(110));
		btree.remove(new IntKey(30));
		btree.remove(new IntKey(10));
		btree.remove(new IntKey(80));
		btree.remove(new IntKey(120));
		btree.remove(new IntKey(130));
		btree.remove(new IntKey(0));
		btree.remove(new IntKey(40));
		btree.remove(new IntKey(100));
		btree.remove(new IntKey(70));
		btree.remove(new IntKey(90));
		btree.remove(new IntKey(50));
		assertTrue(btree.size() == 0);

	}

	public void testRandomInsertRemove() {
		BTree btree = new BTree();
		int keys[] = {15, 12, 5, 23, 57, 38, 62, 16, 8, 9, 52, 37, 6, 30, 3, 7, 4, 17, 35, 1, 11, 18, 39, 36, 41, 25, 49, 22, 28, 29, 0, 48, 2, 50, 19, 24, 44, 26, 32, 42, 13, 31, 47, 58, 60, 45, 40, 56, 27, 14, 20, 53, 43, 51, 54, 55, 46, 34, 59, 33, 10, 61, 21, 63};

		for (int i = 0; i < keys.length; i ++) {
			IntKey key = new IntKey(keys[i]);
			btree.insert(key, new Integer(key.value));
		}

		//System.out.println(btree.toString());
		for (int i = 0; i < keys.length; i ++) {
			IntKey key = new IntKey(keys[i]);
			Object entry = btree.find(key);
			assertTrue(entry != null);
			assertEquals(key.value, ((Integer)entry).intValue());
		}

		for (int i = 0; i < keys.length; i ++) {
			IntKey key = new IntKey(keys[i]);
			btree.remove(key);
		}
	}

	private int lowerBound(int list[], int key) {
		/*
		System.out.print("[");
		for (int i = 0; i < list.length; i++) {
			System.out.print(list[i]+", ");
		}
		System.out.print("]\n");
		*/
		final int len = list.length;
        int lo = 0, hi = len-1;
        while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			int v = list[mid];
			//System.out.println("lo="+lo+" hi="+hi+" mid="+mid+" list[mid]="+list[mid]+ " key="+key);
            if (v < key) {
                lo = mid + 1;
                if (hi < lo) {
					//System.out.println("return " + (mid+1));
                    return mid + 1;
				}
			} else {
                hi = mid - 1;
                if (hi < lo) {
					//System.out.println("return " + mid);
                    return mid;
				}
			}
        }
		System.out.println("return # -1");
		return -1;
    }

	public void testLowerbound() {
		int i = lowerBound(new int[]{0, 1, 2, 3, 4, 5}, 0);
		assertEquals(0, i);

		i = lowerBound(new int[]{0, 1, 2, 3, 4, 5}, 1);
		assertEquals(1, i);

		i = lowerBound(new int[]{0, 1, 2, 3, 4, 5}, 5);
		assertEquals(5, i);

		i = lowerBound(new int[]{0, 1, 2, 3, 4, 5}, -1);
		assertEquals(0, i);

		i = lowerBound(new int[]{0, 1, 2, 3, 4, 5}, 6);
		assertEquals(6, i);

		i = lowerBound(new int[]{0, 1, 2, 3, 4, 5}, 7);
		assertEquals(6, i);

		i = lowerBound(new int[]{0, 0, 0, 1, 2, 2, 2}, 1);
		assertEquals(3, i);

		i = lowerBound(new int[]{0, 0, 1, 1, 2, 2, 2}, 2);
		assertEquals(4, i);

		i = lowerBound(new int[]{0, 0, 1, 1, 2, 2, 2}, 3);
		assertEquals(7, i);

	}

	private int lessThan(int list[], int key) {
		final int len = list.length;
        int lo = 0, hi = len-1;
        while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			int v = list[mid];
            if (v < key) {
                lo = mid + 1;
                if (hi < lo) {
                    return mid;
				}
			} else {
                hi = mid - 1;
                if (hi < lo) {
                    return mid-1;
				}
			}
        }
		return -1;
    }

	public void testLessThan() {
		int i = lessThan(new int[]{0, 1, 2, 3, 4, 5}, 0);
		assertEquals(-1, i);

		i = lessThan(new int[]{0, 1, 2, 3, 4, 5}, 5);
		assertEquals(4, i);

		i = lessThan(new int[]{0, 1, 2, 3, 4, 5}, -1);
		assertEquals(-1, i);

		i = lessThan(new int[]{0, 1, 2, 3, 4, 5}, 6);
		assertEquals(5, i);

		i = lessThan(new int[]{0, 1, 2, 3, 4, 5}, 7);
		assertEquals(5, i);

		i = lessThan(new int[]{0, 0, 1, 1, 2, 2, 2}, 1);
		assertEquals(1, i);

		i = lessThan(new int[]{0, 0, 1, 1, 2, 2, 2}, 2);
		assertEquals(3, i);

		i = lessThan(new int[]{0, 0, 1, 1, 2, 2, 2}, 3);
		assertEquals(6, i);

	}

	private int greaterThan(int list[], int key) {
		final int len = list.length;
        int lo = 0, hi = len-1;
        while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			int v = list[mid];
            if (v <= key) {
                lo = mid + 1;
                if (hi < lo) {
                    return mid+1;
				}
			} else {
                hi = mid - 1;
                if (hi < lo) {
                    return mid;
				}
			}
        }
		return -1;
    }

	public void testGreaterThan() {
		int i = greaterThan(new int[]{0, 1, 2, 3, 4, 5}, 0);
		assertEquals(1, i);

		i = greaterThan(new int[]{0, 1, 2, 3, 4, 5}, 1);
		assertEquals(2, i);

		i = greaterThan(new int[]{0, 1, 2, 3, 4, 5}, 5);
		assertEquals(6, i);

		i = greaterThan(new int[]{0, 1, 2, 3, 4, 5}, -1);
		assertEquals(0, i);

		i = greaterThan(new int[]{0, 1, 2, 3, 4, 5}, 6);
		assertEquals(6, i);

		i = greaterThan(new int[]{0, 1, 2, 3, 4, 5}, 7);
		assertEquals(6, i);

		i = greaterThan(new int[]{0, 0, 1, 1, 2, 2, 2}, 1);
		assertEquals(4, i);

		i = greaterThan(new int[]{0, 0, 1, 1, 2, 2, 2}, 0);
		assertEquals(2, i);

		i = greaterThan(new int[]{0, 0, 1, 1, 2, 2, 2}, 2);
		assertEquals(7, i);

	}

}

