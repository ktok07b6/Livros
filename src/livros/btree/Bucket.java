package livros.btree;

import livros.Debug;
import livros.Log;

/*
  the structure of the Bucket in a Node
  -------------------------------------

  node structure is ...

     k0  k1  k2
    /  \/  \/  \
   n0  n1  n2  n3 
 
  above is represented as Bucket list like the following

    B0     B1     B2     B3
  | key0 | key1 | key2 | null |
  | nod0 | nod1 | nod2 | nod3 |

 */
class Bucket {
	private Key mKey;
	private Object mEntry;
	private Bucket mPrev;
	private Bucket mNext;

	public static int keyCount(Bucket head) {
		Bucket b = head;
		int n = 0;
		while (b != null) {
			if (b.key() == null) {
				break;
			}
			n++;
			b = b.next();
		}
		return n;
	}

	public static Bucket at(Bucket head, int pos) {
		Bucket b = head;
		Debug.assertTrue(0 <= pos);
		for (int i = 0; i < pos; i++) {
			b = b.mNext;
			Debug.assertTrue(b != null);
		}
		return b;
	}

	public static Bucket find(Bucket head, Key k) {
		Bucket b = head;
		while (b != null) {
			if (b.key().equals(k)) {
				return b;
			}
			b = b.next();
		}
		return null;
	}

	public static Bucket findLessThan(Bucket head, Key key) {
		Bucket b = head;
		while (b.key() != null) {
			if (key.compareTo(b.key()) < 0) {
				return b;
			}
			b = b.next();
		}
		return b;
	}

	public Bucket(Key k, Object e) {
		mKey = k;
		mEntry = e;
	}
		
	public String toString() {
		String s = (mKey != null) ? mKey.toString() : "null";
		s += ":";
		s += (mEntry != null) ? mEntry.hashCode() : 0;
		if (mNext != null) {
			s += " ";
			s += mNext.toString();
		}
		return s;
	}

	public void setKey(Key k) {
		mKey = k;
	}

	public Key key() {
		return mKey;
	}

	public void setEntry(Object e) {
		mEntry = e;
	}

	public Object entry() {
		return mEntry;
	}

	public void setPrev(Bucket b) {
		mPrev = b;
		if (b != null) {
			b.mNext = this;
		}
	}

	public Bucket prev() {
		return mPrev;
	}

	public void setNext(Bucket b) {
		mNext = b;
		if (b != null) {
			b.mPrev = this;
		}
	}

	public Bucket next() {
		return mNext;
	}

	public boolean hasNext() {
		return mNext != null;
	}

	public Node node() {
		return (Node)mEntry;
	}
}

