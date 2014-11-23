package livros.db;

interface ExprVisitor
{
	Object visit(Value v);
	Object visit(BinExpr expr);
	Object visit(UnExpr expr);
}
