package livros;

import livros.compiler.AST;
import livros.compiler.ASTPrinter;
import livros.compiler.Parser;
import livros.compiler.Scanner;
import livros.compiler.Token;
import livros.compiler.Translator;
import livros.db.DataBase;
import livros.storage.StorageManager;
import livros.vm.VM;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Livros {
	public static final boolean DEBUG = false;
	public static final boolean PROFILE = false;
	public static boolean AUTO_COMMIT = true;
	public static boolean NO_SHOW = false;

	public static void main(String argv[]) {
		File file = new File("/sdcard");
		if (file.exists()) {
			StorageManager.DB_DIR="/sdcard/tmp/";
			StorageManager.TMP_DIR="/sdcard/tmp/";
		}

		Livros livros = new Livros();
		livros.open();
		livros.mainLoop();
		livros.close();
	}

	private static Console sConsole = new SystemConsole();

	public static Console console() {
		return sConsole;
	}

	public static void setConsole(Console c) {
		sConsole = c;
	}

	private static final int ST_READ = 0;
	private static final int ST_SCAN = 1;
	private static final int ST_DETECT_STM = 2;
	private static final int ST_PARSE = 3;
	private static final int ST_EXEC  = 4;
	private static final int ST_END   = 5;

	DataBase mDatabase;
	public void open() {
		mDatabase = DataBase.open("");
	}

	public void cleanup() {
		mDatabase.cleanup();
	}

	public void close() {
		mDatabase.close();
	}

	public void mainLoop() {
		int state = ST_READ;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			String line = null;
			List tokens = null;
			int tokenIndex = 0;
			List execTokens = new ArrayList();
			AST ast = null;
			long startTime = 0;

			while (state != ST_END) {
				//Log.d("state = " + state);
				switch (state) {
				case ST_READ:
					System.out.print(">");
					line = reader.readLine();
					if (line == null || line.length() < 1) {
						state = ST_END;
					} else {
						state = ST_SCAN;
					}
					break;

				case ST_SCAN:
					tokenIndex = 0;
					tokens = scan(line);
					state = ST_DETECT_STM;
					break;

				case ST_DETECT_STM:
					boolean hasEndStm = false;
					for (; tokenIndex < tokens.size(); tokenIndex++) {
						Token t = (Token)tokens.get(tokenIndex);
						execTokens.add(t);
						if (t.is(Token.SEMICOLON)) {
							tokenIndex++;
							hasEndStm = true;
							break;
						}
					}
					if (hasEndStm) {
						state = ST_PARSE;
					} else {	
						state = ST_READ;
					}
					break;

				case ST_PARSE:
					startTime = System.currentTimeMillis();
					ast = parse(execTokens);
					execTokens.clear();
					if (ast != null) {
						state = ST_EXEC;
					} else {
						state = ST_END;
					}
					break;

				case ST_EXEC:
					List insts = translate(ast);
					if (insts == null) {
						break;
					}
					StorageManager.instance().diagResetReadRecordBytes();
					exec(insts);
					//
					long endTime = System.currentTimeMillis();
					System.out.println("done: " +ast.toString());
					System.out.println("time: " + (endTime - startTime) + "ms");
					//System.out.println("disc read record access: " + StorageManager.instance().diagReadRecordBytes()+ "bytes");
					if (tokens.size() > tokenIndex) {
						state = ST_DETECT_STM;
					} else {
						state = ST_READ;
					}
					break;
				case ST_END:
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List scan(String sql) {
		Scanner scanner = new Scanner(sql);
		List token = scanner.scan();
		return token;
	}

	public AST parse(List tokens) {
		if (DEBUG) {
			for (int i = 0; i < tokens.size(); i++) {
				Token t = (Token)tokens.get(i);
				Log.d("token : " + t.toString());
			}
		}

		Parser parser = new Parser(tokens);
		AST ast = parser.parse();
		if (ast == null) {
			return null;
		}

		if (DEBUG) {
			try {
				ASTPrinter printer = new ASTPrinter();
				ast.accept(printer);
				Log.d(printer.getResult());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return ast;
	}

	public List translate(AST ast) {
		Translator translator = new Translator(AUTO_COMMIT);
		Translator.DEBUG = DEBUG;

		try {	
			ast.accept(translator);
			//translator.dump();
		} catch (Exception ex) {
			ex.printStackTrace();	
		}
		return translator.instructions();
	}

	public void exec(List insts) {
		try {	
			VM vm = new VM();
			VM.DEBUG = DEBUG;
			vm.exec(insts);
			vm.destroy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

