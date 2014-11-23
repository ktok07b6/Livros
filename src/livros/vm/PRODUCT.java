package livros.vm;

public class PRODUCT extends INST
{
	int mArgc;
	public PRODUCT(int argc) {
		super();
		mArgc = argc;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
