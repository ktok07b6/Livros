package livros.btree;

import livros.Debug;
import livros.Log;
import java.util.List;

abstract class Node {
	protected BTree mTree;
	protected Branch mParent;
	int mOrder;

	public Node(BTree tree, Branch parent) {
		mTree = tree;
		mParent = parent;
	}

	public abstract boolean insert(Key key, Object entry);
	public abstract boolean remove(Key key);
	public abstract Object find(Key key);
	public abstract List findLess(Key key);
	public abstract List findGreater(Key key);
	public abstract List allValues();
	public abstract boolean isLeaf();
	public abstract Key keyAt(int i);
	public abstract Object childAt(int i);

	public Node parent() {
		return mParent;
	}

	protected boolean canRotateLeft() {
		return nextSibling() != null && nextSibling().keyCount() > 1;
	}

	protected boolean canRotateRight() {
		return prevSibling() != null && prevSibling().keyCount() > 1;
	}

	protected boolean canFusionRight() {
		return nextSibling() != null && nextSibling().keyCount() == 1 &&
			mParent.keyCount() > 1;
	}

	protected boolean canFusionLeft() {
		return prevSibling() != null && prevSibling().keyCount() == 1 &&
			mParent.keyCount() > 1;
	}

	protected abstract int keyCount();

	protected Node nextSibling() {
		if (mParent != null) {
			return mParent.nextChild(this);
		} else {
			return null;
		}
	}

	protected Node prevSibling() {
		if (mParent != null) {
			return mParent.prevChild(this);
		} else {
			return null;
		}
	}

	protected Node nextSiblingAll() {
		if (mParent != null) {
			return mParent.nextChildSibling(this);
		} else {
			return null;
		}
	}

	protected Node prevSiblingAll() {
		if (mParent != null) {
			return mParent.prevChildSibling(this);
		} else {
			return null;
		}
	}

	protected abstract void balanceTreeForInsertIfNeeded();
	protected abstract void balanceTreeForRemoveIfNeeded();

	// ----- for test
	/*
	public Node childAt(int index) {
		if (isLeaf()) {
			return null;
		}
		Bucket b = mBucket;
		int i = 0;
		while (b != null && i < index) {
			i++;
			b = b.next();
		}
		return (Node)b.entry();
	}
	public Key keyAt(int index) {
		Bucket b = mBucket;
		int i = 0;
		while (b != null && i < index) {
			i++;
			b = b.next();
		}
		return b.key();
	}
	*/
}
