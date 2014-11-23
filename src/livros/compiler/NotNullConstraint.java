package livros.compiler;

public class NotNullConstraint extends Constraint
{
	public NotNullConstraint() {
		super();
	}

	public String toString() {
		return "NOT NULL";
	}

	public Object accept(ASTVisitor v) throws Exception {
		return v.visit(this);
	}
}
