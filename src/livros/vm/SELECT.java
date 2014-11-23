package livros.vm;

public class SELECT extends INST
{
	String mDst;
	String mSrc;

	public SELECT(String dst, String src) {
		super();
		mDst = dst;
		mSrc = src;
	}

	public void accept(InstructionVisitor v) throws Exception {
		v.visit(this);
	}
}
