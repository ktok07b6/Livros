package livros.vm;

public class UNION extends INST
{
	String mDst;
	String mSrc1;
	String mSrc2;
	public UNION(String dst, String src1, String src2) {
		super();
		mDst = dst;
		mSrc1 = src1;
		mSrc2 = src2;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
