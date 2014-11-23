package livros.btree;

import livros.Debug;
import livros.Log;

class BranchBucketArray implements IBranchBucket
{
	private int mKeyCount;
	Bucket mBucket;
	public BranchBucketArray() {
		mKeyCount = 0;
	}

	public String toString() {
		return mBucket.toString();
	}
	
	public Node next(Node node) {
		Bucket b = mBucket;
		while (b != null) {
			if (b.node() == node) {
				break;
			}
			b = b.next();
		}
		Debug.assertTrue(b != null);
		return (Node)(b.next() != null ? b.next().entry() : null);
	}

	public Node prev(Node node) {
		Bucket b = mBucket;
		while (b != null) {
			if (b.node() == node) {
				break;
			}
			b = b.next();
		}
		Debug.assertTrue(b != null);
		return (Node)(b.prev() != null ? b.prev().entry() : null);
	}
	
	public void changeParent(Branch parent) {
		Bucket b = mBucket;
		while (b != null) {
			if (b.entry() instanceof Node) {
				Node node = (Node)b.entry();
				node.mParent = parent;
			}
			b = b.next();
		}
	}

	public Key middleKey() {
		return Bucket.at(mBucket, mKeyCount/2-1).key();
	}

	public IBranchBucket split() {
		Bucket middle = Bucket.at(mBucket, (mKeyCount+1)/2);
		middle.prev().setNext(null);//cut the link
		middle.prev().setKey(null);
		middle.setPrev(null);//cut the link

		BranchBucketArray newb = new BranchBucketArray();
		newb.mBucket = middle;
		mKeyCount = Bucket.keyCount(mBucket);
		newb.mKeyCount = Bucket.keyCount(newb.mBucket);
		return newb;
	}

	public void insertSeparator(Key separator, Object lhs, Object rhs) {
		if (0 == mKeyCount) {
			mBucket = new Bucket(separator, lhs);
			mBucket.setNext(new Bucket(null, rhs));
			mKeyCount = 1;
			return;
		}

		Bucket b = mBucket;
		Bucket prev = null;
		while (b.key() != null) {
			if (separator.compareTo(b.key()) < 0) {
				break;
			}
			prev = b;
			b = b.next();
		}

		Bucket newb = new Bucket(separator, lhs);
		b.setEntry(rhs);
		//insert newb in bucket list
		newb.setNext(b);
		if (prev != null) {
			prev.setNext(newb);
		} else {
			mBucket = newb;
		}
		mKeyCount++;
	}

	public void merge(Key mid, IBranchBucket branchb) {
		BranchBucketArray other = (BranchBucketArray)branchb;
		Bucket b = mBucket;
		while (b.next() != null) {
			b = b.next();
		}
		Debug.assertTrue(b.key() == null);
		b.setKey(mid);
		b.setNext(other.mBucket);
		other.mBucket.setPrev(b);
		mKeyCount = mKeyCount + other.mKeyCount + 1;
	}

	public Key replaceKey(Object entry, Key key) {
		Bucket b = mBucket;
		while (b.key() != null) {
			if (b.entry().equals(entry)) {
				break;
			}
			b = b.next();
		}
		Debug.assertTrue(b != null);
		Key leftKey = b.key();
		b.setKey(key);
		return leftKey;
	}

	public Key removeKey(Object finding) {
		Bucket b = mBucket;
		Key key = null;
		while (b != null) {
			if (b.entry().equals(finding)) {
				key = b.key();
				if (b.prev() != null) {
					b.prev().setNext(b.next());
				} else {
					mBucket = b.next();
					mBucket.setPrev(null);
				}
				Debug.assertTrue(b.next() != null);
				b.next().setEntry(finding);
				break;
			}
			b = b.next();
		}
		Debug.assertTrue(b != null);
		mKeyCount--;
		return key;
	}

	public Object findNodeLessEqual(Key key) {
		Bucket b = Bucket.findLessThan(mBucket, key);
		Debug.assertTrue(b != null);
		return b.entry();
	}

	public Key keyAt(int index) {
		return Bucket.at(mBucket, index).key();
	}

	public Object entryAt(int index) {
		return Bucket.at(mBucket, index).entry();
	}

	public int keyCount() {
		return mKeyCount;
	}

	public boolean isEmpty() {
		return mKeyCount == 0;
	}

	public boolean verify() {
		return true;
	}
}
