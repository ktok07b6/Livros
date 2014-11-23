package livros.vm;

public class NEWTABLE extends INST
{
	String mName;
	public NEWTABLE(String name) {
		super();
		mName = name;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
