package livros.db;

public interface Selector
{
 	boolean hasNext();
	Record next();
	void finish();
}
