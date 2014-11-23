package livros.btree;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.List;

class Leaf extends Node {
	public static int ORDER = 1024;
	ILeafBucket mBuckets;

	public Leaf(BTree tree, Branch parent) {
		super(tree, parent);
		mBuckets = new LeafBucketList();
	}

	public Leaf(BTree tree, Branch parent, ILeafBucket leafb) {
		super(tree, parent);
		mBuckets = leafb;
	}

	public boolean isOverflow() {
		return keyCount() >= ORDER;
	}

	public boolean isUnderflow() {
		return keyCount() < ORDER/2-1;
	}

	public boolean insert(Key key, Object entry) {
		Log.v("insert "+key+" at Leaf "+hashCode()); 
		mBuckets.insert(key, entry);
		balanceTreeForInsertIfNeeded();
		return true;
	}

	public boolean remove(Key key) {
		Log.v("remove "+key+" at Leaf " + hashCode());
		if (!mBuckets.remove(key)) {
			return false;
		}
		balanceTreeForRemoveIfNeeded();
		return true;
	}

	public Object find(Key key) {
		return mBuckets.find(key);
	}

	public List findLess(Key key) {
		List tail = mBuckets.findLess(key);
		//printValues(l);
		Leaf prev = (Leaf)prevSiblingAll();
		while (prev != null) {
			List head = new ArrayList(prev.values());
			if (tail != null) {
				head.addAll(tail);
			}
			tail = head;
			//printValues(l);
			prev = (Leaf)prev.prevSiblingAll();
		}
		return tail;
	}

	public List findGreater(Key key) {
		List l = mBuckets.findGreater(key);
		//printValues(l);
		List head = new ArrayList();
		if (l != null) {
			head.addAll(l);
		}
		Leaf next = (Leaf)nextSiblingAll();
		while (next != null) {
			head.addAll(next.values());
			//printValues(head);
			next = (Leaf)next.nextSiblingAll();
		}
		return head.isEmpty() ? null : head;
	}

	public List allValues() {
		Debug.assertTrue(prevSiblingAll() == null);
		List values = values();
		Leaf next = (Leaf)nextSiblingAll();;
		while (next != null) {
			values.addAll(next.values());
			//printValues(l);
			next = (Leaf)next.nextSiblingAll();
		}
		return values;
	}

	public List values() {
		return mBuckets.values();
	}

	private void printValues(List values) {
		for (int i = 0; i < values.size(); i++) {
			Object o = values.get(i);
			System.out.print(o + ", ");
		}
		System.out.println("");
	}

	public boolean isLeaf() {
		return true;
	}

	public void balanceTreeForInsertIfNeeded() {
		if (isOverflow()) {
			splitUp();
		}
	}

	public void balanceTreeForRemoveIfNeeded() {
		if (isUnderflow()) {
			if (nextSibling() != null) {
				if (nextSibling().keyCount() <= ORDER/2 
					&& mParent.keyCount() > 1) {
					fusion();
				} else if (nextSibling().keyCount() > 1) {
					rotateLeft();
				} else if (mParent != null) {
					decreaseDepth();
				}
			} else if (prevSibling() != null) {
				if (prevSibling().keyCount() <= ORDER/2 
					&& mParent.keyCount() > 1) {
					fusion();
				} else if (prevSibling().keyCount() > 1) {
					rotateRight();
				} else if (mParent != null) {
					decreaseDepth();
				}
			}
		}
	}

	/*
      split in case of LEAF
      -------------------- this
      |k1| |k2| |k3| |k4|
      --------------------
      |e1| |e2| |e3| |e4|
      --------------------
      
             |
             V

         ----------  new parent node
          |k3| nil|
         ----------
          |n0| n1 |
         ----------

     ---------this  ----------new sibling
     |k1| |k2|      |k3| |k4|
     ---------      ----------
     |e1| |e1|      |e3| |e4|
     ---------      ----------

	*/
	protected void splitUp() {
		Log.v("splitUp at Leaf " + hashCode());
		Debug.assertTrue(isOverflow());
		if (mParent == null) {
			Branch newRoot = new Branch(mTree, null);
			mTree.updateRoot(newRoot);
			mParent = newRoot;
		}

		ILeafBucket mid = mBuckets.split();
		Leaf newSibling = new Leaf(mTree, mParent, mid);

		Key kickedKey = mid.keyAt(0);
		Debug.assertTrue(kickedKey != null);

		mParent.insertKeyFromChild(this, newSibling, kickedKey);
		mParent.balanceTreeForInsertIfNeeded();
	}

	/*
    rotate left
    ------------------
	       2
         /   \
        x    2:3

    ------------------
	       3
         /   \
        2     3
	*/                     
	protected void rotateLeft() {
		Log.v("rotateLeft at Leaf " + hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);

		Leaf next = (Leaf)nextSibling();
		Debug.assertTrue(next != null);
		Debug.assertTrue(next.keyCount() > 1);

		mBuckets.merge(next.mBuckets);
		Key upKey = mBuckets.keyAt(mBuckets.keyCount()/2);
		next.mBuckets = mBuckets.split();
		mParent.replaceKey(this, upKey);
	}

	/*
    rotate right
    ------------------
	       3
         /   \
       1:2    x

    ------------------
	       2
         /   \
        1     2
	*/                     
	protected void rotateRight() {
		Log.v("rotateRight at Leaf " + hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);

		Leaf prev = (Leaf)prevSibling();
		Debug.assertTrue(prev != null);
		Debug.assertTrue(prev.keyCount() > 1);

		prev.mBuckets.merge(mBuckets);
		Key upKey = prev.mBuckets.keyAt(prev.mBuckets.keyCount()/2);
		mBuckets = prev.mBuckets.split();
		mParent.replaceKey(prev, upKey);
	}

	/*
	  fusion case 1:
      ------------------
	       2:3:4
         /  | | \
        x   2 3  4  

      ------------------
	       3:4
         /  | \
        2   3  4    
		
	  fusion case 2:
      ------------------
	       2:3:4
         /  | | \
        1   x 3  4  

      ------------------
	       2:4
         /  | \
        1   3  4    

	  fusion case 3:
      ------------------
	       2:3:4
         /  | | \
        1   2 3  x  

      ------------------
	       2:3
         /  | \
        1   2  3    
	 */
	private void fusion() {
		Log.v("fusion at Leaf " + hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);
		Debug.assertTrue(mParent.keyCount() > 1);

		Leaf lhs, rhs;
		if (nextSibling() != null) {
			lhs = this;
			rhs = (Leaf)nextSibling();
		} else {
			lhs = (Leaf)prevSibling();
			rhs = this;
		}
		lhs.mBuckets.merge(rhs.mBuckets);
		mParent.removeKey(lhs);
	}

	private void decreaseDepth() {
		Log.v("decrease at Leaf " + hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);
		Debug.assertTrue(mParent.keyCount() == 1);

		Leaf lhs, rhs;
		if (nextSibling() != null) {
			lhs = this;
			rhs = (Leaf)nextSibling();
		} else {
			lhs = (Leaf)prevSibling();
			rhs = this;
		}
		lhs.mBuckets.merge(rhs.mBuckets);
		mParent.removeKey(lhs);
		if (mTree.isRoot(mParent)) {
			mTree.updateRoot(lhs);
		} else {
			mParent.balanceTreeForRemoveIfNeeded();
		}
	}

	protected int keyCount() {
		return mBuckets.keyCount();
	}

	public Key keyAt(int index) {
		return mBuckets.keyAt(index);
	}

	public Object childAt(int index) {
		return mBuckets.entryAt(index);
	}
}
