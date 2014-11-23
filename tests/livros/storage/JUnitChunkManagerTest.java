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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

public class JUnitChunkManagerTest extends TestCase
{
	String TEST_DB = StorageManager.DB_FILE;
	String TEST_TABLE1 = "tab1";
	String TEST_TABLE2 = "tab2";

	public JUnitChunkManagerTest(String name) {
		super(name);
	}

	public void setUp() {
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

	public void testInit() {
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
			rfile.writeHeader(0, 5, 1, 0);
			rfile.reset();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				assertTrue(ret);

				assertEquals(306, cm.totalRecordCount());
				assertEquals(3, cm.chunkCount());
				assertEquals(0, cm.freeChunkCount());
				VirtualChunk vc1 = cm.firstChunk();
				VirtualChunk vc2 = cm.firstFreeChunk();
				assertTrue(vc1 != null);
				assertTrue(vc2 == null);
			}

			rfile.reset();
			rfile.writeHeader(0, 5, 1, 4);
			rfile.reset();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				assertTrue(ret);

				assertEquals(306, cm.totalRecordCount());
				assertEquals(3, cm.chunkCount());
				assertEquals(2, cm.freeChunkCount());
				VirtualChunk vc1 = cm.firstChunk();
				VirtualChunk vc2 = cm.firstFreeChunk();
				assertTrue(vc1 != null);
				assertTrue(vc2 != null);
				assertTrue(vc1.id() == 1);
				assertTrue(vc1.prev() == null);
				assertTrue(vc1.next() != null);
				assertTrue(vc1.next().id() == 2);
				assertTrue(vc1.next().prev() != null);
				assertTrue(vc1.next().prev().id() == 1);
				assertTrue(vc1.next().next() != null);
				assertTrue(vc1.next().next().id() == 3);
				assertTrue(vc1.next().next().prev() != null);
				assertTrue(vc1.next().next().prev().id() == 2);
				assertTrue(vc1.next().next().next() == null);
			}


		} catch (Exception ex) {
			assertTrue(false);
		}
	}


	public void testAllocateChunk() {
		try {
			RecordFile rfile = new RecordFile(mTable1);

			rfile.writeHeader(0, 0, 0, 0);
			/*
			int flags = ChunkHeader.STABLE;
			ChunkHeader ch1 = new ChunkHeader(1, 0, 2, 0, flags, 0);
			rfile.writeChunkHeader(ch1);
			*/
			//rfile.dump(0);

			rfile.reset();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				assertTrue(ret);
				assertTrue(cm.firstChunk() == null);
				assertTrue(cm.firstFreeChunk() == null);
				assertEquals(0, cm.totalRecordCount());
				assertEquals(0, cm.chunkCount());
				assertEquals(0, cm.freeChunkCount());
				NewerChunk nc1 = cm.allocateChunk();
				assertTrue(nc1 != null);
				NewerChunk nc2 = cm.allocateChunk();
				assertTrue(nc2 != null);
				NewerChunk nc3 = cm.allocateChunk();
				assertTrue(nc3 != null);

				assertEquals(null, cm.firstFreeChunk());
				assertEquals(3, cm.chunkCount());
				assertEquals(0, cm.freeChunkCount());
				cm.commit();
				assertEquals(nc1, cm.firstFreeChunk());
				assertEquals(0, cm.chunkCount());
				assertEquals(3, cm.freeChunkCount());

				assertTrue(rfile.readHeader());
				assertEquals(0, rfile.recordCount());
				assertEquals(3, rfile.chunkCount());
				assertEquals(0, rfile.headChunkId());
				assertEquals(1, rfile.freeHeadChunkId());
			}

			//rfile.dump(0);
			rfile.reset();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				assertTrue(ret);
				assertTrue(cm.firstChunk() == null);
				assertTrue(cm.firstFreeChunk() != null);
				assertEquals(0, cm.totalRecordCount());
				assertEquals(0, cm.chunkCount());
				assertEquals(3, cm.freeChunkCount());

				NewerChunk nc1 = cm.allocateChunk();
				assertTrue(nc1 != null);
				assertTrue(nc1.next() == null);
				assertTrue(nc1.prev() == null);
				assertEquals(1, cm.chunkCount());
				assertEquals(2, cm.freeChunkCount());

				Record r1 = mTable1.createRecord(1);
				r1.set("id", new IntegerValue(0x11));
				r1.set("name", new FixedCharValue(15, "Bebel Gilberto"));
				r1.set("job", new VarCharValue(20, "Singer"));

				int size = RecordConverter.recordSize(r1);
				nc1.insertRecord(r1, size);

				cm.commit();
				assertEquals(1, cm.totalRecordCount());
				assertEquals(1, cm.chunkCount());
				assertEquals(2, cm.freeChunkCount());

				assertTrue(rfile.readHeader());
				assertEquals(1, rfile.recordCount());
				assertEquals(3, rfile.chunkCount());
				assertEquals(3, rfile.headChunkId());
				assertEquals(1, rfile.freeHeadChunkId());
			}

			//rfile.dump(0);
			rfile.reset();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				assertTrue(ret);
				assertTrue(cm.firstChunk() != null);
				assertTrue(cm.firstFreeChunk() != null);
				assertEquals(1, cm.totalRecordCount());
				assertEquals(1, cm.chunkCount());
				assertEquals(2, cm.freeChunkCount());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}


	public void testModifyChunk() {
		try {
			RecordFile rfile = new RecordFile(mTable1);
			rfile.writeHeader(0, 0, 0, 0);
			//add two chunk and two records
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				NewerChunk nc1 = cm.allocateChunk();
				Record r1 = mTable1.createRecord(1);
				r1.set("id", new IntegerValue(0x11));
				r1.set("name", new FixedCharValue(15, "Towa Tei"));
				r1.set("job", new VarCharValue(20, "Composer"));

				int size = RecordConverter.recordSize(r1);
				nc1.insertRecord(r1, size);

				NewerChunk nc2 = cm.allocateChunk();
				Record r2 = mTable1.createRecord(2);
				r2.set("id", new IntegerValue(0x22));
				r2.set("name", new FixedCharValue(15, "Carl Craig"));
				r2.set("job", new VarCharValue(20, "Composer"));

				size = RecordConverter.recordSize(r2);
				nc2.insertRecord(r2, size);

				assertEquals(2, cm.totalRecordCount());
				cm.commit();
			}
			
			rfile.readHeader();
			//add record to one chunk
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();

				VirtualChunk vc = cm.findChunk(1);
				assertEquals(2, cm.chunkCount());
				assertEquals(0, cm.freeChunkCount());
				NewerChunk nc1 = cm.modifyChunk(vc);
				assertEquals(2, cm.chunkCount());
				assertEquals(0, cm.freeChunkCount());
				assertTrue(nc1 != null);
				assertTrue(nc1.id() == 3);
				assertTrue(nc1.state() == ChunkHeader.NEWER);
				assertTrue(nc1.ancestor() != null);
				assertTrue(nc1.ancestor().id() == 1);
				assertTrue(nc1.ancestor().state() == ChunkHeader.OLDER);
				assertTrue(nc1.ancestor().descendant() == nc1);

				Record r3 = mTable1.createRecord(3);
				r3.set("id", new IntegerValue(0x33));
				r3.set("name", new FixedCharValue(15, "Joris Voorn"));
				r3.set("job", new VarCharValue(20, "Composer"));

				int size = RecordConverter.recordSize(r3);
				nc1.insertRecord(r3, size);
				assertTrue(nc1.recordCount() == 2);
				assertTrue(nc1.ancestor().recordCount() == 1);
				cm.commit();
			}

			rfile.readHeader();
			//add record to another chunk
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();

				VirtualChunk vc = cm.findChunk(2);
				assertEquals(2, cm.chunkCount());
				assertEquals(1, cm.freeChunkCount());
				NewerChunk nc1 = cm.modifyChunk(vc);
				assertEquals(2, cm.chunkCount());
				assertEquals(1, cm.freeChunkCount());
				assertTrue(nc1 != null);
				assertTrue(nc1.id() == 1);
				assertTrue(nc1.state() == ChunkHeader.NEWER);
				assertTrue(nc1.ancestor() != null);
				assertTrue(nc1.ancestor().id() == 2);
				assertTrue(nc1.ancestor().state() == ChunkHeader.OLDER);
				assertTrue(nc1.ancestor().descendant() == nc1);

				Record r4 = mTable1.createRecord(4);
				r4.set("id", new IntegerValue(0x44));
				r4.set("name", new FixedCharValue(15, "Jeff Mills"));
				r4.set("job", new VarCharValue(20, "Composer"));

				int size = RecordConverter.recordSize(r4);
				nc1.insertRecord(r4, size);
				assertTrue(nc1.recordCount() == 2);
				assertTrue(nc1.ancestor().recordCount() == 1);
				cm.commit();
			}

			//rfile.dump(0);
			rfile.readHeader();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				
				assertEquals(4, cm.totalRecordCount());
				VirtualChunk vc = cm.firstChunk();
				assertEquals(ChunkHeader.STABLE, vc.state());
				assertEquals(2, vc.recordCount());
			}

			//rfile.dump(0);
			rfile.readHeader();
			//remove two record at one chunk
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();

				VirtualChunk vc = cm.findChunk(3);
				assertEquals(2, cm.chunkCount());
				assertEquals(1, cm.freeChunkCount());
				NewerChunk nc1 = cm.modifyChunk(vc);
				assertEquals(2, cm.chunkCount());
				assertEquals(1, cm.freeChunkCount());
				assertTrue(nc1 != null);
				assertTrue(nc1.id() == 2);
				assertTrue(nc1.state() == ChunkHeader.NEWER);
				assertTrue(nc1.ancestor() != null);
				assertTrue(nc1.ancestor().id() == 3);
				assertTrue(nc1.ancestor().state() == ChunkHeader.OLDER);
				assertTrue(nc1.ancestor().descendant() == nc1);

				assertTrue(nc1.recordCount() == 2);
				assertTrue(nc1.ancestor().recordCount() == 2);

				Record r1 = nc1.get(0);
				Record r2 = nc1.get(1);
				nc1.deleteRecord(r1, RecordConverter.recordSize(r1));
				nc1.deleteRecord(r2, RecordConverter.recordSize(r2));
				cm.commit();
			}

			rfile.readHeader();
			{
				ChunkManager cm = new ChunkManager(rfile);
				boolean ret = cm.init();
				
				assertEquals(2, cm.totalRecordCount());
				assertEquals(1, cm.chunkCount());
				assertEquals(2, cm.freeChunkCount());
				VirtualChunk vc = cm.firstChunk();
				assertEquals(ChunkHeader.STABLE, vc.state());
				assertEquals(2, vc.recordCount());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
}
