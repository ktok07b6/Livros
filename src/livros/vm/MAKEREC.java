package livros.vm;

public class MAKEREC extends INST
{
	int mFieldCount;
	public MAKEREC(int fieldCount) {
		super();
		mFieldCount = fieldCount;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}

