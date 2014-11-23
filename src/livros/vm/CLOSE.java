package livros.vm;

public class CLOSE extends INST
{
	String mDbName;
	public CLOSE(String dbName) {
		super();
		mDbName = dbName;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
