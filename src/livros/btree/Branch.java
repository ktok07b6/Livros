package livros.btree;

import livros.Debug;
import livros.Log;
import java.util.List;

class Branch extends Node {
	public static int ORDER = 1024;
	IBranchBucket mBuckets;

	public Branch(BTree tree, Branch parent) {
		super(tree, parent);
		mBuckets = new BranchBucketList();
	}

	public Branch(BTree tree, Branch parent, IBranchBucket branchb) {
		super(tree, parent);
		mBuckets = branchb;
		mBuckets.changeParent(this);
	}

	public boolean isOverflow() {
		return keyCount() >= ORDER;
	}

	public boolean isUnderflow() {
		return keyCount() < ORDER/2-1;
	}

	public boolean insert(Key key, Object entry) {
		Node child = findChildNode(key);
		return child.insert(key, entry);
	}
	
	public boolean remove(Key key) {
		Node child = findChildNode(key);
		return child.remove(key);
	}

	public Object find(Key key) {
		Node child = findChildNode(key);
		return child.find(key);
	}

	public List findLess(Key key) {
		Node child = findChildNode(key);
		return child.findLess(key);
	}

	public List findGreater(Key key) {
		Node child = findChildNode(key);
		return child.findGreater(key);
	}

	public List allValues() {
		return ((Node)childAt(0)).allValues();
	}

	public boolean isLeaf() {
		return false;
	}

	public Node nextChild(Node node) {
		return mBuckets.next(node);
	}

	public Node prevChild(Node node) {
		return mBuckets.prev(node);
	}

	public Node nextChildSibling(Node node) {
		Node next = mBuckets.next(node);
		if (next == null) {
			Branch nextSibling = (Branch)this.nextSiblingAll();
			if (nextSibling != null) {
				next = (Node)nextSibling.childAt(0);
			}
		}
		return next;
	}

	public Node prevChildSibling(Node node) {
		Node prev = mBuckets.prev(node);
		if (prev == null) {
			Branch prevSibling = (Branch)this.prevSiblingAll();
			if (prevSibling != null) {
				prev = (Node)prevSibling.childAt(prevSibling.keyCount());
			}
		}
		return prev;
	}

	protected void balanceTreeForInsertIfNeeded() {
		if (isOverflow()) {
			splitUp();
		}
	}

	protected void balanceTreeForRemoveIfNeeded() {
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
          split in case of BRANCH
            -------------------- this
            |k1| |k2| |k3| |nil|
            --------------------
            |n1| |n2| |n3| |n4|
            --------------------

                    |
                    V

                ----------  new parent
                   |k2|
                ----------
                   |n0|
                ----------
                 
          ---------- this   ---------- new sibling
          |k1| |nil|        |k3| |nil|
          ----------        ----------
          |n1| |n2|         |n3| |n4|
          ----------        ----------
	*/
	protected void splitUp() {
		Log.v("splitUp at Branch " + hashCode());
		Debug.assertTrue(isOverflow());

		if (mParent == null) {
			Branch newRoot = new Branch(mTree, null);
			mTree.updateRoot(newRoot);
			mParent = newRoot;
		}

		Key separator = mBuckets.middleKey();
		IBranchBucket rhs = mBuckets.split();
		Branch newSibling = new Branch(mTree, mParent, rhs);

		mParent.insertKeyFromChild(this, newSibling, separator);
		mParent.balanceTreeForInsertIfNeeded();

		Debug.assertTrue(mBuckets.verify());
		Debug.assertTrue(mParent.mBuckets.verify());
		Debug.assertTrue(newSibling.mBuckets.verify());
	}

	/* called from child node */
	protected void insertKeyFromChild(Node lhs, Node rhs, Key key) {
		Debug.assertTrue(lhs.mParent == this && rhs.mParent == this);
		mBuckets.insertSeparator(key, lhs, rhs);
	}

	private Node findChildNode(Key key) {
		return (Node)mBuckets.findNodeLessEqual(key);
	}

	/*
    rotate left
    ------------------
           2
         /   \
        x    3:4
       /    / | \
      A    B  C  D

    ------------------
           3
         /   \
        2     4
       / \   /  \
      A   B C    D
	*/                     
	protected void rotateLeft() {
		Log.v("rotateLeft at Branch " + this.hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);
		
		Branch next = (Branch)nextSibling();
		Debug.assertTrue(next != null);
		Debug.assertTrue(next.keyCount() > 1);

		Key downKey = mParent.replaceKey(this, null);
		mBuckets.merge(downKey, next.mBuckets);
		Key upKey = mBuckets.keyAt(mBuckets.keyCount()/2);
		next.mBuckets = mBuckets.split();
		mParent.replaceKey(this, upKey);

		mBuckets.changeParent(this);
		next.mBuckets.changeParent(next);

		Debug.assertTrue(mBuckets.verify());
		Debug.assertTrue(next.mBuckets.verify());
		Debug.assertTrue(mParent.mBuckets.verify());
	}

	/*
    rotate right
    ------------------
           3
         /   \
       1:2    x
      / | \    \
     A  B  C    D

    ------------------
           2
         /   \
        1     3
       / \   /  \
      A   B C    D
	*/                     
	protected void rotateRight() {
		Log.v("rotateRight at Branch " + this.hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);
		
		Branch prev = (Branch)prevSibling();
		Debug.assertTrue(prev != null);
		Debug.assertTrue(prev.keyCount() > 1);

		Key downKey = mParent.replaceKey(prev, null);
		prev.mBuckets.merge(downKey, mBuckets);
		Key upKey = prev.mBuckets.keyAt(prev.mBuckets.keyCount()/2);
		mBuckets = prev.mBuckets.split();
		mParent.replaceKey(prev, upKey);

		mBuckets.changeParent(this);
		prev.mBuckets.changeParent(prev);

		Debug.assertTrue(mBuckets.verify());
		Debug.assertTrue(prev.mBuckets.verify());
		Debug.assertTrue(mParent.mBuckets.verify());
	}

	/*
    fusion case 1
    ------------------
		   4:8
         /  |  \
        x   6  10
       /   / \
      a   b   c  

    ------------------
		   8
         /   \
        4:6  10
       / | \
      a  b  c

    fusion case 2
    ------------------
		   4:8
         /  |  \
        2   6   x
           / \   \
          a   b   c

    ------------------
		   4
         /   \
        2    6:8
            / | \
           a  b  c
	*/

	private void fusion() {
		Log.v("fusion at Branch " + hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);
		Debug.assertTrue(mParent.keyCount() > 1);

		Branch lhs, rhs;
		if (nextSibling() != null) {
			Debug.assertTrue(nextSibling().keyCount() >= 1);
			lhs = this;
			rhs = (Branch)nextSibling();
		} else {
			Debug.assertTrue(prevSibling().keyCount() >= 1);
			lhs = (Branch)prevSibling();
			rhs = this;
		}
		Key stealedKey = mParent.removeKey(lhs);
		lhs.mBuckets.merge(stealedKey, rhs.mBuckets);
		lhs.mBuckets.changeParent(lhs);

		Debug.assertTrue(lhs.mBuckets.verify());
		Debug.assertTrue(rhs.mBuckets.verify());
	}

	private void decreaseDepth() {
		Log.v("decrease at Branch " + hashCode());
		Debug.assertTrue(isUnderflow());
		Debug.assertTrue(mParent != null);
		Debug.assertTrue(mParent.keyCount() == 1);

		if (mParent.keyAt(0) == null) {
			Log.v(mTree.toString());
			System.exit(-1);
		}
		Branch lhs, rhs;
		if (nextSibling() != null) {
			Debug.assertTrue(nextSibling().keyCount() >= 1);
			lhs = this;
			rhs = (Branch)nextSibling();
		} else {
			Debug.assertTrue(prevSibling().keyCount() >= 1);
			lhs = (Branch)prevSibling();
			rhs = this;
		}
		Key stealedKey = mParent.removeKey(lhs);
		Debug.assertTrue(stealedKey != null);
		lhs.mBuckets.merge(stealedKey, rhs.mBuckets);
		lhs.mBuckets.changeParent(lhs);

		if (mTree.isRoot(mParent)) {
			mTree.updateRoot(lhs);
		} else {
			mParent.balanceTreeForRemoveIfNeeded();
		}
		Debug.assertTrue(lhs.mBuckets.verify());
		Debug.assertTrue(rhs.mBuckets.verify());
	}

	/* called from child node */
	protected Key replaceKey(Node n, Key k) {
		return mBuckets.replaceKey(n, k);
	}

	/* called from child node */
	protected Key removeKey(Node n) {
		return mBuckets.removeKey(n);
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
