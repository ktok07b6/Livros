package livros.vm;

public class DELTABLE extends INST
{
	String mTable;
	public DELTABLE(String table) {
		mTable = table;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}

