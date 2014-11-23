package livros.db;

import livros.Debug;
import livros.Log;
import livros.vm.VM;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFunction extends Function
{
	private static Map mFuncMap = new HashMap();

	int mArgc;
	List mBody; // INST
	List mArgs; // Value
	public UserFunction(String fname) {
		super(fname);
	}

	public void setArgc(int argc) {
		mArgc = argc;
	}

	public int argc() {
		return mArgc;
	}

	public void setBody(List body) {
		mBody = body;
	}

	public void setArgs(List args) {
		mArgs = args;
	}

	public Value arg(int n) {
		Debug.assertTrue(0 <=n && n < mArgs.size());
		return (Value)mArgs.get(n);
	}

	public void exec(VM vm, List args) throws Exception {
		setArgs(args);
		vm.exec(mBody);
	}

	public boolean isUserFunction() {
		return true;
	}

	public static void register(UserFunction f) {
		Log.d("register " + f.name());
		mFuncMap.put(f.name(), f);
	}

	public static UserFunction get(String name) {
		Log.d("get " + name);
		return (UserFunction)mFuncMap.get(name);
	}
}
