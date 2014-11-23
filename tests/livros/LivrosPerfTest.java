package livros;

import livros.compiler.AST;
import livros.storage.StorageManager;
import java.io.File;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MemoryPoolMXBean;

public class LivrosPerfTest
{
	static final String TABLE1 = "perftest1";

	static void delete(String name) {
		File file = new File(StorageManager.DB_DIR+name);
		if (file.exists()) {
			deleteAny(file);
		}
	}
		
	static void deleteAny(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteAny(files[i]);
			}
			file.delete();
		}
	}

	static Livros livros = new Livros();	

	static final String CREATE_TABLE_INSERT = 
		"create table inserttest(i1 int, i2 int, t1 char(64), t2 char(64))";
	static final String CREATE_TABLE_INSERT_PRIMARY = 
		"create table inserttest(i1 int primary key, i2 int, t1 char(64), t2 char(64))";

	static int TEST_COUNT = 10000;

	public static void exec(String sql) {
		List tokens = livros.scan(sql);
		AST ast = livros.parse(tokens);
		List insts = livros.translate(ast);
		livros.exec(insts);
	}

	public static void testLivros(String schema, String type) {
		livros.open();
		if (type.equals("insert")) {
			testLivrosInsert(schema);
		} else if (type.equals("select")) {
			testLivrosSelect(schema);
		} else if (type.equals("delete")) {
			testLivrosDelete(schema);
		}

		livros.close();
	}

	public static void testLivrosInsert(String schema) {
		System.out.print("test insert Livros:");

		exec(schema);
		long startTime = System.nanoTime();
		StorageManager.instance().diagResetReadRecordBytes();
		for (int i = 0; i < TEST_COUNT; i++) {
			exec("insert into inserttest values(" + i + "," + i + "," + 
				 "'"+String.valueOf(i)+"'" + "," + 
				 "'"+String.valueOf(i)+"'" + ")");
			//if ((i % 1000) == 9999) exec("commit");
		}
		exec("commit");
		long endTime = System.nanoTime();
		System.out.print(" " + ((endTime-startTime)/1000000) + "ms\t");
		System.out.print(" / " + (StorageManager.instance().diagReadRecordBytes())+ " B(access)");
		//exec("select * from inserttest");
	}

	public static void testLivrosSelect(String schema) {
		System.out.print("test select Livros:");
		
		exec(schema);
		for (int i = 0; i < TEST_COUNT; i++) {
			exec("insert into inserttest values(" + i + "," + i + "," + 
				 "'"+String.valueOf(i)+"'" + "," + 
				 "'"+String.valueOf(i)+"'" + ")");
		}
		exec("commit");
		
		/*
		exec("create table selecttest"+TEST_COUNT+"(i1 int, i2 int, t1 char(64), t2 char(64))");
		for (int i = 0; i < TEST_COUNT; i++) {
			exec("insert into selecttest"+TEST_COUNT+" values(" + i + "," + i + "," + 
				 "'"+String.valueOf(i)+"'" + "," + 
				 "'"+String.valueOf(i)+"'" + ")");
			if ((i % 1000) == 9999) exec("commit");
		}
		
		*/
		exec("commit");
		StorageManager.instance().diagResetReadRecordBytes();
		long startTime = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++) {
			exec("select * from inserttest where i1 = 0");
		}
		long endTime = System.nanoTime();
		System.out.print(" " + ((endTime-startTime)/1000000) + "ms\t");
		System.out.print(" / " + (StorageManager.instance().diagReadRecordBytes())+ " B(access)");
		
		//exec("select * from inserttest");
		
	}

	public static void testLivrosDelete(String schema) {
		System.out.print("test delete Livros:");

		exec(schema);
		for (int i = 0; i < TEST_COUNT; i++) {
			exec("insert into inserttest values(" + i + "," + i + "," + 
				 "'"+String.valueOf(i)+"'" + "," + 
				 "'"+String.valueOf(i)+"'" + ")");
		}
		exec("commit");
		//exec("select * from inserttest");
		StorageManager.instance().diagResetReadRecordBytes();
		long startTime = System.nanoTime();
		for (int i = 0; i < TEST_COUNT; i++) {
			exec("delete from inserttest where i1 = " + i);
		}

		exec("commit");
		//exec("select * from inserttest");
		long endTime = System.nanoTime();
		System.out.print(" " + ((endTime-startTime)/1000000) + "ms\t");
		System.out.print(" / " + (StorageManager.instance().diagReadRecordBytes())+ " B(access)");

	}

	public static void testDerby(String schema, String type) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			Connection conn = DriverManager.getConnection("jdbc:derby:/home/kataoka/works/livros/derby_sample1;create=true");
			if (type.equals("insert")) {
				System.out.print("test insert Derby:");
				testJDBCInsert(conn, schema);
			} else if (type.equals("select")) {
				System.out.print("test select Derby:");
				testJDBCSelect(conn, schema);
			}
			conn.commit();
			conn.close();

			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (Exception e) {
			//System.err.println(e.toString());
		}
	}

	public static void testH2(String schema, String type) {
		try {
			Class.forName("org.h2.Driver");

			Connection conn = DriverManager.getConnection("jdbc:h2:/home/kataoka/works/livros/h2_sample1;create=true");
			if (type.equals("insert")) {
				System.out.print("test insert H2:");
				testJDBCInsert(conn, schema);
			} else if (type.equals("select")) {
				System.out.print("test select H2:");
				testJDBCSelect(conn, schema);
			}
			conn.close();

		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public static void testJDBCInsert(Connection conn, String schema) {
		try {
			conn.setAutoCommit(false);

			Statement statement = conn.createStatement();
			String sql = schema;
			statement.execute(sql);
			conn.commit();

			//INSERT
			long startTime = System.nanoTime();
			for (int i = 0; i < TEST_COUNT; i++) {
				sql = "insert into inserttest values(" + i + "," + i + "," + 
					"'"+String.valueOf(i)+"'" + "," + 
					"'"+String.valueOf(i)+"'" + ")";
				statement.execute(sql);
			}
			conn.commit();
			long endTime = System.nanoTime();
			System.out.print(" " + ((endTime-startTime)/1000000) + "ms\t");

		} catch(SQLException se) {
			for (SQLException e = se; e != null; e = e.getNextException()) {
				System.err.println(e.getSQLState() + e.getMessage());
			}
		}
	}

	public static void testJDBCSelect(Connection conn, String schema) {
		try {
			conn.setAutoCommit(false);

			Statement statement = conn.createStatement();
			String sql = schema;
			statement.execute(sql);
			for (int i = 0; i < TEST_COUNT; i++) {
				sql = "insert into inserttest values(" + i + "," + i + "," + 
					"'"+String.valueOf(i)+"'" + "," + 
					"'"+String.valueOf(i)+"'" + ")";
				statement.execute(sql);
			}
			conn.commit();

			{
				long startTime = System.nanoTime();
				for (int i = 0; i < TEST_COUNT; i++) {
					sql = "select * from inserttest where i1 = "+i;
					ResultSet result = statement.executeQuery(sql);
					if (true) {
						while (result.next()) {
							int i1 = result.getInt(1);
							assert i1 == i;
							//int i2 = result.getInt(2);
							//String t1 = result.getString(3);
							//String t2 = result.getString(4);
							//System.out.println(i1 + " " + i2 + " " + t1 + " " + t2);
						}
					}
				}
				long endTime = System.nanoTime();
				System.out.print(" " + ((endTime-startTime)/1000000) + "ms\t");
			}
		} catch(SQLException se) {
			for (SQLException e = se; e != null; e = e.getNextException()) {
				System.err.println(e.getSQLState() + e.getMessage());
			}
		}
	}

	public static void printMemoryInfo() {
		long free = Runtime.getRuntime().freeMemory() / (1024*1024);
		long total = Runtime.getRuntime().totalMemory() / (1024*1024);
		long max = Runtime.getRuntime().maxMemory() / (1024*1024);
		long used = total - free;
		double ratio = (used * 100 / (double)total);
		System.out.println("/ " + total + "MB / " + free + "MB / " + used + "MB / " + max + "MB");

		// Place this code just before the end of the program
		/*
		try {
			String memoryUsage = new String();
			List pools = ManagementFactory.getMemoryPoolMXBeans();
			for (int i = 0; i < pools.size(); i++) {
				MemoryPoolMXBean pool = (MemoryPoolMXBean)pools.get(i);
				MemoryUsage peak = pool.getPeakUsage();
				memoryUsage += String.format("Peak %s memory used: %,d%n", pool.getName(),peak.getUsed());
				memoryUsage += String.format("Peak %s memory reserved: %,d%n", pool.getName(), peak.getCommitted());
			}

			// we print the result in the console
			System.out.println(memoryUsage);
 
		} catch (Throwable t) {
			System.err.println("Exception in agent: " + t);
		}
		*/
	}

	public static void cleanup() {
		delete("derby_sample1");
		delete("h2_sample1");
		delete("h2_sample1.mv.db");
		delete("h2_sample1.trace.db");
		delete("inserttest");
	}

	public static void main(String[] args) {
		File file = new File("/sdcard");
		if (file.exists()) {
			StorageManager.DB_DIR="/sdcard/tmp/";
			StorageManager.TMP_DIR="/sdcard/tmp/";
		}
		cleanup();
		Livros.NO_SHOW = true;

		if (args.length < 4) {
			System.out.println("usage : [livros|derby|h2] [insert|select] [none|primary] [row count]");
			return;
		}
		final String engine = args[0];
		final String type = args[1];
		final String insert = args[2];
		TEST_COUNT = Integer.parseInt(args[3]);

		if (false/*type.equals("select")*/) {
			StorageManager.DB_FILE = "livros_test_select.db";
		} else {
			StorageManager.DB_FILE = "livros_test.db";
			delete(StorageManager.DB_FILE);
		}
		String schema = "";
		if (insert.equals("none")) {
			schema = CREATE_TABLE_INSERT;
		} else if (insert.equals("primary")) {
			schema = CREATE_TABLE_INSERT_PRIMARY;
		}

		if (engine.equals("livros")) {
			testLivros(schema, type);
		} else if (engine.equals("derby")) {
			testDerby(schema, type);
		} else if (engine.equals("h2")) {
			testH2(schema, type);
		}

		printMemoryInfo();
	}
}
