package livros.btree;

import livros.Debug;
import livros.Log;
import java.util.List;

class LeafBucketArray implements ILeafBucket
{
	private int mKeyCount;
	Bucket mBucket;

	public LeafBucketArray() {
		mKeyCount = 0;
	}

	public String toString() {
		return mBucket.toString();
	}

	public boolean insert(Key key, Object entry) {
		if (mKeyCount == 0) {
			mBucket = new Bucket(key, entry);
		} else {
			Bucket b = mBucket;
			Bucket prev = null;
			while (b != null) {
				if (key.compareTo(b.key()) < 0) {
					break;
				}
				prev = b;
				b = b.next();
			}

			Bucket newb = new Bucket(key, entry);
			//insert newb in bucket list
			newb.setNext(b);
			if (prev != null) {
				prev.setNext(newb);
			} else {
				mBucket = newb;
			}
		}
		mKeyCount++;
		return true;
	}

	public boolean remove(Key key) {
		if (mKeyCount == 0) {
			return false;
		}

		Bucket b = Bucket.find(mBucket, key);
		if (b == null) {
			Log.e("remove " + key + " is not found");
			return false;
		}

		if (b.prev() == null) {// b is head
			mBucket = b.next();
		} else {
			b.prev().setNext(b.next());
		}
		if (b.next() != null) {//b is not tail
			b.next().setPrev(b.prev());
		}
		mKeyCount--;
		return true;
	}

	public Object find(Key key) {
		Bucket b = Bucket.find(mBucket, key);
		return (b != null) ? b.entry() : null;
	}

	public List findLess(Key key) {
		//NIY
		return null;
	}

	public List findGreater(Key key) {
		//NIY
		return null;
	}

	public List values() {
		//NIY
		return null;
	}

	public ILeafBucket split() {
		Bucket middle = Bucket.at(mBucket, mKeyCount/2);
		middle.prev().setNext(null);//cut lhs to rhs link
		middle.setPrev(null);//cut rhs to lhs link

		LeafBucketArray newb = new LeafBucketArray();
		newb.mBucket = middle;
		mKeyCount = Bucket.keyCount(mBucket);
		newb.mKeyCount = Bucket.keyCount(newb.mBucket);
		return newb;
	}

	public void merge(ILeafBucket leafbucket) {
		LeafBucketArray other = (LeafBucketArray)leafbucket;
		if (other.mBucket == null) {
			return;
		}
 		if (mBucket == null) {
			mBucket = other.mBucket;
		} else {
			Bucket b = mBucket;
			while (b.next() != null) {
				b = b.next();
			}
			b.setNext(other.mBucket);
			other.mBucket.setPrev(b);
		}
		mKeyCount = mKeyCount + other.mKeyCount;
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
}
