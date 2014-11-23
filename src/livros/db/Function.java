package livros.db;

import livros.vm.VM;
import java.util.List;

public class Function extends Expr
{
	String mFuncName;
	public Function(String fname) {
		mFuncName = fname;
	}

	public String name() {
		return mFuncName;
	}

	public void exec(VM vm, List args) throws Exception {
		throw new RuntimeException();
	}

	public Value exec(Value v) throws Exception {
		throw new RuntimeException();
	}

	public Value exec(Value l, Value r) throws Exception {
		throw new RuntimeException();
	}

	public boolean isUserFunction() {
		return false;
	}
}
