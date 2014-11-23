package livros.vm;

public class PROJECTION extends INST
{
	String mDst;
	String mSrc;
	int mArgc;
	public PROJECTION(String dst, String src, int argc) {
		super();
		mDst = dst;
		mSrc = src;
		mArgc = argc;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
