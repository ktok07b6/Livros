package livros.storage;

import livros.Debug;
import livros.Log;
import livros.db.Constraint;
import livros.db.DataBase;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.FixedCharValue;
import livros.db.VarCharValue;
import livros.db.IntegerValue;
import livros.db.Record;
import livros.db.RefConst;
import livros.db.Table;
import livros.db.TextValue;
import livros.db.Type;
import livros.db.Value;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class JUnitVirtualChunkTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitVirtualChunkTest(String name) {
		super(name);
		make();
	}

	DataBase mDataBase;
	Table mTable1;
	Table mTable2;

	private void make() {
		mTable1 = new Table(TEST_DB, TEST_TABLE1);
		mTable2 = new Table(TEST_DB, TEST_TABLE2);
		mDataBase = new DataBase(TEST_DB);
		mDataBase.addTable(mTable1);
		mDataBase.addTable(mTable2);

		try {
			//table 1
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				Field idField = new Field("id", Type.integerType, consts);
				mTable1.addField(idField);
			}
			{
				List consts = new ArrayList();
				consts.add(Constraint.notNullConst);
				consts.add(Constraint.uniqueConst);
				Field nameField = new Field("name", Type.fixedChar(15), consts);
				nameField.setDefaultValue(new FixedCharValue(15, "foo"));
				mTable1.addField(nameField);
			}
			{
				Field jobField = new Field("job", Type.varChar(20));
				jobField.setDefaultValue(new VarCharValue(20, "bar"));
				mTable1.addField(jobField);
			}

			//table 2
			{
				List consts = new ArrayList();
				consts.add(Constraint.primaryConst);
				consts.add(new RefConst(TEST_TABLE1, "id"));
				Field idField = new Field("id", Type.integerType, consts);
				mTable2.addField(idField);
			}

		} catch (Exception ex) {
		}
	}


	public void testNewerInsert() {
		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "foo"));
		r1.set("job", new VarCharValue(20, "bar"));

		assertEquals(-1, r1.index());
		assertEquals(-1, r1.chunkid());

		int size1 = RecordConverter.recordSize(r1);
		NewerChunk nc = new NewerChunk(1);
		assertEquals(NewerChunk.SIZE_MAX, nc.remainSize());
		assertEquals(0, nc.recordCount());

		nc.insertRecord(r1, size1);
		assertEquals(NewerChunk.SIZE_MAX-size1, nc.remainSize());
		assertEquals(1, nc.recordCount());
		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());

		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "foo"));
		r2.set("job", new VarCharValue(20, "bar"));
		int size2 = RecordConverter.recordSize(r2);

		nc.insertRecord(r2, size2);
		assertEquals(NewerChunk.SIZE_MAX-(size1+size2), nc.remainSize());
		assertEquals(2, nc.recordCount());
		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());
		assertEquals(1, r2.index());
		assertEquals(1, r2.chunkid());

	} 

	public void testNewerDelete() {
		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "foo"));
		r1.set("job", new VarCharValue(20, "bar"));
		int size1 = RecordConverter.recordSize(r1);

		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "foo"));
		r2.set("job", new VarCharValue(20, "bar"));
		int size2 = RecordConverter.recordSize(r2);

		NewerChunk nc = new NewerChunk(1);
		nc.insertRecord(r1, size1);
		nc.insertRecord(r2, size2);

		assertEquals(NewerChunk.SIZE_MAX-(size1+size2), nc.remainSize());
		assertEquals(2, nc.recordCount());
		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());
		assertEquals(1, r2.index());
		assertEquals(1, r2.chunkid());

		nc.deleteRecord(r1, size1);
		assertEquals(1, nc.recordCount());
		assertEquals(-1, r1.index());
		assertEquals(-1, r1.chunkid());
		assertEquals(0, r2.index());
		assertEquals(1, r2.chunkid());

		nc.deleteRecord(r2, size2);
		assertEquals(0, nc.recordCount());
		assertEquals(-1, r1.index());
		assertEquals(-1, r1.chunkid());
		assertEquals(-1, r2.index());
		assertEquals(-1, r2.chunkid());
	}

	public void testNewerUpdate() {
		Record r1 = mTable1.createRecord(1);
		r1.set("id", new IntegerValue(0x1));
		r1.set("name", new FixedCharValue(15, "foo"));
		r1.set("job", new VarCharValue(20, "bar"));
		int size1 = RecordConverter.recordSize(r1);

		Record r2 = mTable1.createRecord(2);
		r2.set("id", new IntegerValue(0x2));
		r2.set("name", new FixedCharValue(15, "foo"));
		r2.set("job", new VarCharValue(20, "bar"));
		int size2 = RecordConverter.recordSize(r2);

		NewerChunk nc = new NewerChunk(1);
		nc.insertRecord(r1, size1);
		nc.insertRecord(r2, size2);

		assertEquals(NewerChunk.SIZE_MAX-(size1+size2), nc.remainSize());
		assertEquals(2, nc.recordCount());
		assertEquals(0, r1.index());
		assertEquals(1, r1.chunkid());
		assertEquals(1, r2.index());
		assertEquals(1, r2.chunkid());

		Record r1_1 = mTable1.createRecord(1);
		r1_1.set("id", new IntegerValue(0x1));
		r1_1.set("name", new FixedCharValue(15, "foo"));
		r1_1.set("job", new VarCharValue(20, "baaaar"));
		r1_1.setPosition(r1.index(), r1.chunkid());
		int size1_1 = RecordConverter.recordSize(r1_1);

		nc.updateRecord(r1_1, size1 - size1_1);
		assertEquals(2, nc.recordCount());
		assertEquals(0, r1_1.index());
		assertEquals(1, r1_1.chunkid());
		assertEquals(1, r2.index());
		assertEquals(1, r2.chunkid());
		assertEquals(r1_1.toString(), nc.get(0).toString());
		assertEquals("baaaar", nc.get(0).get("job").asText().textValue());
	}
}
