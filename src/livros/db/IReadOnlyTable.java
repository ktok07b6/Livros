package livros.db;

public interface IReadOnlyTable
{
	String name();
	String dbName();
	String baseName();
	Field field(String fieldName);
	FieldList fieldList();
	int size();
	Selector selector(Expr expr);
	boolean isDerivedTable();
}
