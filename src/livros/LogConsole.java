package livros;

public class LogConsole implements Console
{
	StringBuilder mBuilder = new StringBuilder();
	public void println(String msg) {
		mBuilder.append(msg+"\n");
		System.out.println(msg);
	}

	public String getLog() {
		return mBuilder.toString();
	}

	public void reset() {
		mBuilder = new StringBuilder();
	}

}
