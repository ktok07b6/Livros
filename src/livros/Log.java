package livros;

public class Log
{
	private static final boolean LOG_DEBUG = false;
	private static final boolean LOG_ERROR = true;
	private static final boolean LOG_VERBOSE = false;

	public static void d(String info) {
		if (LOG_DEBUG) {
			System.out.println("DBG: " + info);
		}
	}

	public static void e(String info) {
		if (LOG_ERROR) {
			System.out.println("ERROR: " + info);
		}
	}

	public static void v(String info) {
		if (LOG_VERBOSE) {
			System.out.println("VERBOSE: " + info);
		}
	}

}
