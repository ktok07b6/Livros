package livros.btree;

import java.util.List;

public interface ILeafBucket 
{
	boolean insert(Key key, Object entry);
	boolean remove(Key key);
	Object find(Key key);
	List findLess(Key key);
	List findGreater(Key key);
	List values();
	ILeafBucket split();
	void merge(ILeafBucket other);
	Key keyAt(int index);
	Object entryAt(int index);
	int keyCount();
	boolean isEmpty();
}