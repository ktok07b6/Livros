package livros.btree;

import livros.Log;
import java.util.ArrayList;
import java.util.List;

public class BTree
{
	private Node mRoot;
	private int mSize;

	public BTree() {
		mRoot = new Leaf(this, null);
		mSize = 0;
	}

	public String toString() {
		return new BTreePrinter().toString(mRoot);
	}

	public void insert(Key key, Object value) {
		if (mRoot.insert(key, value)) {
			mSize++;
		}
	}

	public void remove(Key key) {
		if (mRoot.remove(key)) {
			mSize--;
		}
	}

	public Object find(Key key) {
		return mRoot.find(key);
	}

	public List findLess(Key key) {
		return mRoot.findLess(key);
	}

	public List findGreater(Key key) {
		return mRoot.findGreater(key);
	}

	public List allValues() {
		return mRoot.allValues();
	}

	void updateRoot(Node root) {
		mRoot = root;
		mRoot.mParent = null;
	}

	boolean isRoot(Node node) {
		return mRoot == node;
	}

	public int size() {
		return mSize;
	}

	//--------- for test
	Node root() {
		return mRoot;
	}
}
