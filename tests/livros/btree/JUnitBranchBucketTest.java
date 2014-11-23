package livros.btree;

import livros.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitBranchBucketTest extends TestCase
{
	public JUnitBranchBucketTest(String name) {
		super(name);
	}

	public void testBranchBucketArray_insert() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_insert(buckets);
	}

	public void testBranchBucketArray_remove() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_remove(buckets);
	}

	public void testBranchBucketArray_replaceKey() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_replaceKey(buckets);
	}

	public void testBranchBucketArray_find() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_find(buckets);
	}

	public void testBranchBucketArray_keyAt() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_keyAt(buckets);
	}

	public void testBranchBucketArray_entryAt() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_entryAt(buckets);
	}

	public void testBranchBucketArray_split() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_split(buckets);
	}

	public void testBranchBucketArray_split2() {
		IBranchBucket buckets = new BranchBucketArray();
		branchBucket_split2(buckets);
	}

	public void testBranchBucketArray_merge() {
		IBranchBucket b1 = new BranchBucketArray();
		IBranchBucket b2 = new BranchBucketArray();
		branchBucket_merge(b1, b2);
	}

	public void testBranchBucketArray_merge2() {
		IBranchBucket b1 = new BranchBucketArray();
		IBranchBucket b2 = new BranchBucketArray();
		branchBucket_merge2(b1, b2);
	}

	public void testBranchBucketList_insert() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_insert(buckets);
	}

	public void testBranchBucketList_remove() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_remove(buckets);
	}

	public void testBranchBucketList_replaceKey() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_replaceKey(buckets);
	}

	public void testBranchBucketList_find() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_find(buckets);
	}

	public void testBranchBucketList_keyAt() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_keyAt(buckets);
	}

	public void testBranchBucketList_entryAt() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_entryAt(buckets);
	}

	public void testBranchBucketList_split() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_split(buckets);
	}

	public void testBranchBucketList_split2() {
		IBranchBucket buckets = new BranchBucketList();
		branchBucket_split2(buckets);
	}

	public void testBranchBucketList_merge() {
		IBranchBucket b1 = new BranchBucketList();
		IBranchBucket b2 = new BranchBucketList();
		branchBucket_merge(b1, b2);
	}

	public void testBranchBucketList_merge2() {
		IBranchBucket b1 = new BranchBucketList();
		IBranchBucket b2 = new BranchBucketList();
		branchBucket_merge2(b1, b2);
	}

	private void branchBucket_insert(IBranchBucket buckets) {
		assertTrue(buckets.isEmpty());
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));

		assertTrue(buckets.isEmpty() == false);
		assertTrue(buckets.keyCount() == 4);
		assertEquals(buckets.keyAt(0), new IntKey(1));
		assertEquals(buckets.keyAt(1), new IntKey(2));
		assertEquals(buckets.keyAt(2), new IntKey(3));
		assertEquals(buckets.keyAt(3), new IntKey(4));
 		assertEquals(buckets.entryAt(0), new Integer(0));
		assertEquals(buckets.entryAt(1), new Integer(1));
		assertEquals(buckets.entryAt(2), new Integer(2));
		assertEquals(buckets.entryAt(3), new Integer(3));
		assertEquals(buckets.entryAt(4), new Integer(4));
	}

	private void branchBucket_remove(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));

		Key k = buckets.removeKey(new Integer(2));
		assertEquals(k, new IntKey(3));
		assertTrue(buckets.keyCount() == 3);
		assertEquals(buckets.keyAt(0), new IntKey(1));
		assertEquals(buckets.keyAt(1), new IntKey(2));
		assertEquals(buckets.keyAt(2), new IntKey(4));
 		assertEquals(buckets.entryAt(0), new Integer(0));
		assertEquals(buckets.entryAt(1), new Integer(1));
		assertEquals(buckets.entryAt(2), new Integer(2));
		assertEquals(buckets.entryAt(3), new Integer(4));

		k = buckets.removeKey(new Integer(0));
		assertEquals(k, new IntKey(1));
		assertTrue(buckets.keyCount() == 2);
		assertEquals(buckets.keyAt(0), new IntKey(2));
		assertEquals(buckets.keyAt(1), new IntKey(4));
		assertEquals(buckets.entryAt(0), new Integer(0));
		assertEquals(buckets.entryAt(1), new Integer(2));
		assertEquals(buckets.entryAt(2), new Integer(4));

		k = buckets.removeKey(new Integer(2));
		assertEquals(k, new IntKey(4));
		assertTrue(buckets.keyCount() == 1);
		assertEquals(buckets.keyAt(0), new IntKey(2));
		assertEquals(buckets.entryAt(0), new Integer(0));
		assertEquals(buckets.entryAt(1), new Integer(2));

		k = buckets.removeKey(new Integer(0));
		assertEquals(k, new IntKey(2));
		assertTrue(buckets.keyCount() == 0);
		assertEquals(buckets.entryAt(0), new Integer(0));

		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		assertTrue(buckets.keyCount() == 1);
	}

	private void branchBucket_replaceKey(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));

		Key k;
		k = buckets.replaceKey(new Integer(0), new IntKey(0));
		assertEquals(k, new IntKey(1));

		k = buckets.replaceKey(new Integer(1), new IntKey(1));
		assertEquals(k, new IntKey(2));

		k = buckets.replaceKey(new Integer(2), new IntKey(2));
		assertEquals(k, new IntKey(3));

		k = buckets.replaceKey(new Integer(3), new IntKey(3));
		assertEquals(k, new IntKey(4));
	}


	private void branchBucket_find(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));

		assertEquals(buckets.findNodeLessEqual(new IntKey(-1)),  new Integer(0));
		assertEquals(buckets.findNodeLessEqual(new IntKey(0)),  new Integer(0));
		assertEquals(buckets.findNodeLessEqual(new IntKey(1)),  new Integer(1));
		assertEquals(buckets.findNodeLessEqual(new IntKey(2)),  new Integer(2));
		assertEquals(buckets.findNodeLessEqual(new IntKey(3)),  new Integer(3));
		assertEquals(buckets.findNodeLessEqual(new IntKey(4)),  new Integer(4));
		assertEquals(buckets.findNodeLessEqual(new IntKey(5)),  new Integer(4));
		assertEquals(buckets.findNodeLessEqual(new IntKey(6)),  new Integer(4));
	}

	private void branchBucket_keyAt(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));

		assertEquals(buckets.keyAt(0), new IntKey(1));
		assertEquals(buckets.keyAt(1), new IntKey(2));
		assertEquals(buckets.keyAt(2), new IntKey(3));
		assertEquals(buckets.keyAt(3), new IntKey(4));
	}

	private void branchBucket_entryAt(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));

		assertEquals(buckets.entryAt(0), new Integer(0));
		assertEquals(buckets.entryAt(1), new Integer(1));
		assertEquals(buckets.entryAt(2), new Integer(2));
		assertEquals(buckets.entryAt(3), new Integer(3));
		assertEquals(buckets.entryAt(4), new Integer(4));
	}

	private void branchBucket_split(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		assertTrue(buckets.keyCount() == 4);

		Key separator = buckets.middleKey();
		assertEquals(separator, new IntKey(2));

		IBranchBucket rest = buckets.split();
		assertTrue(buckets.keyCount() == 1);
		assertTrue(rest.keyCount() == 2);

		assertEquals(buckets.keyAt(0), new IntKey(1));
		assertEquals(buckets.entryAt(0), new Integer(0));
		assertEquals(buckets.entryAt(1), new Integer(1));
		assertEquals(rest.keyAt(0), new IntKey(3));
		assertEquals(rest.keyAt(1), new IntKey(4));
		assertEquals(rest.entryAt(0), new Integer(2));
		assertEquals(rest.entryAt(1), new Integer(3));
		assertEquals(rest.entryAt(2), new Integer(4));
	}

	private void branchBucket_split2(IBranchBucket buckets) {
		buckets.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		buckets.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));
		buckets.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));
		buckets.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		assertTrue(buckets.keyCount() == 4);

		buckets.insertSeparator(new IntKey(5), new Integer(4), new Integer(5));
		buckets.insertSeparator(new IntKey(6), new Integer(5), new Integer(6));
		buckets.insertSeparator(new IntKey(7), new Integer(6), new Integer(7));
		buckets.insertSeparator(new IntKey(8), new Integer(7), new Integer(8));

		Key separator = buckets.middleKey();
		assertEquals(separator, new IntKey(4));

		IBranchBucket rest = buckets.split();
		assertTrue(buckets.keyCount() == 3);
		assertTrue(rest.keyCount() == 4);

		assertEquals(buckets.keyAt(0), new IntKey(1));
		assertEquals(buckets.keyAt(1), new IntKey(2));
		assertEquals(buckets.keyAt(2), new IntKey(3));
		assertEquals(rest.keyAt(0), new IntKey(5));
		assertEquals(rest.keyAt(1), new IntKey(6));
		assertEquals(rest.keyAt(2), new IntKey(7));
		assertEquals(rest.keyAt(3), new IntKey(8));
	}

	private void branchBucket_merge(IBranchBucket b1, IBranchBucket b2) {
		b1.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		b2.insertSeparator(new IntKey(3), new Integer(2), new Integer(3));

		assertTrue(b1.keyCount() == 1);
		assertTrue(b2.keyCount() == 1);

		b1.merge(new IntKey(2), b2);
		assertTrue(b1.keyCount() == 3);
		assertEquals(b1.keyAt(0), new IntKey(1));
		assertEquals(b1.keyAt(1), new IntKey(2));
		assertEquals(b1.keyAt(2), new IntKey(3));
		assertEquals(b1.entryAt(0), new Integer(0));
		assertEquals(b1.entryAt(1), new Integer(1));
		assertEquals(b1.entryAt(2), new Integer(2));
		assertEquals(b1.entryAt(3), new Integer(3));
	}

	private void branchBucket_merge2(IBranchBucket b1, IBranchBucket b2) {
		b1.insertSeparator(new IntKey(1), new Integer(0), new Integer(1));
		b1.insertSeparator(new IntKey(2), new Integer(1), new Integer(2));

	b2.insertSeparator(new IntKey(4), new Integer(3), new Integer(4));
		b2.insertSeparator(new IntKey(5), new Integer(4), new Integer(5));
		b2.insertSeparator(new IntKey(6), new Integer(5), new Integer(6));

		assertTrue(b1.keyCount() == 2);
		assertTrue(b2.keyCount() == 3);		
		b1.merge(new IntKey(3), b2);
		assertTrue(b1.keyCount() == 6);
		assertEquals(b1.keyAt(0), new IntKey(1));
		assertEquals(b1.keyAt(1), new IntKey(2));
		assertEquals(b1.keyAt(2), new IntKey(3));
		assertEquals(b1.keyAt(3), new IntKey(4));
		assertEquals(b1.keyAt(4), new IntKey(5));
		assertEquals(b1.keyAt(5), new IntKey(6));

		assertEquals(b1.entryAt(0), new Integer(0));
		assertEquals(b1.entryAt(1), new Integer(1));
		assertEquals(b1.entryAt(2), new Integer(2));
		assertEquals(b1.entryAt(3), new Integer(3));
		assertEquals(b1.entryAt(4), new Integer(4));
		assertEquals(b1.entryAt(5), new Integer(5));
		assertEquals(b1.entryAt(6), new Integer(6));
	}

}


