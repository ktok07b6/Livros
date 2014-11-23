package livros.btree;

import livros.Log;

public class BTreePrinter {
	StringBuilder mBuilder;
	String mIndent;

	public BTreePrinter() {
		mBuilder = new StringBuilder();
		mIndent = "";
	}

	public String toString(Node n) {
		if (n.isLeaf()) {
			printLeaf((Leaf)n);
		} else {
			printBranch((Branch)n);
		}
		return mBuilder.toString();
	}

	public void printBranch(Branch br) {
		if (br == null) {
			mBuilder.append("#null");
			return;
		}
		mBuilder.append("+");
		for (int i = 0; i < mIndent.length(); i++) {
			mBuilder.append("-");
		}
		mBuilder.append("Branch:" + br.hashCode() + "\n");
		mBuilder.append(mIndent + " *parent:" + (br.mParent!=null?br.mParent.hashCode():0) + "\n");
		//Log.d(mKeyCount);
		mBuilder.append(mIndent + " *keys ... ");

		for (int i = 0; i < br.mBuckets.keyCount(); i++) {
			mBuilder.append(br.mBuckets.keyAt(i));
			mBuilder.append(" ");
		}
		mBuilder.append("\n");

		//assert 0 < br.mKeyCount;
		//assert (br.mBuckets.mBucket != null);
		mBuilder.append(mIndent + " " + br.mBuckets.toString());
		/*
		b = br.mBuckets.mBucket;
		while (b != null) {
			mBuilder.append(mIndent + " *child:"+ (b.entry() != null ? b.entry().hashCode() : 0) + "\n");
			b = b.next();
		}
		*/
		mBuilder.append("\n");

		for (int i = 0; i <= br.mBuckets.keyCount(); i++) {
			Node node = (Node)br.mBuckets.entryAt(i);
			String oldIndent = new String(mIndent);
			mIndent += " ";
			if (node.isLeaf()) {
				printLeaf((Leaf)node);
			} else {
				printBranch((Branch)node);
			}
			mIndent = oldIndent;
		}
	}

	public void printLeaf(Leaf lf) {
		if (lf == null) {
			mBuilder.append("#null");
			return;
		}
		mBuilder.append("+");
		for (int i = 0; i < mIndent.length(); i++) {
			mBuilder.append("-");
		}
		mBuilder.append("Leaf:" + lf.hashCode() + "\n");
		mBuilder.append(mIndent + " *parent:" + (lf.mParent!=null?lf.mParent.hashCode():0) + "\n");
		mBuilder.append(mIndent + " *keys ... ");

		mBuilder.append(lf.mBuckets.toString());
		mBuilder.append("\n");
		mBuilder.append("\n");
	}

}
