package livros.vm;

public class SHOW extends INST
{
	String mTable;

	public SHOW(String table) {
		mTable = table;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}

