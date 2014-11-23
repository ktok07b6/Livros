package livros.vm;

public class TRACE extends INST
{
	public TRACE() {
		super();
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
