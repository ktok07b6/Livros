package livros.vm;

public interface InstructionVisitor
{
	public void visit(CLOSE close) throws Exception;
	public void visit(COMMIT commit) throws Exception;
	public void visit(DELTABLE del) throws Exception;
	public void visit(INSERT insert) throws Exception;
	public void visit(INTERSECT intersect) throws Exception;
	public void visit(MAKEREC makerec) throws Exception; 
	public void visit(NEWFIELD newfield) throws Exception;
	public void visit(NEWTABLE newtable) throws Exception;
	public void visit(OPEN open) throws Exception;
	public void visit(PRODUCT product) throws Exception;
	public void visit(PROJECTION projection) throws Exception;
	public void visit(PUSHEXPR pushfun) throws Exception;
	public void visit(SELECT select) throws Exception;
	public void visit(SHOW show) throws Exception;
	public void visit(SUBTRACT sub) throws Exception;
	public void visit(TRACE trace) throws Exception;
	public void visit(UNION union) throws Exception;
	public void visit(UPDATE update) throws Exception;
}
