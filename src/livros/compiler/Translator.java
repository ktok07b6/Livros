package livros.compiler;

import livros.Debug;
import livros.Log;
import livros.db.BinExpr;
import livros.db.Constraint;
import livros.db.DataBase;
import livros.db.Expr;
import livros.db.Field;
import livros.db.FieldList;
import livros.db.FieldRef;
import livros.db.Operator;
import livros.db.RefConst;
import livros.db.Table;
import livros.db.Type;
import livros.db.Value;
import livros.db.IntegerValue;
import livros.db.TextValue;
import livros.db.UnExpr;
import livros.storage.StorageManager;
import livros.vm.CLOSE;
import livros.vm.COMMIT;
import livros.vm.DELTABLE;
import livros.vm.INSERT;
import livros.vm.INST;
import livros.vm.INTERSECT;
import livros.vm.MAKEREC;
import livros.vm.NEWFIELD;
import livros.vm.NEWTABLE;
import livros.vm.OPEN;
import livros.vm.PRODUCT;
import livros.vm.PROJECTION;
import livros.vm.PUSHEXPR;
import livros.vm.SELECT;
import livros.vm.SHOW;
import livros.vm.SUBTRACT;
import livros.vm.TRACE;
import livros.vm.UNION;
import livros.vm.UPDATE;
import livros.vm.InstructionPrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Translator implements ASTVisitor
{
	private static final boolean ENABLE_RELATIONAL_OP = false;
	public static boolean DEBUG = false;

	Map mTableAliasMap = new HashMap();
	Map mColumnAliasMap = new HashMap();
	String mCurrentTableName;

	public Translator(boolean immediate) {
		mImmediateMode = immediate;
	}

	private boolean mImmediateMode = false;
	private List mInstructions = new ArrayList();

	public void emit(INST inst) {
		mInstructions.add(inst);
	}

	public List instructions() {
		return mInstructions;
	}

	public void dump() {
		InstructionPrinter printer = new InstructionPrinter();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < mInstructions.size(); i++) {
			INST inst = (INST)mInstructions.get(i);
			s.append(printer.toString(inst) + "\n");
		}
		Log.d("\n"+s.toString());
	}

	List visitASTArray(List li) {
		List newList = new ArrayList();
		try {
			for (int i = 0; i < li.size(); i++) {
				AST ast = (AST)li.get(i);
				newList.add(ast.accept(this));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Debug.assertTrue(false);
		}
		return newList;
	}

	void prolog() {
	}

	void epilog() {
		if (DEBUG) {
			emit(new TRACE());
		}
		if (mImmediateMode) {
			emit(new COMMIT());
		}
	}

	public Object visit(SelectStatement ast) throws Exception {
		ast.mQuery.accept(this);
		if (ast.mOrderBy != null) {
			ast.mOrderBy.accept(this);
		}

		return null;
	}

	public Object visit(TableDefinition ast) throws Exception {
		prolog();

		List fields = visitASTArray(ast.mTableElementList);
 		for (int i = 0; i < fields.size(); i++) {
			Field f = (Field)fields.get(i);
			emit(new NEWFIELD(f));
		}
		String tableName = (String)ast.mTableName.accept(this);
		emit(new NEWTABLE(tableName));

		epilog();
		return null;
	}

	public Object visit(DropTableStatement ast) throws Exception {
		prolog();

		String tableName = (String)ast.mTableName.accept(this);
		emit(new DELTABLE(tableName));

		epilog();
		return null;
	}

	public Object visit(InsertStatement ast) throws Exception {
		prolog();

		String tableName = (String)ast.mTableName.accept(this);

		List columns = visitASTArray(ast.mColumnList);

		if (ast.mQuery.isValueCtor()) {
			ValueConstructor valueCtor = (ValueConstructor)ast.mQuery;
			List values = valueCtor.mRowValueList;
			if (columns.size() == 0) {
				DataBase db = DataBase.open("");
				Table t = db.table(tableName);
				if (t == null) {
					throw new ParseException("undefined table name: " + tableName);
				}
				FieldList fields = t.fieldList();
				if (fields.size() != values.size()) {
					throw new ParseException("number of values is not valid");
				}
				for (int i = 0; i < fields.size(); i++) {
					String field = fields.get(i).name();
					emit(new PUSHEXPR(new FieldRef(field)));
					ASTExpr expr = (ASTExpr)values.get(i);
					Expr e = (Expr)expr.accept(this);
					emit(new PUSHEXPR(e));
				}
				emit(new MAKEREC(fields.size()));
			} else {
				if (columns.size() != values.size()) {
					throw new ParseException("number of values is not valid");
				}
				for (int i = 0; i < columns.size(); i++) {
					String field = (String)columns.get(i);
					emit(new PUSHEXPR(new FieldRef(field)));
					ASTExpr expr = (ASTExpr)values.get(i);
					Expr e = (Expr)expr.accept(this);
					emit(new PUSHEXPR(e));
				}
				emit(new MAKEREC(columns.size()));
			}
		} else {
			//TODO:
			ast.mQuery.accept(this);
		}

		emit(new INSERT(tableName));

		epilog();
		return null;
	}

	public Object visit(UpdateStatement ast) throws Exception {
		prolog();

		String tableName = (String)ast.mTableName.accept(this);
		String tmp = newTmpTableName();

		Expr e = (Expr)ast.mSearchCond.accept(this);
		emit(new PUSHEXPR(e));
		emit(new SELECT(tmp, tableName));

		List results = visitASTArray(ast.mSetClauseList);
		
		emit(new UPDATE(tableName, tmp, results.size()));

		epilog();
		return null;
	}

	public Object visit(DeleteStatement ast) throws Exception {
		prolog();

		String tableName = (String)ast.mTableName.accept(this);
		String tmp = newTmpTableName();

		Expr e = (Expr)ast.mSearchCond.accept(this);
		emit(new PUSHEXPR(e));
		emit(new SELECT(tmp, tableName));
		emit(new SUBTRACT(tableName, tmp));

		epilog();
		return null;
	}

	public Object visit(CommitStatement ast) throws Exception {
		emit(new COMMIT());
		return null;
	}

	public Object visit(TableElement ast) throws Exception {
		String name = (String)ast.mColumnName.accept(this);
		Type type = (Type)ast.mDataType.accept(this);

		Value defVal = Value.nullValue;
		if (ast.mDefault != null) 
			defVal = (Value)ast.mDefault.accept(this);

		List constraints = visitASTArray(ast.mConstraints);
		return new Field(name, type, defVal, constraints);
	}

	public Object visit(DataType ast) throws Exception {
		switch (ast.mType) {
		case DataType.INTEGER_TYPE:
			return Type.integerType;
		case DataType.CHAR_TYPE:
			return Type.fixedChar(ast.mLength);
		case DataType.VARCHAR_TYPE:
			return Type.varChar(ast.mLength);
		default:
			Debug.assertTrue(false);
			return null;
		}
	}

	public Object visit(DefaultClause ast) throws Exception {
		if (ast.mLiteral != null) {
			return ast.mLiteral.accept(this);
		} else {
			return Value.nullValue;
		}
	}

	public Object visit(SetClause ast) throws Exception {
		String colName = (String)ast.mColumnName.accept(this);
		emit(new PUSHEXPR(new FieldRef(colName)));
		Expr e = (Expr)ast.mSource.accept(this);
		emit(new PUSHEXPR(e));
		return null;
	}

	public Object visit(Identifier ast) throws Exception {
		return ast.mIdent;
	}

	String newTmpTableName() {
		return StorageManager.instance().newIndexFileName();
	}

	Expr searchOr(ASTExpr le, ASTExpr re) throws Exception {
		if (!ENABLE_RELATIONAL_OP) {
			Expr l = (Expr)le.accept(this);
			Expr r = (Expr)re.accept(this);
			return new BinExpr(Operator.OR, l, r);
		} else {
			//TODO:
			/*
			String t1 = newTmpTableName();
			String t2 = newTmpTableName();
			String t3 = newTmpTableName();

			le.accept(this);
			emit(new SELECT(t1, mCurrentTableName));
			re.accept(this);
			emit(new SELECT(t2, mCurrentTableName));
			emit(new UNION(t3, t2, t1));
			
			mCurrentTableName = t3;
			*/
			return null;
		}
	} 

	Expr searchAnd(ASTExpr le, ASTExpr re) throws Exception {
		if (!ENABLE_RELATIONAL_OP) {
			Expr l = (Expr)le.accept(this);
			Expr r = (Expr)re.accept(this);
			return new BinExpr(Operator.AND, l, r);
		} else {
			//TODO
			/*
			String t1 = newTmpTableName();
			String t2 = newTmpTableName();
			String t3 = newTmpTableName();
			
			le.accept(this);
			emit(new SELECT(t1, mCurrentTableName));
			re.accept(this);
			emit(new SELECT(t2, mCurrentTableName));
			emit(new INTERSECT(t3, t2, t1));

			mCurrentTableName = t3;
			*/
			return null;
		}
	} 

	public Object visit(BinaryExpr ast) throws Exception {
		if (false/*ast.isLogOp()*/) {
			switch (ast.mOp) {
			case BinaryExpr.LOGICAL_OR:
				return searchOr(ast.mLeft, ast.mRight);
			case BinaryExpr.LOGICAL_AND:
				return searchAnd(ast.mLeft, ast.mRight);
			}
		} else {
			Expr le = (Expr)ast.mLeft.accept(this);
			Expr re = (Expr)ast.mRight.accept(this);
			return new BinExpr(ast.mOp, le, re);
		}

		return null;
	}

	public Object visit(UnaryExpr ast) throws Exception {
		Expr e = (Expr)ast.mExpr.accept(this);
		return new UnExpr(ast.mOp, e);
	}

	public Object visit(FuncExpr ast) throws Exception {
		return null;
	}

	public Object visit(IntegerExpr ast) throws Exception {
		return new IntegerValue(ast.mValue);
	}

	public Object visit(StringExpr ast) throws Exception {
		return new TextValue(ast.mValue);
	}

	public Object visit(TrueExpr ast) throws Exception {
		return Value.trueValue;
	}

	public Object visit(FalseExpr ast) throws Exception {
		return Value.falseValue;
	}

	public Object visit(UnknownExpr ast) throws Exception {
		return Value.unknownValue;
	}

	public Object visit(NullExpr ast) throws Exception {
		return Value.nullValue;
	}

	public Object visit(DefaultExpr ast) throws Exception {
		//TODO:
		return null;
	}

	public Object visit(ColumnReference ast) throws Exception {
		FieldRef ref = null;
		String columnName = (String)ast.mColumnName.accept(this);
		if (ast.mTableName != null) {
			String tableName = (String)ast.mTableName.accept(this);
			ref = new FieldRef(tableName, columnName);
		} else {
			ref = new FieldRef(columnName);
		}
		return ref;//null;
	}

	public Object visit(QuerySpecification ast) throws Exception {
		ast.mFrom.accept(this);

		if (ast.mQuantifier >= 0) {
			//TODO:
		}
		
		if (ast.mWhere != null) {
			ast.mWhere.accept(this);
		}

		if (ast.mGroupBy != null) {
			ast.mGroupBy.accept(this);
		}

		List columns = null;
		if (ast.mSelectAll) {

		} else {
			columns = visitASTArray(ast.mColumns);
			String tmp = newTmpTableName();
			emit(new PROJECTION(tmp, mCurrentTableName, columns.size()));
			mCurrentTableName = tmp;
		}

		emit(new SHOW(mCurrentTableName));
		return null;
	}

	public Object visit(ValueConstructor ast) throws Exception {
		Debug.assertTrue(false);
		return visitASTArray(ast.mRowValueList);
	}

	public Object visit(SelectColumn ast) throws Exception {
		if (ast.mTableName != null) {
			//TODO
			return null;
		}
		String name = "";
		if (ast.mAsName != null) {
			name = (String)ast.mAsName.accept(this);
		} else {
			name = ast.mColumn.toString();
		}
		emit(new PUSHEXPR(name));

		Expr e = (Expr)ast.mColumn.accept(this);
		emit(new PUSHEXPR(e));

		return null;
	}

	public Object visit(FromClause ast) throws Exception {
		visitASTArray(ast.mTables);
		if (ast.mTables.size() > 1) {
			//emit(new PRODUCT(ast.mTables.size()));
		}
		return null;
	}

	public Object visit(TableReference ast) throws Exception {
		if (ast.mTableName != null) {
			String tableName = (String)ast.mTableName.accept(this);
			mCurrentTableName = tableName;
			
			if (ast.mCorrelation != null) {
				String alias = (String)ast.mCorrelation.mCorrelationName.accept(this);
				mTableAliasMap.put(tableName, alias);

				if (ast.mCorrelation.mColumnNameList != null) {
					throw new NotSupportedException("<correlation specification> with <column name list> is not supported yet");
				}
			}
		} else {
			throw new NotSupportedException("<table reference> as <subquery> is not supported yet");
		}

		return null;
	}

	public Object visit(CorrelationSpecification ast) throws Exception {
		//never reach here
		Debug.assertTrue(false);
		return null;
	}

	public Object visit(WhereClause ast) throws Exception {
		Expr e = (Expr)ast.mSearchCond.accept(this);
		emit(new PUSHEXPR(e));

		if (ENABLE_RELATIONAL_OP) {
			if (ast.mSearchCond instanceof BinaryExpr) {
				BinaryExpr bin = (BinaryExpr)ast.mSearchCond;
				if (bin.isLogOp()) {
					return null;
				}
			}
		}
		String tmp = newTmpTableName();
		emit(new SELECT(tmp, mCurrentTableName));
		mCurrentTableName = tmp;
		return null;
	}

	public Object visit(GroupByClause ast) throws Exception {
		//TODO:
		visitASTArray(ast.mColumns);
		return null;
	}

	public Object visit(OrderByClause ast) throws Exception {
		//TODO
		return null;
	}

	public Object visit(LikePredicate ast) throws Exception {
		//TODO:
		return null;
	}

	public Object visit(UniqueConstraint ast) throws Exception {
		if (ast.mPrimaryKey) {
			return Constraint.primaryConst;
		} else {
			return Constraint.uniqueConst;
		}
	}

	public Object visit(NotNullConstraint ast) throws Exception {
		return Constraint.notNullConst;
	}

	public Object visit(ReferenceConstraint ast) throws Exception {
		String tableName = (String)ast.mTableName.accept(this);
		String columnName = (String)ast.mColumnName.accept(this);
		return new RefConst(tableName, columnName);
	}

}
