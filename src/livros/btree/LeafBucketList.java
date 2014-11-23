package livros.btree;

import livros.Debug;
import livros.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

class LeafBucketList implements ILeafBucket
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

		public String toString() {
			return "{"+key+":"+value+"}";
		}
		public Key key;
		public Object value;
	}

	private List mBucketList;

	public LeafBucketList() {
		mBucketList = new ArrayList();
	}

	public LeafBucketList(List li) {
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

	public boolean insert(Key key, Object entry) {
		int index = lowerBound(mBucketList, key);
		KV newkv = new KV(key, entry);
		mBucketList.add(index, newkv);
		return true;
	}

    private int lowerBound(List list, Key key) {
		final int len = list.size();
        int lo = 0, hi = len-1;
        while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			KV kv = (KV)list.get(mid);
            int cmp = kv.key.compareTo(key);
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

    private int lessThan(List list, Key key) {
		/*
		System.out.print("[");
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i)+", ");
		}
		System.out.print("]\n");
		*/
		final int len = list.size();
        int lo = 0, hi = len-1;
        while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			KV kv = (KV)list.get(mid);
            int cmp = kv.key.compareTo(key);
			//System.out.println("lo="+lo+" hi="+hi+" mid="+mid+" list[mid]="+kv.key+ " key="+key);
            if (cmp < 0) {
                lo = mid + 1;
                if (hi < lo) {
					//System.out.println("return "+mid);
                    return mid;
				}
			} else {
                hi = mid - 1;
                if (hi < lo) {
					//System.out.println("return "+(mid-1));
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
			int cmp = kv.key.compareTo(key);
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

	public boolean remove(Key key) {
		if (mBucketList.isEmpty()) {
			return false;
		}

		int index = Collections.binarySearch(mBucketList, new KV(key, null));
		if (index >= 0) {
			KV removed = (KV)mBucketList.remove(index);
			return true;
		} else {
			return false;
		}
	}

	public Object find(Key key) {
		int index = Collections.binarySearch(mBucketList, new KV(key, null));
		if (index >= 0) {
			KV kv = (KV)mBucketList.get(index);
			return kv.value;
		} else {
			return null;
		}
	}

	public List findLess(Key key) {
		int index = lessThan(mBucketList, key);
		if (index >= 0) {
			List list = subList(mBucketList, 0, index+1);
			Debug.assertTrue(!list.isEmpty());
			return kvList2ValueList(list);
		} else {
			return null;
		}
	}

	public List findGreater(Key key) {
		int index = greaterThan(mBucketList, key);
		if (index < mBucketList.size()) {
			List list = subList(mBucketList, index, mBucketList.size());
			Debug.assertTrue(!list.isEmpty());
			return kvList2ValueList(list);
		} else {
			return null;
		}
	}

	public List values() {
		return kvList2ValueList(mBucketList);
	}

	private List subList(List src, int from, int to) {
		List dst = new ArrayList();
		for (int i = from; i < to; i++) {
			dst.add(src.get(i));
		}
		return dst;
	}

	private List kvList2ValueList(List kvs) {
		//kv list to value list
		List values = new ArrayList();
		Iterator iter = kvs.iterator();
		while (iter.hasNext()) {
			KV kv = (KV)iter.next();
			values.add(kv.value);
		}
		return values;
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
	public ILeafBucket split() {
		//List middle = mBucketList.subList(mBucketList.size()/2, mBucketList.size());
		//List li = new ArrayList(middle);
		//middle.clear();
		List li = splitList(mBucketList);
		return new LeafBucketList(li);
	}

	public void merge(ILeafBucket leafbucket) {
		LeafBucketList other = (LeafBucketList)leafbucket;
		mBucketList.addAll(other.mBucketList);
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
		return mBucketList.size();
	}

	public boolean isEmpty() {
		return mBucketList.isEmpty();
	}
}
