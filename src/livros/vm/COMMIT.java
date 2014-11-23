package livros.vm;

public class COMMIT extends INST
{
	String mTable;
	public COMMIT() {
	}

	public COMMIT(String t) {
		mTable = t;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}

