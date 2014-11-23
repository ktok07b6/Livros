package livros.btree;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

class BranchBucketList implements IBranchBucket
{
	class KV implements Comparable {
		public KV(Key k, Object o) {	
			key = k;
			value = o;
		}

		public int compareTo(Object other) {
			KV rhs = (KV)other;
			return key.compareTo(rhs.key);
		}

		public Key key;
		public Object value;
	}

	private List mBucketList;

	public BranchBucketList() {
		mBucketList = new ArrayList();
	}

	public BranchBucketList(List li) {
		mBucketList = li;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator iter = mBucketList.iterator();
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			sb.append((kv.key != null) ? kv.key.toString() : "null");
			sb.append(":");
			sb.append((kv.value != null) ? kv.value.hashCode() : 0);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public Node next(Node node) {
		Iterator iter = mBucketList.iterator();
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			if (kv.value.equals(node)) {
				if (iter.hasNext()) {
					KV next = (KV)iter.next();
					return (Node)next.value;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	public Node prev(Node node) {
		Iterator iter = mBucketList.iterator();
		Node prev = null;
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			if (kv.value.equals(node)) {
				if (prev != null) {
					return prev;
				} else {
					return null;
				}
			}
			prev = (Node)kv.value;
		}
		return null;
	}
	
	public void changeParent(Branch parent) {
		Iterator iter = mBucketList.iterator();
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			Node node = (Node)kv.value;
			node.mParent = parent;
		}
	}

	public Key middleKey() {
		Debug.assertTrue(mBucketList.size() >= 2);
		return ((KV)mBucketList.get(mBucketList.size()/2-1)).key;
	}

	private List splitList(List ll) {
		List rl = new ArrayList();
		int end = ll.size();
		int mid = ll.size()/2;
		for (int i = mid; i < end; i++) {
			rl.add(ll.get(i));
		}
		for (int i = end-1; i >= mid; i--) {
			ll.remove(i);
		}
		return rl;
	}
	public IBranchBucket split() {
		Debug.assertTrue(mBucketList.size() >= 4);
		KV tail = (KV)mBucketList.get(mBucketList.size()/2-1);
		tail.key = null;
		//List middle = mBucketList.subList(mBucketList.size()/2, mBucketList.size());
		//List li = new ArrayList(middle);
		//middle.clear();
		List li = splitList(mBucketList);
		KV tail2 = (KV)mBucketList.get(mBucketList.size()-1);
		Debug.assertTrue(tail2.key == null);
		return new BranchBucketList(li);
	}

	public void insertSeparator(Key separator, Object lhs, Object rhs) {
		if (0 == mBucketList.size()) {
			mBucketList.add(new KV(separator, lhs));
			mBucketList.add(new KV(null, rhs));
			return;
		}

		int index = lowerBound(mBucketList, separator);
		KV kv = null;
		Debug.assertTrue(index < mBucketList.size());
		KV next = (KV)mBucketList.get(index);
		next.value = rhs;
		kv = new KV(separator, lhs);
		mBucketList.add(index, kv);
	}

	private int lowerBound(List list, Key key) {
		final int len = list.size();
		int lo = 0, hi = len-1;
		while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			KV kv = (KV)list.get(mid);
			int cmp = kv.key != null ? kv.key.compareTo(key) : 0;
			if (cmp < 0) {
				lo = mid + 1;
				if (hi < lo) {
					return mid + 1;
				}
			} else {
				hi = mid - 1;
				if (hi < lo) {
					return mid;
				}
			}
		}
		return 0;
	}

	private int upperBound(List list, Key key) {
		final int len = list.size();
		int lo = 0, hi = len-1;
		while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			KV kv = (KV)list.get(mid);
			int cmp = kv.key != null ? kv.key.compareTo(key) : 1;
			if (cmp <= 0) {
				lo = mid + 1;
				if (hi < lo) {
					return mid;
				}
			} else {
				hi = mid - 1;
				if (hi < lo) {
					return mid - 1;
				}
			}
		}
		return 0;
	}

	private int greaterThan(List list, Key key) {
		final int len = list.size();
		int lo = 0, hi = len-1;
		while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			KV kv = (KV)list.get(mid);
			int cmp = kv.key != null ? kv.key.compareTo(key) : 1;
			if (cmp <= 0) {
				lo = mid + 1;
				if (hi < lo) {
					return mid + 1;
				}
			} else {
				hi = mid - 1;
				if (hi < lo) {
					return mid;
				}
			}
		}
		return 0;
	}

	public void merge(Key mid, IBranchBucket branchb) {
		BranchBucketList other = (BranchBucketList)branchb;
		Debug.assertTrue(!mBucketList.isEmpty());

		KV tail = (KV)mBucketList.get(mBucketList.size()-1);
		Debug.assertTrue(tail.key == null);
		tail.key = mid;
		mBucketList.addAll(other.mBucketList);
	}

	public Key replaceKey(Object finding, Key key) {
		Key oldkey = null;
		Iterator iter = mBucketList.iterator();
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			if (kv.value.equals(finding)) {
				oldkey = kv.key;
				kv.key = key;
				break;
			}
		}
		return oldkey;
	}

	public Key removeKey(Object finding) {
		Key key = null;
		Iterator iter = mBucketList.iterator();
		int i = 0;
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			if (kv.value.equals(finding)) {
				key = kv.key;
				Debug.assertTrue(iter.hasNext());
				KV next = (KV)iter.next();
				next.value = finding;
				if (key == null) {
					Log.e("remove key is null " + toString());
				}
				mBucketList.remove(i);
				return key;
			}
			i++;
		}
		Debug.assertTrue(false);
		return null;
	}

	public Object findNodeLessEqual(Key key) {
		int index = greaterThan(mBucketList, key);
		if (0 <= index) {
			KV kv = (KV)mBucketList.get(index);
			return kv.value;
		} else {
			return null;
		}
	}

	public Key keyAt(int index) {
		KV kv = (KV)mBucketList.get(index);
		return kv != null ? kv.key : null;
	}

	public Object entryAt(int index) {
		KV kv = (KV)mBucketList.get(index);
		return kv != null ? kv.value : null;
	}

	public int keyCount() {
		return mBucketList.size()-1;
	}

	public boolean isEmpty() {
		return keyCount() <= 0;
	}

	public boolean verify() {
		for (int i = 0; i < mBucketList.size()-1; i++) {
			KV kv = (KV)mBucketList.get(i);
			if (kv.key == null) return false;
			if (kv.value == null) return false;
		}
		
		KV tail = (KV)mBucketList.get(mBucketList.size()-1);
		if (tail.key != null) return false;
		if (tail.value == null) return false;
		return true;
	}
}
