package livros.vm;

public class OPEN extends INST
{
	String mDbName;
	int mAccessFlag;
	public OPEN(String dbName) {
		super();
		mDbName = dbName;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
