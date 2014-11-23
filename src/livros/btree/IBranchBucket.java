package livros.btree;

public interface IBranchBucket 
{
	Node next(Node node);
	Node prev(Node node);
	void changeParent(Branch parent);
	Key middleKey();
	IBranchBucket split();
	void insertSeparator(Key separator, Object lhs, Object rhs);
	void merge(Key mid, IBranchBucket other);
	Key replaceKey(Object entry, Key key);
	Key removeKey(Object finding);
	Object findNodeLessEqual(Key key);
	Key keyAt(int index);
	Object entryAt(int index);
	int keyCount();
	boolean isEmpty();
	boolean verify();
}
