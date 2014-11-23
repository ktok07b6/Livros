package livros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SortedList
{
	private List mList = new ArrayList();

	public SortedList() {
	}

	public void add(Object o) {
		int i = lowerBound(mList, o);
		//Log.d("SortedList#add " + i + "  " + o);
		Debug.assertTrue(0 <= i);
		mList.add(i, o);
	}

	public Object remove(Comparable c) {
		int index = Collections.binarySearch(mList, c);
		if (0 <= index) {
			return mList.remove(index);
		} else {
			return null;
		}
	}

	public Object get(int i) {
		return mList.get(i);
	}

	public int size() {
		return mList.size();
	}

	private int lowerBound(List list, Object o) {
		final int len = list.size();
		int lo = 0, hi = len-1;
		while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			Comparable c = (Comparable)list.get(mid);
			int cmp = c.compareTo(o);
			if (cmp < 0) {
				lo = mid + 1;
				if (hi < lo) {
					return mid + 1;
				}
			} else if (cmp > 0) {
				hi = mid - 1;
				if (hi < lo) {
					return mid;
				}
			} else {
				return -1;
			}
		}
		return 0;
	}

	public Iterator iterator() {
		return mList.iterator();
	}
}
