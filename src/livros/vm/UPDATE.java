package livros.vm;

public class UPDATE extends INST
{
	String mSrc;
	String mDst;
	int mArgc;
	public UPDATE(String dst, String src, int argc) {
		super();
		mDst = dst;
		mSrc = src;
		mArgc = argc;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
