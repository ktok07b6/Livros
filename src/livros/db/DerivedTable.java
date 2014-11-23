package livros.db;

import livros.Debug;
import livros.Log;
import livros.Livros;
import livros.storage.IndexFile;
import livros.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class DerivedTable implements IReadOnlyTable
{
	private static final int BUFFER_SIZE = 10;

	String mName;
	String mDbName;
	String mBaseName;
	FieldList mFields;
	IndexFile mIndexFile;
	int mIndexCount;
	List mIndexBuffer = new ArrayList();

	public DerivedTable(String dbName, String name, String baseName, FieldList fields) {
		mName = name;
		mDbName = dbName;
		mBaseName = baseName;
		mFields = new FieldList(fields);
		mIndexFile = StorageManager.instance().indexFile(name);
		mIndexFile.reset();
		mIndexCount = 0;
	}

	public void init() {
	}

	public void insertIndex(RecordIndex ri) {
		mIndexBuffer.add(ri);
		if (mIndexBuffer.size() > BUFFER_SIZE) {
			mIndexCount += mIndexBuffer.size();
			mIndexFile.writeIndices(mIndexBuffer);
			mIndexBuffer.clear();
		}
	}

	public void flush() {
		if (mIndexBuffer.size() > 0)  {
			mIndexCount += mIndexBuffer.size();
			mIndexFile.writeIndices(mIndexBuffer);
			mIndexFile.writeHeader(mIndexCount);
			//mIndexFile.dump(0);
			mIndexBuffer.clear();
		}
		mIndexFile.close();
	}

	/* IReadOnlyTable interface */
	public String name() {
		return mName;
	}

	/* IReadOnlyTable interface */
	public String dbName() {
		return mDbName;
	}

	/* IReadOnlyTable interface */
	public String baseName() {
		return mBaseName;
	}

	/* IReadOnlyTable interface */
	public Field field(String fieldName) {
		return mFields.get(fieldName);
	}

	/* IReadOnlyTable interface */
	public FieldList fieldList() {
		return mFields;
	}

	/* IReadOnlyTable interface */
	public int size() {
		return mIndexFile.recordCount();
	}

	/* IReadOnlyTable interface */
	public Selector selector(Expr expr) {
		return SelectorFactory.create(this, expr);
	}

	/* IReadOnlyTable interface */
	public boolean isDerivedTable() {
		return true;
	}
}
