package livros.btree;

import livros.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitLeafBucketTest extends TestCase
{
	public JUnitLeafBucketTest(String name) {
		super(name);
	}

	public void testLeafBucketArray_insert() {
		ILeafBucket buckets = new LeafBucketArray();
		leafBucket_insert(buckets);
	}

	public void testLeafBucketArray_remove() {
		ILeafBucket buckets = new LeafBucketArray();
		leafBucket_remove(buckets);
	}

	public void testLeafBucketArray_find() {
		ILeafBucket buckets = new LeafBucketArray();
		leafBucket_find(buckets);
	}

	public void testLeafBucketArray_keyAt() {
		ILeafBucket buckets = new LeafBucketArray();
		leafBucket_keyAt(buckets);
	}

	public void testLeafBucketArray_split() {
		ILeafBucket buckets = new LeafBucketArray();
		leafBucket_split(buckets);
	}

	public void testLeafBucketArray_split2() {
		ILeafBucket buckets = new LeafBucketArray();
		leafBucket_split2(buckets);
	}

	public void testLeafBucketArray_merge() {
		ILeafBucket b1 = new LeafBucketArray();
		ILeafBucket b2 = new LeafBucketArray();
		leafBucket_merge(b1, b2);
	}

	public void testLeafBucketArray_merge2() {
		ILeafBucket b1 = new LeafBucketArray();
		ILeafBucket b2 = new LeafBucketArray();
		leafBucket_merge2(b1, b2);
	}

	public void testLeafBucketList_insert() {
		ILeafBucket buckets = new LeafBucketList();
		leafBucket_insert(buckets);
	}

	public void testLeafBucketList_remove() {
		ILeafBucket buckets = new LeafBucketList();
		leafBucket_remove(buckets);
	}

	public void testLeafBucketList_find() {
		ILeafBucket buckets = new LeafBucketList();
		leafBucket_find(buckets);
	}

	public void testLeafBucketList_keyAt() {
		ILeafBucket buckets = new LeafBucketList();
		leafBucket_keyAt(buckets);
	}

	public void testLeafBucketList_split() {
		ILeafBucket buckets = new LeafBucketList();
		leafBucket_split(buckets);
	}

	public void testLeafBucketList_split2() {
		ILeafBucket buckets = new LeafBucketList();
		leafBucket_split2(buckets);
	}

	public void testLeafBucketList_merge() {
		ILeafBucket b1 = new LeafBucketList();
		ILeafBucket b2 = new LeafBucketList();
		leafBucket_merge(b1, b2);
	}

	public void testLeafBucketList_merge2() {
		ILeafBucket b1 = new LeafBucketList();
		ILeafBucket b2 = new LeafBucketList();
		leafBucket_merge2(b1, b2);
	}


	private void leafBucket_insert(ILeafBucket buckets) {
		buckets.insert(new IntKey(3), new Integer(3));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(0), new Integer(0));
		buckets.insert(new IntKey(2), new Integer(2));

		assertTrue(buckets.keyCount() == 4);
		assertEquals(buckets.keyAt(0), new IntKey(0));
		assertEquals(buckets.keyAt(1), new IntKey(1));
		assertEquals(buckets.keyAt(2), new IntKey(2));
		assertEquals(buckets.keyAt(3), new IntKey(3));
	}

	private void leafBucket_remove(ILeafBucket buckets) {
		buckets.insert(new IntKey(3), new Integer(3));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(0), new Integer(0));
		buckets.insert(new IntKey(2), new Integer(2));

		buckets.remove(new IntKey(2));
		assertTrue(buckets.keyCount() == 3);
		assertEquals(buckets.keyAt(0), new IntKey(0));
		assertEquals(buckets.keyAt(1), new IntKey(1));
		assertEquals(buckets.keyAt(2), new IntKey(3));

		buckets.remove(new IntKey(0));
		assertTrue(buckets.keyCount() == 2);
		assertEquals(buckets.keyAt(0), new IntKey(1));
		assertEquals(buckets.keyAt(1), new IntKey(3));

		assertFalse(buckets.remove(new IntKey(0)));
		assertFalse(buckets.remove(new IntKey(2)));

		buckets.remove(new IntKey(3));
		assertTrue(buckets.keyCount() == 1);
		buckets.remove(new IntKey(1));
		assertTrue(buckets.keyCount() == 0);

		buckets.insert(new IntKey(2), new Integer(2));
		buckets.insert(new IntKey(3), new Integer(3));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(0), new Integer(0));

		assertTrue(buckets.keyCount() == 4);
	}

	private void leafBucket_find(ILeafBucket buckets) {
		buckets.insert(new IntKey(0), new Integer(0));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(2), new Integer(2));
		buckets.insert(new IntKey(3), new Integer(3));

		assertEquals(buckets.find(new IntKey(0)),  new Integer(0));
		assertEquals(buckets.find(new IntKey(1)),  new Integer(1));
		assertEquals(buckets.find(new IntKey(2)),  new Integer(2));
		assertEquals(buckets.find(new IntKey(3)),  new Integer(3));
	}

	private void leafBucket_keyAt(ILeafBucket buckets) {
		buckets.insert(new IntKey(0), new Integer(0));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(2), new Integer(2));
		buckets.insert(new IntKey(3), new Integer(3));

		assertEquals(buckets.keyAt(0), new IntKey(0));
		assertEquals(buckets.keyAt(1), new IntKey(1));
		assertEquals(buckets.keyAt(2), new IntKey(2));
		assertEquals(buckets.keyAt(3), new IntKey(3));
	}

	private void leafBucket_split(ILeafBucket buckets) {
		buckets.insert(new IntKey(0), new Integer(0));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(2), new Integer(2));
		buckets.insert(new IntKey(3), new Integer(3));
		assertTrue(buckets.keyCount() == 4);

		ILeafBucket rest = buckets.split();
		assertTrue(buckets.keyCount() == 2);
		assertTrue(rest.keyCount() == 2);

		assertEquals(buckets.keyAt(0), new IntKey(0));
		assertEquals(buckets.keyAt(1), new IntKey(1));
		assertEquals(rest.keyAt(0), new IntKey(2));
		assertEquals(rest.keyAt(1), new IntKey(3));

	}

	private void leafBucket_split2(ILeafBucket buckets) {
		buckets.insert(new IntKey(0), new Integer(0));
		buckets.insert(new IntKey(1), new Integer(1));
		buckets.insert(new IntKey(2), new Integer(2));
		buckets.insert(new IntKey(3), new Integer(3));
		assertTrue(buckets.keyCount() == 4);

		buckets.insert(new IntKey(4), new Integer(4));
		buckets.insert(new IntKey(5), new Integer(5));
		buckets.insert(new IntKey(6), new Integer(6));
		buckets.insert(new IntKey(7), new Integer(7));

		ILeafBucket rest = buckets.split();
		assertTrue(buckets.keyCount() == 4);
		assertTrue(rest.keyCount() == 4);

		assertEquals(buckets.keyAt(0), new IntKey(0));
		assertEquals(buckets.keyAt(1), new IntKey(1));
		assertEquals(buckets.keyAt(2), new IntKey(2));
		assertEquals(buckets.keyAt(3), new IntKey(3));
		assertEquals(rest.keyAt(0), new IntKey(4));
		assertEquals(rest.keyAt(1), new IntKey(5));
		assertEquals(rest.keyAt(2), new IntKey(6));
		assertEquals(rest.keyAt(3), new IntKey(7));
	}

	private void leafBucket_merge(ILeafBucket b1, ILeafBucket b2) {
		b1.insert(new IntKey(0), new Integer(0));
		b2.insert(new IntKey(1), new Integer(1));
		b2.insert(new IntKey(2), new Integer(2));

		assertTrue(b1.keyCount() == 1);
		assertTrue(b2.keyCount() == 2);
		b1.merge(b2);
		assertTrue(b1.keyCount() == 3);
		assertEquals(b1.keyAt(0), new IntKey(0));
		assertEquals(b1.keyAt(1), new IntKey(1));
		assertEquals(b1.keyAt(2), new IntKey(2));

		//middle key for rotate
		assertEquals(b1.keyAt(b1.keyCount()/2), new IntKey(1));
	}

	private void leafBucket_merge2(ILeafBucket b1, ILeafBucket b2) {
		b1.insert(new IntKey(0), new Integer(0));
		b1.insert(new IntKey(1), new Integer(1));
		b2.insert(new IntKey(2), new Integer(2));
		b2.insert(new IntKey(3), new Integer(3));
		b2.insert(new IntKey(4), new Integer(4));
		b2.insert(new IntKey(5), new Integer(5));

		assertTrue(b1.keyCount() == 2);
		assertTrue(b2.keyCount() == 4);		
		b1.merge(b2);
		assertTrue(b1.keyCount() == 6);
		assertEquals(b1.keyAt(0), new IntKey(0));
		assertEquals(b1.keyAt(1), new IntKey(1));
		assertEquals(b1.keyAt(2), new IntKey(2));
		assertEquals(b1.keyAt(3), new IntKey(3));
		assertEquals(b1.keyAt(4), new IntKey(4));
		assertEquals(b1.keyAt(5), new IntKey(5));

		//middle key for rotate
		assertEquals(b1.keyAt(b1.keyCount()/2), new IntKey(3));
	}

}


