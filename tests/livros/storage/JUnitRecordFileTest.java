package livros.storage;

import livros.Console;
import livros.Debug;
import livros.LogConsole;
import livros.Livros;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitRecordFileTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitRecordFileTest(String name) {
		super(name);
	}

	protected void setUp() {
		File file = new File(StorageManager.DB_DIR+TEST_TABLE1);
		if (file.exists()) {
			file.delete();
		}
		file = new File(StorageManager.DB_DIR+TEST_TABLE2);
		if (file.exists()) {
			file.delete();
		}
		file = new File(StorageManager.DB_DIR+TEST_DB);
		if (file.exists()) {
			file.delete();
		}

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

	public void testHeader() {
		try {
			RecordFile rfile = new RecordFile(mTable1);

			rfile.writeHeader(2, 1, 1, 0);
			//rfile.dump(0);

			rfile.reset();

			boolean ret = rfile.readHeader();
			assertTrue(ret);

			assertEquals(2, rfile.recordCount());
			assertEquals(1, rfile.chunkCount());
			assertEquals(1, rfile.headChunkId());
			assertEquals(0, rfile.freeHeadChunkId());
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testChunk() {
		try {
			int flags = ChunkHeader.STABLE;
			ChunkHeader ch1 = new ChunkHeader(1, 0, 2, 0, 0, flags, 101);
			ChunkHeader ch2 = new ChunkHeader(2, 1, 3, 0, 0, flags, 102);
			ChunkHeader ch3 = new ChunkHeader(3, 2, 0, 0, 0, flags, 103);
			flags = ChunkHeader.FREE;
			ChunkHeader fch1 = new ChunkHeader(4, 0, 5, 0, 0, flags, 104);
			ChunkHeader fch2 = new ChunkHeader(5, 4, 0, 0, 0, flags, 105);

			RecordFile rfile = new RecordFile(mTable1);

			rfile.writeChunkHeader(ch1);
			rfile.writeChunkHeader(ch2);
			rfile.writeChunkHeader(ch3);
			rfile.writeChunkHeader(fch1);
			rfile.writeChunkHeader(fch2);
			//rfile.dump(0);
			rfile.reset();

			assertTrue(rfile.readChunkHeader(0) == null);
			ChunkHeader rch1 = rfile.readChunkHeader(1);
			ChunkHeader rch2 = rfile.readChunkHeader(2);
			ChunkHeader rch3 = rfile.readChunkHeader(3);
			ChunkHeader rfch1 = rfile.readChunkHeader(4);
			ChunkHeader rfch2 = rfile.readChunkHeader(5);
			assertTrue(rch1 != null);
			assertTrue(rch2 != null);
			assertTrue(rch3 != null);
			assertTrue(rfch1 != null);
			assertTrue(rfch2 != null);
			assertEquals(1, rch1.id());
			assertEquals(0, rch1.prevId());
			assertEquals(2, rch1.nextId());
			assertEquals(0, rch1.ancestorId());
			assertEquals(0, rch1.descendantId());
			assertEquals(ChunkHeader.STABLE, rch1.state());
			assertEquals(101, rch1.recordCount());

			assertEquals(2, rch2.id());
			assertEquals(1, rch2.prevId());
			assertEquals(3, rch2.nextId());
			assertEquals(0, rch2.ancestorId());
			assertEquals(0, rch2.descendantId());
			assertEquals(ChunkHeader.STABLE, rch2.state());
			assertEquals(102, rch2.recordCount());

			assertEquals(3, rch3.id());
			assertEquals(2, rch3.prevId());
			assertEquals(0, rch3.nextId());
			assertEquals(0, rch3.ancestorId());
			assertEquals(0, rch3.descendantId());
			assertEquals(ChunkHeader.STABLE, rch3.state());
			assertEquals(103, rch3.recordCount());

			assertEquals(4, rfch1.id());
			assertEquals(0, rfch1.prevId());
			assertEquals(5, rfch1.nextId());
			assertEquals(0, rfch1.ancestorId());
			assertEquals(0, rfch1.descendantId());
			assertEquals(ChunkHeader.FREE, rfch1.state());
			assertEquals(104, rfch1.recordCount());

			assertEquals(5, rfch2.id());
			assertEquals(4, rfch2.prevId());
			assertEquals(0, rfch2.nextId());
			assertEquals(0, rfch2.ancestorId());
			assertEquals(0, rfch2.descendantId());
			assertEquals(ChunkHeader.FREE, rfch2.state());
			assertEquals(105, rfch2.recordCount());

		} catch (Exception ex) {
			assertTrue(false);
		}
	}

	public void testRecords() {
		try {
			RecordFile rfile = new RecordFile(mTable1);

			int flags = ChunkHeader.STABLE;
			ChunkHeader ch1 = new ChunkHeader(1, 0, 0, 0, 0, flags, 2);
			rfile.writeChunkHeader(ch1);

			Record r1 = mTable1.createRecord(1);
			r1.set("id", new IntegerValue(0));
			r1.set("name", new FixedCharValue(15, "Joao Gilberto"));
			r1.set("job", new VarCharValue(20, "Singer"));

			Record r2 = mTable1.createRecord(2);
			r2.set("id", new IntegerValue(1));
			r2.set("name", new FixedCharValue(15, "Tom Jobim"));
			r2.set("job", new VarCharValue(20, "Composer"));

			List records = new ArrayList();
			records.add(r1);
			records.add(r2);

			rfile.writeRecords(1, records);
			
			//rfile.dump(0);
			rfile.reset();
			
			records = new ArrayList();
			rfile.readRecords(1, 2, records);
			Record rr1 = (Record)records.get(0);
			Record rr2 = (Record)records.get(1);
			assertEquals(r1.toString(), rr1.toString());
			assertEquals(r2.toString(), rr2.toString());

		} catch (Exception ex) {
			assertTrue(false);
		}
	}
}
