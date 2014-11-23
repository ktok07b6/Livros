package livros.compiler;

import java.util.List;

public class ASTPrinter implements ASTVisitor
{
	StringBuilder mBuilder;
	public ASTPrinter() {
		mBuilder = new StringBuilder();
	}

	public String getResult() {
		return mBuilder.toString();
	}

	private void printList(List li, String delim) {
		try {
			for (int i = 0; i < li.size(); i++) {
				AST ast = (AST)li.get(i);
				ast.accept(this);
				if (i+1 < li.size()) {
					mBuilder.append(delim);
				}
			}
		} catch (Exception ex) {
		}
	}

	public Object visit(TableDefinition ast) throws Exception {
		mBuilder.append("TableDefinition(");
		ast.mTableName.accept(this);
		mBuilder.append(", {");
		printList(ast.mTableElementList, ", ");
		mBuilder.append("})");
		return null;
	}

	public Object visit(DropTableStatement ast) throws Exception {
		mBuilder.append("DropTableStatement(");
		ast.mTableName.accept(this);
		mBuilder.append(", ");
		//todo behavior
		mBuilder.append("<!!!TODO:!!!>");
		mBuilder.append(")");
		return null;
	}

	public Object visit(InsertStatement ast) throws Exception {
		mBuilder.append("InsertStatement(");
		ast.mTableName.accept(this);
		mBuilder.append(", {");
		printList(ast.mColumnList, ", ");
		mBuilder.append("}, ");
		if (ast.mQuery != null) {
			ast.mQuery.accept(this);
		} else {
			mBuilder.append("DEFAULT VALUES");
		}
		mBuilder.append(")");
		return null;
	}

	public Object visit(UpdateStatement ast) throws Exception {
		mBuilder.append("UpdateStatement(");
		ast.mTableName.accept(this);
		mBuilder.append(", {");
		printList(ast.mSetClauseList, ", ");
		mBuilder.append("}, ");
		ast.mSearchCond.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(DeleteStatement ast) throws Exception {
		mBuilder.append("DeleteStatement(");
		ast.mTableName.accept(this);
		mBuilder.append(", ");
		ast.mSearchCond.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(SelectStatement ast) throws Exception {
		mBuilder.append("SelectStatement(");
		ast.mQuery.accept(this);
		if (ast.mOrderBy != null) {
			mBuilder.append(", ");
			ast.mOrderBy.accept(this);
		}
		mBuilder.append(")");
		return null;
	}

	public Object visit(CommitStatement ast) throws Exception {
		mBuilder.append("CommitStatement()");
		return null;
	}

	public Object visit(TableElement ast) throws Exception {
		mBuilder.append("TableElement(");
		ast.mColumnName.accept(this);
		mBuilder.append(", ");
		ast.mDataType.accept(this);
		mBuilder.append(", ");
		if (ast.mDefault != null) 
			ast.mDefault.accept(this);
		mBuilder.append(", {");
		printList(ast.mConstraints, ", ");
		mBuilder.append("})");
		return null;
	}

	public Object visit(DataType ast) throws Exception {
		mBuilder.append("DataType(");
		mBuilder.append(ast.toString());
		mBuilder.append(")");
		return null;
	}

	public Object visit(DefaultClause ast) throws Exception {
		mBuilder.append("DefaultClause(");
		ast.mLiteral.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(SetClause ast) throws Exception {
		mBuilder.append("SetClause(");
		ast.mColumnName.accept(this);
		mBuilder.append(", ");
		ast.mSource.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(Identifier ast) throws Exception {
		mBuilder.append("Identifier(\""+ast.mIdent+"\")");
		return null;
	}

	public Object visit(BinaryExpr ast) throws Exception {
		mBuilder.append("BinaryExpr(");
		mBuilder.append(ast.opString().trim() + ", ");
		ast.mLeft.accept(this);
		mBuilder.append(", ");
		ast.mRight.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(UnaryExpr ast) throws Exception {
		mBuilder.append("UnaryExpr(");
		mBuilder.append(ast.opString().trim() + ", ");
		ast.mExpr.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(FuncExpr ast) throws Exception {
		mBuilder.append("FuncExpr(");
		mBuilder.append(ast.funcTypeString()+", ");
		if (ast.mQuantifier >= 0)
			mBuilder.append(SetQuantifier.string(ast.mQuantifier) + ", ");
		mBuilder.append("{");
		ast.mExpr.accept(this);
		mBuilder.append("})");
		return null;
	}

	public Object visit(IntegerExpr ast) throws Exception {
		mBuilder.append("IntegerExpr(" + ast.toString() + ")");
		return null;
	}

	public Object visit(StringExpr ast) throws Exception {
		mBuilder.append("StringExpr(" + ast.toString() + ")");
		return null;
	}

	public Object visit(TrueExpr ast) throws Exception {
		mBuilder.append("TrueExpr");
		return null;
	}

	public Object visit(FalseExpr ast) throws Exception {
		mBuilder.append("FalseExpr");
		return null;
	}

	public Object visit(UnknownExpr ast) throws Exception {
		mBuilder.append("UnknownExpr");
		return null;
	}

	public Object visit(NullExpr ast) throws Exception {
		mBuilder.append("NullExpr");
		return null;
	}

	public Object visit(DefaultExpr ast) throws Exception {
		mBuilder.append("DefaultExpr");
		return null;
	}

	public Object visit(ColumnReference ast) throws Exception {
		mBuilder.append("ColumnReference(");
		mBuilder.append(ast.toString());
		mBuilder.append(")");
		return null;
	}

	public Object visit(QuerySpecification ast) throws Exception {
		mBuilder.append("QuerySpecification(");
		if (ast.mQuantifier >= 0)
			mBuilder.append(SetQuantifier.string(ast.mQuantifier) + ", ");

		if (ast.mSelectAll) {
			mBuilder.append("*, ");
		} else {
			mBuilder.append("{");
			printList(ast.mColumns, ", ");
			mBuilder.append("}, ");
		}
		ast.mFrom.accept(this);
		if (ast.mWhere != null) {
			mBuilder.append(", ");
			ast.mWhere.accept(this);
		}
		if (ast.mGroupBy != null) {
			mBuilder.append(", ");
			ast.mGroupBy.accept(this);
		}
		mBuilder.append(")");
		return null;
	}

	public Object visit(ValueConstructor ast) throws Exception {
		mBuilder.append("ValueConstructor({");
		printList(ast.mRowValueList, ", ");
		mBuilder.append("})");
		return null;
	}

	public Object visit(SelectColumn ast) throws Exception {
		mBuilder.append("SelectColumn(");
		if (ast.mTableName != null) {
			ast.mTableName.accept(this);
			mBuilder.append(", *");
		} else {
			ast.mColumn.accept(this);
			if (ast.mAsName != null) {
				mBuilder.append(" AS ");
				ast.mAsName.accept(this);
			}
		}
		mBuilder.append(")");
		return null;
	}

	public Object visit(FromClause ast) throws Exception {
		mBuilder.append("FromClause({");
		printList(ast.mTables, ", ");
		mBuilder.append("})");
		return null;
	}

	public Object visit(TableReference ast) throws Exception {
		mBuilder.append("TableReference(");
		if (ast.mTableName != null) {
			mBuilder.append(ast.mTableName);
			if (ast.mCorrelation != null) {
				mBuilder.append(", ");
				ast.mCorrelation.accept(this);
			}
		} else {
			mBuilder.append(ast.mTableName);
			ast.mQuery.accept(this);
			mBuilder.append(", ");
			ast.mCorrelation.accept(this);
		}
		mBuilder.append(")");
		return null;
	}

	public Object visit(CorrelationSpecification ast) throws Exception {
		mBuilder.append("CorrelatioinSpecification(");
		ast.mCorrelationName.accept(this);
		if (ast.mColumnNameList != null) {
			mBuilder.append("{");
			printList(ast.mColumnNameList, ", ");
			mBuilder.append("}");
		}
		mBuilder.append(")");
		return null;
	}


	public Object visit(WhereClause ast) throws Exception {
		mBuilder.append("WhereClause(");
		ast.mSearchCond.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(GroupByClause ast) throws Exception {
		mBuilder.append("GroupByClause({");
		printList(ast.mColumns, ", ");
		mBuilder.append("})");
		return null;
	}

	public Object visit(OrderByClause ast) throws Exception {
		mBuilder.append("OrderByClause({");
		//TODO:
		mBuilder.append("})");
		return null;
	}

	public Object visit(LikePredicate ast) throws Exception {
		mBuilder.append("LikePredicate(");
		mBuilder.append(ast.mIsNot ? "Not, ":"");
		ast.mMatch.accept(this);
		mBuilder.append(", ");
		ast.mPattern.accept(this);
		mBuilder.append(")");
		return null;
	}

	public Object visit(UniqueConstraint ast) throws Exception {
		mBuilder.append("UniqueConstraint("+ast.toString()+")");
		return null;
	}

	public Object visit(NotNullConstraint ast) throws Exception {
		mBuilder.append("NotNullConstraint");
		return null;
	}

	public Object visit(ReferenceConstraint ast) throws Exception {
		mBuilder.append("ReferenceConstraint");
		ast.mTableName.accept(this);
		mBuilder.append(", ");
		ast.mColumnName.accept(this);
		mBuilder.append(")");
		return null;
	}

}
