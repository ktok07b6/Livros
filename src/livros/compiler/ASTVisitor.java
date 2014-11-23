
package livros.compiler;

public interface ASTVisitor
{
	public Object visit(TableDefinition ast) throws Exception;
	public Object visit(DropTableStatement ast) throws Exception;
	public Object visit(InsertStatement ast) throws Exception;
	public Object visit(UpdateStatement ast) throws Exception;
	public Object visit(DeleteStatement ast) throws Exception;
	public Object visit(SelectStatement ast) throws Exception;
	public Object visit(CommitStatement ast) throws Exception;

	public Object visit(TableElement ast) throws Exception;
	public Object visit(DataType ast) throws Exception;
	public Object visit(DefaultClause ast) throws Exception;
	public Object visit(SetClause ast) throws Exception;

	public Object visit(Identifier ast) throws Exception;
	public Object visit(BinaryExpr ast) throws Exception;
	public Object visit(UnaryExpr ast) throws Exception;
	public Object visit(FuncExpr ast) throws Exception;
	public Object visit(IntegerExpr ast) throws Exception;
	public Object visit(StringExpr ast) throws Exception;
	public Object visit(TrueExpr ast) throws Exception;
	public Object visit(FalseExpr ast) throws Exception;
	public Object visit(UnknownExpr ast) throws Exception;
	public Object visit(NullExpr ast) throws Exception;
	public Object visit(DefaultExpr ast) throws Exception;
	public Object visit(ColumnReference ast) throws Exception;

	public Object visit(QuerySpecification ast) throws Exception;
	public Object visit(ValueConstructor ast) throws Exception;

	public Object visit(SelectColumn ast) throws Exception;
	public Object visit(FromClause ast) throws Exception;
	public Object visit(TableReference ast) throws Exception;
	public Object visit(CorrelationSpecification ast) throws Exception;
	public Object visit(WhereClause ast) throws Exception;
	public Object visit(GroupByClause ast) throws Exception;
	public Object visit(OrderByClause ast) throws Exception;

	public Object visit(LikePredicate ast) throws Exception;

	public Object visit(UniqueConstraint ast) throws Exception;
	public Object visit(NotNullConstraint ast) throws Exception;
	public Object visit(ReferenceConstraint ast) throws Exception;
}
