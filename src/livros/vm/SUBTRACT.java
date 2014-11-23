package livros.vm;

public class SUBTRACT extends INST
{
	String mDst;
	String mSrc;
	public SUBTRACT(String dst, String src) {
		super();
		mDst = dst;
		mSrc = src;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
