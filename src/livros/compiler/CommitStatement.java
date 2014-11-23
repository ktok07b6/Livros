package livros.compiler;

public class CommitStatement extends Statement
{
	public CommitStatement() {
		super();
	}

	public String toString() {
		return "COMMIT";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
