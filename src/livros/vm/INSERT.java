package livros.vm;

public class INSERT extends INST
{
	String mTable;
	public INSERT(String table) {
		mTable = table;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}

